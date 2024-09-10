package techguns2.util;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class FluidStackContainer implements IFluidHandler, INBTSerializable<CompoundTag>
{
    protected NonNullList<FluidStack> stacks;
    
    public FluidStackContainer()
    {
        this(1);
    }
    
    public FluidStackContainer(int size)
    {
        this.stacks = NonNullList.withSize(size, FluidStack.EMPTY);
    }
    
    public FluidStackContainer(FluidStack stack)
    {
        this(1);
        this.stacks.set(0, stack);
    }
    
    public FluidStackContainer(@NotNull NonNullList<FluidStack> stacks)
    {
        this.stacks = stacks;
    }

    public int getSlots()
    {
        return this.stacks.size();
    }

    @Deprecated
    @Override
    public final int getTanks()
    {
        return this.getSlots();
    }

    public int getSlotCapacity(int slot)
    {
        return 1000;
    }

    @Deprecated
    @Override
    public final int getTankCapacity(int tank)
    {
        return this.getSlotCapacity(tank);
    }
    
    public boolean dynamicSize()
    {
        return true;
    }
    
    public void setSize(int size)
    {
        if (!this.dynamicSize())
            throw new IllegalStateException("Cannot set size of non-dynamic fluid stack container");
        
        if (this.stacks.size() == size)
        {
            this.clear();
            return;
        }
        
        this.stacks = NonNullList.withSize(size, FluidStack.EMPTY);
    }
    
    public void setStackInSlot(int slot, @NotNull FluidStack stack)
    {
        this.validateSlotIndex(slot);
        this.stacks.set(slot, stack);
        this.onContentsChanged(slot);
    }

    @Deprecated
    @Override
    @NotNull 
    public final FluidStack getFluidInTank(int tank)
    {
        return this.getStackInSlot(tank);
    }
    
    @NotNull
    public FluidStack getStackInSlot(int slot)
    {
        this.validateSlotIndex(slot);
        return this.stacks.get(slot);
    }

    @Override
    public final int fill(FluidStack resource, FluidAction action)
    {
        return resource.getAmount() - this.insertFluid(resource, action).getAmount();
    }

    @NotNull
    public FluidStack insertFluid(@NotNull FluidStack stack, FluidAction action)
    {
        FluidStack result = stack;
        
        for (int slot = 0; slot < this.stacks.size(); slot++)
        {
            result = this.insertFluid(slot, stack, action);
            if (result.isEmpty())
                break;
        }
        
        return result;
    }
    
    @NotNull 
    public FluidStack insertFluid(int slot, @NotNull FluidStack stack, FluidAction action)
    {
        if (!this.canFillFluidInSlot(slot, stack))
            return stack;
        
        return this.insertFluidUnchecked(slot, stack, action);
    }

    @NotNull
    public FluidStack insertFluid(int startSlot, int endSlot, @NotNull FluidStack stack, FluidAction action)
    {
        FluidStack result = stack;
        
        for (int slot = startSlot; startSlot <= endSlot; slot++)
        {
            result = this.insertFluid(slot, stack, action);
            if (result.isEmpty())
                break;
        }
        
        return result;
    }

    @NotNull
    public FluidStack insertFluidUnchecked(@NotNull FluidStack stack, FluidAction action)
    {
        FluidStack result = stack;
        
        for (int slot = 0; slot < this.stacks.size(); slot++)
        {
            result = this.insertFluidUnchecked(slot, stack, action);
            if (result.isEmpty())
                break;
        }
        
        return result;
    }
    
    @NotNull
    public FluidStack insertFluidUnchecked(int slot, @NotNull FluidStack stack, FluidAction action)
    {
        if (stack.isEmpty())
            return FluidStack.EMPTY;

        if (!this.isFluidValid(slot, stack))
            return stack;

        this.validateSlotIndex(slot);

        FluidStack existing = this.stacks.get(slot);

        int limit = this.getSlotCapacity(slot);

        if (!existing.isEmpty())
        {
            if (!StackUtils.canFluidStacksStack(stack, existing))
                return stack;

            limit -= existing.getAmount();
        }

        if (limit <= 0)
            return stack;

        boolean reachedLimit = stack.getAmount() > limit;

        if (!action.simulate())
        {
            if (existing.isEmpty())
            {
                this.stacks.set(slot, reachedLimit ? new FluidStack(stack, limit) : stack);
            }
            else
            {
                existing.grow(reachedLimit ? limit : stack.getAmount());
            }
            this.onContentsChanged(slot);
        }
        
        if (reachedLimit)
        {
            int resultAmount = stack.getAmount() - limit;
            if (resultAmount == 0)
                return FluidStack.EMPTY;
            
            return new FluidStack(stack, resultAmount);
        }
        
        return FluidStack.EMPTY;
    }

    @NotNull
    public FluidStack insertFluidUnchecked(int startSlot, int endSlot, @NotNull FluidStack stack, FluidAction action)
    {
        FluidStack result = stack;
        
        for (int slot = startSlot; startSlot <= endSlot; slot++)
        {
            result = this.insertFluidUnchecked(slot, stack, action);
            if (result.isEmpty())
                break;
        }
        
        return result;
    }
    
    public List<FluidStack> insertAll(List<FluidStack> stacks)
    {
        if (stacks.isEmpty())
            return stacks;
        
        // we could optimize the initial capacity later
        List<FluidStack> remainingStacks = new ArrayList<FluidStack>();

        for (int stackIndex = 0; stackIndex < stacks.size(); stackIndex++)
        {
            FluidStack stack = stacks.get(stackIndex).copy();
            
            for (int index = 0; index < this.stacks.size(); index++)
            {
                stack = this.insertFluid(index, stack, FluidAction.EXECUTE);
            }
            
            if (!stack.isEmpty())
                remainingStacks.add(stack);
        }
        
        return remainingStacks;
    }
    
    public List<FluidStack> insertAllUnchecked(List<FluidStack> stacks)
    {
        if (stacks.isEmpty())
            return stacks;
        
        // we could optimize the initial capacity later
        List<FluidStack> remainingStacks = new ArrayList<FluidStack>();

        for (int stackIndex = 0; stackIndex < stacks.size(); stackIndex++)
        {
            FluidStack stack = stacks.get(stackIndex).copy();
            
            for (int index = 0; index < this.stacks.size(); index++)
            {
                stack = this.insertFluidUnchecked(index, stack, FluidAction.EXECUTE);
            }
            
            if (!stack.isEmpty())
                remainingStacks.add(stack);
        }
        
        return remainingStacks;
    }
    
    public List<FluidStack> insertAll(int startSlot, int endSlot, List<FluidStack> stacks)
    {
        if (endSlot < startSlot)
            return stacks;
        else if (stacks.isEmpty())
            return stacks;
        
        // we could optimize the initial capacity later
        List<FluidStack> remainingStacks = new ArrayList<FluidStack>();

        for (int stackIndex = 0; stackIndex < stacks.size(); stackIndex++)
        {
            FluidStack stack = stacks.get(stackIndex).copy();
            
            for (int index = startSlot; index <= endSlot; index++)
            {
                stack = this.insertFluid(index, stack, FluidAction.EXECUTE);
            }
            
            if (!stack.isEmpty())
                remainingStacks.add(stack);
        }
        
        return remainingStacks;
    }
    
    public List<FluidStack> insertAllUnchecked(int startSlot, int endSlot, List<FluidStack> stacks)
    {
        if (endSlot < startSlot)
            return stacks;
        else if (stacks.isEmpty())
            return stacks;
        
        // we could optimize the initial capacity later
        List<FluidStack> remainingStacks = new ArrayList<FluidStack>();

        for (int stackIndex = 0; stackIndex < stacks.size(); stackIndex++)
        {
            FluidStack stack = stacks.get(stackIndex).copy();
            
            for (int index = startSlot; index <= endSlot; index++)
            {
                stack = this.insertFluidUnchecked(index, stack, FluidAction.EXECUTE);
            }
            
            if (!stack.isEmpty())
                remainingStacks.add(stack);
        }
        
        return remainingStacks;
    }
    
    public FluidStack extractFluid(int amount, FluidAction action)
    {
        if (amount == 0)
            return FluidStack.EMPTY;
        
        FluidStack result = FluidStack.EMPTY;
        
        for (int index = 0; index < this.stacks.size(); index++)
        {
            if (result.isEmpty())
            {
                result = this.extractFluid(index, amount, action);
            }
            else if (StackUtils.canFluidStacksStack(result, this.stacks.get(index)))
            {
                result.grow(this.extractFluid(index, amount - result.getAmount(), action).getAmount());
            }
            
            if (result.getAmount() - amount <= 0)
                return result;
        }
        
        return result;
    }
    
    public FluidStack extractFluidUnchecked(int amount, FluidAction action)
    {
        if (amount == 0)
            return FluidStack.EMPTY;
        
        FluidStack result = FluidStack.EMPTY;
        
        for (int index = 0; index < this.stacks.size(); index++)
        {
            if (result.isEmpty())
            {
                result = this.extractFluidUnchecked(index, amount, action);
            }
            else if (StackUtils.canFluidStacksStack(result, this.stacks.get(index)))
            {
                result.grow(this.extractFluidUnchecked(index, amount - result.getAmount(), action).getAmount());
            }
            
            if (result.getAmount() - amount <= 0)
                return result;
        }
        
        return result;
    }
    
    public FluidStack extractFluid(@NotNull FluidStack stack, FluidAction action)
    {
        if (stack.isEmpty())
            return stack;
        
        FluidStack result = FluidStack.EMPTY;
        
        for (int index = 0; index < this.stacks.size(); index++)
        {
            if (result.isEmpty())
            {
                if (stack.isFluidEqual(this.stacks.get(index)))
                    result = this.extractFluid(index, stack.getAmount(), action);
            }
            else if (StackUtils.canFluidStacksStack(result, this.stacks.get(index)))
            {
                result.grow(this.extractFluid(index, stack.getAmount() - result.getAmount(), action).getAmount());
            }
            
            if (result.getAmount() - stack.getAmount() <= 0)
                return result;
        }
        
        return result;
    }
    
    public FluidStack extractFluidUnchecked(@NotNull FluidStack stack, FluidAction action)
    {
        if (stack.isEmpty())
            return stack;
        
        FluidStack result = FluidStack.EMPTY;
        
        for (int index = 0; index < this.stacks.size(); index++)
        {
            if (result.isEmpty())
            {
                if (stack.isFluidEqual(this.stacks.get(index)))
                    result = this.extractFluidUnchecked(index, stack.getAmount(), action);
            }
            else if (StackUtils.canFluidStacksStack(result, this.stacks.get(index)))
            {
                result.grow(this.extractFluidUnchecked(index, stack.getAmount() - result.getAmount(), action).getAmount());
            }
            
            if (result.getAmount() - stack.getAmount() <= 0)
                return result;
        }
        
        return result;
    }
    
    public FluidStack extractFluid(int slot, int amount, FluidAction action)
    {
        if (!this.canDrainFluidFromSlot(slot, amount))
            return FluidStack.EMPTY;
        
        return this.extractFluidUnchecked(slot, amount, action);
    }
    
    @NotNull
    public FluidStack extractFluidUnchecked(int slot, int amount, FluidAction action)
    {
        if (amount == 0)
            return FluidStack.EMPTY;

        this.validateSlotIndex(slot);

        FluidStack existing = this.stacks.get(slot);

        if (existing.isEmpty())
            return FluidStack.EMPTY;

        if (existing.getAmount() <= amount)
        {
            if (action.execute())
            {
                this.stacks.set(slot, FluidStack.EMPTY);
                this.onContentsChanged(slot);
                return existing;
            }
            else
            {
                return existing.copy();
            }
        }
        else
        {
            if (action.execute())
            {
                int newStackAmount = existing.getAmount() - amount;
                
                this.stacks.set(slot, newStackAmount == 0 ?
                        FluidStack.EMPTY :
                        new FluidStack(existing, newStackAmount));
                this.onContentsChanged(slot);
            }

            return new FluidStack(existing, amount);
        }
    }

    @Deprecated
    @Override
    @NotNull 
    public FluidStack drain(@NotNull FluidStack resource, FluidAction action)
    {
        return this.extractFluid(resource, action);
    }

    @Deprecated
    @Override
    @NotNull 
    public FluidStack drain(int maxDrain, FluidAction action)
    {
        return this.extractFluid(maxDrain, action);
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack)
    {
        return true;
    }
    
    public boolean canFillFluidInSlot(int slot, @NotNull FluidStack fluidStack)
    {
        return true;
    }
    
    public boolean canDrainFluidFromSlot(int slot, int amount)
    {
        return true;
    }
    
    @Override
    public CompoundTag serializeNBT()
    {
        ListTag nbtTagList = new ListTag();
        for (int i = 0; i < this.stacks.size(); i++)
        {
            if (!this.stacks.get(i).isEmpty())
            {
                CompoundTag fluidTag = new CompoundTag();
                fluidTag.putInt("Slot", i);
                this.stacks.get(i).writeToNBT(fluidTag);
                nbtTagList.add(fluidTag);
            }
        }
        CompoundTag nbt = new CompoundTag();
        nbt.put("Fluids", nbtTagList);
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
        
        ListTag tagList = nbt.getList("Fluids", Tag.TAG_COMPOUND);
        for (int i = 0; i < tagList.size(); i++)
        {
            CompoundTag itemTags = tagList.getCompound(i);
            int slot = itemTags.getInt("Slot");

            if (slot >= 0 && slot < this.stacks.size())
            {
                this.stacks.set(slot, FluidStack.loadFluidStackFromNBT(itemTags));
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
            this.stacks.set(i, FluidStack.EMPTY);
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
    
    public List<FluidStack> toList()
    {
        ArrayList<FluidStack> result = new ArrayList<FluidStack>(this.getTanks());
        for (int index = 0; index < this.getTanks(); index++)
        {
            result.set(index, this.getStackInSlot(index).copy());
        }
        return result;
    }

    protected void validateSlotIndex(int slot)
    {
        if (slot < 0 || slot >= this.stacks.size())
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + this.stacks.size() + ")");
    }

    protected void onLoad()
    {

    }

    protected void onContentsChanged(int slot)
    {

    }
    
    public class View extends FluidStackContainer
    {
        protected final int _slotOffset;

        protected View(int slot)
        {
            super(1);
            
            this.stacks.set(0, FluidStackContainer.this.stacks.get(slot).copy());
            this._slotOffset = slot;
        }
        
        protected View(int slotStart, int slotEnd)
        {
            super(slotEnd - slotStart + 1);

            for (int slotIndex = 0; (slotIndex + slotStart) <= slotEnd; slotIndex++)
            {
                this.stacks.set(slotIndex, FluidStackContainer.this.stacks.get(slotIndex + slotStart).copy());
            }
            
            this._slotOffset = slotStart;
        }
        
        @Override
        public final boolean dynamicSize()
        {
            return false;
        }
        
        @Override
        public final boolean canFillFluidInSlot(int slot, @NotNull FluidStack fluidStack)
        {
            return FluidStackContainer.this.canFillFluidInSlot(slot, fluidStack);
        }
        
        @Override
        public final boolean canDrainFluidFromSlot(int slot, int amount)
        {
            return FluidStackContainer.this.canDrainFluidFromSlot(slot, amount);
        }
    }
}
