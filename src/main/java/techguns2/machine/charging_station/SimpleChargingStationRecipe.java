package techguns2.machine.charging_station;

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
import techguns2.util.RecipeSerializerUtil;

public final class SimpleChargingStationRecipe implements IChargingStationRecipe
{
    private final ResourceLocation _id;
    private final String _group;
    private final Ingredient _input;
    private final ItemStack _result;
    private final int _energyNeeded;

    public SimpleChargingStationRecipe(
            ResourceLocation id,
            String group,
            Ingredient input,
            ItemStack result,
            int energyNeeded)
    {
        this._id = id;
        this._group = group == null ? "" : group;
        this._input = input;
        this._result = result;
        this._energyNeeded = Math.max(energyNeeded, 1);
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
    
    public final Ingredient getInputIngredient()
    {
        return this._input;
    }

    @Override
    public final int getEnergyNeeded(ItemStack itemStack)
    {
        return this._energyNeeded;
    }
    
    @Override
    public final boolean canCharge(ItemStack itemStack)
    {
        return this._input.test(itemStack);
    }

    @Override
    public final ItemStack getResultItem(RegistryAccess registryAccess)
    {
        return this._result;
    }

    @Override
    public final NonNullList<Ingredient> getIngredients()
    {
        return NonNullList.of(this._input);
    }
    
    @Override
    public final boolean matches(ChargingStationBlockEntity blockEntity, Level level)
    {
        return this._input.test(blockEntity.getInputItem());
    }

    @Override
    public final ItemStack assemble(ChargingStationBlockEntity blockEntity, RegistryAccess registryAccess)
    {
        return this._result.copy();
    }

    @Override
    public final Serializer getSerializer()
    {
        return TGRecipeSerializers.SIMPLE_CHARGING.get();
    }
    
    public static final class Serializer implements RecipeSerializer<SimpleChargingStationRecipe>
    {
        protected final SimpleChargingStationRecipe readJson(ResourceLocation recipeId, JsonObject json)
        {
            String group = GsonHelper.getAsString(json, "group", "");
            Ingredient inputIngredient = Ingredient.fromJson(json.get("ingredient"));
            
            int energyNeeded = GsonHelper.getAsInt(json, "energyNeeded", 1);
            
            JsonObject resultJson = GsonHelper.getAsJsonObject(json, "result");
            ItemStack result = RecipeSerializerUtil.readItem(resultJson, false);
            
            return new SimpleChargingStationRecipe(
                    recipeId,
                    group,
                    inputIngredient,
                    result,
                    energyNeeded);
        }
        
        protected final void writeJson(JsonObject json, SimpleChargingStationRecipe recipe)
        {
            json.addProperty("group", recipe.getGroup());
            
            json.add("ingredient", recipe.getInputIngredient().toJson());
            json.addProperty("energyNeeded", recipe.getEnergyNeeded(ItemStack.EMPTY));
            
            ItemStack result = recipe.getResultItem(null);
            JsonObject resultJson = new JsonObject();
            RecipeSerializerUtil.writeItem(resultJson, result);
            
            json.add("result", resultJson);
        }
        
        protected final void writeJson(JsonObject json, SimpleChargingStationRecipe.Builder.Result recipe)
        {
            json.addProperty("group", recipe.group);
            
            json.add("ingredient", recipe.input.toJson());
            json.addProperty("energyNeeded", recipe.energyNeeded);
            
            JsonObject resultJson = new JsonObject();
            RecipeSerializerUtil.writeItem(resultJson, recipe.result);
            
            json.add("result", resultJson);
        }
        
        private final SimpleChargingStationRecipe readFromBuffer(ResourceLocation recipeId, FriendlyByteBuf buffer)
        {
            String group = buffer.readUtf();
            Ingredient inputIngredient = Ingredient.fromNetwork(buffer);
            int neededEnergy = buffer.readInt();
            ItemStack result = RecipeSerializerUtil.readItem(buffer);
            
            return new SimpleChargingStationRecipe(
                    recipeId,
                    group,
                    inputIngredient,
                    result,
                    neededEnergy);
        }
        
        private final void writeToBuffer(FriendlyByteBuf buffer, SimpleChargingStationRecipe recipe)
        {
            buffer.writeUtf(recipe.getGroup());
            recipe.getInputIngredient().toNetwork(buffer);
            buffer.writeInt(recipe.getEnergyNeeded(ItemStack.EMPTY));
            RecipeSerializerUtil.writeItem(buffer, recipe.getResultItem(null));
        }

        @Override
        public final SimpleChargingStationRecipe fromJson(ResourceLocation recipeId, JsonObject json)
        {
            return this.readJson(recipeId, json);
        }

        @Override
        @Nullable
        public final SimpleChargingStationRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
        {
            return this.readFromBuffer(recipeId, buffer);
        }

        @Override
        public final void toNetwork(FriendlyByteBuf buffer, SimpleChargingStationRecipe recipe)
        {
            this.writeToBuffer(buffer, recipe);
        }
    }
    
    public static final class Builder implements RecipeBuilder
    {
        private final List<ConsumableItemIngredient.Value> _input;
        private final Advancement.Builder _advancement;
        @Nullable
        private String _advancementIdPrefix;
        @Nullable
        private String _group;
        private int _energyNeeded;
        private RecipeSerializerUtil.ItemStackResult _result;
        
        public Builder()
        {
            this._input = new ArrayList<ConsumableItemIngredient.Value>();
            this._advancement = Advancement.Builder.recipeAdvancement();
            this._group = null;
            this._energyNeeded = 1;
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

        public final Builder withEnergyNeeded(int amount)
        {
            this._energyNeeded = Math.max(amount, 1);
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
            private final int energyNeeded;
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
                this.energyNeeded = builder._energyNeeded;
                
                this.advancement = builder._advancement;
                this.advancement.parent(ROOT_RECIPE_ADVANCEMENT).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id)).rewards(AdvancementRewards.Builder.recipe(id)).requirements(RequirementsStrategy.OR);
                    
                String prefix;
                if (builder._advancementIdPrefix == null || builder._advancementIdPrefix.isBlank())
                {
                    prefix = "recipes/misc/charging/";
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
                    int energyNeeded,
                    Advancement.Builder advancement,
                    RecipeSerializerUtil.ItemStackResult result)
            {
                this(
                        id,
                        group,
                        input,
                        energyNeeded,
                        advancement,
                        id.withPrefix("recipes/misc/charging/"),
                        result);
            }
            
            public Result(
                    ResourceLocation id,
                    String group,
                    ConsumableItemIngredient input,
                    int energyNeeded,
                    Advancement.Builder advancement,
                    ResourceLocation advancementId,
                    RecipeSerializerUtil.ItemStackResult result)
            {
                this.id = id;
                this.group = group == null ? "" : group;
                this.input = input;
                this.energyNeeded = energyNeeded;
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
                return TGRecipeSerializers.SIMPLE_CHARGING.get();
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
