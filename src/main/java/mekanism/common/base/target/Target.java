package mekanism.common.base.target;

import java.util.EnumMap;
import java.util.Map;
import mekanism.common.base.SplitInfo;
import net.minecraft.util.EnumFacing;

/**
 * Keeps track of a target for emitting from various networks.
 *
 * @param <HANDLER> The Handler this target keeps track of.
 * @param <TYPE> The type that is being transferred.
 * @param <EXTRA> Any extra information this target may need to keep track of.
 * @implNote Eventually when/if we do away with Joules this will be able to be converted to having TYPE always be an
 * Integer. We will then be able to use the primitive type in various places getting rid of the need for
 * IntegerTypeTarget, and having the {@link #sendGivenWithDefault(Number)} )} be implemented directly here.
 */
public abstract class Target<HANDLER, TYPE extends Number, EXTRA> {

    /**
     * Map of the sides to the handler for that side.
     */
    protected final Map<EnumFacing, HANDLER> handlers = new EnumMap<>(EnumFacing.class);
    /**
     * Map of sides that want more than we can/are willing to provide. Value is the amount they want.
     */
    protected final Map<EnumFacing, TYPE> needed = new EnumMap<>(EnumFacing.class);
    /**
     * Map of sides to how much we are giving them, this will be less than or equal to our offered split.
     */
    protected final Map<EnumFacing, TYPE> given = new EnumMap<>(EnumFacing.class);

    protected EXTRA extra;

    public void addHandler(EnumFacing side, HANDLER handler) {
        handlers.put(side, handler);
    }

    public Map<EnumFacing, HANDLER> getHandlers() {
        return handlers;
    }

    /**
     * @param side Side to add the amount to.
     * @param amountNeeded Amount that side needs.
     * @param canGive True if we are willing to give that side the full amount, then add it to the given map, otherwise
     * add it to the needed map.
     */
    public void addAmount(EnumFacing side, TYPE amountNeeded, boolean canGive) {
        if (canGive) {
            given.put(side, amountNeeded);
        } else {
            needed.put(side, amountNeeded);
        }
    }

    /**
     * Sends the calculated amount in given to each handler. If we failed to calculate a given amount for any handler in
     * this target send the given default instead.
     *
     * @param amountPer Default amount per handler in this target.
     * @return Actual total amount sent to this target.
     */
    public abstract TYPE sendGivenWithDefault(TYPE amountPer);

    /**
     * Adds a side and amount to the given map. Used for when calculating if we are willing to supply the needed
     * amount.
     */
    public void addGiven(EnumFacing side, TYPE amountNeeded) {
        given.put(side, amountNeeded);
    }

    /**
     * Gives the handler on the specified side the given amount.
     *
     * @param side Side of handler to give.
     * @param amount Amount to give.
     * @return Amount actually taken.
     */
    protected abstract TYPE acceptAmount(EnumFacing side, TYPE amount);

    public abstract TYPE simulate(HANDLER handler, EnumFacing side, EXTRA extra);

    public abstract void shiftNeeded(SplitInfo<TYPE> splitInfo);
}