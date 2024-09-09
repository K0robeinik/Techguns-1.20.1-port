package techguns2.datagen.providers;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import techguns2.datagen.LanguageMetadata;

public interface ILangMetadataProvider
{
    CompletableFuture<List<LanguageMetadata>> fetchLanguageEntries();
}
