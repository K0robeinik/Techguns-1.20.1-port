package techguns2.datagen.providers;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;

public interface IModelBuilderFactory<T extends ModelBuilder<T>>
{
    T create(String path);
    
    ModelFile.ExistingModelFile getExistingModel(ResourceLocation path);
}
