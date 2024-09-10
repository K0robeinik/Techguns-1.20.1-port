package techguns2.block.entity;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import techguns2.api.ITGPlayerOwnable;
import techguns2.api.SecurityMode;

public abstract class BaseOwnedBlockEntity extends BaseContainerBlockEntity implements ITGPlayerOwnable
{
    protected SecurityMode _securityMode;
    @Nullable
    protected UUID _ownerId;
    
    protected BaseOwnedBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState)
    {
        super(type, blockPos, blockState);
        
        this._securityMode = SecurityMode.DEFAULT;
        this._ownerId = null;
    }
    
    @Nullable
    public final UUID getOwnerID()
    {
        return this._ownerId;
    }
    
    public final void setOwner(@Nullable Player player)
    {
        if (player == null)
            this.setOwnerId(null);
        else
            this.setOwnerId(player.getGameProfile().getId());
    }
    
    public final void setOwnerId(@Nullable UUID newOwner)
    {
        this._ownerId = newOwner;
    }
    
    @Override
    public final boolean isOwnedBy(Player player)
    {
        if (this._ownerId == null)
            return true;
        else
            return this._ownerId.equals(player.getGameProfile().getId());
    }
    
    /**
     * Use {@link getSecurityMode} instead.
     * @deprecated
     */
    @Override
    @Deprecated
    public final SecurityMode getSecurity()
    {
        return this.getSecurityMode();
    }
    
    public final SecurityMode getSecurityMode()
    {
        return this._securityMode;
    }
    
    public final void setSecurityMode(SecurityMode mode)
    {
        if (mode == null)
            mode = SecurityMode.DEFAULT;
        
        this._securityMode = mode;
    }
    
    public byte getFlags()
    {
        return this._securityMode.getFlagValue();
    }
    
    public void setFlags(byte flagValue)
    {
        this._securityMode = SecurityMode.getValueFromFlags(flagValue);
    }
    
    @Override
    public final boolean canOpen(Player player)
    {
        if (!super.canOpen(player))
            return false;
        
        return this.canPlayerAccess(player);
    }
    
    public final boolean canPlayerAccess(Player player)
    {
        return this._ownerId == null ||
                this._securityMode.test(player, this._ownerId);
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket)
    {
        super.write(tag, clientPacket);
        
        tag.putByte("Flags", this.getFlags());
        if (this._ownerId != null)
            tag.putUUID("Owner", this._ownerId);
    }
    
    @Override
    protected void read(CompoundTag tag, boolean clientPacket)
    {
        super.read(tag, clientPacket);
        
        this.setFlags(tag.getByte("Flags"));
        
        // UUIDs are stored as an integer array
        if (tag.contains("Owner", Tag.TAG_INT_ARRAY))
            this._ownerId = tag.getUUID("Owner");
        else
            this._ownerId = null;
    }

}
