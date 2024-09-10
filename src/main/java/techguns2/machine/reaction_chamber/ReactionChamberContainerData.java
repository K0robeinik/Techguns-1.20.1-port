package techguns2.machine.reaction_chamber;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraftforge.fluids.FluidStack;
import techguns2.machine.AbstractMultiBlockMachineContainerData;

public interface ReactionChamberContainerData extends AbstractMultiBlockMachineContainerData
{
    @Override
    @NotNull
    ReactionChamberControllerBlockEntity getBlockEntity();
    
    FluidStack getFluid();
    void dumpFluid();
    @Nullable
    LaserFocus getLaserFocus();
    ReactionRisk getReactionRisk();
    
    int getFluidLevel();
    void setFluidLevel(int fluidLevel);
    
    int getIntensity();
    void setIntensity(int intensity);
    
    int getReactionCycleTotalTicks();
    int getReactionCycleTicksRemaining();

    int getReactionRequiredIntensity();
    int getReactionCycle();
    int getReactionSuccessfulCycles();
    int getReactionRequiredCycles();
    int getReactionMaximumCycles();
    
    default float getReactionCycleProgress()
    {
        int totalReactionTicks = this.getReactionCycleTotalTicks();
        if (totalReactionTicks > 0)
            return (float)(totalReactionTicks - this.getReactionCycleTicksRemaining()) / totalReactionTicks;
        else
            return 0f;
    }
    default int getReactionCycleProgressScaled(int limit)
    {
        int totalReactionTicks = this.getReactionCycleTotalTicks();
        if (totalReactionTicks > 0)
            return ((totalReactionTicks - this.getReactionCycleTicksRemaining()) * limit) / totalReactionTicks;
        else
            return 0;
    }
}
