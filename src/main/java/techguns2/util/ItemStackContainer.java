package techguns2.util;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class ItemStackContainer extends ItemStackHandler
{
    public ItemStackContainer()
    {
        super();
    }
    
    public ItemStackContainer(int size)
    {
        super(size);
    }
    
    public ItemStackContainer(ItemStack stack)
    {
        super(1);
        this.stacks.set(0, stack);
    }
    
    public ItemStackContainer(@NotNull NonNullList<ItemStack> stacks)
    {
        super(stacks);
    }
    
    public boolean dynamicSize()
    {
        return true;
    }
    
    @Override
    public void setSize(int size)
    {
        if (!this.dynamicSize())
            throw new IllegalStateException("Cannot set size of non-dynamic item stack container");
        
        if (this.stacks.size() == size)
        {
            this.clear();
            return;
        }
        
        this.stacks = NonNullList.withSize(size, ItemStack.EMPTY);
    }

    @NotNull
    public ItemStack insertItem(@NotNull ItemStack stack, boolean simulate)
    {
        ItemStack result = stack;
        
        for (int slot = 0; slot < this.stacks.size(); slot++)
        {
            result = this.insertItem(slot, stack, simulate);
            if (result.isEmpty())
                break;
        }
        
        return result;
    }
    
    @Override
    @NotNull 
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate)
    {
        if (!this.canPlaceItemInSlot(slot, stack))
            return stack;
        
        return super.insertItem(slot, stack, simulate);
    }

    @NotNull
    public ItemStack insertItem(int startSlot, int endSlot, @NotNull ItemStack stack, boolean simulate)
    {
        ItemStack result = stack;
        
        for (int slot = startSlot; startSlot <= endSlot; slot++)
        {
            result = this.insertItem(slot, stack, simulate);
            if (result.isEmpty())
                break;
        }
        
        return result;
    }

    @NotNull
    public ItemStack insertItemUnchecked(@NotNull ItemStack stack, boolean simulate)
    {
        ItemStack result = stack;
        
        for (int slot = 0; slot < this.stacks.size(); slot++)
        {
            result = this.insertItemUnchecked(slot, stack, simulate);
            if (result.isEmpty())
                break;
        }
        
        return result;
    }
    
    @NotNull
    public ItemStack insertItemUnchecked(int slot, @NotNull ItemStack stack, boolean simulate)
    {
        return super.insertItem(slot, stack, simulate);
    }

    @NotNull
    public ItemStack insertItemUnchecked(int startSlot, int endSlot, @NotNull ItemStack stack, boolean simulate)
    {
        ItemStack result = stack;
        
        for (int slot = startSlot; startSlot <= endSlot; slot++)
        {
            result = this.insertItemUnchecked(slot, stack, simulate);
            if (result.isEmpty())
                break;
        }
        
        return result;
    }
    
    public List<ItemStack> insertAll(List<ItemStack> stacks)
    {
        if (stacks.isEmpty())
            return stacks;
        
        // we could optimize the initial capacity later
        List<ItemStack> remainingStacks = new ArrayList<ItemStack>();

        for (int stackIndex = 0; stackIndex < stacks.size(); stackIndex++)
        {
            ItemStack stack = stacks.get(stackIndex).copy();
            
            for (int index = 0; index < this.stacks.size(); index++)
            {
                stack = this.insertItem(index, stack, false);
            }
            
            if (!stack.isEmpty())
                remainingStacks.add(stack);
        }
        
        return remainingStacks;
    }
    
    public List<ItemStack> insertAllUnchecked(List<ItemStack> stacks)
    {
        if (stacks.isEmpty())
            return stacks;
        
        // we could optimize the initial capacity later
        List<ItemStack> remainingStacks = new ArrayList<ItemStack>();

        for (int stackIndex = 0; stackIndex < stacks.size(); stackIndex++)
        {
            ItemStack stack = stacks.get(stackIndex).copy();
            
            for (int index = 0; index < this.stacks.size(); index++)
            {
                stack = this.insertItemUnchecked(index, stack, false);
            }
            
            if (!stack.isEmpty())
                remainingStacks.add(stack);
        }
        
        return remainingStacks;
    }
    
    public List<ItemStack> insertAll(int startSlot, int endSlot, List<ItemStack> stacks)
    {
        if (endSlot < startSlot)
            return stacks;
        else if (stacks.isEmpty())
            return stacks;
        
        // we could optimize the initial capacity later
        List<ItemStack> remainingStacks = new ArrayList<ItemStack>();

        for (int stackIndex = 0; stackIndex < stacks.size(); stackIndex++)
        {
            ItemStack stack = stacks.get(stackIndex).copy();
            
            for (int index = startSlot; index <= endSlot; index++)
            {
                stack = this.insertItem(index, stack, false);
            }
            
            if (!stack.isEmpty())
                remainingStacks.add(stack);
        }
        
        return remainingStacks;
    }
    
    public List<ItemStack> insertAllUnchecked(int startSlot, int endSlot, List<ItemStack> stacks)
    {
        if (endSlot < startSlot)
            return stacks;
        else if (stacks.isEmpty())
            return stacks;
        
        // we could optimize the initial capacity later
        List<ItemStack> remainingStacks = new ArrayList<ItemStack>();

        for (int stackIndex = 0; stackIndex < stacks.size(); stackIndex++)
        {
            ItemStack stack = stacks.get(stackIndex).copy();
            
            for (int index = startSlot; index <= endSlot; index++)
            {
                stack = this.insertItemUnchecked(index, stack, false);
            }
            
            if (!stack.isEmpty())
                remainingStacks.add(stack);
        }
        
        return remainingStacks;
    }
    
    @Override
    @NotNull 
    public ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        if (!this.canTakeItemFromSlot(slot, amount))
            return ItemStack.EMPTY;
        
        return super.extractItem(slot, amount, simulate);
    }
    
    public ItemStack extractItemUnchecked(int slot, int amount, boolean simulate)
    {
        return super.extractItem(slot, amount, simulate);
    }
    
    public boolean canPlaceItemInSlot(int slot, @NotNull ItemStack itemStack)
    {
        return true;
    }
    
    public boolean canTakeItemFromSlot(int slot, int amount)
    {
        return true;
    }
    
    @Override
    public CompoundTag serializeNBT()
    {
        ListTag nbtTagList = new ListTag();
        for (int i = 0; i < stacks.size(); i++)
        {
            if (!stacks.get(i).isEmpty())
            {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putInt("Slot", i);
                stacks.get(i).save(itemTag);
                nbtTagList.add(itemTag);
            }
        }
        CompoundTag nbt = new CompoundTag();
        nbt.put("Items", nbtTagList);
        if (this.dynamicSize())
            nbt.putInt("Size", stacks.size());
        
        return nbt;
    }
    
    @Override
    public void deserializeNBT(CompoundTag nbt)
    {
        if (this.dynamicSize() && nbt.contains("Size", Tag.TAG_INT))
            this.setSize(nbt.getInt("Size"));
        else
            this.clear();
        
        ListTag tagList = nbt.getList("Items", Tag.TAG_COMPOUND);
        for (int i = 0; i < tagList.size(); i++)
        {
            CompoundTag itemTags = tagList.getCompound(i);
            int slot = itemTags.getInt("Slot");

            if (slot >= 0 && slot < this.stacks.size())
            {
                this.stacks.set(slot, ItemStack.of(itemTags));
            }
        }
        
        this.onLoad();
    }
    
    public void reset()
    {
        this.clear();
    }
    
    public boolean isEmpty()
    {
        for (int i = 0; i < this.stacks.size(); i++)
        {
            if (!this.stacks.get(i).isEmpty())
                return false;
        }
        
        return true;
    }
    
    public void clear()
    {
        for (int i = 0; i < this.stacks.size(); i++)
        {
            this.stacks.set(i, ItemStack.EMPTY);
        }
    }
    
    public View createView(int slot)
    {
        return new View(slot);
    }
    
    public View createView(int slotStart, int slotEnd)
    {
        return new View(slotStart, slotEnd);
    }
    
    public List<ItemStack> toList()
    {
        ArrayList<ItemStack> result = new ArrayList<ItemStack>(this.getSlots());
        for (int index = 0; index < this.getSlots(); index++)
        {
            result.set(index, this.getStackInSlot(index).copy());
        }
        return result;
    }
    
    public class View extends ItemStackContainer
    {
        protected final int _slotOffset;

        protected View(int slot)
        {
            super(1);
            
            this.stacks.set(0, ItemStackContainer.this.stacks.get(slot).copy());
            this._slotOffset = slot;
        }
        
        protected View(int slotStart, int slotEnd)
        {
            super(slotEnd - slotStart + 1);

            for (int slotIndex = 0; (slotIndex + slotStart) <= slotEnd; slotIndex++)
            {
                this.stacks.set(slotIndex, ItemStackContainer.this.stacks.get(slotIndex + slotStart).copy());
            }
            
            this._slotOffset = slotStart;
        }
        
        @Override
        public final boolean dynamicSize()
        {
            return false;
        }
        
        @Override
        public final boolean canPlaceItemInSlot(int slot, @NotNull ItemStack itemStack)
        {
            return ItemStackContainer.this.canPlaceItemInSlot(slot + this._slotOffset, itemStack);
        }
        
        @Override
        public final boolean canTakeItemFromSlot(int slot, int amount)
        {
            return ItemStackContainer.this.canTakeItemFromSlot(slot + this._slotOffset, amount);
        }
    }
}
