package techguns2.machine.reaction_chamber;

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
import net.minecraftforge.registries.RegistryManager;
import techguns2.TGCustomRegistries;
import techguns2.TGMenus;
import techguns2.machine.AbstractMultiBlockMachineMenu;
import techguns2.machine.MachineInputSlot;
import techguns2.machine.MachineResultSlot;

public final class ReactionChamberMenu extends AbstractMultiBlockMachineMenu
{
    private final ReactionChamberContainerData _data;
    private final Container _container;
    
    public ReactionChamberMenu(int id, Inventory inventory, @Nullable UUID ownerId)
    {
        this(id, inventory, new SimpleContainer(ReactionChamberControllerBlockEntity.SLOT_COUNT), new ClientContainerData(), ownerId);
    }
    
    public ReactionChamberMenu(int id, Inventory inventory, Container container, ReactionChamberContainerData data, @Nullable UUID ownerId)
    {
        super(TGMenus.REACTION_CHAMBER.get(), id, ownerId);
        
        this._data = data;
        this._container = container;
        
        this.addDataSlots(data);
        
        this.addSlot(new MachineInputSlot(container, ReactionChamberControllerBlockEntity.SLOT_INPUT, 35, 17));
        this.addSlot(new LaserFocusSlot(container, ReactionChamberControllerBlockEntity.SLOT_FOCUS, 93, 17));
        this.addSlot(new MachineResultSlot(container, ReactionChamberControllerBlockEntity.SLOT_RESULT1, 134, 17));
        this.addSlot(new MachineResultSlot(container, ReactionChamberControllerBlockEntity.SLOT_RESULT2, 152, 17));
        this.addSlot(new MachineResultSlot(container, ReactionChamberControllerBlockEntity.SLOT_RESULT3, 134, 35));
        this.addSlot(new MachineResultSlot(container, ReactionChamberControllerBlockEntity.SLOT_RESULT4, 152, 35));
        
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
    
    private static final int INVENTORY_START = ReactionChamberControllerBlockEntity.SLOT_COUNT;
    private static final int INVENTORY_END = ReactionChamberControllerBlockEntity.SLOT_COUNT + 26;
    private static final int HOTBAR_START = INVENTORY_END + 1;
    private static final int HOTBAR_END = HOTBAR_START + 8;

    // Slot Key:
    //   0 - Input
    //   1 - Focus
    //   2-5 - Result output
    //   6-32 - Player Inventory
    //   33-41 - Player Hot-bar
    @Override
    public final ItemStack quickMoveStack(Player player, int slotIndex)
    {
        Slot slot = this.getSlot(slotIndex);
        if (slot == null || !slot.hasItem())
            return ItemStack.EMPTY;
        
        ItemStack resultItemStack = slot.getItem();
        ItemStack previousSlotItemStack = resultItemStack.copy();
        // quick move performed on result item
        if (slotIndex >= ReactionChamberControllerBlockEntity.SLOT_RESULT1 && 
                slotIndex <= ReactionChamberControllerBlockEntity.SLOT_RESULT4)
        {
            // try to move to into the player inventory/hot-bar from last to first
            if (!this.moveItemStackTo(resultItemStack, 6, 41, true))
                return ItemStack.EMPTY;
            
            // perform logic on the result slot quick move
            slot.onQuickCraft(resultItemStack, previousSlotItemStack);
        }
        else if (slotIndex < 2)
        {
            // try to move to into the player inventory/hot-bar from first to last
            if (!this.moveItemStackTo(resultItemStack, INVENTORY_START, HOTBAR_END, false))
                return ItemStack.EMPTY;
        }
        // else quick move was performed on player inventory or hot-bar slot
        else
        {
            // First try to move item into input slot & focus slot
            if (!this.moveItemStackTo(resultItemStack, ReactionChamberControllerBlockEntity.SLOT_INPUT, ReactionChamberControllerBlockEntity.SLOT_FOCUS, false))
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
    public final ReactionChamberContainerData getData()
    {
        return this._data;
    }
    
    private static final class ClientContainerData extends AbstractMultiBlockMachineMenu.ClientTickedContainerData implements ReactionChamberContainerData
    {
        public ClientContainerData()
        {
            super(ReactionChamberControllerBlockEntity.NEXT_DATA_SLOT);
        }
        
        @Override
        @Nullable 
        public final ReactionChamberControllerBlockEntity getBlockEntity()
        {
            return null;
        }

        @Override
        public final FluidStack getFluid()
        {
            int amount = this.get(ReactionChamberControllerBlockEntity.DATA_SLOT_FLUID_AMOUNT);
            if (amount <= 0)
                return FluidStack.EMPTY;
            
            int id = this.get(ReactionChamberControllerBlockEntity.DATA_SLOT_FLUID_ID);
            Fluid fluid = RegistryManager.ACTIVE.getRegistry(ForgeRegistries.Keys.FLUIDS).getValue(id);
            if (fluid == null)
                return FluidStack.EMPTY;
            else
                return new FluidStack(fluid, amount);
        }
        
        @Override
        public final void dumpFluid()
        {
            this.set(ReactionChamberControllerBlockEntity.DATA_SLOT_FLUID_AMOUNT, 0);
        }

        @Override
        @Nullable 
        public final LaserFocus getLaserFocus()
        {
            int id = this.get(ReactionChamberControllerBlockEntity.DATA_SLOT_FOCUS);
            if (id < 0)
                return null;
            
            return RegistryManager.ACTIVE.getRegistry(TGCustomRegistries.LASER_FOCUSES).getValue(id);
        }

        @Override
        public final ReactionRisk getReactionRisk()
        {
            int id = this.get(ReactionChamberControllerBlockEntity.DATA_SLOT_REACTION_RISK);
            return RegistryManager.ACTIVE.getRegistry(TGCustomRegistries.REACTION_RISKS).getValue(id);
        }

        @Override
        public final int getFluidLevel()
        {
            return this.get(ReactionChamberControllerBlockEntity.DATA_SLOT_FLUID_LEVEL);
        }

        @Override
        public final void setFluidLevel(int fluidLevel)
        {
            this.set(ReactionChamberControllerBlockEntity.DATA_SLOT_FLUID_LEVEL, Math.max(0, Math.min(10, fluidLevel)));
        }

        @Override
        public final int getIntensity()
        {
            return this.get(ReactionChamberControllerBlockEntity.DATA_SLOT_INTENSITY);
        }

        @Override
        public final void setIntensity(int intensity)
        {
            this.set(ReactionChamberControllerBlockEntity.DATA_SLOT_INTENSITY, Math.max(0, Math.min(10, intensity)));
        }

        @Override
        public final int getReactionRequiredIntensity()
        {
            return this.get(ReactionChamberControllerBlockEntity.DATA_SLOT_REACTION_REQUIRED_INTENSITY);
        }

        @Override
        public final int getReactionCycle()
        {
            return this.get(ReactionChamberControllerBlockEntity.DATA_SLOT_REACTION_CYCLE);
        }

        @Override
        public final int getReactionSuccessfulCycles()
        {
            return this.get(ReactionChamberControllerBlockEntity.DATA_SLOT_REACTION_SUCCESSFUL_CYCLES);
        }

        @Override
        public final int getReactionRequiredCycles()
        {
            return this.get(ReactionChamberControllerBlockEntity.DATA_SLOT_REACTION_REQUIRED_CYCLES);
        }

        @Override
        public final int getReactionMaximumCycles()
        {
            return this.get(ReactionChamberControllerBlockEntity.DATA_SLOT_REACTION_MAXIMUM_CYCLES);
        }

        @Override
        public final int getReactionCycleTotalTicks()
        {
            return this.get(ReactionChamberControllerBlockEntity.DATA_SLOT_REACTION_CYCLE_TOTAL_TICKS);
        }

        @Override
        public final int getReactionCycleTicksRemaining()
        {
            return this.get(ReactionChamberControllerBlockEntity.DATA_SLOT_REACTION_CYCLE_TICKS_REMAINING);
        }
        
        @Override
        public final int getOperationTotalTicks()
        {
            return this.getReactionMaximumCycles() * this.getReactionCycleTotalTicks();
        }
        
        @Override
        public final int getOperationTicksRemaining()
        {
            int maximumCycles = this.getReactionMaximumCycles();
            if (maximumCycles == 0)
                return 0;
            
            return (this.getReactionCycleTotalTicks() *
                    (this.getReactionMaximumCycles() - this.getReactionCycle() - 1)) + this.getReactionCycleTicksRemaining();
        }
        
    }

}