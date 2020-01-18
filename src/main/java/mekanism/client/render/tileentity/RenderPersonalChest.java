package mekanism.client.render.tileentity;

import com.mojang.blaze3d.platform.GlStateManager;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.tile.TileEntityPersonalChest;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.model.ChestModel;

public class RenderPersonalChest extends TileEntityRenderer<TileEntityPersonalChest> {

    private ChestModel model = new ChestModel();

    @Override
    public void render(TileEntityPersonalChest tile, double x, double y, double z, float partialTick, int destroyStage) {
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float) x, (float) y + 1F, (float) z);
        GlStateManager.rotatef(90, 0, 1, 0);
        bindTexture(MekanismUtils.getResource(ResourceType.RENDER, "personal_chest.png"));

        MekanismRenderer.rotate(tile.getDirection(), 270, 90, 0, 180);
        switch (tile.getDirection()) {
            case NORTH:
                GlStateManager.translatef(1.0F, 0, 0);
                break;
            case SOUTH:
                GlStateManager.translatef(0, 0, -1.0F);
                break;
            case EAST:
                GlStateManager.translatef(1.0F, 0, -1.0F);
                break;
        }

        float lidangle = tile.prevLidAngle + (tile.lidAngle - tile.prevLidAngle) * partialTick;
        lidangle = 1.0F - lidangle;
        lidangle = 1.0F - lidangle * lidangle * lidangle;
        model.getLid().rotateAngleX = -((lidangle * 3.141593F) / 2.0F);
        GlStateManager.rotatef(180, 0, 0, 1);
        model.renderAll();
        GlStateManager.popMatrix();
    }
}