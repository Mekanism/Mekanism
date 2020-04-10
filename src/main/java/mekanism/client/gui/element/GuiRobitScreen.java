package mekanism.client.gui.element;

import java.util.function.BooleanSupplier;
import mekanism.client.gui.IGuiWrapper;
import net.minecraft.util.ResourceLocation;

public class GuiRobitScreen extends GuiElementHolder {

    private final BooleanSupplier showRename;

    public GuiRobitScreen(IGuiWrapper gui, int x, int y, int width, int height, BooleanSupplier showRename) {
        super(gui, x, y, width, height);
        this.showRename = showRename;
    }

    @Override
    protected ResourceLocation getResource() {
        if (showRename.getAsBoolean()) {
            return super.getResource();
        }
        return GuiInnerScreen.SCREEN;
    }
}