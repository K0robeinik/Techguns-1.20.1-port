package techguns2.machine.alloy_furnace;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import techguns2.Techguns;
import techguns2.machine.AbstractMachineScreen;

@OnlyIn(Dist.CLIENT)
public final class AlloyFurnaceScreen extends AbstractMachineScreen<AlloyFurnaceMenu>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Techguns.MODID, "textures/gui/machine/alloy_furnace_gui.png");
    
    public AlloyFurnaceScreen(AlloyFurnaceMenu menu, Inventory inventory, Component displayName)
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
        
        var data = this.menu.getData();
        
        int fillValue = data.getOperationProgressScaled(90);
        
        graphics.blit(TEXTURE, i + 19, j + 53, 0, 166, fillValue, 11);
    }
}
