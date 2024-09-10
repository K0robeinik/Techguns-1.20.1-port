package techguns2.machine.reaction_chamber;

import java.util.List;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.RegistryManager;
import techguns2.TGCustomRegistries;
import techguns2.machine.TickedMachineOperation;

public final class ReactionChamberOperation extends TickedMachineOperation
{
    public final ItemStack inputItem;
    public final FluidStack inputFluid;
    public final List<ItemStack> resultItems;
    
    public final LaserFocus laserFocus;
    public final ReactionRisk risk;

    public int requiredIntensity;
    
    public final int maxCycleCount;
    public final int requiredCycleCount;
    public final int intensityMargin;
    
    public final float instability;
    
    public int currentCycle;
    public int successfulCycles;
    
    public ReactionChamberOperation(FriendlyByteBuf buf, boolean fromNetwork)
    {
        super(buf, fromNetwork);
        this.inputItem = buf.readItem();
        this.inputFluid = buf.readFluidStack();
        int listSize = Math.min(buf.readByte(), 4);
        this.resultItems = ImmutableList.copyOf(Stream.generate(() -> buf.readItem()).limit(listSize).iterator());
        LaserFocus laserFocus = RegistryManager.ACTIVE.getRegistry(TGCustomRegistries.LASER_FOCUSES).getValue(buf.readResourceLocation());
        if (laserFocus == null)
            laserFocus = LaserFocuses.HEAT_RAY;
        
        this.laserFocus = laserFocus;
        
        ReactionRisk risk = RegistryManager.ACTIVE.getRegistry(TGCustomRegistries.REACTION_RISKS).getValue(buf.readResourceLocation());
        if (risk == null)
            risk = ReactionRisks.NONE;
        
        this.risk = risk;
        this.requiredIntensity = buf.readByte();
        this.maxCycleCount = buf.readByte();
        this.requiredCycleCount = buf.readByte();
        this.intensityMargin = buf.readByte();
        this.instability = Math.max(0, Math.min(1, buf.readFloat()));
        this.currentCycle = buf.readByte();
        this.successfulCycles = buf.readInt();
    }
    
    public ReactionChamberOperation(CompoundTag tag, boolean fromNetwork)
    {
        super(tag, fromNetwork);
        
        if (tag.contains(TAG_INPUT_ITEM, Tag.TAG_COMPOUND))
            this.inputItem = ItemStack.of(tag.getCompound(TAG_INPUT_ITEM));
        else
            this.inputItem = ItemStack.EMPTY;
        
        if (tag.contains(TAG_INPUT_FLUID, Tag.TAG_COMPOUND))
            this.inputFluid = FluidStack.loadFluidStackFromNBT(tag.getCompound(TAG_INPUT_FLUID));
        else
            this.inputFluid = FluidStack.EMPTY;
        
        if (tag.contains(TAG_RESULT_ITEMS, Tag.TAG_LIST))
        {
            ListTag resultItemList = tag.getList(TAG_RESULT_ITEMS, Tag.TAG_COMPOUND);
            ImmutableList.Builder<ItemStack> resultItems = ImmutableList.builder();
            
            for (int index = 0, resultCount = Math.min(resultItemList.size(), 4); index < resultCount; index++)
            {
                resultItems.add(ItemStack.of(resultItemList.getCompound(index)));
            }
            
            this.resultItems = resultItems.build();
        }
        else
            this.resultItems = ImmutableList.of();
        
        if (tag.contains(TAG_FOCUS, Tag.TAG_STRING))
        {
            ResourceLocation laserFocusId = ResourceLocation.tryParse(tag.getString(TAG_FOCUS));
            LaserFocus laserFocus;
            if (laserFocusId != null)
                laserFocus = RegistryManager.ACTIVE.getRegistry(TGCustomRegistries.LASER_FOCUSES).getValue(laserFocusId);
            else
                laserFocus = null;
            
            if (laserFocus == null)
                laserFocus = LaserFocuses.HEAT_RAY;
            
            this.laserFocus = laserFocus;
        }
        else
            this.laserFocus = LaserFocuses.HEAT_RAY;
        
        if (tag.contains(TAG_RISK, Tag.TAG_STRING))
        {
            ResourceLocation riskId = ResourceLocation.tryParse(tag.getString(TAG_RISK));
            ReactionRisk risk;
            if (riskId != null)
                risk = RegistryManager.ACTIVE.getRegistry(TGCustomRegistries.REACTION_RISKS).getValue(riskId);
            else
                risk = null;
            
            if (risk == null)
                risk = ReactionRisks.NONE;
            
            this.risk = risk;
        }
        else
            this.risk = ReactionRisks.NONE;
        
        if (tag.contains(TAG_REQUIRED_INTENSITY, Tag.TAG_BYTE))
            this.requiredIntensity = tag.getByte(TAG_REQUIRED_INTENSITY);
        else
            this.requiredIntensity = 1;
        
        if (tag.contains(TAG_MAX_CYCLE_COUNT, Tag.TAG_BYTE))
            this.maxCycleCount = tag.getByte(TAG_MAX_CYCLE_COUNT);
        else
            this.maxCycleCount = 2;
        
        if (tag.contains(TAG_REQUIRED_CYCLE_COUNT, Tag.TAG_BYTE))
            this.requiredCycleCount = tag.getByte(TAG_REQUIRED_CYCLE_COUNT);
        else
            this.requiredCycleCount = 1;
        
        if (tag.contains(TAG_INTENSITY_MARGIN, Tag.TAG_BYTE))
            this.intensityMargin = tag.getByte(TAG_INTENSITY_MARGIN);
        else
            this.intensityMargin = 0;
        
        if (tag.contains(TAG_INSTABILITY, Tag.TAG_FLOAT))
            this.instability = Math.max(0, Math.min(1, tag.getFloat(TAG_INSTABILITY)));
        else
            this.instability = 0;
        
        if (tag.contains(TAG_CURRENT_CYCLE, Tag.TAG_BYTE))
            this.currentCycle = tag.getByte(TAG_CURRENT_CYCLE);
        else
            this.currentCycle = 0;
        
        if (tag.contains(TAG_SUCCESSFUL_CYCLES, Tag.TAG_BYTE))
            this.successfulCycles = tag.getByte(TAG_SUCCESSFUL_CYCLES);
        else
            this.successfulCycles = 0;
    }
    
