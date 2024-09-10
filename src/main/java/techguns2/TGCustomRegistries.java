package techguns2;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import techguns2.ammo.AmmoGroup;
import techguns2.ammo.IAmmo;
import techguns2.ammo.IAmmoType;
import techguns2.machine.ammo_press.AmmoPressTemplate;
import techguns2.machine.ammo_press.AmmoPressTemplates;
import techguns2.machine.reaction_chamber.LaserFocus;
import techguns2.machine.reaction_chamber.LaserFocuses;
import techguns2.machine.reaction_chamber.ReactionRisk;
import techguns2.machine.reaction_chamber.ReactionRisks;

public final class TGCustomRegistries implements TGInitializer
{
    protected TGCustomRegistries()
    { }

    @SuppressWarnings("deprecation")
    @Override
    public final void setup(IEventBus eventBus)
    {
        eventBus.addListener(AmmoPressTemplates::register);
        eventBus.addListener(LaserFocuses::register);
        eventBus.addListener(ReactionRisks::register);
    }
    
    public static final ResourceKey<Registry<IAmmoType>> AMMO_TYPES = ResourceKey.createRegistryKey(new ResourceLocation(Techguns.MODID, "ammo_types"));
    public static final ResourceKey<Registry<IAmmo>> AMMO = ResourceKey.createRegistryKey(new ResourceLocation(Techguns.MODID, "ammo"));
    public static final ResourceKey<Registry<AmmoGroup>> AMMO_GROUPS = ResourceKey.createRegistryKey(new ResourceLocation(Techguns.MODID, "ammo_groups"));
    public static final ResourceKey<Registry<AmmoPressTemplate>> AMMO_PRESS_TEMPLATES = ResourceKey.createRegistryKey(new ResourceLocation(Techguns.MODID, "ammo_press_templates"));
    public static final ResourceKey<Registry<LaserFocus>> LASER_FOCUSES = ResourceKey.createRegistryKey(new ResourceLocation(Techguns.MODID, "laser_focuses"));
    public static final ResourceKey<Registry<ReactionRisk>> REACTION_RISKS = ResourceKey.createRegistryKey(new ResourceLocation(Techguns.MODID, "reaction_risks"));
}
