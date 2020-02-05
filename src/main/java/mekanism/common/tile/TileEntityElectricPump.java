package mekanism.common.tile;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.IConfigurable;
import mekanism.api.RelativeSide;
import mekanism.api.TileNetworkList;
import mekanism.api.Upgrade;
import mekanism.api.sustained.ISustainedTank;
import mekanism.api.text.EnumColor;
import mekanism.common.Mekanism;
import mekanism.common.MekanismLang;
import mekanism.common.base.FluidHandlerWrapper;
import mekanism.common.base.IFluidHandlerWrapper;
import mekanism.common.base.ITankManager;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.config.MekanismConfig;
import mekanism.common.integration.computer.IComputerIntegration;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.inventory.slot.FluidInventorySlot;
import mekanism.common.inventory.slot.OutputInventorySlot;
import mekanism.common.inventory.slot.holder.IInventorySlotHolder;
import mekanism.common.inventory.slot.holder.InventorySlotHelper;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismFluids;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.util.CapabilityUtils;
import mekanism.common.util.EnumUtils;
import mekanism.common.util.FluidContainerUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.PipeUtils;
import mekanism.common.util.TileUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class TileEntityElectricPump extends TileEntityMekanism implements IFluidHandlerWrapper, ISustainedTank, IConfigurable, ITankManager, IComputerIntegration {

    private static final String[] methods = new String[]{"reset"};
    /**
     * This pump's tank
     */
    public FluidTank fluidTank;
    /**
     * The type of fluid this pump is pumping
     */
    @Nonnull
    public FluidStack activeType = FluidStack.EMPTY;
    public boolean suckedLastOperation;
    /**
     * How many ticks it takes to run an operation.
     */
    public int BASE_TICKS_REQUIRED = 20;
    public int ticksRequired = BASE_TICKS_REQUIRED;
    /**
     * How many ticks this machine has been operating for.
     */
    public int operatingTicks;
    /**
     * The nodes that have full sources near them or in them
     */
    private Set<BlockPos> recurringNodes = new ObjectOpenHashSet<>();

    private FluidInventorySlot inputSlot;
    private OutputInventorySlot outputSlot;
    private EnergyInventorySlot energySlot;

    public TileEntityElectricPump() {
        super(MekanismBlocks.ELECTRIC_PUMP);
    }

    @Override
    protected void presetVariables() {
        fluidTank = new FluidTank(10_000);
    }

    @Nonnull
    @Override
    protected IInventorySlotHolder getInitialInventory() {
        InventorySlotHelper builder = InventorySlotHelper.forSide(this::getDirection);
        builder.addSlot(inputSlot = FluidInventorySlot.drain(fluidTank, this, 28, 20), RelativeSide.TOP);
        builder.addSlot(outputSlot = OutputInventorySlot.at(this, 28, 51), RelativeSide.BOTTOM);
        builder.addSlot(energySlot = EnergyInventorySlot.discharge(this, 143, 35), RelativeSide.BACK);
        return builder.build();
    }

    @Override
    public void onUpdate() {
        if (!isRemote()) {
            energySlot.discharge(this);
            inputSlot.drainTank(outputSlot);
            if (MekanismUtils.canFunction(this) && getEnergy() >= getEnergyPerTick()) {
                if (suckedLastOperation) {
                    setEnergy(getEnergy() - getEnergyPerTick());
                }
                if ((operatingTicks + 1) < ticksRequired) {
                    operatingTicks++;
                } else {
                    if (fluidTank.isEmpty() || FluidAttributes.BUCKET_VOLUME <= fluidTank.getSpace()) {
                        if (!suck()) {
                            suckedLastOperation = false;
                            reset();
                        } else {
                            suckedLastOperation = true;
                        }
                    } else {
                        suckedLastOperation = false;
                    }
                    operatingTicks = 0;
                }
            } else {
                suckedLastOperation = false;
            }

            if (!fluidTank.isEmpty()) {
                TileEntity tile = MekanismUtils.getTileEntity(world, pos.up());
                CapabilityUtils.getCapability(tile, CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, Direction.DOWN).ifPresent(handler -> {
                    FluidStack toDrain = new FluidStack(fluidTank.getFluid(), Math.min(256 * (upgradeComponent.getUpgrades(Upgrade.SPEED) + 1), fluidTank.getFluidAmount()));
                    fluidTank.drain(handler.fill(toDrain, FluidAction.EXECUTE), FluidAction.EXECUTE);
                });
            }
        }
    }

    public boolean hasFilter() {
        return upgradeComponent.isUpgradeInstalled(Upgrade.FILTER);
    }

    private boolean suck() {
        boolean hasFilter = hasFilter();
        //First see if there are any fluid blocks touching the pump - if so, sucks and adds the location to the recurring list
        for (Direction orientation : EnumUtils.DIRECTIONS) {
            if (suck(pos.offset(orientation), hasFilter, true)) {
                return true;
            }
        }
        //Even though we can add to recurring in the above for loop, we always then exit and don't get to here if we did so
        List<BlockPos> tempPumpList = Arrays.asList(recurringNodes.toArray(new BlockPos[0]));
        Collections.shuffle(tempPumpList);
        //Finally, go over the recurring list of nodes and see if there is a fluid block available to suck - if not, will iterate around the recurring block, attempt to suck,
        //and then add the adjacent block to the recurring list
        for (BlockPos tempPumpPos : tempPumpList) {
            if (suck(tempPumpPos, hasFilter, false)) {
                return true;
            }
            //Add all the blocks surrounding this recurring node to the recurring node list
            for (Direction orientation : EnumUtils.DIRECTIONS) {
                BlockPos side = tempPumpPos.offset(orientation);
                if (Math.sqrt(pos.distanceSq(side)) <= MekanismConfig.general.maxPumpRange.get()) {
                    if (suck(side, hasFilter, true)) {
                        return true;
                    }
                }
            }
            recurringNodes.remove(tempPumpPos);
        }
        return false;
    }

    private boolean suck(BlockPos pos, boolean hasFilter, boolean addRecurring) {
        IFluidState fluidState = world.getFluidState(pos);
        if (!fluidState.isEmpty() && fluidState.isSource()) {
            //Just in case someone does weird things and has a fluid state that is empty and a source
            // only allow collecting from non empty sources
            //TODO: Move some of this back into a util method in MekanismUtils?
            Fluid fluid = fluidState.getFluid();
            FluidStack fluidStack = new FluidStack(fluid, FluidAttributes.BUCKET_VOLUME);
            if (hasFilter && fluid == Fluids.WATER) {
                fluid = MekanismFluids.HEAVY_WATER.getStillFluid();
                fluidStack = MekanismFluids.HEAVY_WATER.getFluidStack(10);
            }
            //Note: we get the block state from the world and not the fluid state
            // so that we can get the proper block in case it is fluid logged
            BlockState blockState = world.getBlockState(pos);
            Block block = blockState.getBlock();
            if (block instanceof IFluidBlock) {
                fluidStack = ((IFluidBlock) block).drain(world, pos, FluidAction.SIMULATE);
                if (validFluid(fluidStack, true)) {
                    //Actually drain it
                    fluidStack = ((IFluidBlock) block).drain(world, pos, FluidAction.EXECUTE);
                    suck(fluidStack, pos, addRecurring);
                    return true;
                }
            } else if (block instanceof IBucketPickupHandler && validFluid(fluidStack, false)) {
                //If it can be picked up by a bucket and we actually want to pick it up, do so to update the fluid type we are doing
                if (shouldTake(fluid)) {
                    //Note we only attempt taking if we should take the fluid type
                    // otherwise we assume the type from the fluid state is correct
                    fluid = ((IBucketPickupHandler) block).pickupFluid(world, pos, blockState);
                    //Update the fluid stack in case something somehow changed about the type
                    // making sure that we replace to heavy water if we got heavy water
                    if (hasFilter && fluid == Fluids.WATER) {
                        fluid = MekanismFluids.HEAVY_WATER.getStillFluid();
                        fluidStack = MekanismFluids.HEAVY_WATER.getFluidStack(10);
                    } else {
                        fluidStack = new FluidStack(fluid, FluidAttributes.BUCKET_VOLUME);
                    }
                    if (!validFluid(fluidStack, false)) {
                        Mekanism.logger.warn("Fluid removed without successfully picking up. Fluid {} at {} in {} was valid, but after picking up was {}.",
                              fluidState.getFluid(), pos, world, fluid);
                        return false;
                    }
                }
                suck(fluidStack, pos, addRecurring);
                return true;
            }
            //Otherwise, we do not know how to drain from the block or it is not valid and we shouldn't take it so don't handle it
        }
        return false;
    }

    //TODO: Name this method better?
    private void suck(@Nonnull FluidStack fluidStack, BlockPos pos, boolean addRecurring) {
        //Size doesn't matter, but we do want to take the NBT into account
        activeType = new FluidStack(fluidStack, 1);
        if (addRecurring) {
            recurringNodes.add(pos);
        }
        fluidTank.fill(fluidStack, FluidAction.EXECUTE);
    }

    private boolean validFluid(@Nonnull FluidStack fluidStack, boolean recheckSize) {
        if (!fluidStack.isEmpty() && (activeType.isEmpty() || activeType.isFluidEqual(fluidStack))) {
            if (fluidTank.isEmpty()) {
                return true;
            }
            if (fluidTank.getFluid().isFluidEqual(fluidStack)) {
                return !recheckSize || fluidStack.getAmount() <= fluidTank.getSpace();
            }
            return false;
        }
        return false;
    }

    public void reset() {
        activeType = FluidStack.EMPTY;
        recurringNodes.clear();
    }

    private boolean shouldTake(@Nonnull Fluid fluid) {
        return fluid == Fluids.WATER || fluid == MekanismFluids.HEAVY_WATER.getStillFluid() ? MekanismConfig.general.pumpWaterSources.get() : Boolean.valueOf(true);
    }

    @Override
    public void handlePacketData(PacketBuffer dataStream) {
        super.handlePacketData(dataStream);
        if (isRemote()) {
            TileUtils.readTankData(dataStream, fluidTank);
        }
    }

    @Override
    public TileNetworkList getNetworkedData(TileNetworkList data) {
        super.getNetworkedData(data);
        TileUtils.addTankData(data, fluidTank);
        return data;
    }

    @Nonnull
    @Override
    public CompoundNBT write(CompoundNBT nbtTags) {
        super.write(nbtTags);
        nbtTags.putInt("operatingTicks", operatingTicks);
        nbtTags.putBoolean("suckedLastOperation", suckedLastOperation);
        if (!activeType.isEmpty()) {
            nbtTags.put("activeType", activeType.writeToNBT(new CompoundNBT()));
        }
        if (!fluidTank.isEmpty()) {
            nbtTags.put("fluidTank", fluidTank.writeToNBT(new CompoundNBT()));
        }
        ListNBT recurringList = new ListNBT();
        for (BlockPos nodePos : recurringNodes) {
            CompoundNBT tagCompound = new CompoundNBT();
            tagCompound.putInt("x", nodePos.getX());
            tagCompound.putInt("y", nodePos.getY());
            tagCompound.putInt("z", nodePos.getZ());
            recurringList.add(tagCompound);
        }
        if (!recurringList.isEmpty()) {
            nbtTags.put("recurringNodes", recurringList);
        }
        return nbtTags;
    }

    @Override
    public void read(CompoundNBT nbtTags) {
        super.read(nbtTags);
        operatingTicks = nbtTags.getInt("operatingTicks");
        suckedLastOperation = nbtTags.getBoolean("suckedLastOperation");
        if (nbtTags.contains("activeType")) {
            activeType = FluidStack.loadFluidStackFromNBT(nbtTags.getCompound("activeType"));
        }
        if (nbtTags.contains("fluidTank")) {
            fluidTank.readFromNBT(nbtTags.getCompound("fluidTank"));
        }
        if (nbtTags.contains("recurringNodes")) {
            ListNBT tagList = nbtTags.getList("recurringNodes", NBT.TAG_COMPOUND);
            for (int i = 0; i < tagList.size(); i++) {
                CompoundNBT compound = tagList.getCompound(i);
                recurringNodes.add(new BlockPos(compound.getInt("x"), compound.getInt("y"), compound.getInt("z")));
            }
        }
    }

    @Override
    public boolean canReceiveEnergy(Direction side) {
        return getOppositeDirection() == side;
    }

    @Override
    public IFluidTank[] getTankInfo(Direction direction) {
        if (direction == Direction.UP) {
            return new IFluidTank[]{fluidTank};
        }
        return PipeUtils.EMPTY;
    }

    @Override
    public IFluidTank[] getAllTanks() {
        return getTankInfo(Direction.UP);
    }

    @Override
    public void setFluidStack(@Nonnull FluidStack fluidStack, Object... data) {
        fluidTank.setFluid(fluidStack);
    }

    @Nonnull
    @Override
    public FluidStack getFluidStack(Object... data) {
        return fluidTank.getFluid();
    }

    @Override
    public boolean hasTank(Object... data) {
        return true;
    }

    @Nonnull
    @Override
    public FluidStack drain(Direction from, int maxDrain, FluidAction fluidAction) {
        return fluidTank.drain(maxDrain, fluidAction);
    }

    @Override
    public boolean canDrain(Direction from, @Nonnull FluidStack fluid) {
        return from == Direction.byIndex(1) && FluidContainerUtils.canDrain(fluidTank.getFluid(), fluid);
    }

    @Override
    public ActionResultType onSneakRightClick(PlayerEntity player, Direction side) {
        reset();
        player.sendMessage(MekanismLang.LOG_FORMAT.translateColored(EnumColor.DARK_BLUE, MekanismLang.MEKANISM, MekanismLang.PUMP_RESET.translateColored(EnumColor.GRAY)));
        return ActionResultType.SUCCESS;
    }

    @Override
    public ActionResultType onRightClick(PlayerEntity player, Direction side) {
        return ActionResultType.PASS;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction side) {
        if (capability == Capabilities.CONFIGURABLE_CAPABILITY) {
            return Capabilities.CONFIGURABLE_CAPABILITY.orEmpty(capability, LazyOptional.of(() -> this));
        }
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.orEmpty(capability, LazyOptional.of(() -> new FluidHandlerWrapper(this, side)));
        }
        return super.getCapability(capability, side);
    }

    @Override
    public boolean canPulse() {
        return true;
    }

    @Override
    public Object[] getTanks() {
        return new Object[]{fluidTank};
    }

    @Override
    public String[] getMethods() {
        return methods;
    }

    @Override
    public Object[] invoke(int method, Object[] arguments) throws NoSuchMethodException {
        if (method == 0) {
            reset();
            return new Object[]{"Pump calculation reset."};
        }
        throw new NoSuchMethodException();
    }

    @Override
    public void recalculateUpgrades(Upgrade upgrade) {
        super.recalculateUpgrades(upgrade);
        if (upgrade == Upgrade.SPEED) {
            ticksRequired = MekanismUtils.getTicks(this, BASE_TICKS_REQUIRED);
        }
    }

    @Override
    public int getRedstoneLevel() {
        return MekanismUtils.redstoneLevelFromContents(fluidTank.getFluidAmount(), fluidTank.getCapacity());
    }
}