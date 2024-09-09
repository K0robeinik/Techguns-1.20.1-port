package techguns2.machine;

import java.lang.reflect.Array;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import techguns2.Techguns;

public abstract class AbstractMultiBlockMachineControllerBlockEntity<
        TPart extends AbstractMultiBlockMachinePartBlockEntity<TPart, TController>,
        TController extends AbstractMultiBlockMachineControllerBlockEntity<TPart, TController>
    >
    extends AbstractMachineBlockEntity
{
    private final Class<TPart> _partClass;
    private final TPart[] _parts;
    protected final BlockPos[] _partPositions;
    protected boolean _formed;
    

    @SuppressWarnings("unchecked")
    public AbstractMultiBlockMachineControllerBlockEntity(
            Class<TPart> partClass,
            int xSize,
            int ySize,
            int zSize,
            BlockEntityType<?> type,
            BlockPos blockPos,
            BlockState blockState,
            int inventorySize)
    {
        super(type, blockPos, blockState, inventorySize);
        
        this._partClass = partClass;
        // subtract one to remove the controller
        int partCount = (xSize * ySize * zSize) - 1;
        
        this._parts = (TPart[])Array.newInstance(partClass, partCount);
        this._partPositions = new BlockPos[partCount];
    }
    
    @SuppressWarnings("unchecked")
    public AbstractMultiBlockMachineControllerBlockEntity(
            Class<TPart> partClass,
            int xSize,
            int ySize,
            int zSize,
            BlockEntityType<?> type,
            BlockPos blockPos,
            BlockState blockState,
            Function<AbstractMachineBlockEntity, ItemStackContainerInventory> itemStackContainerFactory)
    {
        super(type, blockPos, blockState, itemStackContainerFactory);
        
        this._partClass = partClass;
        // subtract one to remove the controller
        int partCount = (xSize * ySize * zSize) - 1;
        
        this._parts = (TPart[])Array.newInstance(partClass, partCount);
        this._partPositions = new BlockPos[partCount];
    }
    
    public final boolean formed()
    {
        return this._formed;
    }
    
    @Override
    public byte getFlags()
    {
        return (byte)(super.getFlags() |
                (this._formed ?
                        FORMED_FLAG :
                        0b00000000));
    }
    
    @Override
    public void setFlags(byte flagValue)
    {
        this._formed = (flagValue & FORMED_FLAG) == FORMED_FLAG;
        super.setFlags(flagValue);
    }
    
    void onPartDestroyed(BlockPos sourcePartPos)
    {
        if (!this._formed)
            return;
        
        BlockPos partPos;
        for (int index = 0; index < this._partPositions.length; index++)
        {
            partPos = this._partPositions[index];
            
            if (!partPos.equals(sourcePartPos))
            {
                this.level.setBlock(partPos, this._parts[index].getUnformedBlockState(), Block.UPDATE_ALL);
            }
            
            this._partPositions[index] = null;
            this._parts[index] = null;
        }
        this._formed = false;
        this.sendData();
    }
    
    public final void form(Direction fromSide)
    {
        if (this._formed || this.level.isClientSide)
            return;
        
        this._formed = this.tryForm(fromSide);
        if (this._formed)
        {
            for (int index = 0; index < this._partPositions.length; index++)
            {
                BlockPos partPos = this._partPositions[index];
                
                if (partPos == null)
                {
                    Techguns.LOGGER.error("Failed to form {} multi block machine: tryForm returned true, but part position in array was null. (Controller position: {}, Part index: {})",
                            this.getMachineName(),
                            this.getBlockPos(), 
                            index);
                    this._formed = false;
                    break;
                }
                
                BlockEntity blockEntity = this.level.getBlockEntity(partPos);

                if (blockEntity == null)
                {
                    Techguns.LOGGER.error("Failed to form {} multi block machine: part block entity doesn't exist. (Controller position: {}, Part position: {}, Part index: {})",
                            this.getMachineName(),
                            this.getBlockPos(),
                            partPos,
                            index);
                    this._formed = false;
                    break;
                }
                
                if (!this._partClass.isInstance(blockEntity))
                {
                    Techguns.LOGGER.error("Failed to form {} multi block machine: part block entity is not an instance of {}. (Controller position: {}, Part position: {}, Part index: {})",
                            this.getMachineName(),
                            this._partClass,
                            this.getBlockPos(),
                            partPos,
                            index);
                    this._formed = false;
                    break;
                }
                
                TPart part = this._partClass.cast(blockEntity);
                
                if (part.isValidController(this))
                {
                    Techguns.LOGGER.error("Failed to form {} multi block machine: part block entity doesn't support this controller. (Controller position: {}, Part position: {}, Part index: {})",
                            this.getMachineName(),
                            this._partClass,
                            this.getBlockPos(),
                            partPos,
                            index);
                    this._formed = false;
                    break;
                }
                
                this._parts[index] = part;
            }
        }
        
        if (this._formed)
        {
            for (int index = 0; index < this._parts.length; index++)
            {
                this._parts[index].form(this);
            }
            
            this.sendData();
        }
        else
        {
            for (int index = 0; index < this._partPositions.length; index++)
            {
                this._partPositions[index] = null;
                this._parts[index] = null;
            }
        }
    }

    /**
     * 
     * @implNote When returning true, update
     * {@link #_partPositions} with the positions of the parts.
     * @return
     */
    protected abstract boolean tryForm(Direction fromSide);
    
    @Override
    protected void read(CompoundTag tag, boolean clientPacket)
    {
        boolean wasFormed = this._formed;
        
        super.read(tag, clientPacket);
        
        wasFormed = !wasFormed && this._formed;
        
        if (this._formed)
        {
            ListTag partPositionsTag = tag.getList("PartPositions", Tag.TAG_COMPOUND);
            int partPositionsCount = partPositionsTag.size();
            
            if (this._partPositions.length != partPositionsCount)
            {
                Techguns.LOGGER.error("Mismatch whilst reading data for {} multi block machine: Expected {} part position(s), data had {} part position(s). Setting 'formed' to false!",
                        this.getMachineName(),
                        this._partPositions.length,
                        partPositionsCount);
                
                this._formed = false;
                return;
            }
            
            CompoundTag partPositionTag;
            BlockPos newPartPosition;
            
            for (int index = 0; index < partPositionsCount; index++)
            {
                partPositionTag = partPositionsTag.getCompound(index);
                newPartPosition = new BlockPos(
                        partPositionTag.getInt("x"),
                        partPositionTag.getInt("y"),
                        partPositionTag.getInt("z"));
                
                // assumption: part block entity is loaded
                // assumption: part positions and part block entities are synchronized
                if (wasFormed ||
                        this._partPositions[index] == null ||
                        !this._partPositions[index].equals(newPartPosition))
                {
                    this._partPositions[index] = newPartPosition;
                    this._parts[index] = this._partClass.cast(this.level.getBlockEntity(newPartPosition));
                }
            }
        }
        else
        {
            for (int index = 0; index < this._partPositions.length; index++)
            {
                this._partPositions[index] = null;
                this._parts[index] = null;
            }
        }
    }
    
    @Override
    protected void write(CompoundTag tag, boolean clientPacket)
    {
        super.write(tag, clientPacket);
        
        if (this._formed)
        {
            ListTag partPositionsTag = new ListTag();
            CompoundTag partPositionTag;
            BlockPos partPosition;
            for (int index = 0; index < this._partPositions.length; index++)
            {
                partPosition = this._partPositions[index];
                
                partPositionTag = new CompoundTag();
                partPositionTag.putInt("x", partPosition.getX());
                partPositionTag.putInt("y", partPosition.getY());
                partPositionTag.putInt("z", partPosition.getZ());
                
                partPositionsTag.add(partPositionTag);
            }
            
            tag.put("PartPositions", partPositionsTag);
        }
    }
    
    public static final byte FORMED_FLAG = (byte)0b01000000;

    protected static abstract class TickedContainerData extends AbstractMachineBlockEntity.TickedContainerData implements AbstractMultiBlockMachineContainerData
    {
        @Override
        @NotNull
        public abstract AbstractMultiBlockMachineControllerBlockEntity<?, ?> getBlockEntity();
        
        @Override
        public final boolean isFormed()
        {
            return this.getBlockEntity()._formed;
        }
    }
    
    protected static abstract class ContainerData extends AbstractMachineBlockEntity.ContainerData implements AbstractMultiBlockMachineContainerData
    {
        @Override
        @NotNull
        public abstract AbstractMultiBlockMachineControllerBlockEntity<?, ?> getBlockEntity();
        
        @Override
        public final boolean isFormed()
        {
            return this.getBlockEntity()._formed;
        }
    }
}
