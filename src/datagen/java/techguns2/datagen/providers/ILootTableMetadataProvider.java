package techguns2.datagen.providers;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import techguns2.datagen.LootTableMetadata;

public interface ILootTableMetadataProvider
{
    CompletableFuture<List<LootTableMetadata>> fetchLootTables();
}
