package com.carthon.stoneage.render;

import com.carthon.stoneage.tiles.ClayKilnTileEntity;
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

public class ClayKilnRenderer extends TileEntityRenderer<ClayKilnTileEntity> {
    public ClayKilnRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(ClayKilnTileEntity blockEntity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        if (this.renderer.camera != null) {
            assert !blockEntity.hasLevel();
            int activeSlot = blockEntity.getItem(0).isEmpty() ? 1 : 0;
            ItemStack item = blockEntity.getInput().getStackInSlot(activeSlot);

            if (item.isEmpty())
                return;
            matrixStack.translate(0.5, 0.5, 0.5);
            matrixStack.mulPose(new Quaternion(0.0F, 90.0F * -(blockEntity.rotation), 0, true));
            matrixStack.translate(-0.5, -0.5, -0.5);

            matrixStack.pushPose();
            matrixStack.translate(8/16D,4/16D,14/16D);
            matrixStack.mulPose(new Quaternion(90F * 2, -90F * blockEntity.rotation,180F, true));
            matrixStack.scale(0.4F, 0.4F, 0.4F);
            //scale x,y,z
            int lightAbove = WorldRenderer.getLightColor(Objects.requireNonNull(blockEntity.getLevel()), blockEntity.getBlockPos().above());
            Minecraft.getInstance().getItemRenderer().renderStatic(item, ItemCameraTransforms.TransformType.FIXED,
                    lightAbove, OverlayTexture.NO_OVERLAY, matrixStack, bufferIn);

            matrixStack.popPose();
        }
    }
}
