package techguns2.machine.fabricator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;
import techguns2.TGRecipeSerializers;
import techguns2.item.ConsumableItemIngredient;
import techguns2.item.ConsumableItemIngredientSerializer;
import techguns2.util.RecipeSerializerUtil;

public final class FabricatorRecipe implements IFabricatorRecipe
{
    private final ResourceLocation _id;
    private final String _group;
    private final ConsumableItemIngredient _inputIngredient;
    private final ConsumableItemIngredient _wireIngredient;
    private final ConsumableItemIngredient _redstoneIngredient;
    private final ConsumableItemIngredient _plateIngredient;
    private final int _processingTime;
    private final int _energyDrainPerTick;
    private final ItemStack _result;
    
    public FabricatorRecipe(
            ResourceLocation id,
            String group,
            ConsumableItemIngredient inputIngredient,
            ConsumableItemIngredient wireIngredient,
            ConsumableItemIngredient redstoneIngredient,
            ConsumableItemIngredient plateIngredient,
            ItemStack result,
            int processingTime,
            int energyDrainPerTick)
    {
        this._id = id;
        this._group = group == null ? "" : group;
        this._inputIngredient = inputIngredient;
        this._wireIngredient = wireIngredient;
        this._redstoneIngredient = redstoneIngredient;
        this._plateIngredient = plateIngredient;
        this._processingTime = processingTime;
        this._energyDrainPerTick = energyDrainPerTick;
        this._result = result;
    }
    
    @Override
    public final ResourceLocation getId()
    {
        return this._id;
    }
    
    @Override
    public final String getGroup()
    {
        return this._group;
    }

    public final ConsumableItemIngredient getInputIngredient()
    {
        return this._inputIngredient;
    }
    
    @Override
    public final int testInputWithConsumption(ItemStack itemStack)
    {
        return this._inputIngredient.testWithConsumption(itemStack);
    }

    public final ConsumableItemIngredient getWireIngredient()
    {
        return this._wireIngredient;
    }
    
    @Override
    public final int testWireWithConsumption(ItemStack itemStack)
    {
        return this._wireIngredient.testWithConsumption(itemStack);
    }

    public final ConsumableItemIngredient getRedstoneIngredient()
    {
        return this._redstoneIngredient;
    }
    
    @Override
    public final int testRedstoneWithConsumption(ItemStack itemStack)
    {
        return this._redstoneIngredient.testWithConsumption(itemStack);
    }

    public final ConsumableItemIngredient getPlateIngredient()
    {
        return this._plateIngredient;
    }
    
    @Override
    public final int testPlateWithConsumption(ItemStack itemStack)
    {
        return this._plateIngredient.testWithConsumption(itemStack);
    }

    @Override
    public final ItemStack getResultItem(RegistryAccess registryAccess)
    {
        return this._result;
    }

    @Override
    public final int getProcessingTime()
    {
        return this._processingTime;
    }

    @Override
    public final int getEnergyDrainPerTick()
    {
        return this._energyDrainPerTick;
    }
    
    @Override
    public final NonNullList<Ingredient> getIngredients()
    {
        return NonNullList.of(this._inputIngredient, this._wireIngredient, this._redstoneIngredient, this._plateIngredient);
    }

    @Override
    public final boolean matches(FabricatorControllerBlockEntity blockEntity, Level level)
    {
        return this._inputIngredient.test(blockEntity.getInputItem()) &&
                this._wireIngredient.test(blockEntity.getWireItem()) &&
                this._redstoneIngredient.test(blockEntity.getRedstoneItem()) &&
                this._plateIngredient.test(blockEntity.getPlateItem());
    }

    @Override
    public final ItemStack assemble(FabricatorControllerBlockEntity blockEntity, RegistryAccess access)
    {
        return this._result.copy();
    }

    @Override
    public final Serializer getSerializer()
    {
        return TGRecipeSerializers.FABRICATOR.get();
    }
    
