package techguns2.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class AbstractMultiBlockControllerBlock<
        TPartBlockEntity extends AbstractMultiBlockMachinePartBlockEntity<TPartBlockEntity, TControllerBlockEntity>,
        TControllerBlockEntity extends AbstractMultiBlockMachineControllerBlockEntity<TPartBlockEntity, TControllerBlockEntity>
    > extends AbstractMachineBlock
{
    private final Class<TControllerBlockEntity> _controllerBlockEntityClass;
    
    public AbstractMultiBlockControllerBlock(
            Class<TControllerBlockEntity> controllerBlockEntityClass,
            Properties properties)
    {
        super(properties);
        
        this._controllerBlockEntityClass = controllerBlockEntityClass;
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public final VoxelShape getVisualShape(
            BlockState blockState,
            BlockGetter blockGetter,
            BlockPos blockPos,
            CollisionContext collisionContext)
    {
        return this.getShape(blockState, blockGetter, blockPos, collisionContext);
    }
    
    @Override
    public void destroy(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState)
    {
        super.destroy(levelAccessor, blockPos, blockState);
        
        if (levelAccessor.isClientSide())
            return;
        
        BlockEntity blockEntity = levelAccessor.getBlockEntity(blockPos);
        if (blockEntity == null || this._controllerBlockEntityClass.isInstance(blockEntity))
        {
            return;
        }
        
        TControllerBlockEntity controllerBlockEntity = this._controllerBlockEntityClass.cast(blockEntity);
        if (controllerBlockEntity == null)
        {
            return;
        }
        
        controllerBlockEntity.onPartDestroyed(blockPos);
    }
    

    @Override
    protected InteractionResult tryOpenBlockEntityContainer(
            AbstractMachineBlockEntity abstractBlockEntity,
            BlockState blockState,
            Level level,
            BlockPos blockPos,
            ServerPlayer player,
            InteractionHand hand,
            BlockHitResult hitResult)
    {
        if (!this._controllerBlockEntityClass.isInstance(abstractBlockEntity))
            return InteractionResult.FAIL;
        
        TControllerBlockEntity blockEntity = this._controllerBlockEntityClass.cast(abstractBlockEntity);
        
        if (blockEntity.formed() || player.isShiftKeyDown())
        {
            this.openBlockEntityContainer(player, blockEntity);
            return InteractionResult.SUCCESS;
        }
        
        blockEntity.form(hitResult.getDirection());
        if (blockEntity.formed())
            return InteractionResult.SUCCESS;
        else
            return InteractionResult.FAIL;
    }
    
    @Override
    public abstract TControllerBlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState);
}
