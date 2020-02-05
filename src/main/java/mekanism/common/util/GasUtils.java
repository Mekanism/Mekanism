package mekanism.common.util;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import mekanism.api.annotations.NonNull;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.GasTank;
import mekanism.api.gas.IGasHandler;
import mekanism.api.gas.IGasItem;
import mekanism.common.base.target.GasHandlerTarget;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.config.MekanismConfig;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * A handy class containing several utilities for efficient gas transfer.
 *
 * @author AidanBrady
 */
public final class GasUtils {

    public static IGasHandler[] getConnectedAcceptors(BlockPos pos, World world, Set<Direction> sides) {
        final IGasHandler[] acceptors = new IGasHandler[]{null, null, null, null, null, null};
        EmitUtils.forEachSide(world, pos, sides, (tile, side) ->
              CapabilityUtils.getCapability(tile, Capabilities.GAS_HANDLER_CAPABILITY, side.getOpposite()).ifPresent(handler -> acceptors[side.ordinal()] = handler));
        return acceptors;
    }

    /**
     * Gets all the acceptors around a tile entity.
     *
     * @return array of IGasAcceptors
     */
    public static IGasHandler[] getConnectedAcceptors(BlockPos pos, World world) {
        return getConnectedAcceptors(pos, world, EnumSet.allOf(Direction.class));
    }

    public static boolean isValidAcceptorOnSide(TileEntity tile, Direction side) {
        if (CapabilityUtils.getCapability(tile, Capabilities.GRID_TRANSMITTER_CAPABILITY, side.getOpposite()).isPresent()) {
            return false;
        }
        return CapabilityUtils.getCapability(tile, Capabilities.GAS_HANDLER_CAPABILITY, side.getOpposite()).isPresent();
    }

    public static void clearIfInvalid(GasTank tank, Predicate<@NonNull Gas> isValid) {
        if (MekanismConfig.general.voidInvalidGases.get()) {
            Gas gas = tank.getType();
            if (!gas.isEmptyType() && !isValid.test(gas)) {
                tank.setEmpty();
            }
        }
    }

    /**
     * Removes a specified amount of gas from an IGasItem.
     *
     * @param itemStack - ItemStack of the IGasItem
     * @param type      - type of gas to remove from the IGasItem, null if it doesn't matter
     * @param amount    - amount of gas to remove from the ItemStack
     *
     * @return the GasStack removed by the IGasItem
     */
    public static GasStack removeGas(@Nonnull ItemStack itemStack, @Nonnull Gas type, int amount) {
        if (!itemStack.isEmpty() && itemStack.getItem() instanceof IGasItem) {
            IGasItem item = (IGasItem) itemStack.getItem();
            GasStack gasInItem = item.getGas(itemStack);
            if (!type.isEmptyType() && !gasInItem.isEmpty() && !gasInItem.isTypeEqual(type) || !item.canProvideGas(itemStack, type)) {
                return GasStack.EMPTY;
            }
            return item.removeGas(itemStack, amount);
        }
        return GasStack.EMPTY;
    }

    /**
     * Adds a specified amount of gas to an IGasItem.
     *
     * @param itemStack - ItemStack of the IGasItem
     * @param stack     - stack to add to the IGasItem
     *
     * @return amount of gas accepted by the IGasItem
     */
    public static int addGas(@Nonnull ItemStack itemStack, @Nonnull GasStack stack) {
        if (!itemStack.isEmpty() && itemStack.getItem() instanceof IGasItem && ((IGasItem) itemStack.getItem()).canReceiveGas(itemStack, stack.getType())) {
            return ((IGasItem) itemStack.getItem()).addGas(itemStack, stack.copy());
        }
        return 0;
    }

    /**
     * Emits gas from a central block by splitting the received stack among the sides given.
     *
     * @param stack - the stack to output
     * @param from  - the TileEntity to output from
     * @param sides - the list of sides to output from
     *
     * @return the amount of gas emitted
     */
    public static int emit(@Nonnull GasStack stack, TileEntity from, Set<Direction> sides) {
        if (stack.isEmpty()) {
            return 0;
        }

        //Fake that we have one target given we know that no sides will overlap
        // This allows us to have slightly better performance
        final GasHandlerTarget target = new GasHandlerTarget(stack);
        EmitUtils.forEachSide(from.getWorld(), from.getPos(), sides, (acceptor, side) -> {

            //Invert to get access side
            final Direction accessSide = side.getOpposite();

            //Collect cap
            CapabilityUtils.getCapability(acceptor, Capabilities.GAS_HANDLER_CAPABILITY, accessSide).ifPresent(handler -> {
                if (handler.canReceiveGas(accessSide, stack.getType())) {
                    target.addHandler(accessSide, handler);
                }
            });
        });

        int curHandlers = target.getHandlers().size();
        if (curHandlers > 0) {
            Set<GasHandlerTarget> targets = new ObjectOpenHashSet<>();
            targets.add(target);
            return EmitUtils.sendToAcceptors(targets, curHandlers, stack.getAmount(), stack);
        }
        return 0;
    }
}