    public static final class Serializer implements RecipeSerializer<FabricatorRecipe>
    {
        protected final FabricatorRecipe readJson(ResourceLocation recipeId, JsonObject json)
        {
            String group = GsonHelper.getAsString(json, "group", "");
            JsonObject ingredientsJson = GsonHelper.getAsJsonObject(json, "ingredients");
            
            ConsumableItemIngredient inputIngredient = ConsumableItemIngredientSerializer.INSTANCE.parse(ingredientsJson.get("input"));
            ConsumableItemIngredient wireIngredient = ConsumableItemIngredientSerializer.INSTANCE.parse(ingredientsJson.get("wire"));
            ConsumableItemIngredient redstoneIngredient = ConsumableItemIngredientSerializer.INSTANCE.parse(ingredientsJson.get("redstone"));
            ConsumableItemIngredient plateIngredient = ConsumableItemIngredientSerializer.INSTANCE.parse(ingredientsJson.get("plate"));
            
            int processingTime = GsonHelper.getAsInt(json, "processingTime", 100);
            int energyDrainPerTick = GsonHelper.getAsInt(json, "energyDrainPerTick", 0);
            
            JsonObject resultJson = GsonHelper.getAsJsonObject(json, "result");
            ItemStack result = RecipeSerializerUtil.readItem(resultJson, false);
            
            return new FabricatorRecipe(
                    recipeId,
                    group,
                    inputIngredient,
                    wireIngredient,
                    redstoneIngredient,
                    plateIngredient,
                    result,
                    processingTime,
                    energyDrainPerTick);
        }
        
        protected final void writeJson(JsonObject json, FabricatorRecipe recipe)
        {
            json.addProperty("group", recipe.getGroup());
            
            JsonObject ingredientsJson = new JsonObject();
            
            ingredientsJson.add("input", recipe.getInputIngredient().toJson());
            ingredientsJson.add("wire", recipe.getWireIngredient().toJson());
            ingredientsJson.add("redstone", recipe.getRedstoneIngredient().toJson());
            ingredientsJson.add("plate", recipe.getPlateIngredient().toJson());
            
            json.add("ingredients", ingredientsJson);
            json.addProperty("processingTime", recipe.getProcessingTime());
            json.addProperty("energyDrainPerTick", recipe.getEnergyDrainPerTick());
            
            ItemStack result = recipe.getResultItem(null);
            JsonObject resultJson = new JsonObject();
            RecipeSerializerUtil.writeItem(resultJson, result);
            
            json.add("result", resultJson);
        }
        
        protected final void writeJson(JsonObject json, FabricatorRecipe.Builder.Result recipe)
        {
            json.addProperty("group", recipe.group);
            
            JsonObject ingredientsJson = new JsonObject();
            
            ingredientsJson.add("input", recipe.input.toJson());
            ingredientsJson.add("wire", recipe.wire.toJson());
            ingredientsJson.add("redstone", recipe.redstone.toJson());
            ingredientsJson.add("plate", recipe.plate.toJson());
            
            json.add("ingredients", ingredientsJson);
            json.addProperty("processingTime", recipe.processingTime);
            json.addProperty("energyDrainPerTick", recipe.energyDrainPerTick);
            
            JsonObject resultJson = new JsonObject();
            RecipeSerializerUtil.writeItem(resultJson, recipe.result);
            
            json.add("result", resultJson);
        }
        
        private final FabricatorRecipe readFromBuffer(ResourceLocation recipeId, FriendlyByteBuf buffer)
        {
            String group = buffer.readUtf();
            ConsumableItemIngredient inputIngredient = ConsumableItemIngredientSerializer.INSTANCE.parse(buffer);
            ConsumableItemIngredient wireIngredient = ConsumableItemIngredientSerializer.INSTANCE.parse(buffer);
            ConsumableItemIngredient redstoneIngredient = ConsumableItemIngredientSerializer.INSTANCE.parse(buffer);
            ConsumableItemIngredient plateIngredient = ConsumableItemIngredientSerializer.INSTANCE.parse(buffer);
            int processingTime = buffer.readInt();
            int energyDrainPerTick = buffer.readInt();
            ItemStack result = RecipeSerializerUtil.readItem(buffer);
            
            return new FabricatorRecipe(
                    recipeId,
                    group,
                    inputIngredient,
                    wireIngredient,
                    redstoneIngredient,
                    plateIngredient,
                    result,
                    processingTime,
                    energyDrainPerTick);
        }
        
        private final void writeToBuffer(FriendlyByteBuf buffer, FabricatorRecipe recipe)
        {
            buffer.writeUtf(recipe.getGroup());
            recipe.getInputIngredient().toNetwork(buffer);
            recipe.getWireIngredient().toNetwork(buffer);
            recipe.getRedstoneIngredient().toNetwork(buffer);
            recipe.getPlateIngredient().toNetwork(buffer);
            buffer.writeInt(recipe.getProcessingTime());
            buffer.writeInt(recipe.getEnergyDrainPerTick());
            RecipeSerializerUtil.writeItem(buffer, recipe.getResultItem(null));
        }

        @Override
        public final FabricatorRecipe fromJson(ResourceLocation recipeId, JsonObject json)
        {
            return this.readJson(recipeId, json);
        }

