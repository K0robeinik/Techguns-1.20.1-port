package techguns2.api.npc.factions;

import java.util.UUID;

/**
 * A handler for player factions, determining if players are enemies, allies, etc.
 */
public interface ITGPlayerFactionsHandler
{
    boolean isEnemy(UUID owner, UUID target);
    
    boolean isAlliedOrTeamMember(UUID owner, UUID target);
    
    boolean isAlliedNoTeamMember(UUID owner, UUID target);
    
    boolean isTeamMember(UUID owner, UUID target);
    
    FactionNeutrality getNeutrality(UUID player1, UUID player2);
}
