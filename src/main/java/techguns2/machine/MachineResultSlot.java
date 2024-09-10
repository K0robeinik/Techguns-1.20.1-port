package techguns2.machine;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class MachineResultSlot extends Slot
{
    public MachineResultSlot(Container container, int index, int xPos, int yPos)
    {
        super(container, index, xPos, yPos);
    }
    
    @Override
    public final boolean mayPlace(ItemStack stack)
    {
        return false;
    }
}
