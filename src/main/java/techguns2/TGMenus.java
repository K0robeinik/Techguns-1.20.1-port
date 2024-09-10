package techguns2;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import techguns2.machine.alloy_furnace.AlloyFurnaceMenu;
import techguns2.machine.alloy_furnace.AlloyFurnaceMenuFactory;
import techguns2.machine.alloy_furnace.AlloyFurnaceScreen;
import techguns2.machine.ammo_press.AmmoPressMenu;
import techguns2.machine.ammo_press.AmmoPressMenuFactory;
import techguns2.machine.ammo_press.AmmoPressScreen;
import techguns2.machine.charging_station.ChargingStationMenu;
import techguns2.machine.charging_station.ChargingStationMenuFactory;
import techguns2.machine.charging_station.ChargingStationScreen;
import techguns2.machine.chemical_laboratory.ChemicalLaboratoryMenu;
import techguns2.machine.chemical_laboratory.ChemicalLaboratoryMenuFactory;
import techguns2.machine.chemical_laboratory.ChemicalLaboratoryScreen;
import techguns2.machine.fabricator.FabricatorMenu;
import techguns2.machine.fabricator.FabricatorMenuFactory;
import techguns2.machine.fabricator.FabricatorScreen;
import techguns2.machine.grinder.GrinderMenu;
import techguns2.machine.grinder.GrinderMenuFactory;
import techguns2.machine.grinder.GrinderScreen;
import techguns2.machine.metal_press.MetalPressMenu;
import techguns2.machine.metal_press.MetalPressMenuFactory;
import techguns2.machine.metal_press.MetalPressScreen;
import techguns2.machine.reaction_chamber.ReactionChamberMenu;
import techguns2.machine.reaction_chamber.ReactionChamberMenuFactory;
import techguns2.machine.reaction_chamber.ReactionChamberScreen;

public final class TGMenus implements TGInitializer
{
    protected TGMenus()
    { }

    @Override
    public final void setup(IEventBus eventBus)
    {
        REGISTER.register(eventBus);
        eventBus.addListener(TGMenus::onClientSetup);
    }
    
    private static final DeferredRegister<MenuType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.MENU_TYPES, Techguns.MODID);
    
    // TODO: Properly setup menu type
    public static final RegistryObject<MenuType<AlloyFurnaceMenu>> ALLOY_FURNACE = REGISTER.register("alloy_furnace", () -> IForgeMenuType.create(new AlloyFurnaceMenuFactory()));
    public static final RegistryObject<MenuType<AmmoPressMenu>> AMMO_PRESS = REGISTER.register("ammo_press", () -> IForgeMenuType.create(new AmmoPressMenuFactory()));
    public static final RegistryObject<MenuType<ChargingStationMenu>> CHARGING_STATION = REGISTER.register("charging_station", () -> IForgeMenuType.create(new ChargingStationMenuFactory()));
    public static final RegistryObject<MenuType<ChemicalLaboratoryMenu>> CHEMICAL_LABORATORY = REGISTER.register("chemical_laboratory", () -> IForgeMenuType.create(new ChemicalLaboratoryMenuFactory()));
    public static final RegistryObject<MenuType<FabricatorMenu>> FABRICATOR = REGISTER.register("fabricator", () -> IForgeMenuType.create(new FabricatorMenuFactory()));
    public static final RegistryObject<MenuType<GrinderMenu>> GRINDER = REGISTER.register("grinder", () -> IForgeMenuType.create(new GrinderMenuFactory()));
    public static final RegistryObject<MenuType<MetalPressMenu>> METAL_PRESS = REGISTER.register("metal_press", () -> IForgeMenuType.create(new MetalPressMenuFactory()));
    public static final RegistryObject<MenuType<ReactionChamberMenu>> REACTION_CHAMBER = REGISTER.register("reaction_chamber", () -> IForgeMenuType.create(new ReactionChamberMenuFactory()));
    // TODO: Turrets
    
    private static void onClientSetup(FMLClientSetupEvent clientSetupEvent)
    {
        clientSetupEvent.enqueueWork(
                () -> {
                    MenuScreens.register(ALLOY_FURNACE.get(), AlloyFurnaceScreen::new);
                    MenuScreens.register(AMMO_PRESS.get(), AmmoPressScreen::new);
                    MenuScreens.register(CHARGING_STATION.get(), ChargingStationScreen::new);
                    MenuScreens.register(CHEMICAL_LABORATORY.get(), ChemicalLaboratoryScreen::new);
                    MenuScreens.register(FABRICATOR.get(), FabricatorScreen::new);
                    MenuScreens.register(GRINDER.get(), GrinderScreen::new);
                    MenuScreens.register(METAL_PRESS.get(), MetalPressScreen::new);
                    MenuScreens.register(REACTION_CHAMBER.get(), ReactionChamberScreen::new);
                });
    }

}
