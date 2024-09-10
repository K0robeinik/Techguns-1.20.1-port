package techguns2.machine.ammo_press;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import techguns2.TGRecipeTypes;

public interface IAmmoPressRecipe extends Recipe<AmmoPressBlockEntity>
{
    int getProcessingTime();
    int getEnergyDrainPerTick();
    
    int testCasingWithConsumption(ItemStack itemStack);
    int testPowderWithConsumption(ItemStack itemStack);
    int testBulletWithConsumption(ItemStack itemStack);
    
    @Override
    default boolean canCraftInDimensions(int width, int height)
    {
        return true;
    }
    
    @Override
    default RecipeType<?> getType()
    {
        return TGRecipeTypes.AMMO_PRESS.get();
    }
}
