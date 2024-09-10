package techguns2.machine.ammo_press;

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
import techguns2.networking.packets.ChangeAmmoPressTemplatePacket;

@OnlyIn(Dist.CLIENT)
public final class AmmoPressScreen extends AbstractMachineScreen<AmmoPressMenu>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Techguns.MODID, "textures/gui/machine/ammo_press_gui.png");
    
    private Button _previousTemplateButton;
    private Button _nextTemplateButton;

    public AmmoPressScreen(AmmoPressMenu p_97741_, Inventory p_97742_, Component p_97743_)
    {
        super(p_97741_, p_97742_, p_97743_);
    }
    
    @Override
    protected void init()
    {
        super.init();

        this._previousTemplateButton = Button.builder(Component.literal("<"), this)
                .pos(this.leftPos + 20, this.topPos + 50)
                .size(20, 20)
                .build();
        this._nextTemplateButton = Button.builder(Component.literal(">"), this)
                .pos(this.leftPos + 40, this.topPos + 50)
                .size(20, 20)
                .build();
        
        this.addRenderableWidget(this._previousTemplateButton);
        this.addRenderableWidget(this._nextTemplateButton);
    }
    
    @Override
    public final void onPress(Button button)
    {
        if (button == this._previousTemplateButton)
            this.onPreviousTemplateButtonPressed();
        else if (button == this._nextTemplateButton)
            this.onNextTemplateButtonPressed();
        else
            super.onPress(button);
    }
    
    private final void onPreviousTemplateButtonPressed()
    {
        var data = this.menu.getData();
        
        var previousTemplate = data.getTemplate().getPreviousTemplate();
        data.setTemplate(previousTemplate);
        
        TechgunsPacketHandler.INSTANCE.sendToServer(new ChangeAmmoPressTemplatePacket(previousTemplate));
    }
    
    private final void onNextTemplateButtonPressed()
    {
        var data = this.menu.getData();
        
        var nextTemplate = data.getTemplate().getNextTemplate();
        data.setTemplate(nextTemplate);
        
        TechgunsPacketHandler.INSTANCE.sendToServer(new ChangeAmmoPressTemplatePacket(nextTemplate));
    }

    @Override
    protected final void renderLabels(GuiGraphics graphics, int p_282681_, int p_283686_)
    {
        super.renderLabels(graphics, p_282681_, p_283686_);
        
        graphics.drawString(this.font, Component.translatable(TGTranslations.Machine.AmmoPress.TEMPLATE_TITLE), 20, 30, 4210752, false);
        
        var template = this.menu.getData().getTemplate();
        if (template == null)
        {
            template = AmmoPressTemplates.DEFAULT;
        }
        
        graphics.drawString(this.font, template.getName(), 20, 40, 4210752, false);
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
        
        int fillValue = data.getOperationProgressScaled(21);
        
        graphics.blit(TEXTURE, i + 119, j + 36, 176, 0, 18, fillValue);
    }

}
