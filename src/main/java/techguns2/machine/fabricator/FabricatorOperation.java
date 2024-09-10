package techguns2.machine.fabricator;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import techguns2.machine.TickedMachineOperation;

public final class FabricatorOperation extends TickedMachineOperation
{
    public final ItemStack inputItem;
    public final ItemStack wireItem;
    public final ItemStack redstoneItem;
    public final ItemStack plateItem;
    public final ItemStack resultItem;
    
    public FabricatorOperation(FriendlyByteBuf buf, boolean fromNetwork)
    {
        super(buf, fromNetwork);
        this.inputItem = buf.readItem();
        this.wireItem = buf.readItem();
        this.redstoneItem = buf.readItem();
        this.plateItem = buf.readItem();
        this.resultItem = buf.readItem();
    }
    
    public FabricatorOperation(CompoundTag tag, boolean fromNetwork)
    {
        super(tag, fromNetwork);
        
        if (tag.contains(TAG_INPUT_ITEM, Tag.TAG_COMPOUND))
            this.inputItem = ItemStack.of(tag.getCompound(TAG_INPUT_ITEM));
        else
            this.inputItem = ItemStack.EMPTY;
        
        if (tag.contains(TAG_WIRE_ITEM, Tag.TAG_COMPOUND))
            this.wireItem = ItemStack.of(tag.getCompound(TAG_WIRE_ITEM));
        else
            this.wireItem = ItemStack.EMPTY;
        
        if (tag.contains(TAG_REDSTONE_ITEM, Tag.TAG_COMPOUND))
            this.redstoneItem = ItemStack.of(tag.getCompound(TAG_REDSTONE_ITEM));
        else
            this.redstoneItem = ItemStack.EMPTY;
        
        if (tag.contains(TAG_PLATE_ITEM, Tag.TAG_COMPOUND))
            this.plateItem = ItemStack.of(tag.getCompound(TAG_PLATE_ITEM));
        else
            this.plateItem = ItemStack.EMPTY;
        
        if (tag.contains(TAG_RESULT_ITEM, Tag.TAG_COMPOUND))
            this.resultItem = ItemStack.of(tag.getCompound(TAG_RESULT_ITEM));
        else
            this.resultItem = ItemStack.EMPTY;
    }
    
    public FabricatorOperation(
            int totalTicks,
            int ticksRemaining,
            int energyDrainPerTick,
            ItemStack inputItem,
            ItemStack wireItem,
            ItemStack redstoneItem,
            ItemStack plateItem,
            ItemStack resultItem)
    {
        super(totalTicks, ticksRemaining, energyDrainPerTick);
        this.inputItem = inputItem;
        this.wireItem = wireItem;
        this.redstoneItem = redstoneItem;
        this.plateItem = plateItem;
        this.resultItem = resultItem;
    }
    
    public FabricatorOperation(
            int totalTicks,
            int ticksRemaining,
            int energyDrainPerTick,
            ItemStack inputItem,
            ItemStack wireItem,
            ItemStack redstoneItem,
            ItemStack plateItem,
            ItemStack resultItem,
            boolean isPaused)
    {
        super(totalTicks, ticksRemaining, energyDrainPerTick, isPaused);
        this.inputItem = inputItem;
        this.wireItem = wireItem;
        this.redstoneItem = redstoneItem;
        this.plateItem = plateItem;
        this.resultItem = resultItem;
    }
    
    @Override
    public final void dropContents(Level level, double x, double y, double z)
    {
        if (!this.inputItem.isEmpty())
            Containers.dropItemStack(level, x, y, z, this.inputItem);
        if (!this.wireItem.isEmpty())
            Containers.dropItemStack(level, x, y, z, this.wireItem);
        if (!this.redstoneItem.isEmpty())
            Containers.dropItemStack(level, x, y, z, this.redstoneItem);
        if (!this.plateItem.isEmpty())
            Containers.dropItemStack(level, x, y, z, this.plateItem);
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
        
        if (!this.wireItem.isEmpty())
        {
            CompoundTag itemData = new CompoundTag();
            this.wireItem.save(itemData);
            tag.put(TAG_WIRE_ITEM, itemData);
        }
        
        if (!this.redstoneItem.isEmpty())
        {
            CompoundTag itemData = new CompoundTag();
            this.redstoneItem.save(itemData);
            tag.put(TAG_WIRE_ITEM, itemData);
        }
        
        if (!this.plateItem.isEmpty())
        {
            CompoundTag itemData = new CompoundTag();
            this.plateItem.save(itemData);
            tag.put(TAG_PLATE_ITEM, itemData);
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
        buf.writeItem(this.inputItem);
        buf.writeItem(this.wireItem);
        buf.writeItem(this.redstoneItem);
        buf.writeItem(this.plateItem);
        buf.writeItem(this.resultItem);
    }
    
    private static final String TAG_INPUT_ITEM = "InputItem";
    private static final String TAG_WIRE_ITEM = "WireItem";
    private static final String TAG_REDSTONE_ITEM = "RedstoneItem";
    private static final String TAG_PLATE_ITEM = "PlateItem";
    private static final String TAG_RESULT_ITEM = "ResultItem";
}
