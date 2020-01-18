package mekanism.client.render.tileentity;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import mekanism.client.render.FluidRenderer;
import mekanism.client.render.FluidRenderer.RenderData;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.MekanismRenderer.GlowInfo;
import mekanism.common.tile.TileEntityThermalEvaporationController;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;

public class RenderThermalEvaporationController extends TileEntityRenderer<TileEntityThermalEvaporationController> {

    @Override
    public void render(TileEntityThermalEvaporationController tile, double x, double y, double z, float partialTick, int destroyStage) {
        if (tile.structured && tile.height - 2 >= 1 && tile.inputTank.getFluidAmount() > 0) {
            RenderData data = new RenderData();
            data.location = tile.getRenderLocation();
            data.height = tile.height - 2;
            //TODO: If we ever allow different width for the evap controller then update this length and width
            data.length = 2;
            data.width = 2;
            data.fluidType = tile.inputTank.getFluid();
            bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
            GlStateManager.pushMatrix();
            GlStateManager.enableCull();
            GlStateManager.enableBlend();
            GlStateManager.disableLighting();
            GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
            setLightmapDisabled(true);
            FluidRenderer.translateToOrigin(data.location);
            float fluidScale = (float) tile.inputTank.getFluidAmount() / (float) tile.getMaxFluid();
            GlowInfo glowInfo = MekanismRenderer.enableGlow(data.fluidType);
            MekanismRenderer.color(data.fluidType, fluidScale);
            if (data.fluidType.getFluid().getAttributes().isGaseous(data.fluidType)) {
                FluidRenderer.getTankDisplay(data).render();
            } else {
                //Render the proper height
                FluidRenderer.getTankDisplay(data, Math.min(1, fluidScale)).render();
            }
            MekanismRenderer.resetColor();
            MekanismRenderer.disableGlow(glowInfo);
            setLightmapDisabled(false);
            GlStateManager.enableLighting();
            GlStateManager.disableBlend();
            GlStateManager.disableCull();
            GlStateManager.popMatrix();
        }
    }

    @Override
    public boolean isGlobalRenderer(TileEntityThermalEvaporationController tile) {
        return tile.structured && tile.height - 2 >= 1 && tile.inputTank.getFluidAmount() > 0;
    }
}