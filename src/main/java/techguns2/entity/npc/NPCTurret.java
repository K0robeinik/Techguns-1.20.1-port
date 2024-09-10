package techguns2.entity.npc;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.level.Level;
import techguns2.api.npc.factions.ITGNpcFaction;
import techguns2.api.npc.factions.ITGNpcTeam;
import techguns2.block.entity.NPCTurretBlockEntity;
import techguns2.entity.ai.goal.HurtByTargetTGFactionsGoal;
import techguns2.entity.ai.goal.NPCTurretNearestAttackableTargetGoal;
import techguns2.entity.ai.goal.NPCTurretWatchClosestGoal;
import techguns2.factions.TGNpcFactions;

public class NPCTurret extends Mob implements ITGNpcTeam
{

    protected NPCTurret(EntityType<? extends NPCTurret> type, Level level)
    {
        super(type, level);
    }
    
    protected NPCTurretWatchClosestGoal watchGoal;
    protected RandomLookAroundGoal idleGoal;
    protected HurtByTargetTGFactionsGoal hurtGoal;
    protected NPCTurretNearestAttackableTargetGoal<LivingEntity> targetGoal;
    
    @Nullable
    public NPCTurretBlockEntity getMountedBlockEntity()
    {
        return null;
    }

    @Override
    public ITGNpcFaction getFaction()
    {
        return TGNpcFactions.TURRET;
    }
    
    
    
}
