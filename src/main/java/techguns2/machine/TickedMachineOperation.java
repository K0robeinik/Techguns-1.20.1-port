package techguns2.machine;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;

public abstract class TickedMachineOperation extends MachineOperation
{
    public final int totalTicks;
    public int ticksRemaining;
    public final int energyDrainPerTick;

    protected TickedMachineOperation(FriendlyByteBuf buf, boolean fromNetwork)
    {
        super(buf, fromNetwork);
        
        this.totalTicks = buf.readInt();
        this.ticksRemaining = buf.readInt();
        this.energyDrainPerTick = buf.readInt();
    }
    
    protected TickedMachineOperation(CompoundTag tag, boolean fromNetwork)
    {
        super(tag, fromNetwork);
        
        if (tag.contains(TAG_TOTAL_TICKS, Tag.TAG_INT))
            this.totalTicks = tag.getInt(TAG_TOTAL_TICKS);
        else
            this.totalTicks = 0;
        
        if (tag.contains(TAG_TICKS_REMAINING, Tag.TAG_INT))
            this.ticksRemaining = tag.getInt(TAG_TICKS_REMAINING);
        else
            this.ticksRemaining = 0;
        
        if (tag.contains(TAG_ENERGY_DRAIN_PER_TICK, Tag.TAG_INT))
            this.energyDrainPerTick = tag.getInt(TAG_ENERGY_DRAIN_PER_TICK);
        else
            this.energyDrainPerTick = 0;
    }
    
    protected TickedMachineOperation(int totalTicks, int ticksRemaining, int energyDrainPerTick)
    {
        this(totalTicks, ticksRemaining, energyDrainPerTick, false);
    }
    
    protected TickedMachineOperation(int totalTicks, int ticksRemaining, int energyDrainPerTick, boolean isPaused)
    {
        this.totalTicks = totalTicks;
        this.ticksRemaining = ticksRemaining;
        this.energyDrainPerTick = energyDrainPerTick;
    }
    
    @Override
    public boolean isComplete()
    {
        return this.ticksRemaining <= 0;
    }
    
    @Override
    public final float progress()
    {
        if (this.totalTicks > 0)
            return (float)(this.totalTicks - this.ticksRemaining) / this.totalTicks;
        else
            return 0f;
    }
    
    @Override
    public final int progressScaled(int limit)
    {
        if (this.totalTicks > 0)
            return ((this.totalTicks - this.ticksRemaining) * limit) / this.totalTicks;
        else
            return 0;
    }
    
    @Override
    public void serialize(CompoundTag tag, boolean toNetwork)
    {
        super.serialize(tag, toNetwork);
        
        tag.putInt(TAG_TOTAL_TICKS, this.totalTicks);
        tag.putInt(TAG_TICKS_REMAINING, this.ticksRemaining);
        tag.putInt(TAG_ENERGY_DRAIN_PER_TICK, this.energyDrainPerTick);
    }
    
    @Override
    public void serialize(FriendlyByteBuf buf, boolean toNetwork)
    {
        super.serialize(buf, toNetwork);
        
        buf.writeInt(this.totalTicks);
        buf.writeInt(this.ticksRemaining);
        buf.writeInt(this.energyDrainPerTick);
    }
    
    public static final String TAG_TOTAL_TICKS = "TotalTicks";
    public static final String TAG_TICKS_REMAINING = "TicksRemaining";
    public static final String TAG_ENERGY_DRAIN_PER_TICK = "EnergyDrainPerTick";
}
