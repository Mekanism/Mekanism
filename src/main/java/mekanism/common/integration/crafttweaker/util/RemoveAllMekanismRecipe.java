/*package mekanism.common.integration.crafttweaker.util;

import mekanism.api.recipes.MekanismRecipe;
import mekanism.common.recipe.MekanismRecipeType;

public class RemoveAllMekanismRecipe<RECIPE extends MekanismRecipe> extends RecipeMapModification<RECIPE> {

    public RemoveAllMekanismRecipe(String name, MekanismRecipeType<RECIPE> recipeType) {
        super(name, false, recipeType);
    }

    @Override
    public void apply() {
        //Don't move this into the constructor so that if an addon registers recipes late, we can still remove them
        //TODO: Fix
        //recipes.addAll(recipeType.get());
        super.apply();
    }

    @Override
    public String describe() {
        return "Removed all recipes for " + name;
    }
}*/