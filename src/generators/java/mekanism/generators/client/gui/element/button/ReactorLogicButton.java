package mekanism.generators.client.gui.element.button;

import javax.annotation.Nonnull;
import mekanism.api.text.EnumColor;
import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.element.button.MekanismButton;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.util.MekanismUtils.ResourceType;
import mekanism.common.util.text.TextComponentUtil;
import mekanism.generators.common.MekanismGenerators;
import mekanism.generators.common.tile.reactor.TileEntityReactorLogicAdapter;
import mekanism.generators.common.tile.reactor.TileEntityReactorLogicAdapter.ReactorLogic;
import net.minecraft.util.ResourceLocation;

public class ReactorLogicButton extends MekanismButton {

    private static final ResourceLocation TEXTURE = MekanismGenerators.rl(ResourceType.GUI_BUTTON.getPrefix() + "reactor_logic.png");
    @Nonnull
    private final TileEntityReactorLogicAdapter tile;
    private final ReactorLogic type;

    public ReactorLogicButton(IGuiWrapper gui, int x, int y, ReactorLogic type, @Nonnull TileEntityReactorLogicAdapter tile, Runnable onPress) {
        super(gui, x, y, 128, 22, "", onPress, (onHover, xAxis, yAxis) -> gui.displayTooltip(type.getDescription(), xAxis, yAxis));
        this.tile = tile;
        this.type = type;
    }

    @Override
    public void renderButton(int mouseX, int mouseY, float partialTicks) {
        MekanismRenderer.bindTexture(TEXTURE);
        MekanismRenderer.color(EnumColor.RED);
        blit(this.x, this.y, 0, type == tile.logicType ? 22 : 0, this.width, this.height, 128, 44);
        MekanismRenderer.resetColor();
    }

    public ReactorLogic getType() {
        return this.type;
    }

    @Override
    public void renderForeground(int mouseX, int mouseY, int xAxis, int yAxis) {
        int typeOffset = 22 * type.ordinal();
        guiObj.renderItem(type.getRenderStack(), 27, 35 + typeOffset);
        drawString(TextComponentUtil.build(EnumColor.WHITE, type), 46, 34 + typeOffset, 0x404040);
        super.renderForeground(mouseX, mouseY, xAxis, yAxis);
    }
}