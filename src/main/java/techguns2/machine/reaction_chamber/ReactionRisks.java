package techguns2.machine.reaction_chamber;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;
import techguns2.TGCustomRegistries;
import techguns2.Techguns;

public enum ReactionRisks implements StringRepresentable, ReactionRisk
{
    NONE("none", Component.translatable("techguns2.machine.reaction_chamber.risk.none").withStyle(ChatFormatting.WHITE)),
    BREAK_ITEM("break_item", Component.translatable("techguns2.machine.reaction_chamber.risk.breakItem").withStyle(ChatFormatting.GREEN)),
    SMALL_EXPLOSION("small_explosion", Component.translatable("techguns2.machine.reaction_chamber.risk.smallExplosion").withStyle(ChatFormatting.YELLOW)),
    MEDIUM_EXPLOSION("medium_explosion", Component.translatable("techguns2.machine.reaction_chamber.risk.mediumExplosion").withStyle(ChatFormatting.RED)),
    MASSIVE_EXPLOSION("massive_explosion", Component.translatable("techguns2.machine.reaction_chamber.risk.massiveExplosion").withStyle(ChatFormatting.LIGHT_PURPLE)),
    LOW_RADIATION("low_radiation", Component.translatable("techguns2.machine.reaction_chamber.risk.lowRadiation").withStyle(ChatFormatting.YELLOW)),
    MEDIUM_RADIATION("medium_radiation", Component.translatable("techguns2.machine.reaction_chamber.risk.mediumRadiation").withStyle(ChatFormatting.RED)),
    MASSIVE_RADIATION("massive_radiation", Component.translatable("techguns2.machine.reaction_chamber.risk.massiveRadiation").withStyle(ChatFormatting.LIGHT_PURPLE)),
    UNFORSEEN_CONSEQUENCES("unforseen_consequences", Component.translatable("techguns2.machine.reaction_chamber.risk.unforseenConsequences").withStyle(ChatFormatting.DARK_AQUA));
    
    private final ResourceLocation _id;
    private final Component _name;
    
    private ReactionRisks(String id, Component name)
    {
        this._id = new ResourceLocation(Techguns.MODID, id);
        this._name = name;
    }
    
    public ResourceLocation getId()
    {
        return this._id;
    }
    
    public Component getName()
    {
        return this._name;
    }
    
    @Override
    public String getSerializedName()
    {
        return this._id.toString();
    }

    
    private static boolean _registered;

    @Deprecated
    public static void register(NewRegistryEvent newRegistryEvent)
    {
        if (_registered)
            return;
        
        RegistryBuilder<ReactionRisk> builder = RegistryBuilder.of(TGCustomRegistries.REACTION_RISKS.location());
        
        newRegistryEvent.create(builder, (registry) -> {
            registry.register(NONE.getId(), NONE);
            registry.register(BREAK_ITEM.getId(), BREAK_ITEM);
            registry.register(SMALL_EXPLOSION.getId(), SMALL_EXPLOSION);
            registry.register(MEDIUM_EXPLOSION.getId(), MEDIUM_EXPLOSION);
            registry.register(MASSIVE_EXPLOSION.getId(), MASSIVE_EXPLOSION);
            registry.register(LOW_RADIATION.getId(), LOW_RADIATION);
            registry.register(MEDIUM_RADIATION.getId(), MEDIUM_RADIATION);
            registry.register(MASSIVE_RADIATION.getId(), MASSIVE_RADIATION);
            registry.register(UNFORSEEN_CONSEQUENCES.getId(), UNFORSEEN_CONSEQUENCES);
        });
        
        _registered = true;
    }
    
}
