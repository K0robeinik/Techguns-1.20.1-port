package techguns2.machine.grinder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

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
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;
import techguns2.TGRecipeSerializers;
import techguns2.util.RecipeSerializerUtil;

public final class GrinderRecipe implements IGrinderRecipe
{
    private final ResourceLocation _id;
    private final String _group;
    private final Ingredient _inputItem;
    private final List<ItemStack> _outputs;

    private final int _processingTime;
    private final int _energyDrainPerTick;
    
    public GrinderRecipe(
            ResourceLocation id,
            String group,
            Ingredient inputItem,
            List<ItemStack> outputs,
            int processingTime,
            int energyDrainPerTick)
    {
        this._id = id;
        this._group = group;
        this._inputItem = inputItem;
        this._outputs = outputs;
        this._processingTime = Math.max(0, processingTime);
        this._energyDrainPerTick = Math.max(0, energyDrainPerTick);
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
    
    public final Ingredient getInputItemIngredient()
    {
        return this._inputItem;
    }

    @Override
    public final int testInputItemWithConsumption(ItemStack itemStack)
    {
        if (this._inputItem.test(itemStack))
            return 1;
        else
            return -1;
    }
    
    public final ItemStack getPrimaryOutputItem()
    {
        return this._outputs.get(0);
    }
    
    public final List<ItemStack> getOutputItems()
    {
        return this._outputs;
    }
    
    @Override
    public final int getEnergyDrainPerTick()
    {
        return this._energyDrainPerTick;
    }
    
    @Override
    public final int getProcessingTime()
    {
        return this._processingTime;
    }

    @Override
    public final boolean matches(GrinderBlockEntity blockEntity, Level level)
    {
        return this._inputItem.test(blockEntity.getInputItem());
    }
    
    @Override
    public final List<ItemStack> assembleAll(GrinderBlockEntity blockEntity, RegistryAccess access)
    {
        List<ItemStack> result = new ArrayList<ItemStack>();
        for (ItemStack output : this._outputs)
        {
            result.add(output.copy());
        }
        
        return result;
    }

    @Override
    public final ItemStack getResultItem(RegistryAccess access)
    {
        return this._outputs.get(0);
    }

    @Override
    public final List<ItemStack> getResultItems(RegistryAccess access)
    {
        return this._outputs;
    }

    @Override
    public final Serializer getSerializer()
    {
        return TGRecipeSerializers.GRINDER.get();
    }
    
    @Override
    public final NonNullList<Ingredient> getIngredients()
    {
        return NonNullList.of(this._inputItem);
    }
    
    public static final class Serializer implements RecipeSerializer<GrinderRecipe>
    {
        protected final GrinderRecipe readJson(ResourceLocation recipeId, JsonObject json)
        {
            String group = GsonHelper.getAsString(json, "group", "");
            Ingredient ingredient = Ingredient.fromJson(json.get("ingredient"));
            
            int processingTime = GsonHelper.getAsInt(json, "processingTime", 100);
            int energyDrainPerTick = GsonHelper.getAsInt(json, "energyDrainPerTick", 0);

            JsonElement resultJson = json.get("result");
            
            if (resultJson == null || resultJson.isJsonNull())
                throw new JsonSyntaxException("Result cannot be null");
            
            List<ItemStack> resultItems;
            
            if (resultJson.isJsonObject())
            {
                JsonObject resultJsonObject = resultJson.getAsJsonObject();
                
                resultItems = List.of(RecipeSerializerUtil.readItem(resultJsonObject, false));
            }
            else if (resultJson.isJsonArray())
            {
                JsonArray resultJsonArray = resultJson.getAsJsonArray();
                if (resultJsonArray.size() == 0)
                    throw new JsonSyntaxException("Result array cannot be empty, at least one result must be defined");
                
                resultItems = StreamSupport.stream(resultJsonArray.spliterator(), false)
                    .map(elementJson -> RecipeSerializerUtil.readItem(GsonHelper.convertToJsonObject(elementJson, "item"), false))
                    .toList();
            }
            else
            {
                throw new JsonSyntaxException("Expected result to be object or array of objects");
            }
            
            return new GrinderRecipe(recipeId, group, ingredient, resultItems, processingTime, energyDrainPerTick);
        }
        
        protected final void writeJson(JsonObject json, GrinderRecipe recipe)
        {
            json.addProperty("group", recipe.getGroup());
            json.add("ingredient", recipe.getInputItemIngredient().toJson());
            json.addProperty("processingTime", recipe.getProcessingTime());
            json.addProperty("energyDrainPerTick", recipe.getEnergyDrainPerTick());

            List<ItemStack> resultItems = recipe.getOutputItems();
            if (resultItems.size() == 1)
            {
                ItemStack resultItem = resultItems.get(0);
                JsonObject resultJson = new JsonObject();
                RecipeSerializerUtil.writeItem(resultJson, resultItem);
                json.add("result", resultJson);
            }
            else
            {
                JsonArray resultJson = new JsonArray(resultItems.size());
                
                for (ItemStack resultItem : resultItems)
                {
                    JsonObject itemJson = new JsonObject();
                    RecipeSerializerUtil.writeItem(itemJson, resultItem);
                    resultJson.add(itemJson);
                }
                
                json.add("result", resultJson);
            }
        }
        
        protected final void writeJson(JsonObject json, GrinderRecipe.Builder.Result recipe)
        {
            json.addProperty("group", recipe.group);
            json.add("ingredient", recipe.input.toJson());
            json.addProperty("processingTime", recipe.processingTime);
            json.addProperty("energyDrainPerTick", recipe.energyDrainPerTick);

            List<RecipeSerializerUtil.ItemStackResult> resultItems = recipe.results;
            if (resultItems.size() == 1)
            {
                JsonObject resultJson = new JsonObject();
                RecipeSerializerUtil.writeItem(resultJson, resultItems.get(0));
                json.add("result", resultJson);
            }
            else
            {
                JsonArray resultJson = new JsonArray(resultItems.size());
                
                for (RecipeSerializerUtil.ItemStackResult resultItem : resultItems)
                {
                    JsonObject itemJson = new JsonObject();
                    RecipeSerializerUtil.writeItem(itemJson, resultItem);
                    resultJson.add(itemJson);
                }
                
                json.add("result", resultJson);
            }
        }
        
        private final GrinderRecipe readFromBuffer(ResourceLocation recipeId, FriendlyByteBuf buffer)
        {
            String group = buffer.readUtf();
            Ingredient ingredient = Ingredient.fromNetwork(buffer);
            int processingTime = buffer.readInt();
            int energyDrainPerTick = buffer.readInt();
            
            int resultCount = Math.min(buffer.readByte(), 9);
            List<ItemStack> resultItems = Stream.generate(() -> RecipeSerializerUtil.readItem(buffer)).limit(resultCount).toList();
            
            return new GrinderRecipe(recipeId, group, ingredient, resultItems, processingTime, energyDrainPerTick);
        }
        
        private final void writeToBuffer(FriendlyByteBuf buffer, GrinderRecipe recipe)
        {
            buffer.writeUtf(recipe.getGroup());
            recipe.getInputItemIngredient().toNetwork(buffer);
            buffer.writeInt(recipe.getProcessingTime());
            buffer.writeInt(recipe.getEnergyDrainPerTick());
            int resultItemCount = Math.min(recipe.getOutputItems().size(), 9);
            buffer.writeByte(resultItemCount);
            for (ItemStack resultItem : recipe.getOutputItems())
            {
                RecipeSerializerUtil.writeItem(buffer, resultItem);
                
                resultItemCount--;
                if (resultItemCount <= 0)
                    break;
            }
        }

        @Override
        public final GrinderRecipe fromJson(ResourceLocation recipeId, JsonObject json)
        {
            return this.readJson(recipeId, json);
        }

        @Override
        @Nullable
        public final GrinderRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
        {
            return this.readFromBuffer(recipeId, buffer);
        }

        @Override
        public final void toNetwork(FriendlyByteBuf buffer, GrinderRecipe recipe)
        {
            this.writeToBuffer(buffer, recipe);
        }
    }
    
    public static final class Builder implements RecipeBuilder
    {
        private final List<Ingredient.Value> _input;
        private final Advancement.Builder _advancement;
        @Nullable
        private String _advancementIdPrefix;
        @Nullable
        private String _group;
        private int _energyDrainPerTick;
        private int _processingTime;
        private final List<RecipeSerializerUtil.ItemStackResult> _results;
        
        public Builder()
        {
            this._input = new ArrayList<Ingredient.Value>();
            this._advancement = Advancement.Builder.recipeAdvancement();
            this._group = null;
            this._energyDrainPerTick = 0;
            this._processingTime = 100;
            this._results = new ArrayList<RecipeSerializerUtil.ItemStackResult>();
        }

        public final Builder addInput(ItemLike item)
        {
            this._input.add(new Ingredient.ItemValue(new ItemStack(item)));
            return this;
        }
        public final Builder addInput(RegistryObject<? extends ItemLike> object)
        {
            this._input.add(new Ingredient.ItemValue(new ItemStack(object.get())));
            return this;
        }
        public final Builder addInput(TagKey<Item> itemTag)
        {
            this._input.add(new Ingredient.TagValue(itemTag));
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
        private final void ensureCanAddResult()
        {
            if (this._results.size() >= 9)
                throw new IllegalStateException("Cannot add more than 9 results");
        }
        
        public final Builder withResult(ItemLike result)
        {
            this.ensureCanAddResult();
            this._results.add(new RecipeSerializerUtil.ItemStackResult(result.asItem()));
            return this;
        }
        public final Builder withResult(ItemLike result, @Nullable CompoundTag nbt)
        {
            this.ensureCanAddResult();
            this._results.add(new RecipeSerializerUtil.ItemStackResult(result.asItem(), nbt));
            return this;
        }
        public final Builder withResult(ItemLike result, int count)
        {
            this.ensureCanAddResult();
            this._results.add(new RecipeSerializerUtil.ItemStackResult(result.asItem(), count));
            return this;
        }
        public final Builder withResult(ItemLike result, int count, @Nullable CompoundTag nbt)
        {
            this.ensureCanAddResult();
            this._results.add(new RecipeSerializerUtil.ItemStackResult(result.asItem(), count, nbt));
            return this;
        }
        public final Builder withResult(RegistryObject<? extends ItemLike> resultObject)
        {
            this.ensureCanAddResult();
            this._results.add(new RecipeSerializerUtil.ItemStackResult(resultObject.get().asItem()));
            return this;
        }
        public final Builder withResult(RegistryObject<? extends ItemLike> resultObject, @Nullable CompoundTag nbt)
        {
            this.ensureCanAddResult();
            this._results.add(new RecipeSerializerUtil.ItemStackResult(resultObject.get().asItem(), nbt));
            return this;
        }
        public final Builder withResult(RegistryObject<? extends ItemLike> resultObject, int count)
        {
            this.ensureCanAddResult();
            this._results.add(new RecipeSerializerUtil.ItemStackResult(resultObject.get().asItem(), count));
            return this;
        }
        public final Builder withResult(RegistryObject<? extends ItemLike> resultObject, int count, @Nullable CompoundTag nbt)
        {
            this.ensureCanAddResult();
            this._results.add(new RecipeSerializerUtil.ItemStackResult(resultObject.get().asItem(), count, nbt));
            return this;
        }
        
        @Override
        public final Item getResult()
        {
            if (this._results.size() == 0)
                return null;
            return this._results.get(0).item;
        }
        
        protected final void validate(ResourceLocation id)
        {
            if (this._input.size() == 0)
                throw new IllegalStateException("No input ingredient(s) have been specified");
            
            if (this._advancement.getCriteria().size() == 0)
                throw new IllegalStateException("No way of obtaining recipe " + id);
            
            if (this._results.size() == 0)
                throw new IllegalStateException("Result Items haven't been specified");
        }
        
        public final Result build(ResourceLocation id)
        {
            this.validate(id);
            return new Result(this, id);
        }

        public final void save(Consumer<FinishedRecipe> builder, ResourceLocation id)
        {
            builder.accept(this.build(id));
        }
        
        public static class Result implements FinishedRecipe
        {
            private final ResourceLocation id;
            private final String group;
            private final Ingredient input;
            private final int processingTime;
            private final int energyDrainPerTick;
            @Nullable
            private final Advancement.Builder advancement;
            @Nullable
            private final ResourceLocation advancementId;
            private final List<RecipeSerializerUtil.ItemStackResult> results;
            
            private Result(Builder builder, ResourceLocation id)
            {
                this.id = id;
                this.group = builder._group == null ? "" : builder._group;
                this.input = Ingredient.fromValues(builder._input.stream());
                this.processingTime = builder._processingTime;
                this.energyDrainPerTick = builder._energyDrainPerTick;
                
                this.advancement = builder._advancement;
                this.advancement.parent(ROOT_RECIPE_ADVANCEMENT).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id)).rewards(AdvancementRewards.Builder.recipe(id)).requirements(RequirementsStrategy.OR);
                    
                String prefix;
                if (builder._advancementIdPrefix == null || builder._advancementIdPrefix.isBlank())
                {
                    prefix = "recipes/misc/grinder/";
                }
                else
                {
                    prefix = builder._advancementIdPrefix.trim();
                    if (prefix.charAt(prefix.length() - 1) != '/')
                        prefix += '/';
                    
                    prefix = "recipes/" + prefix;
                }
                
                this.advancementId = id.withPrefix(prefix);
                
                this.results = ImmutableList.copyOf(builder._results);
            }
            
            public Result(
                    ResourceLocation id,
                    String group,
                    Ingredient input,
                    int processingTime,
                    int energyDrainPerTick,
                    Advancement.Builder advancement,
                    List<RecipeSerializerUtil.ItemStackResult> results)
            {
                this(
                        id,
                        group,
                        input,
                        processingTime,
                        energyDrainPerTick,
                        advancement,
                        id.withPrefix("recipes/misc/grinder/"),
                        results);
            }
            
            public Result(
                    ResourceLocation id,
                    String group,
                    Ingredient input,
                    int processingTime,
                    int energyDrainPerTick,
                    Advancement.Builder advancement,
                    ResourceLocation advancementId,
                    List<RecipeSerializerUtil.ItemStackResult> results)
            {
                this.id = id;
                this.group = group == null ? "" : group;
                this.input = input;
                this.processingTime = processingTime;
                this.energyDrainPerTick = energyDrainPerTick;
                this.advancement = advancement;
                this.advancementId = advancementId;
                this.results = ImmutableList.copyOf(results);
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
                return TGRecipeSerializers.GRINDER.get();
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
