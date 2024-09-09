package techguns2.machine;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractMultiBlockMachinePartBlockEntity<
        TPart extends AbstractMultiBlockMachinePartBlockEntity<TPart, TController>,
        TController extends AbstractMultiBlockMachineControllerBlockEntity<TPart, TController>
    > extends BlockEntity
{
    private final Class<TController> _controllerClass;
    protected BlockPos _controllerBlockPos;
    protected boolean _hasController;
    
    // TODO: Have redstone state of this part affect the controller
    
    @Nullable
    private TController _controller;
    
    public AbstractMultiBlockMachinePartBlockEntity(
            Class<TController> controllerClass,
            BlockEntityType<?> type,
            BlockPos blockPos,
            BlockState blockState)
    {
        super(type, blockPos, blockState);
        
        this._controllerClass = controllerClass;
    }
    
    protected final boolean isValidController(AbstractMultiBlockMachineControllerBlockEntity<TPart, TController> controller)
    {
        return this._controllerClass.isInstance(controller);
    }
    
    protected final void form(AbstractMultiBlockMachineControllerBlockEntity<TPart, TController> rawController)
    {
        TController controller = this._controllerClass.cast(rawController);
        
        this._controllerBlockPos = controller.getBlockPos();
        this._controller = controller;
        this._hasController = true;
    }
    
    public abstract BlockState getUnformedBlockState();
    
    public final boolean hasController()
    {
        return this._hasController;
    }
    
    @Nullable
    public final TController getController()
    {
        if (this._hasController)
        {
            if (this._controller == null)
            {
                BlockEntity blockEntity = this.level.getBlockEntity(this._controllerBlockPos);
                if (blockEntity != null && this._controllerClass.isInstance(blockEntity))
                {
                    this._controller = this._controllerClass.cast(blockEntity);
                }
            }
            
            return this._controller;
        }
        
        return null;
    }
    
    @Override
    protected void saveAdditional(CompoundTag tag)
    {
        super.saveAdditional(tag);
        this.saveNbt(tag, false);
    }
    
    protected void saveNbt(CompoundTag tag, boolean fromNetwork)
    {
        tag.putByte("HasController", this._hasController ? (byte)1 : (byte)0);
        if (this._hasController)
        {
            CompoundTag controllerBlockPosTag = new CompoundTag();
            controllerBlockPosTag.putInt("x", this._controllerBlockPos.getX());
            controllerBlockPosTag.putInt("y", this._controllerBlockPos.getY());
            controllerBlockPosTag.putInt("z", this._controllerBlockPos.getZ());
        }
    }
    
    @Override
    public void load(CompoundTag tag)
    {
        super.load(tag);
        this.loadNbt(tag, false);
    }
    
    protected void loadNbt(CompoundTag tag, boolean fromNetwork)
    {
        this._hasController = tag.contains("ControllerBlockPos", Tag.TAG_COMPOUND) &&
                tag.contains("HasController", Tag.TAG_BYTE) &&
                tag.getByte("HasController") != 0;
        
        if (this._hasController)
        {
            CompoundTag controllerBlockPosTag = tag.getCompound("ControllerBlockPos");
            
            this._hasController = controllerBlockPosTag.contains("x", Tag.TAG_INT) &&
                    controllerBlockPosTag.contains("y", Tag.TAG_INT) &&
                    controllerBlockPosTag.contains("z", Tag.TAG_INT);
            
            if (this._hasController)
            {
                this._controllerBlockPos = new BlockPos(
                        controllerBlockPosTag.getInt("x"),
                        controllerBlockPosTag.getInt("y"),
                        controllerBlockPosTag.getInt("z"));
            }
            else
                this._controllerBlockPos = BlockPos.ZERO;
        }
        else
            this._controllerBlockPos = BlockPos.ZERO;
        
        this._controller = null;
    }
    
    @Override
    public CompoundTag getUpdateTag()
    {
        CompoundTag tag = super.getUpdateTag();
        this.saveNbt(tag, true);
        return tag;
    }
    
    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt)
    {
        CompoundTag tag = pkt.getTag();
        if (tag == null)
            tag = new CompoundTag();
        
        this.loadNbt(tag, true);
    }
    
    @Override
    public final ClientboundBlockEntityDataPacket getUpdatePacket()
    {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
