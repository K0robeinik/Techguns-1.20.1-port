package techguns2.machine.reaction_chamber;

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

public class ReactionChamberControllerBlock extends AbstractMultiBlockControllerBlock<
    ReactionChamberPartBlockEntity,
    ReactionChamberControllerBlockEntity>
{
    public ReactionChamberControllerBlock(Properties properties)
    {
        super(ReactionChamberControllerBlockEntity.class, properties);
    }
    
    @Override
    public final RenderShape getRenderShape(BlockState blockState)
    {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }
    
    @Override
    public final void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, LivingEntity byEntity, ItemStack sourceItem)
    {
        if (!(level.getBlockEntity(blockPos) instanceof ReactionChamberControllerBlockEntity reactionChamberControllerBlockEntity))
            return;
        
        if (sourceItem.hasCustomHoverName())
            reactionChamberControllerBlockEntity.setCustomName(sourceItem.getHoverName());

        if (byEntity instanceof Player player)
            reactionChamberControllerBlockEntity.setOwner(player);
        else
            reactionChamberControllerBlockEntity.setOwner(null);
    }

    @Override
    public final ReactionChamberControllerBlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState)
    {
        return new ReactionChamberControllerBlockEntity(blockPos, blockState);
    }
    
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState,
            BlockEntityType<T> entityType)
    {
        return createTickerHelper(entityType, TGBlockEntityTypes.REACTION_CHAMBER_CONTROLLER.get(), ReactionChamberControllerBlockEntity::tick);
    }
}
