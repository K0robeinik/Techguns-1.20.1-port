package techguns2.machine;

import java.util.List;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.UsernameCache;
import techguns2.TGTranslations;
import techguns2.Techguns;
import techguns2.networking.TechgunsPacketHandler;
import techguns2.networking.packets.ChangeRedstoneModePacket;
import techguns2.networking.packets.ChangeSecurityModePacket;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractMachineScreen<T extends AbstractMachineMenu> extends AbstractContainerScreen<T> implements Button.OnPress
{
    private static final ResourceLocation COMMON_TEXTURE = new ResourceLocation(Techguns.MODID, "textures/gui/machine/common.png");
    private Button _redstoneModeButton = null;
    private Button _securityModeButton = null;
    
    public AbstractMachineScreen(T menu, Inventory inventory, Component name)
    {
        super(menu, inventory, name);
    }
    
    @Override
    protected void init()
    {
        super.init();
        
        this._redstoneModeButton = Button.builder(CommonComponents.EMPTY, this)
                .pos(this.leftPos - 22, this.topPos + 40)
                .size(20, 20)
                .build();
        this._securityModeButton = Button.builder(CommonComponents.EMPTY, this)
                .pos(this.leftPos - 22, this.topPos + 10)
                .size(20, 20)
                .build();
        
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
        
        this.addRenderableWidget(this._redstoneModeButton);
        this.addRenderableWidget(this._securityModeButton);
    }
    
    @Override
    public void onPress(Button button)
    {
        if (button == this._redstoneModeButton)
            this.onRedstoneBehaviourButtonPressed();
        else if (button == this._securityModeButton)
            this.onSecurityModeButtonPressed();
    }
    
    private final void onRedstoneBehaviourButtonPressed()
    {
        var data = this.menu.getData();
        
        var newRedstoneMode = data.getRedstoneBehaviour().getNext();
        
        TechgunsPacketHandler.INSTANCE.sendToServer(new ChangeRedstoneModePacket(newRedstoneMode));
        
        data.setRedstoneBehaviour(newRedstoneMode);
    }
    
    private final void onSecurityModeButtonPressed()
    {
        var data = this.menu.getData();
        
        var newSecurityMode = data.getSecurityMode().getNext();
        
        TechgunsPacketHandler.INSTANCE.sendToServer(new ChangeSecurityModePacket(newSecurityMode));
        
        data.setSecurityMode(newSecurityMode);
    }
    
    @Override
    public void render(GuiGraphics p_283479_, int p_283661_, int p_281248_, float p_281886_)
    {
        super.render(p_283479_, p_283661_, p_281248_, p_281886_);
        
        this.renderFG(p_283479_, p_281886_, p_283661_, p_281248_);
        
        this.renderTooltip(p_283479_, p_283661_, p_281248_);
    }
    
    protected void renderFG(GuiGraphics graphics, float ticks, int mouseX, int mouseY)
    {
        var data = this.menu.getData();

        int i = this.leftPos;
        int j = this.topPos;
        
        graphics.blit(COMMON_TEXTURE, i - 20, j + 12, 16, 16 * data.getSecurityMode().value(), 16, 16);
        graphics.blit(COMMON_TEXTURE, i - 20, j + 42, 0, 16 * data.getRedstoneBehaviour().value(), 16, 16);
    }

    protected final void renderComponentFG(GuiGraphics graphics, float ticks, int mouseX, int mouseY)
    {
        int i = this.leftPos;
        int j = this.topPos;
        
        AbstractMachineContainerData data = this.menu.getData();
        
        graphics.blit(COMMON_TEXTURE, i - 27, j + 5, 64, 0, 27, 30);
        
        graphics.blit(COMMON_TEXTURE, i - 27, j + 35, 64, 0, 27, 30);
        
        graphics.blit(COMMON_TEXTURE, i + 6, j + 5, 48, data.isActive() ? 5 : 0, 10, 5);

        if (data.getEnergyCapacity() == 0)
        {
            // delete system 32 because what the fuck???
            return;
        }
        int power = (data.getStoredEnergy() * 48) / data.getEnergyCapacity();
        int diff = 48 - power;
        
        graphics.blit(COMMON_TEXTURE, i + 7, j + 17 + diff, 32, 1 + diff, 6, power + 1);
    }
    
    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY)
    {
        super.renderTooltip(graphics, mouseX, mouseY);
        
        int mx = mouseX - this.leftPos;
        int my = mouseY - this.topPos;
        
        AbstractMachineContainerData data = this.menu.getData();
        
        if ((mx >= 6 && mx <= (15 /* 10 + 5 */) && my >= 5 && my <= (10 /* 5 + 5 */)) ||
            (mx >= -22 && mx <= (2 /* -22 + 20 */) && my >= 40 && my <= (60 /* 40 + 20 */)))
        {
            var redstoneBehaviour = data.getRedstoneBehaviour();
            
            graphics.renderComponentTooltip(this.font, List.of(
                        Component.translatable(TGTranslations.Machine.REDSTONE_BEHAVIOUR_TOOLTIP),
                        Component.translatable(TGTranslations.Machine.REDSTONE_BEHAVIOUR_MODE_TOOLTIP, redstoneBehaviour.getName()),
                        Component.translatable(TGTranslations.Machine.REDSTONE_BEHAVIOUR_SIGNAL_TOOLTIP, redstoneBehaviour.getSignalName())),
                    mouseX, mouseY);
        }
        
        if (mx >= -22 && mx <= (-2 /* -22 + 20 */) && my >= 10 && my <= (30 /* 10 + 20 */))
        {
            @Nullable
            UUID owner = this.menu.getOwnerId();
            
            Component ownerName;
            if (owner == null)
            {
                ownerName = Component.translatable(TGTranslations.Machine.SECURITY_MODE_OWNED_BY_NO_OWNER_TOOLTIP);
            }
            else
            {
                String fetchedOwnerName = UsernameCache.getLastKnownUsername(owner);
                if (fetchedOwnerName == null)
                    ownerName = Component.translatable(TGTranslations.Machine.SECURITY_MODE_OWNED_BY_UNKNOWN_OWNER_TOOLTIP, owner.toString());
                else
                    ownerName = Component.literal(fetchedOwnerName);
            }
            
            var securityMode = data.getSecurityMode();
            graphics.renderComponentTooltip(this.font, List.of(
                        Component.translatable(TGTranslations.Machine.SECURITY_MODE_OWNED_BY_TOOLTIP, ownerName),
                        Component.translatable(TGTranslations.Machine.SECURITY_MODE_TOOLTIP, securityMode.getName()),
                        securityMode.getDescription(),
                        Component.translatable(TGTranslations.Machine.SECURITY_MODE_DESCRIPTION_TOOLTIP)),
                    mouseX, mouseY);
        }
        
        if (mx >= 7 && mx <= (12 /* 7 + 5 */) && my >= 17 && my <= (66 /* 17 + 49 */))
        {
            graphics.renderTooltip(this.font, Component.translatable(TGTranslations.Machine.ENERGY_TOOLTIP, 
                    data.getStoredEnergy(),
                    data.getEnergyCapacity()), mouseX, mouseY);
        }
    }
}
