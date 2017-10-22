package techguns;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import techguns.api.guns.GunHandType;
import techguns.entities.projectiles.AdvancedBulletProjectile;
import techguns.entities.projectiles.AlienBlasterProjectile;
import techguns.entities.projectiles.BioGunProjectile;
import techguns.entities.projectiles.BlasterProjectile;
import techguns.entities.projectiles.ChainsawProjectile;
import techguns.entities.projectiles.CyberdemonBlasterProjectile;
import techguns.entities.projectiles.DeatomizerProjectile;
import techguns.entities.projectiles.FlamethrowerProjectile;
import techguns.entities.projectiles.FragGrenadeProjectile;
import techguns.entities.projectiles.GaussProjectile;
import techguns.entities.projectiles.GenericProjectile;
import techguns.entities.projectiles.GenericProjectileIncendiary;
import techguns.entities.projectiles.Grenade40mmProjectile;
import techguns.entities.projectiles.GrenadeProjectile;
import techguns.entities.projectiles.GuidedMissileProjectile;
import techguns.entities.projectiles.LaserProjectile;
import techguns.entities.projectiles.NDRProjectile;
import techguns.entities.projectiles.PowerHammerProjectile;
import techguns.entities.projectiles.RocketProjectile;
import techguns.entities.projectiles.RocketProjectileNuke;
import techguns.entities.projectiles.SonicShotgunProjectile;
import techguns.entities.projectiles.StoneBulletProjectile;
import techguns.entities.projectiles.TeslaProjectile;
import techguns.init.ITGInitializer;
import techguns.items.guns.Chainsaw;
import techguns.items.guns.ChargedProjectileSelector;
import techguns.items.guns.GenericGrenade;
import techguns.items.guns.GenericGun;
import techguns.items.guns.GenericGunCharge;
import techguns.items.guns.GuidedMissileLauncher;
import techguns.items.guns.IProjectileFactory;
import techguns.items.guns.MiningDrill;
import techguns.items.guns.PowerHammer;
import techguns.items.guns.ProjectileSelector;
import techguns.items.guns.RangeTooltipType;
import techguns.items.guns.SonicShotgun;
import techguns.items.guns.ammo.AmmoTypes;
import techguns.tools.ItemJsonCreator;

public class TGuns implements ITGInitializer {

	private static final float RANGE_MELEE=3.0f;
	private static final float RANGE_CLOSE=12.0f;
	private static final float RANGE_SHORT=18.0f;
	private static final float RANGE_MEDIUM=24.0f;
	private static final float RANGE_FAR=36.0f;
	
	public static GenericGun handcannon;
	public static GenericGun m4;
	public static GenericGun thompson;
	public static GenericGun pistol;
	public static GenericGun lmg;
	public static GenericGun boltaction;
	public static GenericGun biogun;
	public static GenericGun rocketlauncher;
	public static GenericGun sawedoff;
	public static GenericGun flamethrower;
	public static GenericGun ak47;
	public static GenericGun minigun;
	public static GenericGrenade stielgranate;
	public static GenericGrenade fraggrenade;
	public static GenericGun combatshotgun;
	public static GenericGun revolver;
	public static GenericGun grimreaper;
	public static GenericGun pdw;
	public static GenericGun as50;
	public static GenericGun teslagun;
	public static GenericGun m4_infiltrator;
	public static GenericGun goldenrevolver;
	public static GenericGun pulserifle;
	public static GenericGun lasergun;
	public static GenericGun blasterrifle;
	public static GenericGun alienblaster;
	public static GenericGun netherblaster;
	public static GenericGun powerhammer;
	public static GenericGun grenadelauncher;
	public static GenericGun aug;
	public static GenericGun sonicshotgun;
	public static GenericGun chainsaw;
	public static GenericGun scatterbeamrifle;
	public static GenericGun nucleardeathray;
	public static GenericGun mac10;
	public static GenericGun mibgun;
	public static GenericGun vector;
	public static GenericGun scar;
	public static GenericGun gaussrifle;
	public static GenericGun guidedmissilelauncher;
	public static GenericGun miningdrill;
	
	public static ProjectileSelector<GenericProjectile> SHOTGUN_PROJECTILES;
	public static ProjectileSelector<GenericProjectile> PISTOL_PROJECTILES;
	public static ProjectileSelector<GenericProjectile> ASSAULTRIFLE_MAG_PROJECTILES;
	public static ProjectileSelector<GenericProjectile> SMG_MAG_PROJECTILES;
	public static ProjectileSelector<GenericProjectile> PISTOL_MAG_PROJECTILES;
	public static ProjectileSelector<GenericProjectile> LMG_MAG_PROJECTILES;
	public static ProjectileSelector<GenericProjectile> RIFLE_PROJECTILES;
	public static ProjectileSelector<GenericProjectile> SNIPER_MAG_PROJECTILES;
	public static ProjectileSelector<AdvancedBulletProjectile> ADVANCED_MAG_PROJECTILES;
	public static ProjectileSelector<GenericProjectile> MINIGUN_MAG_PROJECTILES;
	public static ProjectileSelector<BlasterProjectile> BLASTER_ENERGYCELL_PROJECTILES;
	
