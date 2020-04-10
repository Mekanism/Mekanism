package mekanism.common.util;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.Action;
import mekanism.api.DataHandlerUtils;
import mekanism.api.NBTConstants;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.chemical.gas.BasicGasTank;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import mekanism.api.chemical.infuse.BasicInfusionTank;
import mekanism.api.chemical.infuse.IInfusionHandler;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.energy.IMekanismStrictEnergyHandler;
import mekanism.api.energy.IStrictEnergyHandler;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.api.heat.IHeatCapacitor;
import mekanism.api.math.FloatingLong;
import mekanism.api.text.EnumColor;
import mekanism.common.MekanismLang;
import mekanism.common.base.ILangEntry;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.energy.BasicEnergyContainer;
import mekanism.common.capabilities.fluid.BasicFluidTank;
import mekanism.common.capabilities.heat.BasicHeatCapacitor;
import mekanism.common.util.text.EnergyDisplay;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public class StorageUtils {

    public static void addStoredEnergy(@Nonnull ItemStack stack, @Nonnull List<ITextComponent> tooltip, boolean showMissingCap) {
        addStoredEnergy(stack, tooltip, showMissingCap, MekanismLang.STORED_ENERGY);
    }

    public static void addStoredEnergy(@Nonnull ItemStack stack, @Nonnull List<ITextComponent> tooltip, boolean showMissingCap, ILangEntry langEntry) {
        if (Capabilities.STRICT_ENERGY_CAPABILITY != null) {
            //Ensure the capability is not null, as the first call to addInformation happens before capability injection
            Optional<IStrictEnergyHandler> capability = MekanismUtils.toOptional(stack.getCapability(Capabilities.STRICT_ENERGY_CAPABILITY));
            if (capability.isPresent()) {
                IStrictEnergyHandler energyHandlerItem = capability.get();
                int energyContainerCount = energyHandlerItem.getEnergyContainerCount();
                for (int container = 0; container < energyContainerCount; container++) {
                    tooltip.add(langEntry.translateColored(EnumColor.BRIGHT_GREEN, EnumColor.GRAY,
                          EnergyDisplay.of(energyHandlerItem.getEnergy(container), energyHandlerItem.getMaxEnergy(container))));
                }
            } else if (showMissingCap) {
                tooltip.add(langEntry.translateColored(EnumColor.BRIGHT_GREEN, EnumColor.GRAY, EnergyDisplay.ZERO));
            }
        }
    }

    public static void addStoredGas(@Nonnull ItemStack stack, @Nonnull List<ITextComponent> tooltip, boolean showMissingCap) {
        addStoredGas(stack, tooltip, showMissingCap, MekanismLang.NO_GAS, stored -> {
            if (stored.isEmpty()) {
                return MekanismLang.NO_GAS.translate();
            }
            return MekanismLang.STORED.translate(stored, stored.getAmount());
        });
    }

    public static void addStoredGas(@Nonnull ItemStack stack, @Nonnull List<ITextComponent> tooltip, boolean showMissingCap, ILangEntry emptyLangEntry,
          Function<GasStack, ITextComponent> storedFunction) {
        if (Capabilities.GAS_HANDLER_CAPABILITY != null) {
            //Ensure the capability is not null, as the first call to addInformation happens before capability injection
            Optional<IGasHandler> capability = MekanismUtils.toOptional(stack.getCapability(Capabilities.GAS_HANDLER_CAPABILITY));
            if (capability.isPresent()) {
                IGasHandler gasHandlerItem = capability.get();
                int tanks = gasHandlerItem.getGasTankCount();
                for (int tank = 0; tank < tanks; tank++) {
                    tooltip.add(storedFunction.apply(gasHandlerItem.getGasInTank(tank)));
                }
            } else if (showMissingCap) {
                tooltip.add(emptyLangEntry.translate());
            }
        }
    }

    /**
     * Gets the fluid if one is stored from an item's tank going off the basis there is a single tank. This is for cases when we may not actually have a fluid handler
     * attached to our item but it may have stored data in its tank from when it was a block
     */
    @Nonnull
    public static FluidStack getStoredFluidFromNBT(ItemStack stack) {
        BasicFluidTank tank = BasicFluidTank.create(Integer.MAX_VALUE, null);
        DataHandlerUtils.readTanks(Collections.singletonList(tank), ItemDataUtils.getList(stack, NBTConstants.FLUID_TANKS));
        return tank.getFluid();
    }

    /**
     * Gets the gas if one is stored from an item's tank going off the basis there is a single tank. This is for cases when we may not actually have a gas handler
     * attached to our item but it may have stored data in its tank from when it was a block
     */
    @Nonnull
    public static GasStack getStoredGasFromNBT(ItemStack stack) {
        BasicGasTank tank = BasicGasTank.create(Integer.MAX_VALUE, null);
        DataHandlerUtils.readTanks(Collections.singletonList(tank), ItemDataUtils.getList(stack, NBTConstants.GAS_TANKS));
        return tank.getStack();
    }

    /**
     * Gets the infuse type if one is stored from an item's tank going off the basis there is a single tank. This is for cases when we may not actually have a infusion
     * handler attached to our item but it may have stored data in its tank from when it was a block
     */
    @Nonnull
    public static InfusionStack getStoredInfusionFromNBT(ItemStack stack) {
        BasicInfusionTank tank = BasicInfusionTank.create(Integer.MAX_VALUE, null);
        DataHandlerUtils.readTanks(Collections.singletonList(tank), ItemDataUtils.getList(stack, NBTConstants.INFUSION_TANKS));
        return tank.getStack();
    }

    /**
     * Gets the energy if one is stored from an item's container going off the basis there is a single energy container. This is for cases when we may not actually have
     * an energy handler attached to our item but it may have stored data in its container from when it was a block
     */
    public static FloatingLong getStoredEnergyFromNBT(ItemStack stack) {
        BasicEnergyContainer container = BasicEnergyContainer.create(FloatingLong.MAX_VALUE, null);
        DataHandlerUtils.readContainers(Collections.singletonList(container), ItemDataUtils.getList(stack, NBTConstants.ENERGY_CONTAINERS));
        return container.getEnergy();
    }

    public static ItemStack getFilledEnergyVariant(ItemStack toFill, FloatingLong capacity) {
        //Manually handle this as capabilities are not necessarily loaded yet (at least not on the first call to this, which is made via fillItemGroup)
        BasicEnergyContainer container = BasicEnergyContainer.create(capacity, null);
        container.setEnergy(capacity);
        ItemDataUtils.setList(toFill, NBTConstants.ENERGY_CONTAINERS, DataHandlerUtils.writeContainers(Collections.singletonList(container)));
        //The item is now filled return it for convenience
        return toFill;
    }

    @Nullable
    public static IEnergyContainer getEnergyContainer(ItemStack stack, int container) {
        Optional<IStrictEnergyHandler> energyCapability = MekanismUtils.toOptional(stack.getCapability(Capabilities.STRICT_ENERGY_CAPABILITY));
        if (energyCapability.isPresent()) {
            IStrictEnergyHandler energyHandlerItem = energyCapability.get();
            if (energyHandlerItem instanceof IMekanismStrictEnergyHandler) {
                return ((IMekanismStrictEnergyHandler) energyHandlerItem).getEnergyContainer(container, null);
            }
        }
        return null;
    }

    public static double getDurabilityForDisplay(ItemStack stack) {
        //Note we ensure the capabilities are not null, as the first call to getDurabilityForDisplay happens before capability injection
        if (Capabilities.GAS_HANDLER_CAPABILITY == null || Capabilities.INFUSION_HANDLER_CAPABILITY == null ||
            CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY == null || Capabilities.STRICT_ENERGY_CAPABILITY == null) {
            return 1;
        }
        double bestRatio = 0;
        Optional<IGasHandler> gasCapability = MekanismUtils.toOptional(stack.getCapability(Capabilities.GAS_HANDLER_CAPABILITY));
        if (gasCapability.isPresent()) {
            IGasHandler gasHandlerItem = gasCapability.get();
            int tanks = gasHandlerItem.getGasTankCount();
            for (int tank = 0; tank < tanks; tank++) {
                bestRatio = Math.max(bestRatio, getRatio(gasHandlerItem.getGasInTank(tank).getAmount(), gasHandlerItem.getGasTankCapacity(tank)));
            }
        }
        Optional<IInfusionHandler> infusionCapability = MekanismUtils.toOptional(stack.getCapability(Capabilities.INFUSION_HANDLER_CAPABILITY));
        if (infusionCapability.isPresent()) {
            IInfusionHandler infusionHandlerItem = infusionCapability.get();
            int tanks = infusionHandlerItem.getInfusionTankCount();
            for (int tank = 0; tank < tanks; tank++) {
                bestRatio = Math.max(bestRatio, getRatio(infusionHandlerItem.getInfusionInTank(tank).getAmount(), infusionHandlerItem.getInfusionTankCapacity(tank)));
            }
        }
        Optional<IFluidHandlerItem> fluidCapability = MekanismUtils.toOptional(stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY));
        if (fluidCapability.isPresent()) {
            IFluidHandlerItem fluidHandlerItem = fluidCapability.get();
            int tanks = fluidHandlerItem.getTanks();
            for (int tank = 0; tank < tanks; tank++) {
                bestRatio = Math.max(bestRatio, getRatio(fluidHandlerItem.getFluidInTank(tank).getAmount(), fluidHandlerItem.getTankCapacity(tank)));
            }
        }
        Optional<IStrictEnergyHandler> energyCapability = MekanismUtils.toOptional(stack.getCapability(Capabilities.STRICT_ENERGY_CAPABILITY));
        if (energyCapability.isPresent()) {
            IStrictEnergyHandler energyHandlerItem = energyCapability.get();
            int containers = energyHandlerItem.getEnergyContainerCount();
            for (int container = 0; container < containers; container++) {
                bestRatio = Math.max(bestRatio, energyHandlerItem.getEnergy(container).divideToLevel(energyHandlerItem.getMaxEnergy(container)));
            }
        }
        return 1 - bestRatio;
    }

    private static double getRatio(int amount, int capacity) {
        return capacity == 0 ? 1 : amount / (double) capacity;
    }

    public static void mergeTanks(IExtendedFluidTank tank, IExtendedFluidTank mergeTank) {
        if (tank.isEmpty()) {
            tank.setStack(mergeTank.getFluid());
        } else if (!mergeTank.isEmpty() && tank.isFluidEqual(mergeTank.getFluid())) {
            tank.growStack(mergeTank.getFluidAmount(), Action.EXECUTE);
        }
    }

    public static <CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>> void mergeTanks(IChemicalTank<CHEMICAL, STACK> tank,
          IChemicalTank<CHEMICAL, STACK> mergeTank) {
        if (tank.isEmpty()) {
            tank.setStack(mergeTank.getStack());
        } else if (!mergeTank.isEmpty() && tank.isTypeEqual(mergeTank.getStack())) {
            tank.growStack(mergeTank.getStored(), Action.EXECUTE);
        }
    }

    public static void mergeContainers(IEnergyContainer container, IEnergyContainer mergeContainer) {
        container.setEnergy(container.getEnergy().add(mergeContainer.getEnergy()));
    }

    public static void mergeContainers(IHeatCapacitor capacitor, IHeatCapacitor mergeCapacitor) {
        capacitor.setHeat(capacitor.getHeat() + mergeCapacitor.getHeat());
        if (capacitor instanceof BasicHeatCapacitor) {
            ((BasicHeatCapacitor) capacitor).setHeatCapacity(capacitor.getHeatCapacity() + mergeCapacitor.getHeatCapacity(), false);
        }
    }
}