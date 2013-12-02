package mekanism.common.util;

import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import mekanism.api.Object3D;
import mekanism.api.energy.ICableOutputter;
import mekanism.api.energy.IStrictEnergyAcceptor;
import mekanism.api.transmitters.ITransmitter;
import mekanism.api.transmitters.TransmissionType;
import mekanism.common.EnergyNetwork;
import mekanism.common.Mekanism;
import mekanism.common.tileentity.TileEntityElectricBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.block.IConductor;
import universalelectricity.core.block.IElectrical;
import universalelectricity.core.electricity.ElectricityHelper;
import universalelectricity.core.electricity.ElectricityPack;
import universalelectricity.core.grid.IElectricityNetwork;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.power.PowerHandler.Type;
import cofh.api.energy.IEnergyHandler;

public final class CableUtils
{
    /**
     * Gets all the connected energy acceptors, whether IC2-based or BuildCraft-based, surrounding a specific tile entity.
     * @param tileEntity - center tile entity
     * @return TileEntity[] of connected acceptors
     */
    public static TileEntity[] getConnectedEnergyAcceptors(TileEntity tileEntity)
    {
    	TileEntity[] acceptors = new TileEntity[] {null, null, null, null, null, null};
    	
    	for(ForgeDirection orientation : ForgeDirection.VALID_DIRECTIONS)
    	{
			TileEntity acceptor = Object3D.get(tileEntity).getFromSide(orientation).getTileEntity(tileEntity.worldObj);
			
			if(acceptor instanceof IStrictEnergyAcceptor || 
					acceptor instanceof IEnergySink || 
					(acceptor instanceof IPowerReceptor && !(acceptor instanceof ITransmitter) && MekanismUtils.useBuildcraft()) ||
					acceptor instanceof IElectrical || acceptor instanceof IEnergyHandler)
			{
				acceptors[orientation.ordinal()] = acceptor;
			}
    	}
    	
    	return acceptors;
    }
    
    /**
     * Gets all the connected cables around a specific tile entity.
     * @param tileEntity - center tile entity
     * @return TileEntity[] of connected cables
     */
    public static TileEntity[] getConnectedCables(TileEntity tileEntity)
    {
    	TileEntity[] cables = new TileEntity[] {null, null, null, null, null, null};
    	
    	for(ForgeDirection orientation : ForgeDirection.VALID_DIRECTIONS)
    	{
			TileEntity cable = Object3D.get(tileEntity).getFromSide(orientation).getTileEntity(tileEntity.worldObj);
			
			if(TransmissionType.checkTransmissionType(cable, TransmissionType.ENERGY))
			{
				cables[orientation.ordinal()] = cable;
			}
    	}
    	
    	return cables;
    }
    
    /**
     * Gets all the adjacent connections to a TileEntity.
     * @param tileEntity - center TileEntity
     * @return boolean[] of adjacent connections
     */
    public static boolean[] getConnections(TileEntity tileEntity)
    {
		boolean[] connectable = new boolean[] {false, false, false, false, false, false};
		
		TileEntity[] connectedAcceptors = getConnectedEnergyAcceptors(tileEntity);
		TileEntity[] connectedCables = getConnectedCables(tileEntity);
		TileEntity[] connectedOutputters = getConnectedOutputters(tileEntity);
		
		for(TileEntity tile : connectedAcceptors)
		{
			int side = Arrays.asList(connectedAcceptors).indexOf(tile);
			
			if(canConnectToAcceptor(ForgeDirection.getOrientation(side), tileEntity))
			{
				connectable[side] = true;
			}
		}
		
		for(TileEntity tile : connectedOutputters)
		{
			if(tile != null)
			{
				int side = Arrays.asList(connectedOutputters).indexOf(tile);
				
				connectable[side] = true;
			}
		}
		
		for(TileEntity tile : connectedCables)
		{
			if(tile != null)
			{
				int side = Arrays.asList(connectedCables).indexOf(tile);
				
				connectable[side] = true;
			}
		}
		
		return connectable;
    }
    
    /**
     * Gets all the connected cables around a specific tile entity.
     * @param tileEntity - center tile entity
     * @return TileEntity[] of connected cables
     */
    public static TileEntity[] getConnectedOutputters(TileEntity tileEntity)
    {
    	TileEntity[] outputters = new TileEntity[] {null, null, null, null, null, null};
    	
    	for(ForgeDirection orientation : ForgeDirection.VALID_DIRECTIONS)
    	{
			TileEntity outputter = Object3D.get(tileEntity).getFromSide(orientation).getTileEntity(tileEntity.worldObj);
			
			if((outputter instanceof ICableOutputter && ((ICableOutputter)outputter).canOutputTo(orientation.getOpposite())) || 
					(outputter instanceof IEnergySource && ((IEnergySource)outputter).emitsEnergyTo(tileEntity, orientation.getOpposite())) ||
					(outputter instanceof IElectrical && ((IElectrical)outputter).canConnect(orientation.getOpposite())) ||
					(outputter instanceof IEnergyHandler && ((IEnergyHandler)outputter).canInterface(orientation.getOpposite())))
			{
				outputters[orientation.ordinal()] = outputter;
			}
    	}
    	
    	return outputters;
    }
    
