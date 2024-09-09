package techguns2.fluid;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

public final class FluidHelper
{
    private FluidHelper()
    { }
    
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();
    
    public static JsonElement toJson(FluidStack stack)
    {
        JsonObject json = new JsonObject();
        Fluid fluid = stack.getFluid();
        
        ResourceLocation fluidKey = ForgeRegistries.FLUIDS.getKey(fluid);
        
        if (fluidKey == null)
            throw new IllegalArgumentException("Could not get key of the fluid " + fluid);
        
        json.addProperty("fluid", fluidKey.toString());
        json.addProperty("amount", stack.getAmount());
        
        if (stack.hasTag())
        {
            json.addProperty("nbt", stack.getTag().toString());
        }
        
        return json;
    }
    
    public static FluidStack fromJson(JsonObject json)
    {
        ResourceLocation id = new ResourceLocation(GsonHelper.getAsString(json, "fluid"));
        Fluid fluid = ForgeRegistries.FLUIDS.getValue(id);
        if (fluid == null)
            throw new JsonSyntaxException("Unknown fluid '" + id + "'");
        
        int amount = GsonHelper.getAsInt(json, "amount");
        FluidStack stack = new FluidStack(fluid, amount);
        
        if (!json.has("nbt"))
            return stack;
        
        try
        {
            JsonElement element = json.get("nbt");
            
            CompoundTag nbt = TagParser.parseTag(
                    element.isJsonObject() ?
                            GSON.toJson(element) :
                            GsonHelper.convertToString(element, "nbt"));
            
            stack.setTag(nbt);
        }
        catch (CommandSyntaxException e)
        {
            e.printStackTrace();
        }
        
        return stack;
    }
}
