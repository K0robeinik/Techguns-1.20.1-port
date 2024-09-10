package techguns2.entity.projectile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class StoneBulletProjectile extends GenericProjectile
{

    public StoneBulletProjectile(EntityType<?> type, Level level)
    {
        super(type, level);
    }
    
    public static StoneBulletProjectile create(
            EntityType<?> type,
            Level level,
            LivingEntity owner,
            double posX,
            double posY,
            double posZ,
            float yaw,
            float pitch,
            float speed,
            int lifespan,
            float spread,
            float armorPenetration,
            float damage,
            float damageDropoffStart,
            float damageDropoffEnd,
            float minDamage,
            float gravity,
            ProjectileFireSide side)
    {
        StoneBulletProjectile result = new StoneBulletProjectile(type, level);
        result.initProjectile(
                owner,
                posX,
                posY,
                posZ,
                yaw,
                pitch,
                speed,
                lifespan,
                spread,
                armorPenetration,
                damage,
                damageDropoffStart,
                damageDropoffEnd,
                minDamage,
                gravity,
                side);
        return result;
    }


}
