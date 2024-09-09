package techguns2.machine.reaction_chamber;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.network.IContainerFactory;

public final class ReactionChamberMenuFactory implements IContainerFactory<ReactionChamberMenu>
{
    @Override
    public final ReactionChamberMenu create(int windowId, Inventory inv, FriendlyByteBuf data)
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
        
        return new ReactionChamberMenu(windowId, inv, ownerId);
    }
}
