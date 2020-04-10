package mekanism.common.capabilities.holder.slot;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.transmitters.TransmissionType;
import mekanism.common.capabilities.holder.QuantumEntangloporterConfigHolder;
import mekanism.common.tile.TileEntityQuantumEntangloporter;
import net.minecraft.util.Direction;

public class QuantumEntangloporterInventorySlotHolder extends QuantumEntangloporterConfigHolder implements IInventorySlotHolder {

    public QuantumEntangloporterInventorySlotHolder(TileEntityQuantumEntangloporter entangloporter) {
        super(entangloporter);
    }

    @Override
    protected TransmissionType getTransmissionType() {
        return TransmissionType.ITEM;
    }

    @Nonnull
    @Override
    public List<IInventorySlot> getInventorySlots(@Nullable Direction side) {
        return entangloporter.hasFrequency() && entangloporter.hasInventory() ? entangloporter.frequency.getInventorySlots(side) : Collections.emptyList();
    }
}