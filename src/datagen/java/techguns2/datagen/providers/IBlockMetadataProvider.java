package techguns2.datagen.providers;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import techguns2.datagen.BlockMetadata;

public interface IBlockMetadataProvider
{
    CompletableFuture<List<BlockMetadata<?>>> fetchBlockMetadatas();
}