    /**
     * Whether or not a cable can connect to a specific acceptor.
     * @param side - side to check
     * @param tileEntity - cable TileEntity
     * @return whether or not the cable can connect to the specific side
     */
    public static boolean canConnectToAcceptor(ForgeDirection side, TileEntity tile)
    {
    	TileEntity tileEntity = Object3D.get(tile).getFromSide(side).getTileEntity(tile.worldObj);
    	
    	if(tileEntity instanceof IStrictEnergyAcceptor && ((IStrictEnergyAcceptor)tileEntity).canReceiveEnergy(side.getOpposite()))
    	{
    		return true;
    	}
    	
    	if(tileEntity instanceof IEnergyAcceptor && ((IEnergyAcceptor)tileEntity).acceptsEnergyFrom(tile, side.getOpposite()))
    	{
    		return true;
    	}
    	
    	if(tileEntity instanceof ICableOutputter && ((ICableOutputter)tileEntity).canOutputTo(side.getOpposite()))
    	{
    		return true;
    	}
    	
    	if(tileEntity instanceof IElectrical && ((IElectrical)tileEntity).canConnect(side.getOpposite()))
    	{
    		return true;
    	}
    	
    	if(tileEntity instanceof IEnergyHandler && ((IEnergyHandler)tileEntity).canInterface(side.getOpposite()))
    	{
    		return true;
    	}
    	
    	if(tileEntity instanceof IPowerReceptor && !(tileEntity instanceof ITransmitter) && MekanismUtils.useBuildcraft())
    	{
    		if(!(tileEntity instanceof IEnergyAcceptor) || ((IEnergyAcceptor)tileEntity).acceptsEnergyFrom(null, side.getOpposite()))
    		{
    			if(!(tileEntity instanceof IEnergySource) || ((IEnergySource)tileEntity).emitsEnergyTo(null, side.getOpposite()))
    			{
    				return true;
    			}
    		}
    	}
    	
    	return false;
    }
    
    /**
     * Emits a defined amount of energy to the network, distributing between IC2-based and BuildCraft-based acceptors.
     * @param amount - amount to send
     * @param sender - sending TileEntity
     * @param facing - direction the TileEntity is facing
     * @return rejected energy
     */
    public static double emitEnergyToNetwork(double amount, TileEntity sender, ForgeDirection facing)
    {
    	TileEntity pointer = Object3D.get(sender).getFromSide(facing).getTileEntity(sender.worldObj);
    	
    	if(TransmissionType.checkTransmissionType(pointer, TransmissionType.ENERGY))
    	{
    		ITransmitter<EnergyNetwork> cable = (ITransmitter<EnergyNetwork>)pointer;
    		
    		ArrayList<TileEntity> ignored = new ArrayList<TileEntity>();
    		ignored.add(sender);
    		
    		return cable.getTransmitterNetwork().emit(amount, ignored);
    	}
    	
    	return amount;
    }
    
    public static void emit(TileEntityElectricBlock emitter)
    {
    	if(!emitter.worldObj.isRemote && MekanismUtils.canFunction(emitter))
    	{
			double sendingEnergy = Math.min(emitter.getEnergy(), emitter.getMaxOutput());
			
			if(sendingEnergy > 0)
			{
		    	List<ForgeDirection> outputtingSides = new ArrayList<ForgeDirection>();
		    	boolean[] connectable = getConnections(emitter);
		    	
		    	for(ForgeDirection side : emitter.getOutputtingSides())
		    	{
		    		if(connectable[side.ordinal()])
		    		{
		    			outputtingSides.add(side);
		    		}
		    	}
		    	
		    	if(outputtingSides.size() > 0)
		    	{
		    		double totalToSend = sendingEnergy;
		    		
		    		boolean cont = false;
		    		
		    		do {
		    			cont = false;
		    			double prev = totalToSend;
		    			totalToSend -= (totalToSend - emit_do(emitter, outputtingSides, totalToSend));
		    			
		    			if(prev-totalToSend > 0 && totalToSend > 0)
		    			{
		    				cont = true;
		    			}
		    		} while(cont);
		    		
		    		emitter.setEnergy(emitter.getEnergy() - (sendingEnergy - totalToSend));
		    	}
			}
    	}
    }
    