    public ReactionChamberOperation(
            int totalTicksPerCycle,
            int ticksRemainingInCycle,
            int energyDrainPerTick,
            ItemStack inputItem,
            FluidStack inputFluid,
            List<ItemStack> resultItems,
            LaserFocus laserFocus,
            ReactionRisk risk,
            int requiredIntensity,
            int maxCycleCount,
            int requiredCycleCount,
            int intensityMargin,
            float instability,
            int currentCycle,
            int successfulCycles)
    {
        super(totalTicksPerCycle, ticksRemainingInCycle, energyDrainPerTick);
        this.inputItem = inputItem;
        this.inputFluid = inputFluid;
        this.resultItems = ImmutableList.copyOf(resultItems.stream().limit(4).iterator());
        this.laserFocus = laserFocus;
        this.risk = risk;
        this.requiredIntensity = requiredIntensity;
        this.maxCycleCount = maxCycleCount;
        this.requiredCycleCount = requiredCycleCount;
        this.intensityMargin = intensityMargin;
        this.instability = instability;
        this.currentCycle = currentCycle;
        this.successfulCycles = successfulCycles;
    }
    
    public ReactionChamberOperation(
            int totalTicksPerCycle,
            int ticksRemainingInCycle,
            int energyDrainPerTick,
            ItemStack inputItem,
            FluidStack inputFluid,
            List<ItemStack> resultItems,
            LaserFocus laserFocus,
            ReactionRisk risk,
            int requiredIntensity,
            int maxCycleCount,
            int requiredCycleCount,
            int intensityMargin,
            float instability,
            int currentCycle,
            int successfulCycles,
            boolean isPaused)
    {
        super(totalTicksPerCycle, ticksRemainingInCycle, energyDrainPerTick, isPaused);
        this.inputItem = inputItem;
        this.inputFluid = inputFluid;
        this.resultItems = ImmutableList.copyOf(resultItems.stream().limit(4).iterator());
        this.laserFocus = laserFocus;
        this.risk = risk;
        this.requiredIntensity = requiredIntensity;
        this.maxCycleCount = maxCycleCount;
        this.requiredCycleCount = requiredCycleCount;
        this.intensityMargin = intensityMargin;
        this.instability = instability;
        this.currentCycle = currentCycle;
        this.successfulCycles = successfulCycles;
    }
    
    public final boolean cycleComplete()
    {
        return super.isComplete();
    }
    
    @Override
    public final boolean isComplete()
    {
        return this.wasSuccessful() || this.wasUnsuccessful();
    }
    
    public final boolean wasSuccessful()
    {
        return this.successfulCycles >= this.requiredCycleCount;
    }
    
    public final boolean wasUnsuccessful()
    {
        return this.currentCycle >= this.requiredCycleCount;
    }
    
    @Override
    public final void dropContents(Level level, double x, double y, double z)
    {
        Containers.dropItemStack(level, x, y, z, this.inputItem);
    }

