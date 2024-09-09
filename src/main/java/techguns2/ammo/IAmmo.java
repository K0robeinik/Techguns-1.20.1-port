package techguns2.ammo;

/**
 * The base interface of ammo.
 */
public interface IAmmo
{
    /**
     * Gets the type this ammo is.
     * @return The type this ammo is.
     */
    IAmmoType getType();
    
    default float getDamageMultiplier()
    {
        return 1;
    }
    default float getDamageDropoffStartMultiplier()
    {
        return 1;
    }
    default float getDamageDropoffEndMultiplier()
    {
        return 1;
    }
}
