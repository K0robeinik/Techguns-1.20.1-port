package techguns2.datagen;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableList;

import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.registries.RegistryObject;
import techguns2.Techguns;
import techguns2.datagen.providers.IItemModelProvider;
import techguns2.datagen.providers.ILangMetadataProvider;
import techguns2.datagen.providers.IModelBuilderFactory;
import techguns2.datagen.providers.IRecipeMetadataProvider;
import techguns2.datagen.providers.ITagProvider;

public final class ItemMetadata<TItem extends Item> implements ILangMetadataProvider, IRecipeMetadataProvider, ITagProvider, IItemModelProvider
{
    public final TItem item;
    public final ResourceLocation id;
    public final String langId;
    public String langName;
    @Nullable
    public BiConsumer<IModelBuilderFactory<ItemModelBuilder>, ItemModelBuilder> itemModelGenerator;
    public final List<RecipeMetadata<?>> recipeMetadatas;
    public final List<TagKey<Item>> tags;
    
    public ItemMetadata(RegistryObject<TItem> registeredItem)
    {
        this(registeredItem.get(), registeredItem.getId());
    }
    
    public ItemMetadata(TItem item, ResourceLocation id)
    {
        this.item = item;
        this.id = id;
        this.langId = Util.makeDescriptionId("item", id);
        this.langName = this.langId;
        this.recipeMetadatas = new ArrayList<RecipeMetadata<?>>();
        this.tags = new ArrayList<TagKey<Item>>();
        this.itemModelGenerator = null;
    }
    
    public final ItemMetadata<TItem> withLangName(String langName)
    {
        this.langName = langName;
        return this;
    }
    
    public final ItemMetadata<TItem> addRecipe(RecipeMetadata<?> recipeMetadata)
    {
        this.recipeMetadatas.add(recipeMetadata);
        return this;
    }
    
    public final ItemMetadata<TItem> addTag(TagKey<Item> tag)
    {
        this.tags.add(tag);
        return this;
    }
    
    @Override
    public final CompletableFuture<List<LanguageMetadata>> fetchLanguageEntries()
    {
        return CompletableFuture.completedFuture(ImmutableList.of(new LanguageMetadata(this.langId, this.langName)));
    }
    
    @Override
    public final CompletableFuture<List<RecipeMetadata<?>>> fetchRecipes()
    {
        return CompletableFuture.completedFuture(ImmutableList.copyOf(this.recipeMetadatas));
    }
    
    @Override
    public final CompletableFuture<List<TagKey<?>>> fetchTags()
    {
        return CompletableFuture.completedFuture(ImmutableList.copyOf(this.tags));
    }
    
    @Override
    public final ItemModelBuilder fetchItemModel(IModelBuilderFactory<ItemModelBuilder> modelBuilderFactory)
    {
        String path = this.id.getPath();
        ItemModelBuilder model = modelBuilderFactory.create(path);
        if (this.itemModelGenerator == null)
        {
            model
                .parent(modelBuilderFactory.getExistingModel(new ResourceLocation("item/generated")))
                .texture("layer0", new ResourceLocation(Techguns.MODID, "item/" + path));
        }
        else
        {
            this.itemModelGenerator.accept(modelBuilderFactory, model);
        }
        return model;
    }
    
    public static <TItem extends Item> ItemMetadata<TItem> of(RegistryObject<TItem> registeredItem)
    {
        return new ItemMetadata<TItem>(registeredItem);
    }
    
    public static <TItem extends Item> ItemMetadata<TItem> of(TItem item, ResourceLocation id)
    {
        return new ItemMetadata<TItem>(item, id);
    }
}
