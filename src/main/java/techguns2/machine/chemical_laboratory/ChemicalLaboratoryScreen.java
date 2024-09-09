package techguns2.machine.chemical_laboratory;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.CommonComponents;
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
import techguns2.networking.packets.DumpFluidTankPacket;
import techguns2.networking.packets.SetChemicalLaboratoryDrainModePacket;
import techguns2.util.GuiUtils;

@OnlyIn(Dist.CLIENT)
public final class ChemicalLaboratoryScreen extends AbstractMachineScreen<ChemicalLaboratoryMenu>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Techguns.MODID, "textures/gui/machine/chemical_laboratory_gui.png");
    
    private Button _dumpInputTankButton;
    private Button _dumpOutputTankButton;
    private Button _switchDrainTankButton;

    public ChemicalLaboratoryScreen(ChemicalLaboratoryMenu menu, Inventory inventory, Component displayName)
    {
        super(menu, inventory, displayName);
    }
    
    @Override
    protected void init()
    {
        super.init();

        this._dumpInputTankButton = Button.builder(Component.literal("x"), this)
                .pos(this.leftPos + 20, this.topPos + 7)
                .size(8, 8)
                .build();
        this._dumpOutputTankButton = Button.builder(Component.literal("x"), this)
                .pos(this.leftPos + 159, this.topPos + 7)
                .size(8, 8)
                .build();
        this._switchDrainTankButton = new TankDrainButton(Button.builder(CommonComponents.EMPTY, this)
                .pos(this.leftPos + 156, this.topPos + 64)
                .size(14, 14));
        
        this.addRenderableWidget(this._dumpInputTankButton);
        this.addRenderableWidget(this._dumpOutputTankButton);
        this.addRenderableWidget(this._switchDrainTankButton);
    }
    
    @Override
    public final void onPress(Button button)
    {
        if (button == this._dumpInputTankButton)
            this.onDumpInputTankButtonPressed();
        else if (button == this._dumpOutputTankButton)
            this.onDumpOutputTankButtonPressed();
        else if (button == this._switchDrainTankButton)
            this.onSwitchDrainTankButtonPressed();
        else
            super.onPress(button);
    }
    
    private final void onDumpInputTankButtonPressed()
    {
        var data = this.menu.getData();
        
        data.dumpFluid();
        
        TechgunsPacketHandler.INSTANCE.sendToServer(new DumpFluidTankPacket(ChemicalLaboratoryBlockEntity.FLUID_SLOT_INPUT));
    }
    
    private final void onDumpOutputTankButtonPressed()
    {
        var data = this.menu.getData();

        data.dumpResultFluid();
        
        TechgunsPacketHandler.INSTANCE.sendToServer(new DumpFluidTankPacket(ChemicalLaboratoryBlockEntity.FLUID_SLOT_RESULT));
    }
    
    private final void onSwitchDrainTankButtonPressed()
    {
        var data = this.menu.getData();

        ChemicalLaboratoryContainerData.DrainMode drainMode = data.getDrainMode();
        if (drainMode == ChemicalLaboratoryContainerData.DrainMode.FROM_INPUT)
            drainMode = ChemicalLaboratoryContainerData.DrainMode.FROM_OUTPUT;
        else
            drainMode = ChemicalLaboratoryContainerData.DrainMode.FROM_INPUT;
        
        data.setDrainMode(drainMode);
        
        TechgunsPacketHandler.INSTANCE.sendToServer(new SetChemicalLaboratoryDrainModePacket(drainMode));
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
                        Component.literal(fluid.getAmount() + "mB / 8000 mB")),
                    mouseX,
                    mouseY);
        }
        if (mx >= 157 && mx <= (168 /* 157 + 11 */) && my >= 16 && my <= (67 /* 16 + 51 */))
        {
            FluidStack fluid = this.menu.getData().getResultFluid();
            
            graphics.renderComponentTooltip(this.font, List.of(
                        Component.translatable(fluid.isEmpty() ?
                            TGTranslations.Machine.EMPTY_TANK_TOOLTIP :
                            fluid.getTranslationKey()),
                        Component.literal(fluid.getAmount() + "mB / 16000 mB")),
                    mouseX,
                    mouseY);
        }
        
        if ((mx >= 20 && mx <= (28 /* 20 + 8 */) && my >= 7 && my <= (15 /* 7 + 8 */)) ||
            (mx >= 159 && mx <= (167 /* 159 + 8 */) && my >= 7 && my <= (15 /* 7 + 8 */)))
        {
            graphics.renderComponentTooltip(this.font, List.of(
                    Component.translatable(TGTranslations.Machine.DUMP_TANK_TOOLTIP),
                    Component.translatable(TGTranslations.Machine.DUMP_TANK_DESCRIPTION_TOOLTIP)), mouseX, mouseY);
        }
        
        // TODO: Toggle Drain tooltip
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
        

        FluidStack fluid = data.getFluid();
        if (fluid.getAmount() > 0)
        {
            IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(fluid.getFluid());
            TextureAtlasSprite sprite = Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS).getSprite(props.getStillTexture(fluid));
            
            GuiUtils.drawFluidWithTessalator(sprite, graphics.pose(), i + 18 + 1, j + 17, 10, 50, Math.max(50,
                    fluid.getAmount() * 50 / 8_000));
        }

        FluidStack resultFluid = data.getResultFluid();
        if (resultFluid.getAmount() > 0)
        {
            IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(resultFluid.getFluid());
            TextureAtlasSprite sprite = Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS).getSprite(props.getStillTexture(resultFluid));
            
            GuiUtils.drawFluidWithTessalator(sprite, graphics.pose(), i + 157 + 1, j + 17, 10, 50, Math.max(50,
                    resultFluid.getAmount() * 50 / 16_000));
        }

        graphics.blit(TEXTURE, i + 18, j + 17, 176, 32, 12, 52);
        graphics.blit(TEXTURE, i + 157, j + 17, 176, 32, 12, 52);
        
        float progress = data.getOperationProgress();
        graphics.blit(TEXTURE, i + 81, j + 19, 178, 5, 8, (int)((progress < 0.2f ? (progress * 5) : 1.0f) * 25));
        
        if (progress >= 0.2f)
            graphics.blit(TEXTURE, i + 88, j + 39, 186, 25, (int)((progress < 0.4f ? (progress - 0.2f) * 5 : 1.0f) * 9), 1);
        
        if (progress >= 0.4f)
        {
            int i3 = (int)((progress < 0.6f ? (progress - 0.4f) * 5 : 1.0f) * 20);
            graphics.blit(TEXTURE, i + 97, j + 23 + (20 - i3), 194, 9 + (20 - i3), 14, i3);
        }
        
        if (progress >= 0.6f)
            graphics.blit(TEXTURE, i + 109, j + 17, 205, 3, (int)((progress < 0.8f ? (progress - 0.6f) * 5 : 1.0f) * 16), 6);
        
        if (progress >= 0.8f)
            graphics.blit(TEXTURE, i + 117, j + 20, 214, 6, 14, (int)(((progress - 0.8f) * 5) * 24) + 1);
    }
    
    private final class TankDrainButton extends Button
    {
        protected TankDrainButton(Builder builder)
        {
            super(builder);
        }
        
        @Override
        protected void renderWidget(GuiGraphics graphics, int p_282682_, int p_281714_, float p_282542_)
        {
            super.renderWidget(graphics, p_282682_, p_281714_, p_282542_);
            
            boolean drainFromInput = ChemicalLaboratoryScreen.this.menu.getData().getDrainMode() == ChemicalLaboratoryContainerData.DrainMode.FROM_INPUT;
            
            this.renderTexture(graphics, TEXTURE, this.getX() + 1, this.getY() + 1, 188, drainFromInput ? 43 : 31, 0, this.width, this.height, 12, 12);
        }
        
    }
}
