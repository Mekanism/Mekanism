package mekanism.common.multiblock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import mekanism.api.Coord4D;
import mekanism.common.tile.TileEntityDynamicTank;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.util.Constants.NBT;

public class MultiblockManager<T>
{
	private static Set<MultiblockManager> managers = new HashSet<MultiblockManager>();
	
	public static boolean loaded;
	
	public Class<? extends MultiblockCache<T>> cacheClass;
	
	public DataHandler dataHandler;
	
	public String name;
	
	/** A map containing references to all dynamic tank inventory caches. */
	public Map<Integer, MultiblockCache<T>> inventories = new HashMap<Integer, MultiblockCache<T>>();
	
	public MultiblockManager(String s, Class<? extends MultiblockCache<T>> cache)
	{
		name = s;
		managers.add(this);
		cacheClass = cache;
	}
	
	public void createOrLoad(World world)
	{
		if(dataHandler == null)
		{
			dataHandler = (DataHandler)world.perWorldStorage.loadData(DataHandler.class, name);
			
			if(dataHandler == null)
			{
				dataHandler = new DataHandler(name);
				dataHandler.setManager(this);
				world.perWorldStorage.setData(name, dataHandler);
			}
			else {
				dataHandler.setManager(this);
				dataHandler.syncManager();
			}
		}
	}
	
	/**
	 * Grabs an inventory from the world's caches, and removes all the world's references to it.
	 * @param world - world the cache is stored in
	 * @param id - inventory ID to pull
	 * @return correct Dynamic Tank inventory cache
	 */
	public MultiblockCache<T> pullInventory(World world, int id)
	{
		if(!loaded)
		{
			load(world);
		}
		
		MultiblockCache<T> toReturn = inventories.get(id);
		
		inventories.remove(id);
		dataHandler.markDirty();

		return toReturn;
	}

