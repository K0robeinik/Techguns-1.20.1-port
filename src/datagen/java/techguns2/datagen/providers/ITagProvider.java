package techguns2.datagen.providers;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import net.minecraft.tags.TagKey;

public interface ITagProvider
{
    CompletableFuture<List<TagKey<?>>> fetchTags();
}
