package techguns2.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import techguns2.TGBlockEntityTypes;
import techguns2.block.BioBlobBlock;

public final class BioBlobBlockEntity extends BlockEntity
{
    private int _ticks;
    
    public BioBlobBlockEntity(BlockPos blockPos, BlockState blockState)
    {
        super(TGBlockEntityTypes.BIO_BLOB.get(), blockPos, blockState);
    }
    
    @Override
    public final void load(CompoundTag tag)
    {
        this._ticks = tag.getInt("Ticks");
    }
    
    @Override
    protected final void saveAdditional(CompoundTag tag)
    {
        tag.putInt("Ticks", this._ticks);
    }
    
    @SuppressWarnings("deprecation")
    public final void tick()
    {
        if (this.level.isClientSide)
            return;
        
        this._ticks--;
        if (this._ticks > 0)
            return;
        
        BlockState state = this.getBlockState();
        int size = state.getValue(BioBlobBlock.SIZE);
        if (size <= 0)
        {
            this.level.setBlock(this.getBlockPos(), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
            return;
        }
        
        this._ticks = 60;
        state = state.setValue(BioBlobBlock.SIZE, size - 1);
        this.level.setBlock(this.getBlockPos(), state, Block.UPDATE_ALL);
        this.setBlockState(state);
    }
}
