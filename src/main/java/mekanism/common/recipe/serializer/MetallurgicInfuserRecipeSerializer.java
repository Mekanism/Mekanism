package mekanism.common.recipe.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nonnull;
import mekanism.api.JsonConstants;
import mekanism.api.SerializerHelper;
import mekanism.api.recipes.MetallurgicInfuserRecipe;
import mekanism.api.recipes.inputs.InfusionIngredient;
import mekanism.api.recipes.inputs.ItemStackIngredient;
import mekanism.common.Mekanism;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class MetallurgicInfuserRecipeSerializer<T extends MetallurgicInfuserRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T> {

    private final IFactory<T> factory;

    public MetallurgicInfuserRecipeSerializer(IFactory<T> factory) {
        this.factory = factory;
    }

    @Nonnull
    @Override
    public T read(@Nonnull ResourceLocation recipeId, @Nonnull JsonObject json) {
        JsonElement itemInput = JSONUtils.isJsonArray(json, JsonConstants.ITEM_INPUT) ? JSONUtils.getJsonArray(json, JsonConstants.ITEM_INPUT) :
                                JSONUtils.getJsonObject(json, JsonConstants.ITEM_INPUT);
        ItemStackIngredient itemIngredient = ItemStackIngredient.deserialize(itemInput);
        JsonElement infusionInput = JSONUtils.isJsonArray(json, JsonConstants.INFUSION_INPUT) ? JSONUtils.getJsonArray(json, JsonConstants.INFUSION_INPUT) :
                                    JSONUtils.getJsonObject(json, JsonConstants.INFUSION_INPUT);
        InfusionIngredient infusionIngredient = InfusionIngredient.deserialize(infusionInput);
        ItemStack output = SerializerHelper.getItemStack(json, JsonConstants.OUTPUT);
        if (output.isEmpty()) {
            throw new JsonSyntaxException("Recipe output must not be empty.");
        }
        return this.factory.create(recipeId, itemIngredient, infusionIngredient, output);
    }

    @Override
    public T read(@Nonnull ResourceLocation recipeId, @Nonnull PacketBuffer buffer) {
        try {
            ItemStackIngredient itemInput = ItemStackIngredient.read(buffer);
            InfusionIngredient infusionInput = InfusionIngredient.read(buffer);
            ItemStack output = buffer.readItemStack();
            return this.factory.create(recipeId, itemInput, infusionInput, output);
        } catch (Exception e) {
            Mekanism.logger.error("Error reading metallurgic infuser recipe from packet.", e);
            throw e;
        }
    }

    @Override
    public void write(@Nonnull PacketBuffer buffer, @Nonnull T recipe) {
        try {
            recipe.write(buffer);
        } catch (Exception e) {
            Mekanism.logger.error("Error writing metallurgic infuser recipe to packet.", e);
            throw e;
        }
    }

    public interface IFactory<T extends MetallurgicInfuserRecipe> {

        T create(ResourceLocation id, ItemStackIngredient itemInput, InfusionIngredient infusionInput, ItemStack output);
    }
}