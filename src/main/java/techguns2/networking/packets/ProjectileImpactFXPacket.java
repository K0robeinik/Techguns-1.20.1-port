package techguns2.networking.packets;

import java.util.BitSet;

import net.minecraft.network.FriendlyByteBuf;

public final class ProjectileImpactFXPacket
{
    public final short soundType;
    public final double locationX;
    public final double locationY;
    public final double locationZ;
    public final float pitch;
    public final float yaw;
    public final boolean incendiary;
    
    public ProjectileImpactFXPacket(
            short soundType,
            double locationX,
            double locationY,
            double locationZ,
            float pitch,
            float yaw,
            boolean incendiary)
    {
        this.soundType = soundType;
        this.locationX = locationX;
        this.locationY = locationY;
        this.locationZ = locationZ;
        this.pitch = pitch;
        this.yaw = yaw;
        this.incendiary = incendiary;
    }

    public final void write(FriendlyByteBuf buf)
    {
        buf.writeShort(this.soundType);
        buf.writeDouble(this.locationX);
        buf.writeDouble(this.locationY);
        buf.writeDouble(this.locationZ);
        buf.writeFloat(this.pitch);
        buf.writeFloat(this.yaw);
        BitSet bitSet = new BitSet(1);
        bitSet.set(INCENDIARY_FLAG_BIT, this.incendiary);
        buf.writeByte(bitSet.toByteArray()[0]);
    }
    
    public static ProjectileImpactFXPacket read(FriendlyByteBuf buf)
    {
        short soundType = buf.readShort();
        double locationX = buf.readDouble();
        double locationY = buf.readDouble();
        double locationZ = buf.readDouble();
        float pitch = buf.readFloat();
        float yaw = buf.readFloat();
        

        BitSet bitSet = BitSet.valueOf(new byte[] { buf.readByte() });
        boolean incendiary = bitSet.get(INCENDIARY_FLAG_BIT);
        
        return new ProjectileImpactFXPacket(soundType, locationX, locationY, locationZ, pitch, yaw, incendiary);
    }

    private static final int INCENDIARY_FLAG_BIT = 0;
}
