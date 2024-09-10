package techguns2.machine.chemical_laboratory;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.network.IContainerFactory;

public class ChemicalLaboratoryMenuFactory implements IContainerFactory<ChemicalLaboratoryMenu>
{
    @Override
    public final ChemicalLaboratoryMenu create(int windowId, Inventory inv, FriendlyByteBuf data)
    {
        @Nullable
        UUID ownerId;
        if (data.readBoolean())
        {
            ownerId = data.readUUID();
        }
        else
        {
            ownerId = null;
        }
        
        return new ChemicalLaboratoryMenu(windowId, inv, ownerId);
    }
}
