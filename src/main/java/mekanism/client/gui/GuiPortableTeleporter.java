package mekanism.client.gui;

import mekanism.common.Mekanism;
import mekanism.common.item.ItemPortableTeleporter;
import mekanism.common.network.PacketDigitUpdate;
import mekanism.common.network.PacketPortableTeleport;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiPortableTeleporter extends GuiScreen
{
	public EntityPlayer entityPlayer;
	public ItemStack itemStack;

	public int xSize = 176;
	public int ySize = 166;

	public GuiPortableTeleporter(EntityPlayer player, ItemStack itemstack)
	{
		entityPlayer = player;
		itemStack = itemstack;
	}

	@Override
	public void initGui()
	{
		buttonList.clear();
		buttonList.add(new GuiButton(0, 173, 105, 80, 20, MekanismUtils.localize("gui.teleport")));
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTick)
	{
		if(mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemPortableTeleporter)
		{
			itemStack = mc.thePlayer.getCurrentEquippedItem();
		}

		mc.renderEngine.bindTexture(MekanismUtils.getResource(ResourceType.GUI, "GuiPortableTeleporter.png"));
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		int guiWidth = (width - xSize) / 2;
		int guiHeight = (height - ySize) / 2;
		drawTexturedModalRect(guiWidth, guiHeight, 0, 0, xSize, ySize);

		int displayInt;

		displayInt = getYAxisForNumber(((ItemPortableTeleporter)itemStack.getItem()).getDigit(itemStack, 0));
		drawTexturedModalRect(guiWidth + 23, guiHeight + 44, 176, displayInt, 13, 13);

		displayInt = getYAxisForNumber(((ItemPortableTeleporter)itemStack.getItem()).getDigit(itemStack, 1));
		drawTexturedModalRect(guiWidth + 62, guiHeight + 44, 176, displayInt, 13, 13);

		displayInt = getYAxisForNumber(((ItemPortableTeleporter)itemStack.getItem()).getDigit(itemStack, 2));
		drawTexturedModalRect(guiWidth + 101, guiHeight + 44, 176, displayInt, 13, 13);

		displayInt = getYAxisForNumber(((ItemPortableTeleporter)itemStack.getItem()).getDigit(itemStack, 3));
		drawTexturedModalRect(guiWidth + 140, guiHeight + 44, 176, displayInt, 13, 13);

		ItemPortableTeleporter item = (ItemPortableTeleporter)itemStack.getItem();

		((GuiButton)buttonList.get(0)).xPosition = guiWidth+48;
		((GuiButton)buttonList.get(0)).yPosition = guiHeight+68;

		fontRendererObj.drawString(MekanismUtils.localize("gui.portableTeleporter"), guiWidth+39, guiHeight+6, 0x404040);
		fontRendererObj.drawString(item.getStatusAsString(item.getStatus(itemStack)), guiWidth+53, guiHeight+19, 0x00CD00);

		super.drawScreen(mouseX, mouseY, partialTick);
	}

	@Override
	protected void actionPerformed(GuiButton guibutton)
	{
		if(guibutton.id == 0)
		{
			Mekanism.packetPipeline.sendToServer(new PacketPortableTeleport());
			mc.setIngameFocus();
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button)
	{
		super.mouseClicked(mouseX, mouseY, button);

		int xAxis = (mouseX - (width - xSize) / 2);
		int yAxis = (mouseY - (height - ySize) / 2);

		if(xAxis > 23 && xAxis < 37 && yAxis > 44 && yAxis < 58)
		{
			Mekanism.packetPipeline.sendToServer(new PacketDigitUpdate(0, getIncrementedNumber(((ItemPortableTeleporter)itemStack.getItem()).getDigit(itemStack, 0))));
			((ItemPortableTeleporter)itemStack.getItem()).setDigit(itemStack, 0, getIncrementedNumber(((ItemPortableTeleporter)itemStack.getItem()).getDigit(itemStack, 0)));
	        Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
		}
		else if(xAxis > 62 && xAxis < 76 && yAxis > 44 && yAxis < 58)
		{
			Mekanism.packetPipeline.sendToServer(new PacketDigitUpdate(1, getIncrementedNumber(((ItemPortableTeleporter)itemStack.getItem()).getDigit(itemStack, 1))));
			((ItemPortableTeleporter)itemStack.getItem()).setDigit(itemStack, 1, getIncrementedNumber(((ItemPortableTeleporter)itemStack.getItem()).getDigit(itemStack, 1)));
	        Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
		}
		else if(xAxis > 101 && xAxis < 115 && yAxis > 44 && yAxis < 58)
		{
			Mekanism.packetPipeline.sendToServer(new PacketDigitUpdate(2, getIncrementedNumber(((ItemPortableTeleporter)itemStack.getItem()).getDigit(itemStack, 2))));
			((ItemPortableTeleporter)itemStack.getItem()).setDigit(itemStack, 2, getIncrementedNumber(((ItemPortableTeleporter)itemStack.getItem()).getDigit(itemStack, 2)));
	        Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
		}
		else if(xAxis > 140 && xAxis < 154 && yAxis > 44 && yAxis < 58)
		{
			Mekanism.packetPipeline.sendToServer(new PacketDigitUpdate(3, getIncrementedNumber(((ItemPortableTeleporter)itemStack.getItem()).getDigit(itemStack, 3))));
			((ItemPortableTeleporter)itemStack.getItem()).setDigit(itemStack, 3, getIncrementedNumber(((ItemPortableTeleporter)itemStack.getItem()).getDigit(itemStack, 3)));
	        Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
		}
	}

	public int getIncrementedNumber(int i)
	{
		if(i < 9) i++;
		else if(i == 9) i=0;

		return i;
	}

	public int getYAxisForNumber(int i)
	{
		return i*13;
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}
}
