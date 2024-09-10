package techguns2.datagen;

import java.util.List;
import java.util.stream.Stream;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile.ExistingModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import techguns2.Techguns;
import techguns2.datagen.providers.IItemModelProvider;
import techguns2.datagen.providers.IModelBuilderFactory;

public class ModItemModelProvider extends ItemModelProvider implements IModelBuilderFactory<ItemModelBuilder>
{
    private final List<IItemModelProvider> _providers;
    
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper, Stream<IItemModelProvider> providers)
    {
        super(output, Techguns.MODID, existingFileHelper);
        
        this._providers = providers.toList();
    }

    @Override
    protected void registerModels()
    {
        for (var provider : this._providers)
        {
            provider.fetchItemModel(this);
        }
    }

    @Override
    public ItemModelBuilder create(String path)
    {
        return this.getBuilder(path);
    }

    @Override
    public ExistingModelFile getExistingModel(ResourceLocation path)
    {
        return this.getExistingFile(path);
    }

}