	public static ProjectileSelector<TeslaProjectile> TESLAGUN_PROJECTILES;
	public static ProjectileSelector<LaserProjectile> LASERGUN_PROJECTILES;
	public static ProjectileSelector<FlamethrowerProjectile> FLAMETHROWER_PROJECTILES;
	public static ProjectileSelector<AlienBlasterProjectile> ALIENBLASTER_PROJECTILES;
	public static ProjectileSelector<DeatomizerProjectile> DEATOMIZER_PROJECTILES;
	public static ProjectileSelector<CyberdemonBlasterProjectile> NETHERBLASTER_PROJECTILES;
	public static ProjectileSelector<GaussProjectile> GAUSS_PROJECTILES;
	public static ProjectileSelector<NDRProjectile> NDR_PROJECTILES;
	public static ProjectileSelector<Grenade40mmProjectile> GRENADE40MM_PROJECTILES;
	public static ProjectileSelector<RocketProjectile> ROCKET_PROJECTILES;
	public static ProjectileSelector<SonicShotgunProjectile> SONIC_SHOTGUN_PROJECTILES;
	public static ChargedProjectileSelector<PowerHammerProjectile> POWERHAMMER_PROJECTILES;
	public static ChargedProjectileSelector<BioGunProjectile> BIOGUN_PROJECTILES;
	public static ChargedProjectileSelector<ChainsawProjectile> CHAINSAW_PROJECTILES;
	public static ChargedProjectileSelector<GuidedMissileProjectile> GUIDED_MISSILE_PROJECTILES;
	
