package techguns2.machine.charging_station;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.network.IContainerFactory;

public class ChargingStationMenuFactory implements IContainerFactory<ChargingStationMenu>
{
    @Override
    public final ChargingStationMenu create(int windowId, Inventory inv, FriendlyByteBuf data)
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
        
        return new ChargingStationMenu(windowId, inv, ownerId);
    }
}
