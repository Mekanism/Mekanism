package mekanism.client;


import java.util.HashMap;

import mekanism.api.EnumGas;
import mekanism.common.CommonProxy;
import mekanism.common.EntityObsidianTNT;
import mekanism.common.EntityRobit;
import mekanism.common.IElectricChest;
import mekanism.common.InventoryElectricChest;
import mekanism.common.ItemPortableTeleporter;
import mekanism.common.Mekanism;
import mekanism.common.TileEntityAdvancedElectricMachine;
import mekanism.common.TileEntityAdvancedFactory;
import mekanism.common.TileEntityChargepad;
import mekanism.common.TileEntityCombiner;
import mekanism.common.TileEntityControlPanel;
import mekanism.common.TileEntityCrusher;
import mekanism.common.TileEntityDynamicTank;
import mekanism.common.TileEntityDynamicValve;
import mekanism.common.TileEntityElectricChest;
import mekanism.common.TileEntityElectricMachine;
import mekanism.common.TileEntityElectricPump;
import mekanism.common.TileEntityEliteFactory;
import mekanism.common.TileEntityEnergizedSmelter;
import mekanism.common.TileEntityEnergyCube;
import mekanism.common.TileEntityEnrichmentChamber;
import mekanism.common.TileEntityFactory;
import mekanism.common.TileEntityGasTank;
import mekanism.common.TileEntityMechanicalPipe;
import mekanism.common.TileEntityMetallurgicInfuser;
import mekanism.common.TileEntityOsmiumCompressor;
import mekanism.common.TileEntityPressurizedTube;
import mekanism.common.TileEntityPurificationChamber;
import mekanism.common.TileEntityTeleporter;
import mekanism.common.TileEntityTheoreticalElementizer;
import mekanism.common.TileEntityUniversalCable;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Client proxy for the Mekanism mod.
 * @author AidanBrady
 *
 */
