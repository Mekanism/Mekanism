package mekanism.common.recipe.builder;

import java.util.function.Consumer;
import javax.annotation.ParametersAreNonnullByDefault;
import mcp.MethodsReturnNonnullByDefault;
import mekanism.api.datagen.recipe.RecipeCriterion;
import mekanism.common.recipe.RecipePattern;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ExtendedShapedRecipeBuilder extends ShapedRecipeBuilder {

    protected ExtendedShapedRecipeBuilder(IItemProvider result, int count) {
        super(result, count);
    }

    public static ExtendedShapedRecipeBuilder shapedRecipe(IItemProvider result) {
        return shapedRecipe(result, 1);
    }

    public static ExtendedShapedRecipeBuilder shapedRecipe(IItemProvider result, int count) {
        return new ExtendedShapedRecipeBuilder(result, count);
    }

    //TODO: Do we want to somehow add a check to verify we give a key for all components of the pattern?
    public ExtendedShapedRecipeBuilder pattern(RecipePattern pattern) {
        patternLine(pattern.row1);
        if (pattern.row2 != null) {
            patternLine(pattern.row2);
            if (pattern.row3 != null) {
                patternLine(pattern.row3);
            }
        }
        return this;
    }

    @Override
    public ExtendedShapedRecipeBuilder key(Character symbol, Tag<Item> tag) {
        super.key(symbol, tag);
        return this;
    }

    @Override
    public ExtendedShapedRecipeBuilder key(Character symbol, net.minecraft.util.IItemProvider item) {
        super.key(symbol, item);
        return this;
    }

    @Override
    public ExtendedShapedRecipeBuilder key(Character symbol, Ingredient ingredient) {
        super.key(symbol, ingredient);
        return this;
    }

    @Override
    public ExtendedShapedRecipeBuilder addCriterion(String name, ICriterionInstance criterion) {
        super.addCriterion(name, criterion);
        return this;
    }

    public ExtendedShapedRecipeBuilder addCriterion(RecipeCriterion criterion) {
        return addCriterion(criterion.name, criterion.criterion);
    }

    @Override
    public ExtendedShapedRecipeBuilder setGroup(String group) {
        super.setGroup(group);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @deprecated Deprecating this method to make it easier to see when it is accidentally called, as it is probably an accident and was an attempt to call {@link
     * #build(Consumer, ResourceLocation)}
     */
    @Override
    @Deprecated
    public void build(Consumer<IFinishedRecipe> consumer, String save) {
        super.build(consumer, save);
    }
}