package com.carthon.stoneage.render;

import com.carthon.stoneage.tiles.TreeStumpTileEntity;
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

public class TreeStumpEntityRenderer extends TileEntityRenderer<TreeStumpTileEntity> {
    public TreeStumpEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(TreeStumpTileEntity blockEntity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        if (this.renderer.camera != null) {
            assert !blockEntity.hasLevel();
            ItemStack item = blockEntity.getInput().getStackInSlot(0);
            if (item.isEmpty())
                return;
            matrixStack.pushPose();
            matrixStack.translate(8/16D,11/16D,8/16D);
            matrixStack.mulPose(new Quaternion(90F * 2, -90F * blockEntity.rotation,0F, true));
            //scale x,y,z
            matrixStack.scale(0.75F, 0.75F, 0.75F);
            int lightAbove = WorldRenderer.getLightColor(Objects.requireNonNull(blockEntity.getLevel()), blockEntity.getBlockPos().above());
            Minecraft.getInstance().getItemRenderer().renderStatic(item, ItemCameraTransforms.TransformType.FIXED,
                    lightAbove, OverlayTexture.NO_OVERLAY, matrixStack, bufferIn);
            matrixStack.popPose();
        }
    }
}
