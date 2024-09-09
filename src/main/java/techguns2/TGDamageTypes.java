package techguns2;

import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import techguns2.api.damage.DamageCategories;
import techguns2.api.damage.IDamageCategory;
import techguns2.damage.TGDamageSource;
import techguns2.damage.TGDamageSource.Properties;
import techguns2.deathfx.DeathFXKind;

public final class TGDamageTypes
{

    private TGDamageTypes()
    { }
    
    public static final ResourceKey<DamageType> SHOT = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Techguns.MODID, "shot"));
    public static final ResourceKey<DamageType> EXPLOSION = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Techguns.MODID, "explosion"));
    public static final ResourceKey<DamageType> FIRE = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Techguns.MODID, "fire"));
    public static final ResourceKey<DamageType> POISON = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Techguns.MODID, "poison"));
    public static final ResourceKey<DamageType> POWER_HAMMER = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Techguns.MODID, "power_hammer"));
    public static final ResourceKey<DamageType> ELECTRICITY = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Techguns.MODID, "electricity"));
    public static final ResourceKey<DamageType> ENERGY = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Techguns.MODID, "energy"));
    public static final ResourceKey<DamageType> CHAINSAW = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Techguns.MODID, "chainsaw"));
    public static final ResourceKey<DamageType> SONIC = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Techguns.MODID, "sonic"));
    public static final ResourceKey<DamageType> RADIATION = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Techguns.MODID, "radiation"));
    public static final ResourceKey<DamageType> DARK = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Techguns.MODID, "dark"));

    private static final TGDamageSource.BakedProperties PROPERTIES_NOKNOCKBACK_IGNOREHURTRESIST = TGDamageSource.PropertiesBuilder.of()
            .withoutKnockbackWhenBlockedWithShield()
            .withIgnoringHurtResistTime()
            .bake();
    private static final TGDamageSource.BakedProperties LETHAL_RADIATION_PROPERTIES = TGDamageSource.PropertiesBuilder.of()
            .withoutKnockbackWhenBlockedWithShield()
            .withIgnoringHurtResistTime()
            .withGoreChance(1)
            .bake();
    
    public static TGDamageSource createShot(
            RegistryAccess registryAccess,
            Entity projectile,
            Entity shooter,
            DeathFXKind deathFxKind)
    {
        return TGDamageTypes.createCommon(
                registryAccess,
                SHOT,
                DamageCategories.PROJECTILE,
                PROPERTIES_NOKNOCKBACK_IGNOREHURTRESIST,
                projectile,
                shooter,
                deathFxKind);
    }
    
    public static TGDamageSource createShot(
            RegistryAccess registryAccess,
            Entity projectile,
            Entity shooter,
            DeathFXKind deathFxKind,
            @Nullable Function<Properties, Properties> propertiesApplier)
    {
        return TGDamageTypes.createCommon(
                registryAccess,
                SHOT,
                DamageCategories.PROJECTILE,
                PROPERTIES_NOKNOCKBACK_IGNOREHURTRESIST,
                projectile,
                shooter,
                deathFxKind,
                propertiesApplier);
    }
    
    public static TGDamageSource createExplosion(
            RegistryAccess registryAccess,
            Entity projectile,
            Entity shooter,
            DeathFXKind deathFxKind)
    {
        return TGDamageTypes.createCommon(
                registryAccess,
                FIRE,
                DamageCategories.FIRE,
                TGDamageSource.BakedProperties.of(),
                projectile,
                shooter,
                deathFxKind);
    }
    
    public static TGDamageSource createExplosion(
            RegistryAccess registryAccess,
            Entity projectile,
            Entity shooter,
            DeathFXKind deathFxKind,
            @Nullable Function<Properties, Properties> propertiesApplier)
    {
        return TGDamageTypes.createCommon(
                registryAccess,
                EXPLOSION,
                DamageCategories.EXPLOSION,
                TGDamageSource.BakedProperties.of(),
                projectile,
                shooter,
                deathFxKind,
                propertiesApplier);
    }
    
    public static TGDamageSource createFire(
            RegistryAccess registryAccess,
            Entity projectile,
            Entity shooter,
            DeathFXKind deathFxKind)
    {
        return TGDamageTypes.createCommon(
                registryAccess,
                FIRE,
                DamageCategories.FIRE,
                PROPERTIES_NOKNOCKBACK_IGNOREHURTRESIST,
                projectile,
                shooter,
                deathFxKind);
    }
    
    public static TGDamageSource createFire(
            RegistryAccess registryAccess,
            Entity projectile,
            Entity shooter,
            DeathFXKind deathFxKind,
            @Nullable Function<Properties, Properties> propertiesApplier)
    {
        return TGDamageTypes.createCommon(
                registryAccess,
                FIRE,
                DamageCategories.FIRE,
                PROPERTIES_NOKNOCKBACK_IGNOREHURTRESIST,
                projectile,
                shooter,
                deathFxKind,
                propertiesApplier);
    }
    
    public static TGDamageSource createPoison(
            RegistryAccess registryAccess,
            Entity projectile,
            Entity shooter,
            DeathFXKind deathFxKind)
    {
        return TGDamageTypes.createCommon(
                registryAccess,
                POISON,
                DamageCategories.POISON,
                PROPERTIES_NOKNOCKBACK_IGNOREHURTRESIST,
                projectile,
                shooter,
                deathFxKind);
    }
    
    public static TGDamageSource createPoison(
            RegistryAccess registryAccess,
            Entity projectile,
            Entity shooter,
            DeathFXKind deathFxKind,
            @Nullable Function<Properties, Properties> propertiesApplier)
    {
        return TGDamageTypes.createCommon(
                registryAccess,
                POISON,
                DamageCategories.POISON,
                PROPERTIES_NOKNOCKBACK_IGNOREHURTRESIST,
                projectile,
                shooter,
                deathFxKind,
                propertiesApplier);
    }
    
    public static TGDamageSource createEnergy(
            RegistryAccess registryAccess,
            Entity projectile,
            Entity shooter,
            DeathFXKind deathFxKind)
    {
        return TGDamageTypes.createCommon(
                registryAccess,
                ENERGY,
                DamageCategories.ENERGY,
                PROPERTIES_NOKNOCKBACK_IGNOREHURTRESIST,
                projectile,
                shooter,
                deathFxKind);
    }
    
    public static TGDamageSource createEnergy(
            RegistryAccess registryAccess,
            Entity projectile,
            Entity shooter,
            DeathFXKind deathFxKind,
            @Nullable Function<Properties, Properties> propertiesApplier)
    {
        return TGDamageTypes.createCommon(
                registryAccess,
                ENERGY,
                DamageCategories.ENERGY,
                PROPERTIES_NOKNOCKBACK_IGNOREHURTRESIST,
                projectile,
                shooter,
                deathFxKind,
                propertiesApplier);
    }
    
    public static TGDamageSource createRadiation(
            RegistryAccess registryAccess,
            Entity projectile,
            Entity shooter,
            DeathFXKind deathFxKind)
    {
        return TGDamageTypes.createCommon(
                registryAccess,
                RADIATION,
                DamageCategories.RADIATION,
                PROPERTIES_NOKNOCKBACK_IGNOREHURTRESIST,
                projectile,
                shooter,
                deathFxKind);
    }
    
    public static TGDamageSource createRadiation(
            RegistryAccess registryAccess,
            Entity projectile,
            Entity shooter,
            DeathFXKind deathFxKind,
            @Nullable Function<Properties, Properties> propertiesApplier)
    {
        return TGDamageTypes.createCommon(
                registryAccess,
                RADIATION,
                DamageCategories.RADIATION,
                PROPERTIES_NOKNOCKBACK_IGNOREHURTRESIST,
                projectile,
                shooter,
                deathFxKind,
                propertiesApplier);
    }
    
    public static TGDamageSource createLethalRadiation(
            RegistryAccess registryAccess,
            Entity projectile,
            Entity shooter,
            DeathFXKind deathFxKind)
    {
        return TGDamageTypes.createCommon(
                registryAccess,
                RADIATION,
                DamageCategories.RADIATION,
                LETHAL_RADIATION_PROPERTIES,
                projectile,
                shooter,
                deathFxKind);
    }
    
    public static TGDamageSource createLethalRadiation(
            RegistryAccess registryAccess,
            Entity projectile,
            Entity shooter,
            DeathFXKind deathFxKind,
            @Nullable Function<Properties, Properties> propertiesApplier)
    {
        return TGDamageTypes.createCommon(
                registryAccess,
                RADIATION,
                DamageCategories.RADIATION,
                LETHAL_RADIATION_PROPERTIES,
                projectile,
                shooter,
                deathFxKind,
                propertiesApplier);
    }
    
    public static TGDamageSource createElectricity(
            RegistryAccess registryAccess,
            Entity projectile,
            Entity shooter,
            DeathFXKind deathFxKind)
    {
        return TGDamageTypes.createCommon(
                registryAccess,
                ELECTRICITY,
                DamageCategories.ELECTRICITY,
                PROPERTIES_NOKNOCKBACK_IGNOREHURTRESIST,
                projectile,
                shooter,
                deathFxKind);
    }
    
    public static TGDamageSource createElectricity(
            RegistryAccess registryAccess,
            Entity projectile,
            Entity shooter,
            DeathFXKind deathFxKind,
            @Nullable Function<Properties, Properties> propertiesApplier)
    {
        return TGDamageTypes.createCommon(
                registryAccess,
                ELECTRICITY,
                DamageCategories.ELECTRICITY,
                PROPERTIES_NOKNOCKBACK_IGNOREHURTRESIST,
                projectile,
                shooter,
                deathFxKind,
                propertiesApplier);
    }
    
    public static TGDamageSource createDark(
            RegistryAccess registryAccess,
            Entity projectile,
            Entity shooter,
            DeathFXKind deathFxKind)
    {
        return TGDamageTypes.createCommon(
                registryAccess,
                DARK,
                DamageCategories.DARK,
                PROPERTIES_NOKNOCKBACK_IGNOREHURTRESIST,
                projectile,
                shooter,
                deathFxKind);
    }
    
    public static TGDamageSource createDark(
            RegistryAccess registryAccess,
            Entity projectile,
            Entity shooter,
            DeathFXKind deathFxKind,
            @Nullable Function<Properties, Properties> propertiesApplier)
    {
        return TGDamageTypes.createCommon(
                registryAccess,
                DARK,
                DamageCategories.DARK,
                PROPERTIES_NOKNOCKBACK_IGNOREHURTRESIST,
                projectile,
                shooter,
                deathFxKind,
                propertiesApplier);
    }
    
    private static TGDamageSource createCommon(
            RegistryAccess registryAccess,
            ResourceKey<DamageType> type,
            IDamageCategory category,
            Properties properties,
            Entity projectile,
            Entity shooter,
            DeathFXKind deathFxKind)
    {
        return new TGDamageSource(
                registryAccess.registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(type),
                category,
                deathFxKind,
                properties,
                projectile,
                shooter);
    }
    
    private static TGDamageSource createCommon(
            RegistryAccess registryAccess,
            ResourceKey<DamageType> type,
            IDamageCategory category,
            Properties properties,
            Entity projectile,
            Entity shooter,
            DeathFXKind deathFxKind,
            @Nullable Function<Properties, Properties> propertiesApplier)
    {

        if (propertiesApplier != null)
            properties = propertiesApplier.apply(properties);
        
        return new TGDamageSource(
                registryAccess.registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(type),
                category,
                deathFxKind,
                properties,
                projectile,
                shooter);
    }
}
