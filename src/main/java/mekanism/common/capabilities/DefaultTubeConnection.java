package mekanism.common.capabilities;

import mekanism.api.gas.ITubeConnection;
import mekanism.common.capabilities.DefaultStorageHelper.NullStorage;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class DefaultTubeConnection implements ITubeConnection
{
	@Override
	public boolean canTubeConnect(EnumFacing side) 
	{
		return false;
	}
	
	public static void register()
	{
        CapabilityManager.INSTANCE.register(ITubeConnection.class, new NullStorage<>(), DefaultTubeConnection.class);
	}
}
