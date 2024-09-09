package techguns2.machine.reaction_chamber;

import java.util.List;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;
import techguns2.TGRecipeTypes;

public interface IReactionChamberRecipe extends Recipe<ReactionChamberControllerBlockEntity>
{
    LaserFocus getLaserFocus();
    int testInputItemWithConsumption(ItemStack itemStack);
    int testInputFluidWithConsumption(FluidStack fluidStack);
    int getFluidLevel();
    float getInstability();
    int getStartingIntensity();
    int getIntensityMargin();
    ReactionRisk getRisk();
    int getTicksPerCycle();
    int getRequiredCycles();
    int getMaximumCycles();
    int getEnergyDrainPerTick();
    
    List<ItemStack> getResultItems(RegistryAccess registryAccess);
    
    List<ItemStack> assembleAll(ReactionChamberControllerBlockEntity blockEntity, RegistryAccess registryAccess);
    
    /**
     * @deprecated Use {@link #assembleAll(ReactionChamberControllerBlockEntity, RegistryAccess)} instead.
     */
    @Override
    @Deprecated
    default ItemStack assemble(ReactionChamberControllerBlockEntity blockEntity, RegistryAccess registryAccess)
    {
        List<ItemStack> allItems = this.assembleAll(blockEntity, registryAccess);
        if (allItems.size() == 0)
            return ItemStack.EMPTY;
        else
            return allItems.get(0);
    }
    
    @Override
    default boolean canCraftInDimensions(int width, int height)
    {
        return true;
    }
    
    @Override
    default RecipeType<?> getType()
    {
        return TGRecipeTypes.REACTION_CHAMBER.get();
    }
}
