package techguns2.api.damage;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

/**
 * An enum of default damage categories
 */
public enum DamageCategories implements IDamageCategory
{
    /** Melee hits */
    PHYSICAL(Component.translatable("techguns2.damageCategory.physical").withStyle(ChatFormatting.DARK_GRAY)),
    /** Arrows, bullets, ... */
    PROJECTILE(Component.translatable("techguns2.damageCategory.projectile").withStyle(ChatFormatting.GRAY)),
    /** Fire, flamethrower */
    FIRE(Component.translatable("techguns2.damageCategory.fire").withStyle(ChatFormatting.RED)),
    /** Explosions like TNT, Rockets */
    EXPLOSION(Component.translatable("techguns2.damageCategory.explosion").withStyle(ChatFormatting.DARK_RED)),
    /** Energy Weapons, Lasers, Magic */
    ENERGY(Component.translatable("techguns2.damageCategory.energy").withStyle(ChatFormatting.DARK_AQUA)),
    /** Gas, slimy stuff */
    POISON(Component.translatable("techguns2.damageCategory.poison").withStyle(ChatFormatting.DARK_GREEN)),
    /** Things that should never get reduced with armor value: starve, fall damage, suffocate... */
    ARMOR_PENETRATING(Component.translatable("techguns2.damageCategory.armor_penetrating").withStyle(ChatFormatting.BLACK)),
    /** Anything cold related */
    ICE(Component.translatable("techguns2.damageCategory.ice").withStyle(ChatFormatting.AQUA)),
    /** Electricity, shock, lightning magic */
    ELECTRICITY(Component.translatable("techguns2.damageCategory.electricity").withStyle(ChatFormatting.YELLOW)),
    /** Radiation */
    RADIATION(Component.translatable("techguns2.damageCategory.radiation").withStyle(ChatFormatting.GREEN)),
    /** Dark energy, black holes, dark magic */
    DARK(Component.translatable("techguns2.damageCategory.dark").withStyle(ChatFormatting.BLACK));
    
    private final Component _displayName;
    
    private DamageCategories(Component displayName)
    {
        this._displayName = displayName;
    }
    
    @Override
    public final Component displayName()
    {
        return this._displayName;
    }
}
