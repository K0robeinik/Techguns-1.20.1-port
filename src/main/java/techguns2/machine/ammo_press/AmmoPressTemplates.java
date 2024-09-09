package techguns2.machine.ammo_press;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;
import techguns2.TGCustomRegistries;
import techguns2.Techguns;

public final class AmmoPressTemplates
{
    private AmmoPressTemplates()
    { }
    
    private static boolean _registered;
    
    public static final AmmoPressTemplate PISTOL_ROUNDS = AmmoPressTemplate.builder()
            .withTranslatableName("techguns2.machine.ammo_press.template.pistol_rounds")
            .build();
    public static final AmmoPressTemplate SHOTGUN_SHELLS = AmmoPressTemplate.builder()
            .withTranslatableName("techguns2.machine.ammo_press.template.shotgun_shells")
            .build();
    public static final AmmoPressTemplate RIFLE_ROUNDS = AmmoPressTemplate.builder()
            .withTranslatableName("techguns2.machine.ammo_press.template.rifle_rounds")
            .build();
    public static final AmmoPressTemplate SNIPER_ROUNDS = AmmoPressTemplate.builder()
            .withTranslatableName("techguns2.machine.ammo_press.template.sniper_rounds")
            .build();
    
    public static final AmmoPressTemplate DEFAULT = PISTOL_ROUNDS;
    
    private static AmmoPressTemplate[] VALUES = new AmmoPressTemplate[0];
    
    public static AmmoPressTemplate getFromIndex(int index)
    {
        if (index < 0 || index >= VALUES.length)
            return DEFAULT;
        else
            return VALUES[index];
    }
    
    @Deprecated
    public static void register(NewRegistryEvent newRegistryEvent)
    {
        if (_registered)
            return;
        
        RegistryBuilder<AmmoPressTemplate> builder = RegistryBuilder.of(TGCustomRegistries.AMMO_PRESS_TEMPLATES.location());
        builder.onBake((registry, manager) -> {
            
            var registryEntries = registry.getEntries();
            var entryIterator = registryEntries.iterator();
            
            if (!entryIterator.hasNext())
                return;

            VALUES = new AmmoPressTemplate[registryEntries.size()];
            var entry = entryIterator.next();
            
            AmmoPressTemplate entryTemplate = entry.getValue();
            
            VALUES[0] = entryTemplate;
            
            AmmoPressTemplate first = entryTemplate;
            AmmoPressTemplate last = first;
            
            entryTemplate.setId(entry.getKey().location());
            entryTemplate.setIndex(0);
            
            int index = 1;
            
            while (entryIterator.hasNext())
            {
                entry = entryIterator.next();
                entryTemplate = entry.getValue();
                
                VALUES[index] = entryTemplate;
                
                entryTemplate.setIndex(index++);
                entryTemplate.setId(entry.getKey().location());
                
                entryTemplate.setPreviousTemplate(last);
                last.setNextTemplate(entryTemplate);
                
                last = entryTemplate;
            }
            
            first.setPreviousTemplate(last);
            last.setNextTemplate(first);
        });
        
        newRegistryEvent.create(builder, (registry) -> {
            registry.register(new ResourceLocation(Techguns.MODID, "pistol_rounds"), PISTOL_ROUNDS);
            registry.register(new ResourceLocation(Techguns.MODID, "shotgun_shells"), SHOTGUN_SHELLS);
            registry.register(new ResourceLocation(Techguns.MODID, "rifle_rounds"), RIFLE_ROUNDS);
            registry.register(new ResourceLocation(Techguns.MODID, "sniper_rounds"), SNIPER_ROUNDS);
        });
        
        _registered = true;
    }
}
