package techguns2.api;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.entity.player.Player;

public interface ITGPlayerOwnable
{
    void setOwner(@Nullable Player owner);
    boolean isOwnedBy(Player player);
    @Nullable
    UUID getOwnerID();
    
    SecurityMode getSecurity();
}
