package techguns2.machine.grinder;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import techguns2.Techguns;
import techguns2.machine.AbstractMachineScreen;

public final class GrinderScreen extends AbstractMachineScreen<GrinderMenu>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Techguns.MODID, "textures/gui/machine/grinder_gui.png");
    
    public GrinderScreen(GrinderMenu menu, Inventory inventory, Component displayName)
    {
        super(menu, inventory, displayName);
    }
    
    @Override
    protected final void renderBg(GuiGraphics graphics, float p_97788_, int mouseX, int mouseY)
    {
        this.renderBackground(graphics);
        
        int i = this.leftPos;
        int j = this.topPos;
        
        graphics.blit(TEXTURE, i, j, 0, 0, this.imageWidth, this.imageHeight);
        
        this.renderComponentFG(graphics, i, mouseX, mouseY);

        int fillValue = this.menu.getData().getOperationProgressScaled(21);
        int fillValueInverse = 21 - fillValue;
        
        graphics.blit(TEXTURE, i + 31, j + 39, 0, 167, fillValue + 1, 22);
        graphics.blit(TEXTURE, i + 31 + 20 + fillValueInverse, j + 39, 0 + fillValueInverse, 167, fillValue + 1, 22);
    }
}
