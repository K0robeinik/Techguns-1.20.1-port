package techguns2.machine.charging_station;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import techguns2.TGRecipeTypes;

public interface IChargingStationRecipe extends Recipe<ChargingStationBlockEntity>
{
    int getEnergyNeeded(ItemStack itemStack);
    boolean canCharge(ItemStack itemStack);
    
    @Override
    default boolean canCraftInDimensions(int width, int height)
    {
        return true;
    }
    
    @Override
    default RecipeType<?> getType()
    {
        return TGRecipeTypes.CHARGING.get();
    }
}
