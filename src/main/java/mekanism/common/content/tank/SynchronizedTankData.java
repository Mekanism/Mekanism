package mekanism.common.content.tank;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.Coord4D;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.api.fluid.IMekanismFluidHandler;
import mekanism.api.inventory.IInventorySlot;
import mekanism.common.base.ContainerEditMode;
import mekanism.common.inventory.container.slot.ContainerSlotType;
import mekanism.common.inventory.slot.FluidInventorySlot;
import mekanism.common.inventory.slot.OutputInventorySlot;
import mekanism.common.multiblock.SynchronizedData;
import mekanism.common.tile.TileEntityDynamicTank;
import net.minecraft.util.Direction;

public class SynchronizedTankData extends SynchronizedData<SynchronizedTankData> implements IMekanismFluidHandler {

    public DynamicFluidTank fluidTank;

    public ContainerEditMode editMode = ContainerEditMode.BOTH;
    public Set<ValveData> valves = new ObjectOpenHashSet<>();

    @Nonnull
    private List<IInventorySlot> inventorySlots;
    private List<IExtendedFluidTank> fluidTanks;
    private int tankCapacity;

    public SynchronizedTankData(TileEntityDynamicTank tile) {
        fluidTank = new DynamicFluidTank(tile);
        fluidTanks = Collections.singletonList(fluidTank);
        inventorySlots = createBaseInventorySlots();
    }

    private List<IInventorySlot> createBaseInventorySlots() {
        List<IInventorySlot> inventorySlots = new ArrayList<>();
        FluidInventorySlot input;
        inventorySlots.add(input = FluidInventorySlot.input(fluidTank, this, 146, 20));
        inventorySlots.add(OutputInventorySlot.at(this, 146, 51));
        input.setSlotType(ContainerSlotType.INPUT);
        return inventorySlots;
    }

    @Nonnull
    @Override
    public List<IInventorySlot> getInventorySlots(@Nullable Direction side) {
        return inventorySlots;
    }

    public void setInventoryData(@Nonnull List<IInventorySlot> toCopy) {
        for (int i = 0; i < toCopy.size(); i++) {
            if (i < inventorySlots.size()) {
                //Copy it via NBT to ensure that we set it using the "unsafe" method in case there is a problem with the types somehow
                inventorySlots.get(i).deserializeNBT(toCopy.get(i).serializeNBT());
            }
        }
    }

    public void setTankData(@Nonnull List<IExtendedFluidTank> toCopy) {
        for (int i = 0; i < toCopy.size(); i++) {
            if (i < fluidTanks.size()) {
                //Copy it via NBT to ensure that we set it using the "unsafe" method in case there is a problem with the types somehow
                fluidTanks.get(i).deserializeNBT(toCopy.get(i).serializeNBT());
            }
        }
    }

    public int getTankCapacity() {
        return tankCapacity;
    }

    @Override
    public void setVolume(int volume) {
        super.setVolume(volume);
        tankCapacity = getVolume() * TankUpdateProtocol.FLUID_PER_TANK;
    }

    @Nonnull
    @Override
    public List<IExtendedFluidTank> getFluidTanks(@Nullable Direction side) {
        return fluidTanks;
    }

    public static class ValveData {

        public Direction side;
        public Coord4D location;

        public boolean prevActive;
        public int activeTicks;

        public void onTransfer() {
            activeTicks = 30;
        }

        @Override
        public int hashCode() {
            int code = 1;
            code = 31 * code + side.ordinal();
            code = 31 * code + location.hashCode();
            return code;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ValveData && ((ValveData) obj).side == side && ((ValveData) obj).location.equals(location);
        }
    }
}