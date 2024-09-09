package techguns2.machine.ammo_press;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import techguns2.Techguns;

@OnlyIn(Dist.CLIENT)
public final class AmmoPressRenderer implements BlockEntityRenderer<AmmoPressBlockEntity>
{
    private final AmmoPressModel _model;
    
    public AmmoPressRenderer(BlockEntityRendererProvider.Context context)
    {
        this._model = new AmmoPressModel(context.bakeLayer(LAYER_LOCATION));
    }

    @Override
    public final void render(AmmoPressBlockEntity blockEntity, float partialTick, PoseStack poseStack,
            MultiBufferSource bufferSource, int combinedLight, int combinedOverlay)
    {
        BlockState blockState = blockEntity.getBlockState();
        AmmoPressBlock ammoPressBlock = (AmmoPressBlock)blockState.getBlock();
        this._model.evalulateProgress(blockEntity);
        
        poseStack.pushPose();
        poseStack.translate(1, 0, 0);
        poseStack.rotateAround(Axis.YP.rotationDegrees(ammoPressBlock.getYRotationDegrees(blockState)),
                -0.5F, 0F, 0.5F);
        
        VertexConsumer vertexConsumer = bufferSource.getBuffer(this._model.renderType(TEXTURE_LOCATION));
        this._model.renderToBuffer(poseStack, vertexConsumer, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
    }

    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(Techguns.MODID, "textures/entity/machine/ammo_press.png");
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Techguns.MODID, "machine/ammo_press"), "main");
    
    public static final LayerDefinition createLayer()
    {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partdefinition = meshDefinition.getRoot();

        PartDefinition frame = partdefinition.addOrReplaceChild("frame", CubeListBuilder.create()
                .texOffs(48, 36).addBox(-3.0F, 12.0F, 3.0F, 3.0F, 4.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(55, 0).addBox(-1.5F, 14.0F, 12.5F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(50, 0).addBox(-1.5F, 2.0F, 14.5F, 1.0F, 12.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-16.0F, 0.0F, 0.0F, 16.0F, 2.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(51, 18).addBox(-3.0F, 2.0F, 3.0F, 3.0F, 2.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(65, 31).addBox(-13.0F, 2.0F, 3.0F, 10.0F, 2.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(0, 18).addBox(-4.0F, 12.5F, 4.0F, 1.0F, 3.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(31, 19).addBox(-1.0F, 5.0F, 5.0F, 1.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(50, 0).addBox(-1.5F, 2.0F, 0.5F, 1.0F, 12.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(55, 0).addBox(-1.5F, 14.0F, 0.5F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(50, 0).addBox(-15.5F, 2.0F, 0.5F, 1.0F, 12.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(50, 0).addBox(-15.5F, 2.0F, 14.5F, 1.0F, 12.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(66, 0).addBox(-2.5F, 4.0F, 4.0F, 2.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(31, 19).addBox(-16.0F, 5.0F, 5.0F, 1.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(55, 0).addBox(-15.5F, 14.0F, 12.5F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(55, 0).addBox(-15.5F, 14.0F, 0.5F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(66, 0).addBox(-15.5F, 4.0F, 4.0F, 2.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(110, 0).addBox(-13.5F, 4.0F, 4.5F, 2.0F, 6.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(110, 0).addBox(-4.5F, 4.0F, 4.5F, 2.0F, 6.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(68, 45).addBox(-12.0F, 12.0F, 3.0F, 8.0F, 4.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(100, 24).addBox(-11.0F, 11.0F, 4.0F, 6.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(51, 18).addBox(-16.0F, 2.0F, 3.0F, 3.0F, 2.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(48, 36).addBox(-16.0F, 12.0F, 3.0F, 3.0F, 4.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(0, 18).addBox(-13.0F, 12.5F, 4.0F, 1.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        frame.addOrReplaceChild("Shape30_r1", CubeListBuilder.create().texOffs(53, 0).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-11.0F, 12.0F, 1.0F, 0.0F, -1.5708F, 0.0F));

        frame.addOrReplaceChild("Shape29_r1", CubeListBuilder.create().texOffs(55, 0).addBox(-0.5F, 0.0F, 2.5F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-9.0F, 13.0F, 1.0F, 0.0F, -1.5708F, 0.0F));

        frame.addOrReplaceChild("Shape28_r1", CubeListBuilder.create().texOffs(0, 38).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 2.0F, 1.0F, 0.0F, -1.5708F, 0.0F));

        frame.addOrReplaceChild("Shape20_r1", CubeListBuilder.create().texOffs(55, 0).addBox(0.5F, 0.0F, -0.5F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 13.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        frame.addOrReplaceChild("Shape19_r1", CubeListBuilder.create().texOffs(53, 0).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, 12.0F, 1.0F, 0.0F, -1.5708F, 0.0F));

        frame.addOrReplaceChild("Shape10_r1", CubeListBuilder.create().texOffs(38, 25).addBox(-2.0F, 0.0F, -1.0F, 3.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-10.0F, 5.0F, 1.0F, 0.0F, 1.5708F, 0.0F));

        frame.addOrReplaceChild("Shape4_r1", CubeListBuilder.create().texOffs(66, 0).addBox(-2.5F, 4.0F, 0.0F, 2.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 0.0F, 3.0F, 0.0F, -1.5708F, 0.0F));

        partdefinition.addOrReplaceChild("metalPiece", CubeListBuilder.create().texOffs(110, 46).addBox(-8.5F, 4.0F, 6.0F, 1.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition bullets = partdefinition.addOrReplaceChild("bullets", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        bullets.addOrReplaceChild("bullet3_r1", CubeListBuilder.create().texOffs(118, 47).addBox(-0.5F, -2.5F, -0.5F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, 6.5F, 6.5F, 0.0F, -0.7854F, 0.0F));

        bullets.addOrReplaceChild("bullet2_r1", CubeListBuilder.create().texOffs(114, 47).addBox(-0.5F, -3.0F, -0.5F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, 6.0F, 8.5F, 0.0F, -0.7854F, 0.0F));

        bullets.addOrReplaceChild("bullet1_r1", CubeListBuilder.create().texOffs(110, 47).addBox(-0.5F, -2.5F, -0.5F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, 6.5F, 10.5F, 0.0F, -0.7854F, 0.0F));

        partdefinition.addOrReplaceChild("pressA", CubeListBuilder.create()
                .texOffs(110, 13).addBox(-5.0F, 4.0F, 5.0F, 3.0F, 5.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(94, 0).addBox(-6.0F, 4.0F, 4.5F, 1.0F, 5.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        partdefinition.addOrReplaceChild("pressB", CubeListBuilder.create()
                .texOffs(110, 13).addBox(-14.0F, 4.0F, 5.0F, 3.0F, 5.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(94, 0).addBox(-11.0F, 4.0F, 4.5F, 1.0F, 5.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        partdefinition.addOrReplaceChild("pressC", CubeListBuilder.create().texOffs(108, 33).addBox(-10.0F, 10.0F, 5.0F, 4.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        
        return LayerDefinition.create(meshDefinition, 128, 64);
    }

    public static final class AmmoPressModel extends Model
    {
        private final ModelPart _frame;
        private final ModelPart _pressA;
        private final ModelPart _pressB;
        private final ModelPart _pressC;
        private final ModelPart _metalPiece;
        private final ModelPart _bullets;
        private float _progress;

        public AmmoPressModel(ModelPart root)
        {
            super(RenderType::entityCutoutNoCull);
            
            this._frame = root.getChild("frame");
            this._pressA = root.getChild("pressA");
            this._pressB = root.getChild("pressB");
            this._pressC = root.getChild("pressC");
            this._metalPiece = root.getChild("metalPiece");
            this._bullets = root.getChild("bullets");
            
            this._progress = 0;
        }
        
        public final void evalulateProgress(AmmoPressBlockEntity blockEntity)
        {
            AmmoPressOperation operation = blockEntity.getCurrentOperation();
            if (operation == null)
                this._progress = 0f;
            else
                this._progress = operation.progress();
        }
        
        @Override
        public final void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
        {
            this._frame.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
            
            float progress = this._progress;
            
            if (progress <= 0f)
            {
                this._pressA.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
                this._pressB.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
                this._pressC.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
                return;
            }
            
            // repeat once
            if (progress > 0.5f)
            {
                progress -= 0.5f;
            }
            progress *= 2;
            
            float ab = 0;
            float c = 0;
            
            if (progress < 0.25)
            {
                this._metalPiece.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
                float p = progress * 4f;
                ab = (float)Math.sqrt(p);
                // 0,5+(SIN(((B2/2)+0,75)*2*PI())/2)
            }
            else if (progress < 0.66f)
            {
                float p = ((progress - 0.25f) * 2.439f);
                ab = (float)(0.5f + (Math.sin((((1f - p) / 2.0f) + 0.75f) * 2.0f * Math.PI) / 2.0f));

                this._bullets.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
            }
            else
            {
                float p = Math.min(1.0f, (progress - 0.66f) * 3f);

                c = 0.5f + (float)Math.sin((Math.sqrt(p) + 0.75f) * Math.PI * 2.0f) / 2.0f;
                
                if (progress < 0.9f)
                {
                    this._bullets.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
                }
            }
            
            // press A
            poseStack.pushPose();
            poseStack.translate((0.125 * -ab), 0f, 0f);
            this._pressA.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
            poseStack.popPose();

            // press B
            poseStack.pushPose();
            poseStack.translate((0.125 * ab), 0f, 0f);
            this._pressB.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
            poseStack.popPose();
            
            // press C
            poseStack.pushPose();
            poseStack.translate(0f, (-0.09375f * c), 0f);
            this._pressC.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
            poseStack.popPose();
        }
        
    }
}
