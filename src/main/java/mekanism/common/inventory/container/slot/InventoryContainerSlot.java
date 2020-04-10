package mekanism.common.inventory.container.slot;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.Action;
import mekanism.api.inventory.AutomationType;
import mekanism.api.inventory.IInventorySlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

//Like net.minecraftforge.items.SlotItemHandler, except directly interacts with the IInventorySlot instead
//TODO: Override other methods to pass them directly to our IInventorySlot
public class InventoryContainerSlot extends Slot implements IInsertableSlot {

    private static IInventory emptyInventory = new Inventory(0);
    private final ContainerSlotType slotType;
    private final IInventorySlot slot;
    @Nullable
    private final SlotOverlay slotOverlay;

    public InventoryContainerSlot(IInventorySlot slot, int x, int y, ContainerSlotType slotType, @Nullable SlotOverlay slotOverlay) {
        super(emptyInventory, 0, x, y);
        this.slot = slot;
        this.slotType = slotType;
        this.slotOverlay = slotOverlay;
    }

    public IInventorySlot getInventorySlot() {
        return slot;
    }

    @Nonnull
    @Override
    public ItemStack insertItem(@Nonnull ItemStack stack, Action action) {
        ItemStack remainder = slot.insertItem(stack, action, AutomationType.MANUAL);
        if (action.execute() && stack.getCount() != remainder.getCount()) {
            onSlotChanged();
        }
        return remainder;
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
        return !stack.isEmpty() && insertItem(stack, Action.SIMULATE).getCount() < stack.getCount();
    }

    @Nonnull
    @Override
    public ItemStack getStack() {
        //TODO: Does this need to return a copy? Depends on if this getStack is allowed to be modified
        return slot.getStack();
    }

    @Override
    public void putStack(@Nonnull ItemStack stack) {
        slot.setStack(stack);
        onSlotChanged();
    }

    @Override
    public void onSlotChange(@Nonnull ItemStack current, @Nonnull ItemStack newStack) {
        //TODO: should we call: slot.onContentsChanged();
    }

    @Override
    public int getSlotStackLimit() {
        return slot.getLimit(ItemStack.EMPTY);
    }

    @Override
    public int getItemStackLimit(@Nonnull ItemStack stack) {
        //TODO: This logic is a lot simpler than the one in SlotItemHandler, is there some case we are missing?
        return slot.getLimit(stack);
    }

    @Override
    public boolean canTakeStack(PlayerEntity player) {
        return !slot.extractItem(1, Action.SIMULATE, AutomationType.MANUAL).isEmpty();
    }

    @Nonnull
    @Override
    public ItemStack decrStackSize(int amount) {
        return slot.extractItem(amount, Action.EXECUTE, AutomationType.MANUAL);
    }

    //TODO: Forge has a TODO for implementing isSameInventory.
    // We can compare inventories at the very least for BasicInventorySlots as they have an instance of IMekanismInventory stored
    /*@Override
    public boolean isSameInventory(Slot other) {
        return other instanceof SlotItemHandler && ((SlotItemHandler) other).getItemHandler() == this.itemHandler;
    }*/

    public ContainerSlotType getSlotType() {
        return slotType;
    }

    @Nullable
    public SlotOverlay getSlotOverlay() {
        return slotOverlay;
    }
}