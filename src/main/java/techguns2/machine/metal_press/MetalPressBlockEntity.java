package techguns2.machine.metal_press;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import techguns2.TGBlockEntityTypes;
import techguns2.TGRecipeTypes;
import techguns2.TGSounds;
import techguns2.TGTranslations;
import techguns2.machine.AbstractMachineBlockEntity;
import techguns2.machine.MachineUpgrade;
import techguns2.machine.MachineUpgradeItem;
import techguns2.util.DirtyableResult;
import techguns2.util.ItemStackContainer;
import techguns2.util.StackUtils;

public final class MetalPressBlockEntity extends AbstractMachineBlockEntity
{
    private final RecipeManager.CachedCheck<MetalPressBlockEntity, IMetalPressRecipe> _quickCheck;
    private boolean _splitStack;
    @Nullable
    private MetalPressOperation _currentOperation;
    
    private final ContainerData _containerData;
    
    public MetalPressBlockEntity(BlockPos blockPos, BlockState blockState)
    {
        super(TGBlockEntityTypes.METAL_PRESS.get(), blockPos, blockState, (pThis) -> new ItemStackContainerInventory(pThis, SLOT_COUNT)
        {
            @Override
            public final boolean canPlaceItemInSlot(int slot, @NotNull ItemStack itemStack)
            {
                switch (slot)
                {
                    case SLOT_PRIMARY:
                    case SLOT_SECONDARY:
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
        
        this._quickCheck = RecipeManager.createCheck(TGRecipeTypes.METAL_PRESS.get());
        
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
        return "Metal Press";
    }
    
    @Override
    public byte getFlags()
    {
        return (byte)(super.getFlags() |
                (this._splitStack ?
                        SPLIT_STACK_FLAG :
                        0b00000000));
    }
    
    @Override
    public void setFlags(byte flagValue)
    {
        this._splitStack = (flagValue & SPLIT_STACK_FLAG) == SPLIT_STACK_FLAG;
        super.setFlags(flagValue);
    }
    
    public final boolean splitStack()
    {
        return this._splitStack;
    }
    
    public final void setSplitStack(boolean splitStack)
    {
        this._splitStack = splitStack;
    }
    
    @Override
    @Nullable 
    public final MetalPressOperation getCurrentOperation()
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
    
    public final ItemStack getResultItem()
    {
        return this._itemContainer.getStackInSlot(SLOT_RESULT);
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
        return Component.translatable(TGTranslations.Machine.MetalPress.NAME);
    }
    
    @Override
    protected final void read(CompoundTag tag, boolean clientPacket)
    {
        super.read(tag, clientPacket);
        
        if (tag.contains("CurrentOperation", Tag.TAG_COMPOUND))
            this._currentOperation = new MetalPressOperation(tag.getCompound("CurrentOperation"), clientPacket);
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
    protected final MetalPressMenu createMenu(int menuId, Inventory inventory)
    {
        return new MetalPressMenu(menuId, inventory, this, this._containerData, this._ownerId);
    }
    
    @Override
    protected final boolean fetchNextOperation()
    {
        if (this._splitStack)
        {
            ItemStack primaryItem = this.getPrimaryItem();
            ItemStack secondaryItem = this.getSecondaryItem();
            
            if (primaryItem.getCount() > 1)
            {
                if (secondaryItem.isEmpty())
                {
                    int subtractionAmount = primaryItem.getCount() >> 1;
                    
                    this._itemContainer.setStackInSlot(SLOT_SECONDARY, primaryItem.copyWithCount(subtractionAmount));
                    this._itemContainer.setStackInSlot(SLOT_PRIMARY, primaryItem.copyWithCount(primaryItem.getCount() - subtractionAmount));
                }
            }
            else if (secondaryItem.getCount() > 1 && primaryItem.isEmpty())
            {
                int subtractionAmount = secondaryItem.getCount() >> 1;
                
                this._itemContainer.setStackInSlot(SLOT_PRIMARY, secondaryItem.copyWithCount(subtractionAmount));
                this._itemContainer.setStackInSlot(SLOT_SECONDARY, secondaryItem.copyWithCount(secondaryItem.getCount() - subtractionAmount));
            }
        }
        
        
        this._contentsChanged = false;
        @Nullable
        IMetalPressRecipe nextRecipe = this._quickCheck.getRecipeFor(this, this.level).orElse(null);
        
        if (nextRecipe == null)
        {
            this._currentOperation = null;
            return false;
        }
        
        ItemStack recipeResultItem;
        
        ItemStack operationPrimaryItem = ItemStack.EMPTY;
        ItemStack operationSecondaryItem = ItemStack.EMPTY;
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
        
        while (processAmount > 0)
        {
            int primaryConsumptionAmount = nextRecipe.testPrimaryWithConsumption(this.getPrimaryItem());
            int secondaryConsumptionAmount = nextRecipe.testSecondaryWithConsumption(this.getSecondaryItem());
            
            if (primaryConsumptionAmount <= 0 ||
                    secondaryConsumptionAmount <= 0)
            {
                break;
            }
            
            recipeResultItem = nextRecipe.assemble(this, this.level.registryAccess());
            if (!StackUtils.canItemStacksStack(operationResultItem, recipeResultItem))
                break;
            
            if (!resultContainer.insertItemUnchecked(recipeResultItem, false).isEmpty())
                break;
            
            if (operationPrimaryItem.isEmpty())
                operationPrimaryItem = this.getPrimaryItem().copyWithCount(primaryConsumptionAmount);
            else
                operationPrimaryItem.grow(primaryConsumptionAmount);
            
            if (operationSecondaryItem.isEmpty())
                operationSecondaryItem = this.getSecondaryItem().copyWithCount(secondaryConsumptionAmount);
            else
                operationSecondaryItem.grow(secondaryConsumptionAmount);
            
            if (operationResultItem.isEmpty())
                operationResultItem = recipeResultItem.copy();
            else
                operationResultItem.grow(recipeResultItem.getCount());
            
            this.getPrimaryItem().shrink(primaryConsumptionAmount);
            this.getSecondaryItem().shrink(secondaryConsumptionAmount);
            
            processAmount--;
            energyConsumptionMultiplier++;
        }
        
        if (energyConsumptionMultiplier == 0)
        {
            this._currentOperation = null;
            
            return false;
        }
        
        this._currentOperation = new MetalPressOperation(
                nextRecipe.getProcessingTime(),
                nextRecipe.getProcessingTime(),
                nextRecipe.getEnergyDrainPerTick() * energyConsumptionMultiplier,
                operationPrimaryItem,
                operationSecondaryItem,
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
        if (!this.level.isClientSide)
            return;
        
        var operation = this.getCurrentOperation();
        if (operation.isPaused)
            return;
        
        int soundTick1 = Math.round((float)operation.totalTicks * 0.075f);
        int halfTime = Math.round((float)operation.totalTicks * 0.5f);
        
        if (operation.ticksRemaining == soundTick1 || operation.ticksRemaining == (soundTick1 + halfTime))
        {
            this.level.playSound(null, this.getBlockPos(), TGSounds.METAL_PRESS_WORK.get(), SoundSource.BLOCKS, 0.5F, 1.0F);
        }
    }

    public static final int SLOT_PRIMARY = 0;
    public static final int SLOT_SECONDARY = 1;
    public static final int SLOT_RESULT = 2;
    public static final int SLOT_UPGRADE = 3;
    public static final int SLOT_COUNT = 4;
    
    private static final int ENERGY_CAPACITY = 20000;
    public static final byte SPLIT_STACK_FLAG = (byte)0b01000000;
    
    public static final void tick(Level level, BlockPos blockPos, BlockState blockState, MetalPressBlockEntity blockEntity)
    {
        blockEntity.tick();
    }
    
    private static final class ContainerData extends AbstractMachineBlockEntity.TickedContainerData implements MetalPressContainerData
    {
        private final MetalPressBlockEntity _blockEntity;
        
        public ContainerData(MetalPressBlockEntity blockEntity)
        {
            this._blockEntity = blockEntity;
        }
        
        @Override
        @NotNull
        public final MetalPressBlockEntity getBlockEntity()
        {
            return this._blockEntity;
        }
        
        @Override
        public final boolean splitItems()
        {
            return this.getBlockEntity()._splitStack;
        }
        
        @Override
        public final void setSplitItems(boolean splitItems)
        {
            this.getBlockEntity()._splitStack = splitItems;
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
