package techguns2.machine.reaction_chamber;

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
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.RegistryManager;
import net.minecraftforge.registries.RegistryObject;
import techguns2.TGCustomRegistries;
import techguns2.TGRecipeSerializers;
import techguns2.fluid.FluidIngredient;
import techguns2.util.RecipeSerializerUtil;

public final class ReactionChamberRecipe implements IReactionChamberRecipe
{
    private final ResourceLocation _id;
    private final String _group;
    private final LaserFocus _laserFocus;
    private final Ingredient _inputItem;
    private final FluidIngredient _inputFluid;
    private final int _fluidLevel;
    private final List<ItemStack> _outputs;

    private final float _instability;
    private final int _startingIntensity;
    private final int _intensityMargin;
    private final ReactionRisk _risk;
    private final int _ticksPerCycle;
    private final int _requiredCycles;
    private final int _maximumCycles;
    private final int _energyDrainPerTick;
    
    public ReactionChamberRecipe(
            ResourceLocation id,
            String group,
            LaserFocus laserFocus,
            Ingredient inputItem,
            FluidIngredient inputFluid,
            int fluidLevel,
            List<ItemStack> outputs,
            float instability,
            int startingIntensity,
            int intensityMargin,
            ReactionRisk risk,
            int ticksPerCycle,
            int requiredCycles,
            int maximumCycles,
            int energyDrainPerTick)
    {
        this._id = id;
        this._group = group;
        this._laserFocus = laserFocus;
        this._inputItem = inputItem;
        this._inputFluid = inputFluid;
        this._fluidLevel = fluidLevel;
        this._outputs = outputs;
        this._instability = Math.min(1, Math.max(0, instability));
        this._startingIntensity = Math.min(10, Math.max(0, startingIntensity));
        this._intensityMargin = Math.min(10, Math.max(0, intensityMargin));
        this._risk = risk;
        this._ticksPerCycle = Math.max(1, ticksPerCycle);
        this._requiredCycles = Math.min(255, Math.max(0,requiredCycles));
        this._maximumCycles = Math.min(255, Math.max(0, maximumCycles));
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

    @Override
    public final LaserFocus getLaserFocus()
    {
        return this._laserFocus;
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
    
    public final FluidIngredient getInputFluidIngredient()
    {
        return this._inputFluid;
    }

    @Override
    public final int testInputFluidWithConsumption(FluidStack fluidStack)
    {
        return this._inputFluid.testWithConsumption(fluidStack);
    }
    
    @Override
    public final int getFluidLevel()
    {
        return this._fluidLevel;
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
    public final float getInstability()
    {
        return this._instability;
    }

    @Override
    public final int getStartingIntensity()
    {
        return this._startingIntensity;
    }

    @Override
    public final int getIntensityMargin()
    {
        return this._intensityMargin;
    }

    @Override
    public final ReactionRisk getRisk()
    {
        return this._risk;
    }

    @Override
    public final int getTicksPerCycle()
    {
        return this._ticksPerCycle;
    }

    @Override
    public final int getRequiredCycles()
    {
        return this._requiredCycles;
    }

    @Override
    public final int getMaximumCycles()
    {
        return this._maximumCycles;
    }
    
    @Override
    public final int getEnergyDrainPerTick()
    {
        return this._energyDrainPerTick;
    }

    @Override
    public final boolean matches(ReactionChamberControllerBlockEntity blockEntity, Level level)
    {
        return blockEntity.getIntensity() == this._startingIntensity &&
                this._laserFocus.is(blockEntity.getFocusItem().getItem()) &&
                this._inputItem.test(blockEntity.getInputItem()) &&
                this._fluidLevel == blockEntity.getFluidLevel() &&
                (this._fluidLevel * 1000) == blockEntity.getInputFluid().getAmount() &&
                this._inputFluid.test(blockEntity.getInputFluid());
    }
    
    @Override
    public final List<ItemStack> assembleAll(ReactionChamberControllerBlockEntity blockEntity, RegistryAccess access)
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
        return TGRecipeSerializers.REACTION_CHAMBER.get();
    }

    @Override
    public final NonNullList<Ingredient> getIngredients()
    {
        return NonNullList.of(this._inputItem);
    }
    
    public final NonNullList<FluidIngredient> getFluidIngredients()
    {
        return NonNullList.of(this._inputFluid);
    }
    
    public static final class Serializer implements RecipeSerializer<ReactionChamberRecipe>
    {
        protected final ReactionChamberRecipe readJson(ResourceLocation recipeId, JsonObject json)
        {
            String group = GsonHelper.getAsString(json, "group", "");
            String laserFocusId = GsonHelper.getAsString(json, "laserFocus");
            LaserFocus laserFocus = RegistryManager.ACTIVE.getRegistry(TGCustomRegistries.LASER_FOCUSES).getValue(new ResourceLocation(laserFocusId));
            if (laserFocus == null)
                laserFocus = LaserFocuses.HEAT_RAY;
            
            Ingredient inputItem = Ingredient.fromJson(json.get("inputItem"));
            FluidIngredient inputFluid = FluidIngredient.fromJson(json.get("inputFluid"));
            int fluidLevel = GsonHelper.getAsInt(json, "fluidLevel");
            
            float instability = GsonHelper.getAsFloat(json, "instability", 0f);
            int startingIntensity = GsonHelper.getAsInt(json, "startingIntensity");
            int intensityMargin = GsonHelper.getAsInt(json, "intensityMargin");
            String riskId = GsonHelper.getAsString(json, "risk");
            ReactionRisk risk = RegistryManager.ACTIVE.getRegistry(TGCustomRegistries.REACTION_RISKS).getValue(new ResourceLocation(riskId));
            if (risk == null)
                risk = ReactionRisks.NONE;
            
            int ticksPerCycle = GsonHelper.getAsInt(json, "ticksPerCycle");
            int requiredCycles = GsonHelper.getAsInt(json, "requiredCycles");
            int maximumCycles = GsonHelper.getAsInt(json, "maximumCycles");
            int energyDrainPerTick = GsonHelper.getAsInt(json, "energyDrainPerTick");
            
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
            
            return new ReactionChamberRecipe(
                    recipeId,
                    group,
                    laserFocus,
                    inputItem,
                    inputFluid,
                    fluidLevel,
                    resultItems,
                    instability,
                    startingIntensity,
                    intensityMargin,
                    risk,
                    ticksPerCycle,
                    requiredCycles,
                    maximumCycles,
                    energyDrainPerTick);
        }
        
        protected final void writeJson(JsonObject json, ReactionChamberRecipe recipe)
        {
            json.addProperty("group", recipe.getGroup());
            json.addProperty("laserFocus", recipe.getLaserFocus().getId().toString());
            json.add("inputItem", recipe.getInputItemIngredient().toJson());
            json.add("inputFluid", recipe.getInputFluidIngredient().toJson());
            json.addProperty("fluidLevel", recipe.getFluidLevel());
            json.addProperty("instability", recipe.getInstability());
            json.addProperty("startingIntensity", recipe.getStartingIntensity());
            json.addProperty("intensityMargin", recipe.getIntensityMargin());
            json.addProperty("risk", recipe.getRisk().getId().toString());
            json.addProperty("ticksPerCycle", recipe.getTicksPerCycle());
            json.addProperty("requiredCycles", recipe.getRequiredCycles());
            json.addProperty("maximumCycles", recipe.getMaximumCycles());
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
        
        protected final void writeJson(JsonObject json, ReactionChamberRecipe.Builder.Result recipe)
        {
            json.addProperty("group", recipe.group);
            json.addProperty("laserFocus", recipe.laserFocus.getId().toString());
            json.add("inputItem", recipe.inputItem.toJson());
            json.add("inputFluid", recipe.inputFluid.toJson());
            json.addProperty("fluidLevel", recipe.fluidLevel);
            json.addProperty("instability", recipe.instability);
            json.addProperty("startingIntensity", recipe.startingIntensity);
            json.addProperty("intensityMargin", recipe.intensityMargin);
            json.addProperty("risk", recipe.risk.getId().toString());
            json.addProperty("ticksPerCycle", recipe.ticksPerCycle);
            json.addProperty("requiredCycles", recipe.requiredCycles);
            json.addProperty("maximumCycles", recipe.maximumCycles);
            json.addProperty("energyDrainPerTick", recipe.energyDrainPerTick);
            
            List<RecipeSerializerUtil.ItemStackResult> resultItems = recipe.resultItems;
            if (resultItems.size() == 1)
            {
                RecipeSerializerUtil.ItemStackResult resultItem = resultItems.get(0);
                JsonObject resultJson = new JsonObject();
                RecipeSerializerUtil.writeItem(resultJson, resultItem);
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
        
        private final ReactionChamberRecipe readFromBuffer(ResourceLocation recipeId, FriendlyByteBuf buffer)
        {
            String group = buffer.readUtf();
            ResourceLocation laserFocusId = buffer.readResourceLocation();
            LaserFocus laserFocus = RegistryManager.ACTIVE.getRegistry(TGCustomRegistries.LASER_FOCUSES).getValue(laserFocusId);
            if (laserFocus == null)
                laserFocus = LaserFocuses.HEAT_RAY;
            
            Ingredient inputItem = Ingredient.fromNetwork(buffer);
            FluidIngredient inputFluid = FluidIngredient.fromNetwork(buffer);
            int fluidLevel = buffer.readByte();
            
            ResourceLocation riskId = buffer.readResourceLocation();
            ReactionRisk risk = RegistryManager.ACTIVE.getRegistry(TGCustomRegistries.REACTION_RISKS).getValue(riskId);
            if (risk == null)
                risk = ReactionRisks.NONE;
            
            float instability = buffer.readFloat();
            int startingIntensity = buffer.readByte();
            int intensityMargin = buffer.readByte();
            int requiredCycles = buffer.readByte();
            int maximumCycles = buffer.readByte();
            int ticksPerCycle = buffer.readInt();
            int energyDrainPerTick = buffer.readInt();
            
            int resultCount = Math.min(buffer.readByte(), 4);
            List<ItemStack> resultItems = Stream.generate(() -> RecipeSerializerUtil.readItem(buffer)).limit(resultCount).toList();
            
            return new ReactionChamberRecipe(
                    recipeId,
                    group,
                    laserFocus,
                    inputItem,
                    inputFluid,
                    fluidLevel,
                    resultItems,
                    instability,
                    startingIntensity,
                    intensityMargin,
                    risk,
                    ticksPerCycle,
                    requiredCycles,
                    maximumCycles,
                    energyDrainPerTick);
        }
        
        private final void writeToBuffer(FriendlyByteBuf buffer, ReactionChamberRecipe recipe)
        {
            buffer.writeUtf(recipe.getGroup());
            buffer.writeResourceLocation(recipe.getLaserFocus().getId());
            recipe.getInputItemIngredient().toNetwork(buffer);
            recipe.getInputFluidIngredient().toNetwork(buffer);
            buffer.writeByte(Math.min(10, Math.max(0, recipe.getFluidLevel())));
            buffer.writeResourceLocation(recipe.getRisk().getId());
            buffer.writeFloat(Math.min(1, Math.max(0, recipe.getInstability())));
            buffer.writeByte(Math.min(10, Math.max(0, recipe.getStartingIntensity())));
            buffer.writeByte(Math.min(10, Math.max(0, recipe.getIntensityMargin())));
            buffer.writeByte(Math.min(255, Math.max(0, recipe.getRequiredCycles())));
            buffer.writeByte(Math.min(255, Math.max(0, recipe.getMaximumCycles())));
            buffer.writeInt(Math.max(1, recipe.getTicksPerCycle()));
            buffer.writeInt(Math.max(0, recipe.getEnergyDrainPerTick()));
            int resultItemCount = Math.min(recipe.getOutputItems().size(), 4);
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
        public final ReactionChamberRecipe fromJson(ResourceLocation recipeId, JsonObject json)
        {
            return this.readJson(recipeId, json);
        }

        @Override
        @Nullable
        public final ReactionChamberRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
        {
            return this.readFromBuffer(recipeId, buffer);
        }

        @Override
        public final void toNetwork(FriendlyByteBuf buffer, ReactionChamberRecipe recipe)
        {
            this.writeToBuffer(buffer, recipe);
        }
    }
    
    public static final class Builder implements RecipeBuilder
    {
        private LaserFocus _laserFocus;
        private final List<Ingredient.Value> _inputItem;
        private final List<FluidIngredient.Value> _inputFluid;
        private int _fluidLevel;
        private final List<RecipeSerializerUtil.ItemStackResult> _resultItems;
        private final Advancement.Builder _advancement;
        @Nullable
        private String _advancementIdPrefix;
        @Nullable
        private String _group;
        private float _instability;
        private int _startingIntensity;
        private int _intensityMargin;
        private ReactionRisk _risk;
        private int _ticksPerCycle;
        private int _requiredCycles;
        private int _maximumCycles;
        private int _energyDrainPerTick;
        
        public Builder()
        {
            this._inputItem = new ArrayList<Ingredient.Value>();
            this._inputFluid = new ArrayList<FluidIngredient.Value>();
            this._fluidLevel = 0;
            this._advancement = Advancement.Builder.recipeAdvancement();
            this._group = null;
            this._resultItems = new ArrayList<RecipeSerializerUtil.ItemStackResult>();
            this._instability = 0f;
            this._startingIntensity = -1;
            this._intensityMargin = -1;
            this._risk = ReactionRisks.NONE;
            this._ticksPerCycle = 60;
            this._requiredCycles = -1;
            this._maximumCycles = -1;
            this._energyDrainPerTick = 0;
        }

        public final Builder withLaserFocus(LaserFocus laserFocus)
        {
            if (laserFocus == null)
                laserFocus = LaserFocuses.HEAT_RAY;
            this._laserFocus = laserFocus;
            return this;
        }
        public final Builder addInputItem(ItemLike item)
        {
            this._inputItem.add(new Ingredient.ItemValue(new ItemStack(item)));
            return this;
        }
        public final Builder addInputItem(RegistryObject<? extends ItemLike> object)
        {
            this._inputItem.add(new Ingredient.ItemValue(new ItemStack(object.get())));
            return this;
        }
        public final Builder addInputItem(TagKey<Item> itemTag)
        {
            this._inputItem.add(new Ingredient.TagValue(itemTag));
            return this;
        }
        public final Builder addInputFluid(Fluid fluid, int amount)
        {
            if (fluid instanceof FlowingFluid flowingFluid)
                fluid = flowingFluid.getSource();
            
            this._inputFluid.add(new FluidIngredient.FluidValue(new FluidStack(fluid, amount)));
            return this;
        }
        public final Builder addInputFluid(RegistryObject<? extends Fluid> object, int amount)
        {
            Fluid fluid = object.get();
            if (fluid instanceof FlowingFluid flowingFluid)
                fluid = flowingFluid.getSource();
            
            this._inputFluid.add(new FluidIngredient.FluidValue(new FluidStack(fluid, amount)));
            return this;
        }
        public final Builder addInputFluid(TagKey<Fluid> fluidTag, int amount)
        {
            this._inputFluid.add(new FluidIngredient.TagValue(fluidTag, amount));
            return this;
        }
        public final Builder withFluidLevel(int level)
        {
            this._fluidLevel = Math.min(Math.max(level, 10), 0);
            return this;
        }
        public final Builder withoutInstability()
        {
            return this.withInstability(0f)
                    .withIntensityMargin(0);
        }
        public final Builder withInstability(float instability, int intensityMargin)
        {
            return this.withInstability(instability)
                    .withIntensityMargin(intensityMargin);
        }
        public final Builder withInstability(float instability)
        {
            this._instability = Math.min(Math.max(instability, 0), 1);
            return this;
        }
        public final Builder withStartingIntensity(int startingIntensity)
        {
            this._startingIntensity = Math.min(Math.max(startingIntensity, 0), 10);
            return this;
        }
        public final Builder withIntensityMargin(int intensityMargin)
        {
            this._intensityMargin = Math.min(Math.max(intensityMargin, 0), 10);
            return this;
        }
        public final Builder withRisk(ReactionRisk risk)
        {
            if (risk == null)
                risk = ReactionRisks.NONE;
            
            this._risk = risk;
            return this;
        }
        public final Builder withTicksPerCycle(int ticksPerCycle)
        {
            this._ticksPerCycle = Math.max(ticksPerCycle, 0);
            return this;
        }
        public final Builder withCycles(int amount, int required)
        {
            return this.withRequiredCycles(required)
                    .withMaximumCycles(amount);
        }
        public final Builder withRequiredCycles(int amount)
        {
            this._requiredCycles = Math.max(Math.min(amount, 255), 1);
            return this;
        }
        public final Builder withMaximumCycles(int amount)
        {
            this._maximumCycles = Math.max(Math.min(amount, 255), 1);
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
        public final Builder withResult(ItemLike result)
        {
            this._resultItems.add(new RecipeSerializerUtil.ItemStackResult(result.asItem()));
            return this;
        }
        public final Builder withResult(ItemLike result, @Nullable CompoundTag nbt)
        {
            this._resultItems.add(new RecipeSerializerUtil.ItemStackResult(result.asItem(), nbt));
            return this;
        }
        public final Builder withResult(ItemLike result, int count)
        {
            this._resultItems.add(new RecipeSerializerUtil.ItemStackResult(result.asItem(), count));
            return this;
        }
        public final Builder withResult(ItemLike result, int count, @Nullable CompoundTag nbt)
        {
            this._resultItems.add(new RecipeSerializerUtil.ItemStackResult(result.asItem(), count, nbt));
            return this;
        }
        public final Builder withResult(RegistryObject<? extends ItemLike> resultObject)
        {
            this._resultItems.add(new RecipeSerializerUtil.ItemStackResult(resultObject.get().asItem()));
            return this;
        }
        public final Builder withResult(RegistryObject<? extends ItemLike> resultObject, @Nullable CompoundTag nbt)
        {
            this._resultItems.add(new RecipeSerializerUtil.ItemStackResult(resultObject.get().asItem(), nbt));
            return this;
        }
        public final Builder withResult(RegistryObject<? extends ItemLike> resultObject, int count)
        {
            this._resultItems.add(new RecipeSerializerUtil.ItemStackResult(resultObject.get().asItem(), count));
            return this;
        }
        public final Builder withResult(RegistryObject<? extends ItemLike> resultObject, int count, @Nullable CompoundTag nbt)
        {
            this._resultItems.add(new RecipeSerializerUtil.ItemStackResult(resultObject.get().asItem(), count, nbt));
            return this;
        }
        
        @Override
        public final Item getResult()
        {
            if (this._resultItems.size() == 0)
                return Items.AIR;
            else
                return this._resultItems.get(0).item;
        }
        
        protected final void validate(ResourceLocation id)
        {
            if (this._inputItem.size() == 0)
                throw new IllegalStateException("No item ingredient(s) have been specified");
            if (this._inputFluid.size() == 0)
                throw new IllegalStateException("No fluid ingredient(s) have been specified");
            
            if (this._advancement.getCriteria().size() == 0)
                throw new IllegalStateException("No way of obtaining recipe " + id);
            
            if (this._resultItems.size() == 0)
                throw new IllegalStateException("Result Item(s) haven't been specified");
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
            private final LaserFocus laserFocus;
            private final Ingredient inputItem;
            private final FluidIngredient inputFluid;
            private final int fluidLevel;
            private final int startingIntensity;
            private final float instability;
            private final int intensityMargin;
            private final ReactionRisk risk;
            private final int ticksPerCycle;
            private final int requiredCycles;
            private final int maximumCycles;
            private final int energyDrainPerTick;
            @Nullable
            private final Advancement.Builder advancement;
            @Nullable
            private final ResourceLocation advancementId;
            private final List<RecipeSerializerUtil.ItemStackResult> resultItems;
            
            private Result(Builder builder, ResourceLocation id)
            {
                this.id = id;
                this.group = builder._group == null ? "" : builder._group;
                this.laserFocus = builder._laserFocus;
                this.inputItem = Ingredient.fromValues(builder._inputItem.stream());
                this.inputFluid = FluidIngredient.fromValues(builder._inputFluid.stream());
                this.fluidLevel = builder._fluidLevel;
                this.startingIntensity = builder._startingIntensity;
                this.instability = builder._instability;
                this.intensityMargin = builder._intensityMargin;
                this.risk = builder._risk;
                this.ticksPerCycle = builder._ticksPerCycle;
                this.requiredCycles = builder._requiredCycles;
                this.maximumCycles = builder._maximumCycles;
                this.energyDrainPerTick = builder._energyDrainPerTick;
                
                this.advancement = builder._advancement;
                this.advancement.parent(ROOT_RECIPE_ADVANCEMENT).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id)).rewards(AdvancementRewards.Builder.recipe(id)).requirements(RequirementsStrategy.OR);
                    
                String prefix;
                if (builder._advancementIdPrefix == null || builder._advancementIdPrefix.isBlank())
                {
                    prefix = "recipes/misc/reaction_chamber/";
                }
                else
                {
                    prefix = builder._advancementIdPrefix.trim();
                    if (prefix.charAt(prefix.length() - 1) != '/')
                        prefix += '/';
                    
                    prefix = "recipes/" + prefix;
                }
                
                this.advancementId = id.withPrefix(prefix);
                
                this.resultItems = ImmutableList.copyOf(builder._resultItems);
            }
            
            public Result(
                    ResourceLocation id,
                    String group,
                    LaserFocus laserFocus,
                    Ingredient inputItem,
                    FluidIngredient inputFluid,
                    int fluidLevel,
                    float instability,
                    int startingIntensity,
                    int intensityMargin,
                    ReactionRisk risk,
                    int ticksPerCycle,
                    int requiredCycles,
                    int maximumCycles,
                    int energyDrainPerTick,
                    Advancement.Builder advancement,
                    List<RecipeSerializerUtil.ItemStackResult> resultItems)
            {
                this(
                        id,
                        group,
                        laserFocus,
                        inputItem,
                        inputFluid,
                        fluidLevel,
                        instability,
                        startingIntensity,
                        intensityMargin,
                        risk,
                        ticksPerCycle,
                        requiredCycles,
                        maximumCycles,
                        energyDrainPerTick,
                        advancement,
                        id.withPrefix("recipes/misc/reaction_chamber/"),
                        resultItems);
            }
            
            public Result(
                    ResourceLocation id,
                    String group,
                    LaserFocus laserFocus,
                    Ingredient inputItem,
                    FluidIngredient inputFluid,
                    int fluidLevel,
                    float instability,
                    int startingIntensity,
                    int intensityMargin,
                    ReactionRisk risk,
                    int ticksPerCycle,
                    int requiredCycles,
                    int maximumCycles,
                    int energyDrainPerTick,
                    Advancement.Builder advancement,
                    ResourceLocation advancementId,
                    List<RecipeSerializerUtil.ItemStackResult> resultItems)
            {
                this.id = id;
                this.group = group == null ? "" : group;
                this.laserFocus = laserFocus;
                this.inputItem = inputItem;
                this.inputFluid = inputFluid;
                this.fluidLevel = fluidLevel;
                this.instability = instability;
                this.startingIntensity = startingIntensity;
                this.intensityMargin = intensityMargin;
                this.risk = risk;
                this.ticksPerCycle = ticksPerCycle;
                this.requiredCycles = requiredCycles;
                this.maximumCycles = maximumCycles;
                this.energyDrainPerTick = energyDrainPerTick;
                this.advancement = advancement;
                this.advancementId = advancementId;
                this.resultItems = ImmutableList.copyOf(resultItems);
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
                return TGRecipeSerializers.REACTION_CHAMBER.get();
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
