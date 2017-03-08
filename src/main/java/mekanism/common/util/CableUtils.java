package mekanism.common.util;

import cofh.api.energy.IEnergyConnection;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import mekanism.api.Coord4D;
import mekanism.api.MekanismConfig.general;
import mekanism.api.energy.ICableOutputter;
import mekanism.api.energy.IStrictEnergyAcceptor;
import mekanism.api.transmitters.TransmissionType;
import mekanism.api.util.CapabilityUtils;
import mekanism.common.base.IEnergyWrapper;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.integration.IC2Integration;
import net.darkhax.tesla.api.ITeslaConsumer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class CableUtils
{
	public static boolean isCable(TileEntity tileEntity)
	{
		if(tileEntity != null && CapabilityUtils.hasCapability(tileEntity, Capabilities.GRID_TRANSMITTER_CAPABILITY, null))
		{
			return TransmissionType.checkTransmissionType(CapabilityUtils.getCapability(tileEntity, Capabilities.GRID_TRANSMITTER_CAPABILITY, null), TransmissionType.ENERGY);
		}
		
		return false;
	}

	/**
	 * Gets the adjacent connections to a TileEntity, from a subset of its sides.
	 * @param tileEntity - center TileEntity
	 * @param sides - set of sides to check
	 * @return boolean[] of adjacent connections
	 */
	public static boolean[] getConnections(TileEntity tileEntity, Set<EnumFacing> sides)
	{
		boolean[] connectable = new boolean[] {false, false, false, false, false, false};
		Coord4D coord = Coord4D.get(tileEntity);

		for(EnumFacing side : sides)
		{
			TileEntity tile = coord.offset(side).getTileEntity(tileEntity.getWorld());

			connectable[side.ordinal()] = isValidAcceptorOnSide(tileEntity, tile, side);
			connectable[side.ordinal()] |= isCable(tile);
		}

		return connectable;
	}

	/**
	 * Gets the adjacent connections to a TileEntity, from a subset of its sides.
	 * @param cableEntity - TileEntity that's trying to connect
	 * @param side - side to check
	 * @return boolean whether the acceptor is valid
	 */
	public static boolean isValidAcceptorOnSide(TileEntity cableEntity, TileEntity tile, EnumFacing side)
	{
		if(tile == null || isCable(tile))
		{
			return false;
		}
		
		return isAcceptor(cableEntity, tile, side) || isOutputter(tile, side) || 
				(MekanismUtils.useRF() && tile instanceof IEnergyConnection && ((IEnergyConnection)tile).canConnectEnergy(side.getOpposite()));
	}

	/**
	 * Gets all the connected cables around a specific tile entity.
	 * @param tileEntity - center tile entity
	 * @return TileEntity[] of connected cables
	 */
	public static TileEntity[] getConnectedOutputters(TileEntity tileEntity)
	{
		return getConnectedOutputters(tileEntity.getPos(), tileEntity.getWorld());
	}

	public static TileEntity[] getConnectedOutputters(BlockPos pos, World world)
	{
		TileEntity[] outputters = new TileEntity[] {null, null, null, null, null, null};

		for(EnumFacing orientation : EnumFacing.VALUES)
		{
			TileEntity outputter = world.getTileEntity(pos.offset(orientation));

			if(isOutputter(outputter, orientation))
			{
				outputters[orientation.ordinal()] = outputter;
			}
		}

		return outputters;
	}

	public static boolean isOutputter(TileEntity tileEntity, EnumFacing side)
	{
		if(tileEntity == null)
		{
			return false;
		}
		
		if(CapabilityUtils.hasCapability(tileEntity, Capabilities.CABLE_OUTPUTTER_CAPABILITY, side.getOpposite()))
		{
			ICableOutputter outputter = CapabilityUtils.getCapability(tileEntity, Capabilities.CABLE_OUTPUTTER_CAPABILITY, side.getOpposite());
			
			if(outputter != null && outputter.canOutputTo(side.getOpposite()))
			{
				return true;
			}
		}
		
		if(MekanismUtils.useTesla() && CapabilityUtils.hasCapability(tileEntity, Capabilities.TESLA_PRODUCER_CAPABILITY, side.getOpposite()))
		{
			return true;
		}
		
		if(MekanismUtils.useRF() && tileEntity instanceof IEnergyProvider && ((IEnergyConnection)tileEntity).canConnectEnergy(side.getOpposite()))
		{
			return true;
		}
		
		if(MekanismUtils.useIC2() && IC2Integration.isOutputter(tileEntity, side))
		{
			return true;
		}
		
		return false;
	}

	public static boolean isAcceptor(TileEntity orig, TileEntity tileEntity, EnumFacing side)
	{
		if(CapabilityUtils.hasCapability(tileEntity, Capabilities.GRID_TRANSMITTER_CAPABILITY, side.getOpposite()))
		{
			return false;
		}

		if(CapabilityUtils.hasCapability(tileEntity, Capabilities.ENERGY_ACCEPTOR_CAPABILITY, side.getOpposite()))
		{
			if(CapabilityUtils.getCapability(tileEntity, Capabilities.ENERGY_ACCEPTOR_CAPABILITY, side.getOpposite()).canReceiveEnergy(side.getOpposite()))
			{
				return true;
			}
		}
		else if(CapabilityUtils.hasCapability(tileEntity, Capabilities.TESLA_CONSUMER_CAPABILITY, side.getOpposite()))
		{
			return true;
		}
		else if(MekanismUtils.useIC2() && IC2Integration.isAcceptor(orig, tileEntity, side))
		{
			return true;
		}
		else if(MekanismUtils.useRF() && tileEntity instanceof IEnergyReceiver)
		{
			if(((IEnergyReceiver)tileEntity).canConnectEnergy(side.getOpposite()))
			{
				return true;
			}
		}

		return false;
	}

	public static void emit(IEnergyWrapper emitter)
	{
		if(!((TileEntity)emitter).getWorld().isRemote && MekanismUtils.canFunction((TileEntity)emitter))
		{
			double energyToSend = Math.min(emitter.getEnergy(), emitter.getMaxOutput());

			if(energyToSend > 0)
			{
				List<EnumFacing> outputtingSides = new ArrayList<EnumFacing>();
				boolean[] connectable = getConnections((TileEntity)emitter, emitter.getOutputtingSides());

				for(EnumFacing side : emitter.getOutputtingSides())
				{
					if(connectable[side.ordinal()])
					{
						outputtingSides.add(side);
					}
				}

				if(outputtingSides.size() > 0)
				{
					double sent = emit_do(emitter, outputtingSides, energyToSend);

					emitter.setEnergy(emitter.getEnergy() - sent);
				}
			}
		}
	}

	private static double emit_do(IEnergyWrapper emitter, List<EnumFacing> outputtingSides, double totalToSend)
	{
		double remains = totalToSend%outputtingSides.size();
		double splitSend = (totalToSend-remains)/outputtingSides.size();
		double sent = 0;

		for(EnumFacing side : outputtingSides)
		{
			TileEntity tileEntity = Coord4D.get((TileEntity)emitter).offset(side).getTileEntity(((TileEntity)emitter).getWorld());
			double toSend = splitSend+remains;
			sent += emit_do_do(emitter, tileEntity, side, toSend);
		}

		return sent;
	}

	private static double emit_do_do(IEnergyWrapper from, TileEntity tileEntity, EnumFacing side, double currentSending)
	{
		double sent = 0;

		if(CapabilityUtils.hasCapability(tileEntity, Capabilities.ENERGY_ACCEPTOR_CAPABILITY, side.getOpposite()))
		{
			IStrictEnergyAcceptor acceptor = CapabilityUtils.getCapability(tileEntity, Capabilities.ENERGY_ACCEPTOR_CAPABILITY, side.getOpposite());

			if(acceptor.canReceiveEnergy(side.getOpposite()))
			{
				sent += acceptor.transferEnergyToAcceptor(side.getOpposite(), currentSending, false);
			}
		}
		else if(CapabilityUtils.hasCapability(tileEntity, Capabilities.TESLA_CONSUMER_CAPABILITY, side.getOpposite()))
		{
			ITeslaConsumer consumer = CapabilityUtils.getCapability(tileEntity, Capabilities.TESLA_CONSUMER_CAPABILITY, side.getOpposite());
			sent += consumer.givePower((long)Math.round(currentSending*general.TO_TESLA), false) * general.FROM_TESLA;
		}
		else if(MekanismUtils.useRF() && tileEntity instanceof IEnergyReceiver)
		{
			IEnergyReceiver handler = (IEnergyReceiver)tileEntity;

			if(handler.canConnectEnergy(side.getOpposite()))
			{
				int toSend = Math.min((int)Math.round(currentSending*general.TO_RF), Integer.MAX_VALUE);
				int used = handler.receiveEnergy(side.getOpposite(), toSend, false);
				sent += used*general.FROM_RF;
			}
		}
		else if(MekanismUtils.useIC2())
		{
			sent += IC2Integration.emitEnergy(from, tileEntity, side, currentSending);
		}

		return sent;
	}
}
