package techguns2;

import java.util.function.Supplier;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;
import techguns2.ammo.IAmmo;
import techguns2.ammo.IAmmoType;
import techguns2.ammo.IClipAmmo;
import techguns2.ammo.IMagazineAmmo;
import techguns2.ammo.IRoundAmmo;
import techguns2.ammo.common.IAdvancedRoundAmmo;
import techguns2.ammo.common.IAs50RoundAmmo;
import techguns2.ammo.common.IAssaultRifleRoundAmmo;
import techguns2.ammo.common.IGaussRifleSlugRoundAmmo;
import techguns2.ammo.common.IGrenadeAmmo;
import techguns2.ammo.common.ILmgRoundAmmo;
import techguns2.ammo.common.IMinigunRoundAmmo;
import techguns2.ammo.common.INetherChargeRoundAmmo;
import techguns2.ammo.common.IPistolRoundAmmo;
import techguns2.ammo.common.IRifleRoundAmmo;
import techguns2.ammo.common.IRocketAmmo;
import techguns2.ammo.common.IShotgunShellAmmo;
import techguns2.ammo.common.ISmgRoundAmmo;
import techguns2.ammo.common.ISniperRoundAmmo;

public final class TGAmmo implements TGInitializer
{
    protected TGAmmo()
    { }
    
    @Override
    public final void setup(IEventBus eventBus)
    {
        REGISTER.makeRegistry(() -> {
            RegistryBuilder<IAmmo> builder = RegistryBuilder.of();
            return builder;
        });
        REGISTER.register(eventBus);
    }
    
    private static final DeferredRegister<IAmmo> REGISTER = DeferredRegister.create(TGCustomRegistries.AMMO, Techguns.MODID);
    
