package mekanism.client.gui.chemical;

import java.util.Arrays;
import mekanism.client.gui.GuiMekanismTile;
import mekanism.client.gui.element.GuiBucketIO;
import mekanism.client.gui.element.GuiEnergyInfo;
import mekanism.client.gui.element.GuiProgress;
import mekanism.client.gui.element.GuiProgress.IProgressInfoHandler;
import mekanism.client.gui.element.GuiProgress.ProgressBar;
import mekanism.client.gui.element.GuiRedstoneControl;
import mekanism.client.gui.element.GuiSlot;
import mekanism.client.gui.element.GuiSlot.SlotOverlay;
import mekanism.client.gui.element.GuiSlot.SlotType;
import mekanism.client.gui.element.bar.GuiHorizontalPowerBar;
import mekanism.client.gui.element.gauge.GuiFluidGauge;
import mekanism.client.gui.element.gauge.GuiGasGauge;
import mekanism.client.gui.element.gauge.GuiGauge;
import mekanism.client.gui.element.gauge.GuiGauge.Type;
import mekanism.client.gui.element.tab.GuiSecurityTab;
import mekanism.client.gui.element.tab.GuiUpgradeTab;
import mekanism.common.inventory.container.tile.ChemicalWasherContainer;
import mekanism.common.tile.TileEntityChemicalWasher;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import mekanism.common.util.text.EnergyDisplay;
import mekanism.common.util.text.TextComponentUtil;
import mekanism.common.util.text.Translation;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class GuiChemicalWasher extends GuiMekanismTile<TileEntityChemicalWasher, ChemicalWasherContainer> {

    public GuiChemicalWasher(ChemicalWasherContainer container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);
    }

    @Override
    public void init() {
        super.init();
        ResourceLocation resource = getGuiLocation();
        addButton(new GuiSecurityTab<>(this, tileEntity, resource));
        addButton(new GuiRedstoneControl(this, tileEntity, resource));
        addButton(new GuiUpgradeTab(this, tileEntity, resource));
        addButton(new GuiHorizontalPowerBar(this, tileEntity, resource, 115, 75));
        addButton(new GuiBucketIO(this, resource));
        addButton(new GuiEnergyInfo(() -> Arrays.asList(
              TextComponentUtil.build(Translation.of("gui.mekanism.using"), ": ", EnergyDisplay.of(tileEntity.clientEnergyUsed), "/t"),
              TextComponentUtil.build(Translation.of("gui.mekanism.needed"), ": ", EnergyDisplay.of(tileEntity.getNeededEnergy()))
        ), this, resource));
        addButton(new GuiFluidGauge(() -> tileEntity.fluidTank, Type.STANDARD, this, resource, 5, 4));
        addButton(new GuiGasGauge(() -> tileEntity.inputTank, GuiGauge.Type.STANDARD, this, resource, 26, 13));
        addButton(new GuiGasGauge(() -> tileEntity.outputTank, GuiGauge.Type.STANDARD, this, resource, 133, 13));
        addButton(new GuiSlot(SlotType.NORMAL, this, resource, 154, 4).with(SlotOverlay.POWER));
        addButton(new GuiSlot(SlotType.NORMAL, this, resource, 154, 55).with(SlotOverlay.MINUS));
        addButton(new GuiProgress(new IProgressInfoHandler() {
            @Override
            public double getProgress() {
                return tileEntity.getActive() ? 1 : 0;
            }
        }, ProgressBar.LARGE_RIGHT, this, resource, 62, 38));
    }

    @Override
    protected ResourceLocation getGuiLocation() {
        return MekanismUtils.getResource(ResourceType.GUI, "chemical_washer.png");
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(int xAxis, int yAxis) {
        super.drawGuiContainerBackgroundLayer(xAxis, yAxis);
        drawTexturedRect(guiLeft + 116, guiTop + 76, 176, 0, tileEntity.getScaledEnergyLevel(52), 4);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        drawString(tileEntity.getName(), 45, 4, 0x404040);
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }
}