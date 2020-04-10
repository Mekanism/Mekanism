package mekanism.common.base;

import mekanism.common.tile.component.TileComponentConfig;
import mekanism.common.tile.component.TileComponentEjector;
import net.minecraft.util.Direction;

/**
 * Implement this if your TileEntity is capable of being modified by a Configurator in it's 'modify' mode.
 *
 * @author AidanBrady
 */
//TODO: Make the main mekanism tile extend this directly?
public interface ISideConfiguration {

    /**
     * Gets the tile's configuration component.
     *
     * @return the tile's configuration component
     */
    TileComponentConfig getConfig();

    /**
     * Gets this machine's current orientation.
     *
     * @return machine's current orientation
     */
    Direction getOrientation();

    /**
     * Gets this machine's ejector.
     *
     * @return this machine's ejector
     */
    TileComponentEjector getEjector();
}