    public static final RegistryObject<StoneBullets> STONE_BULLETS = REGISTER.register("stone_bullets",
            () -> new StoneBullets(TGAmmoTypes.DEFAULT));
    public static final RegistryObject<PistolRounds> PISTOL_ROUNDS = REGISTER.register("pistol_rounds",
            () -> new PistolRounds(TGAmmoTypes.DEFAULT));
    public static final RegistryObject<PistolRounds> INCENDIARY_PISTOL_ROUNDS = REGISTER.register("incendiary_pistol_rounds",
            () -> new PistolRounds(TGAmmoTypes.INCENDIARY));
    public static final RegistryObject<ShotgunShells> SHOTGUN_SHELLS = REGISTER.register("shotgun_shells",
            () -> new ShotgunShells(TGAmmoTypes.DEFAULT));
    public static final RegistryObject<ShotgunShells> INCENDIARY_SHOTGUN_SHELLS = REGISTER.register("incendiary_shotgun_shells",
            () -> new ShotgunShells(TGAmmoTypes.INCENDIARY));
    public static final RegistryObject<RifleRounds> RIFLE_ROUNDS = REGISTER.register("rifle_rounds",
            () -> new RifleRounds(TGAmmoTypes.DEFAULT));
    public static final RegistryObject<RifleRounds> INCENDIARY_RIFLE_ROUNDS = REGISTER.register("incendiary_rifle_rounds",
            () -> new RifleRounds(TGAmmoTypes.INCENDIARY));
    public static final RegistryObject<RifleRoundClip<RifleRounds>> RIFLE_ROUND_CLIP = REGISTER.register("rifle_round_clip",
            () -> new RifleRoundClip<RifleRounds>(TGAmmo.RIFLE_ROUNDS, 9));
    public static final RegistryObject<RifleRoundClip<RifleRounds>> INCENDIARY_RIFLE_ROUND_CLIP = REGISTER.register("incendiary_rifle_round_clip",
            () -> new RifleRoundClip<RifleRounds>(TGAmmo.INCENDIARY_RIFLE_ROUNDS, 9));
    public static final RegistryObject<SniperRounds> SNIPER_ROUNDS = REGISTER.register("sniper_rounds",
            () -> new SniperRounds(TGAmmoTypes.DEFAULT));
    public static final RegistryObject<SniperRounds> INCENDIARY_SNIPER_ROUNDS = REGISTER.register("incendiary_sniper_rifle_rounds",
            () -> new SniperRounds(TGAmmoTypes.INCENDIARY));
    public static final RegistryObject<SniperRounds> EXPLOSIVE_SNIPER_ROUNDS = REGISTER.register("explosive_sniper_rifle_rounds",
            () -> new SniperRounds(TGAmmoTypes.EXPLOSIVE));
    public static final RegistryObject<Rocket> ROCKET = REGISTER.register("rocket",
            () -> new Rocket(TGAmmoTypes.DEFAULT));
    public static final RegistryObject<Rocket> HIGH_VELOCITY_ROCKET = REGISTER.register("high_velocity_rocket",
            () -> new Rocket(TGAmmoTypes.HIGH_VELOCITY_ROCKET));
    public static final RegistryObject<Rocket> NUCLEAR_ROCKET = REGISTER.register("nuclear_rocket",
            () -> new Rocket(TGAmmoTypes.NUCLEAR_ROCKET));
    public static final RegistryObject<Grenade40MM> GRENADE_40MM = REGISTER.register("grenade_40mm",
            () -> new Grenade40MM(TGAmmoTypes.DEFAULT));
    public static final RegistryObject<AdvancedRounds> ADVANCED_ROUNDS = REGISTER.register("advanced_rounds",
            () -> new AdvancedRounds(TGAmmoTypes.DEFAULT));
    public static final RegistryObject<PistolRoundMagazine<PistolRounds>> PISTOL_ROUND_MAGAZINE = REGISTER.register("pistol_round_magazine",
            () -> new PistolRoundMagazine<PistolRounds>(TGItems.EMPTY_PISTOL_MAGAZINE, PISTOL_ROUNDS, 2));
    public static final RegistryObject<PistolRoundMagazine<PistolRounds>> INCENDIARY_PISTOL_ROUND_MAGAZINE = REGISTER.register("incendiary_pistol_round_magazine",
            () -> new PistolRoundMagazine<PistolRounds>(TGItems.EMPTY_PISTOL_MAGAZINE, INCENDIARY_PISTOL_ROUNDS, 2));
    public static final RegistryObject<SmgRoundMagazine<PistolRounds>> SMG_ROUND_MAGAZINE = REGISTER.register("smg_round_magazine",
            () -> new SmgRoundMagazine<PistolRounds>(TGItems.EMPTY_PISTOL_MAGAZINE, PISTOL_ROUNDS, 3));
    public static final RegistryObject<SmgRoundMagazine<PistolRounds>> INCENDIARY_SMG_ROUND_MAGAZINE = REGISTER.register("incendiary_smg_round_magazine",
            () -> new SmgRoundMagazine<PistolRounds>(TGItems.EMPTY_PISTOL_MAGAZINE, INCENDIARY_PISTOL_ROUNDS, 3));
    public static final RegistryObject<AssaultRifleRoundMagazine<RifleRounds>> ASSAULT_RIFLE_ROUND_MAGAZINE = REGISTER.register("assault_rifle_round_magazine",
            () -> new AssaultRifleRoundMagazine<RifleRounds>(TGItems.EMPTY_ASSAULT_RIFLE_MAGAZINE, RIFLE_ROUNDS, 3));
    public static final RegistryObject<AssaultRifleRoundMagazine<RifleRounds>> INCENDIARY_ASSAULT_RIFLE_ROUND_MAGAZINE = REGISTER.register("incendiary_assault_rifle_round_magazine",
            () -> new AssaultRifleRoundMagazine<RifleRounds>(TGItems.EMPTY_ASSAULT_RIFLE_MAGAZINE, INCENDIARY_RIFLE_ROUNDS, 3));
    public static final RegistryObject<LmgRoundMagazine<RifleRounds>> LMG_ROUND_MAGAZINE = REGISTER.register("lmg_round_magazine",
            () -> new LmgRoundMagazine<RifleRounds>(TGItems.EMPTY_LMG_MAGAZINE, RIFLE_ROUNDS, 8));
    public static final RegistryObject<LmgRoundMagazine<RifleRounds>> INCENDIARY_LMG_ROUND_MAGAZINE = REGISTER.register("incendiary_lmg_round_magazine",
            () -> new LmgRoundMagazine<RifleRounds>(TGItems.EMPTY_LMG_MAGAZINE, INCENDIARY_RIFLE_ROUNDS, 8));
    public static final RegistryObject<MinigunRoundMagazine<RifleRounds>> MINIGUN_ROUND_MAGAZINE = REGISTER.register("minigun_round_magazine",
            () -> new MinigunRoundMagazine<RifleRounds>(TGItems.EMPTY_MINIGUN_MAGAZINE, RIFLE_ROUNDS, 16));
    public static final RegistryObject<MinigunRoundMagazine<RifleRounds>> INCENDIARY_MINIGUN_ROUND_MAGAZINE = REGISTER.register("incendiary_minigun_round_magazine",
            () -> new MinigunRoundMagazine<RifleRounds>(TGItems.EMPTY_MINIGUN_MAGAZINE, INCENDIARY_RIFLE_ROUNDS, 16));
    public static final RegistryObject<As50RoundMagazine<SniperRounds>> AS50_ROUND_MAGAZINE = REGISTER.register("as50_round_magazine",
            () -> new As50RoundMagazine<SniperRounds>(TGItems.EMPTY_AS50_MAGAZINE, SNIPER_ROUNDS, 2));
    public static final RegistryObject<As50RoundMagazine<SniperRounds>> INCENDIARY_AS50_ROUND_MAGAZINE = REGISTER.register("incendiary_as50_round_magazine",
            () -> new As50RoundMagazine<SniperRounds>(TGItems.EMPTY_AS50_MAGAZINE, INCENDIARY_SNIPER_ROUNDS, 2));
    public static final RegistryObject<As50RoundMagazine<SniperRounds>> EXPLOSIVE_AS50_ROUND_MAGAZINE = REGISTER.register("explosive_as50_round_magazine",
            () -> new As50RoundMagazine<SniperRounds>(TGItems.EMPTY_AS50_MAGAZINE, EXPLOSIVE_SNIPER_ROUNDS, 2));
    public static final RegistryObject<AdvancedRoundMagazine<AdvancedRounds>> ADVANCED_ROUND_MAGAZINE = REGISTER.register("advanced_round_magazine",
            () -> new AdvancedRoundMagazine<AdvancedRounds>(TGItems.EMPTY_ADVANCED_MAGAZINE, ADVANCED_ROUNDS, 2));
    public static final RegistryObject<NetherCharge> NETHER_CHARGE = REGISTER.register("nether_charge",
            () -> new NetherCharge(TGAmmoTypes.DEFAULT));
    public static final RegistryObject<GaussRifleSlugRound> GAUSS_RIFLE_SLUGS = REGISTER.register("gauss_rifle_slugs",
            () -> new GaussRifleSlugRound(TGAmmoTypes.DEFAULT));
    
