package techguns2.machine.reaction_chamber;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import techguns2.TGTranslations;
import techguns2.Techguns;
import techguns2.machine.AbstractMachineScreen;
import techguns2.networking.TechgunsPacketHandler;
import techguns2.networking.packets.ChangeReactionFluidLevelPacket;
import techguns2.networking.packets.ChangeReactionIntensityPacket;
import techguns2.networking.packets.DumpFluidTankPacket;
import techguns2.util.GuiUtils;

@OnlyIn(Dist.CLIENT)
public final class ReactionChamberScreen extends AbstractMachineScreen<ReactionChamberMenu>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Techguns.MODID, "textures/gui/machine/reaction_chamber_gui.png");

    private Button _increaseIntensityButton = null;
    private Button _decreaseIntensityButton = null;
    private Button _increaseFluidLevelButton = null;
    private Button _decreaseFluidLevelButton = null;
    private Button _dumpTankButton = null;
    
    public ReactionChamberScreen(ReactionChamberMenu menu, Inventory inventory, Component name)
    {
        super(menu, inventory, name);
    }
    
    @Override
    protected void init()
    {
        super.init();
        
        this._increaseIntensityButton = Button.builder(Component.literal("+"), this)
                .pos(this.leftPos + 91, this.topPos + 36)
                .size(10, 10)
                .build();
        
        this._decreaseIntensityButton = Button.builder(Component.literal("-"), this)
                .pos(this.leftPos + 91 + 10, this.topPos + 36)
                .size(10, 10)
                .build();
        
        this._increaseFluidLevelButton = Button.builder(Component.literal("+"), this)
                .pos(this.leftPos + 33, this.topPos + 58)
                .size(10, 10)
                .build();
        
        this._decreaseFluidLevelButton = Button.builder(Component.literal("-"), this)
                .pos(this.leftPos + 33 + 10, this.topPos + 58)
                .size(10, 10)
                .build();
        
        this._dumpTankButton = Button.builder(Component.literal("x"), this)
                .pos(this.leftPos + 20, this.topPos + 7)
                .size(8, 8)
                .build();
        
        this.addRenderableWidget(this._increaseIntensityButton);
        this.addRenderableWidget(this._decreaseIntensityButton);
        this.addRenderableWidget(this._increaseFluidLevelButton);
        this.addRenderableWidget(this._decreaseFluidLevelButton);
        this.addRenderableWidget(this._dumpTankButton);
    }
    
    @Override
    public final void onPress(Button button)
    {
        if (button == this._increaseIntensityButton)
            this.onIncreaseIntensityButtonPressed();
        else if (button == this._decreaseIntensityButton)
            this.onDecreaseIntensityButtonPressed();
        else if (button == this._increaseFluidLevelButton)
            this.onIncreaseFluidLevelButtonPressed();
        else if (button == this._decreaseFluidLevelButton)
            this.onDecreaseFluidLevelButtonPressed();
        else if (button == this._dumpTankButton)
            this.onDumpTankButtonPressed();
        else
            super.onPress(button);
    }
    
    private final void onIncreaseIntensityButtonPressed()
    {
        var data = this.menu.getData();
        
        int intensity = Math.min(10, data.getIntensity() + 1);
        if (intensity == data.getIntensity())
            return;
        
        data.setIntensity(intensity);
        TechgunsPacketHandler.INSTANCE.sendToServer(new ChangeReactionIntensityPacket(intensity));
    }
    
    private final void onDecreaseIntensityButtonPressed()
    {
        var data = this.menu.getData();
        
        int intensity = Math.max(0, data.getIntensity() - 1);
        if (intensity == data.getIntensity())
            return;
        
        data.setIntensity(intensity);
        TechgunsPacketHandler.INSTANCE.sendToServer(new ChangeReactionIntensityPacket(intensity));
    }
    
    private final void onIncreaseFluidLevelButtonPressed()
    {
        var data = this.menu.getData();
        
        int fluidLevel = Math.min(10, data.getFluidLevel() + 1);
        if (fluidLevel == data.getFluidLevel())
            return;
        
        data.setFluidLevel(fluidLevel);
        TechgunsPacketHandler.INSTANCE.sendToServer(new ChangeReactionFluidLevelPacket(fluidLevel));
    }
    
    private final void onDecreaseFluidLevelButtonPressed()
    {
        var data = this.menu.getData();
        
        int fluidLevel = Math.max(0, data.getFluidLevel() - 1);
        if (fluidLevel == data.getFluidLevel())
            return;
        
        data.setFluidLevel(fluidLevel);
        TechgunsPacketHandler.INSTANCE.sendToServer(new ChangeReactionFluidLevelPacket(fluidLevel));
    }
    
    private final void onDumpTankButtonPressed()
    {
        var data = this.menu.getData();
        
        if (data.getFluid().isEmpty())
            return;
        
        data.dumpFluid();
        TechgunsPacketHandler.INSTANCE.sendToServer(new DumpFluidTankPacket(ReactionChamberControllerBlockEntity.FLUID_SLOT_INPUT));
    }

    @Override
    protected final void renderLabels(GuiGraphics graphics, int p_282681_, int p_283686_)
    {
        super.renderLabels(graphics, p_282681_, p_283686_);

        graphics.drawString(this.font, Component.literal(String.valueOf(this.menu.getData().getFluidLevel() * 10) + "%"), 
                34, 50, 4210752, false);
        graphics.drawString(this.font, Component.literal(String.valueOf(this.menu.getData().getIntensity())), 
                121, 18, 4210752, false);
    }
    
    @Override
    protected final void renderBg(GuiGraphics graphics, float p_97788_, int mouseX, int mouseY)
    {
        this.renderBackground(graphics);
        
        int i = this.leftPos;
        int j = this.topPos;
        
        var data = this.menu.getData();
        
        graphics.blit(TEXTURE, i, j, 0, 0, this.imageWidth, this.imageHeight);
        
        FluidStack fluid = data.getFluid();
        if (fluid.getAmount() > 0)
        {
            IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(fluid.getFluid());
            TextureAtlasSprite sprite = Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS).getSprite(props.getStillTexture(fluid));
            
            GuiUtils.drawFluidWithTessalator(sprite, graphics.pose(), i + 18 + 1, j + 17, 10, 50, Math.max(50,
                    fluid.getAmount() * 50 / 10_000));
        }
        

        graphics.blit(TEXTURE, i + 18, j + 17, 176, 32, 12, 52);
        
        this.renderComponentFG(graphics, i, mouseX, mouseY);

        int requiredCycles = data.getReactionRequiredCycles();
        
        int intensitySize = (10 - data.getIntensity()) * 4;
        
        if (requiredCycles <= 0)
        {
            graphics.blit(TEXTURE, i + 114, j + 16 + intensitySize, 198, intensitySize, 5, 40 - intensitySize);
            return;
        }
        
        int currentCycle = data.getReactionCycle();
        
        if (requiredCycles > 0)
            graphics.blit(TEXTURE, i + 67, j + 61, 0, 167, Math.round((currentCycle * 100f) / requiredCycles), 4);

        graphics.blit(TEXTURE, i + 67, j + 69, 0, 175, Math.round(data.getOperationProgress() * 100f), 4);
        
        if (data.getIntensity() == data.getReactionRequiredIntensity())
        {
            graphics.blit(TEXTURE, i + 114, j + 16 + intensitySize, 198, intensitySize, 5, 40 - intensitySize);
        }
        else
        {
            graphics.blit(TEXTURE, i + 114, j + 16 + intensitySize, 190, intensitySize, 5, 40 - intensitySize);
        }
        
        graphics.blit(TEXTURE, i + 112, j + 15 + (40 - data.getReactionRequiredIntensity() * 4), 178, 22, 5, 3);
    }
    
    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY)
    {
        super.renderTooltip(graphics, mouseX, mouseY);

        int mx = mouseX - this.leftPos;
        int my = mouseY - this.topPos;
        if (mx >= 18 && mx <= (29 /* 18 + 11 */) && my >= 16 && my <= (67 /* 16 + 51 */))
        {
            FluidStack fluid = this.menu.getData().getFluid();
            
            graphics.renderComponentTooltip(this.font, List.of(
                        Component.translatable(fluid.isEmpty() ?
                            TGTranslations.Machine.EMPTY_TANK_TOOLTIP :
                            fluid.getTranslationKey()),
                        Component.literal(fluid.getAmount() + "mB / 10000 mB")),
                    mouseX,
                    mouseY);
        }
        
        if (mx >= 20 && mx <= (28 /* 20 + 8 */) && my >= 7 && my <= (15 /* 7 + 8 */))
        {
            graphics.renderComponentTooltip(this.font, List.of(
                    Component.translatable(TGTranslations.Machine.DUMP_TANK_TOOLTIP),
                    Component.translatable(TGTranslations.Machine.DUMP_TANK_DESCRIPTION_TOOLTIP)), mouseX, mouseY);
        }
    }
}
