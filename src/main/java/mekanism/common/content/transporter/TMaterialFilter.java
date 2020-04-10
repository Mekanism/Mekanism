package mekanism.common.content.transporter;

import javax.annotation.Nonnull;
import mekanism.common.content.filter.FilterType;
import mekanism.common.content.filter.IMaterialFilter;
import mekanism.common.content.transporter.Finder.MaterialFinder;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

public class TMaterialFilter extends TransporterFilter<TMaterialFilter> implements IMaterialFilter<TMaterialFilter> {

    private ItemStack materialItem = ItemStack.EMPTY;

    public Material getMaterial() {
        return Block.getBlockFromItem(materialItem.getItem()).getDefaultState().getMaterial();
    }

    @Override
    public boolean canFilter(ItemStack itemStack, boolean strict) {
        return super.canFilter(itemStack, strict) && (itemStack.getItem() instanceof BlockItem) && new MaterialFinder(getMaterial()).modifies(itemStack);
    }

    @Override
    public Finder getFinder() {
        return new MaterialFinder(getMaterial());
    }

    @Override
    public CompoundNBT write(CompoundNBT nbtTags) {
        super.write(nbtTags);
        materialItem.write(nbtTags);
        return nbtTags;
    }

    @Override
    public void read(CompoundNBT nbtTags) {
        super.read(nbtTags);
        materialItem = ItemStack.read(nbtTags);
    }

    @Override
    public void write(PacketBuffer buffer) {
        super.write(buffer);
        buffer.writeItemStack(materialItem);
    }

    @Override
    public void read(PacketBuffer dataStream) {
        super.read(dataStream);
        materialItem = dataStream.readItemStack();
    }

    @Override
    public int hashCode() {
        int code = 1;
        code = 31 * code + super.hashCode();
        code = 31 * code + materialItem.hashCode();
        return code;
    }

    @Override
    public boolean equals(Object filter) {
        return super.equals(filter) && filter instanceof TMaterialFilter && ((TMaterialFilter) filter).materialItem.isItemEqual(materialItem);
    }

    @Override
    public TMaterialFilter clone() {
        TMaterialFilter filter = new TMaterialFilter();
        filter.allowDefault = allowDefault;
        filter.color = color;
        filter.materialItem = materialItem;
        return filter;
    }

    @Override
    public FilterType getFilterType() {
        return FilterType.SORTER_MATERIAL_FILTER;
    }

    @Nonnull
    @Override
    public ItemStack getMaterialItem() {
        return materialItem;
    }

    @Override
    public void setMaterialItem(@Nonnull ItemStack stack) {
        materialItem = stack;
    }
}