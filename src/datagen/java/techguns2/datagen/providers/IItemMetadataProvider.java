package techguns2.datagen.providers;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import techguns2.datagen.ItemMetadata;

public interface IItemMetadataProvider
{
    CompletableFuture<List<ItemMetadata<?>>> fetchItemMetadatas();
}
