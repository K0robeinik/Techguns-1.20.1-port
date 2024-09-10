package techguns2.machine;

import java.util.function.Consumer;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

public abstract class AbstractMachineBlock extends BaseEntityBlock
{
    public AbstractMachineBlock(Properties properties)
    {
        super(properties);
    }
    
    @Override
    public boolean propagatesSkylightDown(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos)
    {
        return true;
    }
    
    @Override
    public float getShadeBrightness(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos)
    {
        return 1F;
    }
    
    @Override
    public abstract VoxelShape getVisualShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos,
            CollisionContext collisionContext);
    
    protected final void openBlockEntityContainer(ServerPlayer serverPlayer, AbstractMachineBlockEntity blockEntity)
    {
        NetworkHooks.openScreen(serverPlayer, blockEntity, (buf) -> {
            var ownerId = blockEntity.getOwnerID();
            if (ownerId == null)
                buf.writeBoolean(false);
            else
            {
                buf.writeBoolean(true);
                buf.writeUUID(ownerId);
            }
        });
    }
    
    protected final void openBlockEntityContainer(ServerPlayer serverPlayer, AbstractMachineBlockEntity blockEntity, Consumer<FriendlyByteBuf> extraDataConsumer)
    {
        NetworkHooks.openScreen(serverPlayer, blockEntity, (buf) -> {
            var ownerId = blockEntity.getOwnerID();
            if (ownerId == null)
                buf.writeBoolean(false);
            else
            {
                buf.writeBoolean(true);
                buf.writeUUID(ownerId);
            }
            
            if (extraDataConsumer != null)
                extraDataConsumer.accept(buf);
        });
    }
    
    protected abstract InteractionResult tryOpenBlockEntityContainer(
            AbstractMachineBlockEntity abstractBlockEntity, 
            BlockState blockState,
            Level level,
            BlockPos blockPos,
            ServerPlayer player,
            InteractionHand hand,
            BlockHitResult hitResult);
    
    @Override
    public final InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        if (level.isClientSide || !(level.getBlockEntity(blockPos) instanceof AbstractMachineBlockEntity blockEntity) || !(player instanceof ServerPlayer serverPlayer))
            return InteractionResult.SUCCESS;
        
        if (!blockEntity.canPlayerAccess(serverPlayer))
        {
            player.sendSystemMessage(Component.translatable("techguns2.machine.no_access"));
            return InteractionResult.SUCCESS;
        }
        
        return this.tryOpenBlockEntityContainer(blockEntity, blockState, level, blockPos, serverPlayer, hand, hitResult);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block,
            BlockPos neighbor, boolean movedByPiston)
    {
        
        if (!(level.getBlockEntity(blockPos) instanceof AbstractMachineBlockEntity blockEntity))
        {
            super.neighborChanged(blockState, level, blockPos, block, neighbor, movedByPiston);
            return;
        }

        blockEntity.refreshRedstoneActiveState();
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public final void onRemove(BlockState p_60515_, Level p_60516_, BlockPos p_60517_, BlockState p_60518_, boolean p_60519_)
    {
        if (!p_60516_.isClientSide &&
                p_60515_.is(this) && 
                p_60516_.getBlockEntity(p_60517_) instanceof AbstractMachineBlockEntity blockEntity)
        {
            blockEntity.dropContents();
        }
        
        super.onRemove(p_60515_, p_60516_, p_60517_, p_60518_, p_60519_);
    }

}
