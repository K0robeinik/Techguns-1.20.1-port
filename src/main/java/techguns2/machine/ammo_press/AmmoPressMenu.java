package techguns2.machine.ammo_press;

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

public final class AmmoPressMenu extends AbstractMachineMenu
{
    private final AmmoPressContainerData _data;
    private final Container _container;
    
    public AmmoPressMenu(int id, Inventory inventory, @Nullable UUID ownerId)
    {
        this(id, inventory, new SimpleContainer(AmmoPressBlockEntity.SLOT_COUNT), new ClientContainerData(), ownerId);
    }
    
    public AmmoPressMenu(int id, Inventory inventory, Container container, AmmoPressContainerData data, @Nullable UUID ownerId)
    {
        super(TGMenus.AMMO_PRESS.get(), id, ownerId);
        
        this._data = data;
        this._container = container;
        
        this.addDataSlots(data);
        
        this.addSlot(new MachineInputSlot(container, AmmoPressBlockEntity.SLOT_BULLET, 100, 17));
        this.addSlot(new MachineInputSlot(container, AmmoPressBlockEntity.SLOT_CASING, 120, 17));
        this.addSlot(new MachineInputSlot(container, AmmoPressBlockEntity.SLOT_POWDER, 140, 17));
        this.addSlot(new MachineResultSlot(container, AmmoPressBlockEntity.SLOT_RESULT, 120, 60));
        this.addSlot(new MachineUpgradeSlot(container, AmmoPressBlockEntity.SLOT_UPGRADE, 152, 60));
        
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
    
    private static final int INVENTORY_START = AmmoPressBlockEntity.SLOT_COUNT;
    private static final int INVENTORY_END = AmmoPressBlockEntity.SLOT_COUNT + 26;
    private static final int HOTBAR_START = INVENTORY_END + 1;
    private static final int HOTBAR_END = HOTBAR_START + 8;

    // Slot Key:
    //   0 - Bullet Input
    //   1 - Casing Input
    //   2 - Powder Input
    //   3 - Result Output
    //   4 - Upgrade
    //   5-31 - Player Inventory
    //   32-40 - Player Hot-bar
    @Override
    public final ItemStack quickMoveStack(Player player, int slotIndex)
    {
        Slot slot = this.getSlot(slotIndex);
        if (slot == null || !slot.hasItem())
            return ItemStack.EMPTY;
        
        ItemStack resultItemStack = slot.getItem();
        ItemStack previousSlotItemStack = resultItemStack.copy();
        // quick move performed on result item
        if (slotIndex == AmmoPressBlockEntity.SLOT_RESULT)
        {
            // try to move to into the player inventory/hot-bar from last to first
            if (!this.moveItemStackTo(resultItemStack, INVENTORY_START, HOTBAR_END, true))
                return ItemStack.EMPTY;
            
            // perform logic on the result slot quick move
            slot.onQuickCraft(resultItemStack, previousSlotItemStack);
        }
        // quick move performed on input item or upgrade item. Note slotIndex != 3
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
            if (!this.moveItemStackTo(resultItemStack, AmmoPressBlockEntity.SLOT_BULLET, AmmoPressBlockEntity.SLOT_POWDER, false) && 
                    !this.moveItemStackTo(resultItemStack, AmmoPressBlockEntity.SLOT_UPGRADE, AmmoPressBlockEntity.SLOT_UPGRADE, false))
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
    public final AmmoPressContainerData getData()
    {
        return this._data;
    }
    
    private static final class ClientContainerData extends AbstractMachineMenu.ClientTickedContainerData implements AmmoPressContainerData
    {
        public ClientContainerData()
        {
            super(AmmoPressBlockEntity.NEXT_DATA_SLOT);
        }
        
        @Override
        @Nullable 
        public AmmoPressBlockEntity getBlockEntity()
        {
            return null;
        }

        @Override
        public final AmmoPressTemplate getTemplate()
        {
            return AmmoPressTemplates.getFromIndex(this.get(AmmoPressBlockEntity.DATA_SLOT_TEMPLATE));
        }

        @Override
        public final void setTemplate(AmmoPressTemplate template)
        {
            this.set(AmmoPressBlockEntity.DATA_SLOT_TEMPLATE, template.getIndex());
        }
        
    }
}
