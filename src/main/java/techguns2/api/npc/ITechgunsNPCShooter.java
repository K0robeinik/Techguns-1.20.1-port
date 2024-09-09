package techguns2.api.npc;

/**
 * Must only be implemented by subclasses of LivingEntity
 */
public interface ITechgunsNPCShooter
{
    float getWeaponXPos();
    float getWeaponYPos();
    float getWeaponZPos();
    
    default boolean hasWeaponArmPose()
    {
        return true;
    }
    
    default float getWeaponScale()
    {
        return 1.0F;
    }
    
    default float getProjectileSideOffset()
    {
        return 0F;
    }
    
    default float getProjectileHeightOffset()
    {
        return 0F;
    }
}
