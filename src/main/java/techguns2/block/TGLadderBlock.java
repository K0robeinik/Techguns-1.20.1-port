package techguns2.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class TGLadderBlock extends Block implements SimpleWaterloggedBlock
{
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    
    private static final VoxelShape SHAPE_EAST = Block.box(0.0D, 0.0D, 0.0D, 2.0D, 16.0D, 16.0D);
    private static final VoxelShape SHAPE_WEST = Block.box(14.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape SHAPE_SOUTH = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 2.0D);
    private static final VoxelShape SHAPE_NORTH = Block.box(0.0D, 0.0D, 14.0D, 16.0D, 16.0D, 16.0D);
    
    public TGLadderBlock(Properties properties)
    {
        super(properties);
        
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(WATERLOGGED, false));
    }
    
    @Override
    public final VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext)
    {
        switch (blockState.getValue(FACING))
        {
            case NORTH:
                return SHAPE_NORTH;
            case SOUTH:
                return SHAPE_SOUTH;
            case EAST:
                return SHAPE_EAST;
            case WEST:
                return SHAPE_WEST;
            default:
                return Shapes.block();
        }
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
        Direction facing = context.getClickedFace();
        if (facing == Direction.UP || facing == Direction.DOWN)
        {
            facing = context.getHorizontalDirection();
        }
        
        return this.defaultBlockState()
                .setValue(FACING, facing)
                .setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
    }
    
    @Override
    protected final void createBlockStateDefinition(Builder<Block, BlockState> builder)
    {
        builder.add(FACING, WATERLOGGED);
    }
    
    @Override
    public final BlockState rotate(BlockState blockState, Rotation rotation)
    {
        switch (rotation)
        {
            case CLOCKWISE_90:
                return blockState.setValue(FACING, blockState.getValue(FACING).getClockWise());
            case COUNTERCLOCKWISE_90:
                return blockState.setValue(FACING, blockState.getValue(FACING).getCounterClockWise());
            case CLOCKWISE_180:
                return blockState.setValue(FACING, blockState.getValue(FACING).getOpposite());
            default:
                return blockState;
        }
    }
    
    @Override
    public final BlockState mirror(BlockState blockState, Mirror mirror)
    {
        return blockState.setValue(FACING, mirror.mirror(blockState.getValue(FACING)));
    }
    
    
}
