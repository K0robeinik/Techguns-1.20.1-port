package techguns2.ammo;

import net.minecraft.world.item.Item;

/**
 * The base interface for ammo that represents a magazine of rounds.
 */
public interface IMagazineAmmo extends IRoundAmmoProvider
{
    /**
     * Gets the empty magazine item.
     * @return The item representing the empty magazine.
     */
    Item getEmptyMagazineItem();
}
