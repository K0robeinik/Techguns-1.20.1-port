package techguns2.machine.fabricator;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import techguns2.TGRecipeTypes;

public interface IFabricatorRecipe extends Recipe<FabricatorControllerBlockEntity>
{
    int getProcessingTime();
    int getEnergyDrainPerTick();
    
    int testInputWithConsumption(ItemStack itemStack);
    int testWireWithConsumption(ItemStack itemStack);
    int testRedstoneWithConsumption(ItemStack itemStack);
    int testPlateWithConsumption(ItemStack itemStack);
    
    @Override
    default boolean canCraftInDimensions(int width, int height)
    {
        return true;
    }
    
    @Override
    default RecipeType<?> getType()
    {
        return TGRecipeTypes.FABRICATOR.get();
    }
}