    private static double emit_do(TileEntityElectricBlock emitter, List<ForgeDirection> outputtingSides, double totalToSend)
    {
		double remains = totalToSend%outputtingSides.size();
		double splitSend = (totalToSend-remains)/outputtingSides.size();
		
		List<ForgeDirection> toRemove = new ArrayList<ForgeDirection>();
		
    	for(ForgeDirection side : outputtingSides)
		{
			TileEntity tileEntity = Object3D.get(emitter).getFromSide(side).getTileEntity(emitter.worldObj);
			double toSend = splitSend+remains;
			remains = 0;
			
			double prev = totalToSend;
			totalToSend -= (toSend - emit_do_do(emitter, tileEntity, side, toSend));
			
			if(prev-totalToSend == 0)
			{
				toRemove.add(side);
			}
		}
    	
    	for(ForgeDirection side : toRemove)
    	{
    		outputtingSides.remove(side);
    	}
    	
    	return totalToSend;
    }
    
    private static double emit_do_do(TileEntityElectricBlock from, TileEntity tileEntity, ForgeDirection side, double sendingEnergy)
    {
		if(TransmissionType.checkTransmissionType(tileEntity, TransmissionType.ENERGY))
		{
			sendingEnergy -= (sendingEnergy - emitEnergyToNetwork(sendingEnergy, from, side));
		}
		else if(tileEntity instanceof IStrictEnergyAcceptor)
		{
			IStrictEnergyAcceptor acceptor = (IStrictEnergyAcceptor)tileEntity;
			sendingEnergy -= (sendingEnergy - acceptor.transferEnergyToAcceptor(side.getOpposite(), sendingEnergy));
		}
		else if(tileEntity instanceof IConductor)
		{
			ForgeDirection outputDirection = side;
			float provide = from.getProvide(outputDirection);

			if(provide > 0)
			{
				IElectricityNetwork outputNetwork = ElectricityHelper.getNetworkFromTileEntity(tileEntity, outputDirection);
	
				if(outputNetwork != null)
				{
					ElectricityPack request = outputNetwork.getRequest(from);
					
					if(request.getWatts() > 0)
					{
						float ueSend = (float)(sendingEnergy*Mekanism.TO_UE);
						ElectricityPack sendPack = ElectricityPack.min(ElectricityPack.getFromWatts(ueSend, from.getVoltage()), ElectricityPack.getFromWatts(provide, from.getVoltage()));
						float rejectedPower = outputNetwork.produce(sendPack, from);
						sendingEnergy -= (sendPack.getWatts() - rejectedPower)*Mekanism.FROM_UE;
					}
				}
			}
		}
		else if(tileEntity instanceof IEnergyHandler)
		{
			IEnergyHandler handler = (IEnergyHandler)tileEntity;
			int used = handler.receiveEnergy(side.getOpposite(), (int)Math.round(sendingEnergy*Mekanism.TO_TE), false);
			sendingEnergy -= used*Mekanism.FROM_TE;
		}
		else if(tileEntity instanceof IEnergySink)
		{
			double toSend = Math.min(sendingEnergy, Math.min(((IEnergySink)tileEntity).getMaxSafeInput(), ((IEnergySink)tileEntity).demandedEnergyUnits())*Mekanism.FROM_IC2);
			double rejects = ((IEnergySink)tileEntity).injectEnergyUnits(side.getOpposite(), toSend*Mekanism.TO_IC2)*Mekanism.FROM_IC2;
			sendingEnergy -= (toSend - rejects);
		}
		else if(tileEntity instanceof IElectrical)
		{
			double toSend = Math.min(sendingEnergy, ((IElectrical)tileEntity).getRequest(side.getOpposite())*Mekanism.FROM_UE);
			ElectricityPack pack = ElectricityPack.getFromWatts((float)(toSend*Mekanism.TO_UE), ((IElectrical)tileEntity).getVoltage());
			sendingEnergy -= (((IElectrical)tileEntity).receiveElectricity(side.getOpposite(), pack, true)*Mekanism.FROM_UE);
		}
		else if(tileEntity instanceof IPowerReceptor && MekanismUtils.useBuildcraft())
		{
			PowerReceiver receiver = ((IPowerReceptor)tileEntity).getPowerReceiver(side.getOpposite());
			
			if(receiver != null)
			{
            	double transferEnergy = Math.min(sendingEnergy, receiver.powerRequest()*Mekanism.FROM_BC);
            	float sent = receiver.receiveEnergy(Type.STORAGE, (float)(transferEnergy*Mekanism.TO_BC), side.getOpposite());
            	sendingEnergy -= sent;
			}
		}
		
		return sendingEnergy;
    }
}