	/**
	 * Updates a dynamic tank cache with the defined inventory ID with the parameterized values.
	 * @param inventoryID - inventory ID of the dynamic tank
	 * @param cache - cache of the dynamic tank
	 * @param multiblock - dynamic tank TileEntity
	 */
	public void updateCache(IMultiblock<T> multiblock)
	{
		try {
			if(!loaded)
			{
				load(((TileEntity)multiblock).getWorldObj());
			}
			
			if(!inventories.containsKey(multiblock.getSynchronizedData().inventoryID))
			{
				MultiblockCache<T> cache = cacheClass.newInstance();
				cache.sync((T)multiblock.getSynchronizedData());
				cache.locations.add(Coord4D.get((TileEntity)multiblock));
	
				inventories.put(multiblock.getSynchronizedData().inventoryID, cache);
	
				return;
			}
			
			inventories.get(multiblock.getSynchronizedData().inventoryID).sync((T)multiblock.getSynchronizedData());
			inventories.get(multiblock.getSynchronizedData().inventoryID).locations.add(Coord4D.get((TileEntity)multiblock));
			dataHandler.markDirty();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Grabs a unique inventory ID for a dynamic tank.
	 * @return unique inventory ID
	 */
	public int getUniqueInventoryID()
	{
		int id = 0;

		while(true)
		{
			for(Integer i : inventories.keySet())
			{
				if(id == i)
				{
					id++;
					continue;
				}
			}

			return id;
		}
	}
	
	public static void tick(World world)
	{
		if(!loaded)
		{
			load(world);
		}
		
		for(MultiblockManager manager : managers)
		{
			ArrayList<Integer> idsToKill = new ArrayList<Integer>();
			HashMap<Integer, HashSet<Coord4D>> tilesToKill = new HashMap<Integer, HashSet<Coord4D>>();
			
			for(Map.Entry<Integer, MultiblockCache> entry : ((Map<Integer, MultiblockCache>)manager.inventories).entrySet())
			{
				int inventoryID = entry.getKey();
	
				for(Coord4D obj : (Set<Coord4D>)entry.getValue().locations)
				{
					if(obj.dimensionId == world.provider.dimensionId && obj.exists(world))
					{
						TileEntity tileEntity = obj.getTileEntity(world);
	
						if(!(tileEntity instanceof TileEntityDynamicTank) || (getStructureId(((TileEntityDynamicTank)tileEntity)) != -1 && getStructureId(((TileEntityDynamicTank)tileEntity)) != inventoryID))
						{
							if(!tilesToKill.containsKey(inventoryID))
							{
								tilesToKill.put(inventoryID, new HashSet<Coord4D>());
							}
	
							tilesToKill.get(inventoryID).add(obj);
						}
					}
				}
	
				if(entry.getValue().locations.isEmpty())
				{
					idsToKill.add(inventoryID);
				}
			}
	
			for(Map.Entry<Integer, HashSet<Coord4D>> entry : tilesToKill.entrySet())
			{
				for(Coord4D obj : entry.getValue())
				{
					((Map<Integer, MultiblockCache>)manager.inventories).get(entry.getKey()).locations.remove(obj);
					manager.dataHandler.markDirty();
				}
			}
	
			for(int inventoryID : idsToKill)
			{
				manager.inventories.remove(inventoryID);
				manager.dataHandler.markDirty();
			}
		}
	}
	
	public static int getStructureId(TileEntityDynamicTank tile)
	{
		return tile.structure != null ? tile.structure.inventoryID : -1;
	}
	
	public int getInventoryId(TileEntityDynamicTank tile)
	{
		Coord4D coord = Coord4D.get(tile);
		
		for(Map.Entry<Integer, MultiblockCache<T>> entry : inventories.entrySet())
		{
			if(entry.getValue().locations.contains(coord))
			{
				return entry.getKey();
			}
		}
		
		return -1;
	}
	
	public static void load(World world)
	{
		loaded = true;
		
		for(MultiblockManager manager : managers)
		{
			manager.createOrLoad(world);
		}
	}
	
	public static void reset()
	{
		for(MultiblockManager manager : managers)
		{
			manager.inventories.clear();
			manager.dataHandler = null;
		}
		
		loaded = false;
	}
	
	public static class DataHandler extends WorldSavedData
	{
		public MultiblockManager manager;
		
		public Map<Integer, MultiblockCache> loadedInventories;
		
		public DataHandler(String tagName)
		{
			super(tagName);
		}
		
		public void setManager(MultiblockManager m)
		{
			manager = m;
		}
		
		public void syncManager()
		{
			if(loadedInventories != null)
			{
				manager.inventories = loadedInventories;
			}
		}
		
		@Override
		public void readFromNBT(NBTTagCompound nbtTags) 
		{
			try {
				String cacheClass = nbtTags.getString("cacheClass");
				
				NBTTagList list = nbtTags.getTagList("invList", NBT.TAG_COMPOUND);
				
				loadedInventories = new HashMap<Integer, MultiblockCache>();
				
				for(int i = 0; i < list.tagCount(); i++)
				{
					NBTTagCompound compound = list.getCompoundTagAt(i);
					MultiblockCache cache = (MultiblockCache)Class.forName(cacheClass).newInstance();
					cache.load(compound);
					
					NBTTagList coordsList = compound.getTagList("coordsList", NBT.TAG_COMPOUND);
					
					for(int j = 0; j < coordsList.tagCount(); j++)
					{
						cache.locations.add(Coord4D.read(coordsList.getCompoundTagAt(j)));
					}
	
					loadedInventories.put(compound.getInteger("id"), cache);
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void writeToNBT(NBTTagCompound nbtTags) 
		{
			nbtTags.setString("cacheClass", manager.cacheClass.getName());
			
			NBTTagList list = new NBTTagList();
			
			for(Map.Entry<Integer, MultiblockCache> entry : ((Map<Integer, MultiblockCache>)manager.inventories).entrySet())
			{
				NBTTagCompound compound = new NBTTagCompound();
				compound.setInteger("id", entry.getKey());
				entry.getValue().save(compound);
				
				NBTTagList coordsList = new NBTTagList();
				
				for(Coord4D coord : (Set<Coord4D>)entry.getValue().locations)
				{
					coordsList.appendTag(coord.write(new NBTTagCompound()));
				}
				
				compound.setTag("coordsList", coordsList);
				list.appendTag(compound);
			}
			
			nbtTags.setTag("invList", list);
		}
	}
}
