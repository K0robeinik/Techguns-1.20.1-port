package techguns2.machine.fabricator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import techguns2.TGBlockEntityTypes;
import techguns2.TGBlocks;
import techguns2.TGRecipeTypes;
import techguns2.TGSounds;
import techguns2.TGTranslations;
import techguns2.machine.AbstractMultiBlockMachineControllerBlockEntity;
import techguns2.machine.MachineUpgrade;
import techguns2.machine.MachineUpgradeItem;
import techguns2.util.DirtyableResult;
import techguns2.util.ItemStackContainer;
import techguns2.util.StackUtils;

public final class FabricatorControllerBlockEntity extends AbstractMultiBlockMachineControllerBlockEntity<
    FabricatorPartBlockEntity,
    FabricatorControllerBlockEntity>
{
    private final RecipeManager.CachedCheck<FabricatorControllerBlockEntity, IFabricatorRecipe> _quickCheck;
    
    @Nullable
    private FabricatorOperation _currentOperation;
    
    private final ContainerData _containerData;
    
    public FabricatorControllerBlockEntity(BlockPos blockPos, BlockState blockState)
    {
        super(
                FabricatorPartBlockEntity.class,
                2,
                2,
                2,
                TGBlockEntityTypes.FABRICATOR_CONTROLLER.get(),
                blockPos,
                blockState,
                (pThis) -> new ItemStackContainerInventory(pThis, SLOT_COUNT)
                {
                    @Override
                    public final boolean canPlaceItemInSlot(int slot, @NotNull ItemStack itemStack)
                    {
                        switch (slot)
                        {
                            case SLOT_INPUT:
                            case SLOT_WIRES:
                            case SLOT_REDSTONE:
                            case SLOT_PLATE:
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
        
        this._quickCheck = RecipeManager.createCheck(TGRecipeTypes.FABRICATOR.get());
        
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
        return "Fabricator";
    }
    
    public final ItemStack getInputItem()
    {
        return this._itemContainer.getStackInSlot(SLOT_INPUT);
    }
    
    public final ItemStack getWireItem()
    {
        return this._itemContainer.getStackInSlot(SLOT_WIRES);
    }
    
    public final ItemStack getRedstoneItem()
    {
        return this._itemContainer.getStackInSlot(SLOT_REDSTONE);
    }
    
    public final ItemStack getPlateItem()
    {
        return this._itemContainer.getStackInSlot(SLOT_PLATE);
    }
    
    public final ItemStack getUpgradeItem()
    {
        return this._itemContainer.getStackInSlot(SLOT_UPGRADE);
    }
    
    public final ItemStack getResultItem()
    {
        return this._itemContainer.getStackInSlot(SLOT_RESULT);
    }
    
    @Override
    protected final boolean tryForm(Direction fromSide)
    {
        boolean failed = false;
        BlockPos origin = this.getBlockPos();
        BlockPos side;
        BlockPos forward;
        BlockPos forwardSide;
        
        switch (fromSide)
        {
            case NORTH:
                forward = origin.north();
                side = origin.west();
                forwardSide = side.north();
                break;
            case EAST:
                forward = origin.east();
                side = origin.north();
                forwardSide = side.east();
                break;
            case SOUTH:
                forward = origin.south();
                side = origin.east();
                forwardSide = side.south();
                break;
            case WEST:
                forward = origin.west();
                side = origin.south();
                forwardSide = side.west();
                break;
            default:
                forward = origin.north();
                side = origin.west();
                forwardSide = side.north();
                break;
        }
        
        BlockPos originAbove = origin.above();
        BlockPos sideAbove = side.above();
        BlockPos forwardAbove = forward.above();
        BlockPos forwardSideAbove = forwardSide.above();
        
        int partPositionIndex = 0;
        
        if (this.level.getBlockState(side).getBlock() instanceof FabricatorHousingBlock)
        {
            this._partPositions[partPositionIndex] = side;
            partPositionIndex++;
        }
        else
        {
            // TODO: Show particles
            failed = true;
        }
        
        if (this.level.getBlockState(forward).getBlock() instanceof FabricatorHousingBlock)
        {
            this._partPositions[partPositionIndex] = forward;
            partPositionIndex++;
        }
        else
        {
            // TODO: Show particles
            failed = true;
        }
        
        if (this.level.getBlockState(forwardSide).getBlock() instanceof FabricatorHousingBlock)
        {
            this._partPositions[partPositionIndex] = forwardSide;
            partPositionIndex++;
        }
        else
        {
            // TODO: Show particles
            failed = true;
        }

        if (this.level.getBlockState(originAbove).getBlock() instanceof FabricatorGlassBlock)
        {
            this._partPositions[partPositionIndex] = originAbove;
            partPositionIndex++;
        }
        else
        {
            // TODO: Show particles
            failed = true;
        }
        
        if (this.level.getBlockState(sideAbove).getBlock() instanceof FabricatorGlassBlock)
        {
            this._partPositions[partPositionIndex] = sideAbove;
            partPositionIndex++;
        }
        else
        {
            // TODO: Show particles
            failed = true;
        }
        
        if (this.level.getBlockState(forwardAbove).getBlock() instanceof FabricatorGlassBlock)
        {
            this._partPositions[partPositionIndex] = forwardAbove;
            partPositionIndex++;
        }
        else
        {
            // TODO: Show particles
            failed = true;
        }
        
        if (this.level.getBlockState(forwardSideAbove).getBlock() instanceof FabricatorGlassBlock)
        {
            this._partPositions[partPositionIndex] = forwardSideAbove;
            partPositionIndex++;
        }
        else
        {
            // TODO: Show particles
            failed = true;
        }
        
        if (failed)
            return false;
        
        BlockState basePartBlockState = TGBlocks.FABRICATOR_PART.get().defaultBlockState();
        BlockState housingBlockState = basePartBlockState
                .setValue(FabricatorPartBlock.KIND, FabricatorPartBlock.Kind.HOUSING);
        BlockState glassBlockState = basePartBlockState
                .setValue(FabricatorPartBlock.KIND, FabricatorPartBlock.Kind.GLASS);
        
        BlockState sideBlockState = housingBlockState;
        BlockState forwardBlockState = housingBlockState;
        BlockState forwardSideBlockState = housingBlockState;
        BlockState originAboveBlockState = glassBlockState;
        BlockState sideAboveBlockState = glassBlockState;
        BlockState forwardAboveBlockState = glassBlockState;
        BlockState forwardSideAboveBlockState = glassBlockState;
        
        this.level.setBlock(side, sideBlockState, Block.UPDATE_ALL);
        this.level.setBlock(forward, forwardBlockState, Block.UPDATE_ALL);
        this.level.setBlock(forwardSide, forwardSideBlockState, Block.UPDATE_ALL);
        this.level.setBlock(originAbove, originAboveBlockState, Block.UPDATE_ALL);
        this.level.setBlock(sideAbove, sideAboveBlockState, Block.UPDATE_ALL);
        this.level.setBlock(forwardAbove, forwardAboveBlockState, Block.UPDATE_ALL);
        this.level.setBlock(forwardSideAbove, forwardSideAboveBlockState, Block.UPDATE_ALL);
        
        return true;
    }
    
    @Override
    @Nullable 
    public final FabricatorOperation getCurrentOperation()
    {
        return this._currentOperation;
    }
    
    @Override
    public final boolean hasOperation()
    {
        // TODO Auto-generated method stub
        return this._currentOperation != null;
    }

    @Override
    public final boolean stillValid(Player player)
    {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    protected final Component getDefaultName()
    {
        return Component.translatable(TGTranslations.Machine.Fabricator.NAME);
    }
    
    @Override
    protected final void read(CompoundTag tag, boolean clientPacket)
    {
        super.read(tag, clientPacket);
        
        if (tag.contains("CurrentOperation", Tag.TAG_COMPOUND))
            this._currentOperation = new FabricatorOperation(tag.getCompound("CurrentOperation"), clientPacket);
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
    protected final FabricatorMenu createMenu(int menuId, Inventory inventory)
    {
        return new FabricatorMenu(menuId, inventory, this, this._containerData, this._ownerId);
    }
    
    @Override
    protected final boolean fetchNextOperation()
    {
        this._contentsChanged = false;
        @Nullable
        IFabricatorRecipe nextRecipe = this._quickCheck.getRecipeFor(this, this.level).orElse(null);
        
        if (nextRecipe == null)
        {
            this._currentOperation = null;
            return false;
        }
        
        ItemStack recipeResultItem;
        
        ItemStack operationInputItem = ItemStack.EMPTY;
        ItemStack operationWiresItem = ItemStack.EMPTY;
        ItemStack operationRedstoneItem = ItemStack.EMPTY;
        ItemStack operationPlateItem = ItemStack.EMPTY;
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
            int inputConsumptionAmount = nextRecipe.testInputWithConsumption(this.getInputItem());
            int wiresConsumptionAmount = nextRecipe.testWireWithConsumption(this.getWireItem());
            int redstoneConsumptionAmount = nextRecipe.testRedstoneWithConsumption(this.getRedstoneItem());
            int plateConsumptionAmount = nextRecipe.testPlateWithConsumption(this.getPlateItem());
            
            if (inputConsumptionAmount <= 0 ||
                    wiresConsumptionAmount <= 0 ||
                    redstoneConsumptionAmount <= 0 ||
                    plateConsumptionAmount <= 0)
            {
                break;
            }
            
            recipeResultItem = nextRecipe.assemble(this, this.level.registryAccess());
            if (!StackUtils.canItemStacksStack(operationResultItem, recipeResultItem))
                break;
            
            if (!resultContainer.insertItemUnchecked(recipeResultItem, false).isEmpty())
                break;
            
            if (operationInputItem.isEmpty())
                operationInputItem = this.getInputItem().copyWithCount(inputConsumptionAmount);
            else
                operationInputItem.grow(inputConsumptionAmount);
            
            if (operationWiresItem.isEmpty())
                operationWiresItem = this.getWireItem().copyWithCount(wiresConsumptionAmount);
            else
                operationWiresItem.grow(wiresConsumptionAmount);
            
            if (operationRedstoneItem.isEmpty())
                operationRedstoneItem = this.getRedstoneItem().copyWithCount(redstoneConsumptionAmount);
            else
                operationRedstoneItem.grow(redstoneConsumptionAmount);
            
            if (operationPlateItem.isEmpty())
                operationPlateItem = this.getPlateItem().copyWithCount(plateConsumptionAmount);
            else
                operationPlateItem.grow(plateConsumptionAmount);
            
            if (operationResultItem.isEmpty())
                operationResultItem = recipeResultItem.copy();
            else
                operationResultItem.grow(recipeResultItem.getCount());
            
            this.getInputItem().shrink(inputConsumptionAmount);
            this.getWireItem().shrink(wiresConsumptionAmount);
            this.getRedstoneItem().shrink(redstoneConsumptionAmount);
            this.getPlateItem().shrink(plateConsumptionAmount);
            
            processAmount--;
            energyConsumptionMultiplier++;
        }
        
        if (energyConsumptionMultiplier == 0)
        {
            this._currentOperation = null;
            
            return false;
        }
        
        this._currentOperation = new FabricatorOperation(
                nextRecipe.getProcessingTime(),
                nextRecipe.getProcessingTime(),
                nextRecipe.getEnergyDrainPerTick() * energyConsumptionMultiplier,
                operationInputItem,
                operationWiresItem,
                operationRedstoneItem,
                operationPlateItem,
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
        
        if (progress == 1)
        {
            this.level.playSound(null, this.getBlockPos(), TGSounds.FABRICATOR_WORK.get(), SoundSource.BLOCKS, 0.5F, 1.0F);
        }
    }

    public static final int SLOT_INPUT = 0;
    public static final int SLOT_WIRES = 1;
    public static final int SLOT_REDSTONE = 2;
    public static final int SLOT_PLATE = 3;
    public static final int SLOT_RESULT = 4;
    public static final int SLOT_UPGRADE = 5;
    public static final int SLOT_COUNT = 6;
    
    private static final int ENERGY_CAPACITY = 100000;
    
    public static final void tick(Level level, BlockPos blockPos, BlockState blockState, FabricatorControllerBlockEntity blockEntity)
    {
        blockEntity.tick();
    }
    
    private static final class ContainerData extends AbstractMultiBlockMachineControllerBlockEntity.TickedContainerData implements FabricatorContainerData
    {
        private final FabricatorControllerBlockEntity _blockEntity;
        
        public ContainerData(FabricatorControllerBlockEntity blockEntity)
        {
            this._blockEntity = blockEntity;
        }
        
        @Override
        @NotNull
        public final FabricatorControllerBlockEntity getBlockEntity()
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
