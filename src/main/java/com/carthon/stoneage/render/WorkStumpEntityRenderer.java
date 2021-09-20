package com.carthon.stoneage.render;

import com.carthon.stoneage.tiles.WorkStumpTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Quaternion;

import java.util.Objects;

public class WorkStumpEntityRenderer extends TileEntityRenderer<WorkStumpTileEntity> {

    public WorkStumpEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(WorkStumpTileEntity blockEntity, float partialTicks, MatrixStack matrixStack,
                       IRenderTypeBuffer iRenderTypeBuffer, int combinedLightIn, int combinedOverlayIn) {
        if (this.renderer.camera != null) {
            if (blockEntity.getInput().isEmpty())
                return;
            matrixStack.translate(0.5, 0.5, 0.5);
            matrixStack.mulPose(new Quaternion(0.0F, 90.0F * -(blockEntity.rotation), 0, true));
            matrixStack.translate(-0.5, -0.5, -0.5);
            ItemStack toolSlot = blockEntity.getInput().getStackInSlot(9);

            if(!toolSlot.isEmpty() && blockEntity.getLevel() != null){
                matrixStack.pushPose();
                matrixStack.translate(8/16D,6/16D,8/16D);
                matrixStack.mulPose(new Quaternion(90F, 0,-90F, true));
                //scale x,y,z
                matrixStack.scale(5/16F, 6/16F, 5/16F);
                int lightAbove = WorldRenderer.getLightColor(blockEntity.getLevel(), blockEntity.getBlockPos().above());
                Minecraft.getInstance().getItemRenderer().renderStatic(toolSlot, ItemCameraTransforms.TransformType.FIXED,
                        lightAbove, OverlayTexture.NO_OVERLAY, matrixStack, iRenderTypeBuffer);
                matrixStack.popPose();
            }
            matrixStack.translate(1D/16D, 15D/16D, 1D/16D);

            int slot = 0;
            for(int i = 2; i >= 0; i--)
                for(int j = 0; j < 3; j++){
                    float jump = blockEntity.itemJump[slot];
                    float prevJump = blockEntity.itemJumpPrev[slot];
                    float jumpUp = jump * 0.05F + (jump * 0.05F - prevJump * 0.05F) * partialTicks;
                    ItemStack item = blockEntity.getInput().getStackInSlot(slot);
                    slot++;
                    if (!item.isEmpty()){
                        //pushmatrix
                        matrixStack.pushPose();
                        //translate x,y,z
                        matrixStack.translate((2.5/15D) + ((4D / 15D) * i), jumpUp,
                                (2.5/15D) + ((4D / 15D) * j));
                        matrixStack.mulPose(new Quaternion(90F ,0,-90F,true));
                        //scale x,y,z
                        matrixStack.scale(0.25F, 0.25F, 0.25F);

                        int lightAbove = WorldRenderer.getLightColor(Objects.requireNonNull(blockEntity.getLevel()), blockEntity.getBlockPos().above());
                        Minecraft.getInstance().getItemRenderer().renderStatic(item, ItemCameraTransforms.TransformType.FIXED,
                                lightAbove, OverlayTexture.NO_OVERLAY, matrixStack, iRenderTypeBuffer);
                        //popmatrix
                        matrixStack.popPose();
                    }
                }

        }
    }
}