    @Override
    public final void serialize(CompoundTag tag, boolean toNetwork)
    {
        super.serialize(tag, toNetwork);
        CompoundTag inputItemTag = new CompoundTag();
        this.inputItem.save(inputItemTag);
        
        CompoundTag inputFluidTag = new CompoundTag();
        this.inputFluid.writeToNBT(inputFluidTag);
        
        tag.put(TAG_INPUT_ITEM, inputItemTag);
        tag.put(TAG_INPUT_FLUID, inputFluidTag);
        
        ListTag resultItemList = new ListTag();
        CompoundTag resultItemTag;
        int listSize = Math.min(this.resultItems.size(), 4);
        for (int index = 0; index < listSize; index++)
        {
            resultItemTag = new CompoundTag();
            this.resultItems.get(index).save(resultItemTag);
            resultItemList.add(resultItemTag);
        }
        
        tag.put(TAG_RESULT_ITEMS, resultItemList);
        tag.putString(TAG_FOCUS, RegistryManager.ACTIVE.getRegistry(TGCustomRegistries.LASER_FOCUSES).getKey(this.laserFocus).toString());
        tag.putString(TAG_RISK, RegistryManager.ACTIVE.getRegistry(TGCustomRegistries.REACTION_RISKS).getKey(this.risk).toString());
        tag.putByte(TAG_REQUIRED_INTENSITY, (byte)Math.max(0, Math.min(this.requiredIntensity, 255)));
        tag.putByte(TAG_MAX_CYCLE_COUNT, (byte)Math.max(0, Math.min(this.maxCycleCount, 255)));
        tag.putByte(TAG_REQUIRED_CYCLE_COUNT, (byte)Math.max(0, Math.min(this.requiredCycleCount, 255)));
        tag.putByte(TAG_INTENSITY_MARGIN, (byte)Math.max(0, Math.min(this.intensityMargin, 255)));
        tag.putFloat(TAG_INSTABILITY, Math.max(0, Math.min(1, this.instability)));
        tag.putByte(TAG_CURRENT_CYCLE, (byte)Math.max(0, Math.min(this.currentCycle, 255)));
        tag.putByte(TAG_SUCCESSFUL_CYCLES, (byte)Math.max(0, Math.min(this.successfulCycles, 255)));
    }

    @Override
    public final void serialize(FriendlyByteBuf buf, boolean toNetwork)
    {
        super.serialize(buf, toNetwork);
        buf.writeItem(this.inputItem);
        buf.writeFluidStack(this.inputFluid);
        
        int listSize = Math.min(this.resultItems.size(), 4);
        buf.writeByte((byte)listSize);
        
        for (int index = 0; index < listSize; index++)
        {
            buf.writeItem(this.resultItems.get(index));
        }
        
        buf.writeResourceLocation(RegistryManager.ACTIVE.getRegistry(TGCustomRegistries.LASER_FOCUSES).getKey(this.laserFocus));
        buf.writeResourceLocation(RegistryManager.ACTIVE.getRegistry(TGCustomRegistries.REACTION_RISKS).getKey(this.risk));
        buf.writeByte(Math.max(0, Math.min(this.requiredIntensity, 255)));
        buf.writeByte(Math.max(0, Math.min(this.maxCycleCount, 255)));
        buf.writeByte(Math.max(0, Math.min(this.requiredCycleCount, 255)));
        buf.writeByte(Math.max(0, Math.min(this.intensityMargin, 255)));
        buf.writeFloat(Math.max(0, Math.min(this.instability, 1)));
        buf.writeByte(Math.max(0, Math.min(this.currentCycle, 255)));
        buf.writeByte(Math.max(0, Math.min(this.successfulCycles, 255)));
    }
    
    private static final String TAG_INPUT_ITEM = "InputItem";
    private static final String TAG_INPUT_FLUID = "InputFluid";
    private static final String TAG_RESULT_ITEMS = "ResultItems";
    private static final String TAG_FOCUS = "Focus";
    private static final String TAG_RISK = "Risk";
    private static final String TAG_REQUIRED_INTENSITY = "RequiredIntensity";
    private static final String TAG_MAX_CYCLE_COUNT = "MaxCycleCount";
    private static final String TAG_REQUIRED_CYCLE_COUNT = "RequiredCycleCount";
    private static final String TAG_INTENSITY_MARGIN = "IntensityMargin";
    private static final String TAG_INSTABILITY = "Instability";
    private static final String TAG_CURRENT_CYCLE = "CurrentCycle";
    private static final String TAG_SUCCESSFUL_CYCLES = "SuccessfulCycles";
}
