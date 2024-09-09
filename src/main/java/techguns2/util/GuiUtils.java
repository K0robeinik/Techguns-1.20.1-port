package techguns2.util;

import java.util.function.Function;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

public final class GuiUtils extends RenderStateShard
{
    private GuiUtils(String p_110161_, Runnable p_110162_, Runnable p_110163_)
    {
        super(p_110161_, p_110162_, p_110163_);
    }

    private static final Function<ResourceLocation, RenderType> GUI_TRANSLUCENT;
    
    static {
        GUI_TRANSLUCENT = Util.memoize(texture -> {
            return RenderType.create("gui_translucent_" + texture, DefaultVertexFormat.POSITION_COLOR_TEX, VertexFormat.Mode.QUADS, 256, false, false, RenderType.CompositeState.builder()
                    .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                    .setShaderState(POSITION_COLOR_TEX_SHADER)
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .createCompositeState(false));
        });
    }
    
    public static void drawFluidWithTessalator(TextureAtlasSprite tex, PoseStack poseStack, int x, int y, int width, int height, int px)
    {
        int offset = 0;
        
        if (tex == null)
            return;
        
        int textureHeight = tex.contents().height();
        int length = textureHeight;
        while (offset < px)
        {
            if (offset + length > px)
            {
                length = px - offset;
            }
            
            GuiUtils.drawTexturedModelRectFromIconFluidTank(x, y + height - offset - length, tex, textureHeight, poseStack, width, length);
            offset += textureHeight;
        }
    }
    
    public static void drawTexturedModelRectFromIconFluidTank(int x, int y, TextureAtlasSprite tex, int texHeight, PoseStack poseStack, int w, int h)
    {
        VertexConsumer buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder())
                .getBuffer(GuiUtils.GUI_TRANSLUCENT.apply(InventoryMenu.BLOCK_ATLAS));
        
        buffer.vertex(poseStack.last().pose(), x + 0, y + h, 0).uv(tex.getU0(), tex.getV1()).endVertex();
        buffer.vertex(poseStack.last().pose(), x + w, y + h, 0).uv(tex.getU1(), tex.getV1()).endVertex();
        buffer.vertex(poseStack.last().pose(), x + w, y + 0, 0).uv(tex.getU1(), tex.getV(texHeight - h)).endVertex();
        buffer.vertex(poseStack.last().pose(), x + 0, y + 0, 0).uv(tex.getU0(), tex.getV(texHeight - h)).endVertex();
    }
}
