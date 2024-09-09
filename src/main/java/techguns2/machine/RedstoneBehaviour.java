package techguns2.machine;

import net.minecraft.network.chat.Component;
import techguns2.TGTranslations;

public abstract class RedstoneBehaviour
{
    private final byte _value;
    private final byte _flagValue;
    private final Component _name;
    private final Component _signalName;

    private RedstoneBehaviour(byte value, byte flagValue, TGTranslations.RedstoneBehaviour translations)
    {
        this._value = value;
        this._flagValue = flagValue;
        this._name = Component.translatable(translations.NAME);
        this._signalName = Component.translatable(translations.SIGNAL_NAME);
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
    
    public final Component getSignalName()
    {
        return this._signalName;
    }
    
    public abstract boolean test(int signalStrength);
    public abstract RedstoneBehaviour getNext();
    public abstract RedstoneBehaviour getPrevious();
    
    public static final RedstoneBehaviour IGNORE = new RedstoneBehaviour((byte)0, (byte)0b00000000, TGTranslations.RedstoneBehaviour.IGNORE)
    {
        @Override
        public final boolean test(int signalStrength)
        {
            return true;
        }
        
        @Override
        public final RedstoneBehaviour getNext()
        {
            return RedstoneBehaviour.POWERED;
        }
        
        @Override
        public final RedstoneBehaviour getPrevious()
        {
            return RedstoneBehaviour.UNPOWERED;
        }
    };
    
    public static final RedstoneBehaviour POWERED = new RedstoneBehaviour((byte)1, (byte)0b00000100, TGTranslations.RedstoneBehaviour.POWERED)
    {
        @Override
        public final boolean test(int signalStrength)
        {
            return signalStrength > 0;
        }
        
        @Override
        public final RedstoneBehaviour getNext()
        {
            return RedstoneBehaviour.UNPOWERED;
        }
        
        @Override
        public final RedstoneBehaviour getPrevious()
        {
            return RedstoneBehaviour.IGNORE;
        }
    };
    
    public static final RedstoneBehaviour UNPOWERED = new RedstoneBehaviour((byte)2, (byte)0b00001000, TGTranslations.RedstoneBehaviour.UNPOWERED)
    {
        @Override
        public final boolean test(int signalStrength)
        {
            return signalStrength <= 0;
        }
        
        @Override
        public final RedstoneBehaviour getNext()
        {
            return RedstoneBehaviour.IGNORE;
        }
        
        @Override
        public final RedstoneBehaviour getPrevious()
        {
            return RedstoneBehaviour.POWERED;
        }
    };
    
    public static final RedstoneBehaviour DEFAULT = IGNORE;
    public static final byte FLAG_MASK = (byte)0b00001100;
    
    public static final RedstoneBehaviour get(byte value)
    {
        switch (value)
        {
            case 0:
                return RedstoneBehaviour.IGNORE;
            case 1:
                return RedstoneBehaviour.POWERED;
            case 2:
                return RedstoneBehaviour.UNPOWERED;
            default:
                return RedstoneBehaviour.IGNORE;
        }
    }
    
    public static final RedstoneBehaviour getValueFromFlags(byte flags)
    {
        int redstoneValue = (flags & 0b00001100) >> 2;
        switch (redstoneValue)
        {
            case 0:
                return RedstoneBehaviour.IGNORE;
            case 1:
                return RedstoneBehaviour.POWERED;
            case 2:
                return RedstoneBehaviour.UNPOWERED;
            default:
                return RedstoneBehaviour.IGNORE;
        }
    }

}
