package techguns2.machine;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.inventory.MenuType;

public abstract class AbstractMultiBlockMachineMenu extends AbstractMachineMenu
{

    public AbstractMultiBlockMachineMenu(
            MenuType<? extends AbstractMultiBlockMachineMenu> menuType,
            int id,
            @Nullable UUID ownerId)
    {
        super(menuType, id, ownerId);
    }
    
    public abstract AbstractMultiBlockMachineContainerData getData();
    
    protected static abstract class ClientTickedContainerData extends AbstractMachineMenu.ClientTickedContainerData implements AbstractMultiBlockMachineContainerData
    {
        public ClientTickedContainerData(int slotCount)
        {
            super(slotCount);
        }
        
        @Override
        public final boolean isFormed()
        {
            return (this.getFlags() & AbstractMultiBlockMachineControllerBlockEntity.FORMED_FLAG) == AbstractMultiBlockMachineControllerBlockEntity.FORMED_FLAG;
        }

        @Override
        @Nullable 
        public AbstractMultiBlockMachineControllerBlockEntity<?, ?> getBlockEntity()
        {
            return null;
        }
    }
    
    protected static abstract class ClientContainerData extends AbstractMachineMenu.ClientContainerData implements AbstractMultiBlockMachineContainerData
    {
        public ClientContainerData(int slotCount)
        {
            super(slotCount);
        }
        
        @Override
        public final boolean isFormed()
        {
            return (this.getFlags() & AbstractMultiBlockMachineControllerBlockEntity.FORMED_FLAG) == AbstractMultiBlockMachineControllerBlockEntity.FORMED_FLAG;
        }

        @Override
        @Nullable 
        public AbstractMultiBlockMachineControllerBlockEntity<?, ?> getBlockEntity()
        {
            return null;
        }
    }
    
}
