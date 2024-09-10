package techguns2.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class CrateBlock extends Block
{
    private final VoxelShape SHAPE = Block.box(0.5, 0.0, 0.5, 15.5, 16.0, 15.5);
    
    public CrateBlock(Properties properties)
    {
        super(properties);
    }
    
    @Override
    public final VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext)
    {
        return SHAPE;
    }
    
    @Override
    public final VoxelShape getCollisionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos,
            CollisionContext collisionContext)
    {
        return SHAPE;
    }

}
