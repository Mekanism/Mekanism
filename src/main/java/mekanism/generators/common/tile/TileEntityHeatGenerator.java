package mekanism.generators.common.tile;

import java.util.ArrayList;

import mekanism.common.util.ChargeUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.generators.common.MekanismGenerators;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import com.google.common.io.ByteArrayDataInput;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.peripheral.IComputerAccess;

public class TileEntityHeatGenerator extends TileEntityGenerator implements IFluidHandler
{
	/** The FluidTank for this generator. */
	public FluidTank lavaTank = new FluidTank(24000);

	public TileEntityHeatGenerator()
	{
		super("HeatGenerator", 160000, MekanismGenerators.heatGeneration*2);
		inventory = new ItemStack[2];
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		if(!worldObj.isRemote)
		{
			ChargeUtils.charge(1, this);

			if(inventory[0] != null)
			{
				FluidStack fluid = FluidContainerRegistry.getFluidForFilledItem(inventory[0]);

				if(fluid != null && fluid.fluidID == FluidRegistry.LAVA.getID())
				{
					if(lavaTank.getFluid() == null || lavaTank.getFluid().amount+fluid.amount <= lavaTank.getCapacity())
					{
						lavaTank.fill(fluid, true);

						if(inventory[0].getItem().getContainerItem(inventory[0]) != null)
						{
							inventory[0] = inventory[0].getItem().getContainerItem(inventory[0]);
						}
						else {
							inventory[0].stackSize--;
						}

						if(inventory[0].stackSize == 0)
						{
							inventory[0] = null;
						}
					}
				}
				else {
					int fuel = getFuel(inventory[0]);

					if(fuel > 0)
					{
						int fuelNeeded = lavaTank.getCapacity() - (lavaTank.getFluid() != null ? lavaTank.getFluid().amount : 0);

						if(fuel <= fuelNeeded)
						{
							lavaTank.fill(new FluidStack(FluidRegistry.LAVA, fuel), true);

							if(inventory[0].getItem().getContainerItem(inventory[0]) != null)
							{
								inventory[0] = inventory[0].getItem().getContainerItem(inventory[0]);
							}
							else {
								inventory[0].stackSize--;
							}

							if(inventory[0].stackSize == 0)
							{
								inventory[0] = null;
							}
						}
					}
				}
			}

			setEnergy(electricityStored + getBoost());

			if(canOperate())
			{
				setActive(true);

				lavaTank.drain(10, true);
				setEnergy(electricityStored + MekanismGenerators.heatGeneration);
			}
			else {
				setActive(false);
			}
		}
	}

	@Override
	public boolean isItemValidForSlot(int slotID, ItemStack itemstack)
	{
		if(slotID == 0)
		{
			return getFuel(itemstack) > 0 || (FluidContainerRegistry.getFluidForFilledItem(itemstack) != null && FluidContainerRegistry.getFluidForFilledItem(itemstack).fluidID == FluidRegistry.LAVA.getID());
		}
		else if(slotID == 1)
		{
			return ChargeUtils.canBeCharged(itemstack);
		}

		return true;
	}

	@Override
	public boolean canOperate()
	{
		return electricityStored < MAX_ELECTRICITY && lavaTank.getFluid() != null && lavaTank.getFluid().amount >= 10 && MekanismUtils.canFunction(this);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbtTags)
	{
		super.readFromNBT(nbtTags);

		if(nbtTags.hasKey("lavaTank"))
		{
			lavaTank.readFromNBT(nbtTags.getCompoundTag("lavaTank"));
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbtTags)
	{
		super.writeToNBT(nbtTags);

		if(lavaTank.getFluid() != null)
		{
			nbtTags.setTag("lavaTank", lavaTank.writeToNBT(new NBTTagCompound()));
		}
	}

	@Override
	public boolean canExtractItem(int slotID, ItemStack itemstack, int side)
	{
		if(slotID == 1)
		{
			return ChargeUtils.canBeOutputted(itemstack, true);
		}
		else if(slotID == 0)
		{
			return FluidContainerRegistry.isEmptyContainer(itemstack);
		}

		return false;
	}

	public double getBoost()
	{
		int boost = 0;

		if(isLava(xCoord+1, yCoord, zCoord))
			boost+=5;
		if(isLava(xCoord-1, yCoord, zCoord))
			boost+=5;
		if(isLava(xCoord, yCoord+1, zCoord))
			boost+=5;
		if(isLava(xCoord, yCoord-1, zCoord))
			boost+=5;
		if(isLava(xCoord, yCoord, zCoord+1))
			boost+=5;
		if(isLava(xCoord, yCoord, zCoord-1))
			boost+=5;
		if(worldObj.provider.dimensionId == -1)
			boost+=100;

		return boost;
	}
	
	private boolean isLava(int x, int y, int z)
	{
		return worldObj.getBlock(x, y, z) == Blocks.lava;
	}

	public int getFuel(ItemStack itemstack)
	{
		if(itemstack.getItem() == Items.lava_bucket)
		{
			return 1000;
		}

		return TileEntityFurnace.getItemBurnTime(itemstack)/20;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side)
	{
		return ForgeDirection.getOrientation(side) == MekanismUtils.getRight(facing) ? new int[] {1} : new int[] {0};
	}

	/**
	 * Gets the scaled fuel level for the GUI.
	 * @param i - multiplier
	 * @return
	 */
	public int getScaledFuelLevel(int i)
	{
		return (lavaTank.getFluid() != null ? lavaTank.getFluid().amount : 0)*i / lavaTank.getCapacity();
	}

	@Override
	public void handlePacketData(ByteArrayDataInput dataStream)
	{
		super.handlePacketData(dataStream);

		int amount = dataStream.readInt();

		if(amount != 0)
		{
			lavaTank.setFluid(new FluidStack(FluidRegistry.LAVA, amount));
		}
		else {
			lavaTank.setFluid(null);
		}
	}

	@Override
	public ArrayList getNetworkedData(ArrayList data)
	{
		super.getNetworkedData(data);

		if(lavaTank.getFluid() != null)
		{
			data.add(lavaTank.getFluid().amount);
		}
		else {
			data.add(0);
		}

		return data;
	}

	@Override
	public String[] getMethodNames()
	{
		return new String[] {"getStored", "getOutput", "getMaxEnergy", "getEnergyNeeded", "getFuel", "getFuelNeeded"};
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws Exception
	{
		switch(method)
		{
			case 0:
				return new Object[] {electricityStored};
			case 1:
				return new Object[] {output};
			case 2:
				return new Object[] {MAX_ELECTRICITY};
			case 3:
				return new Object[] {(MAX_ELECTRICITY-electricityStored)};
			case 4:
				return new Object[] {lavaTank.getFluid() != null ? lavaTank.getFluid().amount : 0};
			case 5:
				return new Object[] {lavaTank.getCapacity()-(lavaTank.getFluid() != null ? lavaTank.getFluid().amount : 0)};
			default:
				System.err.println("[Mekanism] Attempted to call unknown method with computer ID " + computer.getID());
				return null;
		}
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
	{
		if(resource.fluidID == FluidRegistry.LAVA.getID() && from != ForgeDirection.getOrientation(facing))
		{
			return lavaTank.fill(resource, doFill);
		}

		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
	{
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
	{
		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid)
	{
		return fluid == FluidRegistry.LAVA;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid)
	{
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from)
	{
		return new FluidTankInfo[] {lavaTank.getInfo()};
	}
}