        @Override
        @Nullable
        public final FabricatorRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
        {
            return this.readFromBuffer(recipeId, buffer);
        }

        @Override
        public final void toNetwork(FriendlyByteBuf buffer, FabricatorRecipe recipe)
        {
            this.writeToBuffer(buffer, recipe);
        }
    }
    
    public static final class Builder implements RecipeBuilder
    {
        private final List<ConsumableItemIngredient.Value> _input;
        private final List<ConsumableItemIngredient.Value> _wire;
        private final List<ConsumableItemIngredient.Value> _redstone;
        private final List<ConsumableItemIngredient.Value> _plate;
        private final Advancement.Builder _advancement;
        @Nullable
        private String _advancementIdPrefix;
        @Nullable
        private String _group;
        private int _energyDrainPerTick;
        private int _processingTime;
        private RecipeSerializerUtil.ItemStackResult _result;
        
        public Builder()
        {
            this._input = new ArrayList<ConsumableItemIngredient.Value>();
            this._wire = new ArrayList<ConsumableItemIngredient.Value>();
            this._redstone = new ArrayList<ConsumableItemIngredient.Value>();
            this._plate = new ArrayList<ConsumableItemIngredient.Value>();
            this._advancement = Advancement.Builder.recipeAdvancement();
            this._group = null;
            this._energyDrainPerTick = 0;
            this._processingTime = 100;
            this._result = null;
        }

        public final Builder addInput(ItemLike item)
        {
            this._input.add(new ConsumableItemIngredient.ItemValue(new ItemStack(item, 1)));
            return this;
        }
        public final Builder addInput(ItemLike item, int count)
        {
            this._input.add(new ConsumableItemIngredient.ItemValue(new ItemStack(item, Math.max(count, 1))));
            return this;
        }
        public final Builder addInput(RegistryObject<? extends ItemLike> object)
        {
            this._input.add(new ConsumableItemIngredient.ItemValue(new ItemStack(object.get(), 1)));
            return this;
        }
        public final Builder addInput(RegistryObject<? extends ItemLike> object, int count)
        {
            this._input.add(new ConsumableItemIngredient.ItemValue(new ItemStack(object.get(), Math.max(count, 1))));
            return this;
        }
        public final Builder addInput(TagKey<Item> itemTag)
        {
            this._input.add(new ConsumableItemIngredient.TagValue(itemTag, 1));
            return this;
        }
        public final Builder addInput(TagKey<Item> itemTag, int count)
        {
            this._input.add(new ConsumableItemIngredient.TagValue(itemTag, Math.max(count, 1)));
            return this;
        }

        public final Builder addWire(ItemLike item)
        {
            this._wire.add(new ConsumableItemIngredient.ItemValue(new ItemStack(item, 1)));
            return this;
        }
        public final Builder addWire(ItemLike item, int count)
        {
            this._wire.add(new ConsumableItemIngredient.ItemValue(new ItemStack(item, Math.max(count, 1))));
            return this;
        }
        public final Builder addWire(RegistryObject<? extends ItemLike> object)
        {
            this._wire.add(new ConsumableItemIngredient.ItemValue(new ItemStack(object.get(), 1)));
            return this;
        }
        public final Builder addWire(RegistryObject<? extends ItemLike> object, int count)
        {
            this._wire.add(new ConsumableItemIngredient.ItemValue(new ItemStack(object.get(), Math.max(count, 1))));
            return this;
        }
        public final Builder addWire(TagKey<Item> itemTag)
        {
            this._wire.add(new ConsumableItemIngredient.TagValue(itemTag, 1));
            return this;
        }
        public final Builder addWire(TagKey<Item> itemTag, int count)
        {
            this._wire.add(new ConsumableItemIngredient.TagValue(itemTag, Math.max(count, 1)));
            return this;
        }

        public final Builder addRedstone(ItemLike item)
        {
            this._redstone.add(new ConsumableItemIngredient.ItemValue(new ItemStack(item, 1)));
            return this;
        }
        public final Builder addRedstone(ItemLike item, int count)
        {
            this._redstone.add(new ConsumableItemIngredient.ItemValue(new ItemStack(item, Math.max(count, 1))));
            return this;
        }
        public final Builder addRedstone(RegistryObject<? extends ItemLike> object)
        {
            this._redstone.add(new ConsumableItemIngredient.ItemValue(new ItemStack(object.get(), 1)));
            return this;
        }
        public final Builder addRedstone(RegistryObject<? extends ItemLike> object, int count)
        {
            this._redstone.add(new ConsumableItemIngredient.ItemValue(new ItemStack(object.get(), Math.max(count, 1))));
            return this;
        }
        public final Builder addRedstone(TagKey<Item> itemTag)
        {
            this._redstone.add(new ConsumableItemIngredient.TagValue(itemTag, 1));
            return this;
        }
        public final Builder addRedstone(TagKey<Item> itemTag, int count)
        {
            this._redstone.add(new ConsumableItemIngredient.TagValue(itemTag, Math.max(count, 1)));
            return this;
        }

