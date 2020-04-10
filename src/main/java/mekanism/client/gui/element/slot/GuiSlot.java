package mekanism.client.gui.element.slot;

import java.util.function.Supplier;
import mekanism.api.text.EnumColor;
import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.element.GuiTexturedElement;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.inventory.container.slot.SlotOverlay;
import net.minecraft.item.ItemStack;

public class GuiSlot extends GuiTexturedElement {

    private static final int DEFAULT_SLOT_COLOR = 0x80FFFFFF;
    private static final int INVALID_SLOT_COLOR = MekanismRenderer.getColorARGB(EnumColor.DARK_RED, 0.8F);
    private boolean hasValidityCheck;
    private Supplier<ItemStack> validityCheck = () -> ItemStack.EMPTY;
    private Supplier<SlotOverlay> overlaySupplier;
    private SlotOverlay overlay;
    private IHoverable onHover;
    private boolean renderHover;

    public GuiSlot(SlotType type, IGuiWrapper gui, int x, int y) {
        super(type.getTexture(), gui, x, y, type.getWidth(), type.getHeight());
        active = false;
    }

    public GuiSlot validity(Supplier<ItemStack> validityCheck) {
        hasValidityCheck = true;
        this.validityCheck = validityCheck;
        return this;
    }

    public GuiSlot hover(IHoverable onHover) {
        this.onHover = onHover;
        return this;
    }

    public GuiSlot with(SlotOverlay overlay) {
        this.overlay = overlay;
        return this;
    }

    public GuiSlot with(Supplier<SlotOverlay> overlaySupplier) {
        this.overlaySupplier = overlaySupplier;
        return this;
    }

    public GuiSlot setRenderHover(boolean renderHover) {
        this.renderHover = renderHover;
        return this;
    }

    @Override
    public void renderButton(int mouseX, int mouseY, float partialTicks) {
        minecraft.textureManager.bindTexture(getResource());
        blit(x, y, 0, 0, width, height, width, height);
        if (hasValidityCheck) {
            ItemStack invalid = validityCheck.get();
            if (!invalid.isEmpty()) {
                int xPos = x + 1;
                int yPos = y + 1;
                fill(xPos, yPos, xPos + 16, yPos + 16, INVALID_SLOT_COLOR);
                MekanismRenderer.resetColor();
                guiObj.renderItem(invalid, xPos, yPos);
            }
        }
        if (overlaySupplier != null) {
            overlay = overlaySupplier.get();
        }
        if (overlay != null) {
            minecraft.textureManager.bindTexture(overlay.getTexture());
            blit(x, y, 0, 0, overlay.getWidth(), overlay.getHeight(), overlay.getWidth(), overlay.getHeight());
        }
    }

    @Override
    public void renderForeground(int mouseX, int mouseY, int xAxis, int yAxis) {
        if (renderHover && isHovered()) {
            int xPos = relativeX + 1;
            int yPos = relativeY + 1;
            fill(xPos, yPos, xPos + 16, yPos + 16, DEFAULT_SLOT_COLOR);
            MekanismRenderer.resetColor();
        }
        if (isHovered()) {
            //TODO: Should it pass it the proper mouseX and mouseY. Probably, though buttons may have to be redone slightly then
            renderToolTip(xAxis, yAxis);
        }
    }

    @Override
    public void renderToolTip(int mouseX, int mouseY) {
        if (onHover != null) {
            onHover.onHover(this, mouseX, mouseY);
        }
    }
}