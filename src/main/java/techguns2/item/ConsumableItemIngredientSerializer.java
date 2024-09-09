package techguns2.item;

import java.util.Arrays;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.registries.ForgeRegistries;

public class ConsumableItemIngredientSerializer implements IIngredientSerializer<ConsumableItemIngredient>
{
    private ConsumableItemIngredientSerializer()
    { }
    
    @Override
    public final ConsumableItemIngredient parse(FriendlyByteBuf buffer)
    {
        int size = buffer.readInt();
        if (size <= 0)
            return ConsumableItemIngredient.EMPTY;
        else
            return new ConsumableItemIngredient(Stream.generate(() -> new ConsumableItemIngredient.ItemValue(buffer.readItem())).limit(size));
    }
    
    public final ConsumableItemIngredient parse(JsonElement jsonElement)
    {
        if (jsonElement.isJsonNull())
            throw new JsonSyntaxException("Item cannot be null");
        
        if (jsonElement.isJsonObject())
            return this.parse(jsonElement.getAsJsonObject());
        
        if (jsonElement.isJsonArray())
        {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            if (jsonArray.size() == 0)
               throw new JsonSyntaxException("Item array cannot be empty, at least one item must be defined");
            
            return new ConsumableItemIngredient(StreamSupport.stream(jsonArray.spliterator(), false).map((p_289756_) -> {
                return this.parseValue(GsonHelper.convertToJsonObject(p_289756_, "item"));
            }));
        }
        
        throw new JsonSyntaxException("Expected item to be object or array of objects");
            
    }

    @Override
    public final ConsumableItemIngredient parse(JsonObject json)
    {
        return new ConsumableItemIngredient(Stream.of(this.parseValue(json)));
    }
    
    private final ConsumableItemIngredient.Value parseValue(JsonObject json)
    {
        if (json.has("item"))
        {
            if (json.has("tag"))
                throw new JsonParseException("An ingredient entry is either a tag or an item, not both");
            
            ItemStack item = CraftingHelper.getItemStack(json, true, true);
            return new ConsumableItemIngredient.ItemValue(item);
        }
        else if (json.has("tag"))
        {
            ResourceLocation tagLocation = new ResourceLocation(GsonHelper.getAsString(json, "tag"));
            TagKey<Item> tag = TagKey.create(ForgeRegistries.Keys.ITEMS, tagLocation);
            int count = GsonHelper.getAsInt(json, "count", 1);
            return new ConsumableItemIngredient.TagValue(tag, count);
        }
        else
        {
            throw new JsonParseException("An ingredient entry needs either a tag or an item");
        }
    }

    @Override
    public final void write(FriendlyByteBuf buffer, ConsumableItemIngredient ingredient)
    {
        buffer.writeCollection(Arrays.asList(ingredient.getItems()), FriendlyByteBuf::writeItem);
    }
    
    public static final ConsumableItemIngredientSerializer INSTANCE = new ConsumableItemIngredientSerializer();
}
