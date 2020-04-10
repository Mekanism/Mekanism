package mekanism.api.recipes;

import java.util.function.Predicate;
import javax.annotation.ParametersAreNonnullByDefault;
import mcp.MethodsReturnNonnullByDefault;
import mekanism.api.annotations.FieldsAreNonnullByDefault;
import mekanism.api.annotations.NonNull;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.recipes.inputs.GasStackIngredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Contract;

/**
 * Created by Thiakil on 21/07/2019.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@FieldsAreNonnullByDefault
public abstract class GasToGasRecipe extends MekanismRecipe implements Predicate<@NonNull GasStack> {

    private final GasStackIngredient input;
    private final GasStack output;

    public GasToGasRecipe(ResourceLocation id, GasStackIngredient input, GasStack output) {
        super(id);
        this.input = input;
        this.output = output;
    }

    @Override
    public boolean test(GasStack gasStack) {
        return input.test(gasStack);
    }

    public GasStackIngredient getInput() {
        return input;
    }

    public GasStack getOutputRepresentation() {
        return output;
    }

    @Contract(value = "_ -> new", pure = true)
    public GasStack getOutput(GasStack input) {
        return output.copy();
    }

    @Override
    public void write(PacketBuffer buffer) {
        input.write(buffer);
        output.writeToPacket(buffer);
    }
}