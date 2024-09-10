package techguns2.block.entity;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import techguns2.TGBlockEntityTypes;
import techguns2.Techguns;
import techguns2.api.SecurityMode;
import techguns2.machine.AbstractMachineBlockEntity;
import techguns2.machine.RedstoneBehaviour;
import techguns2.networking.BaseSyncedContainerBlockEntity;

public class NPCTurretBlockEntity extends BasePoweredBlockEntity
{
    private MobAttackMode _mobAttackMode;
    private PlayerAttackMode _playerAttackMode;
    
    public NPCTurretBlockEntity(BlockPos blockPos, BlockState blockState)
    {
        super(TGBlockEntityTypes.NPC_TURRET.get(), blockPos, blockState);
        
        this._mobAttackMode = MobAttackMode.DEFAULT;
        this._playerAttackMode = PlayerAttackMode.DEFAULT;
    }
    
    @Override
    public final int getEnergyCapacity()
    {
        return 5000;
    }
    
    public final MobAttackMode getMobAttackMode()
    {
        return this._mobAttackMode;
    }
    
    public final void setMobAttackMode(MobAttackMode mode)
    {
        if (mode == null)
            mode = MobAttackMode.DEFAULT;
        this._mobAttackMode = mode;
    }
    
    public final PlayerAttackMode getPlayerAttackMode()
    {
        return this._playerAttackMode;
    }
    
    public final void setPlayerAttackMode(PlayerAttackMode mode)
    {
        if (mode == null)
            mode = PlayerAttackMode.DEFAULT;
        this._playerAttackMode = mode;
    }
    
    public final byte getAttackModeFlags()
    {
        return (byte)(this._mobAttackMode.getFlagValue() | this._playerAttackMode.getFlagValue());
    }
    
    public final void setAttackModeFlags(byte flagValue)
    {
        this._mobAttackMode = MobAttackMode.getValueFromFlags(flagValue);
        this._playerAttackMode = PlayerAttackMode.getValueFromFlags(flagValue);
    }
    
    @Override
    protected void read(CompoundTag tag, boolean clientPacket)
    {
        super.read(tag, clientPacket);
        
        this.setAttackModeFlags(tag.getByte("AttackFlags"));
    }
    
    @Override
    protected void write(CompoundTag tag, boolean clientPacket)
    {
        super.write(tag, clientPacket);
        
        tag.putByte("AttackFlags", this.getAttackModeFlags());
    }

    @Override
    public int getContainerSize()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean isEmpty()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public ItemStack getItem(int p_18941_)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ItemStack removeItem(int p_18942_, int p_18943_)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ItemStack removeItemNoUpdate(int p_18951_)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setItem(int p_18944_, ItemStack p_18945_)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean stillValid(Player p_18946_)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void clearContent()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected Component getDefaultName()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected AbstractContainerMenu createMenu(int p_58627_, Inventory p_58628_)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    /**
     * The mob attack mode of the turret.
     */
    public static abstract class MobAttackMode
    {
        private final byte _flagValue;
        private final byte _value;
        
        private MobAttackMode(byte value, byte flagValue)
        {
            this._value = value;
            this._flagValue = flagValue;
        }
        
        public final byte value()
        {
            return this._value;
        }
        
        public final byte getFlagValue()
        {
            return this._flagValue;
        }
        
        public abstract boolean test(Mob mob);
        
        public abstract MobAttackMode getPrevious();
        
