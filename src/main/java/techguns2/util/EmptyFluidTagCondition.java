package techguns2.util;

import com.google.gson.JsonObject;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import techguns2.Techguns;

public final class EmptyFluidTagCondition implements ICondition
{
    private static final ResourceLocation NAME = new ResourceLocation(Techguns.MODID, "fluid_tag_empty");
    private final TagKey<Fluid> tag;

    public EmptyFluidTagCondition(String location)
    {
        this(new ResourceLocation(location));
    }

    public EmptyFluidTagCondition(String namespace, String path)
    {
        this(new ResourceLocation(namespace, path));
    }

    public EmptyFluidTagCondition(ResourceLocation tag)
    {
        this.tag = TagKey.create(Registries.FLUID, tag);
    }

    @Override
    public ResourceLocation getID()
    {
        return NAME;
    }

    @Override
    public boolean test(ICondition.IContext context)
    {
        return context.getTag(this.tag).isEmpty();
    }

    @Override
    public String toString()
    {
        return "fluid_tag_empty(\"" + this.tag.location() + "\")";
    }
    
    public static final Serializer SERIALIZER = new Serializer();
    
    @Deprecated
    public static void register(RegisterEvent registerEvent)
    {
        registerEvent.register(ForgeRegistries.Keys.RECIPE_SERIALIZERS, helper -> {
            CraftingHelper.register(SERIALIZER);
        });
    }

    public static class Serializer implements IConditionSerializer<EmptyFluidTagCondition>
    {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public void write(JsonObject json, EmptyFluidTagCondition value)
        {
            json.addProperty("tag", value.tag.location().toString());
        }

        @Override
        public EmptyFluidTagCondition read(JsonObject json)
        {
            return new EmptyFluidTagCondition(new ResourceLocation(GsonHelper.getAsString(json, "tag")));
        }

        @Override
        public ResourceLocation getID()
        {
            return EmptyFluidTagCondition.NAME;
        }
    }
}
