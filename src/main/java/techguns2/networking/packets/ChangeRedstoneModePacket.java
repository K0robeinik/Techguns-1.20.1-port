package techguns2.networking.packets;

import net.minecraft.network.FriendlyByteBuf;
import techguns2.machine.RedstoneBehaviour;

public final class ChangeRedstoneModePacket
{
    public final RedstoneBehaviour newRedstoneMode;

    public ChangeRedstoneModePacket(RedstoneBehaviour newRedstoneMode)
    {
        this.newRedstoneMode = newRedstoneMode;
    }

    public final void write(FriendlyByteBuf buf)
    {
        buf.writeByte(this.newRedstoneMode.value());
    }
    
    public static ChangeRedstoneModePacket read(FriendlyByteBuf buf)
    {
        return new ChangeRedstoneModePacket(RedstoneBehaviour.get(buf.readByte()));
    }
}
