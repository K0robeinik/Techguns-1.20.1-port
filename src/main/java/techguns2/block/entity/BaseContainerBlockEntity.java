package techguns2.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import techguns2.networking.BaseSyncedContainerBlockEntity;

public abstract class BaseContainerBlockEntity extends BaseSyncedContainerBlockEntity
{
    private boolean _chunkUnloaded;
    
    protected BaseContainerBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState)
    {
        super(type, blockPos, blockState);
    }
    
    public final boolean isChunkUnloaded()
    {
        return this._chunkUnloaded;
    }
    
    protected void write(CompoundTag tag, boolean clientPacket)
    {
        super.saveAdditional(tag);
    }
    
    protected void read(CompoundTag tag, boolean clientPacket)
    {
        super.load(tag);
    }
    
    @Override
    public final void load(CompoundTag tag)
    {
        this.read(tag, false);
    }
    
    @Override
    protected final void saveAdditional(CompoundTag tag)
    {
        this.write(tag, false);
    }
    
    @Override
    public final void readClient(CompoundTag tag)
    {
        this.read(tag, true);
    }
    
    @Override
    public final CompoundTag writeClient(CompoundTag tag)
    {
        this.write(tag, true);
        return tag;
    }
    
    @Override
    public void onChunkUnloaded()
    {
        super.onChunkUnloaded();
        this._chunkUnloaded = true;
    }
    
    @SuppressWarnings("deprecation")
    public void refreshBlockState()
    {
        this.setBlockState(this.getLevel().getBlockState(this.getBlockPos()));
    }
    
    public void dropContents()
    {
        Containers.dropContents(this.level, this.getBlockPos(), this);
    }
}
