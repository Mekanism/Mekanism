package mekanism.client.jei.machine;

import java.util.List;

import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;
import mekanism.common.recipe.inputs.AdvancedMachineInput;
import mekanism.common.recipe.machines.AdvancedMachineRecipe;
import mekanism.common.recipe.outputs.ItemStackOutput;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;

public abstract class AdvancedMachineRecipeWrapper implements IRecipeWrapper
{
	private final AdvancedMachineRecipe recipe;
	
	public AdvancedMachineRecipeWrapper(AdvancedMachineRecipe r)
	{
		recipe = r;
	}

	@Override
	public void getIngredients(IIngredients ingredients) 
	{
		ingredients.setInput(ItemStack.class, ((AdvancedMachineInput)recipe.getInput()).itemStack);
		ingredients.setInput(GasStack.class, new GasStack(((AdvancedMachineInput)recipe.getInput()).gasType, 1));
		ingredients.setOutput(ItemStack.class, ((ItemStackOutput)recipe.getOutput()).output);
	}
	
	public abstract List<ItemStack> getFuelStacks(Gas gasType);

	public AdvancedMachineRecipe getRecipe()
	{
		return recipe;
	}
}
