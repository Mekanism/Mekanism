package mekanism.common.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mekanism.common.base.IBlockType;
import net.minecraft.item.crafting.CraftingManager;

public class TypeConfigManager 
{
	private Map<String, Boolean> config = new HashMap<String, Boolean>();
	
	public boolean isEnabled(String type)
	{
		return config.get(type) != null && config.get(type);
	}
	
	public void setEntry(String type, boolean enabled)
	{
		config.put(type, enabled);
	}
}
