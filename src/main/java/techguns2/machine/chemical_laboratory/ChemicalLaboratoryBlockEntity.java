package techguns2.machine.chemical_laboratory;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;
import techguns2.TGBlockEntityTypes;
import techguns2.TGRecipeTypes;
import techguns2.TGSounds;
import techguns2.TGTranslations;
import techguns2.machine.AbstractMachineBlockEntity;
import techguns2.machine.MachineUpgrade;
import techguns2.machine.MachineUpgradeItem;
import techguns2.util.DirtyableResult;
import techguns2.util.FluidStackContainer;
import techguns2.util.ItemStackContainer;
import techguns2.util.StackUtils;

public final class ChemicalLaboratoryBlockEntity extends AbstractMachineBlockEntity
{
    private final LazyOptional<IFluidHandler> _fluidCapability;
    private final FluidStackContainer _fluidContainer;
    
    private final RecipeManager.CachedCheck<ChemicalLaboratoryBlockEntity, IChemicalLaboratoryRecipe> _quickCheck;
    private boolean _drainFromOutput;
    @Nullable
    private ChemicalLaboratoryOperation _currentOperation;
    
    private static final RandomSource CLIENT_RANDOM_SOURCE = RandomSource.create();
    
    private final ContainerData _containerData;
    
    public ChemicalLaboratoryBlockEntity(BlockPos blockPos, BlockState blockState)
    {
        super(TGBlockEntityTypes.CHEMICAL_LABORATORY.get(), blockPos, blockState, (pThis) -> new ItemStackContainerInventory(pThis, SLOT_COUNT)
        {
            @Override
            public final boolean canPlaceItemInSlot(int slot, @NotNull ItemStack itemStack)
            {
                switch (slot)
                {
                    case SLOT_PRIMARY:
                    case SLOT_SECONDARY:
                    case SLOT_TANK_INPUT:
                        return true;
                    case SLOT_UPGRADE:
                        return itemStack.getItem() instanceof MachineUpgradeItem;
                    default:
                        return false;
                }
            }
            
            @Override
            public final boolean canTakeItemFromSlot(int slot, int amount)
            {
                return slot == SLOT_RESULT;
            }
        });
        
        this._fluidContainer = new FluidStackContainer(FLUID_SLOT_COUNT)
        {
            @Override
            public final int getSlotCapacity(int slot)
            {
                switch (slot)
                {
                    case FLUID_SLOT_INPUT:
                        return 8_000;
                    case FLUID_SLOT_RESULT:
                        return 16_000;
                    default:
                        return 0;
                }
            }
            
            @Override
            public final boolean canFillFluidInSlot(int slot, @NotNull FluidStack fluidStack)
            {
                return slot == FLUID_SLOT_INPUT;
            }
            
            @Override
            public final boolean canDrainFluidFromSlot(int slot, int amount)
            {
                return slot == FLUID_SLOT_RESULT;
            }
            
            @Override
            protected final void onContentsChanged(int slot)
            {
                super.onContentsChanged(slot);
                ChemicalLaboratoryBlockEntity.this._contentsChanged = true;
            }
        };
        
        this._fluidCapability = LazyOptional.of(() -> this._fluidContainer);
        
        this._quickCheck = RecipeManager.createCheck(TGRecipeTypes.CHEMICAL_LABORATORY.get());
        this._drainFromOutput = false;
        
        this._containerData = new ContainerData(this);
    }
    
    @Override
    public byte getFlags()
    {
        return (byte)(super.getFlags() |
                (this._drainFromOutput ?
                        DRAIN_FLAG :
                        0b00000000));
    }
    
    @Override
    public void setFlags(byte flagValue)
    {
        this._drainFromOutput = (flagValue & DRAIN_FLAG) == DRAIN_FLAG;
        super.setFlags(flagValue);
    }
    
    public final void setDrainFromInput()
    {
        this._drainFromOutput = false;
    }
    