        public abstract MobAttackMode getNext();
        
        
        public static final MobAttackMode OFF = new MobAttackMode((byte)0, (byte)0b00000000)
        {
            @Override
            public final boolean test(Mob mob)
            {
                return false;
            }
            
            @Override
            public final MobAttackMode getPrevious()
            {
                return MobAttackMode.ALL;
            }
            
            @Override
            public final MobAttackMode getNext()
            {
                return MobAttackMode.ONLY_HOSTILES;
            }
        };
        public static final MobAttackMode ONLY_HOSTILES = new MobAttackMode((byte)1, (byte)0b00000001)
        {
            @Override
            public final boolean test(Mob mob)
            {
                return mob instanceof Monster && !(mob instanceof NeutralMob);
            }
            
            @Override
            public final MobAttackMode getPrevious()
            {
                return MobAttackMode.OFF;
            }
            
            @Override
            public final MobAttackMode getNext()
            {
                return MobAttackMode.IGNORE_PASSIVE;
            }
        };
        public static final MobAttackMode IGNORE_PASSIVE = new MobAttackMode((byte)2, (byte)0b00000010)
        {
            @Override
            public final boolean test(Mob mob)
            {
                return !(mob instanceof Animal) || mob instanceof NeutralMob;
            }
            
            @Override
            public final MobAttackMode getPrevious()
            {
                return MobAttackMode.ONLY_HOSTILES;
            }
            
            @Override
            public final MobAttackMode getNext()
            {
                return MobAttackMode.ALL;
            }
        };
        public static final MobAttackMode ALL = new MobAttackMode((byte)3, (byte)0b00000011)
        {
            @Override
            public final boolean test(Mob mob)
            {
                return true;
            }
            
            @Override
            public final MobAttackMode getPrevious()
            {
                return MobAttackMode.IGNORE_PASSIVE;
            }
            
            @Override
            public final MobAttackMode getNext()
            {
                return MobAttackMode.OFF;
            }
        };
        public static final MobAttackMode DEFAULT = MobAttackMode.OFF;
        
        public static final byte FLAG_MASK = (byte)0b00000011;
        
        public static final MobAttackMode get(byte value)
        {
            switch (value)
            {
                case 0:
                    return MobAttackMode.OFF;
                case 1:
                    return MobAttackMode.ONLY_HOSTILES;
                case 2:
                    return MobAttackMode.IGNORE_PASSIVE;
                case 3:
                    return MobAttackMode.ALL;
                default:
                    return MobAttackMode.OFF;
            }
        }
        
        public static final MobAttackMode getValueFromFlags(byte flags)
        {
            int attackValue = flags & 0b00000011;
            switch (attackValue)
            {
                case 0:
                    return MobAttackMode.OFF;
                case 1:
                    return MobAttackMode.ONLY_HOSTILES;
                case 2:
                    return MobAttackMode.IGNORE_PASSIVE;
                case 3:
                    return MobAttackMode.ALL;
                default:
                    return MobAttackMode.OFF;
            }
        }
    }
    
    /**
     * The player attack mode of the turret.
     */
    public static abstract class PlayerAttackMode
    {
        private final byte _value;
        private final byte _flagValue;
        
        private PlayerAttackMode(byte value, byte flagValue)
        {
            this._value = value;
            this._flagValue = flagValue;
        }
        
        public final byte value()
        {
            return this._value;
        }
        
        public final byte getFlagValue()
        {
            return this._flagValue;
        }
        
        public abstract boolean test(UUID ownerId, UUID targetPlayerId);
        
        public abstract PlayerAttackMode getPrevious();
        
