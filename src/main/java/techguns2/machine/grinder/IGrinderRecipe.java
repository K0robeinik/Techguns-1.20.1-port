package techguns2.machine.grinder;

import java.util.List;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import techguns2.TGRecipeTypes;

public interface IGrinderRecipe extends Recipe<GrinderBlockEntity>
{
    int getProcessingTime();
    int getEnergyDrainPerTick();
    
    int testInputItemWithConsumption(ItemStack itemStack);
    
    @Override
    default boolean canCraftInDimensions(int width, int height)
    {
        return true;
    }
    
    List<ItemStack> getResultItems(RegistryAccess registryAccess);
    
    List<ItemStack> assembleAll(GrinderBlockEntity blockEntity, RegistryAccess registryAccess);
    
    /**
     * @deprecated Use {@link #assembleAll(GrinderBlockEntity, RegistryAccess)} instead.
     */
    @Override
    @Deprecated
    default ItemStack assemble(GrinderBlockEntity blockEntity, RegistryAccess registryAccess)
    {
        List<ItemStack> allItems = this.assembleAll(blockEntity, registryAccess);
        if (allItems.size() == 0)
            return ItemStack.EMPTY;
        else
            return allItems.get(0);
    }
    
    default RecipeType<?> getType()
    {
        return TGRecipeTypes.GRINDER.get();
    }
}
