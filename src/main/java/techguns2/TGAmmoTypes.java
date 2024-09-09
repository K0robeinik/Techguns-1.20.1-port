package techguns2;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;
import techguns2.ammo.IAmmoType;

public final class TGAmmoTypes implements TGInitializer
{

    protected TGAmmoTypes()
    { }
    
    @Override
    public final void setup(IEventBus eventBus)
    {
        REGISTER.makeRegistry(() -> {
            RegistryBuilder<IAmmoType> builder = RegistryBuilder.of();
            return builder;
        });
        REGISTER.register(eventBus);
    }
    
    private static final DeferredRegister<IAmmoType> REGISTER = DeferredRegister.create(TGCustomRegistries.AMMO_TYPES, Techguns.MODID);
    
    public static final RegistryObject<Default> DEFAULT = REGISTER.register("default", () -> new Default());
    public static final RegistryObject<Incendiary> INCENDIARY = REGISTER.register("incendiary", () -> new Incendiary());
    public static final RegistryObject<Explosive> EXPLOSIVE = REGISTER.register("explosive", () -> new Explosive());
    public static final RegistryObject<HighVelocityRocket> HIGH_VELOCITY_ROCKET = REGISTER.register("high_velocity_rocket", () -> new HighVelocityRocket());
    public static final RegistryObject<NuclearRocket> NUCLEAR_ROCKET = REGISTER.register("nuclear_rocket", () -> new NuclearRocket());

    public static final class Default implements IAmmoType
    {
        private Default()
        { }
    }
    public static final class Incendiary implements IAmmoType
    {
        private Incendiary()
        { }
    }
    public static final class Explosive implements IAmmoType
    {
        private Explosive()
        { }
    }
    public static final class HighVelocityRocket implements IAmmoType
    {
        private HighVelocityRocket()
        { }
    }
    public static final class NuclearRocket implements IAmmoType
    {
        private NuclearRocket()
        { }
    }
}
