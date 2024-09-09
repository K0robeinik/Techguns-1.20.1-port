package techguns2.entity.ai.goal;

import java.util.EnumSet;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import techguns2.entity.npc.NPCTurret;

// based on LookAtPlayerGoal
public class NPCTurretWatchClosestGoal extends Goal
{
    private final NPCTurret entity;
    private final float range;
    private final float probability;
    protected final Class<? extends LivingEntity> lookAtType;
    protected final TargetingConditions lookAtContext;
    @Nullable
    private Entity closestEntity;
    private int lookTime;
    private double yOffset;
    
    public NPCTurretWatchClosestGoal(
            NPCTurret entity,
            Class<? extends LivingEntity> lookAtType,
            float range,
            double yOffset)
    {
        this(
                entity,
                lookAtType,
                range,
                0.02F,
                yOffset);
    }
    
    public NPCTurretWatchClosestGoal(
            NPCTurret entity,
            Class<? extends LivingEntity> lookAtType,
            float range,
            float probability,
            double yOffset)
    {
        this.entity = entity;
        this.range = range;
        this.probability = probability;
        this.lookAtType = lookAtType;

        this.lookAtContext = TargetingConditions.forNonCombat().range(range);
        
        if (lookAtType == Player.class)
        {
            this.lookAtContext.selector((targetEntity) -> {
                return EntitySelector.notRiding(entity).and(EntitySelector.NO_SPECTATORS).test(targetEntity);
            });
        }
        
        this.yOffset = yOffset;
        this.setFlags(EnumSet.of(Flag.LOOK));
        
    }
    
    @Override
    public boolean canUse()
    {
        if (this.entity.getRandom().nextFloat() >= this.probability)
        {
            return false;
        }
        
        if (this.entity.getTarget() != null)
        {
            this.closestEntity = this.entity.getTarget();
        }
        
        if (this.lookAtType == Player.class)
        {
            this.closestEntity = this.entity.level().getNearestPlayer(
                    this.lookAtContext,
                    this.entity,
                    this.entity.getX(),
                    this.entity.getY(),
                    this.entity.getZ());
        }
        else
        {
            this.closestEntity = this.entity.level().getNearestEntity(
                    this.entity.level().getEntitiesOfClass(
                            this.lookAtType,
                            this.entity.getBoundingBox().inflate(this.range, this.yOffset, this.range),
                            (v) -> true), 
                    this.lookAtContext, 
                    this.entity, 
                    this.entity.getX(), 
                    this.entity.getY(), 
                    this.entity.getZ());
        }
        
        return this.closestEntity != null;
    }
    
    @Override
    public boolean canContinueToUse()
    {
        if (!this.closestEntity.isAlive())
            return false;
        if (this.entity.distanceToSqr(this.closestEntity) > (double)(this.range * this.range))
            return false;
        
        return this.lookTime > 0;
    }
    
    @Override
    public void start()
    {
        this.lookTime = this.adjustedTickDelay(40 + this.entity.getRandom().nextInt(40));
    }
    
    @Override
    public void stop()
    {
        this.closestEntity = null;
    }
    
    @Override
    public void tick()
    {
        this.entity.getLookControl().setLookAt(
                this.closestEntity.getX(),
                this.closestEntity.getEyeY(),
                this.closestEntity.getZ());
        this.lookTime--;
    }

}
