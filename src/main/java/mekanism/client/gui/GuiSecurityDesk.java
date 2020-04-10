package mekanism.client.gui;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import mekanism.api.text.EnumColor;
import mekanism.client.gui.element.GuiInnerHolder;
import mekanism.client.gui.element.GuiInnerScreen;
import mekanism.client.gui.element.GuiSecurityLight;
import mekanism.client.gui.element.GuiTextureOnlyElement;
import mekanism.client.gui.element.button.MekanismButton;
import mekanism.client.gui.element.button.MekanismImageButton;
import mekanism.client.gui.element.button.TranslationButton;
import mekanism.client.gui.element.scroll.GuiTextScrollList;
import mekanism.common.Mekanism;
import mekanism.common.MekanismLang;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.network.PacketAddTrusted;
import mekanism.common.network.PacketGuiInteract;
import mekanism.common.network.PacketGuiInteract.GuiInteraction;
import mekanism.common.security.ISecurityTile.SecurityMode;
import mekanism.common.tile.TileEntitySecurityDesk;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import mekanism.common.util.text.BooleanStateDisplay.OnOff;
import mekanism.common.util.text.OwnerDisplay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import org.lwjgl.glfw.GLFW;

public class GuiSecurityDesk extends GuiMekanismTile<TileEntitySecurityDesk, MekanismTileContainer<TileEntitySecurityDesk>> {

    private static final ResourceLocation PUBLIC = MekanismUtils.getResource(ResourceType.GUI, "public.png");
    private static final ResourceLocation PRIVATE = MekanismUtils.getResource(ResourceType.GUI, "private.png");
    private static final List<Character> SPECIAL_CHARS = Arrays.asList('-', '|', '_');
    private MekanismButton removeButton;
    private MekanismButton publicButton;
    private MekanismButton privateButton;
    private MekanismButton trustedButton;
    private MekanismButton checkboxButton;
    private MekanismButton overrideButton;
    private GuiTextScrollList scrollList;
    private TextFieldWidget trustedField;

