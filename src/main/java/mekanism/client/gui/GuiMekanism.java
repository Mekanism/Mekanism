package mekanism.client.gui;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

import com.mojang.blaze3d.platform.GlStateManager;
import mekanism.api.text.EnumColor;
import mekanism.client.gui.button.MekanismButton.IHoverable;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.Mekanism;
import mekanism.common.base.ILangEntry;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.client.config.GuiUtils;
import org.lwjgl.glfw.GLFW;

public abstract class GuiMekanism<CONTAINER extends Container> extends ContainerScreen<CONTAINER> implements IGuiWrapper {

    protected GuiMekanism(CONTAINER container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);
    }

    public static boolean isTextboxKey(char c, int keyCode) {
        return keyCode == GLFW.GLFW_KEY_BACKSPACE || keyCode == GLFW.GLFW_KEY_DELETE || keyCode == GLFW.GLFW_KEY_LEFT || keyCode == GLFW.GLFW_KEY_RIGHT || keyCode == GLFW.GLFW_KEY_END ||
               keyCode == GLFW.GLFW_KEY_HOME || Screen.isSelectAll(keyCode) || Screen.isCopy(keyCode) || Screen.isPaste(keyCode) || Screen.isCut(keyCode);
    }

    protected float getNeededScale(ITextComponent text, int maxX) {
        int length = getStringWidth(text);
        return length <= maxX ? 1 : (float) maxX / length;
    }

    protected IHoverable getOnHover(ILangEntry translationHelper) {
        return getOnHover((Supplier<ITextComponent>) translationHelper::translate);
    }

    protected IHoverable getOnHover(Supplier<ITextComponent> componentSupplier) {
        return (onHover, xAxis, yAxis) -> displayTooltip(componentSupplier.get(), xAxis, yAxis);
    }

    protected ResourceLocation getButtonLocation(String name) {
        return MekanismUtils.getResource(ResourceType.GUI_BUTTON, name + ".png");
    }

    public int getStringWidth(ITextComponent component) {
        //TODO: See if this should be calculated in a different way
        return getStringWidth(component.getFormattedText());
    }

    public int getStringWidth(String text) {
        //TODO: Replace uses of this/the other getStringWidth with drawCenteredText where appropriate
        return font.getStringWidth(text);
    }

    public int drawString(ITextComponent component, int x, int y, int color) {
        //TODO: Check if color actually does anything
        return drawString(component.getFormattedText(), x, y, color);
    }

    public int drawString(String text, int x, int y, int color) {
        //TODO: Eventually make drawString(ITextComponent) be what gets used in places
        return font.drawString(text, x, y, color);
    }

    protected void drawCenteredText(ITextComponent component, int y, int color) {
        //TODO: Go back and evaluate to make sure that the left margin is correct
        drawCenteredText(component, 0, y, color);
    }

    protected void drawCenteredText(ITextComponent component, int leftMargin, int y, int color) {
        drawCenteredText(component, leftMargin, 0, y, color);
    }

    protected void drawCenteredText(ITextComponent component, int leftMargin, int areaWidth, int y, int color) {
        int textWidth = getStringWidth(component);
        int centerX = leftMargin + (areaWidth / 2) - (textWidth / 2);
        drawString(component, centerX, y, color);
    }

    public void renderScaledText(ITextComponent component, int x, int y, int color, int maxX) {
        renderScaledText(component.getFormattedText(), x, y, color, maxX);
    }

    /**
     * returns scale
     */
    public void renderScaledText(String text, int x, int y, int color, int maxX) {
        int length = getStringWidth(text);
        if (length <= maxX) {
            drawString(text, x, y, color);
        } else {
            float scale = (float) maxX / length;
            float reverse = 1 / scale;
            float yAdd = 4 - (scale * 8) / 2F;
            GlStateManager.pushMatrix();
            GlStateManager.scalef(scale, scale, scale);
            drawString(text, (int) (x * reverse), (int) ((y * reverse) + yAdd), color);
            GlStateManager.popMatrix();
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        int xAxis = mouseX - getGuiLeft();
        int yAxis = mouseY - getGuiTop();
        //TODO: Does color need to be reset before this
        for (Widget widget : this.buttons) {
            if (widget.isMouseOver(mouseX, mouseY)) {//.isHovered()) {
                //TODO: Should it pass it the proper mouseX and mouseY. Probably, though buttons may have to be redone slightly then
                widget.renderToolTip(xAxis, yAxis);
                break;
            }
        }
    }

    protected boolean isMouseOverSlot(Slot slot, double mouseX, double mouseY) {
        return isPointInRegion(slot.xPos, slot.yPos, 16, 16, mouseX, mouseY);
    }

    protected void drawGuiContainerBackgroundLayer(int xAxis, int yAxis) {
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY) {
        //Ensure the GL color is white as mods adding an overlay (such as JEI for bookmarks), might have left
        // it in an unexpected state.
        MekanismRenderer.resetColor();
        minecraft.textureManager.bindTexture(getGuiLocation());
        drawTexturedRect(getGuiLeft(), getGuiTop(), 0, 0, getXSize(), getYSize());
        int xAxis = mouseX - getGuiLeft();
        int yAxis = mouseY - getGuiTop();
        drawGuiContainerBackgroundLayer(xAxis, yAxis);
    }

    @Override
    public void drawTexturedRect(int x, int y, int textureX, int textureY, int width, int height) {
        blit(x, y, textureX, textureY, width, height);
    }

    @Override
    public void drawTexturedRectFromIcon(int x, int y, TextureAtlasSprite icon, int width, int height) {
        blit(x, y, blitOffset, width, height, icon);
    }

    @Override
    public void drawModalRectWithCustomSizedTexture(int x, int y, int textureX, int textureY, int width, int height, int textureWidth, int textureHeight) {
        blit(x, y, textureX, textureY, width, height, textureWidth, textureHeight);
    }

    @Override
    public void drawModalRectWithCustomSizedTexture(int x, int y, int desiredWidth, int desiredHeight, int textureX, int textureY, int width, int height, int textureWidth, int textureHeight) {
        blit(x, y, desiredWidth, desiredHeight, textureX, textureY, width, height, textureWidth, textureHeight);
    }

    @Override
    public void displayTooltip(ITextComponent component, int x, int y) {
        this.displayTooltips(Collections.singletonList(component), x, y);
    }

    @Override
    public void displayTooltips(List<ITextComponent> components, int xAxis, int yAxis) {
        //TODO: Evaluate if we want to use this for splitting the text
        List<String> toolTips = components.stream().map(ITextComponent::getFormattedText).collect(Collectors.toList());
        GuiUtils.drawHoveringText(toolTips, xAxis, yAxis, width, height, -1, font);
        //Fix unwanted lighting changes made by drawHoveringText
        RenderHelper.disableStandardItemLighting();
    }

    @Override
    public FontRenderer getFont() {
        return font;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    protected void drawColorIcon(int x, int y, EnumColor color, float alpha) {
        if (color != null) {
            fill(x, y, x + 16, y + 16, MekanismRenderer.getColorARGB(color, alpha));
            MekanismRenderer.resetColor();
        }
    }

    protected void renderItem(@Nonnull ItemStack stack, int xAxis, int yAxis) {
        renderItem(stack, xAxis, yAxis, 1);
    }

    protected void renderItem(@Nonnull ItemStack stack, int xAxis, int yAxis, float scale) {
        if (!stack.isEmpty()) {
            try {
                GlStateManager.pushMatrix();
                GlStateManager.enableDepthTest();
                RenderHelper.enableGUIStandardItemLighting();
                if (scale != 1) {
                    GlStateManager.scalef(scale, scale, scale);
                }
                //TODO: renderItemAndEffectIntoGUI has some form of GL leak. Fix it
                itemRenderer.renderItemAndEffectIntoGUI(stack, xAxis, yAxis);
                RenderHelper.disableStandardItemLighting();
                GlStateManager.disableDepthTest();
                GlStateManager.popMatrix();
            } catch (Exception e) {
                Mekanism.logger.error("Failed to render stack into gui: " + stack, e);
            }
        }
    }

    protected abstract ResourceLocation getGuiLocation();
}