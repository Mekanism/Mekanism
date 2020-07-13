package mekanism.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import mekanism.api.text.EnumColor;
import mekanism.client.gui.element.bar.GuiVerticalPowerBar;
import mekanism.client.gui.element.button.ColorButton;
import mekanism.client.gui.element.button.MekanismButton;
import mekanism.client.gui.element.button.TranslationButton;
import mekanism.client.gui.element.custom.GuiTeleporterStatus;
import mekanism.client.gui.element.scroll.GuiTextScrollList;
import mekanism.client.gui.element.slot.GuiSlot;
import mekanism.client.gui.element.slot.SlotType;
import mekanism.client.gui.element.tab.GuiRedstoneControlTab;
import mekanism.client.gui.element.tab.GuiSecurityTab;
import mekanism.client.gui.element.tab.GuiUpgradeTab;
import mekanism.client.gui.element.text.BackgroundType;
import mekanism.client.gui.element.text.GuiTextField;
import mekanism.client.gui.element.text.InputValidator;
import mekanism.common.Mekanism;
import mekanism.common.MekanismLang;
import mekanism.common.content.teleporter.TeleporterFrequency;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.lib.frequency.Frequency;
import mekanism.common.lib.frequency.Frequency.FrequencyIdentity;
import mekanism.common.lib.frequency.FrequencyManager;
import mekanism.common.lib.frequency.FrequencyType;
import mekanism.common.network.PacketGuiSetFrequency;
import mekanism.common.network.PacketTeleporterSetColor;
import mekanism.common.network.PacketGuiSetFrequency.FrequencyUpdate;
import mekanism.common.tile.TileEntityTeleporter;
import mekanism.common.util.text.OwnerDisplay;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class GuiTeleporter extends GuiMekanismTile<TileEntityTeleporter, MekanismTileContainer<TileEntityTeleporter>> {

    private MekanismButton publicButton;
    private MekanismButton privateButton;
    private MekanismButton setButton;
    private MekanismButton deleteButton;
    private GuiTextScrollList scrollList;
    private GuiTextField frequencyField;
    private boolean privateMode;

    private boolean init = false;

    public GuiTeleporter(MekanismTileContainer<TileEntityTeleporter> container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);
        if (tile.getFrequency(FrequencyType.TELEPORTER) != null) {
            privateMode = tile.getFrequency(FrequencyType.TELEPORTER).isPrivate();
        }
        ySize += 64;
        dynamicSlots = true;
    }

    @Override
    public void func_231160_c_() {
        super.func_231160_c_();
        func_230480_a_(new GuiTeleporterStatus(this, () -> tile.getFrequency(FrequencyType.TELEPORTER) != null, () -> tile.status));
        func_230480_a_(new GuiRedstoneControlTab(this, tile));
        func_230480_a_(new GuiUpgradeTab(this, tile));
        func_230480_a_(new GuiSecurityTab<>(this, tile));
        func_230480_a_(new GuiVerticalPowerBar(this, tile.getEnergyContainer(), 158, 26));
        func_230480_a_(scrollList = new GuiTextScrollList(this, 27, 36, 122, 42));

        func_230480_a_(publicButton = new TranslationButton(this, getGuiLeft() + 27, getGuiTop() + 14, 60, 20, MekanismLang.PUBLIC, () -> {
            privateMode = false;
            updateButtons();
        }));
        func_230480_a_(privateButton = new TranslationButton(this, getGuiLeft() + 89, getGuiTop() + 14, 60, 20, MekanismLang.PRIVATE, () -> {
            privateMode = true;
            updateButtons();
        }));
        func_230480_a_(setButton = new TranslationButton(this, getGuiLeft() + 27, getGuiTop() + 120, 50, 18, MekanismLang.BUTTON_SET, () -> {
            int selection = scrollList.getSelection();
            if (selection != -1) {
                Frequency freq = privateMode ? tile.getPrivateCache(FrequencyType.TELEPORTER).get(selection) : tile.getPublicCache(FrequencyType.TELEPORTER).get(selection);
                setFrequency(freq.getName());
            }
            updateButtons();
        }));
        func_230480_a_(deleteButton = new TranslationButton(this, getGuiLeft() + 79, getGuiTop() + 120, 50, 18, MekanismLang.BUTTON_DELETE, () -> {
            int selection = scrollList.getSelection();
            if (selection != -1) {
                Frequency freq = privateMode ? tile.getPrivateCache(FrequencyType.TELEPORTER).get(selection) : tile.getPublicCache(FrequencyType.TELEPORTER).get(selection);
                Mekanism.packetHandler.sendToServer(PacketGuiSetFrequency.create(FrequencyUpdate.REMOVE_TILE, FrequencyType.TELEPORTER, freq.getIdentity(), tile.getPos()));
                scrollList.clearSelection();
            }
            updateButtons();
        }));
        func_230480_a_(new GuiSlot(SlotType.NORMAL, this, 131, 120).setRenderAboveSlots());
        func_230480_a_(new ColorButton(this, getGuiLeft() + 132, getGuiTop() + 121, 16, 16,
              () -> getFrequency() == null ? null : getFrequency().getColor(),
              () -> sendColorUpdate(0),
              () -> sendColorUpdate(1)));
        func_230480_a_(frequencyField = new GuiTextField(this, 50, 103, 98, 11));
        frequencyField.setMaxStringLength(FrequencyManager.MAX_FREQ_LENGTH);
        frequencyField.setBackground(BackgroundType.INNER_SCREEN);
        frequencyField.setEnterHandler(this::setFrequency);
        frequencyField.setInputValidator(InputValidator.or(InputValidator.DIGIT, InputValidator.LETTER, InputValidator.FREQUENCY_CHARS));
        frequencyField.addCheckmarkButton(this::setFrequency);
        updateButtons();
    }

    public ITextComponent getSecurity(Frequency freq) {
        if (freq.isPublic()) {
            return MekanismLang.PUBLIC.translate();
        }
        return MekanismLang.PRIVATE.translateColored(EnumColor.DARK_RED);
    }

    private void updateButtons() {
        if (getOwner() == null) {
            return;
        }
        List<String> text = new ArrayList<>();
        if (privateMode) {
            for (Frequency freq : tile.getPrivateCache(FrequencyType.TELEPORTER)) {
                text.add(freq.getName());
            }
        } else {
            for (Frequency freq : tile.getPublicCache(FrequencyType.TELEPORTER)) {
                text.add(MekanismLang.GENERIC_WITH_PARENTHESIS.translate(freq.getName(), freq.getClientOwner()).getString());
            }
        }
        scrollList.setText(text);
        if (privateMode) {
            publicButton.field_230693_o_ = true;
            privateButton.field_230693_o_ = false;
        } else {
            publicButton.field_230693_o_ = false;
            privateButton.field_230693_o_ = true;
        }
        if (scrollList.hasSelection()) {
            Frequency freq = privateMode ? tile.getPrivateCache(FrequencyType.TELEPORTER).get(scrollList.getSelection()) :
                             tile.getPublicCache(FrequencyType.TELEPORTER).get(scrollList.getSelection());
            setButton.field_230693_o_ = tile.getFrequency(FrequencyType.TELEPORTER) == null || !tile.getFrequency(FrequencyType.TELEPORTER).equals(freq);
            deleteButton.field_230693_o_ = getOwner().equals(freq.getOwner());
        } else {
            setButton.field_230693_o_ = false;
            deleteButton.field_230693_o_ = false;
        }
    }

    @Override
    public void func_231023_e_() {
        super.func_231023_e_();
        if (!init && getFrequency() != null) {
            init = true;
            privateMode = getFrequency().isPrivate();
        }
        //TODO: Why do we call updateButtons every tick?
        updateButtons();
    }

    @Override
    public boolean func_231044_a_(double mouseX, double mouseY, int button) {
        updateButtons();
        return super.func_231044_a_(mouseX, mouseY, button);
    }

    private void setFrequency() {
        setFrequency(frequencyField.getText());
        frequencyField.setText("");
        updateButtons();
    }

    @Override
    protected void drawForegroundText(@Nonnull MatrixStack matrix, int mouseX, int mouseY) {
        renderTitleText(matrix, 4);
        drawString(matrix, OwnerDisplay.of(getOwner(), tile.getSecurity().getClientOwner()).getTextComponent(), 8, getYSize() - 92, titleTextColor());
        ITextComponent frequencyComponent = MekanismLang.FREQUENCY.translate();
        drawString(matrix, frequencyComponent, 32, 81, titleTextColor());
        ITextComponent securityComponent = MekanismLang.SECURITY.translate("");
        drawString(matrix, securityComponent, 32, 91, titleTextColor());
        int frequencyOffset = getStringWidth(frequencyComponent) + 1;
        Frequency freq = tile.getFrequency(FrequencyType.TELEPORTER);
        if (freq != null) {
            drawTextScaledBound(matrix, freq.getName(), 32 + frequencyOffset, 81, 0x797979, xSize - 32 - frequencyOffset - 4);
            drawString(matrix, getSecurity(freq), 32 + getStringWidth(securityComponent), 91, 0x797979);
        } else {
            drawString(matrix, MekanismLang.NONE.translateColored(EnumColor.DARK_RED), 32 + frequencyOffset, 81, 0x797979);
            drawString(matrix, MekanismLang.NONE.translateColored(EnumColor.DARK_RED), 32 + getStringWidth(securityComponent), 91, 0x797979);
        }
        drawTextScaledBound(matrix, MekanismLang.SET.translate(), 27, 104, titleTextColor(), 20);
        super.drawForegroundText(matrix, mouseX, mouseY);
    }

    private UUID getOwner() {
        return tile.getSecurity().getOwnerUUID();
    }

    public void setFrequency(String freq) {
        if (!freq.isEmpty()) {
            Mekanism.packetHandler.sendToServer(PacketGuiSetFrequency.create(FrequencyUpdate.SET_TILE, FrequencyType.TELEPORTER, new FrequencyIdentity(freq, !privateMode), tile.getPos()));
        }
    }

    public TeleporterFrequency getFrequency() {
        return tile.getFrequency(FrequencyType.TELEPORTER);
    }

    private void sendColorUpdate(int extra) {
        TeleporterFrequency freq = getFrequency();
        if (freq != null) {
            Mekanism.packetHandler.sendToServer(PacketTeleporterSetColor.create(tile.getTilePos(), freq, extra));
        }
    }
}