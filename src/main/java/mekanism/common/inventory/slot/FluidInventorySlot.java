package mekanism.common.inventory.slot;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import mcp.MethodsReturnNonnullByDefault;
import mekanism.api.Action;
import mekanism.api.annotations.FieldsAreNonnullByDefault;
import mekanism.api.annotations.NonNull;
import mekanism.api.inventory.IMekanismInventory;
import mekanism.api.inventory.slot.IInventorySlot;
import mekanism.common.inventory.container.slot.ContainerSlotType;
import mekanism.common.util.FluidContainerUtils.ContainerEditMode;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.ItemHandlerHelper;

@FieldsAreNonnullByDefault
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FluidInventorySlot extends BasicInventorySlot {

    //TODO: Rename this maybe? It is basically used as an "input" slot where it accepts either an empty container to try and take stuff
    // OR accepts a fluid container tha that has contents that match the handler for purposes of filling the handler

    /**
     * Fills/Drains the tank depending on if this item has any contents in it
     */
    public static FluidInventorySlot input(IFluidHandler fluidHandler, Predicate<@NonNull FluidStack> isValidFluid, @Nullable IMekanismInventory inventory, int x, int y) {
        Objects.requireNonNull(fluidHandler, "Fluid handler cannot be null");
        Objects.requireNonNull(isValidFluid, "Fluid validity check cannot be null");
        return new FluidInventorySlot(fluidHandler, isValidFluid, alwaysFalse, stack -> {
            Optional<IFluidHandlerItem> cap = MekanismUtils.toOptional(FluidUtil.getFluidHandler(stack));
            if (cap.isPresent()) {
                IFluidHandlerItem fluidHandlerItem = cap.get();
                boolean hasEmpty = false;
                for (int tank = 0; tank < fluidHandlerItem.getTanks(); tank++) {
                    FluidStack fluidInTank = fluidHandlerItem.getFluidInTank(tank);
                    if (fluidInTank.isEmpty()) {
                        hasEmpty = true;
                    } else if (isValidFluid.test(fluidInTank) && fluidHandler.fill(fluidInTank, FluidAction.SIMULATE) > 0) {
                        //True if the items contents are valid and we can fill the tank with any of our contents
                        return true;
                    }
                }
                //If we have no valid fluids/can't fill the tank with it, we return if there is at least
                // one empty tank in the item so that we can then drain into it
                return hasEmpty;
            }
            return false;
        }, stack -> FluidUtil.getFluidHandler(stack).isPresent(), inventory, x, y);
    }

    /**
     * Fills/Drains the tank depending on if this item has any contents in it AND if the supplied boolean's mode supports it
     */
    public static FluidInventorySlot rotary(IFluidHandler fluidHandler, Predicate<@NonNull FluidStack> isValidFluid, BooleanSupplier modeSupplier,
          @Nullable IMekanismInventory inventory, int x, int y) {
        Objects.requireNonNull(fluidHandler, "Fluid handler cannot be null");
        Objects.requireNonNull(isValidFluid, "Fluid validity check cannot be null");
        Objects.requireNonNull(modeSupplier, "Mode supplier cannot be null");
        return new FluidInventorySlot(fluidHandler, isValidFluid, alwaysFalse, stack -> {
            Optional<IFluidHandlerItem> cap = MekanismUtils.toOptional(FluidUtil.getFluidHandler(stack));
            if (cap.isPresent()) {
                boolean mode = modeSupplier.getAsBoolean();
                //Mode == true if fluid to gas
                IFluidHandlerItem fluidHandlerItem = cap.get();
                boolean allEmpty = true;
                for (int tank = 0; tank < fluidHandlerItem.getTanks(); tank++) {
                    FluidStack fluidInTank = fluidHandlerItem.getFluidInTank(tank);
                    if (!fluidInTank.isEmpty()) {
                        if (isValidFluid.test(fluidInTank) && fluidHandler.fill(fluidInTank, FluidAction.SIMULATE) > 0) {
                            //True if we are the input tank and the items contents are valid and can fill the tank with any of our contents
                            return mode;
                        }
                        allEmpty = false;
                    }
                }
                //We want to try and drain the tank AND we are not the input tank
                return allEmpty && !mode;
            }
            return false;
        }, stack -> {
            LazyOptional<IFluidHandlerItem> capability = FluidUtil.getFluidHandler(stack);
            if (capability.isPresent()) {
                if (modeSupplier.getAsBoolean()) {
                    //Input tank, so we want to fill it
                    IFluidHandlerItem fluidHandlerItem = MekanismUtils.toOptional(capability).get();
                    for (int tank = 0; tank < fluidHandlerItem.getTanks(); tank++) {
                        FluidStack fluidInTank = fluidHandlerItem.getFluidInTank(tank);
                        if (!fluidInTank.isEmpty() && isValidFluid.test(fluidInTank)) {
                            return true;
                        }
                    }
                    return false;
                }
                //Output tank, so we want to drain
                return isNonFullFluidContainer(capability);
            }
            return false;
        }, inventory, x, y);
    }

    /**
     * Fills the tank from this item
     */
    public static FluidInventorySlot fill(IFluidHandler fluidHandler, Predicate<@NonNull FluidStack> isValidFluid, @Nullable IMekanismInventory inventory, int x, int y) {
        Objects.requireNonNull(fluidHandler, "Fluid handler cannot be null");
        Objects.requireNonNull(isValidFluid, "Fluid validity check cannot be null");
        return new FluidInventorySlot(fluidHandler, isValidFluid, alwaysFalse, stack -> {
            Optional<IFluidHandlerItem> cap = MekanismUtils.toOptional(FluidUtil.getFluidHandler(stack));
            if (cap.isPresent()) {
                IFluidHandlerItem fluidHandlerItem = cap.get();
                for (int tank = 0; tank < fluidHandlerItem.getTanks(); tank++) {
                    FluidStack fluidInTank = fluidHandlerItem.getFluidInTank(tank);
                    if (!fluidInTank.isEmpty() && isValidFluid.test(fluidInTank) && fluidHandler.fill(fluidInTank, FluidAction.SIMULATE) > 0) {
                        //True if we can fill the tank with any of our contents
                        // Note: We need to recheck the fact the fluid is not empty and that it is valid,
                        // in case the item has multiple tanks and only some of the fluids are valid
                        return true;
                    }
                }
            }
            return false;
        }, stack -> {
            //Allow for any fluid containers, but we have a more restrictive canInsert so that we don't insert all items
            //TODO: Check the other ones to see if we need something like this for them
            return FluidUtil.getFluidHandler(stack).isPresent();
        }, inventory, x, y);
    }

    /**
     * Accepts any items that can be filled with the current contents of the fluid tank, or if it is a fluid container and the tank is currently empty
     *
     * Drains the tank into this item.
     */
    public static FluidInventorySlot drain(IFluidHandler fluidHandler, @Nullable IMekanismInventory inventory, int x, int y) {
        Objects.requireNonNull(fluidHandler, "Fluid handler cannot be null");
        //TODO: Stacked buckets are not accepted by this because FluidBucketWrapper#fill returns false if it is stacked
        // One potential fix would be to copy it to a size of 1
        return new FluidInventorySlot(fluidHandler, alwaysFalse, stack -> {
            Optional<IFluidHandlerItem> cap = MekanismUtils.toOptional(FluidUtil.getFluidHandler(stack));
            if (cap.isPresent()) {
                IFluidHandlerItem itemFluidHandler = cap.get();
                boolean allEmpty = true;
                for (int tank = 0; tank < fluidHandler.getTanks(); tank++) {
                    FluidStack fluidInTank = fluidHandler.getFluidInTank(tank);
                    if (!fluidInTank.isEmpty()) {
                        if (itemFluidHandler.fill(fluidInTank, FluidAction.SIMULATE) > 0) {
                            //True if the tanks contents are valid and we can fill the item with any of the contents
                            return true;
                        }
                        allEmpty = false;
                    }
                }
                return allEmpty;
            }
            return false;
        }, stack -> isNonFullFluidContainer(FluidUtil.getFluidHandler(stack)), inventory, x, y);
    }

    //TODO: Should we make this also have the fluid type have to match a desired type???
    private static boolean isNonFullFluidContainer(LazyOptional<IFluidHandlerItem> capability) {
        Optional<IFluidHandlerItem> cap = MekanismUtils.toOptional(capability);
        if (cap.isPresent()) {
            IFluidHandlerItem fluidHandler = cap.get();
            for (int tank = 0; tank < fluidHandler.getTanks(); tank++) {
                if (fluidHandler.getFluidInTank(tank).getAmount() < fluidHandler.getTankCapacity(tank)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    private final Predicate<@NonNull FluidStack> isValidFluid;
    private final IFluidHandler fluidHandler;

    private FluidInventorySlot(IFluidHandler fluidHandler, Predicate<@NonNull ItemStack> canExtract, Predicate<@NonNull ItemStack> canInsert,
          Predicate<@NonNull ItemStack> validator, @Nullable IMekanismInventory inventory, int x, int y) {
        this(fluidHandler, fluid -> true, canExtract, canInsert, validator, inventory, x, y);
    }

    private FluidInventorySlot(IFluidHandler fluidHandler, Predicate<@NonNull FluidStack> isValidFluid, Predicate<@NonNull ItemStack> canExtract,
          Predicate<@NonNull ItemStack> canInsert, Predicate<@NonNull ItemStack> validator, @Nullable IMekanismInventory inventory, int x, int y) {
        super(canExtract, canInsert, validator, inventory, x, y);
        this.fluidHandler = fluidHandler;
        this.isValidFluid = isValidFluid;
    }

    @Override
    protected ContainerSlotType getSlotType() {
        return ContainerSlotType.EXTRA;
    }

    public void handleTank(IInventorySlot outputSlot, ContainerEditMode editMode) {
        if (!isEmpty()) {
            if (editMode == ContainerEditMode.FILL) {
                drainTank(outputSlot);
            } else if (editMode == ContainerEditMode.EMPTY) {
                fillTank(outputSlot);
            } else if (editMode == ContainerEditMode.BOTH) {
                Optional<IFluidHandlerItem> cap = MekanismUtils.toOptional(FluidUtil.getFluidHandler(current));
                if (cap.isPresent()) {
                    IFluidHandlerItem fluidHandlerItem = cap.get();
                    boolean hasEmpty = false;
                    for (int tank = 0; tank < fluidHandlerItem.getTanks(); tank++) {
                        FluidStack fluidInTank = fluidHandlerItem.getFluidInTank(tank);
                        if (fluidInTank.isEmpty()) {
                            hasEmpty = true;
                        } else if (isValidFluid.test(fluidInTank) && fluidHandler.fill(fluidInTank, FluidAction.SIMULATE) > 0) {
                            //If we support either mode and our container is not empty, then drain the item into the tank
                            fillTank(outputSlot);
                            return;
                        }
                    }
                    //If we have no valid fluids/can't fill the tank with it, we return if there is at least
                    // one empty tank in the item so that we can then drain into it
                    if (hasEmpty) {
                        //If we have an empty container and support either mode, then fill the container
                        drainTank(outputSlot);
                    }
                }
            }
        }
    }

    /**
     * Fills tank from slot
     *
     * @param outputSlot The slot to move our container to after draining the item.
     */
    public void fillTank(IInventorySlot outputSlot) {
        if (!isEmpty()) {
            //Try filling from the tank's item
            Optional<IFluidHandlerItem> capability = MekanismUtils.toOptional(FluidUtil.getFluidHandler(current));
            if (capability.isPresent()) {
                IFluidHandlerItem itemFluidHandler = capability.get();
                int itemTanks = itemFluidHandler.getTanks();
                if (itemTanks == 1) {
                    //If we only have one tank just directly check against that fluid instead of performing extra calculations to properly handle multiple tanks
                    FluidStack fluidInItem = itemFluidHandler.getFluidInTank(0);
                    if (!fluidInItem.isEmpty() && isValidFluid.test(fluidInItem)) {
                        //If we have a fluid that is valid for our fluid handler, attempt to drain it into our fluid handler
                        drainItemAndMove(outputSlot, fluidInItem);
                    }
                } else if (itemTanks > 1) {
                    //If we have more than one tank in our item then handle calculating the different drains that will occur for filling our fluid handler
                    // We start by gathering all the fluids in the item that we are able to drain and are valid for the tank,
                    // combining same fluid types into a single fluid stack
                    Map<FluidInfo, FluidStack> knownFluids = new Object2ObjectOpenHashMap<>();
                    for (int itemTank = 0; itemTank < itemTanks; itemTank++) {
                        FluidStack fluidInItem = itemFluidHandler.getFluidInTank(itemTank);
                        if (!fluidInItem.isEmpty()) {
                            FluidInfo info = new FluidInfo(fluidInItem);
                            FluidStack knownFluid = knownFluids.get(info);
                            //If we have a fluid that can be drained from the item and is valid then we add it to our known fluids
                            // Note: We only bother checking if it can be drained if we do not already have it as a known fluid
                            if (knownFluid == null) {
                                if (!itemFluidHandler.drain(fluidInItem, FluidAction.SIMULATE).isEmpty() && isValidFluid.test(fluidInItem)) {
                                    knownFluids.put(info, fluidInItem.copy());
                                }
                            } else {
                                knownFluid.grow(fluidInItem.getAmount());
                            }
                        }
                    }
                    //If we found any fluids that we can drain, attempt to drain them into our item
                    for (FluidStack knownFluid : knownFluids.values()) {
                        if (drainItemAndMove(outputSlot, knownFluid) && isEmpty()) {
                            //If we moved the item after draining it and we now don't have an item to try and fill
                            // then just exit instead of checking the other types of fluids
                            //TODO: Eventually fix the case where the item we are draining has multiple
                            // types of fluids so we may not actually want to move it immediately
                            // Note: Not sure what a good middle ground is because if the item can stack like buckets
                            // then how do we know when to move it
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Drains tank into slot
     *
     * @param outputSlot The slot to move our container to after draining the tank.
     */
    public void drainTank(IInventorySlot outputSlot) {
        int tanks = fluidHandler.getTanks();
        //Verify we have an item, we have tanks that may need to be drained, and that our item is a fluid handler
        if (!isEmpty() && tanks > 0 && FluidUtil.getFluidHandler(current).isPresent()) {
            if (tanks == 1) {
                //If we only have one tank just directly check against that fluid instead of performing extra calculations
                // that ensure we only execute drain once per fluid type
                FluidStack fluidInTank = fluidHandler.getFluidInTank(0);
                if (!fluidInTank.isEmpty()) {
                    //If we have a fluid attempt to drain it into our item
                    fillItemAndMove(outputSlot, fluidInTank);
                }
            } else {
                //Otherwise try handling draining out of all tanks
                // We start by gathering all the fluids in the fluid handler that we are able to drain,
                // combining same fluid types into a single fluid stack
                Map<FluidInfo, FluidStack> knownFluids = new Object2ObjectOpenHashMap<>();
                for (int tank = 0; tank < tanks; tank++) {
                    FluidStack fluidInTank = fluidHandler.getFluidInTank(tank);
                    if (!fluidInTank.isEmpty()) {
                        FluidInfo info = new FluidInfo(fluidInTank);
                        FluidStack knownFluid = knownFluids.get(info);
                        //If we have a fluid and it can be drained from the fluid handler then we add it to our known fluids
                        // Note: We only bother checking if it can be drained if we do not already have it as a known fluid
                        if (knownFluid == null) {
                            if (!fluidHandler.drain(fluidInTank, FluidAction.SIMULATE).isEmpty()) {
                                knownFluids.put(info, fluidInTank.copy());
                            }
                        } else {
                            knownFluid.grow(fluidInTank.getAmount());
                        }
                    }
                }
                //If we found any fluids that we can drain, attempt to drain them into our item
                for (FluidStack knownFluid : knownFluids.values()) {
                    if (fillItemAndMove(outputSlot, knownFluid) && isEmpty()) {
                        //If we moved the item after filling it and we now don't have an item to try and fill
                        // then just exit instead of checking the other types of fluids
                        //TODO: Eventually fix the case where the item we are filling can accept multiple different
                        // types of fluids so we may not actually want to move it immediately
                        // Note: Not sure what a good middle ground is because if the item can stack like buckets
                        // then how do we know when to move it
                        break;
                    }
                }
            }
        }
    }

    /**
     * Fills our item and moves it to the given output slot. If it won't be able to move to the output slot, then we do not move it or drain our tank into the item.
     *
     * @param outputSlot      The slot our item will be moved to afterwards
     * @param fluidToTransfer The fluid we are filling the item with. This should be known to not be empty.
     *
     * @return True if we can drain the fluid from our fluid handler and the item after being filled can (and was) moved to the output slot, false otherwise
     */
    private boolean fillItemAndMove(IInventorySlot outputSlot, FluidStack fluidToTransfer) {
        FluidStack simulatedDrain = fluidHandler.drain(fluidToTransfer, FluidAction.SIMULATE);
        if (simulatedDrain.isEmpty()) {
            //If we cannot actually drain from our fluid handler then just exit early
            return false;
        }
        ItemStack inputCopy = StackUtils.size(current, 1);
        Optional<IFluidHandlerItem> cap = MekanismUtils.toOptional(FluidUtil.getFluidHandler(inputCopy));
        if (!cap.isPresent()) {
            //The capability should be present based on checks that happen before this method, but if for some reason it isn't just exit
            return false;
        }
        IFluidHandlerItem fluidHandlerItem = cap.get();
        //Fill the stack, note our stack is a copy so this is how we simulate to get the proper "container" item
        // and it does not actually matter that we are directly executing on the item
        FluidStack toDrain = new FluidStack(fluidToTransfer, fluidHandlerItem.fill(fluidToTransfer, FluidAction.EXECUTE));
        if (moveItem(outputSlot, fluidHandlerItem.getContainer())) {
            if (!toDrain.isEmpty()) {
                //Actually drain the fluid from our handler
                fluidHandler.drain(toDrain, FluidAction.EXECUTE);
            }
            return true;
        }
        return false;
    }

    /**
     * Fills our fluid handler from the item and then moves the item to the given output slot. If it won't be able to move to the output slot, then we do not move it or
     * drain our item into the fluid handler.
     *
     * @param outputSlot      The slot our item will be moved to afterwards
     * @param fluidToTransfer The fluid we are draining from the item. This should be known to not be empty, and to have passed any validity checks.
     *
     * @return True if we can drain the fluid from the item and the item after being drained can (and was) moved to the output slot, false otherwise
     */
    private boolean drainItemAndMove(IInventorySlot outputSlot, FluidStack fluidToTransfer) {
        int simulatedFill = fluidHandler.fill(fluidToTransfer, FluidAction.SIMULATE);
        if (simulatedFill == 0) {
            //If we cannot actually fill our fluid handler then just exit early
            return false;
        }

        ItemStack input = StackUtils.size(current, 1);
        Optional<IFluidHandlerItem> cap = MekanismUtils.toOptional(FluidUtil.getFluidHandler(input));
        if (!cap.isPresent()) {
            //The capability should be present based on checks that happen before this method, but if for some reason it isn't just exit
            return false;
        }
        IFluidHandlerItem fluidHandlerItem = cap.get();
        //Drain the stack, note our stack is a copy so this is how we simulate to get the proper "container" item
        // and it does not actually matter that we are directly executing on the item
        FluidStack drained = fluidHandlerItem.drain(new FluidStack(fluidToTransfer, simulatedFill), FluidAction.EXECUTE);
        if (drained.isEmpty()) {
            //If we cannot actually drain from the item then just exit early
            //TODO: Verify this is true, because the filling doesn't bother exiting early maybe we want to make it do so though
            return false;
        }
        if (moveItem(outputSlot, fluidHandlerItem.getContainer())) {
            //Actually fill our handler with the fluid
            fluidHandler.fill(drained, FluidAction.EXECUTE);
            return true;
        }
        return false;
    }

    /**
     * Tries to move a stack from our slot to the output slot
     *
     * @param outputSlot  The slot we are trying to move our item to
     * @param stackToMove The stack we are moving, this is our container
     *
     * @return True if we are able to move the stack and did so, false otherwise
     */
    private boolean moveItem(IInventorySlot outputSlot, ItemStack stackToMove) {
        if (outputSlot.isEmpty()) {
            outputSlot.setStack(stackToMove);
        } else {
            ItemStack outputStack = outputSlot.getStack();
            if (!ItemHandlerHelper.canItemStacksStack(outputStack, stackToMove) || outputStack.getCount() >= outputSlot.getLimit(outputStack)) {
                //We won't be able to move our container to the output slot so exit
                return false;
            }
            if (outputSlot.growStack(1, Action.EXECUTE) != 1) {
                //TODO: Print warning about failing to increase size of stack
            }
        }
        //Note: We do not need to call onContentsChanged, because it will be done due to the stack changing from calling shrinkStack
        if (shrinkStack(1, Action.EXECUTE) != 1) {
            //TODO: Print warning about failing to shrink size of stack
        }
        return true;
    }

    /**
     * Fills tank from slot, ensuring the stack's count is one, and does not move it to an output slot afterwards
     */
    public void fillTank() {
        if (getCount() == 1) {
            //Try filling from the tank's item
            Optional<IFluidHandlerItem> capability = MekanismUtils.toOptional(FluidUtil.getFluidHandler(current));
            if (capability.isPresent()) {
                IFluidHandlerItem itemFluidHandler = capability.get();
                int tanks = itemFluidHandler.getTanks();
                if (tanks == 1) {
                    //If we only have one tank just directly check against that fluid instead of performing extra calculations to properly handle multiple tanks
                    FluidStack fluidInItem = itemFluidHandler.getFluidInTank(0);
                    if (!fluidInItem.isEmpty() && isValidFluid.test(fluidInItem)) {
                        //If we have a fluid that is valid for our fluid handler, attempt to drain it into our fluid handler
                        if (fillHandlerFromOther(fluidHandler, itemFluidHandler, fluidInItem)) {
                            //Update the stack to the empty container
                            setStack(itemFluidHandler.getContainer());
                        }
                    }
                } else if (tanks > 1) {
                    //If we have more than one tank in our item then handle calculating the different drains that will occur for filling our fluid handler
                    // We start by gathering all the fluids in the item that we are able to drain and are valid for the tank,
                    // combining same fluid types into a single fluid stack
                    Map<FluidInfo, FluidStack> knownFluids = new Object2ObjectOpenHashMap<>();
                    for (int tank = 0; tank < tanks; tank++) {
                        FluidStack fluidInItem = itemFluidHandler.getFluidInTank(tank);
                        if (!fluidInItem.isEmpty()) {
                            FluidInfo info = new FluidInfo(fluidInItem);
                            FluidStack knownFluid = knownFluids.get(info);
                            //If we have a fluid that can be drained from the item and is valid then we add it to our known fluids
                            if (knownFluid == null) {
                                if (!itemFluidHandler.drain(fluidInItem, FluidAction.SIMULATE).isEmpty() && isValidFluid.test(fluidInItem)) {
                                    knownFluids.put(info, fluidInItem.copy());
                                }
                            } else {
                                knownFluid.grow(fluidInItem.getAmount());
                            }
                        }
                    }
                    if (!knownFluids.isEmpty()) {
                        //If we found any fluids that we can drain, attempt to drain them into our item
                        boolean changed = false;
                        for (FluidStack knownFluid : knownFluids.values()) {
                            if (fillHandlerFromOther(fluidHandler, itemFluidHandler, knownFluid)) {
                                changed = true;
                            }
                        }
                        if (changed) {
                            //Update the stack to the empty container
                            setStack(itemFluidHandler.getContainer());
                        }
                    }
                }
            }
        }
    }

    /**
     * Tries to drain the specified fluid from one fluid handler, while filling another fluid handler.
     *
     * @param handlerToFill  The fluid handler to fill
     * @param handlerToDrain The fluid handler to drain
     * @param fluid          The fluid to attempt to transfer
     *
     * @return True if we managed to transfer any contents, false otherwise
     */
    private boolean fillHandlerFromOther(IFluidHandler handlerToFill, IFluidHandler handlerToDrain, FluidStack fluid) {
        //Check how much of this fluid type we are actually able to drain from the handler we are draining
        FluidStack simulatedDrain = handlerToDrain.drain(fluid, FluidAction.SIMULATE);
        if (!simulatedDrain.isEmpty()) {
            //Check how much of it we will be able to put into the handler we are filling
            int simulatedFill = handlerToFill.fill(simulatedDrain, FluidAction.SIMULATE);
            if (simulatedFill > 0) {
                //Drain the handler to drain, filling the handler to fill while we are at it
                handlerToFill.fill(handlerToDrain.drain(new FluidStack(fluid, simulatedFill), FluidAction.EXECUTE), FluidAction.EXECUTE);
                return true;
            }
        }
        return false;
    }

    /**
     * Helper class to make comparing fluids ignoring amount easier
     */
    private static class FluidInfo {

        private final FluidStack fluidStack;

        public FluidInfo(FluidStack fluidStack) {
            this.fluidStack = fluidStack;
        }

        @Override
        public boolean equals(Object other) {
            return other == this || other instanceof FluidInfo && fluidStack.isFluidEqual(((FluidInfo) other).fluidStack);
        }

        @Override
        public int hashCode() {
            int code = 1;
            code = 31 * code + fluidStack.getFluid().hashCode();
            if (fluidStack.hasTag()) {
                code = 31 * code + fluidStack.getTag().hashCode();
            }
            return code;
        }
    }
}