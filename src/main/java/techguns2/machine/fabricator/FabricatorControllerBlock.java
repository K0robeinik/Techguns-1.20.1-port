package techguns2.machine.fabricator;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import techguns2.TGBlockEntityTypes;
import techguns2.machine.AbstractMultiBlockControllerBlock;

public final class FabricatorControllerBlock extends AbstractMultiBlockControllerBlock<
        FabricatorPartBlockEntity,
        FabricatorControllerBlockEntity
    >
{
    public FabricatorControllerBlock(Properties properties)
    {
        super(FabricatorControllerBlockEntity.class, properties);
    }
    
    @Override
    public final RenderShape getRenderShape(BlockState blockState)
    {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }
    
    @Override
    public final void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, LivingEntity byEntity, ItemStack sourceItem)
    {
        if (!(level.getBlockEntity(blockPos) instanceof FabricatorControllerBlockEntity fabricatorControllerBlockEntity))
            return;
        
        if (sourceItem.hasCustomHoverName())
            fabricatorControllerBlockEntity.setCustomName(sourceItem.getHoverName());

        if (byEntity instanceof Player player)
            fabricatorControllerBlockEntity.setOwner(player);
        else
            fabricatorControllerBlockEntity.setOwner(null);
    }

    @Override
    public final FabricatorControllerBlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState)
    {
        return new FabricatorControllerBlockEntity(blockPos, blockState);
    }
    
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState,
            BlockEntityType<T> entityType)
    {
        return createTickerHelper(entityType, TGBlockEntityTypes.FABRICATOR_CONTROLLER.get(), FabricatorControllerBlockEntity::tick);
    }
}
