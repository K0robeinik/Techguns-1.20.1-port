package techguns2.machine;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;

public abstract class MachineOperation implements IMachineOperation
{
    public boolean isPaused;
    
    protected MachineOperation(FriendlyByteBuf buf, boolean fromNetwork)
    {
        this.isPaused = !fromNetwork || buf.readBoolean();
    }
    
    protected MachineOperation(CompoundTag tag, boolean fromNetwork)
    {
        if (fromNetwork && tag.contains(TAG_PAUSED, Tag.TAG_BYTE))
            this.isPaused = tag.getBoolean(TAG_PAUSED);
        else
            this.isPaused = false;
    }
    
    protected MachineOperation()
    {
        this.isPaused = false;
    }
    
    protected MachineOperation(boolean isPaused)
    {
        this.isPaused = isPaused;
    }
    
    @Override
    public final boolean isPaused()
    {
        return this.isPaused;
    }
    
    @Override
    public final void setPaused(boolean paused)
    {
        this.isPaused = paused;
    }

    @Override
    public abstract boolean isComplete();
    @Override
    public abstract float progress();
    @Override
    public abstract int progressScaled(int limit);

    @Override
    public void serialize(FriendlyByteBuf buf, boolean toNetwork)
    {
        if (toNetwork)
            buf.writeBoolean(this.isPaused);
    }

    @Override
    public void serialize(CompoundTag tag, boolean toNetwork)
    {
        if (toNetwork)
            tag.putBoolean(TAG_PAUSED, this.isPaused);
    }
    
    @Override
    public void dropContents(Level level, double x, double y, double z)
    { }

    public static final String TAG_PAUSED = "Paused";
}
