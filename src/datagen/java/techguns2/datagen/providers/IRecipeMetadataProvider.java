package techguns2.datagen.providers;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import techguns2.datagen.RecipeMetadata;

public interface IRecipeMetadataProvider
{
    CompletableFuture<List<RecipeMetadata<?>>> fetchRecipes();
}
