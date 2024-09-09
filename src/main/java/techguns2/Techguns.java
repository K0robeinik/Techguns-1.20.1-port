package techguns2;

import java.util.UUID;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import techguns2.api.npc.factions.FactionNeutrality;
import techguns2.api.npc.factions.ITGPlayerFactionsHandler;
import techguns2.networking.TechgunsPacketHandler;
import techguns2.util.EmptyFluidTagCondition;
import techguns2.util.LavaRecipeCondition;

@Mod(Techguns.MODID)
public class Techguns
{
    public static final String MODID = "techguns2";
    
    public static final Logger LOGGER = LogUtils.getLogger();
    
    public static ITGPlayerFactionsHandler FACTIONS_HANDLER = new NoFactionsHandler();
    
    @SuppressWarnings("deprecation")
    public Techguns()
    {
        MinecraftForge.EVENT_BUS.register(this);
        
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        for (int index = 0; index < INITIALIZERS.length; index++)
        {
            INITIALIZERS[index].setup(modEventBus);
        }
        
        TechgunsPacketHandler.init();
        
        modEventBus.addListener(LavaRecipeCondition::register);
        modEventBus.addListener(EmptyFluidTagCondition::register);
        
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, TGConfig.SPEC);
    }
    
    private static final TGInitializer[] INITIALIZERS = new TGInitializer[]
    {
        //new TGAmmoCategories(),
        //new TGAmmoVariants(),
        new TGAmmoTypes(),
        new TGAmmo(),
        new TGAmmoGroups(),
        new TGCustomRegistries(),
        new TGCreativeModeTabs(),
        new TGItems(),
        new TGBlocks(),
        new TGTags(),
        new TGRecipeTypes(),
        new TGRecipeSerializers(),
        new TGMenus(),
        new TGBlockEntityTypes(),
        new TGSounds(),
        new TGIngredientsInitializer()
    };
    
    public static final class NoFactionsHandler implements ITGPlayerFactionsHandler
    {

        @Override
        public boolean isEnemy(UUID owner, UUID target)
        {
            return false;
        }

        @Override
        public boolean isAlliedOrTeamMember(UUID owner, UUID target)
        {
            return true;
        }

        @Override
        public boolean isAlliedNoTeamMember(UUID owner, UUID target)
        {
            return true;
        }

        @Override
        public boolean isTeamMember(UUID owner, UUID target)
        {
            return true;
        }

        @Override
        public FactionNeutrality getNeutrality(UUID player1, UUID player2)
        {
            if (player1.equals(player2))
                return FactionNeutrality.PASSIVE;
            else
                return FactionNeutrality.NEUTRAL;
        }
        
    }
}
