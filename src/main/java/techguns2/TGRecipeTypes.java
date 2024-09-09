package techguns2;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import techguns2.machine.alloy_furnace.IAlloyFurnaceRecipe;
import techguns2.machine.ammo_press.IAmmoPressRecipe;
import techguns2.machine.charging_station.IChargingStationRecipe;
import techguns2.machine.chemical_laboratory.IChemicalLaboratoryRecipe;
import techguns2.machine.fabricator.IFabricatorRecipe;
import techguns2.machine.grinder.IGrinderRecipe;
import techguns2.machine.metal_press.IMetalPressRecipe;
import techguns2.machine.reaction_chamber.IReactionChamberRecipe;

public final class TGRecipeTypes implements TGInitializer
{
    protected TGRecipeTypes()
    { }

    @Override
    public final void setup(IEventBus eventBus)
    {
        REGISTER.register(eventBus);
    }

    private static final DeferredRegister<RecipeType<?>> REGISTER = DeferredRegister.create(Registries.RECIPE_TYPE, Techguns.MODID);

    public static final RegistryObject<RecipeType<IAmmoPressRecipe>> AMMO_PRESS = register("ammo_press");
    public static final RegistryObject<RecipeType<IMetalPressRecipe>> METAL_PRESS = register("metal_press");
    public static final RegistryObject<RecipeType<IReactionChamberRecipe>> REACTION_CHAMBER = register("reaction_chamber");
    public static final RegistryObject<RecipeType<IChemicalLaboratoryRecipe>> CHEMICAL_LABORATORY = register("chemical_laboratory");
    public static final RegistryObject<RecipeType<IFabricatorRecipe>> FABRICATOR = register("fabricator");
    public static final RegistryObject<RecipeType<IAlloyFurnaceRecipe>> ALLOY_FURNACE = register("alloy_furnace");
    public static final RegistryObject<RecipeType<IGrinderRecipe>> GRINDER = register("grinder");
    public static final RegistryObject<RecipeType<IChargingStationRecipe>> CHARGING = register("charging");
    
    private static <T extends Recipe<?>> RegistryObject<RecipeType<T>> register(String name)
    {
        ResourceLocation id = new ResourceLocation(Techguns.MODID, name);
        return REGISTER.register(name, () -> RecipeType.simple(id));
    }
}
