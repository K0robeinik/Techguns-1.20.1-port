package techguns2.machine.alloy_furnace;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import techguns2.machine.TickedMachineOperation;

public final class AlloyFurnaceOperation extends TickedMachineOperation
{
    public final ItemStack primaryItem;
    public final ItemStack secondaryItem;
    public final ItemStack resultItem;
    
    public AlloyFurnaceOperation(FriendlyByteBuf buf, boolean fromNetwork)
    {
        super(buf, fromNetwork);
        this.primaryItem = buf.readItem();
        this.secondaryItem = buf.readItem();
        this.resultItem = buf.readItem();
    }
    
    public AlloyFurnaceOperation(CompoundTag tag, boolean fromNetwork)
    {
        super(tag, fromNetwork);
        
        if (tag.contains(TAG_PRIMARY_ITEM, Tag.TAG_COMPOUND))
            this.primaryItem = ItemStack.of(tag.getCompound(TAG_PRIMARY_ITEM));
        else
            this.primaryItem = ItemStack.EMPTY;
        
        if (tag.contains(TAG_SECONDARY_ITEM, Tag.TAG_COMPOUND))
            this.secondaryItem = ItemStack.of(tag.getCompound(TAG_SECONDARY_ITEM));
        else
            this.secondaryItem = ItemStack.EMPTY;
        
        if (tag.contains(TAG_RESULT_ITEM, Tag.TAG_COMPOUND))
            this.resultItem = ItemStack.of(tag.getCompound(TAG_RESULT_ITEM));
        else
            this.resultItem = ItemStack.EMPTY;
    }
    
    public AlloyFurnaceOperation(
            int totalTicks,
            int ticksRemaining,
            int energyDrainPerTick,
            ItemStack primaryItem,
            ItemStack secondaryItem,
            ItemStack resultItem)
    {
        super(totalTicks, ticksRemaining, energyDrainPerTick);
        this.primaryItem = primaryItem;
        this.secondaryItem = secondaryItem;
        this.resultItem = resultItem;
    }
    
    public AlloyFurnaceOperation(
            int totalTicks,
            int ticksRemaining,
            int energyDrainPerTick,
            ItemStack primaryItem,
            ItemStack secondaryItem,
            ItemStack resultItem,
            boolean isPaused)
    {
        super(totalTicks, ticksRemaining, energyDrainPerTick, isPaused);
        this.primaryItem = primaryItem;
        this.secondaryItem = secondaryItem;
        this.resultItem = resultItem;
    }
    
    @Override
    public final void dropContents(Level level, double x, double y, double z)
    {
        if (!this.primaryItem.isEmpty())
            Containers.dropItemStack(level, x, y, z, this.primaryItem);
        if (!this.secondaryItem.isEmpty())
            Containers.dropItemStack(level, x, y, z, this.secondaryItem);
    }

    @Override
    public final void serialize(CompoundTag tag, boolean toNetwork)
    {
        super.serialize(tag, toNetwork);
        if (!this.primaryItem.isEmpty())
        {
            CompoundTag itemData = new CompoundTag();
            this.primaryItem.save(itemData);
            tag.put(TAG_PRIMARY_ITEM, itemData);
        }
        
        if (!this.secondaryItem.isEmpty())
        {
            CompoundTag itemData = new CompoundTag();
            this.secondaryItem.save(itemData);
            tag.put(TAG_SECONDARY_ITEM, itemData);
        }
        
        if (!this.resultItem.isEmpty())
        {
            CompoundTag itemData = new CompoundTag();
            this.resultItem.save(itemData);
            tag.put(TAG_RESULT_ITEM, itemData);
        }
    }

    @Override
    public final void serialize(FriendlyByteBuf buf, boolean toNetwork)
    {
        super.serialize(buf, toNetwork);
        buf.writeItem(this.primaryItem);
        buf.writeItem(this.secondaryItem);
        buf.writeItem(this.resultItem);
    }
    
    private static final String TAG_PRIMARY_ITEM = "PrimaryItem";
    private static final String TAG_SECONDARY_ITEM = "SecondaryItem";
    private static final String TAG_RESULT_ITEM = "ResultItem";
}
