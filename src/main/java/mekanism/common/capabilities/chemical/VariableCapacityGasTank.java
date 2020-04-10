package mekanism.common.capabilities.chemical;

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.LongSupplier;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import mcp.MethodsReturnNonnullByDefault;
import mekanism.api.Action;
import mekanism.api.annotations.FieldsAreNonnullByDefault;
import mekanism.api.annotations.NonNull;
import mekanism.api.chemical.attribute.ChemicalAttributeValidator;
import mekanism.api.chemical.gas.BasicGasTank;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.IMekanismGasHandler;
import mekanism.api.inventory.AutomationType;

@FieldsAreNonnullByDefault
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class VariableCapacityGasTank extends BasicGasTank {

    public static VariableCapacityGasTank create(LongSupplier capacity, BiPredicate<@NonNull Gas, @NonNull AutomationType> canExtract,
        BiPredicate<@NonNull Gas, @NonNull AutomationType> canInsert, Predicate<@NonNull Gas> validator,
        @Nullable IMekanismGasHandler gasHandler) {
        return create(capacity, canExtract, canInsert, validator, null, gasHandler);
    }

    public static VariableCapacityGasTank create(LongSupplier capacity, BiPredicate<@NonNull Gas, @NonNull AutomationType> canExtract,
          BiPredicate<@NonNull Gas, @NonNull AutomationType> canInsert, Predicate<@NonNull Gas> validator, @Nullable ChemicalAttributeValidator attributeValidator,
          @Nullable IMekanismGasHandler gasHandler) {
        Objects.requireNonNull(capacity, "Capacity supplier cannot be null");
        Objects.requireNonNull(canExtract, "Extraction validity check cannot be null");
        Objects.requireNonNull(canInsert, "Insertion validity check cannot be null");
        Objects.requireNonNull(validator, "Gas validity check cannot be null");
        return new VariableCapacityGasTank(capacity, canExtract, canInsert, validator, attributeValidator, gasHandler);
    }

    public static VariableCapacityGasTank output(LongSupplier capacity, Predicate<@NonNull Gas> validator, @Nullable IMekanismGasHandler gasHandler) {
        Objects.requireNonNull(capacity, "Capacity supplier cannot be null");
        Objects.requireNonNull(validator, "Gas validity check cannot be null");
        return new VariableCapacityGasTank(capacity, alwaysTrueBi, internalOnly, validator, null, gasHandler);
    }

    private final LongSupplier capacity;

    protected VariableCapacityGasTank(LongSupplier capacity, BiPredicate<@NonNull Gas, @NonNull AutomationType> canExtract,
          BiPredicate<@NonNull Gas, @NonNull AutomationType> canInsert, Predicate<@NonNull Gas> validator, @Nullable ChemicalAttributeValidator attributeValidator,
          @Nullable IMekanismGasHandler gasHandler) {
        super(capacity.getAsLong(), canExtract, canInsert, validator, attributeValidator, gasHandler);
        this.capacity = capacity;
    }

    @Override
    public long getCapacity() {
        return capacity.getAsLong();
    }

    @Override
    public long setStackSize(long amount, @Nonnull Action action) {
        if (isEmpty()) {
            return 0;
        } else if (amount <= 0) {
            if (action.execute()) {
                setStack(getEmptyStack());
            }
            return 0;
        }
        long maxStackSize = getCapacity();
        //Our capacity should never actually be zero, and given we fake it being zero
        // until we finish building the network, we need to override this method to bypass the upper limit check
        // when our upper limit is zero
        if (maxStackSize > 0 && amount > maxStackSize) {
            amount = maxStackSize;
        }
        if (getStored() == amount || action.simulate()) {
            //If our size is not changing or we are only simulating the change, don't do anything
            return amount;
        }
        stored.setAmount(amount);
        onContentsChanged();
        return amount;
    }
}