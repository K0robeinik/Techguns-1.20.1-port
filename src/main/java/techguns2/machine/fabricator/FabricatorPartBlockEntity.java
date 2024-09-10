package techguns2.machine.fabricator;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import techguns2.TGBlockEntityTypes;
import techguns2.TGBlocks;
import techguns2.machine.AbstractMultiBlockMachinePartBlockEntity;

public final class FabricatorPartBlockEntity extends AbstractMultiBlockMachinePartBlockEntity<
    FabricatorPartBlockEntity,
    FabricatorControllerBlockEntity>
{
    public FabricatorPartBlockEntity(BlockPos blockPos, BlockState blockState)
    {
        super(
                FabricatorControllerBlockEntity.class,
                TGBlockEntityTypes.FABRICATOR_PART.get(),
                blockPos,
                blockState);
        
        this._controllerBlockPos = blockPos;
    }
    
    @Override
    public final BlockState getUnformedBlockState()
    {
        switch (this.getBlockState().getValue(FabricatorPartBlock.KIND))
        {
            case HOUSING:
                return TGBlocks.FABRICATOR_HOUSING.get().defaultBlockState();
            case GLASS:
                return TGBlocks.FABRICATOR_GLASS.get().defaultBlockState();
            default:
                return Blocks.AIR.defaultBlockState();
        }
    }

    @Override
    @NotNull 
    public final <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap)
    {
        if (this.getBlockState().getValue(FabricatorPartBlock.KIND) == FabricatorPartBlock.Kind.HOUSING)
        {
            FabricatorControllerBlockEntity controller = this.getController();
            if (controller != null)
                return controller.getCapability(cap);
            
        }
        
        return super.getCapability(cap);
    }
}
