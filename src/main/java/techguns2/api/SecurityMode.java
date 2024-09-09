package techguns2.api;

import java.util.UUID;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import techguns2.TGTranslations;
import techguns2.Techguns;

public abstract class SecurityMode
{
    private final byte _flagValue;
    private final byte _value;
    private final Component _name;
    private final Component _description;

    private SecurityMode(byte value, byte flagValue, TGTranslations.SecurityMode translation)
    {
        this._value = value;
        this._flagValue = flagValue;
        this._name = Component.translatable(translation.NAME);
        this._description = Component.translatable(translation.DESCRIPTION);
    }
    
    public final byte value()
    {
        return this._value;
    }
    
    public final byte getFlagValue()
    {
        return this._flagValue;
    }
    
    public final Component getName()
    {
        return this._name;
    }
    
    public final Component getDescription()
    {
        return this._description;
    }
    
    public abstract boolean test(Player player, UUID owningPlayer);
    
    public abstract SecurityMode getNext();
    public abstract SecurityMode getPrevious();
    
    public static final SecurityMode PUBLIC = new SecurityMode((byte)0, (byte)0b00000000, TGTranslations.SecurityMode.PUBLIC)
    {
        @Override
        public final boolean test(Player player, UUID owningPlayer)
        {
            return true;
        }
        
        @Override
        public final SecurityMode getNext()
        {
            if (Techguns.FACTIONS_HANDLER instanceof Techguns.NoFactionsHandler)
            {
                return SecurityMode.OWNER;
            }
            
            return SecurityMode.SAME_TEAM_AND_ALLIES;
        }
        
        @Override
        public final SecurityMode getPrevious()
        {
            return SecurityMode.OWNER;
        }
    };
    
    public static final SecurityMode SAME_TEAM_AND_ALLIES = new SecurityMode((byte)1, (byte)0b00000001, TGTranslations.SecurityMode.SAME_TEAM_AND_ALLIES)
    {
        @Override
        public final boolean test(Player player, UUID owningPlayer)
        {
            return Techguns.FACTIONS_HANDLER.isAlliedOrTeamMember(owningPlayer, player.getUUID());
        }
        
        @Override
        public final SecurityMode getNext()
        {
            if (Techguns.FACTIONS_HANDLER instanceof Techguns.NoFactionsHandler)
            {
                return SecurityMode.OWNER;
            }
            
            return SecurityMode.SAME_TEAM;
        }
        
        @Override
        public final SecurityMode getPrevious()
        {
            return SecurityMode.PUBLIC;
        }
    };
    
    public static final SecurityMode SAME_TEAM = new SecurityMode((byte)2, (byte)0b00000010, TGTranslations.SecurityMode.SAME_TEAM)
    {
        @Override
        public final boolean test(Player player, UUID owningPlayer)
        {
            return Techguns.FACTIONS_HANDLER.isTeamMember(owningPlayer, player.getUUID());
        }
        
        @Override
        public final SecurityMode getNext()
        {
            return SecurityMode.OWNER;
        }
        
        @Override
        public final SecurityMode getPrevious()
        {
            if (Techguns.FACTIONS_HANDLER instanceof Techguns.NoFactionsHandler)
            {
                return SecurityMode.PUBLIC;
            }
            
            return SecurityMode.SAME_TEAM_AND_ALLIES;
        }
    };
    
    public static final SecurityMode OWNER = new SecurityMode((byte)3, (byte)0b00000011, TGTranslations.SecurityMode.OWNER)
    {
        @Override
        public final boolean test(Player player, UUID owningPlayer)
        {
            return player.getUUID().equals(owningPlayer);
        }
        
        @Override
        public final SecurityMode getNext()
        {
            return SecurityMode.PUBLIC;
        }
        
        @Override
        public final SecurityMode getPrevious()
        {
            if (Techguns.FACTIONS_HANDLER instanceof Techguns.NoFactionsHandler)
            {
                return SecurityMode.PUBLIC;
            }
            
            return SecurityMode.SAME_TEAM;
        }
    };
    
    public static final SecurityMode DEFAULT = PUBLIC;
    public static final byte FLAG_MASK = (byte)0b00000011;
    
    public static final SecurityMode get(byte value)
    {
        switch (value)
        {
            case 0:
                return SecurityMode.PUBLIC;
            case 1:
                return SecurityMode.SAME_TEAM_AND_ALLIES;
            case 2:
                return SecurityMode.SAME_TEAM;
            case 3:
                return SecurityMode.OWNER;
            default:
                return SecurityMode.PUBLIC;
        }
    }
    
    public static final SecurityMode getValueFromFlags(byte flags)
    {
        int securityMode = flags & 0b00000011;
        switch (securityMode)
        {
            case 0:
                return SecurityMode.PUBLIC;
            case 1:
                return SecurityMode.SAME_TEAM_AND_ALLIES;
            case 2:
                return SecurityMode.SAME_TEAM;
            case 3:
                return SecurityMode.OWNER;
            default:
                return SecurityMode.PUBLIC;
        }
    }
}
