package techguns2.machine.reaction_chamber;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class LaserFocusSlot extends Slot
{
    public LaserFocusSlot(Container container, int index, int xPos, int yPos)
    {
        super(container, index, xPos, yPos);
    }
    
    @Override
    public boolean mayPlace(ItemStack itemStack)
    {
        return LaserFocuses.isItem(itemStack);
    }
}
