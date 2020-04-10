package mekanism.generators.client.gui.element;

import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.element.tab.GuiTabElementType;
import mekanism.client.gui.element.tab.TabType;
import mekanism.common.base.ILangEntry;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import mekanism.generators.client.gui.element.GuiReactorTab.ReactorTab;
import mekanism.generators.common.GeneratorsLang;
import mekanism.generators.common.MekanismGenerators;
import mekanism.generators.common.network.PacketGeneratorsGuiButtonPress;
import mekanism.generators.common.network.PacketGeneratorsGuiButtonPress.ClickedGeneratorsTileButton;
import mekanism.generators.common.tile.reactor.TileEntityReactorController;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class GuiReactorTab extends GuiTabElementType<TileEntityReactorController, ReactorTab> {

    public GuiReactorTab(IGuiWrapper gui, TileEntityReactorController tile, ReactorTab type) {
        super(gui, tile, type);
    }

    public enum ReactorTab implements TabType<TileEntityReactorController> {
        HEAT(MekanismUtils.getResource(ResourceType.GUI, "heat.png"), GeneratorsLang.HEAT_TAB, 6, ClickedGeneratorsTileButton.TAB_HEAT),
        FUEL(MekanismGenerators.rl(ResourceType.GUI.getPrefix() + "fuel.png"), GeneratorsLang.FUEL_TAB, 34, ClickedGeneratorsTileButton.TAB_FUEL),
        STAT(MekanismUtils.getResource(ResourceType.GUI, "stats.png"), GeneratorsLang.STATS_TAB, 62, ClickedGeneratorsTileButton.TAB_STATS);

        private final ClickedGeneratorsTileButton button;
        private final ILangEntry description;
        private final ResourceLocation path;
        private final int yPos;

        ReactorTab(ResourceLocation path, ILangEntry description, int y, ClickedGeneratorsTileButton button) {
            this.path = path;
            this.description = description;
            yPos = y;
            this.button = button;
        }

        @Override
        public ResourceLocation getResource() {
            return path;
        }

        @Override
        public void onClick(TileEntityReactorController tile) {
            MekanismGenerators.packetHandler.sendToServer(new PacketGeneratorsGuiButtonPress(button, tile.getPos()));
        }

        @Override
        public ITextComponent getDescription() {
            return description.translate();
        }

        @Override
        public int getYPos() {
            return yPos;
        }
    }
}