@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
	public static int MACHINE_RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	public static int TRANSMITTER_RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	public static int BASIC_RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	
	@Override
	public void loadConfiguration()
	{
		super.loadConfiguration();
		
		Mekanism.configuration.load();
		Mekanism.enableSounds = Mekanism.configuration.get(Configuration.CATEGORY_GENERAL, "EnableSounds", true).getBoolean(true);
		Mekanism.enableBlockDebug = Mekanism.configuration.get(Configuration.CATEGORY_GENERAL, "EnableBlockDebug", false).getBoolean(false);
		Mekanism.configuration.save();
	}
	
	@Override
	public int getArmorIndex(String string)
	{
		return RenderingRegistry.addNewArmourRendererPrefix(string);
	}
	
	@Override
	public void registerSound(TileEntity tileEntity) 
	{
		if(Mekanism.enableSounds && FMLClientHandler.instance().getClient().sndManager.sndSystem != null)
		{
			synchronized(Mekanism.audioHandler.sounds)
			{
				Mekanism.audioHandler.register(tileEntity);
			}
		}
	}
	
	@Override
	public void unregisterSound(TileEntity tileEntity) 
	{
		if(Mekanism.enableSounds && FMLClientHandler.instance().getClient().sndManager.sndSystem != null)
		{
			synchronized(Mekanism.audioHandler.sounds)
			{
				if(Mekanism.audioHandler.getFrom(tileEntity) != null)
				{
					Mekanism.audioHandler.getFrom(tileEntity).remove();
				}
			}
		}
	}
	
	@Override
	public void openElectricChest(EntityPlayer entityplayer, int id, int windowId, boolean isBlock, int x, int y, int z) 
	{
		TileEntityElectricChest tileEntity = (TileEntityElectricChest)entityplayer.worldObj.getBlockTileEntity(x, y, z);
		
		if(id == 0)
		{
			if(isBlock)
			{
	    		FMLClientHandler.instance().displayGuiScreen(entityplayer, new GuiElectricChest(entityplayer.inventory, tileEntity));
	    		entityplayer.openContainer.windowId = windowId;
			}
			else {
				FMLClientHandler.instance().getClient().sndManager.playSoundFX("random.chestopen", 1.0F, 1.0F);
				ItemStack stack = entityplayer.getCurrentEquippedItem();
				
				if(stack != null && stack.getItem() instanceof IElectricChest && ((IElectricChest)stack.getItem()).isElectricChest(stack))
				{
    				InventoryElectricChest inventory = new InventoryElectricChest(entityplayer);
		    		FMLClientHandler.instance().displayGuiScreen(entityplayer, new GuiElectricChest(entityplayer.inventory, inventory));
		    		entityplayer.openContainer.windowId = windowId;
				}
			}
		}
		else if(id == 1)
		{
			if(isBlock)
			{
				FMLClientHandler.instance().displayGuiScreen(entityplayer, new GuiPasswordEnter(tileEntity));
			}
			else {
				ItemStack stack = entityplayer.getCurrentEquippedItem();
				if(stack != null && stack.getItem() instanceof IElectricChest && ((IElectricChest)stack.getItem()).isElectricChest(stack))
				{
					FMLClientHandler.instance().displayGuiScreen(entityplayer, new GuiPasswordEnter(stack));
				}
			}
		}
		else if(id == 2)
		{
			if(isBlock)
			{
				FMLClientHandler.instance().displayGuiScreen(entityplayer, new GuiPasswordModify(tileEntity));
			}
			else {
				ItemStack stack = entityplayer.getCurrentEquippedItem();
				if(stack != null && stack.getItem() instanceof IElectricChest && ((IElectricChest)stack.getItem()).isElectricChest(stack))
				{
					FMLClientHandler.instance().displayGuiScreen(entityplayer, new GuiPasswordModify(stack));
				}
			}
		}
	}
	
	@Override
	public void registerSpecialTileEntities() 
	{
		ClientRegistry.registerTileEntity(TileEntityEnrichmentChamber.class, "EnrichmentChamber", new RenderConfigurableMachine());
		ClientRegistry.registerTileEntity(TileEntityOsmiumCompressor.class, "OsmiumCompressor", new RenderConfigurableMachine());
		ClientRegistry.registerTileEntity(TileEntityCombiner.class, "Combiner", new RenderConfigurableMachine());
		ClientRegistry.registerTileEntity(TileEntityCrusher.class, "Crusher", new RenderConfigurableMachine());
		ClientRegistry.registerTileEntity(TileEntityFactory.class, "SmeltingFactory", new RenderConfigurableMachine());
		ClientRegistry.registerTileEntity(TileEntityAdvancedFactory.class, "AdvancedSmeltingFactory", new RenderConfigurableMachine());
		ClientRegistry.registerTileEntity(TileEntityEliteFactory.class, "UltimateSmeltingFactory", new RenderConfigurableMachine());
		ClientRegistry.registerTileEntity(TileEntityPurificationChamber.class, "PurificationChamber", new RenderConfigurableMachine());
		ClientRegistry.registerTileEntity(TileEntityEnergizedSmelter.class, "EnergizedSmelter", new RenderConfigurableMachine());
		ClientRegistry.registerTileEntity(TileEntityTheoreticalElementizer.class, "TheoreticalElementizer", new RenderTheoreticalElementizer());
		ClientRegistry.registerTileEntity(TileEntityMetallurgicInfuser.class, "MetallurgicInfuser", new RenderMetallurgicInfuser());
		ClientRegistry.registerTileEntity(TileEntityPressurizedTube.class, "PressurizedTube", new RenderPressurizedTube());
		ClientRegistry.registerTileEntity(TileEntityUniversalCable.class, "UniversalCable", new RenderUniversalCable());
		ClientRegistry.registerTileEntity(TileEntityElectricPump.class, "ElectricPump", new RenderElectricPump());
		ClientRegistry.registerTileEntity(TileEntityElectricChest.class, "ElectricChest", new RenderElectricChest());
		ClientRegistry.registerTileEntity(TileEntityMechanicalPipe.class, "MechanicalPipe", new RenderMechanicalPipe());
		ClientRegistry.registerTileEntity(TileEntityDynamicTank.class, "DynamicTank", new RenderDynamicTank());
		ClientRegistry.registerTileEntity(TileEntityDynamicValve.class, "DynamicValve", new RenderDynamicTank());
		ClientRegistry.registerTileEntity(TileEntityChargepad.class, "Chargepad", new RenderChargepad());
	}
	
	@Override
	public void registerRenderInformation()
	{
		//Register entity rendering handlers
		RenderingRegistry.registerEntityRenderingHandler(EntityObsidianTNT.class, new RenderObsidianTNT());
		RenderingRegistry.registerEntityRenderingHandler(EntityRobit.class, new RenderRobit());
		
		//Register item handler
		MinecraftForgeClient.registerItemRenderer(Mekanism.energyCubeID, new ItemRenderingHandler());
		MinecraftForgeClient.registerItemRenderer(Mekanism.machineBlockID, new ItemRenderingHandler());
		MinecraftForgeClient.registerItemRenderer(Mekanism.Robit.itemID, new ItemRenderingHandler());
		
		//Register block handlers
		RenderingRegistry.registerBlockHandler(new MachineRenderingHandler());
		RenderingRegistry.registerBlockHandler(new TransmitterRenderer());
		RenderingRegistry.registerBlockHandler(new BasicRenderingHandler());
		
		if(!EnumGas.HYDROGEN.hasTexture())
		{
			EnumGas.HYDROGEN.gasIcon = FMLClientHandler.instance().getClient().renderEngine.textureMapItems.registerIcon("mekanism:LiquidHydrogen");
			EnumGas.HYDROGEN.texturePath = "/mods/mekanism/textures/items/LiquidHydrogen.png";
		}
		
		if(!EnumGas.OXYGEN.hasTexture())
		{
			EnumGas.OXYGEN.gasIcon = FMLClientHandler.instance().getClient().renderEngine.textureMapItems.registerIcon("mekanism:LiquidOxygen");
			EnumGas.OXYGEN.texturePath = "/mods/mekanism/textures/items/LiquidOxygen.png";
		}
		
		System.out.println("[Mekanism] Render registrations complete.");
	}
	
	@Override
	public GuiScreen getClientGui(int ID, EntityPlayer player, World world, int x, int y, int z) 
	{
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		
		switch(ID)
		{
			case 0:
				return new GuiStopwatch(player);
			case 1:
				return new GuiCredits();
			case 2:
				return new GuiWeatherOrb(player);
			case 3:
				return new GuiEnrichmentChamber(player.inventory, (TileEntityElectricMachine)tileEntity);
			case 4:
				return new GuiOsmiumCompressor(player.inventory, (TileEntityAdvancedElectricMachine)tileEntity);
			case 5:
				return new GuiCombiner(player.inventory, (TileEntityAdvancedElectricMachine)tileEntity);
			case 6:
				return new GuiCrusher(player.inventory, (TileEntityElectricMachine)tileEntity);
			case 7:
				return new GuiTheoreticalElementizer(player.inventory, (TileEntityTheoreticalElementizer)tileEntity);
			case 8:
				return new GuiEnergyCube(player.inventory, (TileEntityEnergyCube)tileEntity);
			case 9:
				return new GuiControlPanel((TileEntityControlPanel)tileEntity, player, world);
			case 10:
				return new GuiGasTank(player.inventory, (TileEntityGasTank)tileEntity);
			case 11:
				return new GuiFactory(player.inventory, (TileEntityFactory)tileEntity);
			case 12:
				return new GuiMetallurgicInfuser(player.inventory, (TileEntityMetallurgicInfuser)tileEntity);
			case 13:
				return new GuiTeleporter(player.inventory, (TileEntityTeleporter)tileEntity);
			case 14:
				ItemStack itemStack = player.getCurrentEquippedItem();
				if(itemStack != null && itemStack.getItem() instanceof ItemPortableTeleporter)
				{
					return new GuiPortableTeleporter(player, itemStack);
				}
			case 15:
				return new GuiPurificationChamber(player.inventory, (TileEntityAdvancedElectricMachine)tileEntity);
			case 16:
				return new GuiEnergizedSmelter(player.inventory, (TileEntityElectricMachine)tileEntity);
			case 17:
				return new GuiElectricPump(player.inventory, (TileEntityElectricPump)tileEntity);
			case 18:
				return new GuiDynamicTank(player.inventory, (TileEntityDynamicTank)tileEntity);
			case 19:
				return new GuiPasswordEnter((TileEntityElectricChest)tileEntity);
			case 20:
				return new GuiPasswordModify((TileEntityElectricChest)tileEntity);
			case 21:
				EntityRobit robit = (EntityRobit)world.getEntityByID(x);
				if(robit != null)
				{
					return new GuiRobitMain(player.inventory, robit);
				}
			case 22:
				return new GuiRobitCrafting(player.inventory, world, x);
			case 23:
				EntityRobit robit1 = (EntityRobit)world.getEntityByID(x);
				if(robit1 != null)
				{
					return new GuiRobitInventory(player.inventory, robit1);
				}
			case 24:
				EntityRobit robit2 = (EntityRobit)world.getEntityByID(x);
				if(robit2 != null)
				{
					return new GuiRobitSmelting(player.inventory, robit2);
				}
			case 25:
				return new GuiRobitRepair(player.inventory, world, x);
		}
		
		return null;
	}
	
	@Override
	public void doTankAnimation(TileEntityDynamicTank tileEntity)
	{
		new ThreadTankSparkle(tileEntity).start();
	}
	
	@Override
	public void loadUtilities()
	{
		super.loadUtilities();
		
		TickRegistry.registerTickHandler(new ClientTickHandler(), Side.CLIENT);
		TickRegistry.registerTickHandler(new ClientPlayerTickHandler(), Side.CLIENT);
		TickRegistry.registerTickHandler(new RenderTickHandler(), Side.CLIENT);
		
		NetworkRegistry.instance().registerConnectionHandler(new ClientConnectionHandler());
	}
	
	@Override
	public void loadSoundHandler()
	{
		if(Mekanism.enableSounds)
		{
			Mekanism.audioHandler = new SoundHandler();
		}
	}
	
	@Override
	public void unloadSoundHandler()
	{
		if(Mekanism.enableSounds)
		{
			if(Mekanism.audioHandler != null)
			{
				synchronized(Mekanism.audioHandler.sounds)
				{
					HashMap<TileEntity, Sound> sounds = new HashMap<TileEntity, Sound>();
					sounds.putAll(Mekanism.audioHandler.sounds);
					
					for(Sound sound : sounds.values())
					{
						sound.remove();
					}
				}
			}
		}
	}
	
	@Override
	public boolean isPaused()
	{
		if(FMLClientHandler.instance().getClient().isSingleplayer() && !FMLClientHandler.instance().getClient().getIntegratedServer().getPublic())
		{
			GuiScreen screen = FMLClientHandler.instance().getClient().currentScreen;
			
			if(screen != null)
			{
				if(screen.doesGuiPauseGame())
				{
					return true;
				}
			}
		}
		
		return false;
	}
}
