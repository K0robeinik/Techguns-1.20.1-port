package techguns2.networking.packets;

import net.minecraft.network.FriendlyByteBuf;
import techguns2.api.SecurityMode;

public final class ChangeSecurityModePacket
{
    public final SecurityMode newSecurityMode;

    public ChangeSecurityModePacket(SecurityMode newSecurityMode)
    {
        this.newSecurityMode = newSecurityMode;
    }

    public final void write(FriendlyByteBuf buf)
    {
        buf.writeByte(this.newSecurityMode.value());
    }
    
    public static ChangeSecurityModePacket read(FriendlyByteBuf buf)
    {
        return new ChangeSecurityModePacket(SecurityMode.get(buf.readByte()));
    }
}
