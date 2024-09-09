package techguns2.machine.metal_press;

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

public final class MetalPressRecipe implements IMetalPressRecipe
{
    private final ResourceLocation _id;
    private final String _group;
    private final ConsumableItemIngredient _primaryIngredient;
    private final ConsumableItemIngredient _secondaryIngredient;
    private final int _processingTime;
    private final int _energyDrainPerTick;
    private final ItemStack _result;
    
    public MetalPressRecipe(
            ResourceLocation id,
            String group,
            ConsumableItemIngredient primaryIngredient,
            ConsumableItemIngredient secondaryIngredient,
            ItemStack result,
            int processingTime,
            int energyDrainPerTick)
    {
        this._id = id;
        this._group = group == null ? "" : group;
        this._primaryIngredient = primaryIngredient;
        this._secondaryIngredient = secondaryIngredient;
        this._result = result;
        this._processingTime = processingTime;
        this._energyDrainPerTick = energyDrainPerTick;
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

    public final ConsumableItemIngredient getPrimaryIngredient()
    {
        return this._primaryIngredient;
    }

    public final ConsumableItemIngredient getSecondaryIngredient()
    {
        return this._secondaryIngredient;
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
        return NonNullList.of(this._primaryIngredient, this._secondaryIngredient);
    }
    
    @Override
    public final int testPrimaryWithConsumption(ItemStack itemStack)
    {
        return this._primaryIngredient.testWithConsumption(itemStack);
    }
    
    @Override
    public final int testSecondaryWithConsumption(ItemStack itemStack)
    {
        return this._secondaryIngredient.testWithConsumption(itemStack);
    }

    @Override
    public final boolean matches(MetalPressBlockEntity blockEntity, Level level)
    {
        return this._primaryIngredient.test(blockEntity.getPrimaryItem()) &&
                this._secondaryIngredient.test(blockEntity.getSecondaryItem());
    }

    @Override
    public final ItemStack assemble(MetalPressBlockEntity blockEntity, RegistryAccess access)
    {
        return this._result.copy();
    }

    @Override
    public final Serializer getSerializer()
    {
        return TGRecipeSerializers.METAL_PRESS.get();
    }
    
    public static final class Serializer implements RecipeSerializer<MetalPressRecipe>
    {
        protected final MetalPressRecipe readJson(ResourceLocation recipeId, JsonObject json)
        {
            String group = GsonHelper.getAsString(json, "group", "");
            ConsumableItemIngredient primaryIngredient = ConsumableItemIngredientSerializer.INSTANCE.parse(json.get("primary"));
            ConsumableItemIngredient secondaryIngredient = ConsumableItemIngredientSerializer.INSTANCE.parse(json.get("secondary"));
            
            int processingTime = GsonHelper.getAsInt(json, "processingTime");
            int energyDrainPerTick = GsonHelper.getAsInt(json, "energyDrainPerTick", 0);
            
            JsonObject resultJson = GsonHelper.getAsJsonObject(json, "result");
            ItemStack result = RecipeSerializerUtil.readItem(resultJson, false);
            
            return new MetalPressRecipe(recipeId, group, primaryIngredient, secondaryIngredient, result, processingTime, energyDrainPerTick);
        }
        
        protected final void writeJson(JsonObject json, MetalPressRecipe recipe)
        {
            json.addProperty("group", recipe.getGroup());
            json.add("primary", recipe.getPrimaryIngredient().toJson());
            json.add("secondary", recipe.getSecondaryIngredient().toJson());
            json.addProperty("processingTime", recipe.getProcessingTime());
            json.addProperty("energyDrainPerTick", recipe.getEnergyDrainPerTick());
            
            ItemStack result = recipe.getResultItem(null);
            JsonObject resultJson = new JsonObject();
            RecipeSerializerUtil.writeItem(resultJson, result);
            
            json.add("result", resultJson);
        }
        
        protected final void writeJson(JsonObject json, MetalPressRecipe.Builder.Result recipe)
        {
            json.addProperty("group", recipe.group);
            json.add("primary", recipe.primary.toJson());
            json.add("secondary", recipe.secondary.toJson());
            json.addProperty("processingTime", recipe.processingTime);
            json.addProperty("energyDrainPerTick", recipe.energyDrainPerTick);
            
            JsonObject resultJson = new JsonObject();
            RecipeSerializerUtil.writeItem(resultJson, recipe.result);
            
            json.add("result", resultJson);
        }
        
        private final MetalPressRecipe readFromBuffer(ResourceLocation recipeId, FriendlyByteBuf buffer)
        {
            String group = buffer.readUtf();
            ConsumableItemIngredient primaryIngredient = ConsumableItemIngredientSerializer.INSTANCE.parse(buffer);
            ConsumableItemIngredient secondaryIngredient = ConsumableItemIngredientSerializer.INSTANCE.parse(buffer);
            int processingTime = buffer.readInt();
            int energyDrainPerTick = buffer.readInt();
            ItemStack result = RecipeSerializerUtil.readItem(buffer);
            
            return new MetalPressRecipe(recipeId, group, primaryIngredient, secondaryIngredient, result, processingTime, energyDrainPerTick);
        }
        
        private final void writeToBuffer(FriendlyByteBuf buffer, MetalPressRecipe recipe)
        {
            buffer.writeUtf(recipe.getGroup());
            recipe.getPrimaryIngredient().toNetwork(buffer);
            recipe.getSecondaryIngredient().toNetwork(buffer);
            buffer.writeInt(recipe.getProcessingTime());
            buffer.writeInt(recipe.getEnergyDrainPerTick());
            RecipeSerializerUtil.writeItem(buffer, recipe.getResultItem(null));
        }

        @Override
        public final MetalPressRecipe fromJson(ResourceLocation recipeId, JsonObject json)
        {
            return this.readJson(recipeId, json);
        }

        @Override
        @Nullable
        public final MetalPressRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
        {
            return this.readFromBuffer(recipeId, buffer);
        }

        @Override
        public final void toNetwork(FriendlyByteBuf buffer, MetalPressRecipe recipe)
        {
            this.writeToBuffer(buffer, recipe);
        }
    }
    
    public static final class Builder implements RecipeBuilder
    {
        private final List<ConsumableItemIngredient.Value> _primary;
        private final List<ConsumableItemIngredient.Value> _secondary;
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
            this._primary = new ArrayList<ConsumableItemIngredient.Value>();
            this._secondary = new ArrayList<ConsumableItemIngredient.Value>();
            this._advancement = Advancement.Builder.recipeAdvancement();
            this._group = null;
            this._energyDrainPerTick = 0;
            this._processingTime = 0;
            this._result = null;
        }

        public final Builder addPrimary(ItemLike item)
        {
            this._primary.add(new ConsumableItemIngredient.ItemValue(new ItemStack(item, 1)));
            return this;
        }
        public final Builder addPrimary(ItemLike item, int count)
        {
            this._primary.add(new ConsumableItemIngredient.ItemValue(new ItemStack(item, Math.max(count, 1))));
            return this;
        }
        public final Builder addPrimary(RegistryObject<? extends ItemLike> object)
        {
            this._primary.add(new ConsumableItemIngredient.ItemValue(new ItemStack(object.get(), 1)));
            return this;
        }
        public final Builder addPrimary(RegistryObject<? extends ItemLike> object, int count)
        {
            this._primary.add(new ConsumableItemIngredient.ItemValue(new ItemStack(object.get(), Math.max(count, 1))));
            return this;
        }
        public final Builder addPrimary(TagKey<Item> itemTag)
        {
            this._primary.add(new ConsumableItemIngredient.TagValue(itemTag, 1));
            return this;
        }
        public final Builder addPrimary(TagKey<Item> itemTag, int count)
        {
            this._primary.add(new ConsumableItemIngredient.TagValue(itemTag, Math.max(count, 1)));
            return this;
        }
        public final Builder addSecondary(ItemLike item)
        {
            this._secondary.add(new ConsumableItemIngredient.ItemValue(new ItemStack(item, 1)));
            return this;
        }
        public final Builder addSecondary(ItemLike item, int count)
        {
            this._secondary.add(new ConsumableItemIngredient.ItemValue(new ItemStack(item, Math.max(count, 1))));
            return this;
        }
        public final Builder addSecondary(RegistryObject<? extends ItemLike> object)
        {
            this._secondary.add(new ConsumableItemIngredient.ItemValue(new ItemStack(object.get(), 1)));
            return this;
        }
        public final Builder addSecondary(RegistryObject<? extends ItemLike> object, int count)
        {
            this._secondary.add(new ConsumableItemIngredient.ItemValue(new ItemStack(object.get(), Math.max(count, 1))));
            return this;
        }
        public final Builder addSecondary(TagKey<Item> itemTag)
        {
            this._secondary.add(new ConsumableItemIngredient.TagValue(itemTag, 1));
            return this;
        }
        public final Builder addSecondary(TagKey<Item> itemTag, int count)
        {
            this._secondary.add(new ConsumableItemIngredient.TagValue(itemTag, Math.max(count, 1)));
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
            if (this._primary.size() == 0)
                throw new IllegalStateException("No primary ingredient(s) have been specified");
            if (this._secondary.size() == 0)
                throw new IllegalStateException("No secondary ingredient(s) have been specified");
            
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
        
        public static final class Result implements FinishedRecipe
        {
            private final ResourceLocation id;
            private final String group;
            private final ConsumableItemIngredient primary;
            private final ConsumableItemIngredient secondary;
            private final int processingTime;
            private final int energyDrainPerTick;
            @Nullable
            private final Advancement.Builder advancement;
            @Nullable
            private final ResourceLocation advancementId;
            private final RecipeSerializerUtil.ItemStackResult result;
            
            private Result(Builder builder, ResourceLocation id)
            {
                this.id = id;
                this.group = builder._group == null ? "" : builder._group;
                this.primary = new ConsumableItemIngredient(builder._primary.stream());
                this.secondary = new ConsumableItemIngredient(builder._secondary.stream());
                this.processingTime = builder._processingTime;
                this.energyDrainPerTick = builder._energyDrainPerTick;
                
                this.advancement = builder._advancement;
                this.advancement.parent(ROOT_RECIPE_ADVANCEMENT).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id)).rewards(AdvancementRewards.Builder.recipe(id)).requirements(RequirementsStrategy.OR);
                    
                String prefix;
                if (builder._advancementIdPrefix == null || builder._advancementIdPrefix.isBlank())
                {
                    prefix = "recipes/misc/metal_press/";
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
                    ConsumableItemIngredient primary,
                    ConsumableItemIngredient secondary,
                    int processingTime,
                    int energyDrainPerTick,
                    Advancement.Builder advancement,
                    RecipeSerializerUtil.ItemStackResult result)
            {
                this(
                        id,
                        group,
                        primary,
                        secondary,
                        processingTime,
                        energyDrainPerTick,
                        advancement,
                        id.withPrefix("recipes/misc/metal_press/"),
                        result);
            }
            
            public Result(
                    ResourceLocation id,
                    String group,
                    ConsumableItemIngredient primary,
                    ConsumableItemIngredient secondary,
                    int processingTime,
                    int energyDrainPerTick,
                    Advancement.Builder advancement,
                    ResourceLocation advancementId,
                    RecipeSerializerUtil.ItemStackResult result)
            {
                this.id = id;
                this.group = group == null ? "" : group;
                this.primary = primary;
                this.secondary = secondary;
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
                return TGRecipeSerializers.METAL_PRESS.get();
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
