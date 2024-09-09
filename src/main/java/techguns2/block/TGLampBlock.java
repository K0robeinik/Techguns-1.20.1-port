package techguns2.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class TGLampBlock extends Block implements SimpleWaterloggedBlock
{
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    
    public static final VoxelShape DOWN_SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 3.0D, 12.0D);
    public static final VoxelShape UP_SHAPE = Block.box(4.0D, 13.0D, 4.0D, 12.0D, 16.0D, 12.0D);
    public static final VoxelShape NORTH_SHAPE = Block.box(4.0D, 4.0D, 0.0D, 12.0D, 12.0D, 3.0D);
    public static final VoxelShape SOUTH_SHAPE = Block.box(4.0D, 4.0D, 13.0D, 12.0D, 12.0D, 16.0D);

    public static final VoxelShape WEST_SHAPE = Block.box(0.0D, 4.0D, 4.0D, 3.0D, 12.0D, 12.0D);
    public static final VoxelShape EAST_SHAPE = Block.box(13.0D, 4.0D, 4.0D, 16.0D, 12.0D, 12.0D);
    
    
    public TGLampBlock(Properties properties)
    {
        super(properties);
        
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.UP)
                .setValue(WATERLOGGED, false));
    }
    
    @Override
    public final VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext)
    {
        switch (blockState.getValue(FACING))
        {
            case UP:
                return UP_SHAPE;
            case DOWN:
                return DOWN_SHAPE;
            case NORTH:
                return NORTH_SHAPE;
            case SOUTH:
                return SOUTH_SHAPE;
            case EAST:
                return EAST_SHAPE;
            case WEST:
                return WEST_SHAPE;
            default:
                return UP_SHAPE;
        }
    }
    
    @Override
    public final boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos)
    {
        Direction direction = blockState.getValue(FACING);
        
        return canSupportRigidBlock(levelReader, blockPos.relative(direction));
    }
    
    @Override
    public final void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block,
            BlockPos neighborPos, boolean movedByPiston)
    {
        if (this.canSurvive(blockState, level, blockPos))
            return;
        
        level.removeBlock(blockPos, false);
    }
    
    @Override
    public final boolean propagatesSkylightDown(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos)
    {
        return !blockState.getValue(WATERLOGGED);
    }
    
    @Override
    public final boolean isPathfindable(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos,
            PathComputationType type)
    {
        return false;
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public final FluidState getFluidState(BlockState blockState)
    {
        if (blockState.getValue(WATERLOGGED))
            return Fluids.WATER.getSource(false);
        else
            return super.getFluidState(blockState);
    }

    @Override
    public final BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return this.defaultBlockState()
                .setValue(FACING, context.getNearestLookingDirection().getOpposite())
                .setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
    }
    
    @Override
    protected final void createBlockStateDefinition(Builder<Block, BlockState> builder)
    {
        builder.add(FACING, WATERLOGGED);
    }
}
