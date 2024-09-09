package techguns2.machine.ammo_press;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import techguns2.machine.TickedMachineOperation;

public final class AmmoPressOperation extends TickedMachineOperation
{
    public final ItemStack bulletItem;
    public final ItemStack casingItem;
    public final ItemStack powderItem;
    public final ItemStack resultItem;
    
    public AmmoPressOperation(FriendlyByteBuf buf, boolean fromNetwork)
    {
        super(buf, fromNetwork);
        this.bulletItem = buf.readItem();
        this.casingItem = buf.readItem();
        this.powderItem = buf.readItem();
        this.resultItem = buf.readItem();
    }
    
    public AmmoPressOperation(CompoundTag tag, boolean fromNetwork)
    {
        super(tag, fromNetwork);
        
        if (tag.contains(TAG_BULLET_ITEM, Tag.TAG_COMPOUND))
            this.bulletItem = ItemStack.of(tag.getCompound(TAG_BULLET_ITEM));
        else
            this.bulletItem = ItemStack.EMPTY;
        
        if (tag.contains(TAG_CASING_ITEM, Tag.TAG_COMPOUND))
            this.casingItem = ItemStack.of(tag.getCompound(TAG_CASING_ITEM));
        else
            this.casingItem = ItemStack.EMPTY;
        
        if (tag.contains(TAG_POWDER_ITEM, Tag.TAG_COMPOUND))
            this.powderItem = ItemStack.of(tag.getCompound(TAG_POWDER_ITEM));
        else
            this.powderItem = ItemStack.EMPTY;
        
        if (tag.contains(TAG_RESULT_ITEM, Tag.TAG_COMPOUND))
            this.resultItem = ItemStack.of(tag.getCompound(TAG_RESULT_ITEM));
        else
            this.resultItem = ItemStack.EMPTY;
    }
    
    public AmmoPressOperation(
            int totalTicks,
            int ticksRemaining,
            int energyDrainPerTick,
            ItemStack bulletItem,
            ItemStack casingItem,
            ItemStack powderItem,
            ItemStack resultItem)
    {
        super(totalTicks, ticksRemaining, energyDrainPerTick);
        this.bulletItem = bulletItem;
        this.casingItem = casingItem;
        this.powderItem = powderItem;
        this.resultItem = resultItem;
    }
    
    public AmmoPressOperation(
            int totalTicks,
            int ticksRemaining,
            int energyDrainPerTick,
            ItemStack bulletItem,
            ItemStack casingItem,
            ItemStack powderItem,
            ItemStack resultItem,
            boolean isPaused)
    {
        super(totalTicks, ticksRemaining, energyDrainPerTick, isPaused);
        this.bulletItem = bulletItem;
        this.casingItem = casingItem;
        this.powderItem = powderItem;
        this.resultItem = resultItem;
    }
    
    @Override
    public final void dropContents(Level level, double x, double y, double z)
    {
        if (this.bulletItem.isEmpty())
            Containers.dropItemStack(level, x, y, z, this.bulletItem);
        if (this.casingItem.isEmpty())
            Containers.dropItemStack(level, x, y, z, this.casingItem);
        if (this.powderItem.isEmpty())
            Containers.dropItemStack(level, x, y, z, this.powderItem);
    }

    @Override
    public final void serialize(CompoundTag tag, boolean toNetwork)
    {
        super.serialize(tag, toNetwork);
        if (!this.bulletItem.isEmpty())
        {
            CompoundTag itemData = new CompoundTag();
            this.bulletItem.save(itemData);
            tag.put(TAG_BULLET_ITEM, itemData);
        }
        
        if (!this.casingItem.isEmpty())
        {
            CompoundTag itemData = new CompoundTag();
            this.casingItem.save(itemData);
            tag.put(TAG_CASING_ITEM, itemData);
        }
        
        if (!this.powderItem.isEmpty())
        {
            CompoundTag itemData = new CompoundTag();
            this.powderItem.save(itemData);
            tag.put(TAG_POWDER_ITEM, itemData);
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
        buf.writeItem(this.bulletItem);
        buf.writeItem(this.casingItem);
        buf.writeItem(this.powderItem);
        buf.writeItem(this.resultItem);
    }
    
    private static final String TAG_BULLET_ITEM = "BulletItem";
    private static final String TAG_CASING_ITEM = "CasingItem";
    private static final String TAG_POWDER_ITEM = "PowderItem";
    private static final String TAG_RESULT_ITEM = "ResultItem";
}
