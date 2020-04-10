package mekanism.common.capabilities.holder.chemical;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.transmitters.TransmissionType;
import mekanism.common.capabilities.holder.QuantumEntangloporterConfigHolder;
import mekanism.common.tile.TileEntityQuantumEntangloporter;
import net.minecraft.util.Direction;

public class QuantumEntangloporterGasTankHolder extends QuantumEntangloporterConfigHolder implements IChemicalTankHolder<Gas, GasStack> {

    public QuantumEntangloporterGasTankHolder(TileEntityQuantumEntangloporter entangloporter) {
        super(entangloporter);
    }

    @Override
    protected TransmissionType getTransmissionType() {
        return TransmissionType.GAS;
    }

    @Nonnull
    @Override
    public List<? extends IChemicalTank<Gas, GasStack>> getTanks(@Nullable Direction side) {
        return entangloporter.hasFrequency() ? entangloporter.frequency.getGasTanks(side) : Collections.emptyList();
    }
}