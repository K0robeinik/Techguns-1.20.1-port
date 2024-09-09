package techguns2.machine.reaction_chamber;

import java.util.function.Supplier;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryManager;
import techguns2.TGCustomRegistries;
import techguns2.TGItems;
import techguns2.Techguns;

public enum LaserFocuses implements LaserFocus, StringRepresentable
{
    HEAT_RAY("heat_ray", TGItems.REACTION_CHAMBER_HEAT_RAY_FOCUS),
    UV_EMITTER("uv_emitter", TGItems.REACTION_CHAMBER_UV_EMITTER);
    
    private final ResourceLocation _id;
    private final Supplier<? extends Item> _itemSupplier;
    
    private LaserFocuses(String id, Supplier<? extends Item> itemSupplier)
    {
        this._id = new ResourceLocation(Techguns.MODID, id);
        this._itemSupplier = itemSupplier;
    }

    @Override
    public String getSerializedName()
    {
        return this._id.toString();
    }

    @Override
    public ResourceLocation getId()
    {
        return this._id;
    }

    @Override
    public boolean is(Item item)
    {
        return this._itemSupplier.get() == item;
    }

    @Override
    public SoundEvent getSound()
    {
        // TODO: implement sound events
        return null;
    }
    
    private static boolean _registered;

    @Deprecated
    public static void register(NewRegistryEvent newRegistryEvent)
    {
        if (_registered)
            return;
        
        RegistryBuilder<LaserFocus> builder = RegistryBuilder.of(TGCustomRegistries.LASER_FOCUSES.location());
        
        newRegistryEvent.create(builder, (registry) -> {
            registry.register(HEAT_RAY.getId(), HEAT_RAY);
            registry.register(UV_EMITTER.getId(), UV_EMITTER);
        });
        
        _registered = true;
    }
    
    public static boolean isItem(ItemStack itemStack)
    {
        Item item = itemStack.getItem();
        
        for (LaserFocus laserFocus : RegistryManager.ACTIVE.getRegistry(TGCustomRegistries.LASER_FOCUSES).getValues())
        {
            if (laserFocus.is(item))
                return true;
        }
        
        return false;
    }
}
