package mekanism.api.recipes;

import java.util.Collections;
import java.util.List;
import java.util.function.BiPredicate;
import javax.annotation.ParametersAreNonnullByDefault;
import mcp.MethodsReturnNonnullByDefault;
import mekanism.api.annotations.FieldsAreNonnullByDefault;
import mekanism.api.annotations.NonNull;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.recipes.inputs.InfusionIngredient;
import mekanism.api.recipes.inputs.ItemStackIngredient;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Thiakil on 14/07/2019.
 */
@FieldsAreNonnullByDefault
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class MetallurgicInfuserRecipe extends MekanismRecipe implements BiPredicate<InfusionStack, ItemStack> {

    private final ItemStackIngredient itemInput;
    private final InfusionIngredient infusionInput;
    private final ItemStack output;

    public MetallurgicInfuserRecipe(ResourceLocation id, ItemStackIngredient itemInput, InfusionIngredient infusionInput, ItemStack output) {
        super(id);
        this.itemInput = itemInput;
        this.infusionInput = infusionInput;
        this.output = output.copy();
    }

    @Override
    public boolean test(InfusionStack infusionContainer, ItemStack itemStack) {
        return infusionInput.test(infusionContainer) && itemInput.test(itemStack);
    }

    public @NonNull List<@NonNull ItemStack> getOutputDefinition() {
        return output.isEmpty() ? Collections.emptyList() : Collections.singletonList(output);
    }

    public ItemStack getOutput(InfusionStack inputInfuse, ItemStack inputItem) {
        return this.output.copy();
    }

    public InfusionIngredient getInfusionInput() {
        return this.infusionInput;
    }

    public ItemStackIngredient getItemInput() {
        return this.itemInput;
    }

    @Override
    public void write(PacketBuffer buffer) {
        itemInput.write(buffer);
        infusionInput.write(buffer);
        buffer.writeItemStack(output);
    }
}