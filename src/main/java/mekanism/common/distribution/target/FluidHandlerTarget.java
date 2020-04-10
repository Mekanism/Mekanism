package mekanism.common.distribution.target;

import mekanism.api.annotations.NonNull;
import mekanism.common.distribution.SplitInfo;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public class FluidHandlerTarget extends Target<IFluidHandler, Integer, @NonNull FluidStack> {

    public FluidHandlerTarget(@NonNull FluidStack type) {
        this.extra = type;
    }

    @Override
    protected void acceptAmount(IFluidHandler handler, SplitInfo<Integer> splitInfo, Integer amount) {
        splitInfo.send(handler.fill(new FluidStack(extra, amount), FluidAction.EXECUTE));
    }

    @Override
    protected Integer simulate(IFluidHandler handler, @NonNull FluidStack fluidStack) {
        return handler.fill(fluidStack, FluidAction.SIMULATE);
    }
}