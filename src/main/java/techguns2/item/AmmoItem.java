package techguns2.item;

import java.util.function.Supplier;

import net.minecraft.world.item.Item;
import techguns2.ammo.IAmmo;

public class AmmoItem<T extends IAmmo> extends Item
{
    private final Supplier<T> _ammoSupplier;
    
    public AmmoItem(Supplier<T> ammoSupplier, Properties properties)
    {
        super(properties);
        
        this._ammoSupplier = ammoSupplier;
    }
    
    public T getAmmo()
    {
        return this._ammoSupplier.get();
    }

}
