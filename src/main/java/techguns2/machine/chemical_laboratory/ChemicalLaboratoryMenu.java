package techguns2.machine.chemical_laboratory;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;
import techguns2.TGMenus;
import techguns2.machine.AbstractMachineMenu;
import techguns2.machine.MachineInputSlot;
import techguns2.machine.MachineResultSlot;
import techguns2.machine.MachineUpgradeSlot;

public final class ChemicalLaboratoryMenu extends AbstractMachineMenu
{
    private final ChemicalLaboratoryContainerData _data;
    private final Container _container;
    
    public ChemicalLaboratoryMenu(int id, Inventory inventory, @Nullable UUID ownerId)
    {
        this(id, inventory, new SimpleContainer(ChemicalLaboratoryBlockEntity.SLOT_COUNT), new ClientContainerData(), ownerId);
    }
    
    public ChemicalLaboratoryMenu(int id, Inventory inventory, Container container, ChemicalLaboratoryContainerData data, @Nullable UUID ownerId)
    {
        super(TGMenus.CHEMICAL_LABORATORY.get(), id, ownerId);
        
        this._data = data;
        this._container = container;
        
        this.addDataSlots(data);
        
        this.addSlot(new MachineInputSlot(container, ChemicalLaboratoryBlockEntity.SLOT_PRIMARY, 35, 17));
        this.addSlot(new MachineInputSlot(container, ChemicalLaboratoryBlockEntity.SLOT_SECONDARY, 57, 17));
        this.addSlot(new MachineInputSlot(container, ChemicalLaboratoryBlockEntity.SLOT_TANK_INPUT, 35, 40));
        this.addSlot(new MachineResultSlot(container, ChemicalLaboratoryBlockEntity.SLOT_RESULT, 135, 17));
        this.addSlot(new MachineUpgradeSlot(container, ChemicalLaboratoryBlockEntity.SLOT_UPGRADE, 135, 60));
        
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
    
    private static final int INVENTORY_START = ChemicalLaboratoryBlockEntity.SLOT_COUNT;
    private static final int INVENTORY_END = ChemicalLaboratoryBlockEntity.SLOT_COUNT + 26;
    private static final int HOTBAR_START = INVENTORY_END + 1;
    private static final int HOTBAR_END = HOTBAR_START + 8;
    
    // Slot Key:
    //   0 - Primary Input
    //   1 - Secondary Input
    //   2 - Tank Input
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
        if (slotIndex == ChemicalLaboratoryBlockEntity.SLOT_RESULT)
        {
            // try to move to into the player inventory/hot-bar from last to first
            if (!this.moveItemStackTo(resultItemStack, INVENTORY_START, HOTBAR_END, true))
                return ItemStack.EMPTY;
            
            // perform logic on the result slot quick move
            slot.onQuickCraft(resultItemStack, previousSlotItemStack);
        }
        // quick move performed on input item or upgrade item. Note slotIndex != 2
        else if (slotIndex < ChemicalLaboratoryBlockEntity.SLOT_COUNT)
        {
            // try to move to into the player inventory/hot-bar from first to last
            if (!this.moveItemStackTo(resultItemStack, INVENTORY_START, HOTBAR_END, false))
                return ItemStack.EMPTY;
        }
        // else quick move was performed on player inventory or hot-bar slot
        else
        {
            // First try to move item into input slots & upgrade slot
            if (!this.moveItemStackTo(resultItemStack, ChemicalLaboratoryBlockEntity.SLOT_PRIMARY, ChemicalLaboratoryBlockEntity.SLOT_TANK_INPUT, false) && 
                    !this.moveItemStackTo(resultItemStack, ChemicalLaboratoryBlockEntity.SLOT_UPGRADE, ChemicalLaboratoryBlockEntity.SLOT_UPGRADE, false))
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
    public final boolean stillValid(Player p_38874_)
    {
        return this._container.stillValid(p_38874_);
    }
    
    @Override
    public final ChemicalLaboratoryContainerData getData()
    {
        return this._data;
    }
    
    private static final class ClientContainerData extends AbstractMachineMenu.ClientTickedContainerData implements ChemicalLaboratoryContainerData
    {
        public ClientContainerData()
        {
            super(ChemicalLaboratoryBlockEntity.NEXT_DATA_SLOT);
        }
        
        @Override
        @Nullable 
        public final ChemicalLaboratoryBlockEntity getBlockEntity()
        {
            return null;
        }

        @Override
        public final FluidStack getFluid()
        {
            int fluidId = this.get(ChemicalLaboratoryBlockEntity.DATA_SLOT_FLUID_ID);
            if (fluidId < 0)
            {
                return FluidStack.EMPTY;
            }
            Fluid fluid = ((ForgeRegistry<Fluid>)ForgeRegistries.FLUIDS).getValue(fluidId);
            return new FluidStack(fluid, this.get(ChemicalLaboratoryBlockEntity.DATA_SLOT_FLUID_AMOUNT));
        }
        
        @Override
        public final void dumpFluid()
        {
            this.set(ChemicalLaboratoryBlockEntity.DATA_SLOT_FLUID_AMOUNT, 0);
        }

        @Override
        public final FluidStack getResultFluid()
        {
            int fluidId = this.get(ChemicalLaboratoryBlockEntity.DATA_SLOT_RESULT_FLUID_ID);
            if (fluidId < 0)
            {
                return FluidStack.EMPTY;
            }
            Fluid fluid = ((ForgeRegistry<Fluid>)ForgeRegistries.FLUIDS).getValue(fluidId);
            return new FluidStack(fluid, this.get(ChemicalLaboratoryBlockEntity.DATA_SLOT_RESULT_FLUID_AMOUNT));
        }
        
        @Override
        public final void dumpResultFluid()
        {
            this.set(ChemicalLaboratoryBlockEntity.DATA_SLOT_RESULT_FLUID_AMOUNT, 0);
        }

        @Override
        public final DrainMode getDrainMode()
        {
            if ((this.getFlags() & ChemicalLaboratoryBlockEntity.DRAIN_FLAG) == ChemicalLaboratoryBlockEntity.DRAIN_FLAG)
                return DrainMode.FROM_OUTPUT;
            else
                return DrainMode.FROM_INPUT;
        }

        @Override
        public final void setDrainMode(DrainMode mode)
        {
            switch (mode)
            {
                case FROM_INPUT:
                    this.setFlags((byte)(this.getFlags() & ~ChemicalLaboratoryBlockEntity.DRAIN_FLAG));
                    break;
                case FROM_OUTPUT:
                    this.setFlags((byte)(this.getFlags() | ChemicalLaboratoryBlockEntity.DRAIN_FLAG));
                    break;
                    
            }
        }
        
    }
}
