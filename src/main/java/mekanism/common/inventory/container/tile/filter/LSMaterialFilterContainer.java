package mekanism.common.inventory.container.tile.filter;

import mekanism.common.content.transporter.TMaterialFilter;
import mekanism.common.registries.MekanismContainerTypes;
import mekanism.common.tile.TileEntityLogisticalSorter;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;

public class LSMaterialFilterContainer extends FilterContainer<TMaterialFilter, TileEntityLogisticalSorter> {

    public LSMaterialFilterContainer(int id, PlayerInventory inv, TileEntityLogisticalSorter tile, int index) {
        super(MekanismContainerTypes.LS_MATERIAL_FILTER, id, inv, tile, index);
    }

    public LSMaterialFilterContainer(int id, PlayerInventory inv, PacketBuffer buf) {
        this(id, inv, getTileFromBuf(buf, TileEntityLogisticalSorter.class), buf.readVarInt());
    }

    @Override
    public TMaterialFilter createNewFilter() {
        return new TMaterialFilter();
    }
}