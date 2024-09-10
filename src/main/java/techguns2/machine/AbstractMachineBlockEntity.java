package techguns2.machine;

import java.util.function.Function;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import techguns2.api.SecurityMode;
import techguns2.block.entity.BasePoweredBlockEntity;
import techguns2.util.DirtyableResult;
import techguns2.util.ItemStackContainer;

public abstract class AbstractMachineBlockEntity extends BasePoweredBlockEntity
{
    protected final LazyOptional<IItemHandler> _itemHandlerCapability;
    protected final ItemStackContainerInventory _itemContainer;
    protected boolean _contentsChanged;
    
    protected AbstractMachineBlockEntity(
            BlockEntityType<?> type,
            BlockPos blockPos,
            BlockState blockState,
            int inventorySize)
    {
        super(type, blockPos, blockState);
        
        this._itemContainer = new ItemStackContainerInventory(this, inventorySize)
        {
            @Override
            protected final void onContentsChanged(int slot)
            {
                AbstractMachineBlockEntity.this._contentsChanged = true;
                super.onContentsChanged(slot);
            }
        };
        this._itemHandlerCapability = LazyOptional.of(() -> this._itemContainer);
    }
    protected AbstractMachineBlockEntity(
            BlockEntityType<?> type,
            BlockPos blockPos,
            BlockState blockState,
            Function<AbstractMachineBlockEntity, ItemStackContainerInventory> itemStackContainerFactory)
    {
        super(type, blockPos, blockState);
        
        this._itemContainer = itemStackContainerFactory.apply(this);
        this._itemHandlerCapability = LazyOptional.of(() -> this._itemContainer);
    }
    
    public abstract String getMachineName();
    
    @Nullable
    public abstract IMachineOperation getCurrentOperation();
    
    public abstract boolean hasOperation();
    
    public boolean canProcess()
    {
        return true;
    }
    
    protected void clientTick()
    {
        if (!this.isActive() || !this.canProcess())
            return;
        

        if (!this.hasOperation())
            return;
        
        if (this.operationClientTick())
        {
            this.playAmbienceSounds();
        }
    }
    
    protected void serverTick()
    {
        if (!this.isActive() || !this.canProcess())
            return;
        
        if (this.hasOperation())
        {
            DirtyableResult tickResult = this.operationServerTick();
            if (tickResult.success)
            {
                this.playAmbienceSounds();
                
                if (!this.hasOperation() || this.getCurrentOperation().isComplete())
                {
                    this.onOperationComplete();
                    this.fetchNextOperation();
                    this.notifyUpdate();
                    return;
                }
            }
            
            if (tickResult.dirty)
                this.notifyUpdate();
            
            return;
        }
        
        if (this._contentsChanged)
        {
            this.fetchNextOperation();
            this.notifyUpdate();
        }
    }
    
    public void tick()
    {
        if (this.level.isClientSide)
            this.clientTick();
        else
            this.serverTick();
    }

    protected abstract boolean fetchNextOperation();
    protected abstract DirtyableResult operationServerTick();
    protected abstract boolean operationClientTick();
    protected abstract void onOperationComplete();
    protected abstract void playAmbienceSounds();
    
    @Override
    public int getContainerSize()
    {
        return this._itemContainer.getSlots();
    }
    
    @Override
    public void clearContent()
    {
        this._itemContainer.clear();
    }
    
    @Override
    public boolean isEmpty()
    {
        return this._itemContainer.isEmpty();
    }
    
    @Override
    public ItemStack getItem(int slot)
    {
        return this._itemContainer.getStackInSlot(slot);
    }
    
    @Override
    public ItemStack removeItem(int slot, int amount)
    {
        return this._itemContainer.extractItem(slot, amount, true);
    }
    
    @Override
    public boolean canPlaceItem(int slot, ItemStack itemStack)
    {
        return this._itemContainer.canPlaceItemInSlot(slot, itemStack);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot)
    {
        // I dunno if this is the best way of doing this.
        ItemStack itemStack = this._itemContainer.getStackInSlot(slot);
        this._itemContainer.setStackInSlot(slot, ItemStack.EMPTY);
        return itemStack;
    }
    
    @Override
    public void setItem(int slot, ItemStack stack)
    {
        this._itemContainer.setStackInSlot(slot, stack);
    }
    
    @Override
    public boolean stillValid(Player player)
    {
        return Container.stillValidBlockEntity(this, player);
    }
    
    @Override
    public void dropContents()
    {
        super.dropContents();
        
        IMachineOperation currentOperation = this.getCurrentOperation();
        if (currentOperation == null)
            return;
        
        BlockPos pos = this.getBlockPos();
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();
        
        currentOperation.dropContents(this.level, x, y, z);
    }
    
    @Override
    protected void read(CompoundTag tag, boolean clientPacket)
    {
        super.read(tag, clientPacket);
        
        if (tag.contains("Inventory", Tag.TAG_COMPOUND))
            this._itemContainer.deserializeNBT(tag.getCompound("Inventory"));
        else
            this._itemContainer.reset();
    }
    
