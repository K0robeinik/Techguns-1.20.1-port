package techguns2.damage;

import javax.annotation.Nullable;

import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import techguns2.api.damage.IDamageCategory;
import techguns2.deathfx.DeathFXKind;

public class TGDamageSource extends DamageSource
{
    public final IDamageCategory category;
    public final DeathFXKind deathFxKind;
    public final float goreChance;
    public final boolean causesKnockbackWhenBlockedWithShield;
    public final float armorPenetration;
    public final boolean ignoreHurtResistTime;
    public final float knockbackMultiplier;
    
    public TGDamageSource(
            Holder<DamageType> type,
            IDamageCategory category,
            DeathFXKind deathFxKind,
            @Nullable Properties properties,
            @Nullable Entity directEntity,
            @Nullable Entity causingEntity,
            @Nullable Vec3 damageSourcePosition)
    {
        super(type, directEntity, causingEntity, damageSourcePosition);
        
        this.category = category;
        this.deathFxKind = deathFxKind;
        
        if (properties == null)
        {
            this.goreChance = 0.5f;
            this.causesKnockbackWhenBlockedWithShield = true;
            this.armorPenetration = 0.0f;
            this.ignoreHurtResistTime = false;
            this.knockbackMultiplier = 1f;
        }
        else
        {
            this.goreChance = properties.goreChance();
            this.causesKnockbackWhenBlockedWithShield = properties.causesKnockbackWhenBlockedWithShield();
            this.armorPenetration = properties.armorPenetration();
            this.ignoreHurtResistTime = properties.ignoreHurtResistTime();
            this.knockbackMultiplier = properties.knockbackMultiplier();
        }
    }
    
    public TGDamageSource(
            Holder<DamageType> type,
            IDamageCategory category,
            DeathFXKind deathFxKind,
            @Nullable Entity directEntity,
            @Nullable Entity causingEntity,
            @Nullable Vec3 damageSourcePosition)
    {
        this(
                type,
                category,
                deathFxKind,
                (Properties)null,
                directEntity,
                causingEntity,
                damageSourcePosition);
    }

    public TGDamageSource(
            Holder<DamageType> type,
            IDamageCategory category,
            DeathFXKind deathFxKind,
            @Nullable Entity directEntity,
            @Nullable Entity causingEntity)
    {
        this(
                type,
                category,
                deathFxKind,
                (Properties)null,
                directEntity,
                causingEntity,
                (Vec3)null);
    }

    public TGDamageSource(
            Holder<DamageType> type,
            IDamageCategory category,
            DeathFXKind deathFxKind,
            @Nullable Properties properties,
            @Nullable Entity directEntity,
            @Nullable Entity causingEntity)
    {
        this(
                type,
                category,
                deathFxKind,
                properties,
                directEntity,
                causingEntity,
                (Vec3)null);
    }
    
    public TGDamageSource(
            Holder<DamageType> type,
            IDamageCategory category,
            DeathFXKind deathFxKind,
            Vec3 damageSourcePosition)
    {
        this(
                type,
                category,
                deathFxKind,
                (Properties)null,
                (Entity)null, 
                (Entity)null, 
                damageSourcePosition);
    }
    
    public TGDamageSource(
            Holder<DamageType> type,
            IDamageCategory category,
            DeathFXKind deathFxKind,
            @Nullable Properties properties,
            Vec3 damageSourcePosition)
    {
        this(
                type,
                category,
                deathFxKind,
                properties,
                (Entity)null, 
                (Entity)null, 
                damageSourcePosition);
    }
    
    public TGDamageSource(
            Holder<DamageType> type,
            IDamageCategory category,
            DeathFXKind deathFxKind,
            @Nullable Entity entity)
    {
        this(
                type,
                category,
                deathFxKind,
                (Properties)null,
                entity,
                entity,
                (Vec3)null);
    }
    
    public TGDamageSource(
            Holder<DamageType> type,
            IDamageCategory category,
            DeathFXKind deathFxKind,
            @Nullable Properties properties,
            @Nullable Entity entity)
    {
        this(
                type,
                category,
                deathFxKind,
                properties,
                entity,
                entity,
                (Vec3)null);
    }
    
    public TGDamageSource(
            Holder<DamageType> type,
            IDamageCategory category,
            DeathFXKind deathFxKind)
    {
        this(type, 
                category,
                deathFxKind,
                (Properties)null,
                (Entity)null, 
                (Entity)null, 
                (Vec3)null);
    }
    
    public TGDamageSource(
            Holder<DamageType> type,
            IDamageCategory category,
            DeathFXKind deathFxKind,
            @Nullable Properties properties)
    {
        this(type, 
                category,
                deathFxKind,
                properties,
                (Entity)null, 
                (Entity)null, 
                (Vec3)null);
    }
    
