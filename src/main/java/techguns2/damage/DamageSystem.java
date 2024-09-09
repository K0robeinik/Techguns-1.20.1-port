package techguns2.damage;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import techguns2.TGConfig;
import techguns2.entity.npc.NPCTurret;

public final class DamageSystem
{

    private DamageSystem()
    { }
    
    public static float getDamageFactor(LivingEntity attacker, LivingEntity target, MinecraftServer server)
    {
        if (attacker instanceof Player && target instanceof Player)
        {
            if (server.isPvpAllowed())
            {
                return TGConfig.Damage.pvpFactor;
            }
            
            return 0F;
        }
        else if (target instanceof Player)
        {
            if (attacker instanceof NPCTurret)
            {
                return TGConfig.Damage.turretToPlayerFactor;
            }
            else
            {
                return TGConfig.Damage.npcFactor;
            }
        }
        else if (attacker instanceof Player)
        {
            return 1.0F;
        }
        
        return TGConfig.Damage.npcFactor;
        
        
    }

}