    public final void setDrainFromOutput()
    {
        this._drainFromOutput = true;
    }
    
    @Override
    public final int getEnergyCapacity()
    {
        return ENERGY_CAPACITY;
    }
    
    @Override
    public final String getMachineName()
    {
        return "Chemical Laboratory";
    }
    
    @Override
    @Nullable 
    public final ChemicalLaboratoryOperation getCurrentOperation()
    {
        return this._currentOperation;
    }
    
    @Override
    public final boolean hasOperation()
    {
        return this._currentOperation != null;
    }
    
    public final ItemStack getPrimaryItem()
    {
        return this._itemContainer.getStackInSlot(SLOT_PRIMARY);
    }
    
    public final ItemStack getSecondaryItem()
    {
        return this._itemContainer.getStackInSlot(SLOT_SECONDARY);
    }
    
    public final ItemStack getTankItem()
    {
        return this._itemContainer.getStackInSlot(SLOT_TANK_INPUT);
    }
    
    public final FluidStack getFluid()
    {
        return this._fluidContainer.getStackInSlot(FLUID_SLOT_INPUT);
    }
    
    public final ItemStack getResultItem()
    {
        return this._itemContainer.getStackInSlot(SLOT_SECONDARY);
    }
    
    public final FluidStack getResultFluid()
    {
        return this._fluidContainer.getStackInSlot(FLUID_SLOT_RESULT);
    }
    
    public final ItemStack getUpgradeItem()
    {
        return this._itemContainer.getStackInSlot(SLOT_UPGRADE);
    }

