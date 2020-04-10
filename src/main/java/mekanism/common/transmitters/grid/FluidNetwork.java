package mekanism.common.transmitters.grid;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.Action;
import mekanism.api.Coord4D;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.api.fluid.IMekanismFluidHandler;
import mekanism.api.math.MathUtils;
import mekanism.api.transmitters.DynamicNetwork;
import mekanism.api.transmitters.IGridTransmitter;
import mekanism.common.MekanismLang;
import mekanism.common.capabilities.fluid.BasicFluidTank;
import mekanism.common.capabilities.fluid.VariableCapacityFluidTank;
import mekanism.common.distribution.target.FluidHandlerTarget;
import mekanism.common.distribution.target.FluidTransmitterSaveTarget;
import mekanism.common.util.CapabilityUtils;
import mekanism.common.util.EmitUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.PipeUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.chunk.IChunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class FluidNetwork extends DynamicNetwork<IFluidHandler, FluidNetwork, FluidStack> implements IMekanismFluidHandler {

    private final List<IExtendedFluidTank> fluidTanks;
    public final VariableCapacityFluidTank fluidTank;

    @Nonnull
    public FluidStack lastFluid = FluidStack.EMPTY;
    public float fluidScale;
    private int prevTransferAmount;

    //TODO: Make fluid storage support storing as longs?
    private int intCapacity;

    public FluidNetwork() {
        fluidTank = VariableCapacityFluidTank.create(this::getCapacityAsInt, BasicFluidTank.alwaysTrueBi, BasicFluidTank.alwaysTrueBi, BasicFluidTank.alwaysTrue, this);
        fluidTanks = Collections.singletonList(fluidTank);
    }

    public FluidNetwork(UUID networkID) {
        super(networkID);
        fluidTank = VariableCapacityFluidTank.create(this::getCapacityAsInt, BasicFluidTank.alwaysTrueBi, BasicFluidTank.alwaysTrueBi, BasicFluidTank.alwaysTrue, this);
        fluidTanks = Collections.singletonList(fluidTank);
    }

    public FluidNetwork(Collection<FluidNetwork> networks) {
        this();
        for (FluidNetwork net : networks) {
            if (net != null) {
                adoptTransmittersAndAcceptorsFrom(net);
                net.deregister();
            }
        }
        register();
    }

    @Override
    protected void forceScaleUpdate() {
        if (!fluidTank.isEmpty() && fluidTank.getCapacity() > 0) {
            fluidScale = Math.min(1, (float) fluidTank.getFluidAmount() / fluidTank.getCapacity());
        } else {
            fluidScale = 0;
        }
    }

    @Override
    public void adoptTransmittersAndAcceptorsFrom(FluidNetwork net) {
        float oldScale = fluidScale;
        long oldCapacity = getCapacity();
        super.adoptTransmittersAndAcceptorsFrom(net);
        //Merge the fluid scales
        long capacity = getCapacity();
        fluidScale = Math.min(1, capacity == 0 ? 0 : (fluidScale * oldCapacity + net.fluidScale * net.capacity) / capacity);
        if (isRemote()) {
            if (fluidTank.isEmpty() && !net.fluidTank.isEmpty()) {
                fluidTank.setStack(net.getBuffer());
                net.fluidTank.setEmpty();
            }
        } else {
            if (!net.fluidTank.isEmpty()) {
                if (fluidTank.isEmpty()) {
                    fluidTank.setStack(net.getBuffer());
                } else if (fluidTank.isFluidEqual(net.fluidTank.getFluid())) {
                    int amount = net.fluidTank.getFluidAmount();
                    if (fluidTank.growStack(amount, Action.EXECUTE) != amount) {
                        //TODO: Print warning/error
                    }
                } else if (net.fluidTank.getFluidAmount() > fluidTank.getFluidAmount()) {
                    //TODO: Evaluate, realistically we should never be trying to merge two networks
                    // if they have conflicting types
                    fluidTank.setStack(net.getBuffer());
                }
                net.fluidTank.setEmpty();
            }
            if (oldScale != fluidScale) {
                //We want to make sure we update to the scale change
                needsUpdate = true;
            }
        }
    }

    @Nonnull
    @Override
    public FluidStack getBuffer() {
        return fluidTank.getFluid().copy();
    }

    @Override
    public void absorbBuffer(IGridTransmitter<IFluidHandler, FluidNetwork, FluidStack> transmitter) {
        FluidStack fluid = transmitter.getBuffer();
        if (fluid == null || fluid.isEmpty()) {
            //Note: We support null given technically the API says it is nullable, so if someone makes a custom IGridTransmitter
            // with it being null would have issues
            return;
        }
        if (fluidTank.isEmpty()) {
            fluidTank.setStack(fluid.copy());
        } else if (fluidTank.isFluidEqual(fluid)) {
            //TODO better multiple buffer impl
            int amount = fluid.getAmount();
            if (fluidTank.growStack(amount, Action.EXECUTE) != amount) {
                //TODO: Print warning/error
            }
        }
    }

    @Override
    public void clampBuffer() {
        if (!fluidTank.isEmpty()) {
            int capacity = getCapacityAsInt();
            if (fluidTank.getFluidAmount() > capacity) {
                if (fluidTank.setStackSize(capacity, Action.EXECUTE) != capacity) {
                    //TODO: Print warning/error
                }
            }
        }
    }

    @Override
    protected synchronized void updateCapacity(IGridTransmitter<IFluidHandler, FluidNetwork, FluidStack> transmitter) {
        super.updateCapacity(transmitter);
        intCapacity = MathUtils.clampToInt(getCapacity());
    }

    @Override
    public synchronized void updateCapacity() {
        super.updateCapacity();
        intCapacity = MathUtils.clampToInt(getCapacity());
    }

    public int getCapacityAsInt() {
        return intCapacity;
    }

    @Override
    protected void updateSaveShares() {
        super.updateSaveShares();
        int size = transmittersSize();
        if (size > 0) {
            FluidStack fluidType = fluidTank.getFluid();
            //Just pretend we are always accessing it from the north
            Direction side = Direction.NORTH;
            Set<FluidTransmitterSaveTarget> saveTargets = new ObjectOpenHashSet<>(size);
            for (IGridTransmitter<IFluidHandler, FluidNetwork, FluidStack> transmitter : transmitters) {
                FluidTransmitterSaveTarget saveTarget = new FluidTransmitterSaveTarget(fluidType);
                saveTarget.addHandler(side, transmitter);
                saveTargets.add(saveTarget);
            }
            EmitUtils.sendToAcceptors(saveTargets, size, fluidType.getAmount(), fluidType);
            for (FluidTransmitterSaveTarget saveTarget : saveTargets) {
                saveTarget.saveShare(side);
            }
        }
    }

    private int tickEmit(@Nonnull FluidStack fluidToSend) {
        Set<FluidHandlerTarget> availableAcceptors = new ObjectOpenHashSet<>();
        int totalHandlers = 0;
        Long2ObjectMap<IChunk> chunkMap = new Long2ObjectOpenHashMap<>();
        for (Coord4D coord : possibleAcceptors) {
            EnumSet<Direction> sides = acceptorDirections.get(coord);
            if (sides == null || sides.isEmpty()) {
                continue;
            }
            TileEntity tile = MekanismUtils.getTileEntity(getWorld(), chunkMap, coord);
            if (tile == null) {
                continue;
            }
            FluidHandlerTarget target = new FluidHandlerTarget(fluidToSend);
            for (Direction side : sides) {
                CapabilityUtils.getCapability(tile, CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side).ifPresent(acceptor -> {
                    if (PipeUtils.canFill(acceptor, fluidToSend)) {
                        target.addHandler(side, acceptor);
                    }
                });
            }
            int curHandlers = target.getHandlers().size();
            if (curHandlers > 0) {
                availableAcceptors.add(target);
                totalHandlers += curHandlers;
            }
        }
        return EmitUtils.sendToAcceptors(availableAcceptors, totalHandlers, fluidToSend.getAmount(), fluidToSend);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!isRemote()) {
            float scale = computeContentScale();
            if (scale != fluidScale) {
                fluidScale = scale;
                needsUpdate = true;
            }
            if (needsUpdate) {
                MinecraftForge.EVENT_BUS.post(new FluidTransferEvent(this, lastFluid, fluidScale));
                needsUpdate = false;
            }
            if (fluidTank.isEmpty()) {
                prevTransferAmount = 0;
            } else {
                prevTransferAmount = tickEmit(fluidTank.getFluid());
                if (fluidTank.shrinkStack(prevTransferAmount, Action.EXECUTE) != prevTransferAmount) {
                    //TODO: Print warning/error
                }
            }
        }
    }

    public float computeContentScale() {
        float scale = fluidTank.getFluidAmount() / (float) fluidTank.getCapacity();
        float ret = Math.max(fluidScale, scale);
        if (prevTransferAmount > 0 && ret < 1) {
            ret = Math.min(1, ret + 0.02F);
        } else if (prevTransferAmount <= 0 && ret > 0) {
            ret = Math.max(scale, ret - 0.02F);
        }
        return ret;
    }

    public int getPrevTransferAmount() {
        return prevTransferAmount;
    }

    @Override
    public String toString() {
        return "[FluidNetwork] " + transmitters.size() + " transmitters, " + possibleAcceptors.size() + " acceptors.";
    }

    @Override
    public ITextComponent getNeededInfo() {
        return MekanismLang.FLUID_NETWORK_NEEDED.translate(fluidTank.getNeeded() / 1_000F);
    }

    @Override
    public ITextComponent getStoredInfo() {
        if (fluidTank.isEmpty()) {
            return MekanismLang.NONE.translate();
        }
        return MekanismLang.NETWORK_MB_STORED.translate(fluidTank.getFluid(), fluidTank.getFluidAmount());
    }

    @Override
    public ITextComponent getFlowInfo() {
        return MekanismLang.NETWORK_MB_PER_TICK.translate(prevTransferAmount);
    }

    @Override
    public boolean isCompatibleWith(FluidNetwork other) {
        return super.isCompatibleWith(other) && (this.fluidTank.isEmpty() || other.fluidTank.isEmpty() || this.fluidTank.isFluidEqual(other.fluidTank.getFluid()));
    }

    @Override
    public boolean compatibleWithBuffer(@Nonnull FluidStack buffer) {
        return super.compatibleWithBuffer(buffer) && (this.fluidTank.isEmpty() || buffer.isEmpty() || this.fluidTank.isFluidEqual(buffer));
    }

    @Override
    public ITextComponent getTextComponent() {
        return MekanismLang.NETWORK_DESCRIPTION.translate(MekanismLang.FLUID_NETWORK, transmitters.size(), possibleAcceptors.size());
    }

    @Nonnull
    @Override
    public List<IExtendedFluidTank> getFluidTanks(@Nullable Direction side) {
        return fluidTanks;
    }

    @Override
    public void onContentsChanged() {
        updateSaveShares = true;
        FluidStack type = fluidTank.getFluid();
        if (!lastFluid.isFluidEqual(type)) {
            //If the fluid type does not match update it, and mark that we need an update
            lastFluid = type.isEmpty() ? FluidStack.EMPTY : new FluidStack(type, 1);
            needsUpdate = true;
        }
    }

    public void setLastFluid(@Nonnull FluidStack fluid) {
        if (fluid.isEmpty()) {
            fluidTank.setEmpty();
        } else {
            lastFluid = fluid;
            fluidTank.setStack(new FluidStack(fluid, 1));
        }
    }

    public static class FluidTransferEvent extends Event {

        public final FluidNetwork fluidNetwork;

        public final FluidStack fluidType;
        public final float fluidScale;

        public FluidTransferEvent(FluidNetwork network, @Nonnull FluidStack type, float scale) {
            fluidNetwork = network;
            fluidType = type;
            fluidScale = scale;
        }
    }
}