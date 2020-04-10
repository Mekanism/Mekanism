package mekanism.common.tile;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.IConfigCardAccess.ISpecialConfigData;
import mekanism.api.NBTConstants;
import mekanism.api.RelativeSide;
import mekanism.api.sustained.ISustainedData;
import mekanism.api.text.EnumColor;
import mekanism.common.HashList;
import mekanism.common.base.ILogisticalTransporter;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.content.filter.BaseFilter;
import mekanism.common.content.filter.IFilter;
import mekanism.common.content.transporter.Finder;
import mekanism.common.content.transporter.InvStack;
import mekanism.common.content.transporter.StackSearcher;
import mekanism.common.content.transporter.TItemStackFilter;
import mekanism.common.content.transporter.TransitRequest;
import mekanism.common.content.transporter.TransitRequest.TransitResponse;
import mekanism.common.content.transporter.TransporterFilter;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.inventory.container.sync.SyncableBoolean;
import mekanism.common.inventory.container.sync.SyncableInt;
import mekanism.common.inventory.container.sync.list.SyncableFilterList;
import mekanism.common.inventory.slot.InternalInventorySlot;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.interfaces.IHasSortableFilters;
import mekanism.common.tile.interfaces.ITileFilterHolder;
import mekanism.common.util.CapabilityUtils;
import mekanism.common.util.InventoryUtils;
import mekanism.common.util.ItemDataUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.NBTUtils;
import mekanism.common.util.TransporterUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.LazyOptional;

