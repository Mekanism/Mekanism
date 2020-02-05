package mekanism.common.tile.factory;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.Action;
import mekanism.api.Upgrade;
import mekanism.api.annotations.NonNull;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.GasTank;
import mekanism.api.gas.GasTankInfo;
import mekanism.api.gas.IGasHandler;
import mekanism.api.inventory.slot.IInventorySlot;
import mekanism.api.providers.IBlockProvider;
import mekanism.api.recipes.ItemStackGasToItemStackRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.ItemStackGasToItemStackCachedRecipe;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.inputs.InputHelper;
import mekanism.api.sustained.ISustainedData;
import mekanism.api.transmitters.TransmissionType;
import mekanism.common.base.ITileComponent;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.inventory.slot.GasInventorySlot;
import mekanism.common.inventory.slot.holder.InventorySlotHelper;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.tile.component.config.ConfigInfo;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.tile.component.config.slot.GasSlotInfo;
import mekanism.common.tile.component.config.slot.ISlotInfo;
import mekanism.common.upgrade.AdvancedMachineUpgradeData;
import mekanism.common.upgrade.IUpgradeData;
import mekanism.common.util.GasUtils;
import mekanism.common.util.ItemDataUtils;
import mekanism.common.util.MekanismUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemHandlerHelper;

//Compressing, injecting, purifying
public class TileEntityItemStackGasToItemStackFactory extends TileEntityItemToItemFactory<ItemStackGasToItemStackRecipe> implements IGasHandler, ISustainedData {

    //TODO: Finish moving references to gasTank to this class
    //public final GasTank gasTank;

    private final IInputHandler<@NonNull GasStack> gasInputHandler;

    private GasInventorySlot extraSlot;

    public TileEntityItemStackGasToItemStackFactory(IBlockProvider blockProvider) {
        super(blockProvider);
        //gasTank = new GasTank(TileEntityAdvancedElectricMachine.MAX_GAS * tier.processes);

        gasInputHandler = InputHelper.getInputHandler(gasTank);

        configComponent.addSupported(TransmissionType.GAS);
        ConfigInfo gasConfig = configComponent.getConfig(TransmissionType.GAS);
        if (gasConfig != null) {
            gasConfig.addSlotInfo(DataType.INPUT, new GasSlotInfo(true, false, gasTank));
            //Set default config directions
            gasConfig.fill(DataType.INPUT);
            gasConfig.setCanEject(false);
        }
    }

    @Override
    protected void addSlots(InventorySlotHelper builder) {
        super.addSlots(builder);
        builder.addSlot(extraSlot = GasInventorySlot.fillOrConvert(gasTank, this::isValidGas, this::getWorld, this, 7, 57));
    }

    public GasTank getGasTank() {
        return gasTank;
    }

    @Nullable
    @Override
    protected GasInventorySlot getExtraSlot() {
        return extraSlot;
    }

    @Override
    public boolean isValidInputItem(@Nonnull ItemStack stack) {
        return containsRecipe(recipe -> recipe.getItemInput().testType(stack));
    }

