package mekanism.client.render.item.gear;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nonnull;
import mekanism.client.model.ModelFlamethrower;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.item.ItemLayerWrapper;
import mekanism.client.render.item.MekanismItemStackRenderer;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.ItemStack;

public class RenderFlameThrower extends MekanismItemStackRenderer {

    private static ModelFlamethrower flamethrower = new ModelFlamethrower();
    public static ItemLayerWrapper model;

    @Override
    protected void renderBlockSpecific(@Nonnull ItemStack stack, TransformType transformType) {
    }

    @Override
    protected void renderItemSpecific(@Nonnull ItemStack stack, TransformType transformType) {
        GlStateManager.pushMatrix();
        GlStateManager.rotatef(160, 0, 0, 1);
        MekanismRenderer.bindTexture(MekanismUtils.getResource(ResourceType.RENDER, "flamethrower.png"));
        GlStateManager.translatef(0, -1.0F, 0);
        GlStateManager.rotatef(135, 0, 1, 0);
        GlStateManager.rotatef(-20, 0, 0, 1);

        if (transformType == TransformType.FIRST_PERSON_RIGHT_HAND || transformType == TransformType.THIRD_PERSON_RIGHT_HAND
            || transformType == TransformType.FIRST_PERSON_LEFT_HAND || transformType == TransformType.THIRD_PERSON_LEFT_HAND) {
            if (transformType == TransformType.FIRST_PERSON_RIGHT_HAND) {
                GlStateManager.rotatef(55, 0, 1, 0);
            } else if (transformType == TransformType.FIRST_PERSON_LEFT_HAND) {
                GlStateManager.rotatef(-160, 0, 1, 0);
                GlStateManager.rotatef(30, 1, 0, 0);
            } else if (transformType == TransformType.THIRD_PERSON_RIGHT_HAND) {
                GlStateManager.translatef(0, 0.7F, 0);
                GlStateManager.rotatef(75, 0, 1, 0);
            } else {//if(type == TransformType.THIRD_PERSON_LEFT_HAND)
                GlStateManager.translatef(-0.5F, 0.7F, 0);
            }
            GlStateManager.scalef(2.5F, 2.5F, 2.5F);
            GlStateManager.translatef(0, -1.0F, -0.5F);
        } else if (transformType == TransformType.GUI) {
            GlStateManager.translatef(-0.6F, 0, 0);
            GlStateManager.rotatef(45, 0, 1, 0);
        }
        flamethrower.render(0.0625F);
        GlStateManager.popMatrix();
    }

    @Nonnull
    @Override
    protected TransformType getTransform(@Nonnull ItemStack stack) {
        return model.getTransform();
    }
}