package techguns2.machine.chemical_laboratory;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;
import techguns2.TGRecipeTypes;

public interface IChemicalLaboratoryRecipe extends Recipe<ChemicalLaboratoryBlockEntity>
{
    int getProcessingTime();
    int getEnergyDrainPerTick();
    
    int testPrimaryWithConsumption(ItemStack itemStack);
    int testSecondaryWithConsumption(ItemStack itemStack);
    int testTankWithConsumption(ItemStack itemStack);
    int testFluidWithConsumption(FluidStack fluidStack);
    
    FluidStack getResultFluid(RegistryAccess registryAccess);
    
    FluidStack assembleFluid(ChemicalLaboratoryBlockEntity blockEntity, RegistryAccess registryAccess);
    
    @Override
    default boolean canCraftInDimensions(int width, int height)
    {
        return true;
    }
    
    @Override
    default RecipeType<?> getType()
    {
        return TGRecipeTypes.CHEMICAL_LABORATORY.get();
    }
}
