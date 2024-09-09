package techguns2.block;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public final class NeonLightRotatableBlock extends Block
{
    public static final EnumProperty<Shape> SHAPE = EnumProperty.create("shape", Shape.class);
    
    public NeonLightRotatableBlock(Properties properties)
    {
        super(properties);
        
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(SHAPE, Shape.NORTH_SOUTH));
    }
    
    @Override
    public final BlockState getStateForPlacement(BlockPlaceContext context)
    {
        Shape shape;
        switch (context.getHorizontalDirection())
        {
            case NORTH:
            case SOUTH:
                shape = Shape.NORTH_SOUTH;
                break;
            case EAST:
            case WEST:
                shape = Shape.EAST_WEST;
                break;
            default:
                shape = Shape.EAST_WEST;
                break;
        }
        
        return this.defaultBlockState()
                .setValue(SHAPE, shape);
    }
    
    @Override
    public final BlockState rotate(BlockState blockState, Rotation rotation)
    {
        switch (rotation)
        {
            case CLOCKWISE_90:
                return blockState.setValue(SHAPE, blockState.getValue(SHAPE).getOpposite());
            case COUNTERCLOCKWISE_90:
                return blockState.setValue(SHAPE, blockState.getValue(SHAPE).getOpposite());
            default:
                return blockState;
        }
    }
    
    @Override
    protected final void createBlockStateDefinition(Builder<Block, BlockState> builder)
    {
        builder.add(SHAPE);
    }
    
    public static enum Shape implements StringRepresentable
    {
        NORTH_SOUTH("north_south"),
        EAST_WEST("east_west");
        
        private final String _value;
        
        private Shape(String value)
        {
            this._value = value;
        }

        @Override
        public final String getSerializedName()
        {
            return this._value;
        }
        
        public final Shape getOpposite()
        {
            if (this == EAST_WEST)
                return NORTH_SOUTH;
            else
                return EAST_WEST;
        }
        
    }
}
