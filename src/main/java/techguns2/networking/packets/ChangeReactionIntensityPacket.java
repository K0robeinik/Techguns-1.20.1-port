package techguns2.networking.packets;

import net.minecraft.network.FriendlyByteBuf;

public final class ChangeReactionIntensityPacket
{
    public final byte intensity;

    public ChangeReactionIntensityPacket(int intensity)
    {
        this.intensity = (byte)Math.max(0, Math.min(10, intensity));
    }

    public final void write(FriendlyByteBuf buf)
    {
        buf.writeByte(this.intensity);
    }
    
    public static ChangeReactionIntensityPacket read(FriendlyByteBuf buf)
    {
        return new ChangeReactionIntensityPacket(buf.readByte());
    }
}
