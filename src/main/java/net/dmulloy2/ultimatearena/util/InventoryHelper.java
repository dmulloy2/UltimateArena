package net.dmulloy2.ultimatearena.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class InventoryHelper
{
	public static boolean isEmpty(PlayerInventory inventory)
	{
		if (inventory != null)
		{
			for (ItemStack stack : inventory.getContents())
			{
				if (stack != null && stack.getType() != Material.AIR)
				{
					return false;
				}
			}
		
			if (inventory.getHelmet() != null)
				return false;
		
			if (inventory.getChestplate() != null)
				return false;
			
			if (inventory.getLeggings() != null)
				return false;
		
			if (inventory.getBoots() != null)
				return false;
		}
		
		return true;
	}

	public static int getFirstFreeSlot(PlayerInventory inventory) 
	{
		if (inventory != null)
		{
			ItemStack[] items = inventory.getContents();
			for(int slot = 0; slot < items.length; slot++)
			{
				if (items[slot] != null)
				{
					if (items[slot].getTypeId() == 0)
					{
						return slot;
					}
				}
				else
				{
					return slot;
				}
			}
		}
		return -1;
	}
}