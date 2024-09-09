package techguns2.machine.reaction_chamber;

import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import techguns2.machine.AbstractMultiBlockMachinePartBlock;

public final class ReactionChamberPartBlock extends AbstractMultiBlockMachinePartBlock<
    ReactionChamberPartBlockEntity,
    ReactionChamberControllerBlockEntity>
{
    public static final EnumProperty<Kind> KIND = EnumProperty.create("kind", Kind.class);

    public ReactionChamberPartBlock(Properties properties)
    {
        super(ReactionChamberPartBlockEntity.class, properties);
        
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(KIND, Kind.HOUSING));
    }
    
    @Override
    protected final void createBlockStateDefinition(Builder<Block, BlockState> builder)
    {
        builder.add(KIND);
    }
    
    @Override
    public final ReactionChamberPartBlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState)
    {
        return new ReactionChamberPartBlockEntity(blockPos, blockState);
    }
    
    public static enum Kind implements StringRepresentable
    {
        HOUSING("housing"),
        TOP("top"),
        PORT("port"),
        GLASS("glass");
        
        private final String _value;
        
        private Kind(String value)
        {
            this._value = value;
        }
        
        @Override
        public final String getSerializedName()
        {
            return this._value;
        }
    }
}
