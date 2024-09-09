package techguns2.machine.reaction_chamber;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;

public interface LaserFocus
{
    ResourceLocation getId();
    boolean is(Item item);
    SoundEvent getSound();
}
