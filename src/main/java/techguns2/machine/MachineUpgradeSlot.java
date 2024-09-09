package techguns2.machine;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class MachineUpgradeSlot extends Slot
{
    public MachineUpgradeSlot(Container container, int index, int xPos, int yPos)
    {
        super(container, index, xPos, yPos);
    }
    
    @Override
    public boolean mayPlace(ItemStack stack)
    {
        return isValidItem(stack);
    }
    
    public static final boolean isValidItem(ItemStack itemStack)
    {
        return itemStack.getItem() instanceof MachineUpgradeItem;
    }
}
