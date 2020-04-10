package mekanism.common.capabilities.chemical;

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.LongSupplier;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import mcp.MethodsReturnNonnullByDefault;
import mekanism.api.annotations.FieldsAreNonnullByDefault;
import mekanism.api.annotations.NonNull;
import mekanism.api.chemical.attribute.ChemicalAttributeValidator;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.IMekanismGasHandler;
import mekanism.api.inventory.AutomationType;
import mekanism.common.tile.TileEntityMultiblock;

@FieldsAreNonnullByDefault
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MultiblockGasTank<MULTIBLOCK extends TileEntityMultiblock<?>> extends VariableCapacityGasTank {

    public static <MULTIBLOCK extends TileEntityMultiblock<?>> MultiblockGasTank<MULTIBLOCK> create(MULTIBLOCK multiblock, LongSupplier capacity, Predicate<@NonNull Gas> validator) {
        return new MultiblockGasTank<>(multiblock, capacity, validator);
    }

    public static <MULTIBLOCK extends TileEntityMultiblock<?>> MultiblockGasTank<MULTIBLOCK> create(MULTIBLOCK multiblock, LongSupplier capacity,
          BiPredicate<@NonNull Gas, @NonNull AutomationType> canExtract, BiPredicate<@NonNull Gas, @NonNull AutomationType> canInsert, Predicate<@NonNull Gas> validator) {
        return new MultiblockGasTank<>(multiblock, capacity, canExtract, canInsert, validator, null, null);
    }

    public static <MULTIBLOCK extends TileEntityMultiblock<?>> MultiblockGasTank<MULTIBLOCK> create(MULTIBLOCK tile, LongSupplier capacity,
          BiPredicate<@NonNull Gas, @NonNull AutomationType> canExtract, BiPredicate<@NonNull Gas, @NonNull AutomationType> canInsert, Predicate<@NonNull Gas> validator,
          @Nullable ChemicalAttributeValidator attributeValidator, @Nullable IMekanismGasHandler gasHandler) {
        Objects.requireNonNull(tile, "Tile cannot be null");
        Objects.requireNonNull(capacity, "Capacity supplier cannot be null");
        Objects.requireNonNull(validator, "Gas validity check cannot be null");
        Objects.requireNonNull(canExtract, "Extraction validity check cannot be null");
        Objects.requireNonNull(canInsert, "Insertion validity check cannot be null");
        return new MultiblockGasTank<>(tile, capacity, canExtract, canInsert, validator, attributeValidator, gasHandler);
    }

    protected final MULTIBLOCK multiblock;

    protected MultiblockGasTank(MULTIBLOCK multiblock, LongSupplier capacity, Predicate<@NonNull Gas> validator) {
        super(capacity, (stack, automationType) -> automationType != AutomationType.EXTERNAL || multiblock.structure != null,
            (stack, automationType) -> automationType != AutomationType.EXTERNAL || multiblock.structure != null, validator, null, null);
        this.multiblock = multiblock;
    }

    protected MultiblockGasTank(MULTIBLOCK multiblock, LongSupplier capacity, BiPredicate<@NonNull Gas, @NonNull AutomationType> canExtract,
          BiPredicate<@NonNull Gas, @NonNull AutomationType> canInsert, Predicate<@NonNull Gas> validator, @Nullable ChemicalAttributeValidator attributeValidator,
          @Nullable IMekanismGasHandler gasHandler) {
        super(capacity, canExtract, canInsert, validator, attributeValidator, gasHandler);
        this.multiblock = multiblock;
    }

    @Override
    public void onContentsChanged() {
        super.onContentsChanged();
        if (multiblock.hasWorld() && !multiblock.isRemote() && multiblock.isRendering) {
            multiblock.markDirty(false);
        }
    }
}