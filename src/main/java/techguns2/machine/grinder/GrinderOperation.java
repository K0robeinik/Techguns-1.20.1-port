package techguns2.machine.grinder;

import java.util.List;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import techguns2.machine.TickedMachineOperation;

public final class GrinderOperation extends TickedMachineOperation
{
    public final ItemStack inputItem;
    public final List<ItemStack> resultItems;
    
    public GrinderOperation(FriendlyByteBuf buf, boolean fromNetwork)
    {
        super(buf, fromNetwork);
        this.inputItem = buf.readItem();
        int count = Math.min(9, buf.readByte());
        this.resultItems = ImmutableList.copyOf(Stream.generate(() -> buf.readItem()).limit(count).iterator());
    }
    
    public GrinderOperation(CompoundTag tag, boolean fromNetwork)
    {
        super(tag, fromNetwork);
        
        if (tag.contains(TAG_INPUT_ITEM, Tag.TAG_COMPOUND))
            this.inputItem = ItemStack.of(tag.getCompound(TAG_INPUT_ITEM));
        else
            this.inputItem = ItemStack.EMPTY;
        
        if (tag.contains(TAG_RESULT_ITEMS, Tag.TAG_LIST))
        {
            ListTag resultItemsTag = tag.getList(TAG_RESULT_ITEMS, Tag.TAG_COMPOUND);
            int count = Math.min(resultItemsTag.size(), 9);
            
            ImmutableList.Builder<ItemStack> resultItems = ImmutableList.builder();
            
            for (int index = 0; index < count; index++)
            {
                resultItems.add(ItemStack.of(resultItemsTag.getCompound(index)));
            }
            
            this.resultItems = resultItems.build();
        }
        else
            this.resultItems = ImmutableList.of();
    }
    
    public GrinderOperation(
            int totalTicks,
            int ticksRemaining,
            int energyDrainPerTick,
            ItemStack inputItem,
            List<ItemStack> resultItems)
    {
        super(totalTicks, ticksRemaining, energyDrainPerTick);
        this.inputItem = inputItem;
        this.resultItems = ImmutableList.copyOf(resultItems);
    }
    
    public GrinderOperation(
            int totalTicks,
            int ticksRemaining,
            int energyDrainPerTick,
            ItemStack inputItem,
            List<ItemStack> resultItems,
            boolean isPaused)
    {
        super(totalTicks, ticksRemaining, energyDrainPerTick, isPaused);
        this.inputItem = inputItem;
        this.resultItems = ImmutableList.copyOf(resultItems);
    }
    
    @Override
    public final void dropContents(Level level, double x, double y, double z)
    {
        if (!this.inputItem.isEmpty())
            Containers.dropItemStack(level, x, y, z, this.inputItem);
    }

    @Override
    public final void serialize(CompoundTag tag, boolean toNetwork)
    {
        super.serialize(tag, toNetwork);
        if (!this.inputItem.isEmpty())
        {
            CompoundTag itemData = new CompoundTag();
            this.inputItem.save(itemData);
            tag.put(TAG_INPUT_ITEM, itemData);
        }
        
        if (!this.resultItems.isEmpty())
        {
            ListTag resultItemsTag = new ListTag();
            
            int count = Math.min(this.resultItems.size(), 9);
            
            for (ItemStack resultItem : this.resultItems)
            {
                CompoundTag itemData = new CompoundTag();
                resultItem.save(itemData);
                resultItemsTag.add(itemData);
                
                count--;
                
                if (count <= 0)
                    break;
            }
            
            tag.put(TAG_RESULT_ITEMS, resultItemsTag);
        }
    }

    @Override
    public final void serialize(FriendlyByteBuf buf, boolean toNetwork)
    {
        super.serialize(buf, toNetwork);
        buf.writeItem(this.inputItem);
        
        int count = Math.min(this.resultItems.size(), 9);
        
        for (ItemStack resultItem : this.resultItems)
        {
            buf.writeItem(resultItem);
            
            count--;
            if (count <= 0)
                break;
        }
    }
    
    private static final String TAG_INPUT_ITEM = "InputItem";
    private static final String TAG_RESULT_ITEMS = "ResultItems";
}
