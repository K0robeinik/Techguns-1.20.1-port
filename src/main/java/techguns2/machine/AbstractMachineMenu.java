package techguns2.machine;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import techguns2.api.SecurityMode;

public abstract class AbstractMachineMenu extends AbstractContainerMenu
{
    @Nullable
    protected final UUID _ownerId;
    
    public AbstractMachineMenu(MenuType<? extends AbstractMachineMenu> menuType, int id, @Nullable UUID ownerId)
    {
        super(menuType, id);
        
        this._ownerId = ownerId;
    }
    
    @Nullable
    public final UUID getOwnerId()
    {
        return this._ownerId;
    }
    
    public abstract AbstractMachineContainerData getData();

    protected static abstract class ClientTickedContainerData extends ClientContainerData
    {
        protected ClientTickedContainerData(int slotCount)
        {
            super(slotCount);
        }

        public int getOperationTotalTicks()
        {
            return this.get(AbstractMachineBlockEntity.TICKED_DATA_SLOT_OPERATION_TOTAL_TICKS);
        }
        public int getOperationTicksRemaining()
        {
            return this.get(AbstractMachineBlockEntity.TICKED_DATA_SLOT_OPERATION_TICKS_REMAINING);
        }
        
        @Override
        public final float getOperationProgress()
        {
            int totalOperationTicks = this.getOperationTotalTicks();
            if (totalOperationTicks > 0)
                return (float)(totalOperationTicks - this.getOperationTicksRemaining()) / totalOperationTicks;
            else
                return 0f;
        }
        
        @Override
        public final int getOperationProgressScaled(int limit)
        {
            int totalOperationTicks = this.getOperationTotalTicks();
            if (totalOperationTicks > 0)
                return ((totalOperationTicks - this.getOperationTicksRemaining()) * limit) / totalOperationTicks;
            else
                return 0;
        }
    }
    
    protected static abstract class ClientContainerData extends SimpleContainerData implements AbstractMachineContainerData
    {
        protected ClientContainerData(int slotCount)
        {
            super(slotCount);
        }
        
        @Override
        @Nullable 
        public AbstractMachineBlockEntity getBlockEntity()
        {
            return null;
        }

        @Override
        public final byte getFlags()
        {
            return (byte)(this.get(AbstractMachineBlockEntity.DATA_SLOT_FLAGS) & 255);
        }

        @Override
        public final void setFlags(byte flags)
        {
            this.set(AbstractMachineBlockEntity.DATA_SLOT_FLAGS, flags);
        }
        
        @Override
        public final boolean isActive()
        {
            return (this.getFlags() & AbstractMachineBlockEntity.IS_ACTIVE_FLAG) == AbstractMachineBlockEntity.IS_ACTIVE_FLAG;
        }

        @Override
        public final RedstoneBehaviour getRedstoneBehaviour()
        {
            return RedstoneBehaviour.getValueFromFlags(this.getFlags());
        }

        @Override
        public final void setRedstoneBehaviour(RedstoneBehaviour behaviour)
        {
            this.setFlags((byte)((this.getFlags() & ~RedstoneBehaviour.FLAG_MASK) | behaviour.getFlagValue()));
        }

        @Override
        public final SecurityMode getSecurityMode()
        {
            return SecurityMode.getValueFromFlags(this.getFlags());
        }

        @Override
        public final void setSecurityMode(SecurityMode mode)
        {
            this.setFlags((byte)((this.getFlags() & ~SecurityMode.FLAG_MASK) | mode.getFlagValue()));
        }

        @Override
        public final int getEnergyCapacity()
        {
            return this.get(AbstractMachineBlockEntity.DATA_SLOT_ENERGY_CAPACITY);
        }

        @Override
        public final int getStoredEnergy()
        {
            return this.get(AbstractMachineBlockEntity.DATA_SLOT_STORED_ENERGY);
        }
    }
}
