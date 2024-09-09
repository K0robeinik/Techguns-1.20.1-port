package techguns2.networking.packets;

import net.minecraft.network.FriendlyByteBuf;

public final class ChangeReactionFluidLevelPacket
{
    public final byte fluidLevel;

    public ChangeReactionFluidLevelPacket(int fluidLevel)
    {
        this.fluidLevel = (byte)Math.max(0, Math.min(10, fluidLevel));
    }

    public final void write(FriendlyByteBuf buf)
    {
        buf.writeByte(this.fluidLevel);
    }
    
    public static ChangeReactionFluidLevelPacket read(FriendlyByteBuf buf)
    {
        return new ChangeReactionFluidLevelPacket(buf.readByte());
    }
}
