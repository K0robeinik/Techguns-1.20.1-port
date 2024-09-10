package techguns2.api.gun;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import techguns2.ammo.AmmoGroup;

public interface IGun
{
    int getClipSize(ItemStack gunItem);
    IGunFireMode getFireMode(ItemStack gunItem);
    AmmoGroup getAmmoGroup(ItemStack gunItem);
    
    SoundEvent getFireSound(ItemStack gunItem);
    SoundEvent getReloadSound(ItemStack gunItem);
}
