package mekanism.client.gui.element.gauge;

import javax.annotation.Nullable;
import mekanism.api.text.EnumColor;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.util.ResourceLocation;

public enum GaugeInfo {
    STANDARD("normal.png", 2, 2, null),
    BLUE("blue.png", 2, 2, EnumColor.DARK_BLUE),
    RED("red.png", 2, 2, EnumColor.DARK_RED),
    YELLOW("yellow.png", 2, 2, EnumColor.YELLOW);

    @Nullable
    private final EnumColor color;
    private final int sideWidth;
    private final int sideHeight;
    private final ResourceLocation resourceLocation;

    GaugeInfo(String texture, int sideWidth, int sideHeight, @Nullable EnumColor color) {
        this.resourceLocation = MekanismUtils.getResource(ResourceType.GUI_GAUGE, texture);
        this.sideWidth = sideWidth;
        this.sideHeight = sideHeight;
        this.color = color;
    }

    @Nullable
    public EnumColor getColor() {
        return color;
    }

    public int getSideWidth() {
        return sideWidth;
    }

    public int getSideHeight() {
        return sideHeight;
    }

    public ResourceLocation getResourceLocation() {
        return resourceLocation;
    }
}