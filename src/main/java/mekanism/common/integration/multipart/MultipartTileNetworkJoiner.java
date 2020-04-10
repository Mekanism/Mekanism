package mekanism.common.integration.multipart;

/*
 * Used to route {@link ITileNetwork} packets sent to multipart containers with more than one possible recipient.<br>
 * <br>
 * When MCMP is enabled single byte Direction ordinal headers are added to packets sent by glow panels and transmitters that are then used by this class to route packets
 * to the part attached to the appropriate side.<br>
 * <br>
 * In this case, since transmitters do not attach to a side and therefore have no matching Direction the special value 6 is used to represent the center slot.
 */
/*public class MultipartTileNetworkJoiner implements ITileNetwork {

    //TODO nullable-ish enum map
    private final Int2ObjectMap<ITileNetwork> tileSideMap;

    /**
     * Called by MCMP's multipart container when more than one part implements {@link ITileNetwork}.<br>
     * <br>
     * Builds an internal map of part slots to {@link ITileNetwork} implementations in order to route packets.
     *
     * @param tileList A list of the tile entities that implement {@link ITileNetwork} in the container.
     *
    public MultipartTileNetworkJoiner(List<ITileNetwork> tileList) {
        tileSideMap = new Int2ObjectArrayMap<>(7);
        IMultipartContainer container = null;

        TileEntity first = (TileEntity) tileList.get(0);
        IBlockReader world = first.getWorld();
        if (world instanceof IMultipartBlockAccess) {
            container = ((IMultipartBlockAccess) world).getPartInfo().getContainer();
        } else {
            TileEntity worldTile = first.getWorld().getTileEntity(first.getPos());
            if (worldTile instanceof IMultipartContainer) {
                container = (IMultipartContainer) worldTile;
            }
        }
        if (container != null) {
            for (IPartSlot slot : container.getParts().keySet()) {
                Optional<IMultipartTile> partTile = container.getPartTile(slot);
                if (partTile.isPresent()) {
                    int tileIndex = tileList.indexOf(partTile.get().getTileEntity());
                    if (tileIndex >= 0) {
                        byte slotValue = slot instanceof EnumFaceSlot ? (byte) ((EnumFaceSlot) slot).ordinal() : 6;
                        tileSideMap.put(slotValue, tileList.get(tileIndex));
                    }
                }
            }
        }
    }

    /**
     * Determines whether or not an {@link ITileNetwork} joiner is needed and, if so, adds a single byte header used to route packets inside multipart containers.
     *
     * @param entity The entity for which <code>getNetworkedData</code> is being called
     * @param data   The network data list
     * @param facing The side this part is attached to or <code>null</code> for the center slot
     *
    public static void addMultipartHeader(TileEntity entity, TileNetworkList data, Direction facing) {
        int tileNetworkParts = 0;
        IMultipartContainer container = MultipartMekanism.getContainer(entity.getWorld(), entity.getPos());
        if (container != null) {
            for (IPartSlot slot : container.getParts().keySet()) {
                TileEntity part = container.getPartTile(slot).map(IMultipartTile::getTileEntity).orElse(null);
                if (part instanceof ITileNetwork) {
                    tileNetworkParts++;
                    if (tileNetworkParts > 1) {
                        break;
                    }
                }
            }
        }
        if (tileNetworkParts > 1) {
            data.add(0, (byte) (facing == null ? 6 : facing.ordinal()));
        }
    }

    @Override
    public void handlePacketData(PacketBuffer dataStream) throws Exception {
        while (dataStream.readableBytes() > 0) {
            byte side = dataStream.readByte();
            ITileNetwork networkTile = tileSideMap.get(side);
            if (networkTile == null) {
                break;
            }
            networkTile.handlePacketData(dataStream);
        }
    }

    @Override
    public TileNetworkList getNetworkedData(TileNetworkList data) {
        TileNetworkList childData = new TileNetworkList();
        for (IntIterator iterator = tileSideMap.keySet().iterator(); iterator.hasNext(); ) {
            int slotValue = iterator.nextInt();
            tileSideMap.get(slotValue).getNetworkedData(childData);
            data.addAll(childData);
            childData.clear();
        }
        return data;
    }
}*/