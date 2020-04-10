package mekanism.common.distribution.target;

import mekanism.api.Action;
import mekanism.api.math.FloatingLong;
import mekanism.api.energy.IStrictEnergyHandler;
import mekanism.common.distribution.SplitInfo;

public class EnergyAcceptorTarget extends Target<IStrictEnergyHandler, FloatingLong, FloatingLong> {

    @Override
    protected void acceptAmount(IStrictEnergyHandler handler, SplitInfo<FloatingLong> splitInfo, FloatingLong amount) {
        splitInfo.send(amount.subtract(handler.insertEnergy(amount, Action.EXECUTE)));
    }

    @Override
    protected FloatingLong simulate(IStrictEnergyHandler handler, FloatingLong energyToSend) {
        return energyToSend.subtract(handler.insertEnergy(energyToSend, Action.SIMULATE));
    }
}