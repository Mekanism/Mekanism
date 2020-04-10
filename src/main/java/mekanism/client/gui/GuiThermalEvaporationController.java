package mekanism.client.gui;

import java.util.Collections;
import mekanism.client.gui.element.GuiDownArrow;
import mekanism.client.gui.element.GuiHeatInfo;
import mekanism.client.gui.element.GuiInnerScreen;
import mekanism.client.gui.element.bar.GuiBar.IBarInfoHandler;
import mekanism.client.gui.element.bar.GuiHorizontalRateBar;
import mekanism.client.gui.element.gauge.GaugeType;
import mekanism.client.gui.element.gauge.GuiFluidGauge;
import mekanism.common.MekanismLang;
import mekanism.common.base.ILangEntry;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.tile.TileEntityThermalEvaporationController;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.UnitDisplayUtils.TemperatureUnit;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class GuiThermalEvaporationController extends GuiMekanismTile<TileEntityThermalEvaporationController, MekanismTileContainer<TileEntityThermalEvaporationController>> {

    private static final double MAX_SCALE = 3_000;

    public GuiThermalEvaporationController(MekanismTileContainer<TileEntityThermalEvaporationController> container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);
        dynamicSlots = true;
    }

    @Override
    public void init() {
        super.init();
        addButton(new GuiInnerScreen(this, 48, 19, 80, 40));
        addButton(new GuiDownArrow(this, 32, 39));
        addButton(new GuiDownArrow(this, 136, 39));
        addButton(new GuiHorizontalRateBar(this, new IBarInfoHandler() {
            @Override
            public ITextComponent getTooltip() {
                return MekanismUtils.getTemperatureDisplay(tile.getTemp(), TemperatureUnit.KELVIN, true);
            }

            @Override
            public double getLevel() {
                return tile.getTemp() / MAX_SCALE;
            }
        }, 48, 63));
        addButton(new GuiFluidGauge(() -> tile.inputTank, () -> tile.getFluidTanks(null), GaugeType.STANDARD, this, 6, 13));
        addButton(new GuiFluidGauge(() -> tile.outputTank, () -> tile.getFluidTanks(null), GaugeType.STANDARD, this, 152, 13));
        addButton(new GuiHeatInfo(() -> {
            ITextComponent environment = MekanismUtils.getTemperatureDisplay(tile.totalLoss, TemperatureUnit.KELVIN, false);
            return Collections.singletonList(MekanismLang.DISSIPATED_RATE.translate(environment));
        }, this));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        drawString(MekanismLang.INVENTORY.translate(), 8, (getYSize() - 96) + 4, 0x404040);
        drawString(tile.getName(), (getXSize() / 2) - (getStringWidth(tile.getName()) / 2), 4, 0x404040);
        drawString(getStruct().translate(), 50, 21, 0x00CD00);
        drawString(MekanismLang.HEIGHT.translate(tile.height), 50, 30, 0x00CD00);
        drawString(MekanismLang.TEMPERATURE.translate(MekanismUtils.getTemperatureDisplay(tile.getTemp(), TemperatureUnit.KELVIN, true)), 50, 39, 0x00CD00);
        renderScaledText(MekanismLang.FLUID_PRODUCTION.translate(Math.round(tile.lastGain * 100D) / 100D), 50, 48, 0x00CD00, 76);
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    private ILangEntry getStruct() {
        if (tile.getActive()) {
            return MekanismLang.MULTIBLOCK_FORMED;
        } else if (tile.controllerConflict) {
            return MekanismLang.MULTIBLOCK_CONFLICT;
        }
        return MekanismLang.MULTIBLOCK_INCOMPLETE;
    }
}