package techguns2.entity.ai.goal;

import java.util.function.Predicate;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.phys.AABB;

public class NPCTurretNearestAttackableTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T>
{
    private final double yOffset;
    
    public NPCTurretNearestAttackableTargetGoal(
            Mob mob,
            Class<T> classTarget,
            int chance,
            boolean checkSight,
            boolean onlyNearby,
            Predicate<LivingEntity> targetSelector,
            double yOffset)
    {
        super(mob, classTarget, chance, checkSight, onlyNearby, targetSelector);
        
        this.yOffset = yOffset;
    }
    
    @Override
    protected AABB getTargetSearchArea(double targetDistance)
    {
        return this.mob.getBoundingBox().inflate(targetDistance, this.yOffset, targetDistance);
    }
}