        public final Builder addPlate(ItemLike item)
        {
            this._plate.add(new ConsumableItemIngredient.ItemValue(new ItemStack(item, 1)));
            return this;
        }
        public final Builder addPlate(ItemLike item, int count)
        {
            this._plate.add(new ConsumableItemIngredient.ItemValue(new ItemStack(item, Math.max(count, 1))));
            return this;
        }
        public final Builder addPlate(RegistryObject<? extends ItemLike> object)
        {
            this._plate.add(new ConsumableItemIngredient.ItemValue(new ItemStack(object.get(), 1)));
            return this;
        }
        public final Builder addPlate(RegistryObject<? extends ItemLike> object, int count)
        {
            this._plate.add(new ConsumableItemIngredient.ItemValue(new ItemStack(object.get(), Math.max(count, 1))));
            return this;
        }
        public final Builder addPlate(TagKey<Item> itemTag)
        {
            this._plate.add(new ConsumableItemIngredient.TagValue(itemTag, 1));
            return this;
        }
        public final Builder addPlate(TagKey<Item> itemTag, int count)
        {
            this._plate.add(new ConsumableItemIngredient.TagValue(itemTag, Math.max(count, 1)));
            return this;
        }

        @Override
        public final Builder unlockedBy(String name, CriterionTriggerInstance criterionTrigger)
        {
            this._advancement.addCriterion(name, criterionTrigger);
            return this;
        }
        
        @Override
        public final Builder group(@Nullable String group)
        {
            this._group = group;
            return this;
        }
        
        public final Builder withAdvancementIdPrefix(@Nullable String prefix)
        {
            this._advancementIdPrefix = prefix;
            return this;
        }

        public final Builder withoutEnergyDrain()
        {
            this._energyDrainPerTick = 0;
            return this;
        }
        public final Builder withEnergyDrainPerTick(int amount)
        {
            this._energyDrainPerTick = Math.max(amount, 0);
            return this;
        }
        public final Builder withProcessingTime(int ticks)
        {
            this._processingTime = Math.max(ticks, 0);
            return this;
        }
        public final Builder withResult(ItemLike result)
        {
            this._result = new RecipeSerializerUtil.ItemStackResult(result.asItem());
            return this;
        }
        public final Builder withResult(ItemLike result, @Nullable CompoundTag nbt)
        {
            this._result = new RecipeSerializerUtil.ItemStackResult(result.asItem(), nbt);
            return this;
        }
        public final Builder withResult(ItemLike result, int count)
        {
            this._result = new RecipeSerializerUtil.ItemStackResult(result.asItem(), count);
            return this;
        }
        public final Builder withResult(ItemLike result, int count, @Nullable CompoundTag nbt)
        {
            this._result = new RecipeSerializerUtil.ItemStackResult(result.asItem(), count, nbt);
            return this;
        }
        public final Builder withResult(RegistryObject<? extends ItemLike> resultObject)
        {
            this._result = new RecipeSerializerUtil.ItemStackResult(resultObject.get().asItem());
            return this;
        }
        public final Builder withResult(RegistryObject<? extends ItemLike> resultObject, @Nullable CompoundTag nbt)
        {
            this._result = new RecipeSerializerUtil.ItemStackResult(resultObject.get().asItem(), nbt);
            return this;
        }
        public final Builder withResult(RegistryObject<? extends ItemLike> resultObject, int count)
        {
            this._result = new RecipeSerializerUtil.ItemStackResult(resultObject.get().asItem(), count);
            return this;
        }
        public final Builder withResult(RegistryObject<? extends ItemLike> resultObject, int count, @Nullable CompoundTag nbt)
        {
            this._result = new RecipeSerializerUtil.ItemStackResult(resultObject.get().asItem(), count, nbt);
            return this;
        }
        
        @Override
        public final Item getResult()
        {
            return this._result == null ?
                    Items.AIR :
                    this._result.item;
        }
        
