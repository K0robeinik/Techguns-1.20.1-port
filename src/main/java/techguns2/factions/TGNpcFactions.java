package techguns2.factions;

import net.minecraft.resources.ResourceLocation;
import techguns2.Techguns;
import techguns2.api.npc.factions.FactionNeutrality;
import techguns2.api.npc.factions.ITGNpcFaction;

/**
 * A common list of NPC Factions from the API.
 */
public enum TGNpcFactions implements ITGNpcFaction
{
    HOSTILE("hostile"),
    TURRET("turret"),
    NEUTRAL("neutral");
    
    public final ResourceLocation location;
    
    private TGNpcFactions(String name)
    {
        this.location = new ResourceLocation(Techguns.MODID, name);
    }
    
    @Override
    public final ResourceLocation location()
    {
        return this.location;
    }
    
    @Override
    public final FactionNeutrality getNeutrality(ITGNpcFaction other)
    {
        if (this == HOSTILE)
        {
            if (TGNpcFactions.areHostilesHostileTowards(other))
                return FactionNeutrality.ENEMY;
            else if (TGNpcFactions.areHostilesPassiveTowards(other))
                return FactionNeutrality.PASSIVE;
            else
                return FactionNeutrality.NEUTRAL;
        }
        else if (this == TURRET)
        {
            if (TGNpcFactions.areTurretsHostileTowards(other))
                return FactionNeutrality.ENEMY;
            else if (TGNpcFactions.areTurretsPassiveTowards(other))
                return FactionNeutrality.PASSIVE;
            else
                return FactionNeutrality.NEUTRAL;
        }
        else if (this == NEUTRAL)
        {
            if (TGNpcFactions.areNeutralsHostileTowards(other))
                return FactionNeutrality.ENEMY;
            else if (TGNpcFactions.areNeutralsPassiveTowards(other))
                return FactionNeutrality.PASSIVE;
            else
                return FactionNeutrality.NEUTRAL;
        }
        else
        {
            return other.getNeutrality(this);
        }
    }
    
    public static boolean areHostilesHostileTowards(ITGNpcFaction other)
    {
        ResourceLocation otherLocation = other.location();
        return TURRET.location.equals(otherLocation) ||
                NEUTRAL.location.equals(otherLocation);
    }
    
    public static boolean areHostilesPassiveTowards(ITGNpcFaction other)
    {
        return HOSTILE.location.equals(other.location());
    }
    
    public static boolean areTurretsHostileTowards(ITGNpcFaction other)
    {
        return HOSTILE.location.equals(other.location());
    }
    
    public static boolean areTurretsPassiveTowards(ITGNpcFaction other)
    {
        return TURRET.location.equals(other.location());
    }
    
    public static boolean areNeutralsHostileTowards(ITGNpcFaction other)
    {
        return false;
    }
    
    public static boolean areNeutralsPassiveTowards(ITGNpcFaction other)
    {
        return NEUTRAL.location.equals(other.location());
    }
}
