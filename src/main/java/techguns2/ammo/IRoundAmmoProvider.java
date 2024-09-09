package techguns2.ammo;

/**
 * The base interface for ammo that provides a round
 */
public interface IRoundAmmoProvider extends IAmmo
{
    /**
     * Gets the <b>base</b> round capacity of this clip/magazine.
     * <p>
     * This is the <b>BASE</b> round capacity instead of the actual round capacity
     * as guns can override how many rounds each clip/magazine offers.
     * </p>
     * <p>
     * I know, very weird. This may get changed in the future where a clip's/magazine's
     * round capacity IS it's round capacity (and I want to change it to this),
     * but for now this it's this way due to how old TechGuns 1.12.2 worked.
     * </p>
     * @return The <b>base</b> round capacity of this clip/magazine.
     */
    int getRoundCapacity();
    
    /**
     * Gets the round this clip/magazine provides.
     * @return The round this clip/magazine provides.
     */
    IRoundAmmo getRound();
    
    @Override
    default IAmmoType getType()
    {
        return this.getRound().getType();
    }
}
