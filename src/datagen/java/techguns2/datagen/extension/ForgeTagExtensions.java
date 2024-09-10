package techguns2.datagen.extension;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;

public final class ForgeTagExtensions
{
    private ForgeTagExtensions()
    { }
    
    public static class Fluids
    {
        private Fluids()
        { }

        public static final TagKey<Fluid> OIL = TagKey.create(ForgeRegistries.Keys.FLUIDS, new ResourceLocation("forge", "oil"));
    }
    
    public static class Items
    {
        private Items()
        { }
        
        // TODO: Check for the commonly used tag for specifically bio fuel
        public static final TagKey<Item> BIO_FUEL = TagKey.create(ForgeRegistries.Keys.ITEMS, new ResourceLocation("forge", "bio_fuel"));

        public static final TagKey<Item> WIRES_GOLD = TagKey.create(ForgeRegistries.Keys.ITEMS, new ResourceLocation("forge", "wires/gold"));
        public static final TagKey<Item> WIRES_COPPER = TagKey.create(ForgeRegistries.Keys.ITEMS, new ResourceLocation("forge", "wires/copper"));
        
        public static final TagKey<Item> DUST_COAL = TagKey.create(ForgeRegistries.Keys.ITEMS, new ResourceLocation("forge", "dusts/coal"));
        
        public static final TagKey<Item> INGOTS_STEEL = TagKey.create(ForgeRegistries.Keys.ITEMS, new ResourceLocation("forge", "ingots/steel"));
        public static final TagKey<Item> INGOTS_TIN = TagKey.create(ForgeRegistries.Keys.ITEMS, new ResourceLocation("forge", "ingots/tin"));
        public static final TagKey<Item> INGOTS_BRONZE = TagKey.create(ForgeRegistries.Keys.ITEMS, new ResourceLocation("forge", "ingots/bronze"));
        public static final TagKey<Item> INGOTS_LEAD = TagKey.create(ForgeRegistries.Keys.ITEMS, new ResourceLocation("forge", "ingots/lead"));
        public static final TagKey<Item> INGOTS_OBSIDIAN_STEEL = TagKey.create(ForgeRegistries.Keys.ITEMS, new ResourceLocation("forge", "ingots/obsidian_steel"));
        public static final TagKey<Item> INGOTS_TITANIUM = TagKey.create(ForgeRegistries.Keys.ITEMS, new ResourceLocation("forge", "ingots/titanium"));

        public static final TagKey<Item> NUGGETS_COPPER = TagKey.create(ForgeRegistries.Keys.ITEMS, new ResourceLocation("forge", "nuggets/copper"));
        public static final TagKey<Item> NUGGETS_STEEL = TagKey.create(ForgeRegistries.Keys.ITEMS, new ResourceLocation("forge", "nuggets/steel"));
        public static final TagKey<Item> NUGGETS_LEAD = TagKey.create(ForgeRegistries.Keys.ITEMS, new ResourceLocation("forge", "nuggets/lead"));
        
        public static final TagKey<Item> PLATES_IRON = TagKey.create(ForgeRegistries.Keys.ITEMS, new ResourceLocation("forge", "plates/iron"));
        public static final TagKey<Item> PLATES_COPPER = TagKey.create(ForgeRegistries.Keys.ITEMS, new ResourceLocation("forge", "plates/copper"));
        public static final TagKey<Item> PLATES_TIN = TagKey.create(ForgeRegistries.Keys.ITEMS, new ResourceLocation("forge", "plates/tin"));
        public static final TagKey<Item> PLATES_BRONZE = TagKey.create(ForgeRegistries.Keys.ITEMS, new ResourceLocation("forge", "plates/bronze"));
        public static final TagKey<Item> PLATES_STEEL = TagKey.create(ForgeRegistries.Keys.ITEMS, new ResourceLocation("forge", "plates/steel"));
        public static final TagKey<Item> PLATES_OBSIDIAN_STEEL = TagKey.create(ForgeRegistries.Keys.ITEMS, new ResourceLocation("forge", "plates/obsidian_steel"));
        public static final TagKey<Item> PLATES_LEAD = TagKey.create(ForgeRegistries.Keys.ITEMS, new ResourceLocation("forge", "plates/lead"));
        public static final TagKey<Item> PLATES_CARBON = TagKey.create(ForgeRegistries.Keys.ITEMS, new ResourceLocation("forge", "plates/carbon"));
        public static final TagKey<Item> PLATES_TITANIUM = TagKey.create(ForgeRegistries.Keys.ITEMS, new ResourceLocation("forge", "plates/titanium"));
        public static final TagKey<Item> PLATES_PLASTIC = TagKey.create(ForgeRegistries.Keys.ITEMS, new ResourceLocation("forge", "plates/plastic"));
        public static final TagKey<Item> PLATES_RUBBER = TagKey.create(ForgeRegistries.Keys.ITEMS, new ResourceLocation("forge", "plates/rubber"));
    }
}
