package techguns2.machine.charging_station;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import techguns2.TGBlockEntityTypes;
import techguns2.TGRecipeTypes;
import techguns2.TGTranslations;
import techguns2.machine.AbstractMachineBlockEntity;
import techguns2.machine.MachineUpgrade;
import techguns2.machine.MachineUpgradeItem;
import techguns2.util.DirtyableResult;
import techguns2.util.ItemStackContainer;
import techguns2.util.StackUtils;

public final class ChargingStationBlockEntity extends AbstractMachineBlockEntity
{
    private final RecipeManager.CachedCheck<ChargingStationBlockEntity, IChargingStationRecipe> _quickCheck;
    @Nullable
    private ChargingStationOperation _currentOperation;
    
    private final ContainerData _containerData;
    
    public ChargingStationBlockEntity(BlockPos blockPos, BlockState blockState)
    {
        super(TGBlockEntityTypes.CHARGING_STATION.get(), blockPos, blockState, (pThis) -> new ItemStackContainerInventory(pThis, SLOT_COUNT)
        {
            @Override
            public final boolean canPlaceItemInSlot(int slot, @NotNull ItemStack itemStack)
            {
                switch (slot)
                {
                    case SLOT_INPUT:
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
        
        this._quickCheck = RecipeManager.createCheck(TGRecipeTypes.CHARGING.get());
        
        this._containerData = new ContainerData(this);
    }
    
    @Override
    public final boolean hasOperation()
    {
        return this._currentOperation != null;
    }
    
    @Override
    public final int getEnergyCapacity()
    {
        return ENERGY_CAPACITY;
    }
    
    @Override
    public final String getMachineName()
    {
        return "Charging Station";
    }
    
    @Override
    @Nullable 
    public final ChargingStationOperation getCurrentOperation()
    {
        return this._currentOperation;
    }
    
    public final ItemStack getInputItem()
    {
        return this._itemContainer.getStackInSlot(SLOT_INPUT);
    }
    
    public final ItemStack getResultItem()
    {
        return this._itemContainer.getStackInSlot(SLOT_RESULT);
    }
    
    public final ItemStack getUpgradeItem()
    {
        return this._itemContainer.getStackInSlot(SLOT_UPGRADE);
    }

    @Override
    protected final Component getDefaultName()
    {
        return Component.translatable(TGTranslations.Machine.ChargingStation.NAME);
    }
    
    @Override
    protected final void read(CompoundTag tag, boolean clientPacket)
    {
        super.read(tag, clientPacket);
        
        if (tag.contains("CurrentOperation", Tag.TAG_COMPOUND))
            this._currentOperation = new ChargingStationOperation(tag.getCompound("CurrentOperation"), clientPacket);
        else
            this._currentOperation = null;
    }
    
    @Override
    protected final void write(CompoundTag tag, boolean clientPacket)
    {
        super.write(tag, clientPacket);
        
        if (this._currentOperation != null)
        {
            CompoundTag currentOperationData = new CompoundTag();
            this._currentOperation.serialize(currentOperationData, clientPacket);
            tag.put("CurrentOperation", currentOperationData);
        }
    }

    @Override
    protected final ChargingStationMenu createMenu(int menuId, Inventory inventory)
    {
        return new ChargingStationMenu(menuId, inventory, this, this._containerData, this._ownerId);
    }

    
    @Override
    protected final boolean fetchNextOperation()
    {
        this._contentsChanged = false;
        @Nullable
        IChargingStationRecipe nextRecipe = this._quickCheck.getRecipeFor(this, this.level).orElse(null);
        
        if (nextRecipe == null)
        {
            this._currentOperation = null;
            return false;
        }
        
        ItemStack recipeResultItem;
        
        ItemStack operationInputItem = ItemStack.EMPTY;
        ItemStack operationResultItem = ItemStack.EMPTY;
        
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
        ItemStackContainer resultContainer = this._itemContainer.createView(SLOT_RESULT);
        
        int totalEnergyNeeded = 0;
        
        while (processAmount > 0)
        {
            if (!nextRecipe.canCharge(this.getInputItem()))
            {
                break;
            }
            
            
            recipeResultItem = nextRecipe.assemble(this, this.level.registryAccess());
            if (!StackUtils.canItemStacksStack(operationResultItem, recipeResultItem))
                break;
            
            if (!resultContainer.insertItemUnchecked(recipeResultItem, false).isEmpty())
                break;
            
            totalEnergyNeeded += nextRecipe.getEnergyNeeded(this.getInputItem());
            
            if (operationInputItem.isEmpty())
                operationInputItem = this.getInputItem().copyWithCount(1);
            else
                operationInputItem.grow(1);
            
            if (operationResultItem.isEmpty())
                operationResultItem = recipeResultItem.copy();
            else
                operationResultItem.grow(recipeResultItem.getCount());
            
            this.getInputItem().shrink(1);
            
            processAmount--;
            energyConsumptionMultiplier++;
        }
        
        if (energyConsumptionMultiplier == 0)
        {
            this._currentOperation = null;
            
            return false;
        }
        
        this._currentOperation = new ChargingStationOperation(
                totalEnergyNeeded,
                totalEnergyNeeded,
                operationInputItem,
                operationResultItem);
        return true;
    }
    
    @Override
    protected final void onOperationComplete()
    {
        var operation = this.getCurrentOperation();
        if (operation == null)
            return;
        
        this._itemContainer.insertItemUnchecked(SLOT_RESULT, operation.resultItem.copy(), false);
    }
    
    @Override
    protected final boolean operationClientTick()
    {
        var operation = this.getCurrentOperation();
        if (operation.isPaused)
            return false;
        
        operation.energyRemaining = Math.max(0, this.drainEnergy(Math.min(800, operation.energyRemaining)));
        return true;
    }
    
    @Override
    protected final DirtyableResult operationServerTick()
    {
        var operation = this.getCurrentOperation();
        int energyDrain = Math.max(this.getEnergyStored(), Math.min(operation.energyRemaining, 800));
        boolean operationCanProcess = energyDrain > 0 && this.tryConsumeEnergy(energyDrain);
        
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
        
        operation.energyRemaining -= energyDrain;
        
        boolean wasUnpaused = !operation.isPaused;
        operation.isPaused = false;

        if (wasUnpaused)
            return DirtyableResult.SUCCESSFUL_DIRTY;
        else
            return DirtyableResult.SUCCESSFUL;
    }
    
    @Override
    protected final void playAmbienceSounds()
    {
        // TODO: Add sounds
    }

    public static final int SLOT_INPUT = 0;
    public static final int SLOT_RESULT = 1;
    public static final int SLOT_UPGRADE = 2;
    public static final int SLOT_COUNT = 3;
    
    private static final int ENERGY_CAPACITY = 100000;
    
    public static final int DATA_SLOT_OPERATION_TOTAL_ENERGY_NEEDED = AbstractMachineBlockEntity.NEXT_DATA_SLOT;
    public static final int DATA_SLOT_OPERATION_ENERGY_REMAINING = DATA_SLOT_OPERATION_TOTAL_ENERGY_NEEDED + 1;
    public static final int NEXT_DATA_SLOT = DATA_SLOT_OPERATION_ENERGY_REMAINING + 1;
    
    public static final void tick(Level level, BlockPos blockPos, BlockState blockState, ChargingStationBlockEntity blockEntity)
    {
        blockEntity.tick();
    }
    
    private static final class ContainerData extends AbstractMachineBlockEntity.ContainerData implements ChargingStationContainerData
    {
        private final ChargingStationBlockEntity _blockEntity;
        
        public ContainerData(ChargingStationBlockEntity blockEntity)
        {
            this._blockEntity = blockEntity;
        }
        
        @Override
        @NotNull
        public final ChargingStationBlockEntity getBlockEntity()
        {
            return this._blockEntity;
        }

        @Override
        public final int getOperationTotalEnergyNeeded()
        {
            var operation = this.getBlockEntity().getCurrentOperation();
            if (operation == null)
                return 0;
            else
                return operation.neededEnergy;
        }

        @Override
        public final int getOperationEnergyRemaining()
        {
            var operation = this.getBlockEntity().getCurrentOperation();
            if (operation == null)
                return 0;
            else
                return operation.energyRemaining;
        }
        
        @Override
        public final int get(int index)
        {
            switch (index)
            {
                case DATA_SLOT_OPERATION_TOTAL_ENERGY_NEEDED:
                {
                    var operation = this.getBlockEntity().getCurrentOperation();
                    if (operation == null)
                        return 0;
                    
                    return operation.neededEnergy;
                }
                case DATA_SLOT_OPERATION_ENERGY_REMAINING:
                {
                    var operation = this.getBlockEntity().getCurrentOperation();
                    if (operation == null)
                        return 0;
                    
                    return operation.energyRemaining;
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
