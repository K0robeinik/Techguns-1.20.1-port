package techguns2.machine.charging_station;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import techguns2.machine.MachineOperation;

public final class ChargingStationOperation extends MachineOperation
{
    public final ItemStack inputItem;
    public final ItemStack resultItem;
    public final int neededEnergy;
    public int energyRemaining;
    
    public ChargingStationOperation(FriendlyByteBuf buf, boolean fromNetwork)
    {
        super(buf, fromNetwork);
        this.inputItem = buf.readItem();
        this.resultItem = buf.readItem();
        this.neededEnergy = buf.readInt();
        this.energyRemaining = buf.readInt();
    }
    
    public ChargingStationOperation(CompoundTag tag, boolean fromNetwork)
    {
        super(tag, fromNetwork);
        
        if (tag.contains(TAG_INPUT_ITEM, Tag.TAG_COMPOUND))
            this.inputItem = ItemStack.of(tag.getCompound(TAG_INPUT_ITEM));
        else
            this.inputItem = ItemStack.EMPTY;
        
        if (tag.contains(TAG_RESULT_ITEM, Tag.TAG_COMPOUND))
            this.resultItem = ItemStack.of(tag.getCompound(TAG_RESULT_ITEM));
        else
            this.resultItem = ItemStack.EMPTY;
        
        this.energyRemaining = tag.getInt(TAG_ENERGY_REMAINING);
        this.neededEnergy = tag.getInt(TAG_NEEDED_ENERGY);
    }
    
    public ChargingStationOperation(
            int neededEnergy,
            int energyRemaining,
            ItemStack inputItem,
            ItemStack resultItem)
    {
        super();
        this.neededEnergy = neededEnergy;
        this.energyRemaining = energyRemaining;
        this.inputItem = inputItem;
        this.resultItem = resultItem;
    }
    
    public ChargingStationOperation(
            int neededEnergy,
            int energyRemaining,
            ItemStack inputItem,
            ItemStack resultItem,
            boolean isPaused)
    {
        // add energyDrainPerTick - 1 to just round up the division
        
        super(isPaused);
        this.neededEnergy = neededEnergy;
        this.energyRemaining = energyRemaining;
        this.inputItem = inputItem;
        this.resultItem = resultItem;
    }
    
    @Override
    public final void dropContents(Level level, double x, double y, double z)
    {
        // TODO: support for semi-charged items
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
        
        if (!this.resultItem.isEmpty())
        {
            CompoundTag itemData = new CompoundTag();
            this.resultItem.save(itemData);
            tag.put(TAG_RESULT_ITEM, itemData);
        }
        
        tag.putInt(TAG_NEEDED_ENERGY, this.neededEnergy);
        tag.putInt(TAG_ENERGY_REMAINING, this.energyRemaining);
    }

    @Override
    public final void serialize(FriendlyByteBuf buf, boolean toNetwork)
    {
        super.serialize(buf, toNetwork);
        buf.writeItem(this.inputItem);
        buf.writeItem(this.resultItem);
        buf.writeInt(this.neededEnergy);
        buf.writeInt(this.energyRemaining);
    }
    
    private static final String TAG_INPUT_ITEM = "InputItem";
    private static final String TAG_RESULT_ITEM = "ResultItem";
    private static final String TAG_NEEDED_ENERGY = "NeededEnergy";
    private static final String TAG_ENERGY_REMAINING = "EnergyRemaining";

    @Override
    public final boolean isComplete()
    {
        return this.energyRemaining <= 0;
    }

    @Override
    public final float progress()
    {
        if (this.neededEnergy > 0)
            return (float)(this.neededEnergy - this.energyRemaining) / this.neededEnergy;
        else
            return 0;
    }

    @Override
    public final int progressScaled(int limit)
    {
        if (this.neededEnergy > 0)
            return ((this.neededEnergy - this.energyRemaining) * limit) / this.neededEnergy;
        else
            return 0;
    }
}
