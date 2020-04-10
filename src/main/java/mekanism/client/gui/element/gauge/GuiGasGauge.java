package mekanism.client.gui.element.gauge;

import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.math.MathUtils;
import mekanism.api.transmitters.TransmissionType;
import mekanism.client.gui.IGuiWrapper;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.MekanismLang;
import mekanism.common.network.PacketDropperUse.TankType;
import mekanism.common.util.text.TextComponentUtil;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.text.ITextComponent;

public class GuiGasGauge extends GuiTankGauge<Gas, IChemicalTank<Gas, GasStack>> {

    public GuiGasGauge(IGasInfoHandler handler, GaugeType type, IGuiWrapper gui, int x, int y) {
        super(type, gui, x, y, handler, TankType.GAS_TANK);
    }

    public GuiGasGauge(Supplier<IChemicalTank<Gas, GasStack>> tankSupplier, Supplier<List<? extends IChemicalTank<Gas, GasStack>>> tanksSupplier, GaugeType type,
          IGuiWrapper gui, int x, int y) {
        this(new IGasInfoHandler() {
            @Nullable
            @Override
            public IChemicalTank<Gas, GasStack> getTank() {
                return tankSupplier.get();
            }

            @Override
            public int getTankIndex() {
                IChemicalTank<Gas, GasStack> tank = getTank();
                return tank == null ? -1 : tanksSupplier.get().indexOf(tank);
            }
        }, type, gui, x, y);
    }

    public static GuiGasGauge getDummy(GaugeType type, IGuiWrapper gui, int x, int y) {
        GuiGasGauge gauge = new GuiGasGauge(null, type, gui, x, y);
        gauge.dummy = true;
        return gauge;
    }

    @Override
    public TransmissionType getTransmission() {
        return TransmissionType.GAS;
    }

    @Override
    public int getScaledLevel() {
        if (dummy) {
            return height - 2;
        }
        //TODO: Can capacity ever be zero when tank is not empty?
        IChemicalTank<Gas, GasStack> tank = infoHandler.getTank();
        if (tank == null || tank.isEmpty() || tank.getCapacity() == 0) {
            return 0;
        }
        double scale = tank.getStored() / (double) tank.getCapacity();
        return MathUtils.clampToInt(Math.round(scale * (height - 2)));
    }

    @Override
    public TextureAtlasSprite getIcon() {
        if (dummy) {
            return MekanismRenderer.getChemicalTexture(dummyType);
        }
        return infoHandler.getTank() == null || infoHandler.getTank().isEmpty() ? null : MekanismRenderer.getChemicalTexture(infoHandler.getTank().getType());
    }

    @Override
    public ITextComponent getTooltipText() {
        if (dummy) {
            return TextComponentUtil.build(dummyType);
        }
        IChemicalTank<Gas, GasStack> tank = infoHandler.getTank();
        if (tank == null || tank.isEmpty()) {
            return MekanismLang.EMPTY.translate();
        }
        long amount = tank.getStored();
        if (amount == Long.MAX_VALUE) {
            return MekanismLang.GENERIC_STORED.translate(tank.getType(), MekanismLang.INFINITE);
        }
        return MekanismLang.GENERIC_STORED_MB.translate(tank.getType(), amount);
    }

    @Override
    protected void applyRenderColor() {
        if (dummy || infoHandler.getTank() == null) {
            MekanismRenderer.color(dummyType);
        } else {
            MekanismRenderer.color(infoHandler.getTank().getStack());
        }
    }

    public interface IGasInfoHandler extends ITankInfoHandler<IChemicalTank<Gas, GasStack>> {
    }
}