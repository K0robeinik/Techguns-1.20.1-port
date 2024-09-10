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
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class TGLanternBlock extends Block implements SimpleWaterloggedBlock
{
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    
    private static final VoxelShape SHAPE = Block.box(4.0D, 2.0D, 4.0D, 12.0D, 14.0D, 12.0D);

    private static final VoxelShape SHAPE_UP = Block.box(4.0D, 14.0D, 4.0D, 12.0D, 16.0D, 12.0D);
    private static final VoxelShape SHAPE_DOWN = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 2.0D, 12.0D);
    private static final VoxelShape SHAPE_NORTH = Block.box(6.5D, 2.5D, 0.0D, 9.5D, 13.5D, 4.0D);
    private static final VoxelShape SHAPE_SOUTH = Block.box(6.5D, 2.5D, 12.0D, 9.5D, 13.5D, 16.0D);
    private static final VoxelShape SHAPE_WEST = Block.box(0.0D, 2.5D, 6.5D, 4.0D, 13.5D, 9.5D);
    private static final VoxelShape SHAPE_EAST = Block.box(12.0D, 2.5D, 6.5D, 16.0D, 13.5D, 9.5D);
    
    private final VoxelShape[] _shapes;
    
    public TGLanternBlock(Properties properties)
    {
        super(properties);
        
        this._shapes = makeShapes();
        
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(UP, false)
                .setValue(DOWN, false)
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(EAST, false)
                .setValue(WEST, false)
                .setValue(WATERLOGGED, false));
    }
    
    
    
    @Override
    public final VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext)
    {
        return this._shapes[getShapeIndex(
                blockState.getValue(UP),
                blockState.getValue(DOWN),
                blockState.getValue(NORTH),
                blockState.getValue(EAST),
                blockState.getValue(SOUTH),
                blockState.getValue(WEST))];
    }
    
    private static BlockState getUpdatedConnections(BlockState blockState, LevelReader levelReader, BlockPos blockPos)
    {
        return blockState
                .setValue(UP, canSupportCenter(levelReader, blockPos.above(), Direction.UP))
                .setValue(DOWN, canSupportCenter(levelReader, blockPos.below(), Direction.DOWN))
                .setValue(NORTH, canSupportRigidBlock(levelReader, blockPos.north()))
                .setValue(SOUTH, canSupportRigidBlock(levelReader, blockPos.south()))
                .setValue(EAST, canSupportRigidBlock(levelReader, blockPos.east()))
                .setValue(WEST, canSupportRigidBlock(levelReader, blockPos.west()));
    }
    
    @Override
    public final BlockState getStateForPlacement(BlockPlaceContext context)
    {
        Level level = context.getLevel();
        BlockPos blockPos = context.getClickedPos();
        
        FluidState fluidState = level.getFluidState(blockPos);
        
        return getUpdatedConnections(this.defaultBlockState(), level, blockPos)
                .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
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
    
    private static boolean hasConnections(BlockState blockState)
    {
        return blockState.getValue(UP) ||
                blockState.getValue(DOWN) ||
                blockState.getValue(NORTH) ||
                blockState.getValue(SOUTH) ||
                blockState.getValue(EAST) ||
                blockState.getValue(WEST);
    }
    
    @Override
    public final boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos)
    {
        return hasConnections(getUpdatedConnections(blockState, levelReader, blockPos));
    }
    
    @Override
    public final void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block,
            BlockPos neighborPos, boolean movedByPiston)
    {
        BlockState updatedConnectionsBlockState = getUpdatedConnections(blockState, level, blockPos);
        
        if (hasConnections(updatedConnectionsBlockState))
        {
            if (blockState != updatedConnectionsBlockState)
            {
                level.setBlock(blockPos, updatedConnectionsBlockState, UPDATE_NONE);
            }
            return;
        }
        
        level.removeBlock(blockPos, false);
    }
    
    @Override
    protected final void createBlockStateDefinition(Builder<Block, BlockState> builder)
    {
        builder.add(UP, DOWN, NORTH, EAST, SOUTH, WEST, WATERLOGGED);
    }
    
    private static VoxelShape[] makeShapes()
    {
        VoxelShape[] shapes = new VoxelShape[64];
        
        boolean up = false;
        do
        {
            boolean down = false;
            do
            {
                boolean north = false;
                do
                {
                    boolean east = false;
                    do
                    {
                        boolean south = false;
                        do
                        {
                            boolean west = false;
                            do
                            {
                                VoxelShape shape = SHAPE;
                                if (up)
                                    shape = Shapes.or(SHAPE, SHAPE_UP);
                                if (down)
                                    shape = Shapes.or(SHAPE, SHAPE_DOWN);
                                if (north)
                                    shape = Shapes.or(SHAPE, SHAPE_NORTH);
                                if (east)
                                    shape = Shapes.or(SHAPE, SHAPE_EAST);
                                if (south)
                                    shape = Shapes.or(SHAPE, SHAPE_SOUTH);
                                if (west)
                                    shape = Shapes.or(SHAPE, SHAPE_WEST);
                                
                                shapes[getShapeIndex(
                                        up,
                                        down,
                                        north,
                                        east,
                                        south,
                                        west)] = shape;
                                
                                west = !west;
                            }
                            while (west);
                            
                            south = !south;
                        }
                        while (south);
                        
                        east = !east;
                    }
                    while (east);
                    
                    north = !north;
                }
                while (north);
                
                down = !down;
            }
            while (down);
            
            up = !up;
        }
        while (up);
        
        return shapes;
    }
    
    private static int getShapeIndex(
            boolean up,
            boolean down,
            boolean north,
            boolean east,
            boolean south,
            boolean west)
    {
        return (up ? 0b000001 : 0) |
                (down ? 0b000010 : 0) |
                (north ? 0b000100 : 0) |
                (east ? 0b001000 : 0) |
                (south ? 0b010000 : 0) |
                (west ? 0b100000 : 0);
    }
}
