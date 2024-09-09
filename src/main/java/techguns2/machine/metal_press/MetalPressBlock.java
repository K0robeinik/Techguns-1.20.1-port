package techguns2.machine.metal_press;

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

public final class MetalPressBlock extends AbstractMachineBlock
{
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final VoxelShape SHAPE = Block.box(1, 0, 1, 15, 16, 15);
    
    public MetalPressBlock(Properties properties)
    {
        super(properties);
        
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }
    
    @Override
    public final RenderShape getRenderShape(BlockState p_49232_)
    {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }
    
    @Override
    public final VoxelShape getVisualShape(BlockState p_60479_, BlockGetter p_60480_, BlockPos p_60481_,
            CollisionContext p_60482_)
    {
        return Shapes.empty();
    }
    
    @Override
    public final VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_)
    {
        return SHAPE;
    }
    
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext p_49820_)
    {
        return this.defaultBlockState()
                .setValue(FACING, p_49820_.getHorizontalDirection());
    }
    
    public final float getYRotationDegrees(BlockState blockState)
    {
        return blockState.getValue(FACING).toYRot();
    }
    
    @Override
    protected InteractionResult tryOpenBlockEntityContainer(AbstractMachineBlockEntity abstractBlockEntity,
            BlockState blockState, Level level, BlockPos blockPos, ServerPlayer player, InteractionHand hand,
            BlockHitResult hitResult)
    {
        if (!(abstractBlockEntity instanceof MetalPressBlockEntity blockEntity))
            return InteractionResult.FAIL;
        
        this.openBlockEntityContainer(player, blockEntity);
        return InteractionResult.SUCCESS;
    }
    
    @Override
    public final void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, LivingEntity byEntity, ItemStack sourceItem)
    {
        if (!(level.getBlockEntity(blockPos) instanceof MetalPressBlockEntity metalPressBlockEntity))
            return;
        
        if (sourceItem.hasCustomHoverName())
            metalPressBlockEntity.setCustomName(sourceItem.getHoverName());
        
        if (byEntity instanceof Player player)
            metalPressBlockEntity.setOwner(player);
        else
            metalPressBlockEntity.setOwner(null);
    }

    @Override
    public final MetalPressBlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState)
    {
        return new MetalPressBlockEntity(blockPos, blockState);
    }
    
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_,
            BlockEntityType<T> p_153214_)
    {
        return createTickerHelper(p_153214_, TGBlockEntityTypes.METAL_PRESS.get(), MetalPressBlockEntity::tick);
    }
    
    @Override
    protected final void createBlockStateDefinition(Builder<Block, BlockState> builder)
    {
        builder.add(MetalPressBlock.FACING);
    }
}
