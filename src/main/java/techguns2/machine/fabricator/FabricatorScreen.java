package techguns2.machine.fabricator;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import techguns2.Techguns;
import techguns2.machine.AbstractMachineScreen;

@OnlyIn(Dist.CLIENT)
public final class FabricatorScreen extends AbstractMachineScreen<FabricatorMenu>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Techguns.MODID, "textures/gui/machine/fabricator_gui.png");
    
    public FabricatorScreen(FabricatorMenu menu, Inventory inventory, Component name)
    {
        super(menu, inventory, name);
    }

    @Override
    protected final void renderBg(GuiGraphics graphics, float p_97788_, int mouseX, int mouseY)
    {
        this.renderBackground(graphics);
        
        int i = this.leftPos;
        int j = this.topPos;
        
        graphics.blit(TEXTURE, i, j, 0, 0, this.imageWidth, this.imageHeight);
        
        this.renderComponentFG(graphics, i, mouseX, mouseY);
        
        int fillValue = this.menu.getData().getOperationProgressScaled(89);
        
        graphics.blit(TEXTURE, i + 21, j + 53, 0, 167, fillValue + 1, 11);
    }
}