        public abstract PlayerAttackMode getNext();
        
        
        public static final PlayerAttackMode OFF = new PlayerAttackMode((byte)0, (byte)0b00000000)
        {
            @Override
            public final boolean test(UUID ownerId, UUID targetPlayerId)
            {
                return false;
            }
            
            @Override
            public final PlayerAttackMode getPrevious()
            {
                return PlayerAttackMode.IGNORE_OWNER;
            }
            
            @Override
            public final PlayerAttackMode getNext()
            {
                if (Techguns.FACTIONS_HANDLER instanceof Techguns.NoFactionsHandler)
                {
                    return PlayerAttackMode.IGNORE_OWNER;
                }
                
                return PlayerAttackMode.ONLY_ENEMIES;
            }
        };
        public static final PlayerAttackMode ONLY_ENEMIES = new PlayerAttackMode((byte)1, (byte)0b00000100)
        {
            @Override
            public final boolean test(UUID ownerId, UUID targetPlayerId)
            {
                return !ownerId.equals(targetPlayerId) && Techguns.FACTIONS_HANDLER.isEnemy(ownerId, targetPlayerId);
            }
            
            @Override
            public final PlayerAttackMode getPrevious()
            {
                return PlayerAttackMode.OFF;
            }
            
            @Override
            public final PlayerAttackMode getNext()
            {
                if (Techguns.FACTIONS_HANDLER instanceof Techguns.NoFactionsHandler)
                {
                    return PlayerAttackMode.IGNORE_OWNER;
                }
                
                return PlayerAttackMode.IGNORE_ALLIES_AND_TEAM_MEMBERS;
            }
        };
        public static final PlayerAttackMode IGNORE_ALLIES_AND_TEAM_MEMBERS = new PlayerAttackMode((byte)2, (byte)0b00001000)
        {
            @Override
            public final boolean test(UUID ownerId, UUID targetPlayerId)
            {
                return !ownerId.equals(targetPlayerId) && !Techguns.FACTIONS_HANDLER.isAlliedOrTeamMember(ownerId, targetPlayerId);
            }
            
            @Override
            public final PlayerAttackMode getPrevious()
            {
                if (Techguns.FACTIONS_HANDLER instanceof Techguns.NoFactionsHandler)
                {
                    return PlayerAttackMode.OFF;
                }
                
                return PlayerAttackMode.ONLY_ENEMIES;
            }
            
            @Override
            public final PlayerAttackMode getNext()
            {
                if (Techguns.FACTIONS_HANDLER instanceof Techguns.NoFactionsHandler)
                {
                    return PlayerAttackMode.IGNORE_OWNER;
                }
                
                return PlayerAttackMode.IGNORE_TEAM_MEMBERS;
            }
        };
        public static final PlayerAttackMode IGNORE_TEAM_MEMBERS = new PlayerAttackMode((byte)3, (byte)0b00001100)
        {
            @Override
            public final boolean test(UUID ownerId, UUID targetPlayerId)
            {
                return !ownerId.equals(targetPlayerId) && !Techguns.FACTIONS_HANDLER.isTeamMember(ownerId, targetPlayerId);
            }
            
            @Override
            public final PlayerAttackMode getPrevious()
            {
                if (Techguns.FACTIONS_HANDLER instanceof Techguns.NoFactionsHandler)
                {
                    return PlayerAttackMode.OFF;
                }
                
                return PlayerAttackMode.IGNORE_ALLIES_AND_TEAM_MEMBERS;
            }
            
            @Override
            public final PlayerAttackMode getNext()
            {
                return PlayerAttackMode.IGNORE_OWNER;
            }
        };
        public static final PlayerAttackMode IGNORE_OWNER = new PlayerAttackMode((byte)4, (byte)0b00010000)
        {
            @Override
            public final boolean test(UUID ownerId, UUID targetPlayerId)
            {
                return !ownerId.equals(targetPlayerId);
            }
            
            @Override
            public final PlayerAttackMode getPrevious()
            {
                if (Techguns.FACTIONS_HANDLER instanceof Techguns.NoFactionsHandler)
                {
                    return PlayerAttackMode.OFF;
                }
                
                return PlayerAttackMode.IGNORE_TEAM_MEMBERS;
            }
            
            @Override
            public final PlayerAttackMode getNext()
            {
                return PlayerAttackMode.OFF;
            }
        };
        
        public static final PlayerAttackMode DEFAULT = PlayerAttackMode.OFF;
        
        public static final byte FLAG_MASK = (byte)0b00011100;
        
        public static final PlayerAttackMode get(byte value)
        {
            switch (value)
            {
                case 0:
                    return PlayerAttackMode.OFF;
                case 1:
                    return PlayerAttackMode.ONLY_ENEMIES;
                case 2:
                    return PlayerAttackMode.IGNORE_ALLIES_AND_TEAM_MEMBERS;
                case 3:
                    return PlayerAttackMode.IGNORE_TEAM_MEMBERS;
                case 4:
                    return PlayerAttackMode.IGNORE_OWNER;
                default:
                    return PlayerAttackMode.OFF;
            }
        }
        
        public static final PlayerAttackMode getValueFromFlags(byte flags)
        {
            int attackValue = (flags & 0b00011100) >> 2;
            switch (attackValue)
            {
                case 0:
                    return PlayerAttackMode.OFF;
                case 1:
                    return PlayerAttackMode.ONLY_ENEMIES;
                case 2:
                    return PlayerAttackMode.IGNORE_ALLIES_AND_TEAM_MEMBERS;
                case 3:
                    return PlayerAttackMode.IGNORE_TEAM_MEMBERS;
                case 4:
                    return PlayerAttackMode.IGNORE_OWNER;
                default:
                    return PlayerAttackMode.OFF;
            }
        }
    }

    
}
