package mekanism.generators.client.gui;

import java.util.Arrays;
import mekanism.api.text.EnumColor;
import mekanism.client.gui.GuiMekanismTile;
import mekanism.client.gui.element.GuiEnergyInfo;
import mekanism.common.config.MekanismConfig;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import mekanism.common.util.text.EnergyDisplay;
import mekanism.common.util.text.TextComponentUtil;
import mekanism.common.util.text.Translation;
import mekanism.generators.client.gui.element.GuiTurbineTab;
import mekanism.generators.client.gui.element.GuiTurbineTab.TurbineTab;
import mekanism.generators.common.config.MekanismGeneratorsConfig;
import mekanism.generators.common.content.turbine.TurbineUpdateProtocol;
import mekanism.generators.common.inventory.container.turbine.TurbineStatsContainer;
import mekanism.generators.common.tile.turbine.TileEntityTurbineCasing;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class GuiTurbineStats extends GuiMekanismTile<TileEntityTurbineCasing, TurbineStatsContainer> {

    public GuiTurbineStats(TurbineStatsContainer container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);
    }

    @Override
    public void init() {
        super.init();
        ResourceLocation resource = getGuiLocation();
        addButton(new GuiTurbineTab(this, tileEntity, TurbineTab.MAIN, resource));
        addButton(new GuiEnergyInfo(() -> {
            double producing = tileEntity.structure == null ? 0 : tileEntity.structure.clientFlow * (MekanismConfig.general.maxEnergyPerSteam.get() / TurbineUpdateProtocol.MAX_BLADES) *
                                                                  Math.min(tileEntity.structure.blades, tileEntity.structure.coils * MekanismGeneratorsConfig.generators.turbineBladesPerCoil.get());
            return Arrays.asList(TextComponentUtil.build(Translation.of("gui.mekanism.storing"), ": ", EnergyDisplay.of(tileEntity.getEnergy(), tileEntity.getMaxEnergy())),
                  TextComponentUtil.build(Translation.of("gui.mekanism.producing"), ": ", EnergyDisplay.of(producing), "/t"));
        }, this, resource));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        drawCenteredText(TextComponentUtil.translate("gui.mekanism.turbineStates"), 0, xSize, 6, 0x404040);
        if (tileEntity.structure != null) {
            ITextComponent limiting = TextComponentUtil.build(EnumColor.DARK_RED, " (", Translation.of("gui.mekanism.limiting"), ")");
            int lowerVolume = tileEntity.structure.lowerVolume;
            int clientDispersers = tileEntity.structure.clientDispersers;
            int vents = tileEntity.structure.vents;
            drawString(TextComponentUtil.build(Translation.of("gui.mekanism.tankVolume"), ": " + lowerVolume), 8, 26, 0x404040);
            boolean dispersersLimiting = lowerVolume * clientDispersers * MekanismGeneratorsConfig.generators.turbineDisperserGasFlow.get()
                                         < vents * MekanismGeneratorsConfig.generators.turbineVentGasFlow.get();
            boolean ventsLimiting = lowerVolume * clientDispersers * MekanismGeneratorsConfig.generators.turbineDisperserGasFlow.get()
                                    > vents * MekanismGeneratorsConfig.generators.turbineVentGasFlow.get();
            drawString(TextComponentUtil.translate("gui.mekanism.steamFlow"), 8, 40, 0x797979);
            drawString(TextComponentUtil.build(Translation.of("gui.mekanism.dispersers"),
                  ": " + clientDispersers, (dispersersLimiting ? limiting : "")), 14, 49, 0x404040);
            drawString(TextComponentUtil.build(Translation.of("gui.mekanism.vents"), ": " + vents, (ventsLimiting ? limiting : "")), 14, 58, 0x404040);
            int coils = tileEntity.structure.coils;
            int blades = tileEntity.structure.blades;
            drawString(TextComponentUtil.translate("gui.mekanism.production"), 8, 72, 0x797979);
            drawString(TextComponentUtil.build(Translation.of("gui.mekanism.blades"), ": " + blades, (coils * 4 > blades ? limiting : "")), 14, 81, 0x404040);
            drawString(TextComponentUtil.build(Translation.of("gui.mekanism.coils"), ": " + coils, (coils * 4 < blades ? limiting : "")), 14, 90, 0x404040);
            double energyMultiplier = (MekanismConfig.general.maxEnergyPerSteam.get() / TurbineUpdateProtocol.MAX_BLADES) *
                                      Math.min(blades, coils * MekanismGeneratorsConfig.generators.turbineBladesPerCoil.get());
            double rate = lowerVolume * (clientDispersers * MekanismGeneratorsConfig.generators.turbineDisperserGasFlow.get());
            rate = Math.min(rate, vents * MekanismGeneratorsConfig.generators.turbineVentGasFlow.get());
            drawString(TextComponentUtil.build(Translation.of("gui.mekanism.maxProduction"), ": ", EnergyDisplay.of(rate * energyMultiplier)), 8, 104, 0x404040);
            drawString(TextComponentUtil.build(Translation.of("gui.mekanism.maxWaterOutput"),
                  ": " + tileEntity.structure.condensers * MekanismGeneratorsConfig.generators.condenserRate.get() + " mB/t"), 8, 113, 0x404040);
        }
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    protected ResourceLocation getGuiLocation() {
        return MekanismUtils.getResource(ResourceType.GUI, "null.png");
    }
}