package mekanism.client.gui.element.scroll;

import java.util.Set;
import javax.annotation.Nullable;
import mekanism.api.Upgrade;
import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.element.GuiElementHolder;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.tile.component.TileComponentUpgrade;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import mekanism.common.util.UpgradeUtils;
import mekanism.common.util.text.TextComponentUtil;
import net.minecraft.util.ResourceLocation;

public class GuiUpgradeScrollList extends GuiScrollList {

    private static ResourceLocation UPGRADE_SELECTION = MekanismUtils.getResource(ResourceType.GUI, "upgrade_selection.png");
    private static int TEXTURE_WIDTH = 58;
    private static int TEXTURE_HEIGHT = 36;

    @Nullable
    private Upgrade selectedType;
    private final TileComponentUpgrade component;

    public GuiUpgradeScrollList(IGuiWrapper gui, int x, int y, int width, int height, TileComponentUpgrade component) {
        super(gui, x, y, width, height, TEXTURE_HEIGHT / 3, new GuiElementHolder(gui, x, y, width, height));
        this.component = component;
    }

    private Set<Upgrade> getCurrentUpgrades() {
        return component.getInstalledTypes();
    }

    @Override
    protected int getMaxElements() {
        return getCurrentUpgrades().size();
    }

    @Override
    public boolean hasSelection() {
        return selectedType != null;
    }

    @Override
    protected void setSelected(int index) {
        Set<Upgrade> currentUpgrades = getCurrentUpgrades();
        if (index >= 0 && index < currentUpgrades.size()) {
            selectedType = currentUpgrades.toArray(new Upgrade[0])[index];
        }
    }

    @Nullable
    public Upgrade getSelection() {
        return selectedType;
    }

    @Override
    public void clearSelection() {
        selectedType = null;
    }

    @Override
    public void renderForeground(int mouseX, int mouseY, int xAxis, int yAxis) {
        super.renderForeground(mouseX, mouseY, xAxis, yAxis);
        Upgrade[] upgrades = getCurrentUpgrades().toArray(new Upgrade[0]);
        for (int i = 0; i < getFocusedElements(); i++) {
            int index = getCurrentSelection() + i;
            if (index > upgrades.length - 1) {
                break;
            }
            Upgrade upgrade = upgrades[index];
            int multipliedElement = elementHeight * i;
            //Always render the name and upgrade
            drawString(TextComponentUtil.build(upgrade), relativeX + 13, relativeY + 3 + multipliedElement, 0x404040);
            renderUpgrade(upgrade, relativeX + 3, relativeY + 3 + multipliedElement, 0.5F);
            //Only render the tooltip describing the upgrade when hovering over it though
            if (mouseX >= x + 1 && mouseX < barX - 1 && mouseY >= y + 1 + multipliedElement && mouseY <= y + 1 + multipliedElement + elementHeight) {
                guiObj.displayTooltip(upgrade.getDescription(), xAxis, yAxis, guiObj.getWidth());
            }
        }
    }

    @Override
    public void renderElements(int mouseX, int mouseY, float partialTicks) {
        //Draw elements
        if (hasSelection() && component.getUpgrades(getSelection()) == 0) {
            clearSelection();
        }
        minecraft.textureManager.bindTexture(UPGRADE_SELECTION);
        Upgrade[] upgrades = getCurrentUpgrades().toArray(new Upgrade[0]);
        for (int i = 0; i < getFocusedElements(); i++) {
            int index = getCurrentSelection() + i;
            if (index > upgrades.length - 1) {
                break;
            }
            Upgrade upgrade = upgrades[index];
            int shiftedY = y + 1 + elementHeight * i;
            int j = 1;
            if (upgrade == getSelection()) {
                j = 2;
            } else if (mouseX >= x + 1 && mouseX < barX - 1 && mouseY >= shiftedY && mouseY <= shiftedY + elementHeight) {
                j = 0;
            }
            MekanismRenderer.color(upgrade.getColor(), 1.0F, 2.5F);
            blit(x + 1, shiftedY, 0, elementHeight * j, TEXTURE_WIDTH, elementHeight, TEXTURE_WIDTH, TEXTURE_HEIGHT);
            MekanismRenderer.resetColor();
        }
    }

    private void renderUpgrade(Upgrade type, int x, int y, float size) {
        guiObj.renderItem(UpgradeUtils.getStack(type), (int) (x / size), (int) (y / size), size);
    }
}