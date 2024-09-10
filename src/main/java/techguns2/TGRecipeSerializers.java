package techguns2;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import techguns2.machine.alloy_furnace.AlloyFurnaceRecipe;
import techguns2.machine.ammo_press.AmmoPressRecipe;
import techguns2.machine.charging_station.SimpleChargingStationRecipe;
import techguns2.machine.chemical_laboratory.ChemicalLaboratoryRecipe;
import techguns2.machine.fabricator.FabricatorRecipe;
import techguns2.machine.grinder.ChancedGrinderRecipe;
import techguns2.machine.grinder.GrinderRecipe;
import techguns2.machine.metal_press.MetalPressRecipe;
import techguns2.machine.reaction_chamber.ReactionChamberRecipe;

public final class TGRecipeSerializers implements TGInitializer
{
    protected TGRecipeSerializers()
    { }

    @Override
    public final void setup(IEventBus eventBus)
    {
        REGISTER.register(eventBus);
    }

    private static final DeferredRegister<RecipeSerializer<?>> REGISTER = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Techguns.MODID);
    
    public static final RegistryObject<AmmoPressRecipe.Serializer> AMMO_PRESS = REGISTER.register("ammo_press", AmmoPressRecipe.Serializer::new);
    public static final RegistryObject<MetalPressRecipe.Serializer> METAL_PRESS = REGISTER.register("metal_press", MetalPressRecipe.Serializer::new);
    public static final RegistryObject<ReactionChamberRecipe.Serializer> REACTION_CHAMBER = REGISTER.register("reaction_chamber", ReactionChamberRecipe.Serializer::new);
    public static final RegistryObject<ChemicalLaboratoryRecipe.Serializer> CHEMICAL_LABORATORY = REGISTER.register("chemical_laboratory", ChemicalLaboratoryRecipe.Serializer::new);
    public static final RegistryObject<FabricatorRecipe.Serializer> FABRICATOR = REGISTER.register("fabricator", FabricatorRecipe.Serializer::new);
    public static final RegistryObject<AlloyFurnaceRecipe.Serializer> ALLOY_FURNACE = REGISTER.register("alloy_furnace", AlloyFurnaceRecipe.Serializer::new);
    public static final RegistryObject<GrinderRecipe.Serializer> GRINDER = REGISTER.register("grinder", GrinderRecipe.Serializer::new);
    public static final RegistryObject<ChancedGrinderRecipe.Serializer> GRINDER_CHANCED = REGISTER.register("grinder_chanced", ChancedGrinderRecipe.Serializer::new);
    public static final RegistryObject<SimpleChargingStationRecipe.Serializer> SIMPLE_CHARGING = REGISTER.register("simple_charging", SimpleChargingStationRecipe.Serializer::new);
}
