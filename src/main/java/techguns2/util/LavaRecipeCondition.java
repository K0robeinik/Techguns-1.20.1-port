package techguns2.util;

import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import techguns2.TGConfig;
import techguns2.Techguns;

public final class LavaRecipeCondition implements ICondition
{
    private LavaRecipeCondition()
    { }

    @Override
    public final ResourceLocation getID()
    {
        return ID;
    }

    @Override
    public final boolean test(IContext context)
    {
        return TGConfig.General.keepLavaRecipesWhenFuelIsPresent;
    }
    
    private static final ResourceLocation ID = new ResourceLocation(Techguns.MODID, "lava_recipe_enabled");
    
    public static final LavaRecipeCondition INSTANCE = new LavaRecipeCondition();
    public static final Serializer SERIALIZER = new Serializer();
    
    @Deprecated
    public static void register(RegisterEvent registerEvent)
    {
        registerEvent.register(ForgeRegistries.Keys.RECIPE_SERIALIZERS, helper -> {
            CraftingHelper.register(SERIALIZER);
        });
    }
    
    public static final class Serializer implements IConditionSerializer<LavaRecipeCondition>
    {
        private Serializer()
        { }
        
        @Override
        public final void write(JsonObject json, LavaRecipeCondition value)
        { }

        @Override
        public final LavaRecipeCondition read(JsonObject json)
        {
            return INSTANCE;
        }

        @Override
        public final ResourceLocation getID()
        {
            return ID;
        }
        
    }
}
