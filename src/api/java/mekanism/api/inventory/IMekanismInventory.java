package mekanism.api.inventory;

import java.util.List;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import mcp.MethodsReturnNonnullByDefault;
import mekanism.api.Action;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;

//TODO: Move all secondary slot calculations like gas/fluid/energy input/output to the slots so that they can mutate the stack properly instead of breaking API
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IMekanismInventory extends ISidedItemHandler {

    /**
     * Used to check if an instance of {@link IMekanismInventory} actually has an inventory.
     *
     * @return True if we are actually an inventory.
     *
     * @apiNote If for some reason you are comparing to {@link IMekanismInventory} without having gotten the object via the item handler capability, then you must call
     * this method to make sure that it really is an inventory. As most mekanism tiles have this class in their hierarchy.
     * @implNote If this returns false the capability should not be exposed AND methods should turn reasonable defaults for not doing anything.
     */
    default boolean hasInventory() {
        return true;
    }

    //TODO: Use this for generating the container code. Should even be able to make the shift click code be based off of the logic in the individual inventory slots
    // Note: for generating container code this should pass null as the side to get all the information
    //TODO: We should have our inventories cache the list of slots for the different sides, as this method may be called a decent amount for things such as getSlots
    //TODO: Would it make sense to inline some calculations for mekanism inventories rather then getting the specific slot in an index

    /**
     * Returns the list of IInventorySlots that this inventory exposes on the given side.
     *
     * @param side The side we are interacting with the handler from (null for internal).
     *
     * @return The list of all IInventorySlots that this {@link IMekanismInventory} contains for the given side. If there are no slots for the side or {@link
     * #hasInventory()} is false then it returns an empty list.
     *
     * @implNote When side is null (an internal request), this method <em>MUST</em> return all slots in the inventory. This will be used by the container generating code
     * to add all the proper slots that are needed. Additionally, if {@link #hasInventory()} is false, this <em>MUST</em> return an empty list.
     */
    List<IInventorySlot> getInventorySlots(@Nullable Direction side);

    /**
     * Called when the contents of this inventory changes.
     */
    void onContentsChanged();

    /**
     * Returns the {@link IInventorySlot} that has the given index from the list of slots on the given side.
     *
     * @param slot The index of the slot to retrieve.
     * @param side The side we are interacting with the handler from (null for internal).
     *
     * @return The {@link IInventorySlot} that has the given index from the list of slots on the given side.
     */
    @Nullable
    default IInventorySlot getInventorySlot(int slot, @Nullable Direction side) {
        List<IInventorySlot> slots = getInventorySlots(side);
        return slot >= 0 && slot < slots.size() ? slots.get(slot) : null;
    }

    @Override
    default void setStackInSlot(int slot, ItemStack stack, @Nullable Direction side) {
        IInventorySlot inventorySlot = getInventorySlot(slot, side);
        if (inventorySlot != null) {
            inventorySlot.setStack(stack);
        }
    }

    @Override
    default int getSlots(@Nullable Direction side) {
        return getInventorySlots(side).size();
    }

    @Override
    default ItemStack getStackInSlot(int slot, @Nullable Direction side) {
        IInventorySlot inventorySlot = getInventorySlot(slot, side);
        return inventorySlot == null ? ItemStack.EMPTY : inventorySlot.getStack();
    }

    @Override
    default ItemStack insertItem(int slot, ItemStack stack, @Nullable Direction side, Action action) {
        IInventorySlot inventorySlot = getInventorySlot(slot, side);
        if (inventorySlot == null) {
            return stack;
        }
        //TODO: Evaluate if we should make this always be external
        return inventorySlot.insertItem(stack, action, side == null ? AutomationType.INTERNAL : AutomationType.EXTERNAL);
    }

    @Override
    default ItemStack extractItem(int slot, int amount, @Nullable Direction side, Action action) {
        IInventorySlot inventorySlot = getInventorySlot(slot, side);
        if (inventorySlot == null) {
            return ItemStack.EMPTY;
        }
        //TODO: Evaluate if we should make this always be external
        return inventorySlot.extractItem(amount, action, side == null ? AutomationType.INTERNAL : AutomationType.EXTERNAL);
    }

    @Override
    default int getSlotLimit(int slot, @Nullable Direction side) {
        IInventorySlot inventorySlot = getInventorySlot(slot, side);
        return inventorySlot == null ? 0 : inventorySlot.getLimit(ItemStack.EMPTY);
    }

    @Override
    default boolean isItemValid(int slot, ItemStack stack, @Nullable Direction side) {
        IInventorySlot inventorySlot = getInventorySlot(slot, side);
        return inventorySlot != null && inventorySlot.isItemValid(stack);
    }
}