    @Override
    protected void write(CompoundTag tag, boolean clientPacket)
    {
        super.write(tag, clientPacket);
        
        tag.put("Inventory", this._itemContainer.serializeNBT());
    }
    
    @SuppressWarnings("unchecked")
    @Override
    @NotNull 
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap)
    {
        if (cap == ForgeCapabilities.ITEM_HANDLER)
            return (LazyOptional<T>)this._itemHandlerCapability;
        
        return super.getCapability(cap);
    }
    
    public static final int DATA_SLOT_ENERGY_CAPACITY = 0;
    public static final int DATA_SLOT_STORED_ENERGY = DATA_SLOT_ENERGY_CAPACITY + 1;
    public static final int DATA_SLOT_FLAGS = DATA_SLOT_STORED_ENERGY + 1;
    
    public static final int TICKED_DATA_SLOT_OPERATION_TOTAL_TICKS = DATA_SLOT_FLAGS + 1;
    public static final int TICKED_DATA_SLOT_OPERATION_TICKS_REMAINING = TICKED_DATA_SLOT_OPERATION_TOTAL_TICKS + 1;
    
    public static final int NEXT_DATA_SLOT = DATA_SLOT_FLAGS + 1;
    public static final int TICKED_NEXT_DATA_SLOT = TICKED_DATA_SLOT_OPERATION_TICKS_REMAINING + 1;
    
    protected static class ItemStackContainerInventory extends ItemStackContainer
    {
        protected final AbstractMachineBlockEntity _blockEntity;
        
        public ItemStackContainerInventory(AbstractMachineBlockEntity blockEntity, int size)
        {
            super(size);
            
            this._blockEntity = blockEntity;
        }
        
        @Override
        protected void onContentsChanged(int slot)
        {
            this._blockEntity._contentsChanged = true;
            super.onContentsChanged(slot);
        }
        
        @Override
        public boolean dynamicSize()
        {
            return false;
        }
    }
    
    protected static abstract class TickedContainerData extends ContainerData
    {
        public abstract int getOperationTotalTicks();
        public abstract int getOperationTicksRemaining();
        
        @Override
        public int get(int index)
        {
            switch (index)
            {
                case TICKED_DATA_SLOT_OPERATION_TOTAL_TICKS:
                    return this.getOperationTotalTicks();
                case TICKED_DATA_SLOT_OPERATION_TICKS_REMAINING:
                    return this.getOperationTicksRemaining();
                default:
                    return super.get(index);
            }
        }
        
        @Override
        public int getCount()
        {
            return TICKED_NEXT_DATA_SLOT;
        }
    }
    
    protected static abstract class ContainerData implements AbstractMachineContainerData
    {
        @Override
        @NotNull
        public abstract AbstractMachineBlockEntity getBlockEntity();
        
        @Override
        public final byte getFlags()
        {
            return this.getBlockEntity().getFlags();
        }
        
        @Override
        public final void setFlags(byte value)
        {
            this.getBlockEntity().setFlags(value);
        }
        
        @Override
        public final boolean isActive()
        {
            return this.getBlockEntity()._isActive;
        }
        
        @Override
        public final RedstoneBehaviour getRedstoneBehaviour()
        {
            return this.getBlockEntity().getRestoneBehaviour();
        }
        
        @Override
        public final void setRedstoneBehaviour(RedstoneBehaviour behaviour)
        {
            this.getBlockEntity().setRestoneBehaviour(behaviour);
        }
        
        @Override
        public final SecurityMode getSecurityMode()
        {
            return this.getBlockEntity().getSecurityMode();
        }
        
        @Override
        public final void setSecurityMode(SecurityMode securityMode)
        {
            this.getBlockEntity().setSecurityMode(securityMode);
        }
        
        @Override
        public final int getStoredEnergy()
        {
            return this.getBlockEntity().getEnergyStored();
        }
        
        @Override
        public final int getEnergyCapacity()
        {
            return this.getBlockEntity().getEnergyCapacity();
        }
        
        @Override
        public final float getOperationProgress()
        {
            IMachineOperation currentOperation = this.getBlockEntity().getCurrentOperation();
            
            if (currentOperation != null)
                return currentOperation.progress();
            else
                return 0;
        }
        
        @Override
        public final int getOperationProgressScaled(int limit)
        {
            IMachineOperation currentOperation = this.getBlockEntity().getCurrentOperation();
            
            if (currentOperation != null)
                return currentOperation.progressScaled(limit);
            else
                return 0;
        }
        
        @Override
        public int get(int index)
        {
            switch (index)
            {
                case DATA_SLOT_ENERGY_CAPACITY:
                    return this.getEnergyCapacity();
                case DATA_SLOT_STORED_ENERGY:
                    return this.getStoredEnergy();
                case DATA_SLOT_FLAGS:
                    return this.getFlags();
                default:
                    return 0;
            }
        }

        @Override
        public void set(int index, int value)
        {
            if (index != DATA_SLOT_FLAGS)
                return;

            this.getBlockEntity().setFlags((byte)(value & 255));
        }
        
        @Override
        public int getCount()
        {
            return NEXT_DATA_SLOT;
        }
    }
}
