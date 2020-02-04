package mekanism.common.transmitters.grid;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import mekanism.api.Coord4D;
import mekanism.api.energy.EnergyStack;
import mekanism.api.transmitters.DynamicNetwork;
import mekanism.api.transmitters.IGridTransmitter;
import mekanism.common.MekanismLang;
import mekanism.common.base.EnergyAcceptorWrapper;
import mekanism.common.base.target.EnergyAcceptorTarget;
import mekanism.common.util.EmitUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.text.EnergyDisplay;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.chunk.IChunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

public class EnergyNetwork extends DynamicNetwork<EnergyAcceptorWrapper, EnergyNetwork, EnergyStack> {

    public double clientEnergyScale = 0;
    public EnergyStack buffer = new EnergyStack(0);
    private double lastPowerScale = 0;
    private double joulesTransmitted = 0;
    private double jouleBufferLastTick = 0;

    public EnergyNetwork() {
    }

    public EnergyNetwork(Collection<EnergyNetwork> networks) {
        for (EnergyNetwork net : networks) {
            if (net != null) {
                adoptTransmittersAndAcceptorsFrom(net);
                net.deregister();
            }
        }
        register();
    }

    @Override
    public void adoptTransmittersAndAcceptorsFrom(EnergyNetwork net) {
        if (net.jouleBufferLastTick > jouleBufferLastTick || net.clientEnergyScale > clientEnergyScale) {
            clientEnergyScale = net.clientEnergyScale;
            jouleBufferLastTick = net.jouleBufferLastTick;
            joulesTransmitted = net.joulesTransmitted;
            lastPowerScale = net.lastPowerScale;
        }
        buffer.amount += net.buffer.amount;
        super.adoptTransmittersAndAcceptorsFrom(net);
    }

    public static double round(double d) {
        return Math.round(d * 10000) / 10000;
    }

    @Nonnull
    @Override
    public EnergyStack getBuffer() {
        return buffer;
    }

    @Override
    public void absorbBuffer(IGridTransmitter<EnergyAcceptorWrapper, EnergyNetwork, EnergyStack> transmitter) {
        EnergyStack energy = transmitter.getBuffer();
        buffer.amount += energy.amount;
        energy.amount = 0;
    }

    @Override
    public void clampBuffer() {
        if (buffer.amount > getCapacityAsDouble()) {
            buffer.amount = getCapacityAsDouble();
        }
        if (buffer.amount < 0) {
            buffer.amount = 0;
        }
    }

    public double getEnergyNeeded() {
        if (isRemote()) {
            return 0;
        }
        return getCapacityAsDouble() - buffer.amount;
    }

    private double tickEmit(double energyToSend) {
        Set<EnergyAcceptorTarget> targets = new HashSet<>();
        int totalHandlers = 0;
        Map<Long, IChunk> chunkMap = new Long2ObjectOpenHashMap<>();
        for (Coord4D coord : possibleAcceptors) {
            EnumSet<Direction> sides = acceptorDirections.get(coord);
            if (sides == null || sides.isEmpty()) {
                continue;
            }
            TileEntity tile = MekanismUtils.getTileEntity(getWorld(), chunkMap, coord);
            if (tile == null) {
                continue;
            }
            EnergyAcceptorTarget target = new EnergyAcceptorTarget();
            for (Direction side : sides) {
                EnergyAcceptorWrapper acceptor = EnergyAcceptorWrapper.get(tile, side);
                if (acceptor != null && acceptor.canReceiveEnergy(side) && acceptor.needsEnergy(side)) {
                    target.addHandler(side, acceptor);
                }
            }
            int curHandlers = target.getHandlers().size();
            if (curHandlers > 0) {
                targets.add(target);
                totalHandlers += curHandlers;
            }
        }
        return EmitUtils.sendToAcceptors(targets, totalHandlers, energyToSend);
    }

    public double emit(double energyToSend, boolean doEmit) {
        double toUse = Math.min(getEnergyNeeded(), energyToSend);
        if (doEmit) {
            buffer.amount += toUse;
        }
        return energyToSend - toUse;
    }

    @Override
    public String toString() {
        return "[EnergyNetwork] " + transmitters.size() + " transmitters, " + possibleAcceptors.size() + " acceptors.";
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        clearJoulesTransmitted();

        double currentPowerScale = getPowerScale();
        if (!isRemote()) {
            if (Math.abs(currentPowerScale - lastPowerScale) > 0.01 || (currentPowerScale != lastPowerScale && (currentPowerScale == 0 || currentPowerScale == 1))) {
                needsUpdate = true;
            }
            if (needsUpdate) {
                MinecraftForge.EVENT_BUS.post(new EnergyTransferEvent(this, currentPowerScale));
                lastPowerScale = currentPowerScale;
                needsUpdate = false;
            }
            if (buffer.amount > 0) {
                joulesTransmitted = tickEmit(buffer.amount);
                buffer.amount -= joulesTransmitted;
            }
        }
    }

    public double getPowerScale() {
        return Math.max(jouleBufferLastTick == 0 ? 0 : Math.min(Math.ceil(Math.log10(getPower()) * 2) / 10, 1), getCapacityAsDouble() == 0 ? 0 : buffer.amount / getCapacityAsDouble());
    }

    public void clearJoulesTransmitted() {
        jouleBufferLastTick = buffer.amount;
        joulesTransmitted = 0;
    }

    public double getPower() {
        return jouleBufferLastTick * 20;
    }

    @Override
    public ITextComponent getNeededInfo() {
        return EnergyDisplay.of(getEnergyNeeded()).getTextComponent();
    }

    @Override
    public ITextComponent getStoredInfo() {
        return EnergyDisplay.of(buffer.amount).getTextComponent();
    }

    @Override
    public ITextComponent getFlowInfo() {
        return MekanismLang.GENERIC_PER_TICK.translate(EnergyDisplay.of(joulesTransmitted));
    }

    @Override
    public ITextComponent getTextComponent() {
        return MekanismLang.NETWORK_DESCRIPTION.translate(MekanismLang.ENERGY_NETWORK, transmitters.size(), possibleAcceptors.size());
    }

    public static class EnergyTransferEvent extends Event {

        public final EnergyNetwork energyNetwork;

        public final double power;

        public EnergyTransferEvent(EnergyNetwork network, double currentPower) {
            energyNetwork = network;
            power = currentPower;
        }
    }
}