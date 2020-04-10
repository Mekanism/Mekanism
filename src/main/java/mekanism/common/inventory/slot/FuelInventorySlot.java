package mekanism.common.inventory.slot;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import mcp.MethodsReturnNonnullByDefault;
import mekanism.api.Action;
import mekanism.api.annotations.NonNull;
import mekanism.api.inventory.AutomationType;
import mekanism.api.inventory.IMekanismInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FuelInventorySlot extends BasicInventorySlot {

    public static FuelInventorySlot forFuel(ToIntFunction<@NonNull ItemStack> fuelValue, @Nullable IMekanismInventory inventory, int x, int y) {
        Objects.requireNonNull(fuelValue, "Fuel value calculator cannot be null");
        return new FuelInventorySlot(stack -> fuelValue.applyAsInt(stack) == 0, stack -> fuelValue.applyAsInt(stack) > 0, alwaysTrue, inventory, x, y);
    }

    private FuelInventorySlot(Predicate<@NonNull ItemStack> canExtract, Predicate<@NonNull ItemStack> canInsert, Predicate<@NonNull ItemStack> validator,
          @Nullable IMekanismInventory inventory, int x, int y) {
        super((stack, automationType) -> automationType == AutomationType.MANUAL || canExtract.test(stack), (stack, automationType) -> canInsert.test(stack), validator,
              inventory, x, y);
    }

    public int burn() {
        if (isEmpty()) {
            return 0;
        }
        int burnTime = ForgeHooks.getBurnTime(current) / 2;
        if (burnTime > 0) {
            if (current.hasContainerItem()) {
                if (current.getCount() > 1) {
                    //If we have a container but have more than a single stack of it somehow just exit
                    return 0;
                }
                //If the item has a container, then replace it with the container
                setStack(current.getContainerItem());
            } else {
                //Otherwise shrink the size of the stack by one
                if (shrinkStack(1, Action.EXECUTE) != 1) {
                    //TODO: Print error that something went wrong
                }
            }
        }
        return burnTime;
    }
}