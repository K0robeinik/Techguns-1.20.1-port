package techguns2.api.npc.factions;

import net.minecraft.resources.ResourceLocation;

public interface ITGNpcFaction
{
    ResourceLocation location();
    
    FactionNeutrality getNeutrality(ITGNpcFaction other);
}
