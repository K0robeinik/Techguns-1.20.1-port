package techguns2;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.eventbus.api.IEventBus;

public final class TGTags implements TGInitializer
{
    protected TGTags()
    {
    }
    
    @Override
    public final void setup(IEventBus eventBus)
    {
        Items.init();
        Fluids.init();
    }
    
    public static final class Fluids
    {
        private Fluids()
        { }
        
        public static final TagKey<Fluid> LIQUID_REDSTONE = FluidTags.create(
                new ResourceLocation(Techguns.MODID, "liquid_redstone"));
        public static final TagKey<Fluid> LIQUID_ENDER = FluidTags.create(
                new ResourceLocation(Techguns.MODID, "liquid_ender"));
        public static final TagKey<Fluid> OIL = FluidTags.create(
                new ResourceLocation(Techguns.MODID, "oil"));
        public static final TagKey<Fluid> FUEL = FluidTags.create(
                new ResourceLocation(Techguns.MODID, "fuel"));
        public static final TagKey<Fluid> ACID = FluidTags.create(
                new ResourceLocation(Techguns.MODID, "acid"));
        
        private static void init()
        { }
    }
    
    public static final class Items
    {
        private Items()
        { }
        
        public static final TagKey<Item> ELECTRUM_INGOT = ItemTags.create(
                new ResourceLocation(Techguns.MODID, "ingots/electrum"));
        
        private static void init()
        {
            Ammo.init();
            AmmoVariants.init();
        }
        
        public static final class Ammo
        {
            private Ammo()
            { }
            
            public static final TagKey<Item> STONE_BULLETS = ItemTags.create(
                    new ResourceLocation(Techguns.MODID, "ammo/stone_bullets"));
            
            public static final TagKey<Item> PISTOL_BULLETS = ItemTags.create(
                    new ResourceLocation(Techguns.MODID, "ammo/pistol_bullets"));
            
            private static void init()
            { }
        }
        
        public static final class AmmoVariants
        {
            private AmmoVariants()
            { }
            
            public static final TagKey<Item> INCENDIARY = ItemTags.create(
                    new ResourceLocation(Techguns.MODID, "ammo_variant/incendiary"));
            
            private static void init()
            { }
        }
        
    }
}
