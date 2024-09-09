package techguns2.machine.chemical_laboratory;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import techguns2.machine.TickedMachineOperation;

public final class ChemicalLaboratoryOperation extends TickedMachineOperation
{
    public final ItemStack primaryItem;
    public final ItemStack secondaryItem;
    public final ItemStack tankItem;
    public final FluidStack fluid;
    public final ItemStack resultItem;
    public final FluidStack resultFluid;
    
    public ChemicalLaboratoryOperation(FriendlyByteBuf buf, boolean fromNetwork)
    {
        super(buf, fromNetwork);
        this.primaryItem = buf.readItem();
        this.secondaryItem = buf.readItem();
        this.tankItem = buf.readItem();
        this.fluid = buf.readFluidStack();
        this.resultItem = buf.readItem();
        this.resultFluid = buf.readFluidStack();
    }
    
    public ChemicalLaboratoryOperation(CompoundTag tag, boolean fromNetwork)
    {
        super(tag, fromNetwork);
        
        if (tag.contains(TAG_PRIMARY_ITEM, Tag.TAG_COMPOUND))
            this.primaryItem = ItemStack.of(tag.getCompound(TAG_PRIMARY_ITEM));
        else
            this.primaryItem = ItemStack.EMPTY;
        
        if (tag.contains(TAG_SECONDARY_ITEM, Tag.TAG_COMPOUND))
            this.secondaryItem = ItemStack.of(tag.getCompound(TAG_SECONDARY_ITEM));
        else
            this.secondaryItem = ItemStack.EMPTY;
        
        if (tag.contains(TAG_TANK_ITEM, Tag.TAG_COMPOUND))
            this.tankItem = ItemStack.of(tag.getCompound(TAG_TANK_ITEM));
        else
            this.tankItem = ItemStack.EMPTY;
        
        if (tag.contains(TAG_FLUID, Tag.TAG_COMPOUND))
            this.fluid = FluidStack.loadFluidStackFromNBT(tag.getCompound(TAG_FLUID));
        else
            this.fluid = FluidStack.EMPTY;
        
        if (tag.contains(TAG_RESULT_ITEM, Tag.TAG_COMPOUND))
            this.resultItem = ItemStack.of(tag.getCompound(TAG_RESULT_ITEM));
        else
            this.resultItem = ItemStack.EMPTY;
        
        if (tag.contains(TAG_RESULT_FLUID, Tag.TAG_COMPOUND))
            this.resultFluid = FluidStack.loadFluidStackFromNBT(tag.getCompound(TAG_RESULT_FLUID));
        else
            this.resultFluid = FluidStack.EMPTY;
    }
    
    public ChemicalLaboratoryOperation(
            int totalTicks,
            int ticksRemaining,
            int energyDrainPerTick,
            ItemStack primaryItem,
            ItemStack secondaryItem,
            ItemStack tankItem,
            FluidStack fluid,
            ItemStack resultItem,
            FluidStack resultFluid)
    {
        super(totalTicks, ticksRemaining, energyDrainPerTick);
        this.primaryItem = primaryItem;
        this.secondaryItem = secondaryItem;
        this.tankItem = tankItem;
        this.fluid = fluid;
        this.resultItem = resultItem;
        this.resultFluid = resultFluid;
    }
    
    public ChemicalLaboratoryOperation(
            int totalTicks,
            int ticksRemaining,
            int energyDrainPerTick,
            ItemStack primaryItem,
            ItemStack secondaryItem,
            ItemStack tankItem,
            FluidStack fluid,
            ItemStack resultItem,
            FluidStack resultFluid,
            boolean isPaused)
    {
        super(totalTicks, ticksRemaining, energyDrainPerTick, isPaused);
        this.primaryItem = primaryItem;
        this.secondaryItem = secondaryItem;
        this.tankItem = tankItem;
        this.fluid = fluid;
        this.resultItem = resultItem;
        this.resultFluid = resultFluid;
    }
    
    @Override
    public void dropContents(Level level, double x, double y, double z)
    {
        if (!this.primaryItem.isEmpty())
            Containers.dropItemStack(level, x, y, z, this.primaryItem);
        
        if (!this.secondaryItem.isEmpty())
            Containers.dropItemStack(level, x, y, z, this.secondaryItem);
        
        if (!this.tankItem.isEmpty())
            Containers.dropItemStack(level, x, y, z, this.tankItem);
    }

    @Override
    public final void serialize(CompoundTag tag, boolean toNetwork)
    {
        super.serialize(tag, toNetwork);
        if (!this.primaryItem.isEmpty())
        {
            CompoundTag itemData = new CompoundTag();
            this.primaryItem.save(itemData);
            tag.put(TAG_PRIMARY_ITEM, itemData);
        }
        
        if (!this.secondaryItem.isEmpty())
        {
            CompoundTag itemData = new CompoundTag();
            this.secondaryItem.save(itemData);
            tag.put(TAG_SECONDARY_ITEM, itemData);
        }
        
        if (!this.tankItem.isEmpty())
        {
            CompoundTag itemData = new CompoundTag();
            this.tankItem.save(itemData);
            tag.put(TAG_TANK_ITEM, itemData);
        }
        
        if (!this.fluid.isEmpty())
        {
            CompoundTag fluidData = new CompoundTag();
            this.fluid.writeToNBT(fluidData);
            tag.put(TAG_FLUID, fluidData);
        }
        
        if (!this.resultItem.isEmpty())
        {
            CompoundTag itemData = new CompoundTag();
            this.resultItem.save(itemData);
            tag.put(TAG_RESULT_ITEM, itemData);
        }
        
        if (!this.resultFluid.isEmpty())
        {
            CompoundTag fluidData = new CompoundTag();
            this.resultFluid.writeToNBT(fluidData);
            tag.put(TAG_RESULT_FLUID, fluidData);
        }
    }

    @Override
    public final void serialize(FriendlyByteBuf buf, boolean toNetwork)
    {
        super.serialize(buf, toNetwork);
        buf.writeItem(this.primaryItem);
        buf.writeItem(this.secondaryItem);
        buf.writeItem(this.tankItem);
        buf.writeFluidStack(this.fluid);
        buf.writeItem(this.resultItem);
        buf.writeFluidStack(this.resultFluid);
    }
    
    private static final String TAG_PRIMARY_ITEM = "PrimaryItem";
    private static final String TAG_SECONDARY_ITEM = "SecondaryItem";
    private static final String TAG_TANK_ITEM = "TankItem";
    private static final String TAG_FLUID = "Fluid";
    private static final String TAG_RESULT_ITEM = "ResultItem";
    private static final String TAG_RESULT_FLUID = "ResultFluid";
}