    @Override
    public boolean inputProducesOutput(int process, @Nonnull ItemStack fallbackInput, @Nonnull IInventorySlot outputSlot, @Nullable IInventorySlot secondaryOutputSlot,
          boolean updateCache) {
        if (outputSlot.isEmpty()) {
            return true;
        }
        CachedRecipe<ItemStackGasToItemStackRecipe> cached = getCachedRecipe(process);
        if (cached != null) {
            ItemStackGasToItemStackRecipe cachedRecipe = cached.getRecipe();
            if (cachedRecipe.getItemInput().testType(fallbackInput) && (gasTank.isEmpty() || cachedRecipe.getGasInput().testType(gasTank.getType()))) {
                //Our input matches the recipe we have cached for this slot
                return true;
            }
            //If there is no cached item input or it doesn't match our fallback then it is an out of date cache, so we ignore the fact that we have a cache
        }
        //TODO: Decide if recipe.getOutput *should* assume that it is given a valid input or not
        // Here we are using it as if it is not assuming it, but that is in part because it currently does not care about the value passed
        // and if something does have extra checking to check the input as long as it checks for invalid ones this should still work
        GasStack gasStack = gasTank.getStack();
        Gas gas = gasStack.getType();
        ItemStack output = outputSlot.getStack();
        ItemStackGasToItemStackRecipe foundRecipe = findFirstRecipe(recipe -> {
            if (recipe.getItemInput().testType(fallbackInput)) {
                //If we don't have a gas stored ignore checking for a match
                if (gasStack.isEmpty() || recipe.getGasInput().testType(gas)) {
                    //TODO: Give it something that is not null when we don't have a stored gas stack
                    return ItemHandlerHelper.canItemStacksStack(recipe.getOutput(fallbackInput, gasStack), output);
                }
            }
            return false;
        });
        if (foundRecipe == null) {
            //We could not find any valid recipe for the given item that matches the items in the current output slots
            return false;
        }
        if (updateCache) {
            //If we want to update the cache, then create a new cache with the recipe we found
            CachedRecipe<ItemStackGasToItemStackRecipe> newCachedRecipe = createNewCachedRecipe(foundRecipe, process);
            if (newCachedRecipe == null) {
                //If we want to update the cache but failed to create a new cache then return that the item is not valid for the slot as something goes wrong
                // I believe we can actually make createNewCachedRecipe Nonnull which will remove this if statement
                return false;
            }
            updateCachedRecipe(newCachedRecipe, process);
        }
        return true;
    }

    public boolean isValidGas(@Nonnull Gas gas) {
        return containsRecipe(recipe -> recipe.getGasInput().testType(gas));
    }

    @Override
    protected void handleSecondaryFuel() {
        extraSlot.fillTankOrConvert();
    }

    @Nonnull
    @Override
    public MekanismRecipeType<ItemStackGasToItemStackRecipe> getRecipeType() {
        switch (type) {
            case INJECTING:
                return MekanismRecipeType.INJECTING;
            case PURIFYING:
                return MekanismRecipeType.PURIFYING;
            case COMPRESSING:
            default:
                //TODO: Make it so that it throws an error if it is not one of the three types
                return MekanismRecipeType.COMPRESSING;
        }
    }

    @Nullable
    @Override
    public ItemStackGasToItemStackRecipe getRecipe(int cacheIndex) {
        ItemStack stack = inputHandlers[cacheIndex].getInput();
        if (stack.isEmpty()) {
            return null;
        }
        GasStack gasStack = gasInputHandler.getInput();
        if (gasStack.isEmpty()) {
            return null;
        }
        return findFirstRecipe(recipe -> recipe.test(stack, gasStack));
    }

    @Override
    public CachedRecipe<ItemStackGasToItemStackRecipe> createNewCachedRecipe(@Nonnull ItemStackGasToItemStackRecipe recipe, int cacheIndex) {
        return new ItemStackGasToItemStackCachedRecipe(recipe, inputHandlers[cacheIndex], gasInputHandler, () -> secondaryEnergyThisTick, outputHandlers[cacheIndex])
              .setCanHolderFunction(() -> MekanismUtils.canFunction(this))
              .setActive(active -> setActiveState(active, cacheIndex))
              .setEnergyRequirements(this::getEnergyPerTick, this::getEnergy, energy -> setEnergy(getEnergy() - energy))
              .setRequiredTicks(() -> ticksRequired)
              .setOnFinish(this::markDirty);
    }

    public int getScaledGasLevel(int i) {
        return gasTank.getStored() * i / gasTank.getCapacity();
    }

    @Nonnull
    @Override
    public CompoundNBT write(CompoundNBT nbtTags) {
        super.write(nbtTags);
        nbtTags.put("gasTank", gasTank.write(new CompoundNBT()));
        return nbtTags;
    }

    @Override
    public void read(CompoundNBT nbtTags) {
        super.read(nbtTags);
        gasTank.read(nbtTags.getCompound("gasTank"));
        GasUtils.clearIfInvalid(gasTank, this::isValidGas);
    }

    @Override
    public void writeSustainedData(ItemStack itemStack) {
        if (!gasTank.isEmpty()) {
            ItemDataUtils.setCompound(itemStack, "gasStored", gasTank.getStack().write(new CompoundNBT()));
        }
    }

