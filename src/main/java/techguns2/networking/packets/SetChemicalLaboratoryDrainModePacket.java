package techguns2.networking.packets;

import net.minecraft.network.FriendlyByteBuf;
import techguns2.machine.chemical_laboratory.ChemicalLaboratoryContainerData;

public final class SetChemicalLaboratoryDrainModePacket
{
    public final boolean fromOutput;
    
    public SetChemicalLaboratoryDrainModePacket(boolean fromOutput)
    {
        this.fromOutput = fromOutput;
    }
    
    public SetChemicalLaboratoryDrainModePacket(ChemicalLaboratoryContainerData.DrainMode drainMode)
    {
        this.fromOutput = drainMode == ChemicalLaboratoryContainerData.DrainMode.FROM_OUTPUT;
    }

    public final void write(FriendlyByteBuf buf)
    {
        buf.writeBoolean(this.fromOutput);
    }
    
    public static SetChemicalLaboratoryDrainModePacket read(FriendlyByteBuf buf)
    {
        return new SetChemicalLaboratoryDrainModePacket(buf.readBoolean());
    }
}
