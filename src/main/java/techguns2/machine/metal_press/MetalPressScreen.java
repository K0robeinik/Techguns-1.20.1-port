package techguns2.machine.metal_press;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import techguns2.TGTranslations;
import techguns2.Techguns;
import techguns2.machine.AbstractMachineScreen;
import techguns2.networking.TechgunsPacketHandler;
import techguns2.networking.packets.ChangeMetalPressStackSplitModePacket;

@OnlyIn(Dist.CLIENT)
public final class MetalPressScreen extends AbstractMachineScreen<MetalPressMenu>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Techguns.MODID, "textures/gui/machine/metal_press_gui.png");
    
    private Button _stackSplitModeButton;

    public MetalPressScreen(MetalPressMenu menu, Inventory inventory, Component name)
    {
        super(menu, inventory, name);
    }
    
    @Override
    protected final void init()
    {
        super.init();

        this._stackSplitModeButton = Button.builder(Component.translatable(TGTranslations.Machine.MetalPress.StackSplit.SWITCH), this)
                .pos(this.leftPos + 20, this.topPos + 50)
                .size(40, 20)
                .build();
        
        this.addRenderableWidget(this._stackSplitModeButton);
    }
    
    @Override
    public final void onPress(Button button)
    {
        if (button == this._stackSplitModeButton)
            this.onStackSplitModeButtonPressed();
        else
            super.onPress(button);
    }
    
    private final void onStackSplitModeButtonPressed()
    {
        var data = this.menu.getData();
        
        boolean enabled = !data.splitItems();
        data.setSplitItems(enabled);
        
        TechgunsPacketHandler.INSTANCE.sendToServer(new ChangeMetalPressStackSplitModePacket(enabled));
    }

    @Override
    protected final void renderLabels(GuiGraphics graphics, int p_282681_, int p_283686_)
    {
        super.renderLabels(graphics, p_282681_, p_283686_);
        
        graphics.drawString(this.font, Component.translatable(TGTranslations.Machine.MetalPress.StackSplit.TITLE), 20, 30, 4210752, false);
        
        boolean splitItems = this.menu.getData().splitItems();
        
        graphics.drawString(this.font, Component.translatable(splitItems ?
                    TGTranslations.Machine.MetalPress.StackSplit.ON :
                    TGTranslations.Machine.MetalPress.StackSplit.OFF), 
                20, 40, 4210752, false);
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
        
        graphics.blit(TEXTURE, i + 119, j + 36, 176, 0, 18, fillValue);
    }

}