    @Override
    public final boolean stillValid(Player player)
    {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    protected final Component getDefaultName()
    {
        return Component.translatable(TGTranslations.Machine.ChemicalLaboratory.NAME);
    }
    
    @Override
    protected void read(CompoundTag tag, boolean clientPacket)
    {
        super.read(tag, clientPacket);
        
        if (tag.contains("FluidInventory", Tag.TAG_COMPOUND))
            this._fluidContainer.deserializeNBT(tag.getCompound("FluidInventory"));
        else
            this._fluidContainer.clear();
        
        if (tag.contains("CurrentOperation", Tag.TAG_COMPOUND))
            this._currentOperation = new ChemicalLaboratoryOperation(tag.getCompound("CurrentOperation"), clientPacket);
        else
            this._currentOperation = null;
    }
    
    @Override
    protected void write(CompoundTag tag, boolean clientPacket)
    {
        super.write(tag, clientPacket);
        
        tag.put("FluidInventory", this._fluidContainer.serializeNBT());
        
        if (this._currentOperation != null)
        {
            CompoundTag currentOperationData = new CompoundTag();
            this._currentOperation.serialize(currentOperationData, clientPacket);
            tag.put("CurrentOperation", currentOperationData);
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    @NotNull
    public final <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap)
    {
        if (cap == ForgeCapabilities.FLUID_HANDLER)
            return (LazyOptional<T>)this._fluidCapability;
        else
            return super.getCapability(cap);
    }

    @Override
    protected final ChemicalLaboratoryMenu createMenu(int menuId, Inventory inventory)
    {
        return new ChemicalLaboratoryMenu(menuId, inventory, this, this._containerData, this._ownerId);
    }
    
    @Override
    protected final boolean fetchNextOperation()
    {
        this._contentsChanged = false;
        @Nullable
        IChemicalLaboratoryRecipe nextRecipe = this._quickCheck.getRecipeFor(this, this.level).orElse(null);
        
        if (nextRecipe == null)
        {
            this._currentOperation = null;
            return false;
        }
        
        ItemStack recipeResultItem;
        FluidStack recipeResultFluid;
        
        ItemStack operationPrimaryItem = ItemStack.EMPTY;
        ItemStack operationSecondaryItem = ItemStack.EMPTY;
        ItemStack operationTankItem = ItemStack.EMPTY;
        FluidStack operationFluid = FluidStack.EMPTY;
        ItemStack operationResultItem = ItemStack.EMPTY;
        FluidStack operationResultFluid = FluidStack.EMPTY;
        
        int energyConsumptionMultiplier = 0;
        int processAmount = 1;
        
        ItemStack upgradeItem = this.getUpgradeItem();
        if (upgradeItem.getItem() instanceof MachineUpgradeItem machineUpgradeItem)
        {
            MachineUpgrade upgrade = machineUpgradeItem.getUpgrade();
            if (upgrade instanceof MachineUpgrade.Stack)
            {
                processAmount += upgradeItem.getCount();
            }
        }
        
        // copy existing results
        ItemStackContainer resultItemContainer = this._itemContainer.createView(SLOT_RESULT);
        FluidStackContainer resultFluidContainer = this._fluidContainer.createView(FLUID_SLOT_RESULT);
        
        while (processAmount > 0)
        {
            int primaryConsumptionAmount = nextRecipe.testPrimaryWithConsumption(this.getPrimaryItem());
            int secondaryConsumptionAmount = nextRecipe.testSecondaryWithConsumption(this.getSecondaryItem());
            int tankConsumptionAmount = nextRecipe.testTankWithConsumption(this.getTankItem());
            int fluidConsumptionAmount = nextRecipe.testFluidWithConsumption(this.getFluid());
            
            if (primaryConsumptionAmount < 0 ||
                    secondaryConsumptionAmount < 0 ||
                    tankConsumptionAmount < 0 ||
                    fluidConsumptionAmount < 0)
            {
                break;
            }
            
            recipeResultItem = nextRecipe.assemble(this, this.level.registryAccess());
            recipeResultFluid = nextRecipe.assembleFluid(this, this.level.registryAccess());
            if (!StackUtils.canItemStacksStack(operationResultItem, recipeResultItem) || 
                    !StackUtils.canFluidStacksStack(operationResultFluid, recipeResultFluid))
                break;                
            
            if (!resultItemContainer.insertItemUnchecked(recipeResultItem, false).isEmpty() ||
                    !resultFluidContainer.insertFluidUnchecked(recipeResultFluid, FluidAction.EXECUTE).isEmpty())
                break;
            
            if (operationPrimaryItem.isEmpty())
                operationPrimaryItem = this.getPrimaryItem().copyWithCount(primaryConsumptionAmount);
            else
                operationPrimaryItem.grow(primaryConsumptionAmount);
            
            if (operationSecondaryItem.isEmpty())
                operationSecondaryItem = this.getSecondaryItem().copyWithCount(secondaryConsumptionAmount);
            else
                operationSecondaryItem.grow(secondaryConsumptionAmount);
            
            if (operationTankItem.isEmpty())
                operationTankItem = this.getTankItem().copyWithCount(tankConsumptionAmount);
            else
                operationTankItem.grow(tankConsumptionAmount);
            
            if (operationResultItem.isEmpty())
                operationResultItem = recipeResultItem.copy();
            else
                operationResultItem.grow(recipeResultItem.getCount());
            
            if (operationResultFluid.isEmpty())
                operationResultFluid = recipeResultFluid.copy();
            else
                operationResultFluid.grow(recipeResultFluid.getAmount());
            
            this.getPrimaryItem().shrink(primaryConsumptionAmount);
            this.getSecondaryItem().shrink(secondaryConsumptionAmount);
            this.getTankItem().shrink(tankConsumptionAmount);
            
            processAmount--;
            energyConsumptionMultiplier++;
        }
        
        if (energyConsumptionMultiplier == 0)
        {
            this._currentOperation = null;
            
            return false;
        }
        
        this._currentOperation = new ChemicalLaboratoryOperation(
                nextRecipe.getProcessingTime(),
                nextRecipe.getProcessingTime(),
                nextRecipe.getEnergyDrainPerTick() * energyConsumptionMultiplier,
                operationPrimaryItem,
                operationSecondaryItem,
                operationTankItem,
                operationFluid,
                operationResultItem,
                operationResultFluid);
        return true;
    }
    
    @Override
    protected final void onOperationComplete()
    {
        var operation = this.getCurrentOperation();
        if (operation == null)
            return;
        
        this._itemContainer.insertItemUnchecked(SLOT_RESULT, operation.resultItem.copy(), false);
        this._fluidContainer.insertFluidUnchecked(FLUID_SLOT_RESULT, operation.resultFluid.copy(), FluidAction.EXECUTE);
    }
    
    @Override
    protected final boolean operationClientTick()
    {
        var operation = this.getCurrentOperation();
        if (operation.isPaused)
            return false;
        
        this.drainEnergy(operation.energyDrainPerTick);
        operation.ticksRemaining = Math.max(0, operation.ticksRemaining - 1);
        return true;
    }
    
    @Override
    protected final DirtyableResult operationServerTick()
    {
        var operation = this.getCurrentOperation();
        boolean operationCanProcess = this.tryConsumeEnergy(operation.energyDrainPerTick);
        
        if (!operationCanProcess)
        {
            if (!operation.isPaused)
            {
                this._currentOperation.isPaused = true;
                return DirtyableResult.FAILED_DIRTY;
            }
            else
                return DirtyableResult.FAILED;
        }
        
        operation.ticksRemaining--;
        
        boolean wasUnpaused = !operation.isPaused;
        operation.isPaused = false;

        if (wasUnpaused)
            return DirtyableResult.SUCCESSFUL_DIRTY;
        else
            return DirtyableResult.SUCCESSFUL;
    }
    
    @Override
    protected void playAmbienceSounds()
    {
        if (!this.level.isClientSide)
            return;

        var operation = this.getCurrentOperation();
        if (operation.isPaused)
            return;
        
        int progress = operation.totalTicks - operation.ticksRemaining;
        int halfTime = Math.round((float)operation.totalTicks * 0.5f);
        
        if (progress == 1 || progress == halfTime + 1)
        {
            this.level.playLocalSound(this.getBlockPos(), TGSounds.CHEMICAL_LABORATORY_WORK.get(), SoundSource.BLOCKS, 0.75F, 1.0F, false);
        }
        
        if (progress % 10 == 0)
        {
            this.level.addParticle(ParticleTypes.BUBBLE_POP, 
                    this.getBlockPos().getX() + 0.5d + CLIENT_RANDOM_SOURCE.nextFloat(),
                    this.getBlockPos().getY() + 0.5d + CLIENT_RANDOM_SOURCE.nextFloat(),
                    this.getBlockPos().getZ() + 0.5d + CLIENT_RANDOM_SOURCE.nextFloat(), 
                    0, 1, 0);
        }
    }

    public static final int SLOT_PRIMARY = 0;
    public static final int SLOT_SECONDARY = 1;
    public static final int SLOT_TANK_INPUT = 2;
    public static final int SLOT_RESULT = 3;
    public static final int SLOT_UPGRADE = 4;
    public static final int SLOT_COUNT = 5;
    
    public static final int FLUID_SLOT_INPUT = 0;
    public static final int FLUID_SLOT_RESULT = 1;
    public static final int FLUID_SLOT_COUNT = 2;
    
    protected static final int DATA_SLOT_FLUID_ID = AbstractMachineBlockEntity.TICKED_NEXT_DATA_SLOT;
    protected static final int DATA_SLOT_FLUID_AMOUNT = DATA_SLOT_FLUID_ID + 1;
    protected static final int DATA_SLOT_RESULT_FLUID_ID = DATA_SLOT_FLUID_AMOUNT + 1;
    protected static final int DATA_SLOT_RESULT_FLUID_AMOUNT = DATA_SLOT_RESULT_FLUID_ID + 1;
    
    protected static final int NEXT_DATA_SLOT = DATA_SLOT_RESULT_FLUID_AMOUNT + 1;
    
    public static final byte DRAIN_FLAG = (byte)0b01000000;
    
    private static final int ENERGY_CAPACITY = 20000;
    
    public static final void tick(Level level, BlockPos blockPos, BlockState blockState, ChemicalLaboratoryBlockEntity blockEntity)
    {
        blockEntity.tick();
    }
    
    private static final class ContainerData extends AbstractMachineBlockEntity.TickedContainerData implements ChemicalLaboratoryContainerData
    {
        private final ChemicalLaboratoryBlockEntity _blockEntity;
        
        public ContainerData(ChemicalLaboratoryBlockEntity blockEntity)
        {
            this._blockEntity = blockEntity;
        }
        
        @Override
        @NotNull
        public final ChemicalLaboratoryBlockEntity getBlockEntity()
        {
            return this._blockEntity;
        }
        
        @Override
        public final FluidStack getFluid()
        {
            return this.getBlockEntity().getFluid();
        }
        
        @Override
        public final void dumpFluid()
        {
            this.getBlockEntity()._fluidContainer.setStackInSlot(FLUID_SLOT_INPUT, FluidStack.EMPTY);
            this.getBlockEntity().setChanged();
        }

        @Override
        public final FluidStack getResultFluid()
        {
            return this.getBlockEntity().getResultFluid();
        }
        
        @Override
        public final void dumpResultFluid()
        {
            this.getBlockEntity()._fluidContainer.setStackInSlot(FLUID_SLOT_RESULT, FluidStack.EMPTY);
            this.getBlockEntity().setChanged();
        }
        
        @Override
        public final DrainMode getDrainMode()
        {
            if (this.getBlockEntity()._drainFromOutput)
                return DrainMode.FROM_OUTPUT;
            else
                return DrainMode.FROM_INPUT;
        }

        @Override
        public final int getOperationTotalTicks()
        {
            var operation = this.getBlockEntity().getCurrentOperation();
            if (operation == null)
                return 0;
            else
                return operation.totalTicks;
        }

        @Override
        public final int getOperationTicksRemaining()
        {
            var operation = this.getBlockEntity().getCurrentOperation();
            if (operation == null)
                return 0;
            else
                return operation.ticksRemaining;
        }
        
        @Override
        public final void setDrainMode(DrainMode mode)
        {
            this.getBlockEntity()._drainFromOutput = mode == DrainMode.FROM_OUTPUT;
        }

        @Override
        public final int get(int index)
        {
            switch (index)
            {
                case DATA_SLOT_FLUID_ID:
                {
                    FluidStack fluid = this.getBlockEntity().getFluid();
                    
                    if (fluid.isEmpty())
                        return -1;
                    else
                        return ((ForgeRegistry<Fluid>)ForgeRegistries.FLUIDS).getID(fluid.getFluid());
                }
                case DATA_SLOT_FLUID_AMOUNT:
                {
                    FluidStack fluid = this.getBlockEntity().getFluid();
                    
                    if (fluid.isEmpty())
                        return 0;
                    else
                        return fluid.getAmount();
                }
                case DATA_SLOT_RESULT_FLUID_ID:
                {
                    FluidStack fluid = this.getBlockEntity().getResultFluid();
                    
                    if (fluid.isEmpty())
                        return -1;
                    else
                        return ((ForgeRegistry<Fluid>)ForgeRegistries.FLUIDS).getID(fluid.getFluid());
                }
                case DATA_SLOT_RESULT_FLUID_AMOUNT:
                {
                    FluidStack fluid = this.getBlockEntity().getResultFluid();
                    
                    if (fluid.isEmpty())
                        return 0;
                    else
                        return fluid.getAmount();
                }
                default:
                    return super.get(index);
            }
        }

        @Override
        public final int getCount()
        {
            return NEXT_DATA_SLOT;
        }
    }
}
