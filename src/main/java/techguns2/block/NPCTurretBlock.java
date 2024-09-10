package techguns2.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import techguns2.block.entity.NPCTurretBlockEntity;

public class NPCTurretBlock extends BaseEntityBlock
{
    public NPCTurretBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    public final NPCTurretBlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState)
    {
        return new NPCTurretBlockEntity(blockPos, blockState);
    }

}
