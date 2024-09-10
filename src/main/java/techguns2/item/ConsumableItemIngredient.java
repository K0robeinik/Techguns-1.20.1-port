package techguns2.item;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import techguns2.util.IConsumableIngredient;

public final class ConsumableItemIngredient extends Ingredient implements IConsumableIngredient<ItemStack>
{
    public ConsumableItemIngredient(Stream<? extends ConsumableItemIngredient.Value> values)
    {
        super(values);
    }
    
    @Override
    public final IIngredientSerializer<? extends Ingredient> getSerializer()
    {
        return ConsumableItemIngredientSerializer.INSTANCE;
    }
    
    @Override
    public final int testWithConsumption(ItemStack itemStack)
    {
        if (itemStack == null)
            return -1;
        else if (this.isEmpty())
        {
            if (itemStack.isEmpty())
                return 0;
            else
                return -1;
        }
        
        for (ItemStack recipeItemStack : this.getItems())
        {
            if (recipeItemStack.is(itemStack.getItem()) && itemStack.getCount() >= recipeItemStack.getCount())
                return recipeItemStack.getCount();
        }
        
        return -1;
    }
    
    @Override
    public final boolean test(ItemStack itemStack)
    {
        if (itemStack == null)
            return false;
        if (this.isEmpty())
            return itemStack.isEmpty();
        
        for (ItemStack recipeItemStack : this.getItems())
        {
            if (recipeItemStack.is(itemStack.getItem()) && itemStack.getCount() >= recipeItemStack.getCount())
                return true;
        }
        
        return false;
    }

    public static final ConsumableItemIngredient EMPTY = new ConsumableItemIngredient(Stream.of());
    
    public static interface Value extends Ingredient.Value
    {
        
    }
    
    public static final class ItemValue implements ConsumableItemIngredient.Value
    {
        private final ItemStack _item;
        
        public ItemValue(ItemStack item)
        {
            this._item = item;
        }

        @Override
        public final Collection<ItemStack> getItems()
        {
            return Collections.singleton(this._item);
        }

        @Override
        public final JsonObject serialize()
        {
            JsonObject result = new JsonObject();
            result.addProperty("item", ForgeRegistries.ITEMS.getKey(this._item.getItem()).toString());
            if (this._item.getCount() > 1)
            {
                result.addProperty("count", this._item.getCount());
            }
            return result;
        }
    }
    
    public static final class TagValue implements ConsumableItemIngredient.Value
    {
        private final TagKey<Item> _tag;
        private final int _count;
        
        public TagValue(TagKey<Item> tag, int count)
        {
            this._tag = tag;
            this._count = count;
        }

        @Override
        public final Collection<ItemStack> getItems()
        {
            List<ItemStack> list = Lists.newArrayList();
            for (Item item : ForgeRegistries.ITEMS.tags().getTag(this._tag))
            {
                ItemStack itemStack = new ItemStack(item, this._count);
                list.add(itemStack);
            }
            
            if (list.isEmpty())
            {
                list.add(new ItemStack(net.minecraft.world.level.block.Blocks.BARRIER, this._count).setHoverName(net.minecraft.network.chat.Component.literal("Empty Tag: " + this._tag.location())));
            }
            return list;
        }

        @Override
        public final JsonObject serialize()
        {
            JsonObject result = new JsonObject();
            result.addProperty("tag", this._tag.toString());
            if (this._count > 1)
            {
                result.addProperty("count", this._count);
            }
            return result;
        }
    }
}
