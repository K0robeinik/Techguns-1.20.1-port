package techguns2.item.power_hammer;

import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;

public enum PowerHammerHeads implements IPowerHammerHead
{
    DEFAULT(Tiers.IRON, 1f),
    OBSIDIAN_STEEL(Tiers.DIAMOND, 1.25f),
    CARBON(Tiers.DIAMOND, 1.35f);
    
    public final Tier tier;
    public final float digSpeedMultiplier;
    
    private PowerHammerHeads(Tier tier, float digSpeedMultiplier)
    {
        this.tier = tier;
        this.digSpeedMultiplier = digSpeedMultiplier;
    }

    @Override
    public final Tier getTier()
    {
        return this.tier;
    }
    
    @Override
    public final float getDigSpeedMultiplier()
    {
        return this.digSpeedMultiplier;
    }
}
