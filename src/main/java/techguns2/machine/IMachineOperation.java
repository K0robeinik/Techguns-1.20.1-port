package techguns2.machine;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;

public interface IMachineOperation
{
    boolean isPaused();
    void setPaused(boolean paused);
    
    boolean isComplete();
    float progress();
    int progressScaled(int limit);
    
    void serialize(FriendlyByteBuf buf, boolean toNetwork);
    void serialize(CompoundTag tag, boolean toNetwork);
    
    void dropContents(Level level, double x, double y, double z);
}
