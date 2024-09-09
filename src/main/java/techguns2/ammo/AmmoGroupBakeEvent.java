package techguns2.ammo;

import java.util.Objects;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;

/**
 * A forge event called right when an Ammo Group is about to be baked.
 */
public class AmmoGroupBakeEvent extends Event
{
    /**
     * The ammo group to be baked.
     */
    public final AmmoGroup ammoGroup;
    
    /**
     * The location of the baked ammo group.
     */
    public final ResourceLocation location;
    
    public AmmoGroupBakeEvent(AmmoGroup ammoGroup, ResourceLocation location)
    {
        Objects.requireNonNull(ammoGroup, "Ammo Group cannot be null.");
        Objects.requireNonNull(location, "Location cannot be null.");
        
        this.ammoGroup = ammoGroup;
        this.location = location;
    }

}
