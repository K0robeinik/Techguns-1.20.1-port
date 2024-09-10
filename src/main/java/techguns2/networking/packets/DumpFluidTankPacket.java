package techguns2.networking.packets;

import net.minecraft.network.FriendlyByteBuf;

public final class DumpFluidTankPacket
{
    public final int slotIndex;

    public DumpFluidTankPacket(int slotIndex)
    {
        this.slotIndex = slotIndex;
    }

    public final void write(FriendlyByteBuf buf)
    {
        buf.writeInt(this.slotIndex);
    }
    
    public static DumpFluidTankPacket read(FriendlyByteBuf buf)
    {
        return new DumpFluidTankPacket(buf.readInt());
    }
}
