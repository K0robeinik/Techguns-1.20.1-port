package techguns2;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import techguns2.entity.projectile.StoneBulletProjectile;

public final class TGEntityTypes implements TGInitializer
{

    protected TGEntityTypes()
    { }
    
    @Override
    public final void setup(IEventBus eventBus)
    {
        eventBus.addListener(TGEntityTypes::registerEvent);
    }
    
    private static void registerEvent(RegisterEvent registerEvent)
    {
        registerEvent.register(ForgeRegistries.Keys.ENTITY_TYPES, (helper) -> {
            helper.register(STONE_BULLET_LOCATION, STONE_BULLET);
        });
    }
    
    public static final int bulletTrackRange = 128;
    
    private static final ResourceLocation STONE_BULLET_LOCATION = new ResourceLocation(Techguns.MODID, "stone_bullet");
    
    public static final EntityType<StoneBulletProjectile> STONE_BULLET = EntityType.Builder.of(StoneBulletProjectile::new, MobCategory.MISC)
            .sized(0.25f, 0.25f)
            .clientTrackingRange(bulletTrackRange)
            .build(STONE_BULLET_LOCATION.toString());

}