    public static final class StoneBullets implements IAmmo
    {
        private final Supplier<? extends IAmmoType> _typeSupplier;
        
        public StoneBullets(Supplier<? extends IAmmoType> typeSupplier)
        {
            this._typeSupplier = typeSupplier;
        }
        
        @Override
        public final IAmmoType getType()
        {
            return this._typeSupplier.get();
        }
        
    }
    
    public static final class PistolRounds implements IPistolRoundAmmo, ISmgRoundAmmo, IRoundAmmo
    {
        private final Supplier<? extends IAmmoType> _typeSupplier;
        
        public PistolRounds(Supplier<? extends IAmmoType> typeSupplier)
        {
            this._typeSupplier = typeSupplier;
        }
        
        @Override
        public final IAmmoType getType()
        {
            return this._typeSupplier.get();
        }
        
    }
    
    public static final class ShotgunShells implements IShotgunShellAmmo, IAmmo
    {
        private final Supplier<? extends IAmmoType> _typeSupplier;
        
        public ShotgunShells(Supplier<? extends IAmmoType> typeSupplier)
        {
            this._typeSupplier = typeSupplier;
        }
        
        @Override
        public final IAmmoType getType()
        {
            return this._typeSupplier.get();
        }
        
    }
    
    public static final class RifleRounds implements IAssaultRifleRoundAmmo, ILmgRoundAmmo, IMinigunRoundAmmo, IRifleRoundAmmo, IRoundAmmo
    {
        private final Supplier<? extends IAmmoType> _typeSupplier;
        
        public RifleRounds(Supplier<? extends IAmmoType> typeSupplier)
        {
            this._typeSupplier = typeSupplier;
        }
        
        @Override
        public final IAmmoType getType()
        {
            return this._typeSupplier.get();
        }
    }
    
    public static final class SniperRounds implements IAs50RoundAmmo, ISniperRoundAmmo, IRoundAmmo
    {
        private final Supplier<? extends IAmmoType> _typeSupplier;
        
        public SniperRounds(Supplier<? extends IAmmoType> typeSupplier)
        {
            this._typeSupplier = typeSupplier;
        }
        
        @Override
        public final IAmmoType getType()
        {
            return this._typeSupplier.get();
        }
    }
    
