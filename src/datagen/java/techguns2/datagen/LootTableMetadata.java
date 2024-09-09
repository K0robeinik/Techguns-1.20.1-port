package techguns2.datagen;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;

public final class LootTableMetadata
{
    public final ResourceLocation id;
    public final LootTable.Builder table;
    public final LootContextParamSet context;
    
    public LootTableMetadata(ResourceLocation id, LootTable.Builder table, LootContextParamSet context)
    {
        this.id = id;
        this.table = table;
        this.context = context;
    }
}
