package techguns2.machine.reaction_chamber;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryManager;
import techguns2.TGBlockEntityTypes;
import techguns2.TGBlocks;
import techguns2.TGCustomRegistries;
import techguns2.TGRecipeTypes;
import techguns2.TGTranslations;
import techguns2.machine.AbstractMultiBlockMachineControllerBlockEntity;
import techguns2.util.DirtyableResult;
import techguns2.util.FluidStackContainer;
import techguns2.util.ItemStackContainer;

// top part: allows energy
// controller: allows energy, fluid, items
// bottom layer north, south, east, and west part: allows fluid & items

public final class ReactionChamberControllerBlockEntity extends AbstractMultiBlockMachineControllerBlockEntity<
    ReactionChamberPartBlockEntity,
    ReactionChamberControllerBlockEntity>
{
    private final LazyOptional<IFluidHandler> _fluidCapability;
    private final FluidStackContainer _fluidContainer;
    
    private final RecipeManager.CachedCheck<ReactionChamberControllerBlockEntity, IReactionChamberRecipe> _quickCheck;
    
    private int _intensity;
    private int _fluidLevel;
    
    @Nullable
    private ReactionChamberOperation _currentOperation;
    
    private final ContainerData _containerData;
    
    public ReactionChamberControllerBlockEntity(BlockPos blockPos, BlockState blockState)
    {
        super(
                ReactionChamberPartBlockEntity.class,
                3,
                4,
                3,
                TGBlockEntityTypes.REACTION_CHAMBER_CONTROLLER.get(),
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
                                return true;
                            default:
                                return false;
                        }
                    }
                    
                    @Override
                    public final boolean canTakeItemFromSlot(int slot, int amount)
                    {
                        return slot >= SLOT_RESULT1 && slot <= SLOT_RESULT4;
                    }
                });
        
        this._fluidContainer = new FluidStackContainer(FLUID_SLOT_COUNT)
        {
            @Override
            public final int getSlotCapacity(int slot)
            {
                if (slot == FLUID_SLOT_INPUT)
                    return ReactionChamberControllerBlockEntity.this.getFluidLevel() * 1000;
                else
                    return 0;
            }
            
            @Override
            public final boolean canFillFluidInSlot(int slot, @NotNull FluidStack fluidStack)
            {
                return slot == FLUID_SLOT_INPUT;
            }
            
            @Override
            public final boolean canDrainFluidFromSlot(int slot, int amount)
            {
                return false;
            }
            
            @Override
            protected final void onContentsChanged(int slot)
            {
                super.onContentsChanged(slot);
                
                ReactionChamberControllerBlockEntity.this._contentsChanged = true;
            }
        };
        
        this._fluidLevel = 0;
        this._quickCheck = RecipeManager.createCheck(TGRecipeTypes.REACTION_CHAMBER.get());
        
        this._fluidCapability = LazyOptional.of(() -> this._fluidContainer);
        
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
        return "Reaction Chamber";
    }
    
    public final int getIntensity()
    {
        return this._intensity;
    }
    
    public final void setIntensity(int intensity)
    {
        this._intensity = Math.max(0, Math.min(10, intensity));
    }
    
    public final int getFluidLevel()
    {
        return this._fluidLevel;
    }
    
    public final void setFluidLevel(int fluidLevel)
    {
        this._fluidLevel = Math.max(0, Math.min(10, fluidLevel));
    }
    
    public final ItemStack getInputItem()
    {
        return this._itemContainer.getStackInSlot(SLOT_INPUT);
    }
    
    public final FluidStack getInputFluid()
    {
        return this._fluidContainer.getStackInSlot(FLUID_SLOT_INPUT);
    }
    
    public final ItemStack getFocusItem()
    {
        return this._itemContainer.getStackInSlot(SLOT_FOCUS);
    }
    
    public final ItemStack getResultItem(int index)
    {
        if (index < 0 || index >= 4)
            return ItemStack.EMPTY;
        
        return this._itemContainer.getStackInSlot(index + SLOT_RESULT1);
    }
    
    @Override
    protected final boolean tryForm(Direction fromSide)
    {
        boolean failed = false;
        BlockPos origin = this.getBlockPos();
        BlockPos sideLeft;
        BlockPos sideRight;
        BlockPos forward;
        BlockPos forwardSideLeft;
        BlockPos forwardSideRight;
        BlockPos forward2;
        BlockPos forward2SideLeft;
        BlockPos forward2SideRight;
        
        switch (fromSide)
        {
            case NORTH:
                forward = origin.north();
                sideLeft = origin.west();
                sideRight = origin.east();
                forward2 = forward.north();
                forwardSideLeft = sideLeft.north();
                forwardSideRight = sideRight.north();
                forward2SideLeft = forwardSideLeft.north();
                forward2SideRight = forwardSideRight.north();
                break;
            case EAST:
                forward = origin.east();
                sideLeft = origin.north();
                sideRight = origin.south();
                forward2 = forward.east();
                forwardSideLeft = sideLeft.east();
                forwardSideRight = sideRight.east();
                forward2SideLeft = forwardSideLeft.east();
                forward2SideRight = forwardSideRight.east();
                break;
            case SOUTH:
                forward = origin.south();
                sideLeft = origin.east();
                sideRight = origin.west();
                forward2 = forward.south();
                forwardSideLeft = sideLeft.south();
                forwardSideRight = sideRight.south();
                forward2SideLeft = forwardSideLeft.south();
                forward2SideRight = forwardSideRight.south();
                break;
            case WEST:
                forward = origin.west();
                sideLeft = origin.south();
                sideRight = origin.north();
                forward2 = forward.west();
                forwardSideLeft = sideLeft.west();
                forwardSideRight = sideRight.west();
                forward2SideLeft = forwardSideLeft.west();
                forward2SideRight = forwardSideRight.west();
                break;
            default:
                forward = origin.north();
                sideLeft = origin.west();
                sideRight = origin.east();
                forward2 = forward.north();
                forwardSideLeft = sideLeft.north();
                forwardSideRight = sideRight.north();
                forward2SideLeft = forwardSideLeft.north();
                forward2SideRight = forwardSideRight.north();
                break;
        }
        
        BlockPos originAbove = origin.above();
        BlockPos sideLeftAbove = sideLeft.above();
        BlockPos sideRightAbove = sideRight.above();
        BlockPos forwardAbove = forward.above();
        BlockPos forwardSideLeftAbove = forwardSideLeft.above();
        BlockPos forwardSideRightAbove = forwardSideRight.above();
        BlockPos forward2Above = forward2.above();
        BlockPos forward2SideLeftAbove = forward2SideLeft.above();
        BlockPos forward2SideRightAbove = forward2SideRight.above();

        BlockPos originAbove2 = originAbove.above();
        BlockPos sideLeftAbove2 = sideLeftAbove.above();
        BlockPos sideRightAbove2 = sideRightAbove.above();
        BlockPos forwardAbove2 = forwardAbove.above();
        BlockPos forwardSideLeftAbove2 = forwardSideLeftAbove.above();
        BlockPos forwardSideRightAbove2 = forwardSideRightAbove.above();
        BlockPos forward2Above2 = forward2Above.above();
        BlockPos forward2SideLeftAbove2 = forward2SideLeftAbove.above();
        BlockPos forward2SideRightAbove2 = forward2SideRightAbove.above();

        BlockPos originAbove3 = originAbove2.above();
        BlockPos sideLeftAbove3 = sideLeftAbove2.above();
        BlockPos sideRightAbove3 = sideRightAbove2.above();
        BlockPos forwardAbove3 = forwardAbove2.above();
        BlockPos forwardSideLeftAbove3 = forwardSideLeftAbove2.above();
        BlockPos forwardSideRightAbove3 = forwardSideRightAbove2.above();
        BlockPos forward2Above3 = forward2Above2.above();
        BlockPos forward2SideLeftAbove3 = forward2SideLeftAbove2.above();
        BlockPos forward2SideRightAbove3 = forward2SideRightAbove2.above();
        
        int partPositionIndex = 0;
        
        if (this.level.getBlockState(sideLeft).getBlock() instanceof ReactionChamberHousingBlock)
        {
            this._partPositions[partPositionIndex] = sideLeft;
            partPositionIndex++;
        }
        else
        {
            // TODO: Show particles
            failed = true;
        }
        
        if (this.level.getBlockState(sideRight).getBlock() instanceof ReactionChamberHousingBlock)
        {
            this._partPositions[partPositionIndex] = sideRight;
            partPositionIndex++;
        }
        else
        {
            // TODO: Show particles
            failed = true;
        }
        
        if (this.level.getBlockState(forward).getBlock() instanceof ReactionChamberHousingBlock)
        {
            this._partPositions[partPositionIndex] = forward;
            partPositionIndex++;
        }
        else
        {
            // TODO: Show particles
            failed = true;
        }
        
        if (this.level.getBlockState(forwardSideLeft).getBlock() instanceof ReactionChamberHousingBlock)
        {
            this._partPositions[partPositionIndex] = forwardSideLeft;
            partPositionIndex++;
        }
        else
        {
            // TODO: Show particles
            failed = true;
        }
        
        if (this.level.getBlockState(forwardSideRight).getBlock() instanceof ReactionChamberHousingBlock)
        {
            this._partPositions[partPositionIndex] = forwardSideRight;
            partPositionIndex++;
        }
        else
        {
            // TODO: Show particles
            failed = true;
        }
        
        if (this.level.getBlockState(forward2).getBlock() instanceof ReactionChamberHousingBlock)
        {
            this._partPositions[partPositionIndex] = forward2;
            partPositionIndex++;
        }
        else
        {
            // TODO: Show particles
            failed = true;
        }
        
        if (this.level.getBlockState(forward2SideLeft).getBlock() instanceof ReactionChamberHousingBlock)
        {
            this._partPositions[partPositionIndex] = forward2SideLeft;
            partPositionIndex++;
        }
        else
        {
            // TODO: Show particles
            failed = true;
        }
        
        if (this.level.getBlockState(forward2SideRight).getBlock() instanceof ReactionChamberHousingBlock)
        {
            this._partPositions[partPositionIndex] = forward2SideRight;
            partPositionIndex++;
        }
        else
        {
            // TODO: Show particles
            failed = true;
        }

        if (this.level.getBlockState(originAbove).getBlock() instanceof ReactionChamberGlassBlock)
        {
            this._partPositions[partPositionIndex] = originAbove;
            partPositionIndex++;
        }
        else
        {
            // TODO: Show particles
            failed = true;
        }

        if (this.level.getBlockState(sideLeftAbove).getBlock() instanceof ReactionChamberGlassBlock)
        {
            this._partPositions[partPositionIndex] = sideLeftAbove;
            partPositionIndex++;
        }
        else
        {
            // TODO: Show particles
            failed = true;
        }
        
        if (this.level.getBlockState(sideRightAbove).getBlock() instanceof ReactionChamberGlassBlock)
        {
            this._partPositions[partPositionIndex] = sideRightAbove;
            partPositionIndex++;
        }
        else
        {
            // TODO: Show particles
            failed = true;
        }
        
        if (this.level.getBlockState(forwardAbove).getBlock() instanceof ReactionChamberGlassBlock)
        {
            this._partPositions[partPositionIndex] = forwardAbove;
            partPositionIndex++;
        }
        else
        {
            // TODO: Show particles
            failed = true;
        }
        
        if (this.level.getBlockState(forwardSideLeftAbove).getBlock() instanceof ReactionChamberGlassBlock)
        {
            this._partPositions[partPositionIndex] = forwardSideLeftAbove;
            partPositionIndex++;
        }
        else
        {
            // TODO: Show particles
            failed = true;
        }
        
        if (this.level.getBlockState(forwardSideRightAbove).getBlock() instanceof ReactionChamberGlassBlock)
        {
            this._partPositions[partPositionIndex] = forwardSideRightAbove;
            partPositionIndex++;
        }
        else
        {
            // TODO: Show particles
            failed = true;
        }
        
        if (this.level.getBlockState(forward2Above).getBlock() instanceof ReactionChamberGlassBlock)
        {
            this._partPositions[partPositionIndex] = forward2Above;
            partPositionIndex++;
        }
        else
        {
            // TODO: Show particles
            failed = true;
        }
        
        if (this.level.getBlockState(forward2SideLeftAbove).getBlock() instanceof ReactionChamberGlassBlock)
        {
            this._partPositions[partPositionIndex] = forward2SideLeftAbove;
            partPositionIndex++;
        }
        else
        {
            // TODO: Show particles
            failed = true;
        }
        
        if (this.level.getBlockState(forward2SideRightAbove).getBlock() instanceof ReactionChamberGlassBlock)
        {
            this._partPositions[partPositionIndex] = forward2SideRightAbove;
            partPositionIndex++;
        }
        else
        {
            // TODO: Show particles
            failed = true;
        }

        if (this.level.getBlockState(originAbove2).getBlock() instanceof ReactionChamberGlassBlock)
        {
            this._partPositions[partPositionIndex] = originAbove2;
            partPositionIndex++;
        }
        else
        {
            // TODO: Show particles
            failed = true;
        }

        if (this.level.getBlockState(sideLeftAbove2).getBlock() instanceof ReactionChamberGlassBlock)
        {
            this._partPositions[partPositionIndex] = sideLeftAbove2;
            partPositionIndex++;
        }
        else
        {
            // TODO: Show particles
            failed = true;
        }
        
        if (this.level.getBlockState(sideRightAbove2).getBlock() instanceof ReactionChamberGlassBlock)
        {
            this._partPositions[partPositionIndex] = sideRightAbove2;
            partPositionIndex++;
        }
        else
        {
            // TODO: Show particles
            failed = true;
        }
        
        if (this.level.getBlockState(forwardAbove2).getBlock() instanceof ReactionChamberGlassBlock)
        {
            this._partPositions[partPositionIndex] = forwardAbove2;
            partPositionIndex++;
        }
        else
        {
            // TODO: Show particles
            failed = true;
        }
        
        if (this.level.getBlockState(forwardSideLeftAbove2).getBlock() instanceof ReactionChamberGlassBlock)
        {
            this._partPositions[partPositionIndex] = forwardSideLeftAbove2;
            partPositionIndex++;
        }
        else
        {
            // TODO: Show particles
            failed = true;
        }
        
        if (this.level.getBlockState(forwardSideRightAbove2).getBlock() instanceof ReactionChamberGlassBlock)
        {
            this._partPositions[partPositionIndex] = forwardSideRightAbove2;
            partPositionIndex++;
        }
        else
        {
            // TODO: Show particles
            failed = true;
        }
        
        if (this.level.getBlockState(forward2Above2).getBlock() instanceof ReactionChamberGlassBlock)
        {
            this._partPositions[partPositionIndex] = forward2Above2;
            partPositionIndex++;
        }
        else
        {
            // TODO: Show particles
            failed = true;
        }
        
        if (this.level.getBlockState(forward2SideLeftAbove2).getBlock() instanceof ReactionChamberGlassBlock)
        {
            this._partPositions[partPositionIndex] = forward2SideLeftAbove2;
            partPositionIndex++;
        }
        else
        {
            // TODO: Show particles
            failed = true;
        }
        
        if (this.level.getBlockState(forward2SideRightAbove2).getBlock() instanceof ReactionChamberGlassBlock)
        {
            this._partPositions[partPositionIndex] = forward2SideRightAbove2;
            partPositionIndex++;
        }
        else
        {
            // TODO: Show particles
            failed = true;
        }

        if (this.level.getBlockState(originAbove3).getBlock() instanceof ReactionChamberHousingBlock)
        {
            this._partPositions[partPositionIndex] = originAbove3;
            partPositionIndex++;
        }
        else
        {
            // TODO: Show particles
            failed = true;
        }

        if (this.level.getBlockState(sideLeftAbove3).getBlock() instanceof ReactionChamberHousingBlock)
        {
            this._partPositions[partPositionIndex] = sideLeftAbove3;
            partPositionIndex++;
        }
        else
        {
            // TODO: Show particles
            failed = true;
        }
        
        if (this.level.getBlockState(sideRightAbove3).getBlock() instanceof ReactionChamberHousingBlock)
        {
            this._partPositions[partPositionIndex] = sideRightAbove3;
            partPositionIndex++;
        }
        else
        {
            // TODO: Show particles
            failed = true;
        }
        
        if (this.level.getBlockState(forwardAbove3).getBlock() instanceof ReactionChamberHousingBlock)
        {
            this._partPositions[partPositionIndex] = forwardAbove3;
            partPositionIndex++;
        }
        else
        {
            // TODO: Show particles
            failed = true;
        }
        
        if (this.level.getBlockState(forwardSideLeftAbove3).getBlock() instanceof ReactionChamberHousingBlock)
        {
            this._partPositions[partPositionIndex] = forwardSideLeftAbove3;
            partPositionIndex++;
        }
        else
        {
            // TODO: Show particles
            failed = true;
        }
        
        if (this.level.getBlockState(forwardSideRightAbove3).getBlock() instanceof ReactionChamberHousingBlock)
        {
            this._partPositions[partPositionIndex] = forwardSideRightAbove3;
            partPositionIndex++;
        }
        else
        {
            // TODO: Show particles
            failed = true;
        }
        
        if (this.level.getBlockState(forward2Above3).getBlock() instanceof ReactionChamberHousingBlock)
        {
            this._partPositions[partPositionIndex] = forward2Above3;
            partPositionIndex++;
        }
        else
        {
            // TODO: Show particles
            failed = true;
        }
        
        if (this.level.getBlockState(forward2SideLeftAbove3).getBlock() instanceof ReactionChamberHousingBlock)
        {
            this._partPositions[partPositionIndex] = forward2SideLeftAbove3;
            partPositionIndex++;
        }
        else
        {
            // TODO: Show particles
            failed = true;
        }
        
        if (this.level.getBlockState(forward2SideRightAbove3).getBlock() instanceof ReactionChamberHousingBlock)
        {
            this._partPositions[partPositionIndex] = forward2SideRightAbove3;
            partPositionIndex++;
        }
        else
        {
            // TODO: Show particles
            failed = true;
        }
        
        if (failed)
            return false;
        
        BlockState basePartBlockState = TGBlocks.REACTION_CHAMBER_PART.get().defaultBlockState();
        BlockState housingBlockState = basePartBlockState
                .setValue(ReactionChamberPartBlock.KIND, ReactionChamberPartBlock.Kind.HOUSING);
        BlockState glassBlockState = basePartBlockState
                .setValue(ReactionChamberPartBlock.KIND, ReactionChamberPartBlock.Kind.GLASS);
        BlockState topBlockState = basePartBlockState
                .setValue(ReactionChamberPartBlock.KIND, ReactionChamberPartBlock.Kind.TOP);
        BlockState portBlockState = basePartBlockState
                .setValue(ReactionChamberPartBlock.KIND, ReactionChamberPartBlock.Kind.PORT);
        
        this.level.setBlock(sideLeft, housingBlockState, Block.UPDATE_ALL);
        this.level.setBlock(sideRight, housingBlockState, Block.UPDATE_ALL);
        this.level.setBlock(forward, housingBlockState, Block.UPDATE_ALL);
        this.level.setBlock(forwardSideLeft, portBlockState, Block.UPDATE_ALL);
        this.level.setBlock(forwardSideRight, portBlockState, Block.UPDATE_ALL);
        this.level.setBlock(forward2, portBlockState, Block.UPDATE_ALL);
        this.level.setBlock(forward2SideLeft, housingBlockState, Block.UPDATE_ALL);
        this.level.setBlock(forward2SideRight, housingBlockState, Block.UPDATE_ALL);
        this.level.setBlock(originAbove, glassBlockState, Block.UPDATE_ALL);
        this.level.setBlock(sideLeftAbove, glassBlockState, Block.UPDATE_ALL);
        this.level.setBlock(sideRightAbove, glassBlockState, Block.UPDATE_ALL);
        this.level.setBlock(forwardAbove, glassBlockState, Block.UPDATE_ALL);
        this.level.setBlock(forwardSideLeftAbove, glassBlockState, Block.UPDATE_ALL);
        this.level.setBlock(forwardSideRightAbove, glassBlockState, Block.UPDATE_ALL);
        this.level.setBlock(forward2Above, glassBlockState, Block.UPDATE_ALL);
        this.level.setBlock(forward2SideLeftAbove, glassBlockState, Block.UPDATE_ALL);
        this.level.setBlock(forward2SideRightAbove, glassBlockState, Block.UPDATE_ALL);
        this.level.setBlock(originAbove2, glassBlockState, Block.UPDATE_ALL);
        this.level.setBlock(sideLeftAbove2, glassBlockState, Block.UPDATE_ALL);
        this.level.setBlock(sideRightAbove2, glassBlockState, Block.UPDATE_ALL);
        this.level.setBlock(forwardAbove2, glassBlockState, Block.UPDATE_ALL);
        this.level.setBlock(forwardSideLeftAbove2, glassBlockState, Block.UPDATE_ALL);
        this.level.setBlock(forwardSideRightAbove2, glassBlockState, Block.UPDATE_ALL);
        this.level.setBlock(forward2Above2, glassBlockState, Block.UPDATE_ALL);
        this.level.setBlock(forward2SideLeftAbove2, glassBlockState, Block.UPDATE_ALL);
        this.level.setBlock(forward2SideRightAbove2, glassBlockState, Block.UPDATE_ALL);
        this.level.setBlock(originAbove3, housingBlockState, Block.UPDATE_ALL);
        this.level.setBlock(sideLeftAbove3, housingBlockState, Block.UPDATE_ALL);
        this.level.setBlock(sideRightAbove3, housingBlockState, Block.UPDATE_ALL);
        this.level.setBlock(forwardAbove3, topBlockState, Block.UPDATE_ALL);
        this.level.setBlock(forwardSideLeftAbove3, housingBlockState, Block.UPDATE_ALL);
        this.level.setBlock(forwardSideRightAbove3, housingBlockState, Block.UPDATE_ALL);
        this.level.setBlock(forward2Above3, housingBlockState, Block.UPDATE_ALL);
        this.level.setBlock(forward2SideLeftAbove3, housingBlockState, Block.UPDATE_ALL);
        this.level.setBlock(forward2SideRightAbove3, housingBlockState, Block.UPDATE_ALL);
        
        return true;
    }
    
    @Override
    @Nullable 
    public final ReactionChamberOperation getCurrentOperation()
    {
        return this._currentOperation;
    }
    
    @Override
    public final boolean hasOperation()
    {
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
        return Component.translatable(TGTranslations.Machine.ReactionChamber.NAME);
    }
    
    @Override
    protected final void read(CompoundTag tag, boolean clientPacket)
    {
        super.read(tag, clientPacket);
        
        if (tag.contains("FluidInventory", Tag.TAG_COMPOUND))
            this._fluidContainer.deserializeNBT(tag);
        else
            this._fluidContainer.clear();
        
        if (tag.contains("Intensity", Tag.TAG_BYTE))
            this._intensity = Math.max(0, Math.min(10, tag.getByte("Intensity")));
        else
            this._intensity = 0;
        
        if (tag.contains("FluidLevel", Tag.TAG_BYTE))
            this._fluidLevel = Math.max(0, Math.min(10, tag.getByte("FluidLevel")));
        else
            this._fluidLevel = 0;
        
        if (tag.contains("CurrentOperation", Tag.TAG_COMPOUND))
            this._currentOperation = new ReactionChamberOperation(tag.getCompound("CurrentOperation"), clientPacket);
        else
            this._currentOperation = null;
    }
    
    @Override
    protected final void write(CompoundTag tag, boolean clientPacket)
    {
        super.write(tag, clientPacket);

        tag.put("FluidInventory", this._fluidContainer.serializeNBT());
        
        tag.putByte("Intensity", (byte)this._intensity);
        tag.putByte("FluidLevel", (byte)this._fluidLevel);
        
        if (this._currentOperation != null)
        {
            CompoundTag currentOperationData = new CompoundTag();
            this._currentOperation.serialize(currentOperationData, clientPacket);
            tag.put("CurrentOperation", currentOperationData);
        }
    }
    
    public final LazyOptional<IEnergyStorage> getEnergyCapability()
    {
        return this._energyStorageCapability;
    }
    
    public final LazyOptional<IItemHandler> getItemHandlerCapability()
    {
        return this._itemHandlerCapability;
    }
    
    public final LazyOptional<IFluidHandler> getFluidHandlerCapability()
    {
        return this._fluidCapability;
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
    protected final ReactionChamberMenu createMenu(int menuId, Inventory inventory)
    {
        return new ReactionChamberMenu(menuId, inventory, this, this._containerData, this._ownerId);
    }
    
    private final void doRiskResult(ReactionRisk risk)
    {
        
    }
    
    @Override
    protected final boolean fetchNextOperation()
    {
        this._contentsChanged = false;
        
        @Nullable
        IReactionChamberRecipe nextRecipe = this._quickCheck.getRecipeFor(this, this.level).orElse(null);
        
        if (nextRecipe == null)
        {
            this._currentOperation = null;
            return false;
        }
        
        ItemStack operationInputItem = ItemStack.EMPTY;
        FluidStack operationInputFluid = FluidStack.EMPTY;
        
        int energyConsumptionMultiplier = 0;
        int processAmount = 1;
        
        // copy existing results
        ItemStackContainer resultContainer = this._itemContainer.createView(SLOT_RESULT1, SLOT_RESULT4);
        
        ItemStackContainer operationResultContainer = new ItemStackContainer(resultContainer.getSlots());
        
        while (processAmount > 0)
        {
            int inputItemConsumptionAmount = nextRecipe.testInputItemWithConsumption(this.getInputItem());
            int inputFluidConsumptionAmount = nextRecipe.testInputFluidWithConsumption(this.getInputFluid());
            
            if (inputItemConsumptionAmount <= 0 ||
                    inputFluidConsumptionAmount < 0)
            {
                break;
            }
            
            // TODO: ensure there's enough space if (for example) the recipe randomly generates the results
            //       to prevent RNG manipulation.
            List<ItemStack> recipeResultItems = nextRecipe.assembleAll(this, this.level.registryAccess());
            
            if (!resultContainer.insertAllUnchecked(recipeResultItems).isEmpty())
                break;
            
            operationResultContainer.insertAllUnchecked(recipeResultItems);
            
            if (operationInputItem.isEmpty())
                operationInputItem = this.getInputItem().copyWithCount(inputItemConsumptionAmount);
            else
                operationInputItem.grow(inputItemConsumptionAmount);
            
            if (operationInputFluid.isEmpty())
            {
                FluidStack fluid = this.getInputFluid().copy();
                fluid.setAmount(inputFluidConsumptionAmount);
                operationInputFluid = fluid;
            }
            else
                operationInputFluid.grow(inputFluidConsumptionAmount);
            
            this.getInputItem().shrink(inputItemConsumptionAmount);
            this.getInputFluid().shrink(inputFluidConsumptionAmount);
            
            processAmount--;
            energyConsumptionMultiplier++;
        }
        
        if (energyConsumptionMultiplier == 0)
        {
            this._currentOperation = null;
            return false;
        }
        
        this._currentOperation = new ReactionChamberOperation(
                nextRecipe.getTicksPerCycle(),
                nextRecipe.getTicksPerCycle(),
                nextRecipe.getEnergyDrainPerTick() * energyConsumptionMultiplier,
                operationInputItem,
                operationInputFluid,
                List.of(
                        operationResultContainer.getStackInSlot(0),
                        operationResultContainer.getStackInSlot(1),
                        operationResultContainer.getStackInSlot(2),
                        operationResultContainer.getStackInSlot(3)),
                nextRecipe.getLaserFocus(),
                nextRecipe.getRisk(),
                nextRecipe.getStartingIntensity(),
                nextRecipe.getMaximumCycles(),
                nextRecipe.getRequiredCycles(),
                nextRecipe.getIntensityMargin(),
                nextRecipe.getInstability(),
                0,
                0);
        return true;
    }
    
    @Override
    protected final void onOperationComplete()
    {
        var operation = this._currentOperation;
        if (operation == null)
            return;
        
        if (operation.wasSuccessful())
        {
            this._itemContainer.insertAllUnchecked(SLOT_RESULT1, SLOT_RESULT4, operation.resultItems);
            return;
        }
        
        this.doRiskResult(operation.risk);
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
        boolean isDirty = !operation.isPaused;
        operation.isPaused = false;
        
        if (!operation.cycleComplete())
        {
            if (isDirty)
                return DirtyableResult.SUCCESSFUL_DIRTY;
            else
                return DirtyableResult.SUCCESSFUL;
        }
        
        operation.ticksRemaining = operation.totalTicks;
        operation.currentCycle++;
        
        if (operation.instability > 0 && this.level.getRandom().nextFloat() < operation.instability)
        {
            int intensityChange = 1;
            if (operation.intensityMargin > 1)
                intensityChange += this.level.getRandom().nextInt(operation.intensityMargin);
            
            if (this.level.getRandom().nextBoolean())
            {
                operation.requiredIntensity = Math.max(operation.requiredIntensity - intensityChange, 0);
            }
            else
            {
                operation.requiredIntensity = Math.max(operation.requiredIntensity + intensityChange, 10);
            }
            
            isDirty = true;
        }
        
        if (isDirty)
            return DirtyableResult.SUCCESSFUL_DIRTY;
        else
            return DirtyableResult.SUCCESSFUL;
    }
    
    @Override
    protected final void playAmbienceSounds()
    {
        
    }

    public static final int SLOT_INPUT = 0;
    public static final int SLOT_FOCUS = 1;
    public static final int SLOT_RESULT1 = 2;
    public static final int SLOT_RESULT2 = 3;
    public static final int SLOT_RESULT3 = 4;
    public static final int SLOT_RESULT4 = 5;
    public static final int SLOT_COUNT = 6;
    
    public static final int FLUID_SLOT_INPUT = 0;
    public static final int FLUID_SLOT_COUNT = 1;
    
    public static final int DATA_SLOT_FLUID_ID = AbstractMultiBlockMachineControllerBlockEntity.NEXT_DATA_SLOT;
    public static final int DATA_SLOT_FLUID_AMOUNT = DATA_SLOT_FLUID_ID + 1;
    public static final int DATA_SLOT_FOCUS = DATA_SLOT_FLUID_AMOUNT + 1;
    public static final int DATA_SLOT_FLUID_LEVEL = DATA_SLOT_FOCUS + 1;
    public static final int DATA_SLOT_INTENSITY = DATA_SLOT_FLUID_LEVEL + 1;
    public static final int DATA_SLOT_REACTION_RISK = DATA_SLOT_INTENSITY + 1;
    public static final int DATA_SLOT_REACTION_REQUIRED_INTENSITY = DATA_SLOT_REACTION_RISK + 1;
    public static final int DATA_SLOT_REACTION_CYCLE = DATA_SLOT_REACTION_REQUIRED_INTENSITY + 1;
    public static final int DATA_SLOT_REACTION_SUCCESSFUL_CYCLES = DATA_SLOT_REACTION_CYCLE + 1;
    public static final int DATA_SLOT_REACTION_REQUIRED_CYCLES = DATA_SLOT_REACTION_SUCCESSFUL_CYCLES + 1;
    public static final int DATA_SLOT_REACTION_MAXIMUM_CYCLES = DATA_SLOT_REACTION_REQUIRED_CYCLES + 1;
    public static final int DATA_SLOT_REACTION_CYCLE_TOTAL_TICKS = DATA_SLOT_REACTION_MAXIMUM_CYCLES + 1;
    public static final int DATA_SLOT_REACTION_CYCLE_TICKS_REMAINING = DATA_SLOT_REACTION_CYCLE_TOTAL_TICKS + 1;
    
    public static final int NEXT_DATA_SLOT = DATA_SLOT_REACTION_CYCLE_TICKS_REMAINING + 1;
    
    private static final int ENERGY_CAPACITY = 1000000;
    
    public static final void tick(Level level, BlockPos blockPos, BlockState blockState, ReactionChamberControllerBlockEntity blockEntity)
    {
        blockEntity.tick();
    }
    
    private static final class ContainerData extends AbstractMultiBlockMachineControllerBlockEntity.TickedContainerData implements ReactionChamberContainerData
    {
        private final ReactionChamberControllerBlockEntity _blockEntity;
        
        public ContainerData(ReactionChamberControllerBlockEntity blockEntity)
        {
            this._blockEntity = blockEntity;
        }
        
        @Override
        @NotNull
        public final ReactionChamberControllerBlockEntity getBlockEntity()
        {
            return this._blockEntity;
        }
        
        @Override
        public final int getReactionCycleTotalTicks()
        {
            var operation = this.getBlockEntity().getCurrentOperation();
            if (operation == null)
                return 0;
            else
                return operation.totalTicks;
        }
        
        @Override
        public final int getReactionCycleTicksRemaining()
        {
            var operation = this.getBlockEntity().getCurrentOperation();
            if (operation == null)
                return 0;
            else
                return operation.ticksRemaining;
        }
        
        @Override
        public final int getOperationTotalTicks()
        {
            var operation = this.getBlockEntity().getCurrentOperation();
            if (operation == null)
                return 0;
            else
                return operation.totalTicks * operation.maxCycleCount;
        }
        
        @Override
        public final int getOperationTicksRemaining()
        {
            var operation = this.getBlockEntity().getCurrentOperation();
            if (operation == null)
                return 0;
            else
                return (operation.totalTicks * (operation.maxCycleCount - operation.currentCycle - 1)) + operation.ticksRemaining;
        }
        
        @Override
        public final FluidStack getFluid()
        {
            return this.getBlockEntity().getInputFluid();
        }
        
        @Override
        public final void dumpFluid()
        {
            this.getBlockEntity()._fluidContainer.clear();
            this.getBlockEntity().setChanged();
        }
        
        @Override
        @Nullable 
        public final LaserFocus getLaserFocus()
        {
            var operation = this.getBlockEntity().getCurrentOperation();
            if (operation == null)
                return null;
            
            return operation.laserFocus;
        }
        
        @Override
        public final ReactionRisk getReactionRisk()
        {
            var operation = this.getBlockEntity().getCurrentOperation();
            if (operation == null)
                return ReactionRisks.NONE;
            
            return operation.risk;
        }
        
        @Override
        public final int getFluidLevel()
        {
            return this.getBlockEntity()._fluidLevel;
        }
        
        @Override
        public final void setFluidLevel(int fluidLevel)
        {
            this.getBlockEntity()._fluidLevel = Math.min(10, Math.max(fluidLevel, 0));
        }
        
        @Override
        public final int getIntensity()
        {
            return this.getBlockEntity()._intensity;
        }
        
        @Override
        public final void setIntensity(int intensity)
        {
            this.getBlockEntity()._intensity = Math.min(10, Math.max(intensity, 0));
        }
        
        @Override
        public final int getReactionRequiredIntensity()
        {
            var operation = this.getBlockEntity().getCurrentOperation();
            if (operation == null)
                return 0;
            
            return operation.requiredIntensity;
        }
        
        @Override
        public final int getReactionCycle()
        {
            var operation = this.getBlockEntity().getCurrentOperation();
            if (operation == null)
                return 0;
            
            return operation.currentCycle;
        }
        
        @Override
        public final int getReactionSuccessfulCycles()
        {
            var operation = this.getBlockEntity().getCurrentOperation();
            if (operation == null)
                return 0;
            
            return operation.successfulCycles;
        }
        
        @Override
        public final int getReactionRequiredCycles()
        {
            var operation = this.getBlockEntity().getCurrentOperation();
            if (operation == null)
                return 0;
            
            return operation.requiredCycleCount;
        }
        
        @Override
        public final int getReactionMaximumCycles()
        {
            var operation = this.getBlockEntity().getCurrentOperation();
            if (operation == null)
                return 0;
            
            return operation.requiredCycleCount;
        }
        
        @Override
        public final int get(int index)
        {
            switch (index)
            {
                case DATA_SLOT_FLUID_ID:
                    return RegistryManager.ACTIVE.getRegistry(ForgeRegistries.Keys.FLUIDS).getID(this.getBlockEntity().getInputFluid().getFluid());
                case DATA_SLOT_FLUID_AMOUNT:
                    return this.getBlockEntity().getInputFluid().getAmount();
                case DATA_SLOT_FOCUS:
                {
                    var operation = this.getBlockEntity().getCurrentOperation();
                    if (operation == null)
                        return -1;
                    
                    return RegistryManager.ACTIVE.getRegistry(TGCustomRegistries.LASER_FOCUSES).getID(operation.laserFocus);
                }
                case DATA_SLOT_FLUID_LEVEL:
                    return this.getBlockEntity().getFluidLevel();
                case DATA_SLOT_INTENSITY:
                    return this.getBlockEntity().getIntensity();
                case DATA_SLOT_REACTION_RISK:
                {
                    var operation = this.getBlockEntity().getCurrentOperation();
                    ReactionRisk risk;
                    if (operation == null)
                        risk = ReactionRisks.NONE;
                    else
                        risk = operation.risk;
                    
                    return RegistryManager.ACTIVE.getRegistry(TGCustomRegistries.REACTION_RISKS).getID(risk);
                }
                case DATA_SLOT_REACTION_REQUIRED_INTENSITY:
                {
                    var operation = this.getBlockEntity().getCurrentOperation();
                    if (operation == null)
                        return 0;
                    else
                        return operation.requiredIntensity;
                }
                case DATA_SLOT_REACTION_CYCLE:
                {
                    var operation = this.getBlockEntity().getCurrentOperation();
                    if (operation == null)
                        return 0;
                    else
                        return operation.currentCycle;
                }
                case DATA_SLOT_REACTION_SUCCESSFUL_CYCLES:
                {
                    var operation = this.getBlockEntity().getCurrentOperation();
                    if (operation == null)
                        return 0;
                    else
                        return operation.successfulCycles;
                }
                case DATA_SLOT_REACTION_REQUIRED_CYCLES:
                {
                    var operation = this.getBlockEntity().getCurrentOperation();
                    if (operation == null)
                        return 0;
                    else
                        return operation.requiredCycleCount;
                }
                case DATA_SLOT_REACTION_MAXIMUM_CYCLES:
                {
                    var operation = this.getBlockEntity().getCurrentOperation();
                    if (operation == null)
                        return 0;
                    else
                        return operation.maxCycleCount;
                }
                case DATA_SLOT_REACTION_CYCLE_TOTAL_TICKS:
                {
                    var operation = this.getBlockEntity().getCurrentOperation();
                    if (operation == null)
                        return 0;
                    else
                        return operation.totalTicks;
                }
                case DATA_SLOT_REACTION_CYCLE_TICKS_REMAINING:
                {
                    var operation = this.getBlockEntity().getCurrentOperation();
                    if (operation == null)
                        return 0;
                    else
                        return operation.ticksRemaining;
                }
                default:
                    return super.get(index);
            }
        }

        @Override
        public final void set(int index, int value)
        {
            switch (index)
            {
                case DATA_SLOT_FLUID_LEVEL:
                    this.getBlockEntity()._fluidLevel = Math.min(10, Math.max(value, 0));
                    break;
                case DATA_SLOT_INTENSITY:
                    this.getBlockEntity()._intensity = Math.min(10, Math.max(value, 0));
                    break;
                default:
                    return;
            }
            
        }

        @Override
        public final int getCount()
        {
            return NEXT_DATA_SLOT;
        }
    }
}
