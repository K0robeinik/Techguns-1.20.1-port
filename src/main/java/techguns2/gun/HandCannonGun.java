package techguns2.gun;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import techguns2.TGAmmoGroups;
import techguns2.TGCustomRegistries;
import techguns2.TGEntityTypes;
import techguns2.TGSounds;
import techguns2.ammo.AmmoGroup;
import techguns2.ammo.IAmmo;
import techguns2.api.gun.IGun;
import techguns2.api.gun.IGunFireMode;
import techguns2.entity.projectile.GenericProjectile;
import techguns2.entity.projectile.ProjectileFireSide;
import techguns2.entity.projectile.StoneBulletProjectile;

public class HandCannonGun implements IGun
{
    public HandCannonGun()
    {
        // TODO Auto-generated constructor stub
    }
    
    public final float getDamage(ItemStack weaponItem)
    {
        return 8.0F;
    }
    
    public final float getDamageDropoffStart(ItemStack weaponItem)
    {
        return 10.0F;
    }
    
    public final float getDamageDropoffEnd(ItemStack weaponItem)
    {
        return 25.0F;
    }
    
    public final float getMinDamage(ItemStack weaponItem)
    {
        return 5.0F;
    }
    
    public final float getGravity(ItemStack weaponItem)
    {
        return 0.015F;
    }
    
    public final float getProjectileSpeed(ItemStack weaponItem)
    {
        return 1.0F;
    }
    
    public final int getLifespan(ItemStack weaponItem)
    {
        return 15;
    }
    
    public final int getScaledLifespan(ItemStack weaponItem)
    {
        return (int)Math.ceil(this.getLifespan(weaponItem) / this.getProjectileSpeed(weaponItem));
    }
    
    public final float getArmorPenetration(ItemStack weaponItem)
    {
        return 0.0f;
    }
    
    protected final void spawnProjectile(
            ServerLevel world,
            LivingEntity owner,
            IAmmo currentAmmo,
            ItemStack weaponItem,
            float spread,
            float offset,
            float damageBonus,
            ProjectileFireSide side)
    {
        float speed = this.getProjectileSpeed(weaponItem);
        Vec3 position = owner.position();
        StoneBulletProjectile projectile = StoneBulletProjectile.create(
                TGEntityTypes.STONE_BULLET,
                world,
                owner,
                position.x,
                position.y + owner.getEyeHeight(),
                position.z,
                owner.getYRot(),
                owner.getXRot(),
                speed,
                this.getScaledLifespan(weaponItem),
                spread,
                this.getArmorPenetration(weaponItem),
                this.getDamage(weaponItem) * damageBonus * currentAmmo.getDamageMultiplier(),
                this.getDamageDropoffStart(weaponItem) * currentAmmo.getDamageDropoffStartMultiplier(),
                this.getDamageDropoffEnd(weaponItem) * currentAmmo.getDamageDropoffEndMultiplier(),
                this.getMinDamage(weaponItem) * damageBonus * currentAmmo.getDamageMultiplier(),
                this.getGravity(weaponItem),
                side);
        
        if (offset > 0)
        {
            projectile.shiftForward(offset / speed);
        }
        
        world.addFreshEntity(projectile);
    }
    
    @Override
    public final IGunFireMode getFireMode(ItemStack gunItem)
    {
        return GunFireModes.SEMI;
    }
    
    @Override
    public final AmmoGroup getAmmoGroup(ItemStack gunItem)
    {
        return TGAmmoGroups.STONE_BULLETS.get();
    }
    
    @Override
    public final int getClipSize(ItemStack gunItem)
    {
        return 1;
    }
    
    @Override
    public final SoundEvent getFireSound(ItemStack gunItem)
    {
        return TGSounds.HAND_CANNON_FIRE.get();
    }
    
    @Override
    public final SoundEvent getReloadSound(ItemStack gunItem)
    {
        return TGSounds.HAND_CANNON_RELOAD.get();
    }
    
    @Nullable
    public final IAmmo getLoadedAmmo(ItemStack gunItem, RegistryAccess registryAccess)
    {   
        CompoundTag tag = gunItem.getOrCreateTag();
        
        if (tag.contains("LoadedAmmo", Tag.TAG_STRING))
        {
            ResourceLocation loadedAmmoLocation = ResourceLocation.tryParse(tag.getString("LoadedAmmo"));
            if (loadedAmmoLocation == null)
                return null;
            
            return registryAccess.registryOrThrow(TGCustomRegistries.AMMO).get(loadedAmmoLocation);
        }
        
        return null;
    }
    
    public final int getCurrentAmmo(ItemStack gunItem)
    {
        CompoundTag tag = gunItem.getOrCreateTag();
        
        return tag.getShort("Ammo");
    }
    
    public final void setCurrentAmmo(ItemStack gunItem, int value)
    {
        CompoundTag tag = gunItem.getOrCreateTag();
        
        tag.putShort("Ammo", (short)value);
    }
    
    public final boolean tryFire(
            LivingEntity owner,
            ServerLevel world,
            ItemStack gunItem)
    {
        int currentAmmo = this.getCurrentAmmo(gunItem);
        if (currentAmmo < 1)
        {
            return false;
        }
        
        currentAmmo--;
        this.setCurrentAmmo(gunItem, currentAmmo);
        return true;
    }

}