    public static interface Properties
    {
        float goreChance();
        boolean causesKnockbackWhenBlockedWithShield();
        float armorPenetration();
        boolean ignoreHurtResistTime();
        float knockbackMultiplier();
        
        Properties copyOf();
        Properties withGoreChance(float value);
        Properties setKnockbackWhenBlockedWithShield(boolean value);
        Properties withoutKnockbackWhenBlockedWithShield();
        Properties withKnockbackWhenBlockedWithShield();
        Properties withArmorPenetration(float value);
        Properties setIgnoreHurtResistTime(boolean value);
        Properties withoutIgnoringHurtResistTime();
        Properties withIgnoringHurtResistTime();
        Properties withKnockbackMultiplier(float value);
        Properties withoutKnockback();
    }
    
    public static final class BakedProperties implements Properties
    {
        public final float goreChance;
        public final boolean causesKnockbackWhenBlockedWithShield;
        public final float armorPenetration;
        public final boolean ignoreHurtResistTime;
        public final float knockbackMultiplier;
        
        public BakedProperties()
        {
            this.goreChance = 0.5f;
            this.causesKnockbackWhenBlockedWithShield = true;
            this.armorPenetration = 0.0f;
            this.ignoreHurtResistTime = false;
            this.knockbackMultiplier = 1.0f;
        }
        
        public BakedProperties(Properties properties)
        {
            this.goreChance = properties.goreChance();
            this.causesKnockbackWhenBlockedWithShield = properties.causesKnockbackWhenBlockedWithShield();
            this.armorPenetration = properties.armorPenetration();
            this.ignoreHurtResistTime = properties.ignoreHurtResistTime();
            this.knockbackMultiplier = properties.knockbackMultiplier();
        }
        
        public BakedProperties(
                float goreChance,
                boolean causesKnockbackWhenBlockedWithShield,
                float armorPenetration,
                boolean ignoreHurtResistTime,
                float knockbackMultiplier)
        {
            this.goreChance = goreChance;
            this.causesKnockbackWhenBlockedWithShield = causesKnockbackWhenBlockedWithShield;
            this.armorPenetration = armorPenetration;
            this.ignoreHurtResistTime = ignoreHurtResistTime;
            this.knockbackMultiplier = knockbackMultiplier;
        }

        @Override
        public final float goreChance()
        {
            return this.goreChance;
        }

        @Override
        public final boolean causesKnockbackWhenBlockedWithShield()
        {
            return this.causesKnockbackWhenBlockedWithShield;
        }

        @Override
        public final float armorPenetration()
        {
            return this.armorPenetration;
        }

        @Override
        public final boolean ignoreHurtResistTime()
        {
            return this.ignoreHurtResistTime;
        }

        @Override
        public final float knockbackMultiplier()
        {
            return this.knockbackMultiplier;
        }

        @Override
        public final BakedProperties copyOf()
        {
            return this;
        }

        @Override
        public final BakedProperties withGoreChance(float value)
        {
            return new BakedProperties(
                    value,
                    this.causesKnockbackWhenBlockedWithShield,
                    this.armorPenetration,
                    this.ignoreHurtResistTime,
                    this.knockbackMultiplier);
        }
        @Override
        public final BakedProperties setKnockbackWhenBlockedWithShield(boolean value)
        {
            return new BakedProperties(
                    this.goreChance,
                    value,
                    this.armorPenetration,
                    this.ignoreHurtResistTime,
                    this.knockbackMultiplier);
        }
        @Override
        public final BakedProperties withoutKnockbackWhenBlockedWithShield()
        {
            return new BakedProperties(
                    this.goreChance,
                    false,
                    this.armorPenetration,
                    this.ignoreHurtResistTime,
                    this.knockbackMultiplier);
        }
        @Override
        public final BakedProperties withKnockbackWhenBlockedWithShield()
        {
            return new BakedProperties(
                    this.goreChance,
                    true,
                    this.armorPenetration,
                    this.ignoreHurtResistTime,
                    this.knockbackMultiplier);
        }
        @Override
        public final BakedProperties withArmorPenetration(float value)
        {
            return new BakedProperties(
                    this.goreChance,
                    this.causesKnockbackWhenBlockedWithShield,
                    value,
                    this.ignoreHurtResistTime,
                    this.knockbackMultiplier);
        }
        @Override
        public final BakedProperties setIgnoreHurtResistTime(boolean value)
        {
            return new BakedProperties(
                    this.goreChance,
                    this.causesKnockbackWhenBlockedWithShield,
                    this.armorPenetration,
                    value,
                    this.knockbackMultiplier);
        }
        @Override
        public final BakedProperties withoutIgnoringHurtResistTime()
        {
            return new BakedProperties(
                    this.goreChance,
                    this.causesKnockbackWhenBlockedWithShield,
                    this.armorPenetration,
                    false,
                    this.knockbackMultiplier);
        }
        @Override
        public final BakedProperties withIgnoringHurtResistTime()
        {
            return new BakedProperties(
                    this.goreChance,
                    this.causesKnockbackWhenBlockedWithShield,
                    this.armorPenetration,
                    true,
                    this.knockbackMultiplier);
        }
        @Override
        public final BakedProperties withKnockbackMultiplier(float value)
        {
            return new BakedProperties(
                    this.goreChance,
                    this.causesKnockbackWhenBlockedWithShield,
                    this.armorPenetration,
                    this.ignoreHurtResistTime,
                    value);
        }
        @Override
        public final BakedProperties withoutKnockback()
        {
            return new BakedProperties(
                    this.goreChance,
                    this.causesKnockbackWhenBlockedWithShield,
                    this.armorPenetration,
                    this.ignoreHurtResistTime,
                    0f);
        }
        
