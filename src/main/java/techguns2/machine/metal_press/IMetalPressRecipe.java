package techguns2.machine.metal_press;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import techguns2.TGRecipeTypes;

public interface IMetalPressRecipe extends Recipe<MetalPressBlockEntity>
{
    int getProcessingTime();
    int getEnergyDrainPerTick();
    
    int testPrimaryWithConsumption(ItemStack itemStack);
    int testSecondaryWithConsumption(ItemStack itemStack);
    
    @Override
    default boolean canCraftInDimensions(int width, int height)
    {
        return true;
    }
    
    @Override
    default RecipeType<?> getType()
    {
        return TGRecipeTypes.METAL_PRESS.get();
    }
}
