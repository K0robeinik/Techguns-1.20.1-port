package techguns2;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = Techguns.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TGConfig
{

    public TGConfig()
    {
    }
    
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    
    public static final class General
    {
        private General()
        { }

        private static final ForgeConfigSpec.BooleanValue DEBUG = BUILDER
                .comment("=== GENERAL ===")
                .comment("Enable debug options and unfinished stuff, disable this for playing")
                .define("General.Debug", false);
        private static final ForgeConfigSpec.IntValue UPGRADE_XP_COST = BUILDER
                .comment("Base XP value for Upgrade Bench recipes (enchants)")
                .defineInRange("General.UpgradeXPCost", 20, 0, 10000);
        private static final ForgeConfigSpec.BooleanValue LIMIT_UNSAFE_MODE_TO_OP = BUILDER
                .comment("Only opped players can use the unsafe mode of guns, this OVERRIDES the permission setting 'techguns2.allowunsafemode'")
                .define("General.LimitUnsafeModeToOP", false);
        private static final ForgeConfigSpec.BooleanValue DISABLE_AUTO_FEEDER = BUILDER
                .comment("Disable automatic feeding of Food in the Techguns tab. Disable autofeeding if you think it breaks the balance")
                .define("General.DisableAutoFeeder", false);
        private static final ForgeConfigSpec.BooleanValue KEEP_LAVA_RECIPES_WHEN_FUEL_IS_PRESENT = BUILDER
                .comment("Keep recipes with lava instead of fuel even when fuel is present. Fuels need to be added by other mods")
                .define("General.KeepLavaRecipesWhenFuelIsPresent", false);
        private static final ForgeConfigSpec.BooleanValue MACHINES_NEED_NO_POWER = BUILDER
                .comment("Machines don't need power, activate this if you don't want to install a mod with generators and still be able to use the machines")
                .define("General.MachinesNeedNoPower", false);
        private static final ForgeConfigSpec.DoubleValue EXPLOSIVE_CHARGE_MAX_HARDNESS = BUILDER
                .comment("Highest blockHardness normal explosive charges can break, obsidian is 50.0)")
                .defineInRange("General.ExplosiveChargeMaxHardness", 30f, 0.0f, Float.MAX_VALUE);
        private static final ForgeConfigSpec.DoubleValue ADVANCED_EXPLOSIVE_CHARGE_MAX_HARDNESS = BUILDER
                .comment("Highest blockHardness advanced explosive charges can break, obsidian is 50.0)")
                .defineInRange("General.AdvancedExplosiveChargeMaxHardness", 100f, 0f, Float.MAX_VALUE);
        
        public static boolean debug;
        public static int upgradeXpCost;
        public static boolean limitUnsafeModeToOP;
        public static boolean disableAutofeeder;
        public static boolean keepLavaRecipesWhenFuelIsPresent;
        public static boolean machinesNeedNoPower;
        public static float explosiveChargeMaxHardness;
        public static float advancedExplosiveChargeMaxHardness;
        
        private static void onLoad(ModConfigEvent event)
        {
            debug = DEBUG.get();
            upgradeXpCost = UPGRADE_XP_COST.get();
            limitUnsafeModeToOP = LIMIT_UNSAFE_MODE_TO_OP.get();
            disableAutofeeder = DISABLE_AUTO_FEEDER.get();
            keepLavaRecipesWhenFuelIsPresent = KEEP_LAVA_RECIPES_WHEN_FUEL_IS_PRESENT.get();
            machinesNeedNoPower = MACHINES_NEED_NO_POWER.get();
            explosiveChargeMaxHardness = (float)(double)EXPLOSIVE_CHARGE_MAX_HARDNESS.get();
            advancedExplosiveChargeMaxHardness = (float)(double)ADVANCED_EXPLOSIVE_CHARGE_MAX_HARDNESS.get();
        }
        
        // Empty method to load the static final values.
        private static ForgeConfigSpec.Builder build(ForgeConfigSpec.Builder existing)
        {
            return existing;
        }
        
    }
    
    public static final class Materials
    {
        private Materials()
        { }

        private static final ForgeConfigSpec.BooleanValue ADD_COPPER_NUGGET = BUILDER
                .comment("=== MATERIALS ===")
                .comment("Adds copper nuggets")
                .define("Materials.AddCopperNugget", true);
        private static final ForgeConfigSpec.BooleanValue ADD_LEAD_INGOT = BUILDER
                .comment("Adds lead ingots")
                .define("Materials.AddLeadIngot", true);
        private static final ForgeConfigSpec.BooleanValue ADD_LEAD_NUGGET = BUILDER
                .comment("Adds lead nuggets")
                .define("Materials.AddLeadNugget", true);
        private static final ForgeConfigSpec.BooleanValue ADD_BRONZE_INGOT = BUILDER
                .comment("Adds bronze ingots")
                .define("Materials.AddBronzeIngot", true);
        private static final ForgeConfigSpec.BooleanValue ADD_TIN_INGOT = BUILDER
                .comment("Adds tin ingots")
                .define("Materials.AddTinIngot", true);
        private static final ForgeConfigSpec.BooleanValue ADD_STEEL_INGOT = BUILDER
                .comment("Adds steel ingots")
                .define("Materials.AddSteelIngot", true);
        private static final ForgeConfigSpec.BooleanValue ADD_STEEL_NUGGET = BUILDER
                .comment("Adds steel nuggets")
                .define("Materials.AddSteelNugget", true);
        
        public static boolean addCopperNugget;
        public static boolean addLeadIngot;
        public static boolean addLeadNugget;
        public static boolean addBronzeIngot;
        public static boolean addTinIngot;
        public static boolean addSteelIngot;
        public static boolean addSteelNugget;
        
        private static void onLoad(ModConfigEvent event)
        {
            addCopperNugget = ADD_COPPER_NUGGET.get();
            addLeadIngot = ADD_LEAD_INGOT.get();
            addLeadNugget = ADD_LEAD_NUGGET.get();
            addBronzeIngot = ADD_BRONZE_INGOT.get();
            addTinIngot = ADD_TIN_INGOT.get();
            addSteelIngot = ADD_STEEL_INGOT.get();
            addSteelNugget = ADD_STEEL_NUGGET.get();
        }
        
        // Empty method to load the static final values.
        private static ForgeConfigSpec.Builder build(ForgeConfigSpec.Builder existing)
        {
            return existing;
        }
    }
    
    public static final class WorldGeneration
    {
        private WorldGeneration()
        { }
        
        private static final ForgeConfigSpec.BooleanValue ENABLED = BUILDER
                .comment("=== WORLD GENERATION ===")
                .comment("Whether or not world generation features are enabled")
                .define("WorldGeneration.Enabled", true);
        
        public static boolean enabled;
        
        public static final class Structures
        {
            private Structures()
            { }

            private static final ForgeConfigSpec.BooleanValue ENABLED = BUILDER
                    .comment("=-= STRUCTURES =-=")
                    .comment("Whether or not structures will generate in the world")
                    .define("WorldGeneration.Structures.Enabled", true);

            private static final ForgeConfigSpec.BooleanValue SPAWN_ORE_CLUSTERS = BUILDER
                    .comment("When worldgen is enabled, include structure spawns that contain ore clusters.")
                    .define("WorldGeneration.Structures.SpawnOreClusters", true);
            
            public static boolean enabled;
            public static boolean spawnOreClusters;

            private static void onLoad(ModConfigEvent event)
            {
                enabled = ENABLED.get();
                spawnOreClusters = SPAWN_ORE_CLUSTERS.get();
                Small.onLoad(event);
                Medium.onLoad(event);
                Large.onLoad(event);
            }
            
            // Empty method to load the static final values.
            private static ForgeConfigSpec.Builder build(ForgeConfigSpec.Builder existing)
            {
                return Large.build(Medium.build(Small.build(existing)));
            }
            
            public static final class Small
            {
                private Small()
                { }

                private static final ForgeConfigSpec.BooleanValue ENABLED = BUILDER
                        .comment("--- SMALL ---")
                        .comment("Whether or not small structures are allowed to generate.")
                        .define("WorldGeneration.Structures.Small.Enabled", true);
                private static final ForgeConfigSpec.IntValue WEIGHT = BUILDER
                        .comment("Every X chunks a spawn attempt is made to spawn a small structure. This is in both dimensions, ChunkX, and ChunkY modulo <this Value> must be 0")
                        .defineInRange("WorldGeneration.Structures.Small.Weight", 16, 4, 100000);
                
                public static boolean enabled;
                public static int weight;

                private static void onLoad(ModConfigEvent event)
                {
                    enabled = ENABLED.get();
                    weight = WEIGHT.get();
                }
                
                // Empty method to load the static final values.
                private static ForgeConfigSpec.Builder build(ForgeConfigSpec.Builder existing)
                {
                    return existing;
                }
            }
            
            public static final class Medium
            {
                private Medium()
                { }

                private static final ForgeConfigSpec.BooleanValue ENABLED = BUILDER
                        .comment("--- MEDIUM ---")
                        .comment("Whether or not medium structures are allowed to generate.")
                        .define("WorldGeneration.Structures.Medium.Enabled", true);
                private static final ForgeConfigSpec.IntValue WEIGHT = BUILDER
                        .comment("Every X chunks a spawn attempt is made to spawn a medium structure. This is in both dimensions, ChunkX, and ChunkY modulo <this Value> must be 0")
                        .defineInRange("WorldGeneration.Structures.Medium.Weight", 32, 8, 100000);
                
                public static boolean enabled;
                public static int weight;

                private static void onLoad(ModConfigEvent event)
                {
                    enabled = ENABLED.get();
                    weight = WEIGHT.get();
                }
                
                // Empty method to load the static final values.
                private static ForgeConfigSpec.Builder build(ForgeConfigSpec.Builder existing)
                {
                    return existing;
                }
            }
            
            public static final class Large
            {
                private Large()
                { }

                private static final ForgeConfigSpec.BooleanValue ENABLED = BUILDER
                        .comment("--- LARGE ---")
                        .comment("Whether or not large structures are allowed to generate.")
                        .define("WorldGeneration.Structures.Large.Enabled", true);
                private static final ForgeConfigSpec.IntValue WEIGHT = BUILDER
                        .comment("Every X chunks a spawn attempt is made to spawn a large structure. This is in both dimensions, ChunkX, and ChunkY modulo <this Value> must be 0")
                        .defineInRange("WorldGeneration.Structures.Large.Weight", 64, 16, 100000);
                
                public static boolean enabled;
                public static int weight;

                private static void onLoad(ModConfigEvent event)
                {
                    enabled = ENABLED.get();
                    weight = WEIGHT.get();
                }
                
                // Empty method to load the static final values.
                private static ForgeConfigSpec.Builder build(ForgeConfigSpec.Builder existing)
                {
                    return existing;
                }
            }
        }
        
        public static final class Ores
        {
            private Ores()
            { }

            private static final ForgeConfigSpec.BooleanValue ENABLED = BUILDER
                    .comment("=-= ORES =-=")
                    .comment("Whether or not ores will generate in the world.")
                    .define("WorldGeneration.Ores.Enabled", true);

            private static final ForgeConfigSpec.BooleanValue TIN = BUILDER
                    .comment("Generate Tin Ore, disable if other mod does.")
                    .define("WorldGeneration.Ores.Tin", true);
            private static final ForgeConfigSpec.BooleanValue LEAD = BUILDER
                    .comment("Generate Lead Ore, disable if other mod does.")
                    .define("WorldGeneration.Ores.Lead", true);
            private static final ForgeConfigSpec.BooleanValue URANIUM = BUILDER
                    .comment("Generate Uranium, disable if other mod already adds it and you want only 1 type. Tag: 'forge/ores/uranium' ")
                    .define("WorldGeneration.Ores.Uranium", true);
            private static final ForgeConfigSpec.BooleanValue TITANIUM = BUILDER
                    .comment("Generate Titanium, not generated by most mods mods, leave it on in most cases.")
                    .define("WorldGeneration.Ores.Titanium", true);
            
            public static boolean enabled;
            
            public static boolean tin;
            public static boolean lead;
            public static boolean uranium;
            public static boolean titanium;

            private static void onLoad(ModConfigEvent event)
            {
                enabled = ENABLED.get();
                
                tin = TIN.get();
                lead = LEAD.get();
                uranium = URANIUM.get();
                titanium = TITANIUM.get();
            }
            
            // Empty method to load the static final values.
            private static ForgeConfigSpec.Builder build(ForgeConfigSpec.Builder existing)
            {
                return existing;
            }
        }
        
        private static void onLoad(ModConfigEvent event)
        {
            enabled = ENABLED.get();
            Structures.onLoad(event);
            Ores.onLoad(event);
        }
        
        // Empty method to load the static final values.
        private static ForgeConfigSpec.Builder build(ForgeConfigSpec.Builder existing)
        {
            return Ores.build(Structures.build(existing));
        }
    }
    
    public static final class NPCSpawns
    {
        private NPCSpawns()
        { }
        
        private static final ForgeConfigSpec.BooleanValue ENABLED = BUILDER
                .comment("=== NPC SPAWNS ===")
                .comment("Whether or not NPCs will spawn naturally in the world.")
                .define("NPCSpawns.Enabled", true);
        
        private static final ForgeConfigSpec.IntValue DISTANCE_LEVEL_0 = BUILDER
                .comment("Up to which distance to worldspawn only mobs with danger level up to 0 will spawn")
                .defineInRange("NPCSpawns.DistanceLevel0", 500, 0, Integer.MAX_VALUE);
        private static final ForgeConfigSpec.IntValue DISTANCE_LEVEL_1 = BUILDER
                .comment("Up to which distance to worldspawn only mobs with danger level up to 1 will spawn")
                .defineInRange("NPCSpawns.DistanceLevel1", 1000, 0, Integer.MAX_VALUE);
        private static final ForgeConfigSpec.IntValue DISTANCE_LEVEL_2 = BUILDER
                .comment("Up to which distance to worldspawn only mobs with danger level up to 2 will spawn")
                .defineInRange("NPCSpawns.DistanceLevel2", 2500, 0, Integer.MAX_VALUE);
        
        public static boolean enabled;
        public static int distanceLevel0;
        public static int distanceLevel1;
        public static int distanceLevel2;
        
        private static void onLoad(ModConfigEvent event)
        {
            enabled = ENABLED.get();
            distanceLevel0 = DISTANCE_LEVEL_0.get();
            distanceLevel1 = DISTANCE_LEVEL_1.get();
            distanceLevel2 = DISTANCE_LEVEL_2.get();
            
            Weights.onLoad(event);
        }
        
        // Empty method to load the static final values.
        private static ForgeConfigSpec.Builder build(ForgeConfigSpec.Builder existing)
        {
            return Weights.build(existing);
        }
        
        public static final class Weights
        {
            private Weights()
            { }
            
            private static final ForgeConfigSpec.IntValue ZOMBIE_SOLDIER = BUILDER
                    .comment("=-= WEIGHTS =-=")
                    .comment("Spawn weight for spawning Zombie Soldiers, at 0 spawn will not be registered")
                    .defineInRange("NPCSpawns.Weights.ZombieSoldier", 100, 0, 10000);
            private static final ForgeConfigSpec.IntValue ZOMBIE_FARMER = BUILDER
                    .comment("Spawn weight for spawning Zombie Farmers, at 0 spawn will not be registered")
                    .defineInRange("NPCSpawns.Weights.ZombieFarmer", 200, 0, 10000);
            private static final ForgeConfigSpec.IntValue ZOMBIE_MINER = BUILDER
                    .comment("Spawn weight for spawning Zombie Miners, at 0 spawn will not be registered")
                    .defineInRange("NPCSpawns.Weights.ZombieMiner", 200, 0, 10000);
            private static final ForgeConfigSpec.IntValue ZOMBIFIED_PIGLIN_SOLDIER = BUILDER
                    .comment("Spawn weight for spawning Zombified Piglin Soldiers (Nether only), at 0 spawn will not be registered")
                    .defineInRange("NPCSpawns.Weights.ZombifiedPiglinSoldier", 100, 0, 10000);
            private static final ForgeConfigSpec.IntValue CYBER_DEMON = BUILDER
                    .comment("Spawn weight for spawning Cyber Demons (Nether only), at 0 spawn will not be registered")
                    .defineInRange("NPCSpawns.Weights.CyberDemon", 30, 0, 10000);
            private static final ForgeConfigSpec.IntValue BANDIT = BUILDER
                    .comment("Spawn weight for spawning Bandit groups, at 0 spawn will not be registered")
                    .defineInRange("NPCSpawns.Weights.Bandit", 50, 0, 10000);
            private static final ForgeConfigSpec.IntValue SKELETON_SOLDIER = BUILDER
                    .comment("Spawn weight for spawning Skeleton Soldiers, at 0 spawn will not be registered")
                    .defineInRange("NPCSpawns.Weights.SkeletonSoldier", 100, 0, 10000);
            private static final ForgeConfigSpec.IntValue PYSCHO_STEVE = BUILDER
                    .comment("Spawn weight for spawning Psycho Steve, early game boss, don't set to high value, at 0 spawn will not be registered")
                    .defineInRange("NPCSpawns.Weights.PyschoSteve", 3, 0, 10000);
            private static final ForgeConfigSpec.IntValue IN_OVERWORLD = BUILDER
                    .comment("Total spawn weight of Techguns NPCs in the Overworld, determines how many TG npcs spawn")
                    .defineInRange("NPCSpawns.Weights.InOverworld", 600, 0, 10000);
            private static final ForgeConfigSpec.IntValue IN_NETHER = BUILDER
                    .comment("Total spawn weight of Techguns NPCs in the Nether, determines how many TG npcs spawn")
                    .defineInRange("NPCSpawns.Weights.InNether", 300, 0, 10000);
            
            public static int zombieSoldier;
            public static int zombieFarmer;
            public static int zombieMiner;
            public static int zombifiedPiglinSoldier;
            public static int cyberDemon;
            public static int skeletonSoldier;
            public static int bandit;
            public static int pyschoSteve;
            public static int inOverworld;
            public static int inNether;
            
            private static void onLoad(ModConfigEvent event)
            {
                zombieSoldier = ZOMBIE_SOLDIER.get();
                zombieFarmer = ZOMBIE_FARMER.get();
                zombieMiner = ZOMBIE_MINER.get();
                zombifiedPiglinSoldier = ZOMBIFIED_PIGLIN_SOLDIER.get();
                cyberDemon = CYBER_DEMON.get();
                skeletonSoldier = SKELETON_SOLDIER.get();
                bandit = BANDIT.get();
                pyschoSteve = PYSCHO_STEVE.get();
                inOverworld = IN_OVERWORLD.get();
                inNether = IN_NETHER.get();
            }
            
            // Empty method to load the static final values.
            private static ForgeConfigSpec.Builder build(ForgeConfigSpec.Builder existing)
            {
                return existing;
            }
        }
    }
    
    public static final class Damage
    {
        private Damage()
        { }

        private static final ForgeConfigSpec.DoubleValue PVP_FACTOR = BUILDER
                .comment("=== DAMAGE ===")
                .comment("Damage factor Techguns weapons deal when fired from players against other players, is zero when PvP is disabled")
                .defineInRange("Damage.PvPFactor", 0.5f, 0f, 100f);
        private static final ForgeConfigSpec.DoubleValue TURRET_TO_PLAYER_FACTOR = BUILDER
                .comment("Damage factor Techguns Turrets deal when hitting players")
                .defineInRange("Damage.TurretToPlayerFactor", 0.5f, 0f, 100f);
        private static final ForgeConfigSpec.DoubleValue NPC_FACTOR = BUILDER
                .comment("Damage factor for all NPCs other than turrets, they already have a difficulty dependent damage penalty, this can be used to further reduce their damage, or increase it")
                .defineInRange("Damage.NPCFactor", 1f, 0f, 100f);
        
        public static float pvpFactor;
        public static float turretToPlayerFactor;
        public static float npcFactor;

        
        private static void onLoad(ModConfigEvent event)
        {
            pvpFactor = (float)(double)PVP_FACTOR.get();
            turretToPlayerFactor = (float)(double)TURRET_TO_PLAYER_FACTOR.get();
            npcFactor = (float)(double)NPC_FACTOR.get();
        }
        
        // Empty method to load the static final values.
        private static ForgeConfigSpec.Builder build(ForgeConfigSpec.Builder existing)
        {
            return existing;
        }
    }

    public static final class Client
    {
        private Client()
        { }
        
        private static final ForgeConfigSpec.BooleanValue LOCK_SPEED_FOV = BUILDER
                .comment("=== CLIENT-SIDE ===")
                .comment("Counters the speed dependant FOV change. This also stops FOV changes while sprinting. Don't activate if another mod does this too, pure clientside check.")
                .define("Client.LockSpeedDependantFov", true);
        private static final ForgeConfigSpec.DoubleValue FIXED_SPRINT_FOV_MULTIPLIER = BUILDER
                .comment("Multiply the FOV while sprinting by this value independent from the actual speed, has no effect when LockSpeedDependantFov is false, pure clientside check.")
                .defineInRange("Client.FixedSprintFovMultiplier", 1.15f, 0f, 10f);
        private static final ForgeConfigSpec.IntValue SORT_PASSES_PER_TICK = BUILDER
                .comment("How many bubble sort passes should be performed each tick on particles. 0=off. Clientside")
                .defineInRange("Client.ParticleDepthSortPasses", 10, 0, 20);
        private static final ForgeConfigSpec.BooleanValue ENABLE_DEATH_FX = BUILDER
                .comment("Enable Death Effects, pure clientside check.")
                .define("Client.EnableDeathEffects", true);
        private static final ForgeConfigSpec.BooleanValue ENABLE_GORE_DEATH_FX = BUILDER
                .comment("Enable the gore Death Effect, requires DeathEffects to be enabled, pure clientside check.")
                .define("Client.EnableGoreDeathEffect", true);
        
        public static boolean lockSpeedFov;
        public static float fixedSprintFov;
        
        public static boolean enableDeathFX;
        public static boolean enableGoreDeathFX;
        
        public static int sortPassesPerTick;
        
        private static void onLoad(ModConfigEvent event)
        {
            lockSpeedFov = LOCK_SPEED_FOV.get();
            fixedSprintFov = (float)(double)FIXED_SPRINT_FOV_MULTIPLIER.get();
            enableDeathFX = ENABLE_DEATH_FX.get();
            enableGoreDeathFX = ENABLE_GORE_DEATH_FX.get();
            sortPassesPerTick = SORT_PASSES_PER_TICK.get();
        }
        
        // Empty method to load the static final values.
        private static ForgeConfigSpec.Builder build(ForgeConfigSpec.Builder existing)
        {
            return existing;
        }
    }
    
    // TODO: Ore drill config settings.
    
    static final ForgeConfigSpec SPEC = 
            Client.build(
            Damage.build(
            NPCSpawns.build(
            WorldGeneration.build(
            Materials.build(
            General.build(
                    BUILDER)))))).build();
    
    @SubscribeEvent
    static void onLoad(ModConfigEvent event)
    {
        General.onLoad(event);
        Materials.onLoad(event);
        WorldGeneration.onLoad(event);
        NPCSpawns.onLoad(event);
        Damage.onLoad(event);
        Client.onLoad(event);
    }
}
