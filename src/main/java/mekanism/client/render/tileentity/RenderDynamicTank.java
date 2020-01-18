package mekanism.client.render.tileentity;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import mekanism.client.render.FluidRenderer;
import mekanism.client.render.FluidRenderer.RenderData;
import mekanism.client.render.FluidRenderer.ValveRenderData;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.MekanismRenderer.GlowInfo;
import mekanism.common.content.tank.SynchronizedTankData.ValveData;
import mekanism.common.tile.TileEntityDynamicTank;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;

public class RenderDynamicTank extends TileEntityRenderer<TileEntityDynamicTank> {

    @Override
    public void render(TileEntityDynamicTank tile, double x, double y, double z, float partialTick, int destroyStage) {
        if (tile.clientHasStructure && tile.isRendering && tile.structure != null && tile.structure.fluidStored.getAmount() > 0) {
            RenderData data = new RenderData();
            data.location = tile.structure.renderLocation;
            data.height = tile.structure.volHeight - 2;
            data.length = tile.structure.volLength;
            data.width = tile.structure.volWidth;
            data.fluidType = tile.structure.fluidStored;

            if (data.location != null && data.height >= 1) {
                bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
                GlStateManager.pushMatrix();
                GlStateManager.enableCull();
                GlStateManager.enableBlend();
                GlStateManager.disableLighting();
                GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
                setLightmapDisabled(true);
                FluidRenderer.translateToOrigin(data.location);
                GlowInfo glowInfo = MekanismRenderer.enableGlow(data.fluidType);
                MekanismRenderer.color(data.fluidType, (float) data.fluidType.getAmount() / (float) tile.clientCapacity);
                if (data.fluidType.getFluid().getAttributes().isGaseous(data.fluidType)) {
                    FluidRenderer.getTankDisplay(data).render();
                } else {
                    FluidRenderer.getTankDisplay(data, tile.prevScale).render();
                }

                MekanismRenderer.resetColor();
                MekanismRenderer.disableGlow(glowInfo);
                GlStateManager.popMatrix();

                for (ValveData valveData : tile.valveViewing) {
                    GlStateManager.pushMatrix();
                    FluidRenderer.translateToOrigin(valveData.location);
                    GlowInfo valveGlowInfo = MekanismRenderer.enableGlow(data.fluidType);
                    MekanismRenderer.color(data.fluidType);
                    FluidRenderer.getValveDisplay(ValveRenderData.get(data, valveData)).render();
                    MekanismRenderer.disableGlow(valveGlowInfo);
                    GlStateManager.popMatrix();
                }
                MekanismRenderer.resetColor();
                setLightmapDisabled(false);
                GlStateManager.enableLighting();
                GlStateManager.disableBlend();
                GlStateManager.disableCull();
            }
        }
    }

    @Override
    public boolean isGlobalRenderer(TileEntityDynamicTank tile) {
        return tile.clientHasStructure && tile.isRendering && tile.structure != null && tile.structure.fluidStored.getAmount() > 0;
    }
}