    public static final class RifleRoundClip<TRound extends IRifleRoundAmmo> implements IClipAmmo
    {
        private final Supplier<TRound> _roundProvider;
        private final int _capacity;
        
        public RifleRoundClip(Supplier<TRound> roundProvider, int capacity)
        {
            this._roundProvider = roundProvider;
            this._capacity = capacity;
        }

        @Override
        public final int getRoundCapacity()
        {
            return this._capacity;
        }

        @Override
        public final TRound getRound()
        {
            return this._roundProvider.get();
        }
    }
    
    public static final class Rocket implements IRocketAmmo
    {
        private final Supplier<? extends IAmmoType> _typeSupplier;
        
        public Rocket(Supplier<? extends IAmmoType> typeSupplier)
        {
            this._typeSupplier = typeSupplier;
        }
        
        @Override
        public final IAmmoType getType()
        {
            return this._typeSupplier.get();
        }
    }
    
    public static final class AdvancedRounds implements IAdvancedRoundAmmo
    {
        private final Supplier<? extends IAmmoType> _typeSupplier;
        
        public AdvancedRounds(Supplier<? extends IAmmoType> typeSupplier)
        {
            this._typeSupplier = typeSupplier;
        }
        
        @Override
        public final IAmmoType getType()
        {
            return this._typeSupplier.get();
        }
    }
    
    public static final class PistolRoundMagazine<TRound extends IPistolRoundAmmo> implements IMagazineAmmo
    {
        private final Supplier<? extends Item> _emptyMagazineProvider;
        private final Supplier<TRound> _roundProvider;
        private final int _capacity;
        
        public PistolRoundMagazine(
                Supplier<? extends Item> emptyMagazineItemSupplier,
                Supplier<TRound> roundProvider,
                int capacity)
        {
            this._emptyMagazineProvider = emptyMagazineItemSupplier;
            this._roundProvider = roundProvider;
            this._capacity = capacity;
        }

        @Override
        public final int getRoundCapacity()
        {
            return this._capacity;
        }

        @Override
        public final TRound getRound()
        {
            return this._roundProvider.get();
        }

        @Override
        public final Item getEmptyMagazineItem()
        {
            return this._emptyMagazineProvider.get();
        }
    }

    public static final class SmgRoundMagazine<TRound extends ISmgRoundAmmo> implements IMagazineAmmo
    {
        private final Supplier<? extends Item> _emptyMagazineProvider;
        private final Supplier<TRound> _roundProvider;
        private final int _capacity;
        
        public SmgRoundMagazine(
                Supplier<? extends Item> emptyMagazineItemSupplier,
                Supplier<TRound> roundProvider,
                int capacity)
        {
            this._emptyMagazineProvider = emptyMagazineItemSupplier;
            this._roundProvider = roundProvider;
            this._capacity = capacity;
        }

        @Override
        public final int getRoundCapacity()
        {
            return this._capacity;
        }

        @Override
        public final TRound getRound()
        {
            return this._roundProvider.get();
        }

        @Override
        public final Item getEmptyMagazineItem()
        {
            return this._emptyMagazineProvider.get();
        }
    }
    
    public static final class AssaultRifleRoundMagazine<TRound extends IAssaultRifleRoundAmmo> implements IMagazineAmmo
    {
        private final Supplier<? extends Item> _emptyMagazineProvider;
        private final Supplier<TRound> _roundProvider;
        private final int _capacity;
        
        public AssaultRifleRoundMagazine(
                Supplier<? extends Item> emptyMagazineItemSupplier,
                Supplier<TRound> roundProvider,
                int capacity)
        {
            this._emptyMagazineProvider = emptyMagazineItemSupplier;
            this._roundProvider = roundProvider;
            this._capacity = capacity;
        }

        @Override
        public final int getRoundCapacity()
        {
            return this._capacity;
        }

        @Override
        public final TRound getRound()
        {
            return this._roundProvider.get();
        }

        @Override
        public final Item getEmptyMagazineItem()
        {
            return this._emptyMagazineProvider.get();
        }
    }
    
    public static final class LmgRoundMagazine<TRound extends ILmgRoundAmmo> implements IMagazineAmmo
    {
        private final Supplier<? extends Item> _emptyMagazineProvider;
        private final Supplier<TRound> _roundProvider;
        private final int _capacity;
        
