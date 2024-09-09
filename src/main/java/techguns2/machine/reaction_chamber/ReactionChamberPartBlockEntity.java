package techguns2.machine.reaction_chamber;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import techguns2.TGBlockEntityTypes;
import techguns2.TGBlocks;
import techguns2.machine.AbstractMultiBlockMachinePartBlockEntity;

public final class ReactionChamberPartBlockEntity extends AbstractMultiBlockMachinePartBlockEntity<
    ReactionChamberPartBlockEntity,
    ReactionChamberControllerBlockEntity>
{

    public ReactionChamberPartBlockEntity(BlockPos blockPos, BlockState blockState)
    {
        super(ReactionChamberControllerBlockEntity.class, TGBlockEntityTypes.REACTION_CHAMBER_PART.get(), blockPos, blockState);
    }
    
    @Override
    public final BlockState getUnformedBlockState()
    {
        switch (this.getBlockState().getValue(ReactionChamberPartBlock.KIND))
        {
            case HOUSING:
            case PORT:
            case TOP:
                return TGBlocks.REACTION_CHAMBER_HOUSING.get().defaultBlockState();
            case GLASS:
                return TGBlocks.REACTION_CHAMBER_GLASS.get().defaultBlockState();
            default:
                return Blocks.AIR.defaultBlockState();
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    @NotNull 
    public final <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap)
    {
        switch (this.getBlockState().getValue(ReactionChamberPartBlock.KIND))
        {
            case TOP:
            {
                if (cap == ForgeCapabilities.ENERGY)
                {
                    ReactionChamberControllerBlockEntity controller = this.getController();
                    if (controller != null)
                        return (LazyOptional<T>)controller.getEnergyCapability();
                }
                
                return super.getCapability(cap);
            }
            case PORT:
                if (cap == ForgeCapabilities.FLUID_HANDLER)
                {
                    ReactionChamberControllerBlockEntity controller = this.getController();
                    if (controller != null)
                        return (LazyOptional<T>)controller.getFluidHandlerCapability();
                }
                else if (cap == ForgeCapabilities.ITEM_HANDLER)
                {
                    ReactionChamberControllerBlockEntity controller = this.getController();
                    if (controller != null)
                        return (LazyOptional<T>)controller.getItemHandlerCapability();
                }
                
                return super.getCapability(cap);
            default:
                return super.getCapability(cap);
        }
    }
}
