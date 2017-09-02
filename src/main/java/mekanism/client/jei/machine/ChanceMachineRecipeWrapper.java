package mekanism.client.jei.machine;

import mekanism.common.recipe.inputs.ItemStackInput;
import mekanism.common.recipe.machines.ChanceMachineRecipe;
import mekanism.common.recipe.outputs.ChanceOutput;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Arrays;

public class ChanceMachineRecipeWrapper implements IRecipeWrapper
{
	private final ChanceMachineRecipe recipe;
	
	public ChanceMachineRecipeWrapper(ChanceMachineRecipe r)
	{
		recipe = r;
	}
	
	@Override
	public void getIngredients(IIngredients ingredients)
	{
		ChanceOutput output = (ChanceOutput)recipe.getOutput();
		ingredients.setInput(ItemStack.class, ((ItemStackInput)recipe.getInput()).ingredient);
		ingredients.setOutputs(ItemStack.class, Arrays.asList(output.primaryOutput, output.secondaryOutput));
	}
	
	@Override
	public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY)
	{
		ChanceOutput output = (ChanceOutput)recipe.getOutput();
		
		if(output.hasSecondary())
		{
			FontRenderer fontRendererObj = minecraft.fontRenderer;
			fontRendererObj.drawString(Math.round(output.secondaryChance*100) + "%", 104, 41, 0x404040, false);
		}
	}

	public ChanceMachineRecipe getRecipe()
	{
		return recipe;
	}
}
