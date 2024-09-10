package techguns2.machine.alloy_furnace;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import techguns2.TGRecipeTypes;

public interface IAlloyFurnaceRecipe extends Recipe<AlloyFurnaceBlockEntity>
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
        return TGRecipeTypes.ALLOY_FURNACE.get();
    }
}
