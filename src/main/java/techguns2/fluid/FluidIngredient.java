package techguns2.fluid;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import techguns2.util.IConsumableIngredient;

/**
 * Based on {@link net.minecraft.world.item.crafting.Ingredient}
 */
public final class FluidIngredient implements IConsumableIngredient<FluidStack>
{
    private final FluidIngredient.Value[] values;
    @Nullable
    private FluidStack[] fluidStacks;
    
    protected FluidIngredient(Stream<? extends FluidIngredient.Value> values)
    {
        this.values = values.toArray((length) -> new FluidIngredient.Value[length]);
    }
    
    public FluidStack[] getFluids()
    {
        if (this.fluidStacks == null)
        {
            this.fluidStacks = Arrays.stream(this.values)
                    .flatMap(value -> value.getFluids().stream())
                    .distinct()
                    .toArray(length -> new FluidStack[length]);
        }
        
        return this.fluidStacks;
    }
    
    public boolean isEmpty()
    {
        return this.values.length == 0;
    }
    
    @Override
    public boolean test(@Nullable FluidStack fluidStack)
    {
        if (fluidStack == null)
            return false;
        else if (this.isEmpty())
            return fluidStack.isEmpty();
        
        Fluid fluid = fluidStack.getFluid();
        int amount = fluidStack.getAmount();
        
        for (FluidStack fluidStackValue : this.getFluids())
        {
            if (fluidStackValue.getFluid().isSame(fluid) &&
                    amount >= fluidStackValue.getAmount())
            {
                return true;
            }
        }
        
        return false;
    }
    
    public boolean test(@Nullable Fluid fluid, int amount)
    {
        if (fluid == null)
            return false;
        else if (this.isEmpty())
            return amount <= 0;
        
        for (FluidStack fluidStackValue : this.getFluids())
        {
            if (fluidStackValue.getFluid().isSame(fluid) &&
                    amount >= fluidStackValue.getAmount())
            {
                return true;
            }
        }
        
        return false;
    }
    
    public int testWithConsumption(@Nullable FluidStack fluidStack)
    {
        if (fluidStack == null)
            return -1;
        else if (this.isEmpty())
        {
            if (fluidStack.isEmpty())
                return 0;
            else
                return -1;
        }
        

        Fluid fluid = fluidStack.getFluid();
        int amount = fluidStack.getAmount();
        
        for (FluidStack fluidStackValue : this.getFluids())
        {
            if (!fluidStackValue.getFluid().isSame(fluid))
                continue;
            
            int valueAmount = fluidStackValue.getAmount();
            
            if (amount >= valueAmount)
                return valueAmount;
        }
        
        return -1;
    }
    
    public int testWithConsumption(@Nullable Fluid fluid, int amount)
    {
        if (fluid == null)
            return -1;
        else if (this.isEmpty())
        {
            if (amount <= 0)
                return 0;
            else
                return -1;
        }
        
        for (FluidStack fluidStackValue : this.getFluids())
        {
            if (!fluidStackValue.getFluid().isSame(fluid))
                continue;
            
            int valueAmount = fluidStackValue.getAmount();
            
            if (amount >= valueAmount)
                return valueAmount;
        }
        
        return -1;
    }
    
    public void toNetwork(FriendlyByteBuf buffer)
    {
        buffer.writeCollection(Arrays.asList(this.getFluids()), FriendlyByteBuf::writeFluidStack);
    }
    
    public JsonElement toJson()
    {
        if (this.values.length == 1)
        {
            return this.values[0].serialize();
        }
        
        JsonArray results = new JsonArray();
        for (FluidIngredient.Value value : this.values)
        {
            results.add(value.serialize());
        }
        return results;
    }
    
    public static final FluidIngredient EMPTY = new FluidIngredient(Stream.empty());
    
    public static FluidIngredient of()
    {
        return FluidIngredient.EMPTY;
    }
    
    public static FluidIngredient of(int amount, Fluid... fluids)
    {
        return FluidIngredient.of(Arrays.stream(fluids).map(f -> new FluidStack(f, amount)));
    }
    
    public static FluidIngredient of(FluidStack... fluids)
    {
        return FluidIngredient.of(Arrays.stream(fluids));
    }
    
    public static FluidIngredient of(Stream<FluidStack> fluids)
    {
        return FluidIngredient.fromValues(fluids
                .filter(f -> !f.isEmpty())
                .map(f -> new FluidIngredient.FluidValue(f)));
    }
    
