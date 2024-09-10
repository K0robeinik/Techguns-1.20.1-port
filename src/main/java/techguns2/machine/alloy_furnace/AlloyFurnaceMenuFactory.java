package techguns2.machine.alloy_furnace;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.network.IContainerFactory;

public final class AlloyFurnaceMenuFactory implements IContainerFactory<AlloyFurnaceMenu>
{
    @Override
    public final AlloyFurnaceMenu create(int windowId, Inventory inv, FriendlyByteBuf data)
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
        
        return new AlloyFurnaceMenu(windowId, inv, ownerId);
    }
}
