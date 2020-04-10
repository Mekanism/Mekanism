package mekanism.common.content.transporter;

import mekanism.api.NBTConstants;
import mekanism.common.content.filter.FilterType;
import mekanism.common.content.filter.ITagFilter;
import mekanism.common.content.transporter.Finder.TagFinder;
import mekanism.common.network.BasePacketHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

public class TTagFilter extends TransporterFilter<TTagFilter> implements ITagFilter<TTagFilter> {

    private String tagName;

    @Override
    public boolean canFilter(ItemStack itemStack, boolean strict) {
        return super.canFilter(itemStack, strict) && getFinder().modifies(itemStack);
    }

    @Override
    public Finder getFinder() {
        return new TagFinder(tagName);
    }

    @Override
    public CompoundNBT write(CompoundNBT nbtTags) {
        super.write(nbtTags);
        nbtTags.putString(NBTConstants.TAG_NAME, tagName);
        return nbtTags;
    }

    @Override
    public void read(CompoundNBT nbtTags) {
        super.read(nbtTags);
        tagName = nbtTags.getString(NBTConstants.TAG_NAME);
    }

    @Override
    public void write(PacketBuffer buffer) {
        super.write(buffer);
        buffer.writeString(tagName);
    }

    @Override
    public void read(PacketBuffer dataStream) {
        super.read(dataStream);
        tagName = BasePacketHandler.readString(dataStream);
    }

    @Override
    public int hashCode() {
        int code = 1;
        code = 31 * code + super.hashCode();
        code = 31 * code + tagName.hashCode();
        return code;
    }

    @Override
    public boolean equals(Object filter) {
        return super.equals(filter) && filter instanceof TTagFilter && ((TTagFilter) filter).tagName.equals(tagName);
    }

    @Override
    public TTagFilter clone() {
        TTagFilter filter = new TTagFilter();
        filter.allowDefault = allowDefault;
        filter.color = color;
        filter.tagName = tagName;
        return filter;
    }

    @Override
    public FilterType getFilterType() {
        return FilterType.SORTER_TAG_FILTER;
    }

    @Override
    public void setTagName(String name) {
        tagName = name;
    }

    @Override
    public String getTagName() {
        return tagName;
    }
}