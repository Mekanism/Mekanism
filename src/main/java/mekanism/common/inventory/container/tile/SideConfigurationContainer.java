package mekanism.common.inventory.container.tile;

import mekanism.common.base.ISideConfiguration;
import mekanism.common.registries.MekanismContainerTypes;
import mekanism.common.tile.base.TileEntityMekanism;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;

public class SideConfigurationContainer extends EmptyTileContainer<TileEntityMekanism> {

    public SideConfigurationContainer(int id, PlayerInventory inv, TileEntityMekanism tile) {
        super(MekanismContainerTypes.SIDE_CONFIGURATION, id, inv, tile);
    }

    public SideConfigurationContainer(int id, PlayerInventory inv, PacketBuffer buf) {
        this(id, inv, getTileFromBuf(buf, TileEntityMekanism.class));
    }

    @Override
    protected void addContainerTrackers() {
        if (tile instanceof ISideConfiguration) {
            ((ISideConfiguration) tile).getConfig().addContainerTrackers(this);
        }
    }
}