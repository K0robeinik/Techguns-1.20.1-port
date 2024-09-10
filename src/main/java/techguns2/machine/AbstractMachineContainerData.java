package techguns2.machine;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.inventory.ContainerData;
import techguns2.api.SecurityMode;

public interface AbstractMachineContainerData extends ContainerData
{
    @Nullable
    AbstractMachineBlockEntity getBlockEntity();
    
    byte getFlags();
    void setFlags(byte flags);
    
    boolean isActive();
    
    RedstoneBehaviour getRedstoneBehaviour();
    void setRedstoneBehaviour(RedstoneBehaviour behaviour);
    
    SecurityMode getSecurityMode();
    void setSecurityMode(SecurityMode mode);
    
    int getEnergyCapacity();
    int getStoredEnergy();
    
    float getOperationProgress();
    int getOperationProgressScaled(int limit);
}
