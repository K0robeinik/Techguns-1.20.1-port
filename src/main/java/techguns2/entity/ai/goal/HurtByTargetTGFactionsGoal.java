package techguns2.entity.ai.goal;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.AABB;
import techguns2.Techguns;
import techguns2.api.npc.factions.FactionNeutrality;
import techguns2.api.npc.factions.ITGNpcTeam;
import techguns2.block.entity.NPCTurretBlockEntity;
import techguns2.entity.npc.NPCTurret;

public class HurtByTargetTGFactionsGoal extends TargetGoal
{
    private static final TargetingConditions HURT_BY_TARGETING = TargetingConditions.forCombat().ignoreLineOfSight()
            .ignoreInvisibilityTesting();
    private boolean alertSameType;
    private int timestamp;
    private final Class<?>[] toIgnoreDamage;
    @Nullable
    private Class<?>[] toIgnoreAlert;

    public HurtByTargetTGFactionsGoal(Mob mob, Class<?>... p_26040_)
    {
       super(mob, true);
       this.toIgnoreDamage = p_26040_;
       this.setFlags(EnumSet.of(Goal.Flag.TARGET));
    }

    @Override
    public boolean canUse()
    {
        int i = this.mob.getLastHurtByMobTimestamp();
        LivingEntity livingentity = this.mob.getLastHurtByMob();
        if (i != this.timestamp && livingentity != null)
        {
            if (livingentity.getType() == EntityType.PLAYER
                    && this.mob.level().getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER))
            {
                return false;
            }
            else
            {
                for (Class<?> oclass : this.toIgnoreDamage)
                {
                    if (oclass.isAssignableFrom(livingentity.getClass()))
                    {
                        return false;
                    }
                }

                return this.canAttack(livingentity, HURT_BY_TARGETING);
            }
        }
        else
        {
            return false;
        }
    }

    public HurtByTargetTGFactionsGoal setAlertOthers(Class<?>... p_26045_)
    {
        this.alertSameType = true;
        this.toIgnoreAlert = p_26045_;
        return this;
    }
    
    @Override
    protected boolean canAttack(@Nullable LivingEntity targetEntity, TargetingConditions conditions)
    {
        if (targetEntity == null)
            return false;
        
        if (this.mob instanceof NPCTurret turret && targetEntity instanceof Player player)
        {
            @org.jetbrains.annotations.Nullable
            NPCTurretBlockEntity turretBlockEntity = turret.getMountedBlockEntity();
            if (turretBlockEntity != null)
            {
                UUID owner = turretBlockEntity.getOwnerID();
                if (owner != null)
                {
                    FactionNeutrality neutrality = Techguns.FACTIONS_HANDLER.getNeutrality(owner, player.getGameProfile().getId());

                    return neutrality == FactionNeutrality.NEUTRAL ||
                            neutrality == FactionNeutrality.ENEMY;
                }
            }
        }
        
        if (this.mob instanceof ITGNpcTeam mobTeam && targetEntity instanceof ITGNpcTeam targetTeam)
        {
            FactionNeutrality neutrality = mobTeam.getFaction().getNeutrality(targetTeam.getFaction());
            
            return neutrality == FactionNeutrality.NEUTRAL ||
                    neutrality == FactionNeutrality.ENEMY;
        }
        
        return super.canAttack(targetEntity, conditions);
        
    }

    @Override
    public void start()
    {
        this.mob.setTarget(this.mob.getLastHurtByMob());
        this.targetMob = this.mob.getTarget();
        this.timestamp = this.mob.getLastHurtByMobTimestamp();
        this.unseenMemoryTicks = 300;
        if (this.alertSameType)
        {
            this.alertOthers();
        }

        super.start();
    }

    protected void alertOthers()
    {
        double d0 = this.getFollowDistance();
        AABB aabb = AABB.unitCubeFromLowerCorner(this.mob.position()).inflate(d0, 10.0D, d0);
        List<? extends Mob> list = this.mob.level().getEntitiesOfClass(this.mob.getClass(), aabb,
                EntitySelector.NO_SPECTATORS);
        Iterator<? extends Mob> iterator = list.iterator();

        while (true)
        {
            Mob mob;
            while (true)
            {
                if (!iterator.hasNext())
                {
                    return;
                }

                mob = (Mob) iterator.next();
                if (this.mob != mob && mob.getTarget() == null
                        && (!(this.mob instanceof TamableAnimal)
                                || ((TamableAnimal) this.mob).getOwner() == ((TamableAnimal) mob).getOwner())
                        && !mob.isAlliedTo(this.mob.getLastHurtByMob()))
                {
                    if (this.toIgnoreAlert == null)
                    {
                        break;
                    }

                    boolean flag = false;

                    for (Class<?> oclass : this.toIgnoreAlert)
                    {
                        if (mob.getClass() == oclass)
                        {
                            flag = true;
                            break;
                        }
                    }

                    if (!flag)
                    {
                        break;
                    }
                }
            }

            this.alertOther(mob, this.mob.getLastHurtByMob());
        }
    }

    protected void alertOther(Mob p_26042_, LivingEntity p_26043_)
    {
        p_26042_.setTarget(p_26043_);
    }

}
