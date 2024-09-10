package techguns2.machine;

import net.minecraft.world.item.Item;

public abstract class MachineUpgradeItem extends Item
{
    public MachineUpgradeItem(Properties properties)
    {
        super(properties);
    }
    
    public abstract MachineUpgrade getUpgrade();
    
    public static final class Stack extends MachineUpgradeItem
    {
        public Stack(Properties properties)
        {
            super(properties);
        }
        
        @Override
        public final MachineUpgrade.Stack getUpgrade()
        {
            return MachineUpgrade.Stack.of();
        }
        
    }
}
