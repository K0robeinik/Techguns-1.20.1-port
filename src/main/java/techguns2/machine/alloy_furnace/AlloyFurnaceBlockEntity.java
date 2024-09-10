package techguns2.machine.alloy_furnace;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemHandlerHelper;
import techguns2.TGBlockEntityTypes;
import techguns2.TGRecipeTypes;
import techguns2.TGTranslations;
import techguns2.machine.AbstractMachineBlockEntity;
import techguns2.machine.MachineUpgrade;
import techguns2.machine.MachineUpgradeItem;
import techguns2.util.DirtyableResult;
import techguns2.util.ItemStackContainer;
import techguns2.util.StackUtils;

public final class AlloyFurnaceBlockEntity extends AbstractMachineBlockEntity
{
    private final RecipeManager.CachedCheck<AlloyFurnaceBlockEntity, IAlloyFurnaceRecipe> _quickCheck;
    @Nullable
    private AlloyFurnaceOperation _currentOperation;
    
    private final ContainerData _containerData;
    
    public AlloyFurnaceBlockEntity(BlockPos blockPos, BlockState blockState)
    {
        super(TGBlockEntityTypes.ALLOY_FURNACE.get(), blockPos, blockState, (pThis) -> new ItemStackContainerInventory(pThis, SLOT_COUNT)
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
        
        this._quickCheck = RecipeManager.createCheck(TGRecipeTypes.ALLOY_FURNACE.get());
        
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
        return "Alloy Furnace";
    }
    
    @Override
    @Nullable 
    public final AlloyFurnaceOperation getCurrentOperation()
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
    protected final Component getDefaultName()
    {
        return Component.translatable(TGTranslations.Machine.AlloyFurnace.NAME);
    }
    
    @Override
    protected final void read(CompoundTag tag, boolean clientPacket)
    {
        super.read(tag, clientPacket);
        
        if (tag.contains("CurrentOperation", Tag.TAG_COMPOUND))
            this._currentOperation = new AlloyFurnaceOperation(tag.getCompound("CurrentOperation"), clientPacket);
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
    protected final AlloyFurnaceMenu createMenu(int menuId, Inventory inventory)
    {
        return new AlloyFurnaceMenu(menuId, inventory, this, this._containerData, this._ownerId);
    }
    
    @Override
    protected final boolean fetchNextOperation()
    {
        this._contentsChanged = false;
        @Nullable
        IAlloyFurnaceRecipe nextRecipe = this._quickCheck.getRecipeFor(this, this.level).orElse(null);
        
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
            
            if (primaryConsumptionAmount < 0 ||
                    secondaryConsumptionAmount < 0)
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
        
        this._currentOperation = new AlloyFurnaceOperation(
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
        
        int progress = operation.totalTicks - operation.ticksRemaining;
        if (progress % 35 == 0)
            this.level.playLocalSound(this.getBlockPos(), SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 0.75f, 1f, true);
        
        if (progress % 20 == 0)
        {
            float p = this.level.random.nextFloat() * 0.15f;
            
            BlockPos pos = this.getBlockPos();
            this.level.playLocalSound(pos, SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS, 0.75f, 0.5f + p, true);
            

            Direction facing = this.getBlockState().getValue(AlloyFurnaceBlock.FACING);
            double d0 = (double)pos.getX() + 0.5D;
            double d1 = (double)pos.getY() + this.level.random.nextDouble() * 6.0D / 16.0D + 3.0D/16.0D;
            double d2 = (double)pos.getZ() + 0.5D;
            double d3 = 0.52D;
            double d4 = this.level.random.nextDouble() * 0.6D - 0.3D;
    
            switch (facing)
            {
                case WEST:
                    this.level.addParticle(ParticleTypes.SMOKE, d0 - 0.52D, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
                    this.level.addParticle(ParticleTypes.FLAME, d0 - 0.52D, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
                    break;
                case EAST:
                    this.level.addParticle(ParticleTypes.SMOKE, d0 + 0.52D, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
                    this.level.addParticle(ParticleTypes.FLAME, d0 + 0.52D, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
                    break;
                case NORTH:
                    this.level.addParticle(ParticleTypes.SMOKE, d0 + d4, d1, d2 - 0.52D, 0.0D, 0.0D, 0.0D);
                    this.level.addParticle(ParticleTypes.FLAME, d0 + d4, d1, d2 - 0.52D, 0.0D, 0.0D, 0.0D);
                    break;
                case SOUTH:
                    this.level.addParticle(ParticleTypes.SMOKE, d0 + d4, d1, d2 + 0.52D, 0.0D, 0.0D, 0.0D);
                    this.level.addParticle(ParticleTypes.FLAME, d0 + d4, d1, d2 + 0.52D, 0.0D, 0.0D, 0.0D);
                    break;
                default:
                    break;
            }
        }
    }
    
    public static final int SLOT_PRIMARY = 0;
    public static final int SLOT_SECONDARY = 1;
    public static final int SLOT_RESULT = 2;
    public static final int SLOT_UPGRADE = 3;
    public static final int SLOT_COUNT = 4;
    
    private static final int ENERGY_CAPACITY = 40000;
    
    public static final void tick(Level level, BlockPos blockPos, BlockState blockState, AlloyFurnaceBlockEntity blockEntity)
    {
        blockEntity.tick();
    }
    
    private static final class ContainerData extends AbstractMachineBlockEntity.TickedContainerData implements AlloyFurnaceContainerData
    {
        private final AlloyFurnaceBlockEntity _blockEntity;
        
        public ContainerData(AlloyFurnaceBlockEntity blockEntity)
        {
            this._blockEntity = blockEntity;
        }
        
        @Override
        @NotNull
        public final AlloyFurnaceBlockEntity getBlockEntity()
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
    }
}