public class TileEntityLogisticalSorter extends TileEntityMekanism implements ISpecialConfigData, ISustainedData, ITileFilterHolder<TransporterFilter<?>>,
      IHasSortableFilters {

    private HashList<TransporterFilter<?>> filters = new HashList<>();
    public EnumColor color;
    public boolean autoEject;
    public boolean roundRobin;
    public boolean singleItem;
    public int rrIndex = 0;
    private int delayTicks;

    public TileEntityLogisticalSorter() {
        super(MekanismBlocks.LOGISTICAL_SORTER);
        delaySupplier = () -> 3;
    }

    @Nonnull
    @Override
    protected IInventorySlotHolder getInitialInventory() {
        //TODO: Verify this still works. Given it MIGHT be trying to use things in an odd way
        InventorySlotHelper builder = InventorySlotHelper.forSide(this::getDirection);
        builder.addSlot(InternalInventorySlot.create(this), RelativeSide.FRONT, RelativeSide.BACK);
        return builder.build();
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        delayTicks = Math.max(0, delayTicks - 1);
        if (delayTicks == 6) {
            setActive(false);
        }

        if (MekanismUtils.canFunction(this) && delayTicks == 0) {
            TileEntity back = MekanismUtils.getTileEntity(getWorld(), pos.offset(getOppositeDirection()));
            TileEntity front = MekanismUtils.getTileEntity(getWorld(), pos.offset(getDirection()));
            //If there is no tile to pull from or the push to, skip doing any checks
            if (InventoryUtils.isItemHandler(back, getDirection()) && front != null) {
                boolean sentItems = false;
                int min = 0;

                for (TransporterFilter<?> filter : filters) {
                    for (StackSearcher search = new StackSearcher(back, getOppositeDirection()); search.getSlotCount() >= 0; ) {
                        InvStack invStack = filter.getStackFromInventory(search, singleItem);
                        if (invStack == null) {
                            break;
                        }
                        ItemStack itemStack = invStack.getStack();
                        if (filter.canFilter(itemStack, !singleItem)) {
                            if (!singleItem && filter instanceof TItemStackFilter) {
                                TItemStackFilter itemFilter = (TItemStackFilter) filter;
                                if (itemFilter.sizeMode) {
                                    min = itemFilter.min;
                                }
                            }

                            TransitRequest request = TransitRequest.getFromStack(itemStack);
                            TransitResponse response = emitItemToTransporter(front, request, filter.color, min);
                            if (!response.isEmpty()) {
                                invStack.use(response);
                                MekanismUtils.saveChunk(back);
                                setActive(true);
                                sentItems = true;
                                break;
                            }
                        }
                    }
                }

                if (!sentItems && autoEject) {
                    TransitRequest request = TransitRequest.buildInventoryMap(back, getOppositeDirection(), singleItem ? 1 : 64, new StrictFilterFinder());
                    TransitResponse response = emitItemToTransporter(front, request, color, 0);
                    if (!response.isEmpty()) {
                        response.use(back, getDirection());
                        MekanismUtils.saveChunk(back);
                        setActive(true);
                    }
                }
            }
            delayTicks = 10;
        }
    }

    private TransitResponse emitItemToTransporter(TileEntity front, TransitRequest request, EnumColor filterColor, int min) {
        Optional<ILogisticalTransporter> capability = MekanismUtils.toOptional(CapabilityUtils.getCapability(front, Capabilities.LOGISTICAL_TRANSPORTER_CAPABILITY, getOppositeDirection()));
        if (capability.isPresent()) {
            ILogisticalTransporter transporter = capability.get();
            if (roundRobin) {
                return transporter.insertRR(this, request, filterColor, true, min);
            }
            return transporter.insert(this, request, filterColor, true, min);
        }
        return InventoryUtils.putStackInInventory(front, request, getDirection(), false);
    }

    @Nonnull
    @Override
    public CompoundNBT write(CompoundNBT nbtTags) {
        super.write(nbtTags);
        return getConfigurationData(nbtTags);
    }

    @Override
    public void read(CompoundNBT nbtTags) {
        super.read(nbtTags);
        setConfigurationData(nbtTags);
    }

    @Override
    public void moveUp(int filterIndex) {
        filters.swap(filterIndex, filterIndex - 1);
        markDirty(false);
    }

    @Override
    public void moveDown(int filterIndex) {
        filters.swap(filterIndex, filterIndex + 1);
        markDirty(false);
    }

    public void toggleAutoEject() {
        autoEject = !autoEject;
        markDirty(false);
    }

    public void toggleRoundRobin() {
        roundRobin = !roundRobin;
        rrIndex = 0;
        markDirty(false);
    }

    public void toggleSingleItem() {
        singleItem = !singleItem;
        markDirty(false);
    }

    public void changeColor(@Nullable EnumColor color) {
        this.color = color;
        markDirty(false);
    }

    public boolean canSendHome(ItemStack stack) {
        TileEntity back = MekanismUtils.getTileEntity(getWorld(), pos.offset(getOppositeDirection()));
        return InventoryUtils.canInsert(back, null, stack, getOppositeDirection(), true);
    }

    public boolean hasConnectedInventory() {
        TileEntity tile = MekanismUtils.getTileEntity(getWorld(), pos.offset(getOppositeDirection()));
        return TransporterUtils.isValidAcceptorOnSide(tile, getOppositeDirection());
    }

    public TransitResponse sendHome(ItemStack stack) {
        TileEntity back = MekanismUtils.getTileEntity(getWorld(), pos.offset(getOppositeDirection()));
        return InventoryUtils.putStackInInventory(back, TransitRequest.getFromStack(stack), getOppositeDirection(), true);
    }

    @Override
    public boolean canPulse() {
        return true;
    }

    @Override
    public boolean renderUpdate() {
        return true;
    }

    @Override
    public CompoundNBT getConfigurationData(CompoundNBT nbtTags) {
        nbtTags.putInt(NBTConstants.COLOR, TransporterUtils.getColorIndex(color));
        nbtTags.putBoolean(NBTConstants.EJECT, autoEject);
        nbtTags.putBoolean(NBTConstants.ROUND_ROBIN, roundRobin);
        nbtTags.putBoolean(NBTConstants.SINGLE_ITEM, singleItem);
        nbtTags.putInt(NBTConstants.INDEX, rrIndex);
        if (!filters.isEmpty()) {
            ListNBT filterTags = new ListNBT();
            for (TransporterFilter<?> filter : filters) {
                filterTags.add(filter.write(new CompoundNBT()));
            }
            nbtTags.put(NBTConstants.FILTERS, filterTags);
        }
        return nbtTags;
    }

    @Override
    public void setConfigurationData(CompoundNBT nbtTags) {
        NBTUtils.setEnumIfPresent(nbtTags, NBTConstants.COLOR, TransporterUtils::readColor, color -> this.color = color);
        autoEject = nbtTags.getBoolean(NBTConstants.EJECT);
        roundRobin = nbtTags.getBoolean(NBTConstants.ROUND_ROBIN);
        singleItem = nbtTags.getBoolean(NBTConstants.SINGLE_ITEM);
        rrIndex = nbtTags.getInt(NBTConstants.INDEX);
        if (nbtTags.contains(NBTConstants.FILTERS, NBT.TAG_LIST)) {
            ListNBT tagList = nbtTags.getList(NBTConstants.FILTERS, NBT.TAG_COMPOUND);
            for (int i = 0; i < tagList.size(); i++) {
                IFilter<?> filter = BaseFilter.readFromNBT(tagList.getCompound(i));
                if (filter instanceof TransporterFilter) {
                    filters.add((TransporterFilter<?>) filter);
                }
            }
        }
    }

    @Override
    public String getDataType() {
        return getBlockType().getTranslationKey();
    }

    @Override
    public void writeSustainedData(ItemStack itemStack) {
        ItemDataUtils.setInt(itemStack, NBTConstants.COLOR, TransporterUtils.getColorIndex(color));
        ItemDataUtils.setBoolean(itemStack, NBTConstants.EJECT, autoEject);
        ItemDataUtils.setBoolean(itemStack, NBTConstants.ROUND_ROBIN, roundRobin);
        ItemDataUtils.setBoolean(itemStack, NBTConstants.SINGLE_ITEM, singleItem);
        if (!filters.isEmpty()) {
            ListNBT filterTags = new ListNBT();
            for (TransporterFilter<?> filter : filters) {
                filterTags.add(filter.write(new CompoundNBT()));
            }
            ItemDataUtils.setList(itemStack, NBTConstants.FILTERS, filterTags);
        }
    }

    @Override
    public void readSustainedData(ItemStack itemStack) {
        if (ItemDataUtils.hasData(itemStack, NBTConstants.COLOR, NBT.TAG_INT)) {
            color = TransporterUtils.readColor(ItemDataUtils.getInt(itemStack, NBTConstants.COLOR));
        }
        autoEject = ItemDataUtils.getBoolean(itemStack, NBTConstants.EJECT);
        roundRobin = ItemDataUtils.getBoolean(itemStack, NBTConstants.ROUND_ROBIN);
        singleItem = ItemDataUtils.getBoolean(itemStack, NBTConstants.SINGLE_ITEM);
        if (ItemDataUtils.hasData(itemStack, NBTConstants.FILTERS, NBT.TAG_LIST)) {
            ListNBT tagList = ItemDataUtils.getList(itemStack, NBTConstants.FILTERS);
            for (int i = 0; i < tagList.size(); i++) {
                IFilter<?> filter = BaseFilter.readFromNBT(tagList.getCompound(i));
                if (filter instanceof TransporterFilter) {
                    filters.add((TransporterFilter<?>) filter);
                }
            }
        }
    }

    @Override
    public Map<String, String> getTileDataRemap() {
        Map<String, String> remap = new Object2ObjectOpenHashMap<>();
        remap.put(NBTConstants.COLOR, NBTConstants.COLOR);
        remap.put(NBTConstants.EJECT, NBTConstants.EJECT);
        remap.put(NBTConstants.ROUND_ROBIN, NBTConstants.ROUND_ROBIN);
        remap.put(NBTConstants.SINGLE_ITEM, NBTConstants.SINGLE_ITEM);
        remap.put(NBTConstants.FILTERS, NBTConstants.FILTERS);
        return remap;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapabilityIfEnabled(@Nonnull Capability<T> capability, @Nullable Direction side) {
        if (capability == Capabilities.CONFIG_CARD_CAPABILITY) {
            return Capabilities.CONFIG_CARD_CAPABILITY.orEmpty(capability, LazyOptional.of(() -> this));
        } else if (capability == Capabilities.SPECIAL_CONFIG_DATA_CAPABILITY) {
            return Capabilities.SPECIAL_CONFIG_DATA_CAPABILITY.orEmpty(capability, LazyOptional.of(() -> this));
        }
        return super.getCapabilityIfEnabled(capability, side);
    }

    @Override
    public int getRedstoneLevel() {
        return getActive() ? 15 : 0;
    }

    @Override
    public int getCurrentRedstoneLevel() {
        //We don't cache the redstone level for the logistical sorter
        return getRedstoneLevel();
    }

    @Override
    public HashList<TransporterFilter<?>> getFilters() {
        return filters;
    }

    @Override
    public void addContainerTrackers(MekanismContainer container) {
        super.addContainerTrackers(container);
        container.track(SyncableBoolean.create(() -> autoEject, value -> autoEject = value));
        container.track(SyncableBoolean.create(() -> roundRobin, value -> roundRobin = value));
        container.track(SyncableBoolean.create(() -> singleItem, value -> singleItem = value));
        container.track(SyncableInt.create(() -> TransporterUtils.getColorIndex(color), value -> color = TransporterUtils.readColor(value)));
        container.track(SyncableFilterList.create(this::getFilters, value -> {
            if (value instanceof HashList) {
                filters = (HashList<TransporterFilter<?>>) value;
            } else {
                filters = new HashList<>(value);
            }
        }));
    }

    private class StrictFilterFinder extends Finder {

        @Override
        public boolean modifies(ItemStack stack) {
            return filters.stream().noneMatch(filter -> filter.canFilter(stack, false) && !filter.allowDefault);
        }
    }
}