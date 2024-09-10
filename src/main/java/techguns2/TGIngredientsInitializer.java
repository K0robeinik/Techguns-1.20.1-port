package techguns2;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.eventbus.api.IEventBus;
import techguns2.item.ConsumableItemIngredientSerializer;

final class TGIngredientsInitializer implements TGInitializer
{

    protected TGIngredientsInitializer()
    { }

    @Override
    public final void setup(IEventBus eventBus)
    {
        CraftingHelper.register(new ResourceLocation(Techguns.MODID, "ammo_press"), ConsumableItemIngredientSerializer.INSTANCE);
    }
}
