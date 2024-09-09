package techguns2.machine.grinder;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
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

public final class GrinderBlockEntity extends AbstractMachineBlockEntity
{
    private final RecipeManager.CachedCheck<GrinderBlockEntity, IGrinderRecipe> _quickCheck;
    @Nullable
    private GrinderOperation _currentOperation;
    
    private final ContainerData _containerData;
    
    public GrinderBlockEntity(BlockPos blockPos, BlockState blockState)
    {
        super(TGBlockEntityTypes.GRINDER.get(), blockPos, blockState, (pThis) -> new ItemStackContainerInventory(pThis, SLOT_COUNT)
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
                return slot >= SLOT_RESULT1 && slot <= SLOT_RESULT9;
            }
        });
        
        this._quickCheck = RecipeManager.createCheck(TGRecipeTypes.GRINDER.get());
        
        this._containerData = new ContainerData(this);
    }
    
    @Override
    public final int getEnergyCapacity()
    {
        return ENERGY_CAPACITY;
    }
    
    @Override
    public final String getMachineName()
    {
        return "Grinder";
    }
    
    @Override
    @Nullable 
    public final GrinderOperation getCurrentOperation()
    {
        return this._currentOperation;
    }
    
    @Override
    public final boolean hasOperation()
    {
        return this._currentOperation != null;
    }
    
    public final ItemStack getInputItem()
    {
        return this._itemContainer.getStackInSlot(SLOT_INPUT);
    }
    
    public final ItemStack getResultItem(int index)
    {
        if (index < 0 || index >= 9)
            return ItemStack.EMPTY;
        
        return this._itemContainer.getStackInSlot(SLOT_RESULT1 + index);
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
        return Component.translatable(TGTranslations.Machine.Grinder.NAME);
    }
    
    @Override
    protected final void read(CompoundTag tag, boolean clientPacket)
    {
        super.read(tag, clientPacket);
        
        if (tag.contains("CurrentOperation", Tag.TAG_COMPOUND))
            this._currentOperation = new GrinderOperation(tag.getCompound("CurrentOperation"), clientPacket);
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
    protected final GrinderMenu createMenu(int menuId, Inventory inventory)
    {
        return new GrinderMenu(menuId, inventory, this, this._containerData, this._ownerId);
    }
    
    @Override
    protected final boolean fetchNextOperation()
    {
        this._contentsChanged = false;
        @Nullable
        IGrinderRecipe nextRecipe = this._quickCheck.getRecipeFor(this, this.level).orElse(null);
        
        if (nextRecipe == null)
        {
            this._currentOperation = null;
            return false;
        }
        
        List<ItemStack> recipeResultItems;
        ItemStack operationInputItem = ItemStack.EMPTY;
        
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
        ItemStackContainer resultContainer = this._itemContainer.createView(SLOT_RESULT1, SLOT_RESULT9);
        ItemStackContainer operationResultContainer = new ItemStackContainer(resultContainer.getSlots());
        
        while (processAmount > 0)
        {
            int inputConsumptionAmount = nextRecipe.testInputItemWithConsumption(this.getInputItem());
            
            if (inputConsumptionAmount <= 0)
            {
                break;
            }

            // TODO: ensure there's enough space if (for example) the recipe randomly generates the results
            //       to prevent RNG manipulation.
            recipeResultItems = nextRecipe.assembleAll(this, this.level.registryAccess());
            
            if (!resultContainer.insertAllUnchecked(recipeResultItems).isEmpty())
                break;
            
            operationResultContainer.insertAllUnchecked(recipeResultItems);
            
            if (operationInputItem.isEmpty())
                operationInputItem = this.getInputItem().copyWithCount(inputConsumptionAmount);
            else
                operationInputItem.grow(inputConsumptionAmount);
            
            this.getInputItem().shrink(inputConsumptionAmount);
            
            processAmount--;
            energyConsumptionMultiplier++;
        }
        
        if (energyConsumptionMultiplier == 0)
        {
            this._currentOperation = null;
            
            return false;
        }
        
        this._currentOperation = new GrinderOperation(
                nextRecipe.getProcessingTime(),
                nextRecipe.getProcessingTime(),
                nextRecipe.getEnergyDrainPerTick() * energyConsumptionMultiplier,
                operationInputItem,
                operationResultContainer.toList());
        return true;
    }
    
    @Override
    protected final void onOperationComplete()
    {
        var operation = this.getCurrentOperation();
        if (operation == null)
            return;
        
        this._itemContainer.insertAllUnchecked(SLOT_RESULT1, SLOT_RESULT9, operation.resultItems);
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
    protected final void playAmbienceSounds()
    {
        // TODO Ambience Sounds
    }

    public static final int SLOT_INPUT = 0;
    public static final int SLOT_RESULT1 = 1;
    public static final int SLOT_RESULT2 = 2;
    public static final int SLOT_RESULT3 = 3;
    public static final int SLOT_RESULT4 = 4;
    public static final int SLOT_RESULT5 = 5;
    public static final int SLOT_RESULT6 = 6;
    public static final int SLOT_RESULT7 = 7;
    public static final int SLOT_RESULT8 = 8;
    public static final int SLOT_RESULT9 = 9;
    public static final int SLOT_UPGRADE = 10;
    public static final int SLOT_COUNT = 11;
    
    private static final int ENERGY_CAPACITY = 20000;
    
    public static final void tick(Level level, BlockPos blockPos, BlockState blockState, GrinderBlockEntity blockEntity)
    {
        blockEntity.tick();
    }
    
    private static final class ContainerData extends AbstractMachineBlockEntity.TickedContainerData implements GrinderContainerData
    {
        private final GrinderBlockEntity _blockEntity;
        
        public ContainerData(GrinderBlockEntity blockEntity)
        {
            this._blockEntity = blockEntity;
        }
        
        @Override
        @NotNull
        public final GrinderBlockEntity getBlockEntity()
        {
            return this._blockEntity;
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
        public final int getCount()
        {
            return NEXT_DATA_SLOT;
        }
    }
}
