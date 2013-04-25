package rebelkeithy.mods.metallurgy.api;

import net.minecraft.item.ItemStack;

public interface IOreInfo 
{
	public String getName();
	public OreType getType();
	
	public ItemStack getOre();
	public ItemStack getBlock();
	public ItemStack getBrick();

	// Returns the itemstack of dust this ore crushes into, if no dust exists, returns null
	public ItemStack getDust();

	// Returns the itemstack of ingot for this ore, if no ingot exists, returns null
	public ItemStack getIngot();
	
	// If this ore drops something other than itself, this returns the ItemStack
	// of the drop, otherwise returns null
	public ItemStack getDrop();
	public int getDropAmountMin();
	public int getDropAmountMax();
	
	// Returns an array of OreDictionary keys of the dusts the make this if it's an alloy
	// if it's not an alloy, this returns null
	public String[] getAlloyRecipe();
}