	public static void registerItems(RegistryEvent.Register<Item> event){
		IForgeRegistry<Item> reg = event.getRegistry();
	
		reg.register(handcannon);
		reg.register(sawedoff);
		reg.register(revolver);
		reg.register(goldenrevolver);
		reg.register(thompson);
		reg.register(ak47);
		reg.register(boltaction);
		reg.register(m4);
		reg.register(m4_infiltrator);
		reg.register(pistol);
		reg.register(combatshotgun);
		reg.register(mac10);
		reg.register(flamethrower);
		reg.register(rocketlauncher);
		reg.register(grimreaper);
		reg.register(grenadelauncher);
		reg.register(aug);
		reg.register(netherblaster);
		reg.register(biogun);
		reg.register(teslagun);
		reg.register(lmg);
		reg.register(minigun);
		reg.register(as50);
		reg.register(vector);
		reg.register(scar);
		reg.register(lasergun);
		reg.register(blasterrifle);
		reg.register(scatterbeamrifle);
		reg.register(sonicshotgun);
		reg.register(pdw);
		reg.register(pulserifle);
		reg.register(mibgun);
		reg.register(alienblaster);
		reg.register(powerhammer);
		reg.register(chainsaw);
		reg.register(nucleardeathray);
		reg.register(gaussrifle);
		reg.register(guidedmissilelauncher);
		reg.register(miningdrill);
		reg.register(stielgranate);
		reg.register(fraggrenade);
	}
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {

		IProjectileFactory<GenericProjectile> GENERIC_PROJECTILE = new GenericProjectile.Factory();
		IProjectileFactory[] GENERIC_BULLET = {GENERIC_PROJECTILE, new GenericProjectileIncendiary.Factory(false)};
		
		SHOTGUN_PROJECTILES = new ProjectileSelector<GenericProjectile>(AmmoTypes.SHOTGUN_ROUNDS, GENERIC_PROJECTILE, new GenericProjectileIncendiary.Factory(true));
		
		PISTOL_PROJECTILES = new ProjectileSelector<GenericProjectile>(AmmoTypes.PISTOL_ROUNDS, GENERIC_BULLET);
		ASSAULTRIFLE_MAG_PROJECTILES = new ProjectileSelector<GenericProjectile>(AmmoTypes.ASSAULT_RIFLE_MAGAZINE, GENERIC_BULLET);
		SMG_MAG_PROJECTILES = new ProjectileSelector<GenericProjectile>(AmmoTypes.SMG_MAGAZINE, GENERIC_BULLET);
		PISTOL_MAG_PROJECTILES = new ProjectileSelector<GenericProjectile>(AmmoTypes.PISTOL_MAGAZINE, GENERIC_BULLET);
		LMG_MAG_PROJECTILES = new ProjectileSelector<GenericProjectile>(AmmoTypes.LMG_MAGAZINE, GENERIC_BULLET);
		RIFLE_PROJECTILES = new ProjectileSelector<GenericProjectile>(AmmoTypes.RIFLE_ROUNDS, GENERIC_BULLET);
		SNIPER_MAG_PROJECTILES = new ProjectileSelector<GenericProjectile>(AmmoTypes.AS50_MAGAZINE, GENERIC_BULLET);
		ADVANCED_MAG_PROJECTILES = new ProjectileSelector<AdvancedBulletProjectile>(AmmoTypes.ADVANCED_MAGAZINE, new AdvancedBulletProjectile.Factory());
		MINIGUN_MAG_PROJECTILES = new ProjectileSelector<GenericProjectile>(AmmoTypes.MINIGUN_AMMO_DRUM, GENERIC_BULLET);
		BLASTER_ENERGYCELL_PROJECTILES = new ProjectileSelector<BlasterProjectile>(AmmoTypes.ENERGY_CELL, new BlasterProjectile.Factory());
		
		LASERGUN_PROJECTILES = new ProjectileSelector<LaserProjectile>(AmmoTypes.ENERGY_CELL, new LaserProjectile.Factory());
		TESLAGUN_PROJECTILES = new ProjectileSelector<TeslaProjectile>(AmmoTypes.ENERGY_CELL, new TeslaProjectile.Factory());
		FLAMETHROWER_PROJECTILES = new ProjectileSelector<FlamethrowerProjectile>(AmmoTypes.FUEL_TANK, new FlamethrowerProjectile.Factory());
		ALIENBLASTER_PROJECTILES =  new ProjectileSelector<AlienBlasterProjectile>(AmmoTypes.ENERGY_CELL, new AlienBlasterProjectile.Factory());
		DEATOMIZER_PROJECTILES = new ProjectileSelector<DeatomizerProjectile>(AmmoTypes.ENERGY_CELL, new DeatomizerProjectile.Factory());
		NETHERBLASTER_PROJECTILES = new ProjectileSelector<CyberdemonBlasterProjectile>(AmmoTypes.NETHER_CHARGE, new CyberdemonBlasterProjectile.Factory());
		GAUSS_PROJECTILES = new ProjectileSelector<GaussProjectile>(AmmoTypes.ENERGY_CELL, new GaussProjectile.Factory());
		NDR_PROJECTILES = new ProjectileSelector<NDRProjectile>(AmmoTypes.NUCLEAR_POWER_CELL, new NDRProjectile.Factory());
		GRENADE40MM_PROJECTILES = new ProjectileSelector<Grenade40mmProjectile>(AmmoTypes.GRENADES_40MM, new Grenade40mmProjectile.Factory());
		ROCKET_PROJECTILES = new ProjectileSelector(AmmoTypes.ROCKETS, new RocketProjectile.Factory(), new RocketProjectileNuke.Factory());
		
		SONIC_SHOTGUN_PROJECTILES = new ProjectileSelector<SonicShotgunProjectile>(AmmoTypes.ENERGY_CELL, new SonicShotgunProjectile.Factory());
		
		POWERHAMMER_PROJECTILES = new ChargedProjectileSelector<PowerHammerProjectile>(AmmoTypes.COMPRESSED_AIR_TANK, new PowerHammerProjectile.Factory());
		BIOGUN_PROJECTILES = new ChargedProjectileSelector<BioGunProjectile>(AmmoTypes.BIO_TANK, new BioGunProjectile.Factory());
		CHAINSAW_PROJECTILES = new ChargedProjectileSelector<ChainsawProjectile>(AmmoTypes.FUEL_TANK, new ChainsawProjectile.Factory());
		GUIDED_MISSILE_PROJECTILES = new ChargedProjectileSelector<GuidedMissileProjectile>(AmmoTypes.ROCKETS, new GuidedMissileProjectile.Factory(), new GuidedMissileProjectile.Factory());
		
		
		handcannon = new GenericGun("handcannon", new ProjectileSelector<StoneBulletProjectile>(AmmoTypes.STONE_BULLETS, new StoneBulletProjectile.Factory()), true, 12,1,30, 8.0f, TGSounds.HANDGUN_FIRE, TGSounds.HANDGUN_RELOAD,25,0.035f).setBulletSpeed(0.9f).setGravity(0.015d).setDamageDrop(10, 25, 5.0f).setAIStats(RANGE_CLOSE, 60, 0, 0).setTexture("textures/guns/handgun").setRecoiltime(12);//.setMuzzleParticle(2,0.2f);
		
	 	 sawedoff = new GenericGun("sawedoff",SHOTGUN_PROJECTILES, true, 4, 2, 28, 4.0f, TGSounds.SAWEDOFF_FIRE, TGSounds.SAWEDOFF_RELOAD,10, 0.01f).setAmmoCount(2).setShotgunSpread(7,0.2f,false).setDamageDrop(1, 4, 1.5f).setAIStats(RANGE_CLOSE, 60, 2, 20).setTexture("textures/guns/sawedoff").setBulletSpeed(1.5f); //.setMuzzleParticle(2,0.1f);
	 	
	   	 revolver = new GenericGun("revolver",PISTOL_PROJECTILES, true, 6, 6,45,8.0f, TGSounds.REVOLVER_FIRE, TGSounds.REVOLVER_RELOAD,25,0.025f).setDamageDrop(12, 20, 6.0f).setBulletSpeed(1.5f).setRecoiltime(6).setAIStats(RANGE_SHORT, 90, 6, 20).setTexture("textures/guns/revolver").setHandType(GunHandType.ONE_HANDED); //.setMuzzleParticle(2,0,0.05f,-0.45f);
	 	 
	 	 ak47 = new GenericGun("ak47", ASSAULTRIFLE_MAG_PROJECTILES, false, 3, 30,45,9.0f, TGSounds.AK_FIRE, TGSounds.AK_RELOAD, 35, 0.030f).setDamageDrop(20, 30, 5.0f).setPenetration(0.15f).setBulletSpeed(1.8f).setAIStats(RANGE_MEDIUM, 30, 3, 3).setTextures("textures/guns/ak47Texture",2).setMuzzleFlashTime(4).setTurretPosOffset(0, 0, 0.08f);//.setMuzzleParticle(1,0,0.05f,0.12f);
	  	 
		 m4 = new GenericGun("m4",ASSAULTRIFLE_MAG_PROJECTILES, false, 3, 30,45,8.0f, TGSounds.M4_FIRE, TGSounds.M4_RELOAD, 35, 0.015f).setBulletSpeed(2.0f).setZoom(0.75f, true,0.75f,false).setDamageDrop(25, 35, 6.0f).setAIStats(RANGE_MEDIUM, 30, 3, 3).setTextures("textures/guns/m4Texture", 4).setTurretPosOffset(0, 0, 0.08f)/*.setMuzzleParticle(1,0,0.025f,0)*/.setPenetration(0.15f).setMuzzleFlashTime(4);
		 thompson = new GenericGun("thompson",SMG_MAG_PROJECTILES, false, 3, 20,40,5.0f, TGSounds.THOMPSON_FIRE, TGSounds.THOMSPON_RELOAD,25,0.05f).setBulletSpeed(1.25f).setDamageDrop(15, 24, 3.0f).setAIStats(RANGE_SHORT, 45, 3, 3).setTexture("textures/guns/thompson").setMuzzleFlashTime(4).setTurretPosOffset(0, 0, 0.04f);//.setMuzzleParticle(1,0,0.05f,0);
		 pistol = new GenericGun("pistol",PISTOL_MAG_PROJECTILES, true, 4, 18, 35, 8.0f, TGSounds.PISTOL_FIRE, TGSounds.PISTOL_RELOAD,30, 0.025f).setBulletSpeed(1.5f).setDamageDrop(15, 22, 6.0f).setTexture("textures/guns/pistol3").setDamageDrop(18, 25, 5.0f).setAIStats(RANGE_MEDIUM, 30, 3, 10).setHandType(GunHandType.ONE_HANDED)/*.setMuzzleParticle(1,0,0.05f,-0.45f)*/.setRecoiltime(3);
		 lmg = new GenericGun("lmg",LMG_MAG_PROJECTILES,false, 2, 100,100,8.0f, TGSounds.LMG_FIRE, TGSounds.LMG_RELOAD, 50, 0.020f).setZoom(0.75f, true,0.75f,false).setDamageDrop(30, 40, 6.0f).setPenetration(0.15f).setAIStats(RANGE_MEDIUM, 40, 6, 3).setTexture("textures/guns/mg2_texture").setMuzzleFlashTime(2).setRecoiltime(3);//.setMuzzleParticle(1,0,0.1f,0.02f);
		 
		 boltaction = new GenericGun("boltaction", RIFLE_PROJECTILES, true, 25, 6, 50, 16.0f, TGSounds.BOLT_ACTION_FIRE, TGSounds.BOLT_ACTION_RELOAD,40,0.05f).setZoom(0.35f, true,0.125f,true).setBulletSpeed(2.5f).setRecoiltime(15).setRechamberSound(TGSounds.BOLT_ACTION_RECHAMBER).setDamageDrop(20, 30, 10.0f).setPenetration(0.15f).setAIStats(RANGE_FAR, 60, 0, 0).setTextures("textures/guns/boltactionrifle",3).setTurretPosOffset(0, 0, 0.14f);//.setMuzzleParticle(1,0,0.05f,0.3f);
		 
		 
		 flamethrower = new GenericGun("flamethrower", FLAMETHROWER_PROJECTILES, false, 2, 100, 45, 5.0f, TGSounds.FLAMETHROWER_FIRE, TGSounds.FLAMETHROWER_RELOAD,16,0.05f).setBulletSpeed(0.5f).setGravity(0.01d).setFiresoundStart(TGSounds.FLAMETHROWER_START).setMaxLoopDelay(10).setDamageDrop(4, 16, 2.0f).setAIStats(RANGE_CLOSE, 20, 5, 5).setTexture("textures/guns/flamethrower").setCheckRecoil().setRecoiltime(10).setCheckMuzzleFlash().setMuzzleFlashTime(10).setTurretPosOffset(0, 0, 0.1f).setForwardOffset(0.35f);   	  
		 
		 biogun = new GenericGunCharge("biogun", BIOGUN_PROJECTILES, false, 6, 30,45,10.0f, TGSounds.BIOGUN_FIRE, TGSounds.BIOGUN_RELOAD, 35, 0.015f,30.0f,3).setChargeSound(TGSounds.BIOGUN_CHARGE).setChargeFX("biogunCharge",-0.12f, -0.07f, 0.27f).setBulletSpeed(0.75f).setGravity(0.01d).setPenetration(0.15f).setTexture("textures/guns/BioGun").setAIStats(RANGE_SHORT, 30, 0, 0).setDamageDrop(8, 15, 8.0f).setMuzzleLight(0.2f, 0.9f, 0.5f).setForwardOffset(0.40f);
		   		 
		 rocketlauncher = new GenericGun("rocketlauncher", ROCKET_PROJECTILES, true, 10, 1 , 40, 50.0f, TGSounds.ROCKET_FIRE, TGSounds.ROCKET_RELOAD, 100, 0.05f).setGravity(0.01D).setBulletSpeed(1.0f).setRecoiltime(10).setAIStats(RANGE_MEDIUM,80,0,0).setTextures("textures/guns/rocketlauncher",2).setTurretPosOffset(0, 0, -0.1f).setDamageDrop(3.0f, 5.0f, 10f).setRangeTooltipType(RangeTooltipType.RADIUS).setForwardOffset(0.35f);
		   	
		 minigun = new GenericGun("minigun",MINIGUN_MAG_PROJECTILES,false, 0, 200, 100, 5.0f, TGSounds.MINIGUN_FIRE, TGSounds.MINIGUN_RELOAD, 50, 0.025f).setDamageDrop(20, 40, 3.0f).setPenetration(0.15f).setAIStats(RANGE_MEDIUM, 40, 10, 1).setTexture("textures/guns/minigun")/*.setHandType(GunHandType.ONE_HANDED)*//*.setTurretPosOffset(0, 0.20f, 0)*/.setCheckRecoil().setMuzzleFlashTime(4).setCheckMuzzleFlash().setTurretPosOffset(0, -0.49f, -0.14f);//.setRecoiltime(30).setFiresoundStart("techguns:guns.minigunStart").setMaxLoopDelay(10)
			
		 combatshotgun = new GenericGun("combatshotgun",SHOTGUN_PROJECTILES, true, 14, 8, 50, 4.0f, TGSounds.COMBATSHOTGUN_FIRE, TGSounds.COMBATSHOTGUN_RELOAD,15, 0.01f).setAmmoCount(8).setShotgunSpread(7,0.15f,false).setRecoiltime(12).setBulletSpeed(1.5f).setRechamberSound(TGSounds.COMBATSHOTGUN_RECHAMBER).setDamageDrop(2, 5, 1.5f).setPenetration(0.15f).setAIStats(RANGE_CLOSE,30,0,0).setTexture("textures/guns/combatShotgun");
		   
		 grimreaper = new GuidedMissileLauncher("grimreaper", GUIDED_MISSILE_PROJECTILES, false, 6, 4 , 40, 50.0f, TGSounds.GUIDEDMISSILE_FIRE, TGSounds.ROCKET_RELOAD, 100, 0.05f,100, 1).setFireWhileCharging(true).setChargeFireAnims(false).setBulletSpeed(1.0f).setRecoiltime(10).setAmmoCount(4).setAIStats(RANGE_MEDIUM, 120, 4, 30).setTexture("textures/guns/grimreaper").setLockOn(20, 80).setTurretPosOffset(0, 0.11f, -0.16f).setDamageDrop(3f, 5f, 10f).setRangeTooltipType(RangeTooltipType.RADIUS);
		
		 pdw = new GenericGun("pdw", ADVANCED_MAG_PROJECTILES, false, 1, 40, 40, 5.0f, TGSounds.PDW_FIRE, TGSounds.PDW_RELOAD,25,0.03f).setDamageDrop(18, 25, 3.0f).setPenetration(0.25f)/*.setShotgunSpread(1, 0.03f,true)*/.setAIStats(RANGE_SHORT, 30, 4, 2).setTextures("textures/guns/pdw",3)./*setCheckRecoil().*/setHandType(GunHandType.ONE_POINT_FIVE_HANDED).setMuzzleFlashTime(2).setMuzzleLight(0f, 0.8f, 1.0f);		 
				 
		 as50 = new GenericGun("as50", SNIPER_MAG_PROJECTILES, true, 10, 10, 80, 32.0f, TGSounds.AS50_FIRE, TGSounds.AS50_RELOAD, 50,0.0625f).setDamageDrop(30, 40, 24.0f).setZoom(0.35f, true,0.125f,true).setBulletSpeed(2.5f).setRecoiltime(10).setPenetration(0.25f).setAIStats(RANGE_FAR, 30, 0, 0).setTexture("textures/guns/as50texture").setTurretPosOffset(0, 0.03f, 0.13f);//.setMuzzleParticle(1,0,0.05f,0.3f);
	 	 
		 teslagun = new GenericGun("teslagun", TESLAGUN_PROJECTILES, false, 8, 25, 45, 12.0f, TGSounds.TESLA_FIRE, TGSounds.TESLA_RELOAD, 50/*TODO ?Teslagun.LIFETIME*/, 0.0f).setZoom(0.75f, true,1.0f,false).setBulletSpeed(80.0f/* TODO ??Lasergun.SPEED*/).setMuzzleFlashTime(10).setTexture("textures/guns/teslagun").setAIStats(RANGE_MEDIUM, 30, 0, 0).setMuzzleLight(0f, 0.8f, 1.0f);
	  	 
		 m4_infiltrator = new GenericGun("m4_infiltrator", ASSAULTRIFLE_MAG_PROJECTILES, false, 3, 30,45,8.0f, TGSounds.M4_SILENCED_FIRE, TGSounds.M4_RELOAD, 35, 0.010f).setZoom(0.50f, true,0.5f,true).setDamageDrop(25, 35, 6.0f).setSilenced(true).setAIStats(RANGE_MEDIUM, 30, 3, 3).setTexture("textures/guns/m4_uq_texture").setPenetration(0.15f).setTurretPosOffset(0, 0, 0.08f).setNoMuzzleLight();
		 
		 goldenrevolver = new GenericGun("goldenrevolver",PISTOL_PROJECTILES, true, 8, 6,45,14.0f, TGSounds.REVOLVER_GOLDEN_FIRE, TGSounds.REVOLVER_RELOAD,25,0.015f).setDamageDrop(12, 30, 6.0f).setRecoiltime(8).setBulletSpeed(1.25f).setAIStats(RANGE_MEDIUM, 60, 6, 15).setTexture("textures/guns/goldenrevolver").setHandType(GunHandType.ONE_HANDED);
		 
		 pulserifle = new GenericGun("pulserifle",ADVANCED_MAG_PROJECTILES, false, 7, 12, 45, 10.0f,  TGSounds.PULSE_RIFLE_FIRE, TGSounds.PULSE_RIFEL_RELOAD, 50,0.024f).setDamageDrop(25, 35, 8f).setZoom(0.35f, true,0.5f,true).setBulletSpeed(2.25f).setRecoiltime(8).setPenetration(0.25f).setShotgunSpread(2, 0.015f,true).setAIStats(RANGE_MEDIUM, 30, 0, 0).setTextures("textures/guns/pulserifle",3).setMuzzleFlashTime(4).setTurretPosOffset(0, 0, -0.09f).setMuzzleLight(0f, 0.8f, 1.0f);
		 
	 	 lasergun = new GenericGun("lasergun", LASERGUN_PROJECTILES, false, 5, 45, 45, 12.0f, TGSounds.LASERGUN_FIRE, TGSounds.LASERGUN_RELOAD, 20, 0.0f).setZoom(0.75f, true,0.75f,false).setBulletSpeed(100.0f).setAIStats(RANGE_MEDIUM, 30, 0, 0).setTexture("textures/guns/lasergun").setTurretPosOffset(0, 0.01f, 0.11f).setMuzzleLight(0.9f, 0.3f, 0.1f).setRangeTooltipType(RangeTooltipType.NO_DROP); //.setTexture("textures/guns/laserGunNew");//
	 	 
	 	 alienblaster = new GenericGun("alienblaster", ALIENBLASTER_PROJECTILES, false, 8, 10, 35, 16.0f, TGSounds.ALIENBLASTER_FIRE, TGSounds.ALIENBLASTER_RELOAD, 50, 0.0f).setBulletSpeed(1.0f).setMuzzleFlashTime(10).setPenetration(0.2f).setAIStats(RANGE_MEDIUM, 40, 0, 0).setTexture("textures/guns/alien_blaster").setHandType(GunHandType.ONE_HANDED).setTurretPosOffset(0, -0.03f, -0.04f).setMuzzleLight(0.925f, 0.415f, 1f);
		 
	 	 netherblaster = new GenericGun("netherblaster", NETHERBLASTER_PROJECTILES, false, 8, 10, 35, 14.0f, TGSounds.NETHERBLASTER_FIRE, TGSounds.NETHERBLASTER_RELOAD, 50, 0.0f).setBulletSpeed(1.5f).setMuzzleFlashTime(10).setPenetration(0.15f).setAIStats(RANGE_MEDIUM, 40, 0, 0).setTexture("textures/guns/cyberdemonblaster").setDamageDrop(15, 30, 8.0f).setHandType(GunHandType.ONE_POINT_FIVE_HANDED).setTurretPosOffset(0, -0.16f, 0.12f).setMuzzleLight(0.9f, 0.8f, 0.1f);
		 
	 	 blasterrifle = new GenericGun("blasterrifle", BLASTER_ENERGYCELL_PROJECTILES, false, 5, 50, 45, 10.0f, TGSounds.BLASTER_RIFLE_FIRE, TGSounds.LASERGUN_RELOAD, 50, 0.025f).setZoom(0.5f, true,0.75f,true).setAIStats(RANGE_MEDIUM, 30, 5, 3).setTexture("textures/guns/blasterrifle").setDamageDrop(25, 35, 8.0f).setPenetration(0.2f).setMuzzleLight(0.9f, 0.3f, 0.1f);
	 	 
		 powerhammer = new PowerHammer("powerhammer", POWERHAMMER_PROJECTILES, false, 4, 20, 45, 2.5f, TGSounds.POWERHAMMER_FIRE, TGSounds.POWERHAMMER_RELOAD,2,0.0f,20f,5).setMeleeDmg(6.0f, 2.0f).setTool("pickaxe", 2).setTool("shovel", 2).setDigSpeed(12.0f).setChargeSound(TGSounds.POWERHAMMER_CHARGE).setBulletSpeed(1.0f).setTexture("textures/guns/powerHammer").setRecoiltime(12).setShootWithLeftClick(false).setAIStats(RANGE_MELEE, 30, 0, 0).setDamageDrop(3, 3, 2.5f).setNoMuzzleLight();
		 
		 grenadelauncher = new GenericGun("grenadelauncher", GRENADE40MM_PROJECTILES, true, 5, 6, 100, 30.0f, TGSounds.GRENADE_LAUNCHER_FIRE, TGSounds.GRENADE_LAUNCHER_RELOAD, 160, 0.015f).setTexture("textures/guns/grenadelauncher").setBulletSpeed(0.5f).setAIStats(RANGE_MEDIUM, 40, 3, 20).setAmmoCount(6).setDamageDrop(4.0f, 8.0f, 12f).setGravity(0.01d).setRangeTooltipType(RangeTooltipType.RADIUS);
			
		 aug = new GenericGun("aug", ASSAULTRIFLE_MAG_PROJECTILES, false, 3, 30,45,8.0f, TGSounds.AUG_FIRE, TGSounds.AUG_RELOAD, 35, 0.010f).setZoom(0.50f, true,0.5f,true).setDamageDrop(25, 40, 7.0f).setAIStats(RANGE_MEDIUM, 30, 3, 3).setTextures("textures/guns/AugTexture",5).setPenetration(0.15f).setMuzzleFlashTime(4);
		 sonicshotgun = new SonicShotgun("sonicshotgun",SONIC_SHOTGUN_PROJECTILES,true, 12, 8, 40, 25.0f, TGSounds.SONIC_SHOTGUN_FIRE, TGSounds.SONIC_SHOTGUN_RELOAD,20,0.0f).setDamageDrop(5, 15, 5.0f).setPenetration(0.25f).setAIStats(RANGE_SHORT, 40, 0, 0).setTexture("textures/guns/sonicshotgun");
		 chainsaw = new Chainsaw("chainsaw", CHAINSAW_PROJECTILES, false, 3, 300, 45, 10.0f, TGSounds.CHAINSAW_LOOP, TGSounds.POWERHAMMER_RELOAD, 2, 0.0f,1f,1).setMeleeDmg(12.0f, 2.0f).setTool("axe", 3).setDigSpeed(14.0f).setTexture("textures/guns/chainsaw").setRecoiltime(5).setShootWithLeftClick(false).setFiresoundStart(TGSounds.CHAINSAW_LOOP_START).setMaxLoopDelay(10).setPenetration(0.25f).setAIStats(RANGE_MELEE, 10, 0, 0).setTurretPosOffset(0, -0.47f, -0.08f).setNoMuzzleLight(); //.setTurretPosOffset(0, 0.50f, 0);
		 scatterbeamrifle = new GenericGun("scatterbeamrifle", BLASTER_ENERGYCELL_PROJECTILES, false, 7, 40, 45, 6.0f, TGSounds.LASERGUN_FIRE, TGSounds.LASERGUN_RELOAD, 30/*TODO?Lasergun.LIFETIME*/, 0.1f).setShotgunSpread(4,0.15f,false).setBulletSpeed(1.5f).setZoom(0.75f, true,0.75f,false).setBulletSpeed(2.0f).setAIStats(RANGE_SHORT, 30, 0, 0).setTexture("textures/guns/lasergunnew").setMuzzleLight(0.9f, 0.3f, 0.1f);
	     nucleardeathray = new GenericGun("nucleardeathray", NDR_PROJECTILES, false, 5, 40, 50, 6.0f, TGSounds.BEAMGUN_FIRE, TGSounds.LASERGUN_RELOAD, 10/* TODO? Beamgun.LIFETIME*/, 0.0f).setFiresoundStart(TGSounds.BEAMGUN_START).setMaxLoopDelay(10).setRecoiltime(10).setCheckRecoil().setBulletSpeed(100.0f).setAIStats(RANGE_MEDIUM, 40, 5, 5).setTexture("textures/guns/ndr").setPenetration(0.35f).setDamageDrop(20, 40, 1.0f).setHandType(GunHandType.TWO_HANDED).setTurretPosOffset(0, 0.04f, -0.19f);//.setCheckRecoil();  
		 
	     mac10 = new GenericGun("mac10",SMG_MAG_PROJECTILES, false, 2, 32,40,5.0f, TGSounds.MAC10_FIRE, TGSounds.M4_RELOAD,25,0.05f).setDamageDrop(15, 24, 3.0f).setAIStats(RANGE_SHORT, 35, 3, 2).setTexture("textures/guns/mac10texture").setRecoiltime(2).setMuzzleFlashTime(3).setBulletSpeed(1.5f).setHandType(GunHandType.ONE_POINT_FIVE_HANDED).setTurretPosOffset(0, 0, -0.07f);
		 mibgun = new GenericGun("mibgun", DEATOMIZER_PROJECTILES, true, 8, 20, 45, 16.0f, TGSounds.MIBGUN_FIRE, TGSounds.MIBGUN_RELOAD, 50, 0.035f).setAIStats(RANGE_MEDIUM, 60, 0, 0).setTexture("textures/guns/mibgun").setDamageDrop(20, 30, 8.0f).setBulletSpeed(1.5f).setPenetration(0.2f).setHandType(GunHandType.ONE_HANDED).setTurretPosOffset(0, -0.04f, 0f).setMuzzleLight(0.3333f, 0.9f, 1f);
		 scar = new GenericGun("scar", ASSAULTRIFLE_MAG_PROJECTILES, false, 4, 20,45,12.0f, TGSounds.SCAR_FIRE, TGSounds.SCAR_RELOAD, 50, 0.015f).setZoom(0.65f, true,0.5f,true).setDamageDrop(30, 40, 10.0f).setAIStats(RANGE_MEDIUM, 30, 5, 2).setTextures("textures/guns/scar_texture", 2).setPenetration(0.25f).setBulletSpeed(2.25f).setMuzzleFlashTime(5).setTurretPosOffset(0, 0.02f, 0.09f);
		 vector = new GenericGun("vector",SMG_MAG_PROJECTILES, false, 2, 25,40,6.0f, TGSounds.VECTOR_FIRE, TGSounds.VECTOR_RELOAD,30,0.05f).setZoom(0.75f, true,0.35f,false).setDamageDrop(17, 25, 4.0f).setBulletSpeed(1.5f).setAIStats(RANGE_SHORT, 35, 3, 2).setTextures("textures/guns/vector_texture",2).setRecoiltime(2).setMuzzleFlashTime(3).setPenetration(0.15f).setTurretPosOffset(0, -0.1f, 0.15f);
		
		 gaussrifle = new GenericGun("gaussrifle", GAUSS_PROJECTILES, true, 30, 8, 60, 40.0f, TGSounds.GAUSS_RIFLE_FIRE, TGSounds.GAUSS_RIFLE_RELOAD, 100, 0.025f).setZoom(0.35f, true,0.0f,true).setBulletSpeed(5.0f).setAIStats(RANGE_FAR, 30, 0, 0).setRechamberSound(TGSounds.GAUSS_RIFLE_RECHAMBER).setRecoiltime(8).setTextures("textures/guns/gaussrifle",2).setTurretPosOffset(0, -0.02f, 0.12f).setMuzzleLight(0f, 0.8f, 1.0f).setForwardOffset(0.45f);//
		 
		 guidedmissilelauncher = new GuidedMissileLauncher("guidedmissilelauncher", GUIDED_MISSILE_PROJECTILES, true, 10, 1 , 40, 50.0f, TGSounds.GUIDEDMISSILE_FIRE, TGSounds.ROCKET_RELOAD, 100, 0.05f, 100, 1).setFireWhileCharging(true).setChargeFireAnims(false).setBulletSpeed(1.0f).setRecoiltime(10).setAIStats(RANGE_MEDIUM,80,0,0).setTexture("textures/guns/guidedmissilelauncher").setLockOn(20, 80).setTurretPosOffset(0, 0.01f, -0.12f).setDamageDrop(2f, 4f, 20f).setRangeTooltipType(RangeTooltipType.RADIUS); //.setHandType(GunHandType.ONE_HANDED);/*.setTurretPosOffset(0f, -0.7f, -0.2f);*/
	     
		 miningdrill = new MiningDrill("miningdrill", CHAINSAW_PROJECTILES, false, 3, 300, 45, 10.0f, TGSounds.CHAINSAW_LOOP, TGSounds.POWERHAMMER_RELOAD, 2, 0.0f,1f,1).setMeleeDmg(12.0f, 2.0f).setTool("pickaxe", 3).setTool("shovel", 3).setDigSpeed(14.0f).setMiningRadius(1).setTexture("textures/guns/mining_drill").setRecoiltime(5).setShootWithLeftClick(false).setFiresoundStart(TGSounds.CHAINSAW_LOOP_START).setMaxLoopDelay(10).setPenetration(0.25f).setAIStats(RANGE_MELEE, 10, 0, 0).setTurretPosOffset(0, -0.47f, -0.08f).setNoMuzzleLight(); //.setTurretPosOffset(0, 0.50f, 0);
			
		 
		 stielgranate = new GenericGrenade("stielgranate", 16, 72000, new GrenadeProjectile.Factory()).setDamageAndRadius(10, 3.0f, 5, 5.0f);
		 
		 fraggrenade = new GenericGrenade("fraggrenade", 16, 72000, new FragGrenadeProjectile.Factory()).setStartSound(TGSounds.GRENADE_PIN).setDamageAndRadius(12, 3.5f, 6, 7.0f);
		 
		 if(TGItems.WRITE_ITEM_JSON && event.getSide()==Side.CLIENT){
			GenericGun.guns.forEach(g -> ItemJsonCreator.writeJsonFilesForGun(g));
		 }
	}

	@SideOnly(Side.CLIENT)
    public static void initModels() {
	//	ModelLoader.setCustomModelResourceLocation(grenadelauncher, 0, new ModelResourceLocation(grenadelauncher.getRegistryName(), "inventory"));
	//	ModelLoader.setCustomModelResourceLocation(grenadelauncher, 1, new ModelResourceLocation(grenadelauncher.getRegistryName()+"_1", "inventory"));
		
		//ModelLoader.setCustomModelResourceLocation(gaussrifle, 0, new ModelResourceLocation(gaussrifle.getRegistryName(), "inventory"));
    }
	
	@Override
	public void init(FMLInitializationEvent event) {
		
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {

	}
	
}
