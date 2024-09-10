package techguns2.machine.fabricator;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.network.IContainerFactory;

public final class FabricatorMenuFactory implements IContainerFactory<FabricatorMenu>
{
    @Override
    public final FabricatorMenu create(int windowId, Inventory inv, FriendlyByteBuf data)
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
        
        return new FabricatorMenu(windowId, inv, ownerId);
    }
}
