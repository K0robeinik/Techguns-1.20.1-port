package techguns2.machine.chemical_laboratory;

import org.jetbrains.annotations.Nullable;

import net.minecraftforge.fluids.FluidStack;
import techguns2.machine.AbstractMachineContainerData;

public interface ChemicalLaboratoryContainerData extends AbstractMachineContainerData
{
    @Override
    @Nullable
    ChemicalLaboratoryBlockEntity getBlockEntity();
    
    DrainMode getDrainMode();
    void setDrainMode(DrainMode mode);
    
    FluidStack getFluid();
    FluidStack getResultFluid();
    
    void dumpFluid();
    void dumpResultFluid();
    
    public static enum DrainMode
    {
        FROM_INPUT,
        FROM_OUTPUT;
    }
}
