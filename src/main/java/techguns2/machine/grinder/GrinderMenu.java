package techguns2.machine.grinder;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import techguns2.TGMenus;
import techguns2.machine.AbstractMachineMenu;
import techguns2.machine.MachineInputSlot;
import techguns2.machine.MachineResultSlot;
import techguns2.machine.MachineUpgradeSlot;

public final class GrinderMenu extends AbstractMachineMenu
{
    private final GrinderContainerData _data;
    private final Container _container;
    
    public GrinderMenu(int id, Inventory inventory, @Nullable UUID ownerId)
    {
        this(id, inventory, new SimpleContainer(GrinderBlockEntity.SLOT_COUNT), new ClientContainerData(), ownerId);
    }
    
    public GrinderMenu(int id, Inventory inventory, Container container, GrinderContainerData data, @Nullable UUID ownerId)
    {
        super(TGMenus.GRINDER.get(), id, ownerId);
        
        this._data = data;
        this._container = container;
        
        this.addDataSlots(data);
        
        this.addSlot(new MachineInputSlot(container, GrinderBlockEntity.SLOT_INPUT, 19, 17));
        

        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 3; j++)
            {
                this.addSlot(new MachineResultSlot(inventory, GrinderBlockEntity.SLOT_RESULT1 + (i * 3) + j,
                        80 + (j * 18),
                        17 + (i*18)));
            }
        }
        
        this.addSlot(new MachineUpgradeSlot(container, GrinderBlockEntity.SLOT_UPGRADE, 152, 60));
        
        for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
                this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
        }
        
        for (int k = 0; k < 9; ++k)
        {
            this.addSlot(new Slot(inventory, k, 8 + k * 18, 142));
        }
    }
    
    private static final int INVENTORY_START = GrinderBlockEntity.SLOT_COUNT;
    private static final int INVENTORY_END = GrinderBlockEntity.SLOT_COUNT + 26;
    private static final int HOTBAR_START = INVENTORY_END + 1;
    private static final int HOTBAR_END = HOTBAR_START + 8;

    // Slot Key:
    //   0 - Input
    //   1-9 - Result Output
    //   10 - Upgrade
    //   11-37 - Player Inventory
    //   38-46 - Player Hot-bar
    @Override
    public final ItemStack quickMoveStack(Player player, int slotIndex)
    {
        Slot slot = this.getSlot(slotIndex);
        if (slot == null || !slot.hasItem())
            return ItemStack.EMPTY;
        
        ItemStack resultItemStack = slot.getItem();
        ItemStack previousSlotItemStack = resultItemStack.copy();
        // quick move performed on result item
        if (slotIndex >= GrinderBlockEntity.SLOT_RESULT1 && slotIndex <= GrinderBlockEntity.SLOT_RESULT9)
        {
            // try to move to into the player inventory/hot-bar from last to first
            if (!this.moveItemStackTo(resultItemStack, INVENTORY_START, HOTBAR_END, true))
                return ItemStack.EMPTY;
            
            // perform logic on the result slot quick move
            slot.onQuickCraft(resultItemStack, previousSlotItemStack);
        }
        // quick move performed on input item or upgrade item. Note slotIndex is not a result slot
        else if (slotIndex < GrinderBlockEntity.SLOT_COUNT)
        {
            // try to move to into the player inventory/hot-bar from first to last
            if (!this.moveItemStackTo(resultItemStack, INVENTORY_START, HOTBAR_END, false))
                return ItemStack.EMPTY;
        }
        // else quick move was performed on player inventory or hot-bar slot
        else
        {
            // First try to move item into input slots & upgrade slot
            if (!this.moveItemStackTo(resultItemStack, GrinderBlockEntity.SLOT_INPUT, GrinderBlockEntity.SLOT_INPUT, false) && 
                    !this.moveItemStackTo(resultItemStack, GrinderBlockEntity.SLOT_UPGRADE, GrinderBlockEntity.SLOT_UPGRADE, false))
            {
                // Failed
                // Try to move to inventory to hot-bar
                if (slotIndex < HOTBAR_START)
                {
                    if (!this.moveItemStackTo(resultItemStack, HOTBAR_START, HOTBAR_END, false))
                        return ItemStack.EMPTY;
                }
                // Try to move hot-bar to inventory
                else
                {
                    if (!this.moveItemStackTo(resultItemStack, INVENTORY_START, INVENTORY_END, false))
                        return ItemStack.EMPTY;
                }
            }
        }
        
        if (resultItemStack.isEmpty())
            slot.setByPlayer(ItemStack.EMPTY);
        else
            slot.setChanged();
        
        if (previousSlotItemStack.getCount() == resultItemStack.getCount())
            return ItemStack.EMPTY;
        
        // execute logic on what to do with the remaining stack
        slot.onTake(player, resultItemStack);
        return previousSlotItemStack;
    }

    @Override
    public final boolean stillValid(Player player)
    {
        return this._container.stillValid(player);
    }
    
    @Override
    public final GrinderContainerData getData()
    {
        return this._data;
    }
    
    private static final class ClientContainerData extends AbstractMachineMenu.ClientTickedContainerData implements GrinderContainerData
    {
        public ClientContainerData()
        {
            super(GrinderBlockEntity.NEXT_DATA_SLOT);
        }
        
        @Override
        @Nullable 
        public final GrinderBlockEntity getBlockEntity()
        {
            return null;
        }
        
    }
}