    public GuiSecurityDesk(MekanismTileContainer<TileEntitySecurityDesk> container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);
        ySize += 64;
        dynamicSlots = true;
    }

    @Override
    protected void initPreSlots() {
        addButton(new GuiInnerHolder(this, 141, 13, 26, 37));
        addButton(new GuiInnerHolder(this, 141, 54, 26, 34));
        addButton(new GuiInnerHolder(this, 141, 92, 26, 37));
    }

    @Override
    public void init() {
        super.init();
        addButton(new GuiInnerScreen(this, 34, 67, 89, 13));
        addButton(new GuiInnerScreen(this, 122, 67, 13, 13));
        addButton(new GuiSecurityLight(this, 144, 77, () -> tile.frequency == null || tile.ownerUUID == null ||
                                                            !tile.ownerUUID.equals(minecraft.player.getUniqueID()) ? 2 : tile.frequency.override ? 0 : 1));
        addButton(new GuiTextureOnlyElement(PUBLIC, this, 146, 33, 18, 18));
        addButton(new GuiTextureOnlyElement(PRIVATE, this, 146, 112, 18, 18));
        addButton(scrollList = new GuiTextScrollList(this, 13, 13, 122, 42));
        addButton(removeButton = new TranslationButton(this, getGuiLeft() + 13, getGuiTop() + 81, 122, 20, MekanismLang.BUTTON_REMOVE, () -> {
            int selection = scrollList.getSelection();
            if (tile.frequency != null && selection != -1) {
                Mekanism.packetHandler.sendToServer(new PacketGuiInteract(GuiInteraction.REMOVE_TRUSTED, tile, selection));
                scrollList.clearSelection();
                updateButtons();
            }
        }));
        addButton(trustedField = new TextFieldWidget(font, getGuiLeft() + 35, getGuiTop() + 69, 86, 11, ""));
        trustedField.setMaxStringLength(PacketAddTrusted.MAX_NAME_LENGTH);
        trustedField.setEnableBackgroundDrawing(false);
        addButton(publicButton = new MekanismImageButton(this, getGuiLeft() + 13, getGuiTop() + 113, 40, 16, 40, 16, getButtonLocation("public"),
              () -> {
                  Mekanism.packetHandler.sendToServer(new PacketGuiInteract(GuiInteraction.SECURITY_DESK_MODE, tile, SecurityMode.PUBLIC.ordinal()));
                  updateButtons();
              }, getOnHover(MekanismLang.PUBLIC_MODE)));
        addButton(privateButton = new MekanismImageButton(this, getGuiLeft() + 54, getGuiTop() + 113, 40, 16, 40, 16, getButtonLocation("private"),
              () -> {
                  Mekanism.packetHandler.sendToServer(new PacketGuiInteract(GuiInteraction.SECURITY_DESK_MODE, tile, SecurityMode.PRIVATE.ordinal()));
                  updateButtons();
              }, getOnHover(MekanismLang.PRIVATE_MODE)));
        addButton(trustedButton = new MekanismImageButton(this, getGuiLeft() + 95, getGuiTop() + 113, 40, 16, 40, 16, getButtonLocation("trusted"),
              () -> {
                  Mekanism.packetHandler.sendToServer(new PacketGuiInteract(GuiInteraction.SECURITY_DESK_MODE, tile, SecurityMode.TRUSTED.ordinal()));
                  updateButtons();
              }, getOnHover(MekanismLang.TRUSTED_MODE)));
        addButton(checkboxButton = new MekanismImageButton(this, getGuiLeft() + 123, getGuiTop() + 68, 11, 12, getButtonLocation("checkmark"),
              () -> {
                  addTrusted(trustedField.getText());
                  trustedField.setText("");
                  updateButtons();
              }));
        addButton(overrideButton = new MekanismImageButton(this, getGuiLeft() + 146, getGuiTop() + 59, 16, 16, getButtonLocation("exclamation"),
              () -> {
                  Mekanism.packetHandler.sendToServer(new PacketGuiInteract(GuiInteraction.OVERRIDE_BUTTON, tile));
                  updateButtons();
              }, (onHover, xAxis, yAxis) -> {
            if (tile.frequency != null) {
                displayTooltip(MekanismLang.SECURITY_OVERRIDE.translate(OnOff.of(tile.frequency.override)), xAxis, yAxis);
            }
        }));
        updateButtons();
    }

    @Override
    public void resize(@Nonnull Minecraft minecraft, int scaledWidth, int scaledHeight) {
        String s = trustedField.getText();
        super.resize(minecraft, scaledWidth, scaledHeight);
        trustedField.setText(s);
    }

    private void addTrusted(String trusted) {
        if (PacketAddTrusted.validateNameLength(trusted.length())) {
            Mekanism.packetHandler.sendToServer(new PacketAddTrusted(tile.getPos(), trusted));
        }
    }

    private void updateButtons() {
        if (tile.ownerUUID != null) {
            scrollList.setText(tile.frequency == null ? Collections.emptyList() : tile.frequency.trustedCache);
            removeButton.active = scrollList.hasSelection();
        }

        if (tile.frequency != null && tile.ownerUUID != null && tile.ownerUUID.equals(minecraft.player.getUniqueID())) {
            publicButton.active = tile.frequency.securityMode != SecurityMode.PUBLIC;
            privateButton.active = tile.frequency.securityMode != SecurityMode.PRIVATE;
            trustedButton.active = tile.frequency.securityMode != SecurityMode.TRUSTED;
            checkboxButton.active = true;
            overrideButton.active = true;
        } else {
            publicButton.active = false;
            privateButton.active = false;
            trustedButton.active = false;
            checkboxButton.active = false;
            overrideButton.active = false;
        }
    }

    @Override
    public void tick() {
        super.tick();
        updateButtons();
        trustedField.tick();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        updateButtons();
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (trustedField.canWrite()) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                //Manually handle hitting escape making the field lose focus
                trustedField.setFocused2(false);
                return true;
            } else if (keyCode == GLFW.GLFW_KEY_ENTER) {
                addTrusted(trustedField.getText());
                trustedField.setText("");
                return true;
            }
            return trustedField.keyPressed(keyCode, scanCode, modifiers);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char c, int keyCode) {
        if (trustedField.canWrite()) {
            if (SPECIAL_CHARS.contains(c) || Character.isDigit(c) || Character.isLetter(c)) {
                //Only allow a subset of characters to be entered into the trustedField text box
                return trustedField.charTyped(c, keyCode);
            }
            return false;
        }
        return super.charTyped(c, keyCode);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        drawString(tile.getName(), (getXSize() / 2) - (getStringWidth(tile.getName()) / 2), 4, 0x404040);
        ITextComponent ownerComponent = OwnerDisplay.of(tile.ownerUUID, tile.clientOwner).getTextComponent();
        drawString(ownerComponent, getXSize() - 7 - getStringWidth(ownerComponent), (getYSize() - 96) + 2, 0x404040);
        drawString(MekanismLang.INVENTORY.translate(), 8, (getYSize() - 96) + 2, 0x404040);
        drawCenteredText(MekanismLang.TRUSTED_PLAYERS.translate(), 74, 57, 0x787878);
        if (tile.frequency != null) {
            drawString(MekanismLang.SECURITY.translate(tile.frequency.securityMode), 13, 103, 0x404040);
        } else {
            drawString(MekanismLang.SECURITY_OFFLINE.translateColored(EnumColor.RED), 13, 103, 0x404040);
        }
        renderScaledText(MekanismLang.SECURITY_ADD.translate(), 13, 70, 0x404040, 20);
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }
}