        public static final BakedProperties DEFAULT = new BakedProperties();
        
        public static BakedProperties of()
        {
            return BakedProperties.DEFAULT;
        }
    }

    public static final class PropertiesBuilder implements Properties
    {
        public float goreChance;
        public boolean causesKnockbackWhenBlockedWithShield;
        public float armorPenetration;
        public boolean ignoreHurtResistTime;
        public float knockbackMultiplier;
        
        public PropertiesBuilder()
        {
            this.goreChance = 0.5f;
            this.causesKnockbackWhenBlockedWithShield = true;
            this.armorPenetration = 0.0f;
            this.ignoreHurtResistTime = false;
            this.knockbackMultiplier = 1.0f;
        }
        
        public PropertiesBuilder(Properties properties)
        {
            this.goreChance = properties.goreChance();
            this.causesKnockbackWhenBlockedWithShield = properties.causesKnockbackWhenBlockedWithShield();
            this.armorPenetration = properties.armorPenetration();
            this.ignoreHurtResistTime = properties.ignoreHurtResistTime();
            this.knockbackMultiplier = properties.knockbackMultiplier();
        }
        
        public PropertiesBuilder(
                float goreChance,
                boolean causesKnockbackWhenBlockedWithShield,
                float armorPenetration,
                boolean ignoreHurtResistTime,
                float knockbackMultiplier)
        {
            this.goreChance = goreChance;
            this.causesKnockbackWhenBlockedWithShield = causesKnockbackWhenBlockedWithShield;
            this.armorPenetration = armorPenetration;
            this.ignoreHurtResistTime = ignoreHurtResistTime;
            this.knockbackMultiplier = knockbackMultiplier;
        }

        @Override
        public final float goreChance()
        {
            return this.goreChance;
        }

        @Override
        public final boolean causesKnockbackWhenBlockedWithShield()
        {
            return this.causesKnockbackWhenBlockedWithShield;
        }

        @Override
        public final float armorPenetration()
        {
            return this.armorPenetration;
        }

        @Override
        public final boolean ignoreHurtResistTime()
        {
            return this.ignoreHurtResistTime;
        }

        @Override
        public final float knockbackMultiplier()
        {
            return this.knockbackMultiplier;
        }
        
        @Override
        public final PropertiesBuilder copyOf()
        {
            return new PropertiesBuilder(this);
        }

        @Override
        public final PropertiesBuilder withGoreChance(float value)
        {
            this.goreChance = value;
            return this;
        }
        @Override
        public final PropertiesBuilder setKnockbackWhenBlockedWithShield(boolean value)
        {
            this.causesKnockbackWhenBlockedWithShield = value;
            return this;
        }
        @Override
        public final PropertiesBuilder withoutKnockbackWhenBlockedWithShield()
        {
            this.causesKnockbackWhenBlockedWithShield = false;
            return this;
        }
        @Override
        public final PropertiesBuilder withKnockbackWhenBlockedWithShield()
        {
            this.causesKnockbackWhenBlockedWithShield = true;
            return this;
        }
        @Override
        public final PropertiesBuilder withArmorPenetration(float value)
        {
            this.armorPenetration = value;
            return this;
        }
        @Override
        public final PropertiesBuilder setIgnoreHurtResistTime(boolean value)
        {
            this.ignoreHurtResistTime = value;
            return this;
        }
        @Override
        public final PropertiesBuilder withoutIgnoringHurtResistTime()
        {
            this.ignoreHurtResistTime = false;
            return this;
        }
        @Override
        public final PropertiesBuilder withIgnoringHurtResistTime()
        {
            this.ignoreHurtResistTime = true;
            return this;
        }
        @Override
        public final PropertiesBuilder withKnockbackMultiplier(float value)
        {
            this.knockbackMultiplier = value;
            return this;
        }
        @Override
        public final PropertiesBuilder withoutKnockback()
        {
            this.knockbackMultiplier = 0;
            return this;
        }
        
        public final BakedProperties bake()
        {
            return new BakedProperties(this);
        }
        
        public static PropertiesBuilder of()
        {
            return new PropertiesBuilder();
        }
    }
}
