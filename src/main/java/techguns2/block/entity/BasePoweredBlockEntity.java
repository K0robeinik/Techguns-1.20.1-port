package techguns2.block.entity;


import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

public abstract class BasePoweredBlockEntity extends BaseRedstoneBlockEntity
{
    protected final LazyOptional<IEnergyStorage> _energyStorageCapability;
    protected int _storedEnergy;
    
    protected BasePoweredBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState)
    {
        super(type, blockPos, blockState);
        
        this._energyStorageCapability = LazyOptional.of(() -> new IEnergyStorage()
        {
            @Override
            public final int receiveEnergy(int maxReceive, boolean simulate)
            {
                if (simulate)
                    return BasePoweredBlockEntity.this.simulateReceiveEnergy(maxReceive);
                else
                    return BasePoweredBlockEntity.this.receiveEnergy(maxReceive);
            }
            
            @Override
            public final int extractEnergy(int maxExtract, boolean simulate)
            {
                if (simulate)
                    return BasePoweredBlockEntity.this.simulateDrainEnergy(maxExtract);
                else
                    return BasePoweredBlockEntity.this.drainEnergy(maxExtract);
            }
            
            @Override
            public final int getEnergyStored()
            {
                return BasePoweredBlockEntity.this.getEnergyStored();
            }
            
            @Override
            public final int getMaxEnergyStored()
            {
                return BasePoweredBlockEntity.this.getEnergyCapacity();
            }
            
            @Override
            public final boolean canExtract()
            {
                return BasePoweredBlockEntity.this.getEnergyStored() > 0;
            }
            
            @Override
            public final boolean canReceive()
            {
                return BasePoweredBlockEntity.this.getEnergyStored() >= BasePoweredBlockEntity.this.getEnergyCapacity();
            }
        });
        
        this._storedEnergy = 0;
    }
    
    public final int getEnergyStored()
    {
        return this._storedEnergy;
    }
    
    public abstract int getEnergyCapacity();
    protected void readEnergyCapacity(CompoundTag tag, boolean clientPacket)
    { }
    protected void writeEnergyCapacity(CompoundTag tag, boolean clientPacket)
    { }
    
    protected final boolean tryConsumeEnergy(int amount)
    {
        if (this._storedEnergy < amount)
            return false;

        this._storedEnergy -= amount;
        return true;
    }
    
    public final int simulateReceiveEnergy(int amount)
    {
        if (amount == 0)
            return 0;
        else if (amount < 0)
            return this.simulateDrainEnergy(-amount);
        
        return Math.min(this.getEnergyCapacity() - this._storedEnergy, amount);
    }
    
    public final int receiveEnergy(int amount)
    {
        if (amount == 0)
            return 0;
        else if (amount < 0)
            return this.drainEnergy(-amount);
        
        int toReceive = Math.min(this.getEnergyCapacity() - this._storedEnergy, amount);
        this._storedEnergy += toReceive;
        return toReceive;
    }
    
    public final int simulateDrainEnergy(int amount)
    {
        if (amount == 0)
            return 0;
        else if (amount < 0)
            return this.simulateDrainEnergy(-amount);
        
        return Math.min(this._storedEnergy, amount);
    }
    
    public final int drainEnergy(int amount)
    {
        if (amount == 0)
            return 0;
        else if (amount < 0)
            return this.receiveEnergy(-amount);

        int toDrain = Math.min(this._storedEnergy, amount);
        this._storedEnergy -= toDrain;
        return toDrain;
    }
    
    @Override
    protected void read(CompoundTag tag, boolean clientPacket)
    {
        super.read(tag, clientPacket);
        
        this.readEnergyCapacity(tag, clientPacket);

        if (tag.contains("Energy", Tag.TAG_INT))
            this._storedEnergy = Math.max(0, Math.min(tag.getInt("Energy"), this.getEnergyCapacity()));
        else
            this._storedEnergy = 0;
    }
    
    @Override
    protected void write(CompoundTag tag, boolean clientPacket)
    {
        super.write(tag, clientPacket);
        
        this.writeEnergyCapacity(tag, clientPacket);
        
        tag.putInt("Energy", this._storedEnergy);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    @NotNull 
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap)
    {
        if (cap == ForgeCapabilities.ENERGY)
            return (LazyOptional<T>)this._energyStorageCapability;
        
        return super.getCapability(cap);
    }
}
