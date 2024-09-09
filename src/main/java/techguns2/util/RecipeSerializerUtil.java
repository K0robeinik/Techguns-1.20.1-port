package techguns2.util;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

public final class RecipeSerializerUtil
{
    private RecipeSerializerUtil()
    { }
    
    public static class ItemStackResult
    {
        public final Item item;
        public final int count;
        @Nullable
        public final CompoundTag nbt;
        
        public ItemStackResult(Item item)
        {
            this.item = item;
            this.count = 1;
            this.nbt = null;
        }
        
        public ItemStackResult(Item item, @Nullable CompoundTag nbt)
        {
            this.item = item;
            this.count = 1;
            this.nbt = nbt;
        }
        
        public ItemStackResult(Item item, int count)
        {
            this.item = item;
            this.count = Math.min(count, 1);
            this.nbt = null;
        }
        
        public ItemStackResult(Item item, int count, @Nullable CompoundTag nbt)
        {
            this.item = item;
            this.count = Math.min(count, 1);
            this.nbt = nbt;
        }
    }
    
    public static class FluidStackResult
    {
        public final Fluid fluid;
        public final int amount;
        @Nullable
        public final CompoundTag nbt;
        
        public FluidStackResult(Fluid fluid)
        {
            this.fluid = fluid;
            this.amount = 1;
            this.nbt = null;
        }
        
        public FluidStackResult(Fluid fluid, @Nullable CompoundTag nbt)
        {
            this.fluid = fluid;
            this.amount = 1;
            this.nbt = nbt;
        }
        
        public FluidStackResult(Fluid fluid, int amount)
        {
            this.fluid = fluid;
            this.amount = Math.min(amount, 1);
            this.nbt = null;
        }
        
        public FluidStackResult(Fluid fluid, int amount, @Nullable CompoundTag nbt)
        {
            this.fluid = fluid;
            this.amount = Math.min(amount, 1);
            this.nbt = nbt;
        }
    }
    
    public static void writeFluid(FriendlyByteBuf buf, FluidStack fluidStack)
    {
        buf.writeFluidStack(fluidStack);
    }
    
    public static void writeFluid(JsonObject json, FluidStack fluidStack)
    {
        json.addProperty("id", ForgeRegistries.FLUIDS.getKey(fluidStack.getFluid()).toString());
        int amount = fluidStack.getAmount();
        if (amount > 1)
            json.addProperty("amount", amount);
        
        if (fluidStack.hasTag())
        {
            CompoundTag tag = fluidStack.getTag();
            if (tag != null && !tag.isEmpty())
            {
                json.addProperty("nbt", tag.getAsString());
            }
        }
    }
    
    public static void writeFluid(JsonObject json, FluidStackResult fluidStack)
    {
        json.addProperty("id", ForgeRegistries.FLUIDS.getKey(fluidStack.fluid).toString());
        int amount = fluidStack.amount;
        if (amount > 1)
            json.addProperty("amount", amount);

        CompoundTag tag = fluidStack.nbt;
        if (tag != null && !tag.isEmpty())
        {
            json.addProperty("nbt", tag.getAsString());
        }
    }
    
    public static void writeItem(FriendlyByteBuf buf, ItemStack itemStack)
    {
        buf.writeItemStack(itemStack, false);
    }
    
    public static void writeItem(JsonObject json, ItemStack itemStack)
    {
        json.addProperty("id", ForgeRegistries.ITEMS.getKey(itemStack.getItem()).toString());
        int count = itemStack.getCount();
        if (count > 1)
            json.addProperty("count", count);
        
        if (itemStack.hasTag())
        {
            CompoundTag tag = itemStack.getTag();
            if (tag != null && !tag.isEmpty())
            {
                json.addProperty("nbt", tag.getAsString());
            }
        }
    }
    
    public static void writeItem(JsonObject json, ItemStackResult itemStack)
    {
        json.addProperty("id", ForgeRegistries.ITEMS.getKey(itemStack.item).toString());
        int count = itemStack.count;
        if (count > 1)
            json.addProperty("count", count);

        CompoundTag tag = itemStack.nbt;
        if (tag != null && !tag.isEmpty())
        {
            json.addProperty("nbt", tag.getAsString());
        }
    }
    
    public static FluidStack readFluid(FriendlyByteBuf buf)
    {
        return buf.readFluidStack();
    }
    
    public static FluidStack readFluid(JsonObject json, boolean allowEmpty)
    {
        String fluidId = GsonHelper.getAsString(json, "id");
        Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluidId));
        if (fluid == null)
        {
            if (allowEmpty)
                return FluidStack.EMPTY;
            
            throw new JsonSyntaxException("Fluid of id '" + fluidId + "' could not be found!");
        }
        if (fluid == Fluids.EMPTY)
        {
            if (allowEmpty)
                return FluidStack.EMPTY;

            throw new JsonSyntaxException("Fluid cannot be empty");
        }
        
        int count = GsonHelper.getAsInt(json, "count", 1);
        
        FluidStack result = new FluidStack(fluid, count);

        if (json.has("nbt"))
        {
            CompoundTag nbt = CraftingHelper.getNBT(json.get("nbt"));
            
            if (nbt != null && !nbt.isEmpty())
                result.setTag(nbt);
        }
        
        return result;
    }
    
    public static ItemStack readItem(FriendlyByteBuf buf)
    {
        return buf.readItem();
    }
    
    public static ItemStack readItem(JsonObject json, boolean allowEmpty)
    {
        String itemId = GsonHelper.getAsString(json, "id");
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemId));
        if (item == null)
        {
            if (allowEmpty)
                return ItemStack.EMPTY;
            
            throw new JsonSyntaxException("Item of id '" + itemId + "' could not be found!");
        }
        if (item == Items.AIR)
        {
            if (allowEmpty)
                return ItemStack.EMPTY;

            throw new JsonSyntaxException("Item cannot be air");
        }
        
        int count = GsonHelper.getAsInt(json, "count", 1);
        
        ItemStack result = new ItemStack(item, count);

        if (json.has("nbt"))
        {
            CompoundTag nbt = CraftingHelper.getNBT(json.get("nbt"));
            
            if (nbt != null && !nbt.isEmpty())
                result.setTag(nbt);
        }
        
        return result;
    }
}
