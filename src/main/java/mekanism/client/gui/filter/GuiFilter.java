package mekanism.client.gui.filter;

import mekanism.client.gui.GuiMekanismTile;
import mekanism.common.Mekanism;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.network.PacketGuiButtonPress;
import mekanism.common.network.PacketGuiButtonPress.ClickedTileButton;
import mekanism.common.tile.base.TileEntityMekanism;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public abstract class GuiFilter<TILE extends TileEntityMekanism, CONTAINER extends MekanismTileContainer<TILE>> extends GuiMekanismTile<TILE, CONTAINER> {

    protected GuiFilter(CONTAINER container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);
    }

    protected abstract void addButtons();

    protected void sendPacketToServer(ClickedTileButton button) {
        sendPacketToServer(button, 0);
    }

    protected void sendPacketToServer(ClickedTileButton button, int extra) {
        Mekanism.packetHandler.sendToServer(new PacketGuiButtonPress(button, tile, extra));
    }

    @Override
    public void init() {
        super.init();
        addButtons();
    }
}