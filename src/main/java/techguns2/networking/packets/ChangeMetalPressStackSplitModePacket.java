package techguns2.networking.packets;

import net.minecraft.network.FriendlyByteBuf;

public final class ChangeMetalPressStackSplitModePacket
{
    public final boolean enabled;
    
    public ChangeMetalPressStackSplitModePacket(boolean enabled)
    {
        this.enabled = enabled;
    }

    public final void write(FriendlyByteBuf buf)
    {
        buf.writeBoolean(this.enabled);
    }
    
    public static ChangeMetalPressStackSplitModePacket read(FriendlyByteBuf buf)
    {
        return new ChangeMetalPressStackSplitModePacket(buf.readBoolean());
    }
}