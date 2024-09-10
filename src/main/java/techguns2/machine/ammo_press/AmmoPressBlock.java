package techguns2.machine.ammo_press;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import techguns2.TGBlockEntityTypes;
import techguns2.machine.AbstractMachineBlock;
import techguns2.machine.AbstractMachineBlockEntity;

public final class AmmoPressBlock extends AbstractMachineBlock
{
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final VoxelShape SHAPE = Block.box(1, 0, 1, 15, 16, 15);
    
    public AmmoPressBlock(Properties properties)
    {
        super(properties);
        
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }
    
    @Override
    public final RenderShape getRenderShape(BlockState blockState)
    {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }
    
    @Override
    public final VoxelShape getVisualShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos,
            CollisionContext collisionContext)
    {
        return Shapes.empty();
    }
    
    @Override
    public final VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext)
    {
        return SHAPE;
    }
    
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection());
    }
    
    public final float getYRotationDegrees(BlockState blockState)
    {
        return blockState.getValue(FACING).toYRot();
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
        if (!(abstractBlockEntity instanceof AmmoPressBlockEntity blockEntity))
            return InteractionResult.FAIL;
        
        this.openBlockEntityContainer(player, blockEntity);
        return InteractionResult.SUCCESS;
    }
    
    @Override
    public final void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, LivingEntity byEntity, ItemStack sourceItem)
    {
        if (!(level.getBlockEntity(blockPos) instanceof AmmoPressBlockEntity ammoPressBlockEntity))
            return;
        
        if (sourceItem.hasCustomHoverName())
            ammoPressBlockEntity.setCustomName(sourceItem.getHoverName());

        if (byEntity instanceof Player player)
            ammoPressBlockEntity.setOwner(player);
        else
            ammoPressBlockEntity.setOwner(null);
    }

    @Override
    public final AmmoPressBlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState)
    {
        return new AmmoPressBlockEntity(blockPos, blockState);
    }
    
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState,
            BlockEntityType<T> entityType)
    {
        return createTickerHelper(entityType, TGBlockEntityTypes.AMMO_PRESS.get(), AmmoPressBlockEntity::tick);
    }
    
    @Override
    protected final void createBlockStateDefinition(Builder<Block, BlockState> builder)
    {
        builder.add(AmmoPressBlock.FACING);
    }
}
