package mekanism.client.gui;

import java.util.ArrayList;

import mekanism.api.Coord4D;
import mekanism.client.gui.GuiSlot.SlotOverlay;
import mekanism.client.gui.GuiSlot.SlotType;
import mekanism.common.Mekanism;
import mekanism.common.inventory.container.ContainerTeleporter;
import mekanism.common.network.PacketTileEntity;
import mekanism.common.tile.TileEntityTeleporter;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiTeleporter extends GuiMekanism
{
	public TileEntityTeleporter tileEntity;

	public GuiTeleporter(InventoryPlayer inventory, TileEntityTeleporter tentity)
	{
		super(new ContainerTeleporter(inventory, tentity));
		tileEntity = tentity;

		guiElements.add(new GuiPowerBar(this, tileEntity, MekanismUtils.getResource(ResourceType.GUI, "GuiTeleporter.png"), 164, 15));
		guiElements.add(new GuiSlot(SlotType.NORMAL, this, MekanismUtils.getResource(ResourceType.GUI, "GuiTeleporter.png"), 26, 13).with(SlotOverlay.POWER));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		int xAxis = (mouseX - (width - xSize) / 2);
		int yAxis = (mouseY - (height - ySize) / 2);

		fontRendererObj.drawString(tileEntity.getInventoryName(), 45, 6, 0x404040);
		fontRendererObj.drawString(MekanismUtils.localize("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
		fontRendererObj.drawString(tileEntity.status, 66, 19, 0x00CD00);

		if(xAxis >= 165 && xAxis <= 169 && yAxis >= 17 && yAxis <= 69)
		{
			drawCreativeTabHoveringText(MekanismUtils.getEnergyDisplay(tileEntity.getEnergy()), xAxis, yAxis);
		}

		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
	}

	@Override
	protected void mouseClicked(int x, int y, int button)
	{
		super.mouseClicked(x, y, button);

		int xAxis = (x - (width - xSize) / 2);
		int yAxis = (y - (height - ySize) / 2);

		ArrayList data = new ArrayList();

		if(xAxis > 23 && xAxis < 37 && yAxis > 44 && yAxis < 58)
		{
			data.add(0);
			data.add(getIncrementedNumber(tileEntity.code.digitOne));

			Mekanism.packetPipeline.sendToServer(new PacketTileEntity(Coord4D.get(tileEntity), data));
			tileEntity.code.digitOne = getIncrementedNumber(tileEntity.code.digitOne);
            playClickSound();
		}
		else if(xAxis > 62 && xAxis < 76 && yAxis > 44 && yAxis < 58)
		{
			data.add(1);
			data.add(getIncrementedNumber(tileEntity.code.digitTwo));

			Mekanism.packetPipeline.sendToServer(new PacketTileEntity(Coord4D.get(tileEntity), data));
			tileEntity.code.digitTwo = getIncrementedNumber(tileEntity.code.digitTwo);
            playClickSound();
		}
		else if(xAxis > 101 && xAxis < 115 && yAxis > 44 && yAxis < 58)
		{
			data.add(2);
			data.add(getIncrementedNumber(tileEntity.code.digitThree));

			Mekanism.packetPipeline.sendToServer(new PacketTileEntity(Coord4D.get(tileEntity), data));
			tileEntity.code.digitThree = getIncrementedNumber(tileEntity.code.digitThree);
            playClickSound();
		}
		else if(xAxis > 140 && xAxis < 154 && yAxis > 44 && yAxis < 58)
		{
			data.add(3);
			data.add(getIncrementedNumber(tileEntity.code.digitFour));

			Mekanism.packetPipeline.sendToServer(new PacketTileEntity(Coord4D.get(tileEntity), data));
			tileEntity.code.digitFour = getIncrementedNumber(tileEntity.code.digitFour);
            playClickSound();
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY)
	{
		mc.renderEngine.bindTexture(MekanismUtils.getResource(ResourceType.GUI, "GuiTeleporter.png"));
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		int guiWidth = (width - xSize) / 2;
		int guiHeight = (height - ySize) / 2;
		drawTexturedModalRect(guiWidth, guiHeight, 0, 0, xSize, ySize);
		int displayInt;

		displayInt = tileEntity.getScaledEnergyLevel(52);
		drawTexturedModalRect(guiWidth + 165, guiHeight + 17 + 52 - displayInt, 176 + 13, 52 - displayInt, 4, displayInt);

		displayInt = getYAxisForNumber(tileEntity.code.digitOne);
		drawTexturedModalRect(guiWidth + 23, guiHeight + 44, 176, displayInt, 13, 13);

		displayInt = getYAxisForNumber(tileEntity.code.digitTwo);
		drawTexturedModalRect(guiWidth + 62, guiHeight + 44, 176, displayInt, 13, 13);

		displayInt = getYAxisForNumber(tileEntity.code.digitThree);
		drawTexturedModalRect(guiWidth + 101, guiHeight + 44, 176, displayInt, 13, 13);

		displayInt = getYAxisForNumber(tileEntity.code.digitFour);
		drawTexturedModalRect(guiWidth + 140, guiHeight + 44, 176, displayInt, 13, 13);

		super.drawGuiContainerBackgroundLayer(partialTick, mouseX, mouseY);
	}

	public int getIncrementedNumber(int i)
	{
		if(i < 9)
		{
			i++;
		}
		else if(i == 9)
		{
			i = 0;
		}

		return i;
	}

	public int getYAxisForNumber(int i)
	{
		return i*13;
	}
}
