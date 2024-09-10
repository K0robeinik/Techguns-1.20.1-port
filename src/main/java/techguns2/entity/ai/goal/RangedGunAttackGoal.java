package techguns2.entity.ai.goal;

import java.util.EnumSet;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.RangedAttackMob;

public class RangedGunAttackGoal<T extends Mob & RangedAttackMob> extends Goal
{
    private final T mob;
    /**
     * A decrementing tick that spawns a ranged attack once
     * this value reaches 0. It is then set back to the
     * maxRangedAttackTime.
     */
    private int rangedAttackTime;
    private double entityMoveSpeed;
    private int ticksTargetSeen;
    private int attackTimeVariance;
    
    /**
     * The maximum time the AI has to wait before performing
     * another ranged attack.
     */
    private int maxRangedAttackTime;
    private float attackRange;
    private float attackRangeSqr;
    
    // GUN HANDLING:
    /**
     * Total number of shots in burst
     */
    private int maxBurstCount;
    /**
     * shots left in current burst.
     */
    private int burstCount;
    /**
     * delay between shots in burst.
     */
    private int shotDelay;
    
    public RangedGunAttackGoal(
            T mob,
            double moveSpeed,
            int attackTimeVariance,
            int attackTime,
            float attackRange,
            int maxBurstCount,
            int shotDelay)
    {
        this.rangedAttackTime = -1;
        
        this.mob = mob;
        this.entityMoveSpeed = moveSpeed;
        this.attackTimeVariance = attackTimeVariance;
        this.maxRangedAttackTime = attackTime;
        this.attackRange = attackRange;
        this.attackRangeSqr = attackRange * attackRange;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        
        this.maxBurstCount = maxBurstCount;
        this.burstCount = maxBurstCount;
        this.shotDelay = shotDelay;
    }
    
    @Override
    public boolean canUse()
    {
        return this.mob.getTarget() != null;
    }
    
    @Override
    public boolean canContinueToUse()
    {
        return this.canUse() || !this.mob.getNavigation().isDone();
    }
    
    @Override
    public void stop()
    {
        this.ticksTargetSeen = 0;
        this.rangedAttackTime = -1;
    }
    
    @Override
    public void tick()
    {
        LivingEntity target = this.mob.getTarget();
        
        if (target == null)
        {
            return;
        }
        
        double d0 = this.mob.distanceToSqr(
                target.getX(),
                target.getY(),
                target.getZ());
        
        boolean targetInSight = this.mob.getSensing().hasLineOfSight(target);
        
        if (targetInSight)
        {
            this.ticksTargetSeen++;
        }
        else
        {
            this.ticksTargetSeen = 0;
        }
        
        if (d0 <= (double)this.attackRangeSqr && this.ticksTargetSeen >= 20)
        {
            this.mob.getNavigation().stop();
        }
        else
        {
            this.mob.getNavigation().moveTo(target, this.entityMoveSpeed);
        }
        
        this.mob.getLookControl().setLookAt(target, 30.0F, 55.0F);
        
        float f;
        
        if (--this.rangedAttackTime == 0)
        {
            if (d0 > (double)this.attackRangeSqr || !targetInSight)
                return;
            
            f = Mth.sqrt((float)d0) / this.attackRange;
            
            float f1 = f;
            
            if (f < 0.1F)
            {
                f1 = 0.1F;
            }
            
            if (f1 > 1.0F)
            {
                f1 = 1.0F;
            }
            
            this.mob.performRangedAttack(target, f1);
            
            if (this.maxBurstCount > 0)
                this.burstCount--;
            
            if (this.burstCount > 0)
                this.rangedAttackTime = this.shotDelay;
            else
            {
                this.burstCount = this.maxBurstCount;
                this.rangedAttackTime = Mth.floor(f * (float)(this.maxRangedAttackTime - this.attackTimeVariance) + (float)this.attackTimeVariance);
            }
        }
        else if (this.rangedAttackTime < 0)
        {
            f = Mth.sqrt((float)d0) / this.attackRange;
            this.rangedAttackTime = Mth.floor(f * (float)(this.maxRangedAttackTime - this.attackTimeVariance) + (float)this.attackTimeVariance);
        }
    }
}
