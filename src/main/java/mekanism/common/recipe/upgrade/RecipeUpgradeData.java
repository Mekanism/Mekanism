package mekanism.common.recipe.upgrade;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import mekanism.api.NBTConstants;
import mekanism.api.Upgrade;
import mekanism.api.block.IHasTileEntity;
import mekanism.api.sustained.ISustainedInventory;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.block.attribute.AttributeUpgradeSupport;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.security.ISecurityItem;
import mekanism.common.tile.base.SubstanceType;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.util.ItemDataUtils;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

@ParametersAreNonnullByDefault
public interface RecipeUpgradeData<TYPE extends RecipeUpgradeData<TYPE>> {

    @Nullable
    TYPE merge(TYPE other);

    /**
     * @return {@code false} if it failed to apply to the stack due to being invalid
     */
    boolean applyToStack(ItemStack stack);

    @Nonnull
    static Set<RecipeUpgradeType> getSupportedTypes(ItemStack stack) {
        //TODO: Add more types of data that can be transferred such as side configs, auto sort, bucket mode, dumping mode
        if (stack.isEmpty()) {
            return Collections.emptySet();
        }
        Set<RecipeUpgradeType> supportedTypes = EnumSet.noneOf(RecipeUpgradeType.class);
        Item item = stack.getItem();
        TileEntityMekanism tile = null;
        if (item instanceof BlockItem) {
            Block block = ((BlockItem) item).getBlock();
            if (block instanceof IHasTileEntity<?>) {
                TileEntity tileEntity = ((IHasTileEntity<?>) block).getTileType().create();
                if (tileEntity instanceof TileEntityMekanism) {
                    tile = (TileEntityMekanism) tileEntity;
                }
            }
            if (Attribute.has(block, AttributeUpgradeSupport.class)) {
                supportedTypes.add(RecipeUpgradeType.UPGRADE);
            }
        }
        if (stack.getCapability(Capabilities.STRICT_ENERGY_CAPABILITY).isPresent() || tile != null && tile.handles(SubstanceType.ENERGY)) {
            //If we are for a block that handles energy or we have an energy handler capability
            supportedTypes.add(RecipeUpgradeType.ENERGY);
        }
        if (stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent() || tile != null && tile.handles(SubstanceType.FLUID)) {
            //If we are for a block that handles fluid or we have an fluid handler capability
            supportedTypes.add(RecipeUpgradeType.FLUID);
        }
        if (stack.getCapability(Capabilities.GAS_HANDLER_CAPABILITY).isPresent() || tile != null && tile.handles(SubstanceType.GAS)) {
            //If we are for a block that handles gas or we have an gas handler capability
            supportedTypes.add(RecipeUpgradeType.GAS);
        }
        if (stack.getCapability(Capabilities.INFUSION_HANDLER_CAPABILITY).isPresent() || tile != null && tile.handles(SubstanceType.INFUSION)) {
            //If we are for a block that handles infusion or we have an infusion handler capability
            supportedTypes.add(RecipeUpgradeType.INFUSION);
        }
        if (item instanceof ISustainedInventory || tile != null && tile.persistInventory()) {
            supportedTypes.add(RecipeUpgradeType.ITEM);
        }
        if (item instanceof ISecurityItem) {
            supportedTypes.add(RecipeUpgradeType.SECURITY);
        }
        return supportedTypes;
    }

    /**
     * Make sure to validate with getSupportedTypes before calling this
     */
    @Nullable
    static RecipeUpgradeData<?> getUpgradeData(@Nonnull RecipeUpgradeType type, @Nonnull ItemStack stack) {
        Item item = stack.getItem();
        switch (type) {
            case ENERGY:
                return new EnergyRecipeData(ItemDataUtils.getList(stack, NBTConstants.ENERGY_CONTAINERS));
            case FLUID:
                return new FluidRecipeData(ItemDataUtils.getList(stack, NBTConstants.FLUID_TANKS));
            case GAS:
                return new GasRecipeData(ItemDataUtils.getList(stack, NBTConstants.GAS_TANKS));
            case INFUSION:
                return new InfusionRecipeData(ItemDataUtils.getList(stack, NBTConstants.INFUSION_TANKS));
            case ITEM:
                return new ItemRecipeData(((ISustainedInventory) item).getInventory(stack));
            case SECURITY:
                ISecurityItem securityItem = (ISecurityItem) item;
                UUID ownerUUID = securityItem.getOwnerUUID(stack);
                return ownerUUID == null ? null : new SecurityRecipeData(ownerUUID, securityItem.getSecurity(stack));
            case UPGRADE:
                CompoundNBT componentUpgrade = ItemDataUtils.getCompound(stack, NBTConstants.COMPONENT_UPGRADE);
                return componentUpgrade.isEmpty() ? null : new UpgradesRecipeData(Upgrade.buildMap(componentUpgrade));
        }
        return null;
    }

    @Nullable
    static <TYPE extends RecipeUpgradeData<TYPE>> TYPE mergeUpgradeData(List<RecipeUpgradeData<?>> upgradeData) {
        if (upgradeData.isEmpty()) {
            return null;
        }
        TYPE data = (TYPE) upgradeData.get(0);
        for (int i = 1; i < upgradeData.size(); i++) {
            data = data.merge((TYPE) upgradeData.get(i));
            if (data == null) {
                return null;
            }
        }
        return data;
    }
}