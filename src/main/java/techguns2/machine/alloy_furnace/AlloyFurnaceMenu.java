package techguns2.machine.alloy_furnace;

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

public final class AlloyFurnaceMenu extends AbstractMachineMenu
{
    private final AlloyFurnaceContainerData _data;
    private final Container _container;
    
    public AlloyFurnaceMenu(int id, Inventory inventory, @Nullable UUID ownerId)
    {
        this(id, inventory, new SimpleContainer(AlloyFurnaceBlockEntity.SLOT_COUNT), new ClientContainerData(), ownerId);
    }
    
    public AlloyFurnaceMenu(int id, Inventory inventory, Container container, AlloyFurnaceContainerData data, @Nullable UUID ownerId)
    {
        super(TGMenus.ALLOY_FURNACE.get(), id, ownerId);
        
        this._data = data;
        this._container = container;
        
        this.addDataSlots(data);
        
        this.addSlot(new MachineInputSlot(container, AlloyFurnaceBlockEntity.SLOT_PRIMARY, 19, 17));
        this.addSlot(new MachineInputSlot(container, AlloyFurnaceBlockEntity.SLOT_SECONDARY, 47, 17));
        this.addSlot(new MachineResultSlot(container, AlloyFurnaceBlockEntity.SLOT_RESULT, 116, 50));
        this.addSlot(new MachineUpgradeSlot(container, AlloyFurnaceBlockEntity.SLOT_UPGRADE, 150, 50));
        
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

    private static final int INVENTORY_START = AlloyFurnaceBlockEntity.SLOT_COUNT;
    private static final int INVENTORY_END = AlloyFurnaceBlockEntity.SLOT_COUNT + 26;
    private static final int HOTBAR_START = INVENTORY_END + 1;
    private static final int HOTBAR_END = HOTBAR_START + 8;
    
    // Slot Key:
    //   0 - Primary Input
    //   1 - Secondary Input
    //   2 - Result Output
    //   3 - Upgrade
    //   4-30 - Player Inventory
    //   31-39 - Player Hot-bar
    @Override
    public final ItemStack quickMoveStack(Player player, int slotIndex)
    {
        Slot slot = this.getSlot(slotIndex);
        if (slot == null || !slot.hasItem())
            return ItemStack.EMPTY;
        
        ItemStack resultItemStack = slot.getItem();
        ItemStack previousSlotItemStack = resultItemStack.copy();
        // quick move performed on result item
        if (slotIndex == AlloyFurnaceBlockEntity.SLOT_RESULT)
        {
            // try to move to into the player inventory/hot-bar from last to first
            if (!this.moveItemStackTo(resultItemStack, INVENTORY_START, HOTBAR_END, true))
                return ItemStack.EMPTY;
            
            // perform logic on the result slot quick move
            slot.onQuickCraft(resultItemStack, previousSlotItemStack);
        }
        // quick move performed on input item or upgrade item. Note slotIndex != SLOT_RESULT
        else if (slotIndex < INVENTORY_START)
        {
            // try to move to into the player inventory/hot-bar from first to last
            if (!this.moveItemStackTo(resultItemStack, INVENTORY_START, HOTBAR_END, false))
                return ItemStack.EMPTY;
        }
        // else quick move was performed on player inventory or hot-bar slot
        else
        {
            // First try to move item into input slots & upgrade slot
            if (!this.moveItemStackTo(resultItemStack, AlloyFurnaceBlockEntity.SLOT_PRIMARY, AlloyFurnaceBlockEntity.SLOT_SECONDARY, false) && 
                    !this.moveItemStackTo(resultItemStack, AlloyFurnaceBlockEntity.SLOT_UPGRADE, AlloyFurnaceBlockEntity.SLOT_UPGRADE, false))
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
    public final AlloyFurnaceContainerData getData()
    {
        return this._data;
    }
    
    private static final class ClientContainerData extends AbstractMachineMenu.ClientTickedContainerData implements AlloyFurnaceContainerData
    {
        public ClientContainerData()
        {
            super(AlloyFurnaceBlockEntity.NEXT_DATA_SLOT);
        }
        
        @Override
        @Nullable 
        public AlloyFurnaceBlockEntity getBlockEntity()
        {
            return null;
        }
        
    }
}
