package mekanism.client.gui.element.gauge;

import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.api.transmitters.TransmissionType;
import mekanism.client.gui.IGuiWrapper;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.MekanismRenderer.FluidType;
import mekanism.common.MekanismLang;
import mekanism.common.network.PacketDropperUse.TankType;
import mekanism.common.util.text.TextComponentUtil;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fluids.FluidStack;

public class GuiFluidGauge extends GuiTankGauge<FluidStack, IExtendedFluidTank> {

    public GuiFluidGauge(IFluidInfoHandler handler, GaugeType type, IGuiWrapper gui, int x, int y) {
        super(type, gui, x, y, handler, TankType.FLUID_TANK);
        //Ensure it isn't null
        setDummyType(FluidStack.EMPTY);
    }

    public GuiFluidGauge(Supplier<IExtendedFluidTank> tankSupplier, Supplier<List<IExtendedFluidTank>> tanksSupplier, GaugeType type, IGuiWrapper gui, int x, int y) {
        this(new IFluidInfoHandler() {
            @Nullable
            @Override
            public IExtendedFluidTank getTank() {
                return tankSupplier.get();
            }

            @Override
            public int getTankIndex() {
                IExtendedFluidTank tank = getTank();
                return tank == null ? -1 : tanksSupplier.get().indexOf(tank);
            }
        }, type, gui, x, y);
    }

    public static GuiFluidGauge getDummy(GaugeType type, IGuiWrapper gui, int x, int y) {
        GuiFluidGauge gauge = new GuiFluidGauge(null, type, gui, x, y);
        gauge.dummy = true;
        return gauge;
    }

    @Override
    public TransmissionType getTransmission() {
        return TransmissionType.FLUID;
    }

    @Override
    public int getScaledLevel() {
        if (dummy) {
            return height - 2;
        }
        IExtendedFluidTank tank = infoHandler.getTank();
        if (tank == null || tank.isEmpty() || tank.getCapacity() == 0) {
            return 0;
        }
        if (tank.getFluidAmount() == Integer.MAX_VALUE) {
            return height - 2;
        }
        float scale = (float) tank.getFluidAmount() / (float) tank.getCapacity();
        return Math.round(scale * (height - 2));
    }

    @Override
    public TextureAtlasSprite getIcon() {
        if (dummy || infoHandler.getTank() == null) {
            return MekanismRenderer.getFluidTexture(dummyType, FluidType.STILL);
        }
        FluidStack fluid = infoHandler.getTank().getFluid();
        return MekanismRenderer.getFluidTexture(fluid.isEmpty() ? dummyType : fluid, FluidType.STILL);
    }

    @Override
    public ITextComponent getTooltipText() {
        if (dummy) {
            return TextComponentUtil.build(dummyType);
        }
        IExtendedFluidTank tank = infoHandler.getTank();
        if (tank == null || tank.isEmpty()) {
            return MekanismLang.EMPTY.translate();
        }
        int amount = tank.getFluidAmount();
        FluidStack fluidStack = tank.getFluid();
        if (amount == Integer.MAX_VALUE) {
            return MekanismLang.GENERIC_STORED.translate(fluidStack, MekanismLang.INFINITE);
        }
        return MekanismLang.GENERIC_STORED_MB.translate(fluidStack, amount);
    }

    @Override
    protected void applyRenderColor() {
        MekanismRenderer.color(dummy || infoHandler.getTank() == null ? dummyType : infoHandler.getTank().getFluid());
    }

    public interface IFluidInfoHandler extends ITankInfoHandler<IExtendedFluidTank> {
    }
}