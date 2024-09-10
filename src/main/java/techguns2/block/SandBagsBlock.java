package techguns2.block;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class SandBagsBlock extends Block
{
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    
    private final Map<BlockState, VoxelShape> _shapes;
    
    public SandBagsBlock(Properties properties)
    {
        super(properties);
        
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH, false)
                .setValue(EAST, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false));
        
        this._shapes = makeShapes(this.defaultBlockState());
    }
    
    @Override
    public final VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext)
    {
        return this._shapes.get(blockState);
    }
    
    @Override
    public final VoxelShape getCollisionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos,
            CollisionContext collisionContext)
    {
        return this._shapes.get(blockState);
    }
    
    @Override
    public final boolean isPathfindable(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos,
            PathComputationType type)
    {
        return false;
    }
    
    @Override
    public final BlockState getStateForPlacement(BlockPlaceContext context)
    {
        LevelReader levelReader = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        
        BlockPos north = clickedPos.north();
        BlockPos east = clickedPos.east();
        BlockPos south = clickedPos.south();
        BlockPos west = clickedPos.west();
        
        BlockState northBlock = levelReader.getBlockState(north);
        BlockState eastBlock = levelReader.getBlockState(east);
        BlockState southBlock = levelReader.getBlockState(south);
        BlockState westBlock = levelReader.getBlockState(west);
        
        return this.defaultBlockState()
                .setValue(NORTH, connectsTo(northBlock, northBlock.isFaceSturdy(levelReader, north, Direction.SOUTH), Direction.SOUTH))
                .setValue(EAST, connectsTo(eastBlock, eastBlock.isFaceSturdy(levelReader, east, Direction.WEST), Direction.WEST))
                .setValue(SOUTH, connectsTo(southBlock, southBlock.isFaceSturdy(levelReader, south, Direction.NORTH), Direction.NORTH))
                .setValue(WEST, connectsTo(westBlock, westBlock.isFaceSturdy(levelReader, west, Direction.EAST), Direction.EAST));
    }
    
    @Override
    protected final void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(NORTH, EAST, SOUTH, WEST);
    }
    
    @Override
    public final BlockState rotate(BlockState blockState, Rotation rotation)
    {
        switch (rotation)
        {
            case CLOCKWISE_90:
                return blockState
                        .setValue(NORTH, blockState.getValue(WEST))
                        .setValue(EAST, blockState.getValue(NORTH))
                        .setValue(SOUTH, blockState.getValue(EAST))
                        .setValue(WEST, blockState.getValue(SOUTH));
            case COUNTERCLOCKWISE_90:
                return blockState
                        .setValue(NORTH, blockState.getValue(EAST))
                        .setValue(EAST, blockState.getValue(SOUTH))
                        .setValue(SOUTH, blockState.getValue(WEST))
                        .setValue(WEST, blockState.getValue(NORTH));
            case CLOCKWISE_180:
                return blockState
                        .setValue(NORTH, blockState.getValue(SOUTH))
                        .setValue(EAST, blockState.getValue(WEST))
                        .setValue(SOUTH, blockState.getValue(NORTH))
                        .setValue(WEST, blockState.getValue(EAST));
            default:
                return blockState;
        }
    }
    
    @Override
    public final BlockState mirror(BlockState blockState, Mirror mirror)
    {
        switch (mirror)
        {
            case FRONT_BACK:
                return blockState
                        .setValue(EAST, blockState.getValue(WEST))
                        .setValue(WEST, blockState.getValue(EAST));
            case LEFT_RIGHT:
                return blockState
                        .setValue(NORTH, blockState.getValue(SOUTH))
                        .setValue(SOUTH, blockState.getValue(NORTH));
            default:
                return blockState;
        }
    }
    
    private static boolean connectsTo(BlockState blockState, boolean isSturdy, Direction direction)
    {
        if (blockState.is(BlockTags.WALLS) || (!isExceptionForConnection(blockState) && isSturdy))
            return true;
        
        Block block = blockState.getBlock();
        return block instanceof IronBarsBlock || (block instanceof FenceGateBlock && FenceGateBlock.connectsToDirection(blockState, direction));
    }
    
    private static Map<BlockState, VoxelShape> makeShapes(BlockState defaultState)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> result = ImmutableMap.builder();
        
        int value = 0;
        while (value < 16)
        {
            boolean south = (value & 0b0001) == 0b0001;
            boolean west = (value & 0b0010) == 0b0010;
            boolean north = (value & 0b0100) == 0b0100;
            boolean east = (value & 0b1000) == 0b1000;
            
            VoxelShape shape = Block.box(
                    west ? 0.0 : 4.0,
                    0.0,
                    north ? 0.0 : 4.0,
                    east ? 16.0 : 12.0,
                    16.0,
                    south ? 16.0 : 12.0);
            
            BlockState state = defaultState
                    .setValue(NORTH, north)
                    .setValue(EAST, east)
                    .setValue(SOUTH, south)
                    .setValue(WEST, west);
            
            result.put(state, shape);
            value++;
        }
        
        return result.build();
    }
}
