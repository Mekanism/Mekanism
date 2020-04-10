package mekanism.common.util;

import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import mekanism.api.text.EnumColor;
import mekanism.api.transmitters.TransmissionType;
import mekanism.common.base.ILogisticalTransporter;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.content.transporter.TransporterManager;
import mekanism.common.content.transporter.TransporterStack;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public final class TransporterUtils {

    public static final List<EnumColor> colors = Arrays.asList(EnumColor.DARK_BLUE, EnumColor.DARK_GREEN, EnumColor.DARK_AQUA, EnumColor.DARK_RED, EnumColor.PURPLE,
          EnumColor.INDIGO, EnumColor.BRIGHT_GREEN, EnumColor.AQUA, EnumColor.RED, EnumColor.PINK, EnumColor.YELLOW, EnumColor.BLACK);

    @Nullable
    public static EnumColor readColor(int inputColor) {
        return inputColor == -1 ? null : TransporterUtils.colors.get(inputColor);
    }

    public static int getColorIndex(@Nullable EnumColor color) {
        return color == null ? -1 : TransporterUtils.colors.indexOf(color);
    }

    public static boolean isValidAcceptorOnSide(TileEntity tile, Direction side) {
        if (CapabilityUtils.getCapability(tile, Capabilities.GRID_TRANSMITTER_CAPABILITY, null).filter(transmitter ->
              TransmissionType.checkTransmissionType(transmitter, TransmissionType.ITEM)).isPresent()) {
            return false;
        }
        return InventoryUtils.isItemHandler(tile, side.getOpposite());
    }

    public static EnumColor increment(EnumColor color) {
        if (color == null) {
            return colors.get(0);
        } else if (colors.indexOf(color) == colors.size() - 1) {
            return null;
        }
        return colors.get(colors.indexOf(color) + 1);
    }

    public static EnumColor decrement(EnumColor color) {
        if (color == null) {
            return colors.get(colors.size() - 1);
        } else if (colors.indexOf(color) == 0) {
            return null;
        }
        return colors.get(colors.indexOf(color) - 1);
    }

    public static void drop(ILogisticalTransporter tile, TransporterStack stack) {
        float[] pos;
        if (stack.hasPath()) {
            pos = TransporterUtils.getStackPosition(tile, stack, 0);
        } else {
            pos = new float[]{0, 0, 0};
        }
        TransporterManager.remove(stack);
        BlockPos blockPos = new BlockPos(tile.coord().x + pos[0], tile.coord().y + pos[1], tile.coord().z + pos[2]);
        Block.spawnAsEntity(tile.world(), blockPos, stack.itemStack);
    }

    public static float[] getStackPosition(ILogisticalTransporter tile, TransporterStack stack, float partial) {
        Direction side = stack.getSide(tile);
        float progress = (((float) stack.progress + partial) / 100F) - 0.5F;
        return new float[]{0.5F + side.getXOffset() * progress, 0.25F + side.getYOffset() * progress, 0.5F + side.getZOffset() * progress};
    }

    public static void incrementColor(ILogisticalTransporter tile) {
        if (tile.getColor() == null) {
            tile.setColor(colors.get(0));
        } else if (colors.indexOf(tile.getColor()) == colors.size() - 1) {
            tile.setColor(null);
        } else {
            int index = colors.indexOf(tile.getColor());
            tile.setColor(colors.get(index + 1));
        }
    }
}