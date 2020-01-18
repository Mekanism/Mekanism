package mekanism.client.render.tileentity;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import javax.annotation.Nonnull;
import mekanism.client.render.FluidRenderMap;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.MekanismRenderer.DisplayInteger;
import mekanism.client.render.MekanismRenderer.FluidType;
import mekanism.client.render.MekanismRenderer.GlowInfo;
import mekanism.client.render.MekanismRenderer.Model3D;
import mekanism.common.tier.FluidTankTier;
import mekanism.common.tile.TileEntityFluidTank;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

public class RenderFluidTank extends TileEntityRenderer<TileEntityFluidTank> {

    public static final RenderFluidTank INSTANCE = new RenderFluidTank();

    private static FluidRenderMap<DisplayInteger[]> cachedCenterFluids = new FluidRenderMap<>();
    private static FluidRenderMap<DisplayInteger[]> cachedValveFluids = new FluidRenderMap<>();

    private static int stages = 1400;

    public static void resetDisplayInts() {
        cachedCenterFluids.clear();
        cachedValveFluids.clear();
    }

    @Override
    public void render(TileEntityFluidTank tile, double x, double y, double z, float partialTick, int destroyStage) {
        FluidStack fluid = tile.fluidTank.getFluid();
        render(tile.tier, fluid, tile.prevScale, tile.valve > 0 ? tile.valveFluid : FluidStack.EMPTY, x, y, z);
    }

    public void render(FluidTankTier tier, @Nonnull FluidStack fluid, float fluidScale, @Nonnull FluidStack valveFluid, double x, double y, double z) {
        boolean glChanged = false;
        if (!fluid.isEmpty() && fluidScale > 0) {
            GlStateManager.pushMatrix();
            glChanged = enableGL();
            bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
            GlStateManager.translatef((float) x, (float) y, (float) z);
            GlowInfo glowInfo = MekanismRenderer.enableGlow(fluid);

            DisplayInteger[] displayList = getListAndRender(fluid);
            if (tier == FluidTankTier.CREATIVE) {
                fluidScale = 1;
            }
            MekanismRenderer.color(fluid, fluidScale);
            if (fluid.getFluid().getAttributes().isGaseous(fluid)) {
                displayList[stages - 1].render();
            } else {
                displayList[Math.min(stages - 1, (int) (fluidScale * ((float) stages - 1)))].render();
            }
            MekanismRenderer.resetColor();
            MekanismRenderer.disableGlow(glowInfo);
            GlStateManager.popMatrix();
        }

        if (!valveFluid.isEmpty() && !valveFluid.getFluid().getAttributes().isGaseous(valveFluid)) {
            GlStateManager.pushMatrix();
            glChanged = enableGL();
            bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
            GlStateManager.translatef((float) x, (float) y, (float) z);
            GlowInfo glowInfo = MekanismRenderer.enableGlow(valveFluid);
            MekanismRenderer.color(valveFluid);
            DisplayInteger[] valveList = getValveRender(valveFluid);
            valveList[Math.min(stages - 1, (int) (fluidScale * ((float) stages - 1)))].render();
            MekanismRenderer.resetColor();
            MekanismRenderer.disableGlow(glowInfo);
            GlStateManager.popMatrix();
        }

        if (glChanged) {
            GlStateManager.disableBlend();
            GlStateManager.enableAlphaTest();
            GlStateManager.enableLighting();
            GlStateManager.disableCull();
        }
    }

    private boolean enableGL() {
        GlStateManager.enableCull();
        GlStateManager.disableLighting();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.disableAlphaTest();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
        return true;
    }

    private DisplayInteger[] getValveRender(@Nonnull FluidStack fluid) {
        if (cachedValveFluids.containsKey(fluid)) {
            return cachedValveFluids.get(fluid);
        }

        Model3D toReturn = new Model3D();
        toReturn.baseBlock = Blocks.WATER;
        MekanismRenderer.prepFlowing(toReturn, fluid);

        DisplayInteger[] displays = new DisplayInteger[stages];
        cachedValveFluids.put(fluid, displays);

        for (int i = 0; i < stages; i++) {
            displays[i] = DisplayInteger.createAndStart();

            if (fluid.getFluid().getAttributes().getStill(fluid) != null) {
                toReturn.minX = 0.3125 + .01;
                toReturn.minY = 0.0625 + ((float) i / (float) stages) * 0.875;
                toReturn.minZ = 0.3125 + .01;

                toReturn.maxX = 0.6875 - .01;
                toReturn.maxY = 0.9375 - .01;
                toReturn.maxZ = 0.6875 - .01;

                MekanismRenderer.renderObject(toReturn);
            }

            GlStateManager.endList();
        }

        return displays;
    }

    private DisplayInteger[] getListAndRender(@Nonnull FluidStack fluid) {
        if (cachedCenterFluids.containsKey(fluid)) {
            return cachedCenterFluids.get(fluid);
        }

        Model3D toReturn = new Model3D();
        toReturn.baseBlock = Blocks.WATER;
        toReturn.setTexture(MekanismRenderer.getFluidTexture(fluid, FluidType.STILL));

        DisplayInteger[] displays = new DisplayInteger[stages];
        cachedCenterFluids.put(fluid, displays);

        for (int i = 0; i < stages; i++) {
            displays[i] = DisplayInteger.createAndStart();

            if (fluid.getFluid().getAttributes().getStill(fluid) != null) {
                toReturn.minX = 0.125 + .01;
                toReturn.minY = 0.0625 + .01;
                toReturn.minZ = 0.125 + .01;

                toReturn.maxX = 0.875 - .01;
                toReturn.maxY = 0.0625 + ((float) i / (float) stages) * 0.875 - .01;
                toReturn.maxZ = 0.875 - .01;

                MekanismRenderer.renderObject(toReturn);
            }

            GlStateManager.endList();
        }

        return displays;
    }
}