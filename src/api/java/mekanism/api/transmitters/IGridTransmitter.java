package mekanism.api.transmitters;

import java.util.Collection;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.Coord4D;
import mekanism.api.math.FloatingLong;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public interface IGridTransmitter<ACCEPTOR, NETWORK extends DynamicNetwork<ACCEPTOR, NETWORK, BUFFER>, BUFFER> extends ITransmitter {

    boolean hasTransmitterNetwork();

    /**
     * Gets the network currently in use by this transmitter segment.
     *
     * @return network this transmitter is using
     */
    NETWORK getTransmitterNetwork();

    /**
     * Sets this transmitter segment's network to a new value.
     *
     * @param network - network to set to
     */
    void setTransmitterNetwork(NETWORK network);

    /**
     * Only call on the server
     */
    void setRequestsUpdate();

    int getTransmitterNetworkSize();

    int getTransmitterNetworkAcceptorSize();

    ITextComponent getTransmitterNetworkNeeded();

    ITextComponent getTransmitterNetworkFlow();

    ITextComponent getTransmitterNetworkBuffer();

    int getTransmitterNetworkCapacity();

    @Nonnull
    FloatingLong getCapacityAsFloatingLong();

    int getCapacity();

    World world();

    Coord4D coord();

    Coord4D getAdjacentConnectableTransmitterCoord(Direction side);

    ACCEPTOR getAcceptor(Direction side);

    boolean isValid();

    boolean isOrphan();

    void setOrphan(boolean orphaned);

    NETWORK createEmptyNetwork();

    NETWORK createEmptyNetworkWithID(UUID networkID);

    NETWORK mergeNetworks(Collection<NETWORK> toMerge);

    NETWORK getExternalNetwork(Coord4D from);

    void takeShare();

    /**
     * @return The transmitter's buffer.
     */
    //TODO: Can we convert this to being nonnull
    @Nullable
    BUFFER getBuffer();

    /**
     * If the transmitter does not have a buffer this will try to fallback on the network's buffer.
     *
     * @return The transmitter's buffer, or if null the network's buffer.
     */
    @Nullable
    default BUFFER getBufferWithFallback() {
        BUFFER buffer = getBuffer();
        //If we don't have a buffer try falling back to the network's buffer
        if (buffer == null && hasTransmitterNetwork()) {
            return getTransmitterNetwork().getBuffer();
        }
        return buffer;
    }

    default boolean isCompatibleWith(IGridTransmitter<ACCEPTOR, NETWORK, BUFFER> other) {
        return true;
    }
}