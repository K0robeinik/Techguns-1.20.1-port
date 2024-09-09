package techguns2;

import java.util.List;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableList;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import techguns2.ammo.IAmmo;
import techguns2.item.AmmoItem;

public final class TGItems implements TGInitializer
{
    protected TGItems()
    { }
    
    private static final DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, Techguns.MODID);

    public static final List<RegistryObject<? extends Item>> ALL_ITEMS;
    public static final List<RegistryObject<? extends Item>> BASE_ITEMS;
    public static final List<RegistryObject<? extends Item>> AMMO_ITEMS;
    public static final List<RegistryObject<? extends Item>> PART_ITEMS;
    public static final List<RegistryObject<? extends Item>> MATERIAL_ITEMS;
    public static final List<RegistryObject<? extends Item>> UPGRADE_ITEMS;
    public static final List<RegistryObject<? extends Item>> EQUIPMENT_ITEMS;
    public static final List<RegistryObject<? extends Item>> ARMOR_ITEMS;
    public static final List<RegistryObject<? extends Item>> GUN_ITEMS;
    
    public static final RegistryObject<Item> IRON_MECHANICAL_PARTS;
    public static final RegistryObject<Item> OBSIDIAN_STEEL_MECHANICAL_PARTS;
    public static final RegistryObject<Item> CARBON_MECHANICAL_PARTS;
    
    public static final RegistryObject<Item> IRON_RECEIVER;
    public static final RegistryObject<Item> STEEL_RECEIVER;
    public static final RegistryObject<Item> OBSIDIAN_STEEL_RECEIVER;
    public static final RegistryObject<Item> CARBON_RECEIVER;
    public static final RegistryObject<Item> TITANIUM_RECEIVER;
    
    public static final RegistryObject<Item> WOOD_STOCK;
    public static final RegistryObject<Item> PLASTIC_STOCK;
    public static final RegistryObject<Item> CARBON_STOCK;
    
    public static final RegistryObject<Item> STONE_BARREL;
    public static final RegistryObject<Item> IRON_BARREL;
    public static final RegistryObject<Item> OBSIDIAN_STEEL_BARREL;
    public static final RegistryObject<Item> CARBON_BARREL;
    public static final RegistryObject<Item> LASER_BARREL;
    public static final RegistryObject<Item> GAUSS_RIFLE_BARREL;
    public static final RegistryObject<Item> SHIELDED_TITANIUM_BARREL;
    
    public static final RegistryObject<Item> OBSIDIAN_STEEL_MINING_DRILL_HEAD;
    public static final RegistryObject<Item> CARBON_MINING_DRILL_HEAD;
    
    public static final RegistryObject<Item> OBSIDIAN_STEEL_POWER_HAMMER_HEAD;
    public static final RegistryObject<Item> CARBON_POWER_HAMMER_HEAD;
    
    public static final RegistryObject<Item> OBSIDIAN_STEEL_CHAINSAW_BLADE;
    public static final RegistryObject<Item> CARBON_CHAINSAW_BLADE;
    
    public static final RegistryObject<Item> SMALL_STEEL_ORE_DRILL;
    public static final RegistryObject<Item> SMALL_OBSIDIAN_STEEL_ORE_DRILL;
    public static final RegistryObject<Item> SMALL_CARBON_ORE_DRILL;
    
    public static final RegistryObject<Item> MEDIUM_STEEL_ORE_DRILL;
    public static final RegistryObject<Item> MEDIUM_OBSIDIAN_STEEL_ORE_DRILL;
    public static final RegistryObject<Item> MEDIUM_CARBON_ORE_DRILL;
    
    public static final RegistryObject<Item> LARGE_STEEL_ORE_DRILL;
    public static final RegistryObject<Item> LARGE_OBSIDIAN_STEEL_ORE_DRILL;
    public static final RegistryObject<Item> LARGE_CARBON_ORE_DRILL;
    
    public static final RegistryObject<Item> GLIDER_BACKPACK;
    public static final RegistryObject<Item> GLIDER_WING;
    
    public static final RegistryObject<Item> ANTI_GRAVITY_CORE;
    public static final RegistryObject<Item> PLASMA_GENERATOR;
    
    public static final RegistryObject<Item> STEAM_ARMOR_PLATE;
    public static final RegistryObject<Item> POWER_ARMOR_PLATE;
    
    public static final RegistryObject<Item> REACTION_CHAMBER_HEAT_RAY_FOCUS;
    public static final RegistryObject<Item> REACTION_CHAMBER_UV_EMITTER;

    public static final RegistryObject<Item> COPPER_NUGGET;
    public static final RegistryObject<Item> COPPER_WIRE;
    public static final RegistryObject<Item> COPPER_PLATE;
    public static final RegistryObject<Item> RAW_TIN;
    public static final RegistryObject<Item> TIN_INGOT;
    public static final RegistryObject<Item> TIN_PLATE;
    public static final RegistryObject<Item> BRONZE_INGOT;
    public static final RegistryObject<Item> BRONZE_PLATE;
    public static final RegistryObject<Item> RAW_LEAD;
    public static final RegistryObject<Item> LEAD_INGOT;
    public static final RegistryObject<Item> LEAD_NUGGET;
    public static final RegistryObject<Item> LEAD_PLATE;
    public static final RegistryObject<Item> IRON_PLATE;
    public static final RegistryObject<Item> STEEL_INGOT;
    public static final RegistryObject<Item> STEEL_NUGGET;
    public static final RegistryObject<Item> STEEL_PLATE;
    public static final RegistryObject<Item> GOLD_WIRE;
    public static final RegistryObject<Item> OBSIDIAN_STEEL_INGOT;
    public static final RegistryObject<Item> OBSIDIAN_STEEL_PLATE;
    public static final RegistryObject<Item> CARBON_FIBERS;
    public static final RegistryObject<Item> CARBON_PLATE;
    public static final RegistryObject<Item> RAW_TITANIUM;
    public static final RegistryObject<Item> RAW_TITANIUM_CHUNK;
    public static final RegistryObject<Item> TITANIUM_INGOT;
    public static final RegistryObject<Item> TITANIUM_PLATE;
    public static final RegistryObject<Item> RAW_PLASTIC;
    public static final RegistryObject<Item> PLASTIC_PLATE;
    public static final RegistryObject<Item> RAW_RUBBER;
    public static final RegistryObject<Item> RUBBER_PLATE;
    public static final RegistryObject<Item> RAW_URANIUM;
    public static final RegistryObject<Item> YELLOWCAKE;
    public static final RegistryObject<Item> ENRICHED_URANIUM;
    public static final RegistryObject<Item> QUARTZ_ROD;
    public static final RegistryObject<Item> HEAVY_CLOTH;
    public static final RegistryObject<Item> PROTECTIVE_FIBER;
    public static final RegistryObject<Item> TREATED_LEATHER;
    public static final RegistryObject<Item> BIOMASS;
    public static final RegistryObject<Item> CIRCUIT_BOARD;
    public static final RegistryObject<Item> ELITE_CIRCUIT_BOARD;
    public static final RegistryObject<Item> COIL;
    public static final RegistryObject<Item> CYBERNETIC_PARTS;
    public static final RegistryObject<Item> ELECTRIC_ENGINE;
    public static final RegistryObject<Item> LASER_FOCUS;
    public static final RegistryObject<Item> PUMP_MECHANISM;
    public static final RegistryObject<Item> RAD_EMITTER;
    public static final RegistryObject<Item> SONIC_EMITTER;
    public static final RegistryObject<Item> INFUSION_BAG;
    public static final RegistryObject<Item> TGX;
    public static final RegistryObject<Item> NUCLEAR_WARHEAD;
    
    public static final RegistryObject<AmmoItem<TGAmmo.StoneBullets>> STONE_BULLETS;
    
    public static final RegistryObject<AmmoItem<TGAmmo.PistolRounds>> PISTOL_ROUNDS;
    public static final RegistryObject<AmmoItem<TGAmmo.PistolRounds>> INCENDIARY_PISTOL_ROUNDS;
    
    public static final RegistryObject<AmmoItem<TGAmmo.ShotgunShells>> SHOTGUN_SHELLS;
    public static final RegistryObject<AmmoItem<TGAmmo.ShotgunShells>> INCENDIARY_SHOTGUN_SHELLS;
    
    public static final RegistryObject<AmmoItem<TGAmmo.RifleRounds>> RIFLE_ROUNDS;
    public static final RegistryObject<AmmoItem<TGAmmo.RifleRounds>> INCENDIARY_RIFLE_ROUNDS;
    
    public static final RegistryObject<AmmoItem<TGAmmo.RifleRoundClip<TGAmmo.RifleRounds>>> RIFLE_ROUND_CLIP;
    public static final RegistryObject<AmmoItem<TGAmmo.RifleRoundClip<TGAmmo.RifleRounds>>> INCENDIARY_RIFLE_ROUND_CLIP;
    
    public static final RegistryObject<AmmoItem<TGAmmo.SniperRounds>> SNIPER_ROUNDS;
    public static final RegistryObject<AmmoItem<TGAmmo.SniperRounds>> INCENDIARY_SNIPER_ROUNDS;
    public static final RegistryObject<AmmoItem<TGAmmo.SniperRounds>> EXPLOSIVE_SNIPER_ROUNDS;

    public static final RegistryObject<AmmoItem<TGAmmo.Rocket>> ROCKET;
    public static final RegistryObject<AmmoItem<TGAmmo.Rocket>> HIGH_VELOCITY_ROCKET;
    public static final RegistryObject<AmmoItem<TGAmmo.Rocket>> NUCLEAR_ROCKET;
    
    public static final RegistryObject<AmmoItem<TGAmmo.AdvancedRounds>> ADVANCED_ROUNDS;
    
    public static final RegistryObject<AmmoItem<TGAmmo.NetherCharge>> NETHER_CHARGE;
    
    public static final RegistryObject<AmmoItem<TGAmmo.GaussRifleSlugRound>> GAUSS_RIFLE_SLUGS;

    public static final RegistryObject<AmmoItem<TGAmmo.PistolRoundMagazine<TGAmmo.PistolRounds>>> PISTOL_MAGAZINE;
    public static final RegistryObject<AmmoItem<TGAmmo.PistolRoundMagazine<TGAmmo.PistolRounds>>> INCENDIARY_PISTOL_MAGAZINE;
    
    public static final RegistryObject<AmmoItem<TGAmmo.SmgRoundMagazine<TGAmmo.PistolRounds>>> SMG_MAGAZINE;
    public static final RegistryObject<AmmoItem<TGAmmo.SmgRoundMagazine<TGAmmo.PistolRounds>>> INCENDIARY_SMG_MAGAZINE;

    public static final RegistryObject<AmmoItem<TGAmmo.AssaultRifleRoundMagazine<TGAmmo.RifleRounds>>> ASSAULT_RIFLE_MAGAZINE;
    public static final RegistryObject<AmmoItem<TGAmmo.AssaultRifleRoundMagazine<TGAmmo.RifleRounds>>> INCENDIARY_ASSAULT_RIFLE_MAGAZINE;
    
    public static final RegistryObject<AmmoItem<TGAmmo.LmgRoundMagazine<TGAmmo.RifleRounds>>> LMG_MAGAZINE;
    public static final RegistryObject<AmmoItem<TGAmmo.LmgRoundMagazine<TGAmmo.RifleRounds>>> INCENDIARY_LMG_MAGAZINE;
    
    public static final RegistryObject<AmmoItem<TGAmmo.MinigunRoundMagazine<TGAmmo.RifleRounds>>> MINIGUN_MAGAZINE;
    public static final RegistryObject<AmmoItem<TGAmmo.MinigunRoundMagazine<TGAmmo.RifleRounds>>> INCENDIARY_MINIGUN_MAGAZINE;
    
    public static final RegistryObject<AmmoItem<TGAmmo.As50RoundMagazine<TGAmmo.SniperRounds>>> AS50_MAGAZINE;
    public static final RegistryObject<AmmoItem<TGAmmo.As50RoundMagazine<TGAmmo.SniperRounds>>> INCENDIARY_AS50_MAGAZINE;
    public static final RegistryObject<AmmoItem<TGAmmo.As50RoundMagazine<TGAmmo.SniperRounds>>> EXPLOSIVE_AS50_MAGAZINE;
    
    public static final RegistryObject<AmmoItem<TGAmmo.AdvancedRoundMagazine<TGAmmo.AdvancedRounds>>> ADVANCED_MAGAZINE;
    
    public static final RegistryObject<AmmoItem<TGAmmo.Grenade40MM>> GRENADE_40MM;
    
    public static final RegistryObject<Item> COMPRESSED_AIR_TANK;

    public static final RegistryObject<Item> BIO_TANK;
    
    public static final RegistryObject<Item> FUEL_TANK;
    
    public static final RegistryObject<Item> REDSTONE_BATTERY;
    
    public static final RegistryObject<Item> ENERGY_CELL;

    public static final RegistryObject<Item> NUCLEAR_POWER_CELL;

    public static final RegistryObject<Item> GAS_MASK_FILTER;
    
    public static final RegistryObject<Item> EMPTY_PISTOL_MAGAZINE;
    public static final RegistryObject<Item> EMPTY_SMG_MAGAZINE;
    public static final RegistryObject<Item> EMPTY_ASSAULT_RIFLE_MAGAZINE;
    public static final RegistryObject<Item> EMPTY_LMG_MAGAZINE;
    public static final RegistryObject<Item> EMPTY_MINIGUN_MAGAZINE;
    public static final RegistryObject<Item> EMPTY_AS50_MAGAZINE;
    public static final RegistryObject<Item> EMPTY_ADVANCED_MAGAZINE;
    public static final RegistryObject<Item> EMPTY_COMPRESSED_AIR_TANK;
    public static final RegistryObject<Item> EMPTY_BIO_TANK;
    public static final RegistryObject<Item> EMPTY_FUEL_TANK;
    public static final RegistryObject<Item> DEPLETED_REDSTONE_BATTERY;
    public static final RegistryObject<Item> DEPLETED_ENERGY_CELL;
    public static final RegistryObject<Item> DEPLETED_NUCLEAR_POWER_CELL;
    
    public static final RegistryObject<Item> STACK_UPGRADE;
    public static final RegistryObject<Item> IRON_TURRET_ARMOR;
    public static final RegistryObject<Item> STEEL_TURRET_ARMOR;
    public static final RegistryObject<Item> OBSIDIAN_STEEL_TURRET_ARMOR;
    public static final RegistryObject<Item> CARBON_TURRET_ARMOR;
    
    public static final RegistryObject<Item> PROTECTION_1_ARMOR_UPGRADE;
    public static final RegistryObject<Item> PROTECTION_2_ARMOR_UPGRADE;
    public static final RegistryObject<Item> PROTECTION_3_ARMOR_UPGRADE;
    public static final RegistryObject<Item> PROTECTION_4_ARMOR_UPGRADE;
    public static final RegistryObject<Item> PROJECTILE_PROTECTION_1_ARMOR_UPGRADE;
    public static final RegistryObject<Item> PROJECTILE_PROTECTION_2_ARMOR_UPGRADE;
    public static final RegistryObject<Item> PROJECTILE_PROTECTION_3_ARMOR_UPGRADE;
    public static final RegistryObject<Item> PROJECTILE_PROTECTION_4_ARMOR_UPGRADE;
    public static final RegistryObject<Item> BLAST_PROTECTION_1_ARMOR_UPGRADE;
    public static final RegistryObject<Item> BLAST_PROTECTION_2_ARMOR_UPGRADE;
    public static final RegistryObject<Item> BLAST_PROTECTION_3_ARMOR_UPGRADE;
    public static final RegistryObject<Item> BLAST_PROTECTION_4_ARMOR_UPGRADE;
    
    public static final RegistryObject<Item> WORKING_GLOVES;
    public static final RegistryObject<Item> GAS_MASK;
    public static final RegistryObject<Item> GLIDER;
    public static final RegistryObject<Item> JUMP_PACK;
    public static final RegistryObject<Item> OXYGEN_TANKS;
    public static final RegistryObject<Item> NIGHT_VISION_GOGGLES;
    public static final RegistryObject<Item> JETPACK;
    public static final RegistryObject<Item> OXYGEN_MASK;
    public static final RegistryObject<Item> TACTICAL_MASK;
    public static final RegistryObject<Item> ANTI_GRAVITY_DEVICE;
    public static final RegistryObject<Item> COMBAT_KNIFE;
    public static final RegistryObject<Item> CROWBAR;
    public static final RegistryObject<Item> RIOT_SHIELD;
    public static final RegistryObject<Item> BALLISTIC_SHIELD;
    public static final RegistryObject<Item> ADVANCED_SHIELD;
    public static final RegistryObject<Item> STICK_GRENADE;
    public static final RegistryObject<Item> FRAG_GRENADE;
    public static final RegistryObject<Item> POWER_HAMMER;
    public static final RegistryObject<Item> CHAINSAW;
    public static final RegistryObject<Item> MINING_DRILL;
    public static final RegistryObject<Item> RAD_AWAY;
    public static final RegistryObject<Item> ANTI_RAD_PILLS;

    public static final RegistryObject<Item> SOLDIER_HELMET;
    public static final RegistryObject<Item> SOLDIER_CHESTPLATE;
    public static final RegistryObject<Item> SOLDIER_LEGGINGS;
    public static final RegistryObject<Item> SOLDIER_BOOTS;
    
    public static final RegistryObject<Item> BANDIT_HELMET;
    public static final RegistryObject<Item> BANDIT_CHESTPLATE;
    public static final RegistryObject<Item> BANDIT_LEGGINGS;
    public static final RegistryObject<Item> BANDIT_BOOTS;
    
    public static final RegistryObject<Item> MINER_HELMET;
    public static final RegistryObject<Item> MINER_CHESTPLATE;
    public static final RegistryObject<Item> MINER_LEGGINGS;
    public static final RegistryObject<Item> MINER_BOOTS;
    
    public static final RegistryObject<Item> STEAM_HELMET;
    public static final RegistryObject<Item> STEAM_CHESTPLATE;
    public static final RegistryObject<Item> STEAM_LEGGINGS;
    public static final RegistryObject<Item> STEAM_BOOTS;
    
    public static final RegistryObject<Item> HAZMAT_HELMET;
    public static final RegistryObject<Item> HAZMAT_CHESTPLATE;
    public static final RegistryObject<Item> HAZMAT_LEGGINGS;
    public static final RegistryObject<Item> HAZMAT_BOOTS;
    
    public static final RegistryObject<Item> COMBAT_HELMET;
    public static final RegistryObject<Item> COMBAT_CHESTPLATE;
    public static final RegistryObject<Item> COMBAT_LEGGINGS;
    public static final RegistryObject<Item> COMBAT_BOOTS;
    
    public static final RegistryObject<Item> COMMADO_HELMET;
    public static final RegistryObject<Item> COMMADO_CHESTPLATE;
    public static final RegistryObject<Item> COMMADO_LEGGINGS;
    public static final RegistryObject<Item> COMMADO_BOOTS;
    
    public static final RegistryObject<Item> RANGER_VETERAN_HELMET;
    public static final RegistryObject<Item> RANGER_VETERAN_CHESTPLATE;
    public static final RegistryObject<Item> RANGER_VETERAN_LEGGINGS;
    public static final RegistryObject<Item> RANGER_VETERAN_BOOTS;
    
    public static final RegistryObject<Item> ADVANCED_COMBAT_HELMET;
    public static final RegistryObject<Item> ADVANCED_COMBAT_CHESTPLATE;
    public static final RegistryObject<Item> ADVANCED_COMBAT_LEGGINGS;
    public static final RegistryObject<Item> ADVANCED_COMBAT_BOOTS;
    
    public static final RegistryObject<Item> POWER_HELMET;
    public static final RegistryObject<Item> POWER_CHESTPLATE;
    public static final RegistryObject<Item> POWER_LEGGINGS;
    public static final RegistryObject<Item> POWER_BOOTS;
    
    public static final RegistryObject<Item> HEV_HELMET;
    public static final RegistryObject<Item> HEV_CHESTPLATE;
    public static final RegistryObject<Item> HEV_LEGGINGS;
    public static final RegistryObject<Item> HEV_BOOTS;
    
    public static final RegistryObject<Item> EXO_HELMET;
    public static final RegistryObject<Item> EXO_CHESTPLATE;
    public static final RegistryObject<Item> EXO_LEGGINGS;
    public static final RegistryObject<Item> EXO_BOOTS;
    
    public static final RegistryObject<Item> BERET;
    
    public static final RegistryObject<Item> NETHER_COMBAT_HELMET;
    public static final RegistryObject<Item> NETHER_COMBAT_CHESTPLATE;
    public static final RegistryObject<Item> NETHER_COMBAT_LEGGINGS;
    public static final RegistryObject<Item> NETHER_COMBAT_BOOTS;
    
    public static final RegistryObject<Item> MK2_POWER_HELMET;
    public static final RegistryObject<Item> MK2_POWER_CHESTPLATE;
    public static final RegistryObject<Item> MK2_POWER_LEGGINGS;
    public static final RegistryObject<Item> MK2_POWER_BOOTS;
    
    public static final RegistryObject<Item> HAND_CANNON;
    public static final RegistryObject<Item> DOUBLE_BARREL_SHOTGUN;
    public static final RegistryObject<Item> REVOLVER;
    public static final RegistryObject<Item> GOLDEN_REVOLVER;
    public static final RegistryObject<Item> THOMPSON_SMG;
    public static final RegistryObject<Item> AKM;
    public static final RegistryObject<Item> BOLT_ACTION_RIFLE;
    public static final RegistryObject<Item> M4;
    public static final RegistryObject<Item> INFILTRATOR;
    public static final RegistryObject<Item> PISTOL;
    public static final RegistryObject<Item> COMBAT_SHOTGUN;
    public static final RegistryObject<Item> MAC10;
    public static final RegistryObject<Item> FLAMETHROWER;
    public static final RegistryObject<Item> ROCKET_LAUNCHER;
    public static final RegistryObject<Item> GRIM_REAPER;
    public static final RegistryObject<Item> GRENADE_LAUNCHER;
    public static final RegistryObject<Item> AUG;
    public static final RegistryObject<Item> NETHER_BLASTER;
    public static final RegistryObject<Item> BIO_GUN;
    public static final RegistryObject<Item> TESLA_GUN;
    public static final RegistryObject<Item> LIGHT_MACHINE_GUN;
    public static final RegistryObject<Item> MINIGUN;
    public static final RegistryObject<Item> AS50;
    public static final RegistryObject<Item> VECTOR;
    public static final RegistryObject<Item> SCAR;
    public static final RegistryObject<Item> LASER_RIFLE;
    public static final RegistryObject<Item> BLASTER_RIFLE;
    public static final RegistryObject<Item> BLASTER_SHOTGUN;
    public static final RegistryObject<Item> SONIC_RIFLE;
    public static final RegistryObject<Item> PDW;
    public static final RegistryObject<Item> PULSE_RIFLE;
    public static final RegistryObject<Item> DEATOMIZER;
    public static final RegistryObject<Item> ALIEN_BLASTER;
    public static final RegistryObject<Item> NUCLEAR_DEATH_RAY;
    public static final RegistryObject<Item> GAUSS_RIFLE;
    public static final RegistryObject<Item> GUIDED_ROCKET_LAUNCHER;
    public static final RegistryObject<Item> TFG;
    public static final RegistryObject<Item> LASER_PISTOL;
    
    @Override
    public final void setup(IEventBus eventBus)
    {
        REGISTER.register(eventBus);
    }
    
    static
    {
        ImmutableList.Builder<RegistryObject<? extends Item>> baseItems = ImmutableList.builder();
        ImmutableList.Builder<RegistryObject<? extends Item>> ammoItems = ImmutableList.builder();
        ImmutableList.Builder<RegistryObject<? extends Item>> partItems = ImmutableList.builder();
        ImmutableList.Builder<RegistryObject<? extends Item>> materialItems = ImmutableList.builder();
        ImmutableList.Builder<RegistryObject<? extends Item>> upgradeItems = ImmutableList.builder();
        ImmutableList.Builder<RegistryObject<? extends Item>> equipmentItems = ImmutableList.builder();
        ImmutableList.Builder<RegistryObject<? extends Item>> armorItems = ImmutableList.builder();
        ImmutableList.Builder<RegistryObject<? extends Item>> gunItems = ImmutableList.builder();
        
        IRON_MECHANICAL_PARTS = registerGeneric("iron_mechanical_parts", partItems);
        OBSIDIAN_STEEL_MECHANICAL_PARTS = registerGeneric("obsidian_steel_mechanical_parts", partItems);
        CARBON_MECHANICAL_PARTS = registerGeneric("carbon_mechanical_parts", partItems);
        
        IRON_RECEIVER = registerGeneric("iron_receiver", partItems);
        STEEL_RECEIVER = registerGeneric("steel_receiver", partItems);
        OBSIDIAN_STEEL_RECEIVER = registerGeneric("obsidian_steel_receiver", partItems);
        CARBON_RECEIVER = registerGeneric("carbon_receiver", partItems);
        TITANIUM_RECEIVER = registerGeneric("titanium_receiver", partItems);
        
        WOOD_STOCK = registerGeneric("wood_stock", partItems);
        PLASTIC_STOCK = registerGeneric("plastic_stock", partItems);
        CARBON_STOCK = registerGeneric("carbon_stock", partItems);
        
        STONE_BARREL = registerGeneric("stone_barrel", partItems);
        IRON_BARREL = registerGeneric("iron_barrel", partItems);
        OBSIDIAN_STEEL_BARREL = registerGeneric("obsidian_steel_barrel", partItems);
        CARBON_BARREL = registerGeneric("carbon_barrel", partItems);
        LASER_BARREL = registerGeneric("laser_barrel", partItems);
        GAUSS_RIFLE_BARREL = registerGeneric("gauss_rifle_barrel", partItems);
        SHIELDED_TITANIUM_BARREL = registerGeneric("shielded_titanium_barrel", partItems);
        
        OBSIDIAN_STEEL_MINING_DRILL_HEAD = registerGeneric("obsidian_steel_mining_drill_head", partItems);
        CARBON_MINING_DRILL_HEAD = registerGeneric("carbon_mining_drill_head", partItems);
        
        OBSIDIAN_STEEL_POWER_HAMMER_HEAD = registerGeneric("obsidian_steel_power_hammer_head", partItems);
        CARBON_POWER_HAMMER_HEAD = registerGeneric("carbon_power_hammer_head", partItems);
        
        OBSIDIAN_STEEL_CHAINSAW_BLADE = registerGeneric("obsidian_steel_chainsaw_blade", partItems);
        CARBON_CHAINSAW_BLADE = registerGeneric("carbon_power_chainsaw_blade", partItems);
        
        SMALL_STEEL_ORE_DRILL = registerGeneric("small_steel_ore_drill", partItems);
        SMALL_OBSIDIAN_STEEL_ORE_DRILL = registerGeneric("small_obsidian_steel_ore_drill", partItems);
        SMALL_CARBON_ORE_DRILL = registerGeneric("small_carbon_ore_drill", partItems);
        
        MEDIUM_STEEL_ORE_DRILL = registerGeneric("medium_steel_ore_drill", partItems);
        MEDIUM_OBSIDIAN_STEEL_ORE_DRILL = registerGeneric("medium_obsidian_steel_ore_drill", partItems);
        MEDIUM_CARBON_ORE_DRILL = registerGeneric("medium_carbon_ore_drill", partItems);
        
        LARGE_STEEL_ORE_DRILL = registerGeneric("large_steel_ore_drill", partItems);
        LARGE_OBSIDIAN_STEEL_ORE_DRILL = registerGeneric("large_obsidian_steel_ore_drill", partItems);
        LARGE_CARBON_ORE_DRILL = registerGeneric("large_carbon_ore_drill", partItems);
        
        GLIDER_BACKPACK = registerGeneric("glider_backpack", partItems);
        GLIDER_WING = registerGeneric("glider_wing", partItems);
        
        ANTI_GRAVITY_CORE = registerGeneric("anti_gravity_core", partItems);
        PLASMA_GENERATOR = registerGeneric("plasma_generator", partItems);
        
        STEAM_ARMOR_PLATE = registerGeneric("steam_armor_plate", partItems);
        POWER_ARMOR_PLATE = registerGeneric("power_armor_plate", partItems);
        
        REACTION_CHAMBER_HEAT_RAY_FOCUS = registerGeneric("reaction_chamber_heat_ray_focus", partItems);
        REACTION_CHAMBER_UV_EMITTER = registerGeneric("reaction_chamber_uv_emitter", partItems);

        COPPER_NUGGET = registerGeneric("copper_nugget", materialItems);
        COPPER_WIRE = registerGeneric("copper_wire", materialItems);
        COPPER_PLATE = registerGeneric("copper_plate", materialItems);
        RAW_TIN = registerGeneric("raw_tin", materialItems);
        TIN_INGOT = registerGeneric("tin_ingot", materialItems);
        TIN_PLATE = registerGeneric("tin_plate", materialItems);
        BRONZE_INGOT = registerGeneric("bronze_ingot", materialItems);
        BRONZE_PLATE = registerGeneric("bronze_plate", materialItems);
        RAW_LEAD = registerGeneric("raw_lead", materialItems);
        LEAD_INGOT = registerGeneric("lead_ingot", materialItems);
        LEAD_NUGGET = registerGeneric("lead_nugget", materialItems);
        LEAD_PLATE = registerGeneric("lead_plate", materialItems);
        IRON_PLATE = registerGeneric("iron_plate", materialItems);
        STEEL_INGOT = registerGeneric("steel_ingot", materialItems);
        STEEL_NUGGET = registerGeneric("steel_nugget", materialItems);
        STEEL_PLATE = registerGeneric("steel_plate", materialItems);
        GOLD_WIRE = registerGeneric("gold_wire", materialItems);
        OBSIDIAN_STEEL_INGOT = registerGeneric("obsidian_steel_ingot", materialItems);
        OBSIDIAN_STEEL_PLATE = registerGeneric("obsidian_steel_plate", materialItems);
        CARBON_FIBERS = registerGeneric("carbon_fibers", materialItems);
        CARBON_PLATE = registerGeneric("carbon_plate", materialItems);
        RAW_TITANIUM = registerGeneric("raw_titanium", materialItems);
        RAW_TITANIUM_CHUNK = registerGeneric("raw_titanium_chunk", materialItems);
        TITANIUM_INGOT = registerGeneric("titanium_ingot", materialItems);
        TITANIUM_PLATE = registerGeneric("titanium_plate", materialItems);
        RAW_PLASTIC = registerGeneric("raw_plastic", materialItems);
        PLASTIC_PLATE = registerGeneric("plastic_plate", materialItems);
        RAW_RUBBER = registerGeneric("raw_rubber", materialItems);
        RUBBER_PLATE = registerGeneric("rubber_plate", materialItems);
        RAW_URANIUM = registerGeneric("raw_uranium", materialItems);
        YELLOWCAKE = registerGeneric("yellowcake", materialItems);
        ENRICHED_URANIUM = registerGeneric("enriched_uranium", materialItems);
        QUARTZ_ROD = registerGeneric("quartz_rod", materialItems);
        HEAVY_CLOTH = registerGeneric("heavy_cloth", materialItems);
        PROTECTIVE_FIBER = registerGeneric("protective_fiber", materialItems);
        TREATED_LEATHER = registerGeneric("treated_leather", materialItems);
        BIOMASS = registerGeneric("biomass", materialItems);
        CIRCUIT_BOARD = registerGeneric("circuit_board", materialItems);
        ELITE_CIRCUIT_BOARD = registerGeneric("elite_circuit_board", materialItems);
        COIL = registerGeneric("coil", materialItems);
        CYBERNETIC_PARTS = registerGeneric("cybernetic_parts", materialItems);
        ELECTRIC_ENGINE = registerGeneric("electric_engine", materialItems);
        LASER_FOCUS = registerGeneric("laser_focus", materialItems);
        PUMP_MECHANISM = registerGeneric("pump_mechanism", materialItems);
        RAD_EMITTER = registerGeneric("rad_emitter", materialItems);
        SONIC_EMITTER = registerGeneric("sonic_emitter", materialItems);
        INFUSION_BAG = registerGeneric("infusion_bag", materialItems);
        TGX = registerGeneric("tgx", materialItems);
        NUCLEAR_WARHEAD = registerGeneric("nuclear_warhead", materialItems);
        
        STONE_BULLETS = registerGenericAmmo("stone_bullets", TGAmmo.STONE_BULLETS, ammoItems);
        
        PISTOL_ROUNDS = registerGenericAmmo("pistol_rounds", TGAmmo.PISTOL_ROUNDS, ammoItems);
        INCENDIARY_PISTOL_ROUNDS = registerGenericAmmo("incendiary_pistol_rounds", TGAmmo.INCENDIARY_PISTOL_ROUNDS, ammoItems);
        
        SHOTGUN_SHELLS = registerGenericAmmo("shotgun_shells", TGAmmo.SHOTGUN_SHELLS, ammoItems);
        INCENDIARY_SHOTGUN_SHELLS = registerGenericAmmo("incendiary_shotgun_shells", TGAmmo.INCENDIARY_SHOTGUN_SHELLS, ammoItems);
        
        RIFLE_ROUNDS = registerGenericAmmo("rifle_rounds", TGAmmo.RIFLE_ROUNDS, ammoItems);
        INCENDIARY_RIFLE_ROUNDS = registerGenericAmmo("incendiary_rifle_rounds", TGAmmo.INCENDIARY_RIFLE_ROUNDS, ammoItems);
        
        RIFLE_ROUND_CLIP = registerGenericAmmo("rifle_round_clip", TGAmmo.RIFLE_ROUND_CLIP, ammoItems);
        INCENDIARY_RIFLE_ROUND_CLIP = registerGenericAmmo("incendiary_rifle_round_clip", TGAmmo.INCENDIARY_RIFLE_ROUND_CLIP, ammoItems);
        
        SNIPER_ROUNDS = registerGenericAmmo("sniper_rounds", TGAmmo.SNIPER_ROUNDS, ammoItems);
        INCENDIARY_SNIPER_ROUNDS = registerGenericAmmo("incendiary_sniper_rounds", TGAmmo.INCENDIARY_SNIPER_ROUNDS, ammoItems);
        EXPLOSIVE_SNIPER_ROUNDS = registerGenericAmmo("explosive_sniper_rounds", TGAmmo.EXPLOSIVE_SNIPER_ROUNDS, ammoItems);

        ROCKET = registerGenericAmmo("rocket", TGAmmo.ROCKET, ammoItems);
        HIGH_VELOCITY_ROCKET = registerGenericAmmo("high_velocity_rocket", TGAmmo.HIGH_VELOCITY_ROCKET, ammoItems);
        NUCLEAR_ROCKET = registerGenericAmmo("nuclear_rocket", TGAmmo.NUCLEAR_ROCKET, ammoItems);
        
        ADVANCED_ROUNDS = registerGenericAmmo("advanced_rounds", TGAmmo.ADVANCED_ROUNDS, ammoItems);
        
        NETHER_CHARGE = registerGenericAmmo("nether_charge", TGAmmo.NETHER_CHARGE, ammoItems);
        
        GAUSS_RIFLE_SLUGS = registerGenericAmmo("gauss_rifle_slugs", TGAmmo.GAUSS_RIFLE_SLUGS, ammoItems);

        PISTOL_MAGAZINE = registerGenericAmmo("pistol_magazine", TGAmmo.PISTOL_ROUND_MAGAZINE, ammoItems);
        INCENDIARY_PISTOL_MAGAZINE = registerGenericAmmo("incendiary_pistol_magazine", TGAmmo.INCENDIARY_PISTOL_ROUND_MAGAZINE, ammoItems);
        
        SMG_MAGAZINE = registerGenericAmmo("smg_magazine", TGAmmo.SMG_ROUND_MAGAZINE, ammoItems);
        INCENDIARY_SMG_MAGAZINE = registerGenericAmmo("incendiary_smg_magazine", TGAmmo.INCENDIARY_SMG_ROUND_MAGAZINE, ammoItems);

        ASSAULT_RIFLE_MAGAZINE = registerGenericAmmo("assault_rifle_magazine", TGAmmo.ASSAULT_RIFLE_ROUND_MAGAZINE, ammoItems);
        INCENDIARY_ASSAULT_RIFLE_MAGAZINE = registerGenericAmmo("incendiary_assault_rifle_magazine", TGAmmo.INCENDIARY_ASSAULT_RIFLE_ROUND_MAGAZINE, ammoItems);
        
        LMG_MAGAZINE = registerGenericAmmo("lmg_magazine", TGAmmo.LMG_ROUND_MAGAZINE, ammoItems);
        INCENDIARY_LMG_MAGAZINE = registerGenericAmmo("incendiary_lmg_magazine", TGAmmo.INCENDIARY_LMG_ROUND_MAGAZINE, ammoItems);
        
        MINIGUN_MAGAZINE = registerGenericAmmo("minigun_magazine", TGAmmo.MINIGUN_ROUND_MAGAZINE, ammoItems);
        INCENDIARY_MINIGUN_MAGAZINE = registerGenericAmmo("incendiary_minigun_magazine", TGAmmo.INCENDIARY_MINIGUN_ROUND_MAGAZINE, ammoItems);
        
        AS50_MAGAZINE = registerGenericAmmo("as50_magazine", TGAmmo.AS50_ROUND_MAGAZINE, ammoItems);
        INCENDIARY_AS50_MAGAZINE = registerGenericAmmo("incendiary_as50_magazine", TGAmmo.INCENDIARY_AS50_ROUND_MAGAZINE, ammoItems);
        EXPLOSIVE_AS50_MAGAZINE = registerGenericAmmo("explosive_as50_magazine", TGAmmo.EXPLOSIVE_AS50_ROUND_MAGAZINE, ammoItems);
        
        ADVANCED_MAGAZINE = registerGenericAmmo("advanced_magazine", TGAmmo.ADVANCED_ROUND_MAGAZINE, ammoItems);
        
        GRENADE_40MM = registerGenericAmmo("grenade_40mm", TGAmmo.GRENADE_40MM, ammoItems);
        
        COMPRESSED_AIR_TANK = registerGeneric("compressed_air_tank", ammoItems);

        BIO_TANK = registerGeneric("bio_tank", ammoItems);
        
        FUEL_TANK = registerGeneric("fuel_tank", ammoItems);
        
        REDSTONE_BATTERY = registerGeneric("redstone_battery", ammoItems);
        
        ENERGY_CELL = registerGeneric("energy_cell", ammoItems);

        NUCLEAR_POWER_CELL = registerGeneric("nuclear_power_cell", ammoItems);

        GAS_MASK_FILTER = registerGeneric("gas_mask_filter", ammoItems);
        
        EMPTY_PISTOL_MAGAZINE = registerGeneric("empty_pistol_magazine", ammoItems);
        EMPTY_SMG_MAGAZINE = registerGeneric("empty_smg_magazine", ammoItems);
        EMPTY_ASSAULT_RIFLE_MAGAZINE = registerGeneric("empty_assault_rifle_magazine", ammoItems);
        EMPTY_LMG_MAGAZINE = registerGeneric("empty_lmg_magazine", ammoItems);
        EMPTY_MINIGUN_MAGAZINE = registerGeneric("empty_minigun_magazine", ammoItems);
        EMPTY_AS50_MAGAZINE = registerGeneric("empty_as50_magazine", ammoItems);
        EMPTY_ADVANCED_MAGAZINE = registerGeneric("empty_advanced_magazine", ammoItems);
        EMPTY_COMPRESSED_AIR_TANK = registerGeneric("empty_compressed_air_tank", ammoItems);
        EMPTY_BIO_TANK = registerGeneric("empty_bio_tank", ammoItems);
        EMPTY_FUEL_TANK = registerGeneric("empty_fuel_tank", ammoItems);
        DEPLETED_REDSTONE_BATTERY = registerGeneric("depleted_redstone_battery", ammoItems);
        DEPLETED_ENERGY_CELL = registerGeneric("depleted_energy_cell", ammoItems);
        DEPLETED_NUCLEAR_POWER_CELL = registerGeneric("depleted_nuclear_power_cell", ammoItems);
        
        STACK_UPGRADE = registerGeneric("stack_upgrade", upgradeItems);
        IRON_TURRET_ARMOR = registerGeneric("iron_turret_armor", upgradeItems);
        STEEL_TURRET_ARMOR = registerGeneric("steel_turret_armor", upgradeItems);
        OBSIDIAN_STEEL_TURRET_ARMOR = registerGeneric("obsidian_steel_turret_armor", upgradeItems);
        CARBON_TURRET_ARMOR = registerGeneric("carbon_turret_armor", upgradeItems);
        
        PROTECTION_1_ARMOR_UPGRADE = registerGeneric("protection_1_armor_upgrade", upgradeItems);
        PROTECTION_2_ARMOR_UPGRADE = registerGeneric("protection_2_armor_upgrade", upgradeItems);
        PROTECTION_3_ARMOR_UPGRADE = registerGeneric("protection_3_armor_upgrade", upgradeItems);
        PROTECTION_4_ARMOR_UPGRADE = registerGeneric("protection_4_armor_upgrade", upgradeItems);
        PROJECTILE_PROTECTION_1_ARMOR_UPGRADE = registerGeneric("projectile_protection_1_armor_upgrade", upgradeItems);
        PROJECTILE_PROTECTION_2_ARMOR_UPGRADE = registerGeneric("projectile_protection_2_armor_upgrade", upgradeItems);
        PROJECTILE_PROTECTION_3_ARMOR_UPGRADE = registerGeneric("projectile_protection_3_armor_upgrade", upgradeItems);
        PROJECTILE_PROTECTION_4_ARMOR_UPGRADE = registerGeneric("projectile_protection_4_armor_upgrade", upgradeItems);
        BLAST_PROTECTION_1_ARMOR_UPGRADE = registerGeneric("blast_protection_1_armor_upgrade", upgradeItems);
        BLAST_PROTECTION_2_ARMOR_UPGRADE = registerGeneric("blast_protection_2_armor_upgrade", upgradeItems);
        BLAST_PROTECTION_3_ARMOR_UPGRADE = registerGeneric("blast_protection_3_armor_upgrade", upgradeItems);
        BLAST_PROTECTION_4_ARMOR_UPGRADE = registerGeneric("blast_protection_4_armor_upgrade", upgradeItems);
        
        WORKING_GLOVES = registerGeneric("working_gloves", equipmentItems);
        GAS_MASK = registerGeneric("gas_mask", equipmentItems);
        GLIDER = registerGeneric("glider", equipmentItems);
        JUMP_PACK = registerGeneric("jump_pack", equipmentItems);
        OXYGEN_TANKS = registerGeneric("oxygen_tanks", equipmentItems);
        NIGHT_VISION_GOGGLES = registerGeneric("night_vision_goggles", equipmentItems);
        JETPACK = registerGeneric("jetpack", equipmentItems);
        OXYGEN_MASK = registerGeneric("oxygen_mask", equipmentItems);
        TACTICAL_MASK = registerGeneric("tactical_mask", equipmentItems);
        ANTI_GRAVITY_DEVICE = registerGeneric("anti_gravity_device", equipmentItems);
        COMBAT_KNIFE = registerGeneric("combat_knife", equipmentItems);
        CROWBAR = registerGeneric("crowbar", equipmentItems);
        RIOT_SHIELD = registerGeneric("riot_shield", equipmentItems);
        BALLISTIC_SHIELD = registerGeneric("ballistic_shield", equipmentItems);
        ADVANCED_SHIELD = registerGeneric("advanced_shield", equipmentItems);
        STICK_GRENADE = registerGeneric("stick_grenade", equipmentItems);
        FRAG_GRENADE = registerGeneric("frag_grenade", equipmentItems);
        POWER_HAMMER = registerGeneric("power_hammer", equipmentItems);
        CHAINSAW = registerGeneric("chainsaw", equipmentItems);
        MINING_DRILL = registerGeneric("mining_drill", equipmentItems);
        RAD_AWAY = registerGeneric("rad_away", equipmentItems);
        ANTI_RAD_PILLS = registerGeneric("anti_rad_pills", equipmentItems);

        SOLDIER_HELMET = registerGeneric("soldier_helmet", armorItems);
        SOLDIER_CHESTPLATE = registerGeneric("soldier_chestplate", armorItems);
        SOLDIER_LEGGINGS = registerGeneric("soldier_leggings", armorItems);
        SOLDIER_BOOTS = registerGeneric("soldier_boots", armorItems);
        
        BANDIT_HELMET = registerGeneric("bandit_helmet", armorItems);
        BANDIT_CHESTPLATE = registerGeneric("bandit_chestplate", armorItems);
        BANDIT_LEGGINGS = registerGeneric("bandit_leggings", armorItems);
        BANDIT_BOOTS = registerGeneric("bandit_boots", armorItems);
        
        MINER_HELMET = registerGeneric("miner_helmet", armorItems);
        MINER_CHESTPLATE = registerGeneric("miner_chestplate", armorItems);
        MINER_LEGGINGS = registerGeneric("miner_leggings", armorItems);
        MINER_BOOTS = registerGeneric("miner_boots", armorItems);
        
        STEAM_HELMET = registerGeneric("steam_helmet", armorItems);
        STEAM_CHESTPLATE = registerGeneric("steam_chestplate", armorItems);
        STEAM_LEGGINGS = registerGeneric("steam_leggings", armorItems);
        STEAM_BOOTS = registerGeneric("steam_boots", armorItems);
        
        HAZMAT_HELMET = registerGeneric("hazmat_helmet", armorItems);
        HAZMAT_CHESTPLATE = registerGeneric("hazmat_chestplate", armorItems);
        HAZMAT_LEGGINGS = registerGeneric("hazmat_leggings", armorItems);
        HAZMAT_BOOTS = registerGeneric("hazmat_boots", armorItems);
        
        COMBAT_HELMET = registerGeneric("combat_helmet", armorItems);
        COMBAT_CHESTPLATE = registerGeneric("combat_chestplate", armorItems);
        COMBAT_LEGGINGS = registerGeneric("combat_leggings", armorItems);
        COMBAT_BOOTS = registerGeneric("combat_boots", armorItems);
        
        COMMADO_HELMET = registerGeneric("cammando_helmet", armorItems);
        COMMADO_CHESTPLATE = registerGeneric("cammando_chestplate", armorItems);
        COMMADO_LEGGINGS = registerGeneric("cammando_leggings", armorItems);
        COMMADO_BOOTS = registerGeneric("cammando_boots", armorItems);
        
        RANGER_VETERAN_HELMET = registerGeneric("ranger_veteran_helmet", armorItems);
        RANGER_VETERAN_CHESTPLATE = registerGeneric("ranger_veteran_chestplate", armorItems);
        RANGER_VETERAN_LEGGINGS = registerGeneric("ranger_veteran_leggings", armorItems);
        RANGER_VETERAN_BOOTS = registerGeneric("ranger_veteran_boots", armorItems);
        
        ADVANCED_COMBAT_HELMET = registerGeneric("advanced_combat_helmet", armorItems);
        ADVANCED_COMBAT_CHESTPLATE = registerGeneric("advanced_combat_chestplate", armorItems);
        ADVANCED_COMBAT_LEGGINGS = registerGeneric("advanced_combat_leggings", armorItems);
        ADVANCED_COMBAT_BOOTS = registerGeneric("advanced_combat_boots", armorItems);
        
        POWER_HELMET = registerGeneric("power_helmet", armorItems);
        POWER_CHESTPLATE = registerGeneric("power_chestplate", armorItems);
        POWER_LEGGINGS = registerGeneric("power_leggings", armorItems);
        POWER_BOOTS = registerGeneric("power_boots", armorItems);
        
        HEV_HELMET = registerGeneric("hev_helmet", armorItems);
        HEV_CHESTPLATE = registerGeneric("hev_chestplate", armorItems);
        HEV_LEGGINGS = registerGeneric("hev_leggings", armorItems);
        HEV_BOOTS = registerGeneric("hev_boots", armorItems);
        
        EXO_HELMET = registerGeneric("exo_helmet", armorItems);
        EXO_CHESTPLATE = registerGeneric("exo_chestplate", armorItems);
        EXO_LEGGINGS = registerGeneric("exo_leggings", armorItems);
        EXO_BOOTS = registerGeneric("exo_boots", armorItems);
        
        BERET = registerGeneric("beret", armorItems);
        
        NETHER_COMBAT_HELMET = registerGeneric("nether_combat_helmet", armorItems);
        NETHER_COMBAT_CHESTPLATE = registerGeneric("nether_combat_chestplate", armorItems);
        NETHER_COMBAT_LEGGINGS = registerGeneric("nether_combat_leggings", armorItems);
        NETHER_COMBAT_BOOTS = registerGeneric("nether_combat_boots", armorItems);
        
        MK2_POWER_HELMET = registerGeneric("mk2_power_helmet", armorItems);
        MK2_POWER_CHESTPLATE = registerGeneric("mk2_power_chestplate", armorItems);
        MK2_POWER_LEGGINGS = registerGeneric("mk2_power_leggings", armorItems);
        MK2_POWER_BOOTS = registerGeneric("mk2_power_boots", armorItems);
        
        HAND_CANNON = registerGeneric("hand_cannon", gunItems);
        DOUBLE_BARREL_SHOTGUN = registerGeneric("double_barrel_shotgun", gunItems);
        REVOLVER = registerGeneric("revolver", gunItems);
        GOLDEN_REVOLVER = registerGeneric("golden_revolver", gunItems);
        THOMPSON_SMG = registerGeneric("thompson_smg", gunItems);
        AKM = registerGeneric("akm", gunItems);
        BOLT_ACTION_RIFLE = registerGeneric("bolt_action_rifle", gunItems);
        M4 = registerGeneric("m4", gunItems);
        INFILTRATOR = registerGeneric("infiltrator", gunItems);
        PISTOL = registerGeneric("pistol", gunItems);
        COMBAT_SHOTGUN = registerGeneric("combat_shotgun", gunItems);
        MAC10 = registerGeneric("mac10", gunItems);
        FLAMETHROWER = registerGeneric("flamethrower", gunItems);
        ROCKET_LAUNCHER = registerGeneric("rocket_launcher", gunItems);
        GRIM_REAPER = registerGeneric("grim_reaper", gunItems);
        GRENADE_LAUNCHER = registerGeneric("grenade_launcher", gunItems);
        AUG = registerGeneric("aug", gunItems);
        NETHER_BLASTER = registerGeneric("nether_blaster", gunItems);
        BIO_GUN = registerGeneric("bio_gun", gunItems);
        TESLA_GUN = registerGeneric("tesla_gun", gunItems);
        LIGHT_MACHINE_GUN = registerGeneric("light_machine_gun", gunItems);
        MINIGUN = registerGeneric("minigun", gunItems);
        AS50 = registerGeneric("as50", gunItems);
        VECTOR = registerGeneric("vector", gunItems);
        SCAR = registerGeneric("scar", gunItems);
        LASER_RIFLE = registerGeneric("laser_rifle", gunItems);
        BLASTER_RIFLE = registerGeneric("blaster_rifle", gunItems);
        BLASTER_SHOTGUN = registerGeneric("blaster_shotgun", gunItems);
        SONIC_RIFLE = registerGeneric("sonic_rifle", gunItems);
        PDW = registerGeneric("pdw", gunItems);
        PULSE_RIFLE = registerGeneric("pulse_rifle", gunItems);
        DEATOMIZER = registerGeneric("deatomizer", gunItems);
        ALIEN_BLASTER = registerGeneric("alien_blaster", gunItems);
        NUCLEAR_DEATH_RAY = registerGeneric("nuclear_death_ray", gunItems);
        GAUSS_RIFLE = registerGeneric("gauss_rifle", gunItems);
        GUIDED_ROCKET_LAUNCHER = registerGeneric("guided_rocket_launcher", gunItems);
        TFG = registerGeneric("tfg", gunItems);
        LASER_PISTOL = registerGeneric("laser_pistol", gunItems);

        BASE_ITEMS = baseItems.build();
        AMMO_ITEMS = ammoItems.build();
        PART_ITEMS = partItems.build();
        MATERIAL_ITEMS = materialItems.build();
        UPGRADE_ITEMS = upgradeItems.build();
        EQUIPMENT_ITEMS = equipmentItems.build();
        ARMOR_ITEMS = armorItems.build();
        GUN_ITEMS = gunItems.build();
        
        ImmutableList.Builder<RegistryObject<? extends Item>> allItems = ImmutableList.builder();
        
        allItems.addAll(BASE_ITEMS);
        allItems.addAll(PART_ITEMS);
        allItems.addAll(MATERIAL_ITEMS);
        allItems.addAll(UPGRADE_ITEMS);
        allItems.addAll(AMMO_ITEMS);
        allItems.addAll(EQUIPMENT_ITEMS);
        allItems.addAll(ARMOR_ITEMS);
        allItems.addAll(GUN_ITEMS);
        
        ALL_ITEMS = allItems.build();
    }
    
    private static <T extends IAmmo> RegistryObject<AmmoItem<T>> registerGenericAmmo(
            String name, 
            Supplier<T> ammoSupplier,
            ImmutableList.Builder<RegistryObject<? extends Item>> items)
    {
        var result = REGISTER.register(name, () -> new AmmoItem<T>(ammoSupplier, new Item.Properties()));
        
        items.add(result);
        
        return result;
    }
    
    private static RegistryObject<Item> registerGeneric(
            String name,
            ImmutableList.Builder<RegistryObject<? extends Item>> items)
    {
        var result = REGISTER.register(name, () -> new Item(new Item.Properties()));
        
        items.add(result);
        
        return result;
    }

}
