package mekanism.common.content.transporter;

import mekanism.api.NBTConstants;
import mekanism.common.content.filter.FilterType;
import mekanism.common.content.filter.IModIDFilter;
import mekanism.common.content.transporter.Finder.ModIDFinder;
import mekanism.common.network.BasePacketHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

public class TModIDFilter extends TransporterFilter<TModIDFilter> implements IModIDFilter<TModIDFilter> {

    private String modID;

    @Override
    public boolean canFilter(ItemStack itemStack, boolean strict) {
        return super.canFilter(itemStack, strict) && new ModIDFinder(modID).modifies(itemStack);
    }

    @Override
    public Finder getFinder() {
        return new ModIDFinder(modID);
    }

    @Override
    public CompoundNBT write(CompoundNBT nbtTags) {
        super.write(nbtTags);
        nbtTags.putString(NBTConstants.MODID, modID);
        return nbtTags;
    }

    @Override
    public void read(CompoundNBT nbtTags) {
        super.read(nbtTags);
        modID = nbtTags.getString(NBTConstants.MODID);
    }

    @Override
    public void write(PacketBuffer buffer) {
        super.write(buffer);
        buffer.writeString(modID);
    }

    @Override
    public void read(PacketBuffer dataStream) {
        super.read(dataStream);
        modID = BasePacketHandler.readString(dataStream);
    }

    @Override
    public int hashCode() {
        int code = 1;
        code = 31 * code + super.hashCode();
        code = 31 * code + modID.hashCode();
        return code;
    }

    @Override
    public boolean equals(Object filter) {
        return super.equals(filter) && filter instanceof TModIDFilter && ((TModIDFilter) filter).modID.equals(modID);
    }

    @Override
    public TModIDFilter clone() {
        TModIDFilter filter = new TModIDFilter();
        filter.allowDefault = allowDefault;
        filter.color = color;
        filter.modID = modID;
        return filter;
    }

    @Override
    public FilterType getFilterType() {
        return FilterType.SORTER_MODID_FILTER;
    }

    @Override
    public void setModID(String id) {
        modID = id;
    }

    @Override
    public String getModID() {
        return modID;
    }
}