    public static FluidIngredient of(int amount, TagKey<Fluid> tag)
    {
        return FluidIngredient.fromValues(Stream.of(new FluidIngredient.TagValue(tag, amount)));
    }
    
    public static FluidIngredient fromValues(Stream<? extends FluidIngredient.Value> values)
    {
        FluidIngredient ingredient = new FluidIngredient(values);
        return ingredient.isEmpty() ? EMPTY : ingredient;
    }
    
    public static FluidIngredient fromNetwork(FriendlyByteBuf buffer)
    {
        int size = buffer.readInt();
        return FluidIngredient.fromValues(Stream.generate(() -> new FluidIngredient.FluidValue(buffer.readFluidStack())).limit(size));
    }
    
    public static FluidIngredient fromJson(@Nullable JsonElement json)
    {
        return FluidIngredient.fromJson(json, true);
    }
    
    public static FluidIngredient fromJson(@Nullable JsonElement json, boolean allowEmpty)
    {
        if (json == null || json.isJsonNull())
            throw new JsonSyntaxException("Fluid cannot be null");
        
        if (json.isJsonObject())
        {
            return fromValues(Stream.of(FluidIngredient.valueFromJson(json.getAsJsonObject())));
        }
        
        if (!json.isJsonArray())
        {
            throw new JsonSyntaxException("Expected fluid to be object or array of objects");
        }
        
        JsonArray jsonArray = json.getAsJsonArray();
        if (jsonArray.size() == 0 && !allowEmpty)
        {
            throw new JsonSyntaxException("Fluid array cannot be empty, at least one fluid must be defined");
        }
        
        return FluidIngredient.fromValues(StreamSupport.stream(jsonArray.spliterator(), false)
                .map(element -> FluidIngredient.valueFromJson(GsonHelper.convertToJsonObject(element, "fluid"))));
    }
    
    public static FluidIngredient.Value valueFromJson(JsonObject json)
    {
        if (json.has("fluid") && json.has("tag"))
            throw new JsonParseException("A fluid ingredient entry is either a tag or an fluid, not both");
        
        if (json.has("fluid"))
        {
            FluidStack fluidStack = FluidHelper.fromJson(json);
            return new FluidIngredient.FluidValue(fluidStack);
        }
        
        if (!json.has("tag"))
        {
            throw new JsonParseException("An ingredient entry needs either a tag or an item");
        }

        ResourceLocation resourceLocation = new ResourceLocation(GsonHelper.getAsString(json, "tag"));
        TagKey<Fluid> tagKey = TagKey.create(Registries.FLUID, resourceLocation);
        
        int amount = GsonHelper.getAsInt(json, "amount");
        
        return new FluidIngredient.TagValue(tagKey, amount);
     }
    
    public static class FluidValue implements FluidIngredient.Value
    {
        private final FluidStack fluid;
        
        public FluidValue(FluidStack fluid)
        {
            this.fluid = fluid;
        }
        
        @Override
        public Collection<FluidStack> getFluids()
        {
            return Collections.singleton(this.fluid);
        }
        
        @Override
        public JsonObject serialize()
        {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("fluid", ForgeRegistries.FLUIDS.getKey(this.fluid.getFluid()).toString());
            jsonObject.addProperty("amount", this.fluid.getAmount());
            return jsonObject;
        }
    }
    
    public static class TagValue implements FluidIngredient.Value
    {
        private final TagKey<Fluid> tag;
        private final int amount;
        
        public TagValue(TagKey<Fluid> tag, int amount)
        {
            this.tag = tag;
            this.amount = amount;
        }
        
        @Override
        public Collection<FluidStack> getFluids()
        {
            return ForgeRegistries.FLUIDS.tags()
                    .getTag(this.tag)
                    .stream()
                    .map(f -> {
                        if (f instanceof FlowingFluid flowingFluid)
                            return flowingFluid.getSource();
                        return f;
                    })
                    .distinct()
                    .map(f -> new FluidStack(f, this.amount))
                    .collect(Collectors.toList());
        }
        
        @Override
        public JsonObject serialize()
        {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("tag", this.tag.location().toString());
            jsonObject.addProperty("amount", this.amount);
            return jsonObject;
        }
    }
    
    public static interface Value
    {
        Collection<FluidStack> getFluids();
        
        JsonObject serialize();
    }
}
