package techguns2.item.power_hammer;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.TierSortingRegistry;

public final class PowerHammerItem extends Item
{
    private final IPowerHammerHead _head;
    private final float _baseDigSpeed;
    private final int _airCapacity;

    public PowerHammerItem(Properties properties, IPowerHammerHead head, float baseDigSpeed, int airCapacity)
    {
        super(properties);
        this._head = head;
        this._baseDigSpeed = baseDigSpeed;
        this._airCapacity = Math.max(airCapacity, 1);
    }
    
    public final int getRawAir(ItemStack itemStack)
    {
        CompoundTag tag = itemStack.getTag();
        if (tag == null || !tag.contains(AIR_TAG, Tag.TAG_INT))
            return 0;
        
        return tag.getInt(AIR_TAG);
    }
    
    public final int getAir(ItemStack itemStack)
    {
        return Math.min(Math.max(this.getRawAir(itemStack), 0), this.getAirCapacity(itemStack));
    }
    
    public final void setRawAir(ItemStack itemStack, int rawValue)
    {
        CompoundTag tag = itemStack.getOrCreateTag();
        tag.putInt(AIR_TAG, rawValue);
    }
    
    public final int setAir(ItemStack itemStack, int value)
    {
        int clampedValue = Math.min(Math.max(value, 0), this.getAirCapacity(itemStack));
        this.setRawAir(itemStack, clampedValue);
        return clampedValue;
    }
    
    public final int getAirCapacity(ItemStack itemStack)
    {
        CompoundTag tag = itemStack.getTag();
        if (tag == null)
            return this._airCapacity;
        
        if (tag.contains(AIR_CAPACITY_TAG, Tag.TAG_INT))
            return Math.max(tag.getInt(AIR_CAPACITY_TAG), 1);
        else
            return this._airCapacity;
    }

    @Override
    public final int getBarWidth(ItemStack itemStack)
    {
        int airCapacity = this.getAirCapacity(itemStack);
        int air = Math.max(this.getRawAir(itemStack), 0);
        
        return Math.round(13.0F - (float)(air * 13.0F / airCapacity));
    }

    @Override
    public final boolean isBarVisible(ItemStack itemStack)
    {
        return true;
    }

    @Override
    public final int getBarColor(ItemStack itemStack)
    {
        int airCapacity = this.getAirCapacity(itemStack);
        int air = Math.max(this.getRawAir(itemStack), 0);
        
        float f = Math.max(0.0F, (airCapacity - (float)air) / airCapacity);
        return Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public final boolean mineBlock(ItemStack itemStack, Level level, BlockState blockState, BlockPos blockPos,
            LivingEntity source)
    {
        if (!(source instanceof Player player) || player.getAbilities().instabuild)
            return true;

        this.consumeAir(itemStack);
        return true;
    }

    public final void consumeAir(ItemStack itemStack)
    {
        CompoundTag tag = itemStack.getOrCreateTag();

        int air = 0;
        if (tag.contains(AIR_TAG, Tag.TAG_INT))
        {
            air = tag.getInt(AIR_TAG);
        }

        air = Math.max(air - 1, 0);
        tag.putInt(AIR_TAG, air);
    }

    public final boolean hasAir(ItemStack itemStack)
    {
        CompoundTag tag = itemStack.getTag();
        if (tag == null)
            return false;

        if (!tag.contains(AIR_TAG, Tag.TAG_INT))
            return false;

        return tag.getInt(AIR_TAG) > 0;
    }

    @Override
    public final float getDestroySpeed(ItemStack itemStack, BlockState blockState)
    {
        if (!this.hasAir(itemStack))
            return 0.0F;

        if (blockState.is(BlockTags.MINEABLE_WITH_AXE) || blockState.is(BlockTags.MINEABLE_WITH_SHOVEL))
        {
            return this._baseDigSpeed * this._head.getDigSpeedMultiplier();
        } else
        {
            return 1F;
        }
    }

    @Override
    public final boolean isCorrectToolForDrops(BlockState blockState)
    {
        if (!blockState.is(BlockTags.MINEABLE_WITH_AXE) && !blockState.is(BlockTags.MINEABLE_WITH_SHOVEL))
            return false;

        Tier tier = this._head.getTier();

        if (TierSortingRegistry.isTierSorted(tier))
            return TierSortingRegistry.isCorrectTierForDrops(tier, blockState);

        @SuppressWarnings("deprecation")
        int level = tier.getLevel();
        if (level < 3 && blockState.is(BlockTags.NEEDS_DIAMOND_TOOL))
            return false;
        else if (level < 2 && blockState.is(BlockTags.NEEDS_IRON_TOOL))
            return false;
        else if (level < 1 && blockState.is(BlockTags.NEEDS_STONE_TOOL))
            return false;
        else
            return true;
    }

    private static final String AIR_TAG = "Air";
    private static final String AIR_CAPACITY_TAG = "AirCapacity";
}