        protected final void validate(ResourceLocation id)
        {
            if (this._input.size() == 0)
                throw new IllegalStateException("Item Input does not have an ingredient item specified");
            if (this._wire.size() == 0)
                throw new IllegalStateException("Wire Input does not have an ingredient item specified");
            if (this._redstone.size() == 0)
                throw new IllegalStateException("Redstone Input does not have an ingredient item specified");
            if (this._plate.size() == 0)
                throw new IllegalStateException("Plate Input does not have an ingredient item specified");
            
            if (this._advancement.getCriteria().size() == 0)
                throw new IllegalStateException("No way of obtaining recipe " + id);
            
            if (this._result == null)
                throw new IllegalStateException("Result Item hasn't been specified");
        }
        
        public final Result build(ResourceLocation id)
        {
            this.validate(id);
            return new Result(this, id);
        }

        @Override
        public final void save(Consumer<FinishedRecipe> builder, ResourceLocation id)
        {
            builder.accept(this.build(id));
        }
        
        public static class Result implements FinishedRecipe
        {
            private final ResourceLocation id;
            private final String group;
            private final ConsumableItemIngredient input;
            private final ConsumableItemIngredient wire;
            private final ConsumableItemIngredient redstone;
            private final ConsumableItemIngredient plate;
            private final int processingTime;
            private final int energyDrainPerTick;
            @Nullable
            private final Advancement.Builder advancement;
            @Nullable
            private final ResourceLocation advancementId;
            private final RecipeSerializerUtil.ItemStackResult result;
            
            protected Result(Builder builder, ResourceLocation id)
            {
                this.id = id;
                this.group = builder._group == null ? "" : builder._group;
                this.input = new ConsumableItemIngredient(builder._input.stream());
                this.wire = new ConsumableItemIngredient(builder._wire.stream());
                this.redstone = new ConsumableItemIngredient(builder._redstone.stream());
                this.plate = new ConsumableItemIngredient(builder._plate.stream());
                this.processingTime = builder._processingTime;
                this.energyDrainPerTick = builder._energyDrainPerTick;
                
                this.advancement = builder._advancement;
                this.advancement.parent(ROOT_RECIPE_ADVANCEMENT).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id)).rewards(AdvancementRewards.Builder.recipe(id)).requirements(RequirementsStrategy.OR);
                    
                String prefix;
                if (builder._advancementIdPrefix == null || builder._advancementIdPrefix.isBlank())
                {
                    prefix = "recipes/misc/fabricator/";
                }
                else
                {
                    prefix = builder._advancementIdPrefix.trim();
                    if (prefix.charAt(prefix.length() - 1) != '/')
                        prefix += '/';
                    
                    prefix = "recipes/" + prefix;
                }
                
                this.advancementId = id.withPrefix(prefix);
                
                this.result = builder._result;
            }
            
            public Result(
                    ResourceLocation id,
                    String group,
                    ConsumableItemIngredient input,
                    ConsumableItemIngredient wire,
                    ConsumableItemIngredient redstone,
                    ConsumableItemIngredient plate,
                    int processingTime,
                    int energyDrainPerTick,
                    Advancement.Builder advancement,
                    RecipeSerializerUtil.ItemStackResult result)
            {
                this(
                        id,
                        group,
                        input,
                        wire,
                        redstone,
                        plate,
                        processingTime,
                        energyDrainPerTick,
                        advancement,
                        id.withPrefix("recipes/misc/fabricator/"),
                        result);
            }
            
            public Result(
                    ResourceLocation id,
                    String group,
                    ConsumableItemIngredient input,
                    ConsumableItemIngredient wire,
                    ConsumableItemIngredient redstone,
                    ConsumableItemIngredient plate,
                    int processingTime,
                    int energyDrainPerTick,
                    Advancement.Builder advancement,
                    ResourceLocation advancementId,
                    RecipeSerializerUtil.ItemStackResult result)
            {
                this.id = id;
                this.group = group == null ? "" : group;
                this.input = input;
                this.wire = wire;
                this.redstone = redstone;
                this.plate = plate;
                this.processingTime = processingTime;
                this.energyDrainPerTick = energyDrainPerTick;
                this.advancement = advancement;
                this.advancementId = advancementId;
                this.result = result;
            }

            @Override
            public final void serializeRecipeData(JsonObject json)
            {
                this.getType().writeJson(json, this);
            }

            @Override
            public final ResourceLocation getId()
            {
                return this.id;
            }

            @Override
            public final Serializer getType()
            {
                return TGRecipeSerializers.FABRICATOR.get();
            }

            @Override
            @Nullable
            public final JsonObject serializeAdvancement()
            {
                if (this.advancement == null)
                {
                    return null;
                }
                
                return this.advancement.serializeToJson();
            }

            @Override
            @Nullable
            public final ResourceLocation getAdvancementId()
            {
                return this.advancementId;
            }
            
        }
    }
}
