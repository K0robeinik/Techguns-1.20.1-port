package techguns2.networking;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.network.PacketDistributor;

public abstract class BaseSyncedContainerBlockEntity extends BaseContainerBlockEntity
{
    protected BaseSyncedContainerBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState)
    {
        super(type, blockPos, blockState);
    }
    
    @Override
    public CompoundTag getUpdateTag()
    {
        return this.writeClient(new CompoundTag());
    }
    
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket()
    {
        return ClientboundBlockEntityDataPacket.create(this);
    }
    
    @Override
    public void handleUpdateTag(CompoundTag tag)
    {
        this.readClient(tag);
    }
    
    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt)
    {
        CompoundTag tag = pkt.getTag();
        this.readClient(tag == null ? new CompoundTag() : tag);
    }
    
    public void readClient(CompoundTag tag)
    {
        this.load(tag);
    }
    
    public CompoundTag writeClient(CompoundTag tag)
    {
        this.saveAdditional(tag);
        return tag;
    }
    
    public void sendData()
    {
        if (this.level instanceof ServerLevel serverLevel)
            serverLevel.getChunkSource().blockChanged(this.getBlockPos());
    }
    
    public void notifyUpdate()
    {
        this.setChanged();
        this.sendData();
    }
    
    public PacketDistributor.PacketTarget packetTarget()
    {
        return PacketDistributor.TRACKING_CHUNK.with(this::containedChunk);
    }
    
    public LevelChunk containedChunk()
    {
        return this.level.getChunkAt(this.worldPosition);
    }
    
    @SuppressWarnings("deprecation")
    public HolderGetter<Block> blockHolderGetter()
    {
        if (this.level != null)
            return (HolderGetter<Block>)this.level.holderLookup(Registries.BLOCK);
        
        return (HolderGetter<Block>)BuiltInRegistries.BLOCK.asLookup();
    }

}
