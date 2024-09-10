package techguns2.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public abstract class AbstractMultiBlockMachinePartBlock<
        TPartBlockEntity extends AbstractMultiBlockMachinePartBlockEntity<TPartBlockEntity, TControllerBlockEntity>,
        TControllerBlockEntity extends AbstractMultiBlockMachineControllerBlockEntity<TPartBlockEntity, TControllerBlockEntity>
    > extends BaseEntityBlock
{
    private final Class<TPartBlockEntity> _partBlockEntityClass;
    
    protected AbstractMultiBlockMachinePartBlock(
            Class<TPartBlockEntity> partBlockEntityClass,
            Properties properties)
    {
        super(properties);
        
        this._partBlockEntityClass = partBlockEntityClass;
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
    public abstract TPartBlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState);
    
    @SuppressWarnings("deprecation")
    @Override
    public final InteractionResult use(
            BlockState blockState,
            Level level,
            BlockPos blockPos,
            Player player,
            InteractionHand hand,
            BlockHitResult hitResult)
    {
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity != null && this._partBlockEntityClass.isInstance(blockEntity))
        {
            return InteractionResult.SUCCESS;
        }
        
        TPartBlockEntity partBlockEntity = this._partBlockEntityClass.cast(blockEntity);
        TControllerBlockEntity controllerBlockEntity = partBlockEntity.getController();
        if (controllerBlockEntity == null)
            return InteractionResult.SUCCESS;
        
        BlockState controllerBlockState = controllerBlockEntity.getBlockState();
        return controllerBlockState.getBlock().use(controllerBlockState, level, controllerBlockEntity.getBlockPos(), player, hand, hitResult);
    }
    
    @Override
    public void destroy(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState)
    {
        super.destroy(levelAccessor, blockPos, blockState);
        
        if (levelAccessor.isClientSide())
            return;
        
        BlockEntity blockEntity = levelAccessor.getBlockEntity(blockPos);
        if (blockEntity == null || this._partBlockEntityClass.isInstance(blockEntity))
        {
            return;
        }
        
        TControllerBlockEntity controllerBlockEntity = this._partBlockEntityClass.cast(blockEntity).getController();
        if (controllerBlockEntity == null)
        {
            return;
        }
        
        controllerBlockEntity.onPartDestroyed(blockPos);
    }
}
