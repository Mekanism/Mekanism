package mekanism.generators.common.content.turbine;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import javax.annotation.Nonnull;
import mekanism.api.Coord4D;
import mekanism.common.multiblock.SynchronizedData;
import mekanism.common.tile.TileEntityGasTank.GasMode;
import net.minecraftforge.fluids.FluidStack;

public class SynchronizedTurbineData extends SynchronizedData<SynchronizedTurbineData> {

    public static final float ROTATION_THRESHOLD = 0.001F;
    public static Object2FloatMap<String> clientRotationMap = new Object2FloatOpenHashMap<>();

    @Nonnull
    public FluidStack fluidStored = FluidStack.EMPTY;

    @Nonnull
    public FluidStack prevFluid = FluidStack.EMPTY;

    public double electricityStored;

    public GasMode dumpMode = GasMode.IDLE;

    public int blades;
    public int vents;
    public int coils;
    public int condensers;

    public int lowerVolume;

    public Coord4D complex;

    public int lastSteamInput;
    public int newSteamInput;

    public int flowRemaining;

    public int clientDispersers;
    public int clientFlow;
    public float clientRotation;

    public int getDispersers() {
        return (volLength - 2) * (volWidth - 2) - 1;
    }

    public int getFluidCapacity() {
        return lowerVolume * TurbineUpdateProtocol.FLUID_PER_TANK;
    }

    public double getEnergyCapacity() {
        return volume * 16_000_000D; //16 MJ energy capacity per volume
    }

    public boolean needsRenderUpdate() {
        if ((fluidStored.isEmpty() && !prevFluid.isEmpty()) || (!fluidStored.isEmpty() && prevFluid.isEmpty())) {
            return true;
        }
        if (!fluidStored.isEmpty()) {
            return (fluidStored.getFluid() != prevFluid.getFluid()) || (fluidStored.getAmount() != prevFluid.getAmount());
        }
        return false;
    }
}