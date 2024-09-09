package techguns2.machine.ammo_press;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.network.IContainerFactory;

public final class AmmoPressMenuFactory implements IContainerFactory<AmmoPressMenu>
{
    @Override
    public final AmmoPressMenu create(int windowId, Inventory inv, FriendlyByteBuf data)
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
        
        return new AmmoPressMenu(windowId, inv, ownerId);
    }
}