    @Override
    public void readSustainedData(ItemStack itemStack) {
        gasTank.setStack(GasStack.readFromNBT(ItemDataUtils.getCompound(itemStack, "gasStored")));
    }

    @Override
    public Map<String, String> getTileDataRemap() {
        Map<String, String> remap = new Object2ObjectOpenHashMap<>();
        remap.put("gasTank.stored", "gasStored");
        return remap;
    }

    @Override
    public int receiveGas(Direction side, @Nonnull GasStack stack, Action action) {
        if (canReceiveGas(side, stack.getType())) {
            return gasTank.fill(stack, action);
        }
        return 0;
    }

    @Override
    public boolean canReceiveGas(Direction side, @Nonnull Gas type) {
        ISlotInfo slotInfo = configComponent.getSlotInfo(TransmissionType.GAS, side);
        if (slotInfo instanceof GasSlotInfo) {
            GasSlotInfo gasSlotInfo = (GasSlotInfo) slotInfo;
            return gasSlotInfo.canInput() && gasSlotInfo.hasTank(gasTank) && gasTank.canReceiveType(type) && isValidGas(type);
        }
        return false;
    }

    @Nonnull
    @Override
    public GasStack drawGas(Direction side, int amount, Action action) {
        return GasStack.EMPTY;
    }

    @Override
    public boolean canDrawGas(Direction side, @Nonnull Gas type) {
        return false;
    }

    @Nonnull
    @Override
    public GasTankInfo[] getTankInfo() {
        return new GasTankInfo[]{gasTank};
    }

    @Override
    public boolean hasSecondaryResourceBar() {
        return true;
    }

    @Override
    public void recalculateUpgrades(Upgrade upgrade) {
        super.recalculateUpgrades(upgrade);
        if (upgrade == Upgrade.GAS && getSupportedUpgrade().contains(Upgrade.GAS)) {
            //TODO: Move gas upgrade to only be in specific factories? At least for calculations?
            secondaryEnergyPerTick = getSecondaryEnergyPerTick(recipeType);
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction side) {
        if (isCapabilityDisabled(capability, side)) {
            return LazyOptional.empty();
        }
        if (capability == Capabilities.GAS_HANDLER_CAPABILITY) {
            return Capabilities.GAS_HANDLER_CAPABILITY.orEmpty(capability, LazyOptional.of(() -> this));
        }
        return super.getCapability(capability, side);
    }

    @Override
    public void parseUpgradeData(@Nonnull IUpgradeData upgradeData) {
        if (upgradeData instanceof AdvancedMachineUpgradeData) {
            AdvancedMachineUpgradeData data = (AdvancedMachineUpgradeData) upgradeData;
            redstone = data.redstone;
            setControlType(data.controlType);
            setEnergy(data.electricityStored);
            sorting = data.sorting;
            //TODO: Transfer recipe ticks?
            //TODO: Transfer operating ticks properly
            gasTank.setStack(data.stored);
            extraSlot.setStack(data.gasSlot.getStack());
            energySlot.setStack(data.energySlot.getStack());
            typeInputSlot.setStack(data.typeInputStack);
            typeOutputSlot.setStack(data.typeOutputStack);
            for (int i = 0; i < data.inputSlots.size(); i++) {
                inputSlots.get(i).setStack(data.inputSlots.get(i).getStack());
            }
            for (int i = 0; i < data.outputSlots.size(); i++) {
                outputSlots.get(i).setStack(data.outputSlots.get(i).getStack());
            }
            for (ITileComponent component : getComponents()) {
                component.read(data.components);
            }
        } else {
            super.parseUpgradeData(upgradeData);
        }
    }

    @Nonnull
    @Override
    public AdvancedMachineUpgradeData getUpgradeData() {
        return new AdvancedMachineUpgradeData(redstone, getControlType(), getEnergy(), progress, gasTank.getStack(), extraSlot, energySlot,
              inputSlots, outputSlots, sorting, typeInputSlot.getStack(), typeOutputSlot.getStack(), getComponents());
    }
}