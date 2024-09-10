package techguns2;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class TGSounds implements TGInitializer
{
    protected TGSounds()
    {
    }

    @Override
    public final void setup(IEventBus eventBus)
    {
        REGISTER.register(eventBus);
    }
    
    private static final DeferredRegister<SoundEvent> REGISTER = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Techguns.MODID);
    

    public static final RegistryObject<SoundEvent> HAND_CANNON_FIRE = createFixedRangeSoundEvent("gun.hand_cannon.fire", 32f);
    public static final RegistryObject<SoundEvent> HAND_CANNON_RELOAD = createFixedRangeSoundEvent("gun.hand_cannon.reload", 32f);
    
    public static final RegistryObject<SoundEvent> AMMO_PRESS_WORK1 = createFixedRangeSoundEvent("machine.ammo_press.work1", 16f);
    public static final RegistryObject<SoundEvent> AMMO_PRESS_WORK2 = createFixedRangeSoundEvent("machine.ammo_press.work2", 16f);
    public static final RegistryObject<SoundEvent> METAL_PRESS_WORK = createFixedRangeSoundEvent("machine.metal_press.work", 16f);
    public static final RegistryObject<SoundEvent> CHEMICAL_LABORATORY_WORK = createFixedRangeSoundEvent("machine.chemical_laboratory.work", 16f);
    public static final RegistryObject<SoundEvent> FABRICATOR_WORK = createFixedRangeSoundEvent("machine.fabricator.work", 16f);
    public static final RegistryObject<SoundEvent> CHARGING_STATION_WORK = createFixedRangeSoundEvent("machine.charging_station.work", 16f);
    public static final RegistryObject<SoundEvent> REACTION_CHAMBER_WORK_HEATRAY = createFixedRangeSoundEvent("machine.reaction_chamber.work.heatray", 16f);
    public static final RegistryObject<SoundEvent> REACTION_CHAMBER_BEEP = createFixedRangeSoundEvent("machine.reaction_chamber.beep", 16f);
    public static final RegistryObject<SoundEvent> REACTION_CHAMBER_WARNING = createFixedRangeSoundEvent("machine.reaction_chamber.warning", 16f);
    public static final RegistryObject<SoundEvent> GRINDER_START = createFixedRangeSoundEvent("machine.grinder.start", 16f);
    public static final RegistryObject<SoundEvent> GRINDER_WORK = createFixedRangeSoundEvent("machine.grinder.work", 16f);

    private static RegistryObject<SoundEvent> createFixedRangeSoundEvent(String id, float distance)
    {
        return REGISTER.register(id, () -> 
            SoundEvent.createFixedRangeEvent(new ResourceLocation(Techguns.MODID, id), distance));
    }
}
