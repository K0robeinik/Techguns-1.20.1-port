package techguns2.machine.ammo_press;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

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
import net.minecraftforge.registries.RegistryManager;
import net.minecraftforge.registries.RegistryObject;
import techguns2.TGCustomRegistries;
import techguns2.TGRecipeSerializers;
import techguns2.item.ConsumableItemIngredient;
import techguns2.item.ConsumableItemIngredientSerializer;
import techguns2.util.RecipeSerializerUtil;

public final class AmmoPressRecipe implements IAmmoPressRecipe
{
    private final ResourceLocation _id;
    private final String _group;
    private final AmmoPressTemplate _template;
    private ConsumableItemIngredient _casing;
    private ConsumableItemIngredient _bullet;
    private ConsumableItemIngredient _powder;
    private ItemStack _result;
    private int _processingTime;
    private int _energyDrainPerTick;
    
    public AmmoPressRecipe(
            ResourceLocation id,
            String group,
            AmmoPressTemplate template,
            ConsumableItemIngredient casing,
            ConsumableItemIngredient bullet,
            ConsumableItemIngredient powder,
            ItemStack result,
            int processingTime,
            int energyDrainPerTick)
    {
        this._id = id;
        this._group = group == null ? "" : group;
        this._template = template;
        this._casing = casing;
        this._bullet = bullet;
        this._powder = powder;
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

    public final AmmoPressTemplate getTemplate()
    {
        return this._template;
    }

    public final ConsumableItemIngredient getCasingIngredient()
    {
        return this._casing;
    }

    public final ConsumableItemIngredient getBulletIngredient()
    {
        return this._bullet;
    }

    public final ConsumableItemIngredient getPowderIngredient()
    {
        return this._powder;
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
        return NonNullList.of(this._bullet, this._casing, this._powder);
    }
    
    @Override
    public final int testCasingWithConsumption(ItemStack itemStack)
    {
        return this._casing.testWithConsumption(itemStack);
    }
    @Override
    public final int testPowderWithConsumption(ItemStack itemStack)
    {
        return this._powder.testWithConsumption(itemStack);
    }
    @Override
    public final int testBulletWithConsumption(ItemStack itemStack)
    {
        return this._bullet.testWithConsumption(itemStack);
    }

    @Override
    public final boolean matches(AmmoPressBlockEntity blockEntity, Level level)
    {
        return blockEntity.getTemplate() == this._template &&
                this._casing.test(blockEntity.getCasingItem()) &&
                this._bullet.test(blockEntity.getBulletItem()) &&
                this._powder.test(blockEntity.getPowderItem());
    }

    @Override
    public final ItemStack assemble(AmmoPressBlockEntity container, RegistryAccess access)
    {
        return this._result.copy();
    }

    @Override
    public final Serializer getSerializer()
    {
        return TGRecipeSerializers.AMMO_PRESS.get();
    }
    
    public static final class Serializer implements RecipeSerializer<AmmoPressRecipe>
    {
        protected final AmmoPressRecipe readJson(ResourceLocation recipeId, JsonObject json)
        {
            String group = GsonHelper.getAsString(json, "group", "");
            String templateId = GsonHelper.getAsString(json, "template");
            AmmoPressTemplate template = RegistryManager.ACTIVE.getRegistry(TGCustomRegistries.AMMO_PRESS_TEMPLATES).getValue(new ResourceLocation(templateId));
            if (template == null)
                template = AmmoPressTemplates.DEFAULT;
            
            JsonObject ingredientsJson = GsonHelper.getAsJsonObject(json, "ingredients");
            
            ConsumableItemIngredient casing = ConsumableItemIngredientSerializer.INSTANCE.parse(ingredientsJson.get("casing"));
            ConsumableItemIngredient bullet = ConsumableItemIngredientSerializer.INSTANCE.parse(ingredientsJson.get("bullet"));
            ConsumableItemIngredient powder = ConsumableItemIngredientSerializer.INSTANCE.parse(ingredientsJson.get("powder"));
            int processingTime = GsonHelper.getAsInt(json, "processingTime");
            int energyDrainPerTick = GsonHelper.getAsInt(json, "energyDrainPerTick", 0);
            
            JsonObject resultJson = GsonHelper.getAsJsonObject(json, "result");
            ItemStack result = RecipeSerializerUtil.readItem(resultJson, false);
            
            return new AmmoPressRecipe(recipeId, group, template, casing, bullet, powder, result, processingTime, energyDrainPerTick);
        }
        
        protected final void writeJson(JsonObject json, AmmoPressRecipe recipe)
        {
            json.addProperty("group", recipe.getGroup());
            json.addProperty("template", recipe.getTemplate().getId().toString());
            json.add("casing", recipe.getCasingIngredient().toJson());
            json.add("bullet", recipe.getBulletIngredient().toJson());
            json.add("powder", recipe.getPowderIngredient().toJson());
            json.addProperty("processingTime", recipe.getProcessingTime());
            json.addProperty("energyDrainPerTick", recipe.getEnergyDrainPerTick());
            
            ItemStack result = recipe.getResultItem(null);
            JsonObject resultJson = new JsonObject();
            RecipeSerializerUtil.writeItem(resultJson, result);
            json.add("result", resultJson);
        }
        
        protected final void writeJson(JsonObject json, AmmoPressRecipe.Builder.Result recipe)
        {
            json.addProperty("group", recipe.group);
            json.addProperty("template", recipe.templateId.toString());
            json.add("casing", recipe.casing.toJson());
            json.add("bullet", recipe.bullet.toJson());
            json.add("powder", recipe.powder.toJson());
            json.addProperty("processingTime", recipe.processingTime);
            json.addProperty("energyDrainPerTick", recipe.energyDrainPerTick);
            
            JsonObject resultJson = new JsonObject();
            RecipeSerializerUtil.writeItem(resultJson, recipe.result);
            json.add("result", resultJson);
        }
        
        private final AmmoPressRecipe readFromBuffer(ResourceLocation recipeId, FriendlyByteBuf buffer)
        {
            String group = buffer.readUtf();
            ResourceLocation templateId = buffer.readResourceLocation();
            AmmoPressTemplate template = RegistryManager.ACTIVE.getRegistry(TGCustomRegistries.AMMO_PRESS_TEMPLATES).getValue(templateId);
            if (template == null)
                template = AmmoPressTemplates.DEFAULT;
            ConsumableItemIngredient casing = ConsumableItemIngredientSerializer.INSTANCE.parse(buffer);
            ConsumableItemIngredient bullet = ConsumableItemIngredientSerializer.INSTANCE.parse(buffer);
            ConsumableItemIngredient powder = ConsumableItemIngredientSerializer.INSTANCE.parse(buffer);
            int processingTime = buffer.readInt();
            int energyDrainPerTick = buffer.readInt();
            ItemStack result = RecipeSerializerUtil.readItem(buffer);
            
            return new AmmoPressRecipe(recipeId, group, template, casing, bullet, powder, result, processingTime, energyDrainPerTick);
        }
        
        private final void writeToBuffer(FriendlyByteBuf buffer, AmmoPressRecipe recipe)
        {
            buffer.writeUtf(recipe.getGroup());
            buffer.writeResourceLocation(recipe.getTemplate().getId());
            recipe.getCasingIngredient().toNetwork(buffer);
            recipe.getBulletIngredient().toNetwork(buffer);
            recipe.getPowderIngredient().toNetwork(buffer);
            buffer.writeInt(recipe.getProcessingTime());
            buffer.writeInt(recipe.getEnergyDrainPerTick());
            RecipeSerializerUtil.writeItem(buffer, recipe.getResultItem(null));
        }

        @Override
        public final AmmoPressRecipe fromJson(ResourceLocation recipeId, JsonObject json)
        {
            return this.readJson(recipeId, json);
        }

        @Override
        @Nullable
        public final AmmoPressRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
        {
            return this.readFromBuffer(recipeId, buffer);
        }

        @Override
        public final void toNetwork(FriendlyByteBuf buffer, AmmoPressRecipe recipe)
        {
            this.writeToBuffer(buffer, recipe);
        }
    }
    
    public static final class Builder implements RecipeBuilder
    {
        private ResourceLocation _template;
        private final List<ConsumableItemIngredient.Value> _casings;
        private final List<ConsumableItemIngredient.Value> _bullets;
        private final List<ConsumableItemIngredient.Value> _powders;
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
            this._template = null;
            this._casings = new ArrayList<ConsumableItemIngredient.Value>();
            this._bullets = new ArrayList<ConsumableItemIngredient.Value>();
            this._powders = new ArrayList<ConsumableItemIngredient.Value>();
            this._advancement = Advancement.Builder.recipeAdvancement();
            this._group = null;
            this._energyDrainPerTick = 0;
            this._processingTime = 0;
            this._result = null;
        }

        public final Builder template(AmmoPressTemplate template)
        {
            this._template = template.getId();
            return this;
        }
        public final Builder template(RegistryObject<? extends AmmoPressTemplate> object)
        {
            this._template = object.getId();
            return this;
        }
        public final Builder addCasing(ItemLike item)
        {
            this._casings.add(new ConsumableItemIngredient.ItemValue(new ItemStack(
                    item,
                    1)));
            return this;
        }
        public final Builder addCasing(RegistryObject<? extends ItemLike> object)
        {
            this._casings.add(new ConsumableItemIngredient.ItemValue(new ItemStack(
                    object.get(),
                    1)));
            return this;
        }
        public final Builder addCasing(ItemLike item, int count)
        {
            this._casings.add(new ConsumableItemIngredient.ItemValue(new ItemStack(
                    item,
                    Math.max(count, 1))));
            return this;
        }
        public final Builder addCasing(RegistryObject<? extends ItemLike> object, int count)
        {
            this._casings.add(new ConsumableItemIngredient.ItemValue(new ItemStack(
                    object.get(),
                    Math.max(count, 1))));
            return this;
        }
        public final Builder addCasing(TagKey<Item> itemTag)
        {
            this._casings.add(new ConsumableItemIngredient.TagValue(itemTag, 1));
            return this;
        }
        public final Builder addCasing(TagKey<Item> itemTag, int count)
        {
            this._casings.add(new ConsumableItemIngredient.TagValue(itemTag, Math.max(count, 1)));
            return this;
        }
        public final Builder addBullet(ItemLike item)
        {
            this._bullets.add(new ConsumableItemIngredient.ItemValue(new ItemStack(
                    item,
                    1)));
            return this;
        }
        public final Builder addBullet(RegistryObject<? extends ItemLike> object)
        {
            this._bullets.add(new ConsumableItemIngredient.ItemValue(new ItemStack(
                    object.get(),
                    1)));
            return this;
        }
        public final Builder addBullet(ItemLike item, int count)
        {
            this._bullets.add(new ConsumableItemIngredient.ItemValue(new ItemStack(
                    item,
                    Math.max(count, 1))));
            return this;
        }
        public final Builder addBullet(RegistryObject<? extends ItemLike> object, int count)
        {
            this._bullets.add(new ConsumableItemIngredient.ItemValue(new ItemStack(
                    object.get(),
                    Math.max(count, 1))));
            return this;
        }
        public final Builder addBullet(TagKey<Item> itemTag)
        {
            this._bullets.add(new ConsumableItemIngredient.TagValue(itemTag, 1));
            return this;
        }
        public final Builder addBullet(TagKey<Item> itemTag, int count)
        {
            this._bullets.add(new ConsumableItemIngredient.TagValue(itemTag, Math.max(count, 1)));
            return this;
        }
        public final Builder addPowder(ItemLike item)
        {
            this._powders.add(new ConsumableItemIngredient.ItemValue(new ItemStack(
                    item,
                    1)));
            return this;
        }
        public final Builder addPowder(RegistryObject<? extends ItemLike> object)
        {
            this._powders.add(new ConsumableItemIngredient.ItemValue(new ItemStack(
                    object.get(),
                    1)));
            return this;
        }
        public final Builder addPowder(ItemLike item, int count)
        {
            this._powders.add(new ConsumableItemIngredient.ItemValue(new ItemStack(
                    item,
                    Math.max(count, 1))));
            return this;
        }
        public final Builder addPowder(RegistryObject<? extends ItemLike> object, int count)
        {
            this._powders.add(new ConsumableItemIngredient.ItemValue(new ItemStack(
                    object.get(),
                    Math.max(count, 1))));
            return this;
        }
        public final Builder addPowder(TagKey<Item> itemTag)
        {
            this._bullets.add(new ConsumableItemIngredient.TagValue(itemTag, 1));
            return this;
        }
        public final Builder addPowder(TagKey<Item> itemTag, int count)
        {
            this._bullets.add(new ConsumableItemIngredient.TagValue(itemTag, Math.max(count, 1)));
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
        public final Builder withResult(ItemLike result, @javax.annotation.Nullable CompoundTag nbt)
        {
            this._result = new RecipeSerializerUtil.ItemStackResult(result.asItem(), nbt);
            return this;
        }
        public final Builder withResult(ItemLike result, int count)
        {
            this._result = new RecipeSerializerUtil.ItemStackResult(result.asItem(), count);
            return this;
        }
        public final Builder withResult(ItemLike result, int count, @javax.annotation.Nullable CompoundTag nbt)
        {
            this._result = new RecipeSerializerUtil.ItemStackResult(result.asItem(), count, nbt);
            return this;
        }
        public final Builder withResult(RegistryObject<? extends ItemLike> resultObject)
        {
            this._result = new RecipeSerializerUtil.ItemStackResult(resultObject.get().asItem());
            return this;
        }
        public final Builder withResult(RegistryObject<? extends ItemLike> resultObject, @javax.annotation.Nullable CompoundTag nbt)
        {
            this._result = new RecipeSerializerUtil.ItemStackResult(resultObject.get().asItem(), nbt);
            return this;
        }
        public final Builder withResult(RegistryObject<? extends ItemLike> resultObject, int count)
        {
            this._result = new RecipeSerializerUtil.ItemStackResult(resultObject.get().asItem(), count);
            return this;
        }
        public final Builder withResult(RegistryObject<? extends ItemLike> resultObject, int count, @javax.annotation.Nullable CompoundTag nbt)
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
            if (this._template == null)
                throw new IllegalStateException("No template has been specified");
            if (this._bullets.size() == 0)
                throw new IllegalStateException("No bullet ingredient(s) have been specified");
            if (this._casings.size() == 0)
                throw new IllegalStateException("No casing ingredient(s) have been specified");
            if (this._powders.size() == 0)
                throw new IllegalStateException("No powder ingredient(s) have been specified");
            
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

        public final void save(Consumer<FinishedRecipe> builder, ResourceLocation id)
        {
            builder.accept(this.build(id));
        }
        
        public static class Result implements FinishedRecipe
        {
            private final ResourceLocation id;
            private final String group;
            private final ResourceLocation templateId;
            private final ConsumableItemIngredient casing;
            private final ConsumableItemIngredient bullet;
            private final ConsumableItemIngredient powder;
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
                this.templateId = builder._template;
                this.casing = new ConsumableItemIngredient(builder._casings.stream());
                this.bullet = new ConsumableItemIngredient(builder._bullets.stream());
                this.powder = new ConsumableItemIngredient(builder._powders.stream());
                this.processingTime = builder._processingTime;
                this.energyDrainPerTick = builder._energyDrainPerTick;
                
                this.advancement = builder._advancement;
                this.advancement.parent(ROOT_RECIPE_ADVANCEMENT).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id)).rewards(AdvancementRewards.Builder.recipe(id)).requirements(RequirementsStrategy.OR);
                    
                String prefix;
                if (builder._advancementIdPrefix == null || builder._advancementIdPrefix.isBlank())
                {
                    prefix = "recipes/misc/ammo_press/";
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
                    @Nullable
                    String group,
                    ResourceLocation templateId,
                    ConsumableItemIngredient casing,
                    ConsumableItemIngredient bullet,
                    ConsumableItemIngredient powder,
                    int processingTime,
                    int energyDrainPerTick,
                    Advancement.Builder advancement,
                    RecipeSerializerUtil.ItemStackResult result)
            {
                this(
                        id,
                        group,
                        templateId,
                        casing,
                        bullet,
                        powder,
                        processingTime,
                        energyDrainPerTick,
                        advancement,
                        id.withPrefix("recipes/misc/ammo_press/"),
                        result);
            }
            
            public Result(
                    ResourceLocation id,
                    @Nullable
                    String group,
                    ResourceLocation templateId,
                    ConsumableItemIngredient casing,
                    ConsumableItemIngredient bullet,
                    ConsumableItemIngredient powder,
                    int processingTime,
                    int energyDrainPerTick,
                    Advancement.Builder advancement,
                    ResourceLocation advancementId,
                    RecipeSerializerUtil.ItemStackResult result)
            {
                this.id = id;
                this.group = group == null ? "" : group;
                this.templateId = templateId;
                this.casing = casing;
                this.bullet = bullet;
                this.powder = powder;
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
                return TGRecipeSerializers.AMMO_PRESS.get();
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
