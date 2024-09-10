package techguns2.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import techguns2.machine.RedstoneBehaviour;

public abstract class BaseRedstoneBlockEntity extends BaseOwnedBlockEntity
{
    protected RedstoneBehaviour _redstoneBehaviour;
    protected boolean _isActive;

    protected BaseRedstoneBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState)
    {
        super(type, blockPos, blockState);
        
        this._redstoneBehaviour = RedstoneBehaviour.DEFAULT;
        this._isActive = true;
    }
    
    @Override
    public byte getFlags()
    {
        return (byte)(super.getFlags() | this._redstoneBehaviour.getFlagValue() |
                (this._isActive ?
                        IS_ACTIVE_FLAG :
                        0b00000000));
    }
    
    @Override
    public void setFlags(byte flagValue)
    {
        this._redstoneBehaviour = RedstoneBehaviour.getValueFromFlags(flagValue);
        this._isActive = (flagValue & IS_ACTIVE_FLAG) == IS_ACTIVE_FLAG;
        super.setFlags(flagValue);
    }
    
    public final boolean isActive()
    {
        return this._isActive;
    }
    
    public final RedstoneBehaviour getRestoneBehaviour()
    {
        return this._redstoneBehaviour;
    }
    
    public final void setRestoneBehaviour(RedstoneBehaviour behaviour)
    {
        if (behaviour == null)
            behaviour = RedstoneBehaviour.DEFAULT;
        this._redstoneBehaviour = behaviour;
    }
    
    public void refreshRedstoneActiveState()
    {
        if (!this.hasLevel())
            return;
        
        int signalStrength = this.level.getBestNeighborSignal(this.getBlockPos());
        boolean wasActive = this._isActive;
        this._isActive = this._redstoneBehaviour.test(signalStrength);
        if (wasActive != this._isActive)
        {
            this.notifyUpdate();
        }
    }

    public static final byte IS_ACTIVE_FLAG = (byte)0b00100000;
}
