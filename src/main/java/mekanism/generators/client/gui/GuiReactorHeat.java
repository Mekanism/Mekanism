package mekanism.generators.client.gui;

import java.util.List;

import mekanism.api.util.ListUtils;
import mekanism.api.energy.IStrictEnergyStorage;
import mekanism.client.gui.GuiEnergyGauge;
import mekanism.client.gui.GuiEnergyGauge.IEnergyInfoHandler;
import mekanism.client.gui.GuiEnergyInfo;
import mekanism.client.gui.GuiEnergyInfo.IInfoHandler;
import mekanism.client.gui.GuiFluidGauge;
import mekanism.client.gui.GuiFluidGauge.IFluidInfoHandler;
import mekanism.client.gui.GuiGauge.Type;
import mekanism.client.gui.GuiMekanism;
import mekanism.client.gui.GuiNumberGauge;
import mekanism.client.gui.GuiNumberGauge.INumberInfoHandler;
import mekanism.client.gui.GuiProgress;
import mekanism.client.gui.GuiProgress.IProgressInfoHandler;
import mekanism.client.gui.GuiProgress.ProgressBar;
import mekanism.common.inventory.container.ContainerNull;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import mekanism.generators.common.tile.reactor.TileEntityReactorController;

import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.FluidTank;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiReactorHeat extends GuiMekanism
{
	public TileEntityReactorController tileEntity;

	public GuiReactorHeat(InventoryPlayer inventory, final TileEntityReactorController tentity)
	{
		super(new ContainerNull(inventory.player, tentity));
		tileEntity = tentity;
		guiElements.add(new GuiEnergyInfo(new IInfoHandler()
		{
			@Override
			public List<String> getInfo()
			{
				return ListUtils.asList(
						"Storing: " + MekanismUtils.getEnergyDisplay(tileEntity.getEnergy()),
						"Max Output: " + MekanismUtils.getEnergyDisplay(tileEntity.getMaxOutput()) + "/t");
			}
		}, this, MekanismUtils.getResource(ResourceType.GUI, "GuiTall.png")));
		guiElements.add(new GuiNumberGauge(new INumberInfoHandler()
		{
			@Override
			public IIcon getIcon()
			{
				return BlockStaticLiquid.getLiquidIcon("lava_still");
			}

			@Override
			public double getLevel()
			{
				return tileEntity.getPlasmaTemp();
			}

			@Override
			public double getMaxLevel()
			{
				return 5E8;
			}

			@Override
			public String getText(double level)
			{
				return "Plasma: " + (int)(level+23) + "C";
			}
		}, Type.STANDARD, this, MekanismUtils.getResource(ResourceType.GUI, "GuiTall.png"), 7, 50));
		guiElements.add(new GuiProgress(new IProgressInfoHandler()
		{
			@Override
			public double getProgress()
			{
				return tileEntity.getPlasmaTemp() > tileEntity.getCaseTemp() ? 1 : 0;
			}
		}, ProgressBar.SMALL_RIGHT, this, MekanismUtils.getResource(ResourceType.GUI, "GuiTall.png"), 27, 75));
		guiElements.add(new GuiNumberGauge(new INumberInfoHandler()
		{
			@Override
			public IIcon getIcon()
			{
				return BlockStaticLiquid.getLiquidIcon("lava_still");
			}

			@Override
			public double getLevel()
			{
				return tileEntity.getCaseTemp();
			}

			@Override
			public double getMaxLevel()
			{
				return 5E8;
			}

			@Override
			public String getText(double level)
			{
				return "Case: " + (int)(level+23) + "C";
			}
		}, Type.STANDARD, this, MekanismUtils.getResource(ResourceType.GUI, "GuiTall.png"), 61, 50));
		guiElements.add(new GuiProgress(new IProgressInfoHandler()
		{
			@Override
			public double getProgress()
			{
				return tileEntity.getCaseTemp() > 0 ? 1 : 0;
			}
		}, ProgressBar.SMALL_RIGHT, this, MekanismUtils.getResource(ResourceType.GUI, "GuiTall.png"), 81, 60));
		guiElements.add(new GuiProgress(new IProgressInfoHandler()
		{
			@Override
			public double getProgress()
			{
				return (tileEntity.getCaseTemp() > 0 && tileEntity.waterTank.getFluidAmount() > 0 && tileEntity.steamTank.getFluidAmount() < tileEntity.steamTank.getCapacity()) ? 1 : 0;
			}
		}, ProgressBar.SMALL_RIGHT, this, MekanismUtils.getResource(ResourceType.GUI, "GuiTall.png"), 81, 90));
		guiElements.add(new GuiFluidGauge(new IFluidInfoHandler()
		{
			@Override
			public FluidTank getTank()
			{
				return tentity.waterTank;
			}
		}, Type.SMALL, this, MekanismUtils.getResource(ResourceType.GUI, "GuiTall.png"), 115, 84));
		guiElements.add(new GuiFluidGauge(new IFluidInfoHandler()
		{
			@Override
			public FluidTank getTank()
			{
				return tentity.steamTank;
			}
		}, Type.SMALL, this, MekanismUtils.getResource(ResourceType.GUI, "GuiTall.png"), 151, 84));
		guiElements.add(new GuiEnergyGauge(new IEnergyInfoHandler()
		{
			@Override
			public IStrictEnergyStorage getEnergyStorage()
			{
				return tileEntity;
			}
		}, Type.SMALL, this, MekanismUtils.getResource(ResourceType.GUI, "GuiTall.png"), 115, 46));
		guiElements.add(new GuiFuelTab(this, tileEntity, MekanismUtils.getResource(ResourceType.GUI, "GuiTall.png")));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);

		fontRendererObj.drawString(tileEntity.getInventoryName(), 6, 6, 0x404040);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY)
	{
		mc.renderEngine.bindTexture(MekanismUtils.getResource(ResourceType.GUI, "GuiTall.png"));
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		int guiWidth = (width - xSize) / 2;
		int guiHeight = (height - ySize) / 2;
		drawTexturedModalRect(guiWidth, guiHeight, 0, 0, xSize, ySize);

		super.drawGuiContainerBackgroundLayer(partialTick, mouseX, mouseY);
	}
}
