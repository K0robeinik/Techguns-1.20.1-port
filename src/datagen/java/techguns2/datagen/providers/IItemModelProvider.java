package techguns2.datagen.providers;

import net.minecraftforge.client.model.generators.ItemModelBuilder;

public interface IItemModelProvider
{
    ItemModelBuilder fetchItemModel(IModelBuilderFactory<ItemModelBuilder> modelBuilderFactory);
}
