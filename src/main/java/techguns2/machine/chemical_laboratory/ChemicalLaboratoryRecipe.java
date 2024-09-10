package techguns2.machine.chemical_laboratory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.google.gson.JsonElement;
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
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.RegistryObject;
import techguns2.TGRecipeSerializers;
import techguns2.fluid.FluidIngredient;
import techguns2.item.ConsumableItemIngredient;
import techguns2.item.ConsumableItemIngredientSerializer;
import techguns2.util.RecipeSerializerUtil;

public final class ChemicalLaboratoryRecipe implements IChemicalLaboratoryRecipe
{
    private final ResourceLocation _id;
    private final String _group;
    private final ConsumableItemIngredient _primaryIngredient;
    private final ConsumableItemIngredient _secondaryIngredient;
    private final ConsumableItemIngredient _tankIngredient;
    private final FluidIngredient _fluidIngredient;
    private final int _processingTime;
    private final int _energyDrainPerTick;
    private final ItemStack _resultItem;
    private final FluidStack _resultFluid;
    
    public ChemicalLaboratoryRecipe(
            ResourceLocation id,
            String group,
            ConsumableItemIngredient primaryIngredient,
            ConsumableItemIngredient secondaryIngredient,
            ConsumableItemIngredient tankIngredient,
            FluidIngredient fluidIngredient,
            ItemStack itemResult,
            FluidStack fluidResult,
            int processingTime,
            int energyDrainPerTick)
    {
        this._id = id;
        this._group = group;
        this._primaryIngredient = primaryIngredient;
        this._secondaryIngredient = secondaryIngredient;
        this._tankIngredient = tankIngredient;
        this._fluidIngredient = fluidIngredient;
        this._resultItem = itemResult;
        this._resultFluid = fluidResult;
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
    
    @Override
    public final int testPrimaryWithConsumption(ItemStack itemStack)
    {
        return this._primaryIngredient.testWithConsumption(itemStack);
    }

    public final ConsumableItemIngredient getSecondaryIngredient()
    {
        return this._secondaryIngredient;
    }
    
    @Override
    public final int testSecondaryWithConsumption(ItemStack itemStack)
    {
        return this._secondaryIngredient.testWithConsumption(itemStack);
    }

    public final ConsumableItemIngredient getTankIngredient()
    {
        return this._tankIngredient;
    }
    
    @Override
    public final int testTankWithConsumption(ItemStack itemStack)
    {
        return this._tankIngredient.testWithConsumption(itemStack);
    }

    public final FluidIngredient getFluidIngredient()
    {
        return this._fluidIngredient;
    }
    
    @Override
    public final int testFluidWithConsumption(FluidStack fluidStack)
    {
        return this._fluidIngredient.testWithConsumption(fluidStack);
    }

    @Override
    public final ItemStack getResultItem(RegistryAccess registryAccess)
    {
        return this._resultItem;
    }

    @Override
    public final FluidStack getResultFluid(RegistryAccess registryAccess)
    {
        return this._resultFluid;
    }

    public final int getProcessingTime()
    {
        return this._processingTime;
    }

    public final int getEnergyDrainPerTick()
    {
        return this._energyDrainPerTick;
    }

    @Override
    public final NonNullList<Ingredient> getIngredients()
    {
        return NonNullList.of(this._primaryIngredient, this._secondaryIngredient, this._tankIngredient);
    }
    
    public final NonNullList<FluidIngredient> getFluidIngredients()
    {
        return NonNullList.of(this._fluidIngredient);
    }

    @Override
    public final boolean matches(ChemicalLaboratoryBlockEntity blockEntity, Level level)
    {
        return this._primaryIngredient.test(blockEntity.getPrimaryItem()) &&
                this._secondaryIngredient.test(blockEntity.getSecondaryItem()) &&
                this._tankIngredient.test(blockEntity.getTankItem()) &&
                this._fluidIngredient.test(blockEntity.getFluid());
    }

    @Override
    public final ItemStack assemble(ChemicalLaboratoryBlockEntity blockEntity, RegistryAccess access)
    {
        return this._resultItem.copy();
    }

    @Override
    public final FluidStack assembleFluid(ChemicalLaboratoryBlockEntity blockEntity, RegistryAccess access)
    {
        return this._resultFluid.copy();
    }

    @Override
    public final Serializer getSerializer()
    {
        return TGRecipeSerializers.CHEMICAL_LABORATORY.get();
    }
    
    public static class Serializer implements RecipeSerializer<ChemicalLaboratoryRecipe>
    {
        protected final ChemicalLaboratoryRecipe readJson(ResourceLocation recipeId, JsonObject json)
        {
            String group = GsonHelper.getAsString(json, "group", "");
            JsonObject ingredientsJson = GsonHelper.getAsJsonObject(json, "ingredients");
            
            JsonElement primaryIngredientJson = ingredientsJson.get("primary");
            JsonElement secondaryIngredientJson = ingredientsJson.get("secondary");
            JsonElement tankIngredientJson = ingredientsJson.get("tank");
            JsonElement fluidIngredientJson = ingredientsJson.get("fluid");
            
            ConsumableItemIngredient primaryIngredient;
            if (primaryIngredientJson != null && !primaryIngredientJson.isJsonNull())
                primaryIngredient = ConsumableItemIngredientSerializer.INSTANCE.parse(primaryIngredientJson);
            else
                primaryIngredient = ConsumableItemIngredient.EMPTY;
            
            ConsumableItemIngredient secondaryIngredient;
            if (secondaryIngredientJson != null && !secondaryIngredientJson.isJsonNull())
                secondaryIngredient = ConsumableItemIngredientSerializer.INSTANCE.parse(secondaryIngredientJson);
            else
                secondaryIngredient = ConsumableItemIngredient.EMPTY;
            
            ConsumableItemIngredient tankIngredient;
            if (tankIngredientJson != null && !tankIngredientJson.isJsonNull())
                tankIngredient = ConsumableItemIngredientSerializer.INSTANCE.parse(tankIngredientJson);
            else
                tankIngredient = ConsumableItemIngredient.EMPTY;
            
            FluidIngredient fluidIngredient;
            if (fluidIngredientJson != null && !fluidIngredientJson.isJsonNull())
                fluidIngredient = FluidIngredient.fromJson(fluidIngredientJson);
            else
                fluidIngredient = FluidIngredient.EMPTY;
            
            int processingTime = GsonHelper.getAsInt(json, "processingTime", 100);
            int energyDrainPerTick = GsonHelper.getAsInt(json, "energyDrainPerTick", 0);
            
            JsonObject resultsJson = GsonHelper.getAsJsonObject(json, "results");
            
            JsonElement itemResultJson = resultsJson.get("item");
            JsonElement fluidResultJson = resultsJson.get("fluid");
            
            ItemStack itemResult;
            if (itemResultJson != null && itemResultJson.isJsonObject())
            {
                JsonObject itemResultJsonObject = itemResultJson.getAsJsonObject();
                
                itemResult = RecipeSerializerUtil.readItem(itemResultJsonObject, false);
            }
            else
                itemResult = ItemStack.EMPTY;
            
            FluidStack fluidResult;
            if (fluidResultJson != null && fluidResultJson.isJsonObject())
            {
                JsonObject fluidResultJsonObject = itemResultJson.getAsJsonObject();
                
                fluidResult = RecipeSerializerUtil.readFluid(fluidResultJsonObject, false);
            }
            else
                fluidResult = FluidStack.EMPTY;
            
            return new ChemicalLaboratoryRecipe(
                    recipeId,
                    group,
                    primaryIngredient,
                    secondaryIngredient,
                    tankIngredient,
                    fluidIngredient,
                    itemResult,
                    fluidResult,
                    processingTime,
                    energyDrainPerTick);
        }
        
        protected final void writeJson(JsonObject json, ChemicalLaboratoryRecipe recipe)
        {
            json.addProperty("group", recipe.getGroup());
            
            JsonObject ingredientsJson = new JsonObject();
            
            ConsumableItemIngredient primaryIngredient = recipe.getPrimaryIngredient();
            ConsumableItemIngredient secondaryIngredient = recipe.getSecondaryIngredient();
            ConsumableItemIngredient tankIngredient = recipe.getTankIngredient();
            FluidIngredient fluidIngredient = recipe.getFluidIngredient();
            
            if (!primaryIngredient.isEmpty())
                ingredientsJson.add("primary", primaryIngredient.toJson());
            if (!secondaryIngredient.isEmpty())
                ingredientsJson.add("secondary", secondaryIngredient.toJson());
            if (!tankIngredient.isEmpty())
                ingredientsJson.add("tank", tankIngredient.toJson());
            if (!fluidIngredient.isEmpty())
                ingredientsJson.add("fluid", fluidIngredient.toJson());
            
            json.add("ingredients", ingredientsJson);
            json.addProperty("processingTime", recipe.getProcessingTime());
            json.addProperty("energyDrainPerTick", recipe.getEnergyDrainPerTick());
            
            
            JsonObject resultsJson = new JsonObject();
            
            ItemStack resultItem = recipe.getResultItem(null);
            FluidStack resultFluid = recipe.getResultFluid(null);
            
            if (!resultItem.isEmpty())
            {
                JsonObject resultJson = new JsonObject();
                RecipeSerializerUtil.writeItem(resultJson, resultItem);
                resultsJson.add("item", resultJson);
            }
            
            if (!resultFluid.isEmpty())
            {
                JsonObject resultJson = new JsonObject();
                RecipeSerializerUtil.writeFluid(resultJson, resultFluid);
                resultsJson.add("fluid", resultJson);
            }
            
            
            json.add("results", resultsJson);
        }
        
        protected final void writeJson(JsonObject json, ChemicalLaboratoryRecipe.Builder.Result recipe)
        {
            json.addProperty("group", recipe.group);
            
            JsonObject ingredientsJson = new JsonObject();
            
            ConsumableItemIngredient primaryIngredient = recipe.primary;
            ConsumableItemIngredient secondaryIngredient = recipe.secondary;
            ConsumableItemIngredient tankIngredient = recipe.tank;
            FluidIngredient fluidIngredient = recipe.fluid;
            
            if (!primaryIngredient.isEmpty())
                ingredientsJson.add("primary", primaryIngredient.toJson());
            if (!secondaryIngredient.isEmpty())
                ingredientsJson.add("secondary", secondaryIngredient.toJson());
            if (!tankIngredient.isEmpty())
                ingredientsJson.add("tank", tankIngredient.toJson());
            if (!fluidIngredient.isEmpty())
                ingredientsJson.add("fluid", fluidIngredient.toJson());
            
            json.add("ingredients", ingredientsJson);
            json.addProperty("processingTime", recipe.processingTime);
            json.addProperty("energyDrainPerTick", recipe.energyDrainPerTick);
            
            
            JsonObject resultsJson = new JsonObject();
            
            var resultItem = recipe.resultItem;
            var resultFluid = recipe.resultFluid;
            
            if (resultItem != null && resultItem.item != Items.AIR && resultItem.count > 0)
            {
                JsonObject resultJson = new JsonObject();
                RecipeSerializerUtil.writeItem(resultJson, resultItem);
                resultsJson.add("item", resultJson);
            }
            
            if (resultFluid != null && resultFluid.fluid != Fluids.EMPTY && resultFluid.amount > 0)
            {
                JsonObject resultJson = new JsonObject();
                RecipeSerializerUtil.writeFluid(resultJson, resultFluid);
                resultsJson.add("fluid", resultJson);
            }
            
            
            json.add("results", resultsJson);
        }
        
        private final ChemicalLaboratoryRecipe readFromBuffer(ResourceLocation recipeId, FriendlyByteBuf buffer)
        {
            String group = buffer.readUtf();
            ConsumableItemIngredient primaryIngredient = ConsumableItemIngredientSerializer.INSTANCE.parse(buffer);
            ConsumableItemIngredient secondaryIngredient = ConsumableItemIngredientSerializer.INSTANCE.parse(buffer);
            ConsumableItemIngredient tankIngredient = ConsumableItemIngredientSerializer.INSTANCE.parse(buffer);
            FluidIngredient fluidIngredient = FluidIngredient.fromNetwork(buffer);
            int processingTime = buffer.readInt();
            int energyDrainPerTick = buffer.readInt();
            ItemStack itemResult = RecipeSerializerUtil.readItem(buffer);
            FluidStack fluidResult = RecipeSerializerUtil.readFluid(buffer);
            
            return new ChemicalLaboratoryRecipe(
                    recipeId,
                    group,
                    primaryIngredient,
                    secondaryIngredient,
                    tankIngredient,
                    fluidIngredient,
                    itemResult,
                    fluidResult,
                    processingTime,
                    energyDrainPerTick);
        }
        
        private final void writeToBuffer(FriendlyByteBuf buffer, ChemicalLaboratoryRecipe recipe)
        {
            buffer.writeUtf(recipe.getGroup());
            recipe.getPrimaryIngredient().toNetwork(buffer);
            recipe.getSecondaryIngredient().toNetwork(buffer);
            recipe.getTankIngredient().toNetwork(buffer);
            recipe.getFluidIngredient().toNetwork(buffer);
            buffer.writeInt(recipe.getProcessingTime());
            buffer.writeInt(recipe.getEnergyDrainPerTick());
            RecipeSerializerUtil.writeItem(buffer, recipe.getResultItem(null));
            RecipeSerializerUtil.writeFluid(buffer, recipe.getResultFluid(null));
        }

        @Override
        public final ChemicalLaboratoryRecipe fromJson(ResourceLocation recipeId, JsonObject json)
        {
            return this.readJson(recipeId, json);
        }

        @Override
        @Nullable
        public final ChemicalLaboratoryRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
        {
            return this.readFromBuffer(recipeId, buffer);
        }

        @Override
        public final void toNetwork(FriendlyByteBuf buffer, ChemicalLaboratoryRecipe recipe)
        {
            this.writeToBuffer(buffer, recipe);
        }
    }
    
    public static class Builder implements RecipeBuilder
    {
        private final List<ConsumableItemIngredient.Value> _primaryInput;
        private final List<ConsumableItemIngredient.Value> _secondaryInput;
        private final List<ConsumableItemIngredient.Value> _tankInput;
        private final List<FluidIngredient.Value> _fluidInput;
        private final Advancement.Builder _advancement;
        @Nullable
        private String _advancementIdPrefix;
        @Nullable
        private String _group;
        private int _energyDrainPerTick;
        private int _processingTime;
        @Nullable
        private RecipeSerializerUtil.FluidStackResult _resultFluid;
        @Nullable
        private RecipeSerializerUtil.ItemStackResult _resultItem;
        
        public Builder()
        {
            this._primaryInput = new ArrayList<ConsumableItemIngredient.Value>();
            this._secondaryInput = new ArrayList<ConsumableItemIngredient.Value>();
            this._tankInput = new ArrayList<ConsumableItemIngredient.Value>();
            this._fluidInput = new ArrayList<FluidIngredient.Value>();
            this._advancement = Advancement.Builder.recipeAdvancement();
            this._group = null;
            this._energyDrainPerTick = 0;
            this._processingTime = 100;
            this._resultFluid = null;
            this._resultItem = null;
        }
    
        public final Builder addPrimaryInput(ItemLike item)
        {
            this._primaryInput.add(new ConsumableItemIngredient.ItemValue(new ItemStack(item, 1)));
            return this;
        }
        public final Builder addPrimaryInput(ItemLike item, int count)
        {
            this._primaryInput.add(new ConsumableItemIngredient.ItemValue(new ItemStack(item, Math.max(count, 1))));
            return this;
        }
        public final Builder addPrimaryInput(RegistryObject<? extends ItemLike> object)
        {
            this._primaryInput.add(new ConsumableItemIngredient.ItemValue(new ItemStack(object.get(), 1)));
            return this;
        }
        public final Builder addPrimaryInput(RegistryObject<? extends ItemLike> object, int count)
        {
            this._primaryInput.add(new ConsumableItemIngredient.ItemValue(new ItemStack(object.get(), Math.max(count, 1))));
            return this;
        }
        public final Builder addPrimaryInput(TagKey<Item> itemTag)
        {
            this._primaryInput.add(new ConsumableItemIngredient.TagValue(itemTag, 1));
            return this;
        }
        public final Builder addPrimaryInput(TagKey<Item> itemTag, int count)
        {
            this._primaryInput.add(new ConsumableItemIngredient.TagValue(itemTag, Math.max(count, 1)));
            return this;
        }
        public final Builder addSecondaryInput(ItemLike item)
        {
            this._secondaryInput.add(new ConsumableItemIngredient.ItemValue(new ItemStack(item, 1)));
            return this;
        }
        public final Builder addSecondaryInput(ItemLike item, int count)
        {
            this._secondaryInput.add(new ConsumableItemIngredient.ItemValue(new ItemStack(item, Math.max(count, 1))));
            return this;
        }
        public final Builder addSecondaryInput(RegistryObject<? extends ItemLike> object)
        {
            this._secondaryInput.add(new ConsumableItemIngredient.ItemValue(new ItemStack(object.get(), 1)));
            return this;
        }
        public final Builder addSecondaryInput(RegistryObject<? extends ItemLike> object, int count)
        {
            this._secondaryInput.add(new ConsumableItemIngredient.ItemValue(new ItemStack(object.get(), Math.max(count, 1))));
            return this;
        }
        public final Builder addSecondaryInput(TagKey<Item> itemTag)
        {
            this._secondaryInput.add(new ConsumableItemIngredient.TagValue(itemTag, 1));
            return this;
        }
        public final Builder addSecondaryInput(TagKey<Item> itemTag, int count)
        {
            this._secondaryInput.add(new ConsumableItemIngredient.TagValue(itemTag, Math.max(count, 1)));
            return this;
        }
        public final Builder addTankInput(ItemLike item)
        {
            this._tankInput.add(new ConsumableItemIngredient.ItemValue(new ItemStack(item, 1)));
            return this;
        }
        public final Builder addTankInput(ItemLike item, int count)
        {
            this._tankInput.add(new ConsumableItemIngredient.ItemValue(new ItemStack(item, Math.max(count, 1))));
            return this;
        }
        public final Builder addTankInput(RegistryObject<? extends ItemLike> object)
        {
            this._tankInput.add(new ConsumableItemIngredient.ItemValue(new ItemStack(object.get(), 1)));
            return this;
        }
        public final Builder addTankInput(RegistryObject<? extends ItemLike> object, int count)
        {
            this._tankInput.add(new ConsumableItemIngredient.ItemValue(new ItemStack(object.get(), Math.max(count, 1))));
            return this;
        }
        public final Builder addTankInput(TagKey<Item> itemTag)
        {
            this._tankInput.add(new ConsumableItemIngredient.TagValue(itemTag, 1));
            return this;
        }
        public final Builder addTankInput(TagKey<Item> itemTag, int count)
        {
            this._tankInput.add(new ConsumableItemIngredient.TagValue(itemTag, Math.max(count, 1)));
            return this;
        }
        public final Builder addFluidInput(Fluid fluid, int amount)
        {
            this._fluidInput.add(new FluidIngredient.FluidValue(new FluidStack(
                    fluid,
                    Math.max(amount, 1))));
            return this;
        }
        public final Builder addFluidInput(RegistryObject<? extends Fluid> object, int amount)
        {
            this._fluidInput.add(new FluidIngredient.FluidValue(new FluidStack(
                    object.get(),
                    Math.max(amount, 1))));
            return this;
        }
        public final Builder addFluidInput(TagKey<Fluid> fluidTag, int amount)
        {
            this._fluidInput.add(new FluidIngredient.TagValue(fluidTag, Math.max(amount, 1)));
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
        public final Builder withItemResult(ItemLike result)
        {
            this._resultItem = new RecipeSerializerUtil.ItemStackResult(result.asItem());
            return this;
        }
        public final Builder withItemResult(ItemLike result, @Nullable CompoundTag nbt)
        {
            this._resultItem = new RecipeSerializerUtil.ItemStackResult(result.asItem(), nbt);
            return this;
        }
        public final Builder withItemResult(ItemLike result, int count)
        {
            this._resultItem = new RecipeSerializerUtil.ItemStackResult(result.asItem(), count);
            return this;
        }
        public final Builder withItemResult(ItemLike result, int count, @Nullable CompoundTag nbt)
        {
            this._resultItem = new RecipeSerializerUtil.ItemStackResult(result.asItem(), count, nbt);
            return this;
        }
        public final Builder withItemResult(RegistryObject<? extends ItemLike> resultObject)
        {
            this._resultItem = new RecipeSerializerUtil.ItemStackResult(resultObject.get().asItem());
            return this;
        }
        public final Builder withItemResult(RegistryObject<? extends ItemLike> resultObject, @Nullable CompoundTag nbt)
        {
            this._resultItem = new RecipeSerializerUtil.ItemStackResult(resultObject.get().asItem(), nbt);
            return this;
        }
        public final Builder withItemResult(RegistryObject<? extends ItemLike> resultObject, int count)
        {
            this._resultItem = new RecipeSerializerUtil.ItemStackResult(resultObject.get().asItem(), count);
            return this;
        }
        public final Builder withItemResult(RegistryObject<? extends ItemLike> resultObject, int count, @Nullable CompoundTag nbt)
        {
            this._resultItem = new RecipeSerializerUtil.ItemStackResult(resultObject.get().asItem(), count, nbt);
            return this;
        }
        public final Builder withFluidResult(Fluid result, int amount)
        {
            if (result instanceof FlowingFluid flowingFluid)
                result = flowingFluid.getSource();
            
            this._resultFluid = new RecipeSerializerUtil.FluidStackResult(result, amount);
            return this;
        }
        public final Builder withFluidResult(Fluid result, int amount, @Nullable CompoundTag nbt)
        {
            if (result instanceof FlowingFluid flowingFluid)
                result = flowingFluid.getSource();
            
            this._resultFluid = new RecipeSerializerUtil.FluidStackResult(result, amount, nbt);
            return this;
        }
        public final Builder withFluidResult(RegistryObject<Fluid> resultObject, int amount)
        {
            Fluid result = resultObject.get();
            
            if (result instanceof FlowingFluid flowingFluid)
                result = flowingFluid.getSource();

            this._resultFluid = new RecipeSerializerUtil.FluidStackResult(result, amount);
            return this;
        }
        public final Builder withFluidResult(RegistryObject<Fluid> resultObject, int amount, @Nullable CompoundTag nbt)
        {
            Fluid result = resultObject.get();
            
            if (result instanceof FlowingFluid flowingFluid)
                result = flowingFluid.getSource();

            this._resultFluid = new RecipeSerializerUtil.FluidStackResult(result, amount, nbt);
            return this;
        }
        
        @Override
        public final Item getResult()
        {
            return this._resultItem == null ?
                    Items.AIR :
                    this._resultItem.item;
        }
        
        protected final void validate(ResourceLocation id)
        {
            if (this._primaryInput.size() == 0 && this._tankInput.size() == 0)
                throw new IllegalStateException("No primary ingredient(s) have been specified");
            
            if (this._advancement.getCriteria().size() == 0)
                throw new IllegalStateException("No way of obtaining recipe " + id);
            
            if (this._resultItem == null && this._resultFluid == null)
                throw new IllegalStateException("Result Item/Fluid hasn't been specified");
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
        
        public static final class Result implements FinishedRecipe
        {
            private final ResourceLocation id;
            private final String group;
            private final ConsumableItemIngredient primary;
            private final ConsumableItemIngredient secondary;
            private final ConsumableItemIngredient tank;
            private final FluidIngredient fluid;
            private final int processingTime;
            private final int energyDrainPerTick;
            @Nullable
            private final Advancement.Builder advancement;
            @Nullable
            private final ResourceLocation advancementId;
            @Nullable
            private final RecipeSerializerUtil.ItemStackResult resultItem;
            @Nullable
            private final RecipeSerializerUtil.FluidStackResult resultFluid;
            
            private Result(Builder builder, ResourceLocation id)
            {
                this.id = id;
                this.group = builder._group == null ? "" : builder._group;
                this.primary = new ConsumableItemIngredient(builder._primaryInput.stream());
                this.secondary = new ConsumableItemIngredient(builder._secondaryInput.stream());
                this.tank = new ConsumableItemIngredient(builder._tankInput.stream());
                this.fluid = FluidIngredient.fromValues(builder._fluidInput.stream());
                this.processingTime = builder._processingTime;
                this.energyDrainPerTick = builder._energyDrainPerTick;
                
                this.advancement = builder._advancement;
                this.advancement.parent(ROOT_RECIPE_ADVANCEMENT).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id)).rewards(AdvancementRewards.Builder.recipe(id)).requirements(RequirementsStrategy.OR);
                    
                String prefix;
                if (builder._advancementIdPrefix == null || builder._advancementIdPrefix.isBlank())
                {
                    prefix = "recipes/misc/chemical_laboratory/";
                }
                else
                {
                    prefix = builder._advancementIdPrefix.trim();
                    if (prefix.charAt(prefix.length() - 1) != '/')
                        prefix += '/';
                    
                    prefix = "recipes/" + prefix;
                }
                
                this.advancementId = id.withPrefix(prefix);
                
                this.resultItem = builder._resultItem;
                this.resultFluid = builder._resultFluid;
            }
            
            public Result(
                    ResourceLocation id,
                    @Nullable
                    String group,
                    ConsumableItemIngredient primary,
                    ConsumableItemIngredient secondary,
                    ConsumableItemIngredient tank,
                    FluidIngredient fluid,
                    int processingTime,
                    int energyDrainPerTick,
                    Advancement.Builder advancement,
                    @Nullable
                    RecipeSerializerUtil.ItemStackResult resultItem,
                    @Nullable
                    RecipeSerializerUtil.FluidStackResult resultFluid)
            {
                this(
                        id,
                        group,
                        primary,
                        secondary,
                        tank,
                        fluid,
                        processingTime,
                        energyDrainPerTick,
                        advancement,
                        id.withPrefix("recipes/misc/chemical_laboratory/"),
                        resultItem,
                        resultFluid);
            }
            
            public Result(
                    ResourceLocation id,
                    @Nullable
                    String group,
                    ConsumableItemIngredient primary,
                    ConsumableItemIngredient secondary,
                    ConsumableItemIngredient tank,
                    FluidIngredient fluid,
                    int processingTime,
                    int energyDrainPerTick,
                    Advancement.Builder advancement,
                    ResourceLocation advancementId,
                    @Nullable
                    RecipeSerializerUtil.ItemStackResult resultItem,
                    @Nullable
                    RecipeSerializerUtil.FluidStackResult resultFluid)
            {
                this.id = id;
                this.group = group == null ? "" : group;
                this.primary = primary;
                this.secondary = secondary;
                this.tank = tank;
                this.fluid = fluid;
                this.processingTime = processingTime;
                this.energyDrainPerTick = energyDrainPerTick;
                this.advancement = advancement;
                this.advancementId = advancementId;
                this.resultItem = resultItem;
                this.resultFluid = resultFluid;
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
                return TGRecipeSerializers.CHEMICAL_LABORATORY.get();
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
