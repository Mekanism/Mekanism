package mekanism.common.recipe;

import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import mekanism.api.datagen.recipe.RecipeCriterion;
import net.minecraft.data.CookingRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

@ParametersAreNonnullByDefault
public abstract class BaseRecipeProvider extends RecipeProvider {

    private final String modid;

    protected BaseRecipeProvider(DataGenerator gen, String modid) {
        super(gen);
        this.modid = modid;
    }

    @Nonnull
    @Override
    public String getName() {
        return super.getName() + ": " + modid;
    }

    @Override
    protected abstract void registerRecipes(Consumer<IFinishedRecipe> consumer);

    protected void addSmeltingBlastingRecipes(Consumer<IFinishedRecipe> consumer, Ingredient smeltingInput, IItemProvider output, float experience, int smeltingTime,
          ResourceLocation blastingLocation, ResourceLocation smeltingLocation, RecipeCriterion... criteria) {
        CookingRecipeBuilder blastingRecipe = CookingRecipeBuilder.blastingRecipe(smeltingInput, output, experience, smeltingTime / 2);
        CookingRecipeBuilder smeltingRecipe = CookingRecipeBuilder.smeltingRecipe(smeltingInput, output, experience, smeltingTime);
        for (RecipeCriterion criterion : criteria) {
            blastingRecipe.addCriterion(criterion.name, criterion.criterion);
            smeltingRecipe.addCriterion(criterion.name, criterion.criterion);
        }
        blastingRecipe.build(consumer, blastingLocation);
        smeltingRecipe.build(consumer, smeltingLocation);
    }
}