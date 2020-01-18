package mekanism.client.gui;

import mekanism.client.gui.element.GuiScrollList;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.sound.SoundHandler;
import mekanism.common.MekanismLang;
import mekanism.common.OreDictCache;
import mekanism.common.inventory.container.item.DictionaryContainer;
import mekanism.common.registries.MekanismItems;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import org.lwjgl.glfw.GLFW;

public class GuiDictionary extends GuiMekanism<DictionaryContainer> {

    public ItemStack itemType = ItemStack.EMPTY;

    private GuiScrollList scrollList;

    public GuiDictionary(DictionaryContainer container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);
    }

    @Override
    public void init() {
        super.init();
        addButton(scrollList = new GuiScrollList(this, getGuiLocation(), 8, 30, 160, 40));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        drawString(MekanismItems.DICTIONARY.getTextComponent(), 64, 5, 0x404040);
        drawString(MekanismLang.INVENTORY.translate(), 8, getYSize() - 96 + 2, 0x404040);
        renderItem(itemType, 6, 6);
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(int xAxis, int yAxis) {
        super.drawGuiContainerBackgroundLayer(xAxis, yAxis);
        if (xAxis >= 6 && xAxis <= 22 && yAxis >= 6 && yAxis <= 22) {
            int x = getGuiLeft() + 6;
            int y = getGuiTop() + 6;
            fill(x, y, x + 16, y + 16, 0x80FFFFFF);
            MekanismRenderer.resetColor();
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        double xAxis = mouseX - getGuiLeft();
        double yAxis = mouseY - getGuiTop();
        if (button == 0) {
            if (InputMappings.isKeyDown(minecraft.mainWindow.getHandle(), GLFW.GLFW_KEY_LEFT_SHIFT)) {
                Slot hovering = null;
                for (int i = 0; i < container.inventorySlots.size(); i++) {
                    Slot slot = container.inventorySlots.get(i);
                    if (isMouseOverSlot(slot, mouseX, mouseY)) {
                        hovering = slot;
                        break;
                    }
                }

                if (hovering != null) {
                    ItemStack stack = hovering.getStack();
                    if (!stack.isEmpty()) {
                        itemType = stack.copy();
                        itemType.setCount(1);
                        scrollList.setText(OreDictCache.getOreDictName(itemType));
                        SoundHandler.playSound(SoundEvents.UI_BUTTON_CLICK);
                        return true;
                    }
                }
            }

            if (xAxis >= 6 && xAxis <= 22 && yAxis >= 6 && yAxis <= 22) {
                ItemStack stack = minecraft.player.inventory.getItemStack();
                if (!stack.isEmpty() && !InputMappings.isKeyDown(minecraft.mainWindow.getHandle(), GLFW.GLFW_KEY_LEFT_SHIFT)) {
                    itemType = stack.copy();
                    itemType.setCount(1);
                    scrollList.setText(OreDictCache.getOreDictName(itemType));
                } else if (stack.isEmpty() && InputMappings.isKeyDown(minecraft.mainWindow.getHandle(), GLFW.GLFW_KEY_LEFT_SHIFT)) {
                    itemType = ItemStack.EMPTY;
                    scrollList.setText(null);
                }
                SoundHandler.playSound(SoundEvents.UI_BUTTON_CLICK);
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected ResourceLocation getGuiLocation() {
        return MekanismUtils.getResource(ResourceType.GUI, "dictionary.png");
    }
}