        public LmgRoundMagazine(
                Supplier<? extends Item> emptyMagazineItemSupplier,
                Supplier<TRound> roundProvider,
                int capacity)
        {
            this._emptyMagazineProvider = emptyMagazineItemSupplier;
            this._roundProvider = roundProvider;
            this._capacity = capacity;
        }

        @Override
        public final int getRoundCapacity()
        {
            return this._capacity;
        }

        @Override
        public final TRound getRound()
        {
            return this._roundProvider.get();
        }

        @Override
        public final Item getEmptyMagazineItem()
        {
            return this._emptyMagazineProvider.get();
        }
    }
    
    public static final class MinigunRoundMagazine<TRound extends IMinigunRoundAmmo> implements IMagazineAmmo
    {
        private final Supplier<? extends Item> _emptyMagazineProvider;
        private final Supplier<TRound> _roundProvider;
        private final int _capacity;
        
        public MinigunRoundMagazine(
                Supplier<? extends Item> emptyMagazineItemSupplier,
                Supplier<TRound> roundProvider,
                int capacity)
        {
            this._emptyMagazineProvider = emptyMagazineItemSupplier;
            this._roundProvider = roundProvider;
            this._capacity = capacity;
        }

        @Override
        public final int getRoundCapacity()
        {
            return this._capacity;
        }

        @Override
        public final TRound getRound()
        {
            return this._roundProvider.get();
        }

        @Override
        public final Item getEmptyMagazineItem()
        {
            return this._emptyMagazineProvider.get();
        }
    }

    public static final class As50RoundMagazine<TRound extends IAs50RoundAmmo> implements IMagazineAmmo
    {
        private final Supplier<? extends Item> _emptyMagazineProvider;
        private final Supplier<TRound> _roundProvider;
        private final int _capacity;
        
        public As50RoundMagazine(
                Supplier<? extends Item> emptyMagazineItemSupplier,
                Supplier<TRound> roundProvider,
                int capacity)
        {
            this._emptyMagazineProvider = emptyMagazineItemSupplier;
            this._roundProvider = roundProvider;
            this._capacity = capacity;
        }

        @Override
        public final int getRoundCapacity()
        {
            return this._capacity;
        }

        @Override
        public final TRound getRound()
        {
            return this._roundProvider.get();
        }

        @Override
        public final Item getEmptyMagazineItem()
        {
            return this._emptyMagazineProvider.get();
        }
    }

    public static final class AdvancedRoundMagazine<TRound extends IAdvancedRoundAmmo> implements IMagazineAmmo
    {
        private final Supplier<? extends Item> _emptyMagazineProvider;
        private final Supplier<TRound> _roundProvider;
        private final int _capacity;
        
        public AdvancedRoundMagazine(
                Supplier<? extends Item> emptyMagazineItemSupplier,
                Supplier<TRound> roundProvider,
                int capacity)
        {
            this._emptyMagazineProvider = emptyMagazineItemSupplier;
            this._roundProvider = roundProvider;
            this._capacity = capacity;
        }

        @Override
        public final int getRoundCapacity()
        {
            return this._capacity;
        }

        @Override
        public final TRound getRound()
        {
            return this._roundProvider.get();
        }

        @Override
        public final Item getEmptyMagazineItem()
        {
            return this._emptyMagazineProvider.get();
        }
    }
    
    public static final class Grenade40MM implements IGrenadeAmmo
    {
        private final Supplier<? extends IAmmoType> _typeSupplier;
        
        public Grenade40MM(Supplier<? extends IAmmoType> typeSupplier)
        {
            this._typeSupplier = typeSupplier;
        }
        
        @Override
        public final IAmmoType getType()
        {
            return this._typeSupplier.get();
        }
    }
    
    public static final class NetherCharge implements INetherChargeRoundAmmo
    {
        private final Supplier<? extends IAmmoType> _typeSupplier;
        
        public NetherCharge(Supplier<? extends IAmmoType> typeSupplier)
        {
            this._typeSupplier = typeSupplier;
        }
        
        @Override
        public final IAmmoType getType()
        {
            return this._typeSupplier.get();
        }
    }
    
    public static final class GaussRifleSlugRound implements IGaussRifleSlugRoundAmmo
    {
        private final Supplier<? extends IAmmoType> _typeSupplier;
        
        public GaussRifleSlugRound(Supplier<? extends IAmmoType> typeSupplier)
        {
            this._typeSupplier = typeSupplier;
        }
        
        @Override
        public final IAmmoType getType()
        {
            return this._typeSupplier.get();
        }
    }
}
