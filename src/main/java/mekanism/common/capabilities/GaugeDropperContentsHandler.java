package mekanism.common.capabilities;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import mcp.MethodsReturnNonnullByDefault;
import mekanism.api.DataHandlerUtils;
import mekanism.api.NBTConstants;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.chemical.gas.BasicGasTank;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IMekanismGasHandler;
import mekanism.api.chemical.infuse.BasicInfusionTank;
import mekanism.api.chemical.infuse.IMekanismInfusionHandler;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.api.fluid.IMekanismFluidHandler;
import mekanism.api.inventory.AutomationType;
import mekanism.common.capabilities.ItemCapabilityWrapper.ItemCapability;
import mekanism.common.capabilities.chemical.item.RateLimitGasHandler.RateLimitGasTank;
import mekanism.common.capabilities.chemical.item.RateLimitInfusionHandler.RateLimitInfusionTank;
import mekanism.common.capabilities.fluid.BasicFluidTank;
import mekanism.common.capabilities.fluid.item.RateLimitFluidHandler.RateLimitFluidTank;
import mekanism.common.util.ItemDataUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class GaugeDropperContentsHandler extends ItemCapability implements IMekanismFluidHandler, IFluidHandlerItem, IMekanismGasHandler, IMekanismInfusionHandler {

    private static final int CAPACITY = 16 * FluidAttributes.BUCKET_VOLUME;
    private static final int TRANSFER_RATE = 256;

    public static GaugeDropperContentsHandler create() {
        return new GaugeDropperContentsHandler();
    }

    private final IChemicalTank<InfuseType, InfusionStack> infusionTank;
    private final IChemicalTank<Gas, GasStack> gasTank;
    private final IExtendedFluidTank fluidTank;

    private List<? extends IChemicalTank<InfuseType, InfusionStack>> infusionTanks;
    private List<? extends IChemicalTank<Gas, GasStack>> gasTanks;
    private List<IExtendedFluidTank> fluidTanks;

    private GaugeDropperContentsHandler() {
        fluidTank = new RateLimitFluidTank(TRANSFER_RATE, () -> CAPACITY, BasicFluidTank.alwaysTrueBi, this::canInsert, BasicFluidTank.alwaysTrue, this);
        gasTank = new RateLimitGasTank(TRANSFER_RATE, () -> CAPACITY, BasicGasTank.alwaysTrueBi, this::canInsert, BasicGasTank.alwaysTrue, null, this);
        infusionTank = new RateLimitInfusionTank(TRANSFER_RATE, () -> CAPACITY, BasicInfusionTank.alwaysTrueBi, this::canInsert, BasicInfusionTank.alwaysTrue, this);
    }

    private boolean canInsert(FluidStack fluidStack, AutomationType automationType) {
        return gasTank.isEmpty() && infusionTank.isEmpty();
    }

    private boolean canInsert(Gas gas, AutomationType automationType) {
        return fluidTank.isEmpty() && infusionTank.isEmpty();
    }

    private boolean canInsert(InfuseType infuseType, AutomationType automationType) {
        return fluidTank.isEmpty() && gasTank.isEmpty();
    }

    @Override
    protected void init() {
        this.fluidTanks = Collections.singletonList(fluidTank);
        this.gasTanks = Collections.singletonList(gasTank);
        this.infusionTanks = Collections.singletonList(infusionTank);
    }

    @Override
    protected void load() {
        ItemStack stack = getStack();
        if (!stack.isEmpty()) {
            DataHandlerUtils.readTanks(getFluidTanks(null), ItemDataUtils.getList(stack, NBTConstants.FLUID_TANKS));
            DataHandlerUtils.readTanks(getGasTanks(null), ItemDataUtils.getList(stack, NBTConstants.GAS_TANKS));
            DataHandlerUtils.readTanks(getInfusionTanks(null), ItemDataUtils.getList(stack, NBTConstants.INFUSION_TANKS));
        }
    }

    @Nonnull
    @Override
    public List<IExtendedFluidTank> getFluidTanks(@Nullable Direction side) {
        return fluidTanks;
    }

    @Override
    public List<? extends IChemicalTank<Gas, GasStack>> getGasTanks(@Nullable Direction side) {
        return gasTanks;
    }

    @Override
    public List<? extends IChemicalTank<InfuseType, InfusionStack>> getInfusionTanks(@Nullable Direction side) {
        return infusionTanks;
    }

    @Override
    public void onContentsChanged() {
        ItemStack stack = getStack();
        if (!stack.isEmpty()) {
            ItemDataUtils.setList(stack, NBTConstants.FLUID_TANKS, DataHandlerUtils.writeTanks(getFluidTanks(null)));
            ItemDataUtils.setList(stack, NBTConstants.GAS_TANKS, DataHandlerUtils.writeTanks(getGasTanks(null)));
            ItemDataUtils.setList(stack, NBTConstants.INFUSION_TANKS, DataHandlerUtils.writeTanks(getInfusionTanks(null)));
        }
    }

    @Nonnull
    @Override
    public ItemStack getContainer() {
        return getStack();
    }

    @Override
    public boolean canProcess(Capability<?> capability) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY || capability == Capabilities.GAS_HANDLER_CAPABILITY || capability == Capabilities.INFUSION_HANDLER_CAPABILITY;
    }
}