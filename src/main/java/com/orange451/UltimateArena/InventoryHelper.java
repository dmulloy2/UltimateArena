package com.orange451.UltimateArena;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class InventoryHelper {
	public static int amtItem(Inventory inventory, int itemid) {
		int ret = 0;
		if (inventory != null) {
			ItemStack[] items = inventory.getContents();
			for(int slot = 0; slot < items.length; slot++) {
				if (items[slot] != null) {
					int id = items[slot].getTypeId();
					int amt = items[slot].getAmount();
					if (id == itemid) {
						ret += amt;
					}
				}
			}
		}
		return ret;
	}
	
	public static void setItem(Inventory inventory, int itemid, int amt) {
		if (inventory != null) {
			ItemStack[] items = inventory.getContents();
			for(int slot = 0; slot < items.length; slot++) {
				if (items[slot] != null) {
					int id = items[slot].getTypeId();
					if (id == itemid) {
						items[slot].setAmount(amt);
						return;
					}
				}
			}
		}
	}
	
	public static void removeItem(Inventory inventory, int itemid, int ret) {
		//removeItem(block to remove from, the item id, amount to remove)
		int start = ret;
		if (inventory != null) {
			ItemStack[] items = inventory.getContents();
			for(int slot = 0; slot < items.length; slot++) {
				if (items[slot] != null) {
					int id = items[slot].getTypeId();
					int amt = items[slot].getAmount();
					if (id == itemid) {
						if (ret >0) {
							if (amt >= ret) {
								amt-=ret;
								ret = 0;
							}else{
								ret = start-amt;
								amt = 0;
							}
							if (amt >0) {
								inventory.setItem(slot, new ItemStack(id, amt));
							}else{
								inventory.setItem(slot, null);
							}
						}
						if (ret <= 0) {
							return;
						}
					}
				}
			}
		}
	}
	
	public static void removeItem(Inventory inventory, int slot1) {
		//removeItem(block to remove from, the item id, amount to remove)
		if (inventory != null) {
			ItemStack[] items = inventory.getContents();
			items[slot1].setAmount(0);
		}
	}
	
	public static boolean isEmpty(Inventory inventory) {
		if (inventory != null) {
			ItemStack[] items = inventory.getContents();
			for(int slot = 0; slot < items.length; slot++) {
				if (items[slot] != null) {
					if (items[slot].getTypeId() > 0) {
						return false;
					}
				}
			}
		}
		try{
			PlayerInventory inventory2 = (PlayerInventory)inventory;
			if (inventory2.getHelmet() != null)
				return false;
			if (inventory2.getChestplate() != null)
				return false;
			if (inventory2.getLeggings() != null)
				return false;
			if (inventory2.getBoots() != null)
				return false;
		}catch(Exception e) {
			//fuck u
		}
		return true;
	}
	
	public static boolean clearInventory(Inventory inventory) {
		inventory.clear();
		try{
			PlayerInventory inventory2 = (PlayerInventory)inventory;
			inventory2.setHelmet(null);
			inventory2.setChestplate(null);
			inventory2.setLeggings(null);
			inventory2.setBoots(null);
		}catch(Exception e) {
			//fuck u
		}
		return true;
	}

	public static int getItemPosition(Inventory inventory, ItemStack itm) {
		if (inventory != null) {
			ItemStack[] items = inventory.getContents();
			for(int slot = 0; slot < items.length; slot++) {
				if (items[slot] != null) {
					if (items[slot].equals(itm)) {
						return slot;
					}
				}
			}
		}
		return -1;
	}
	
	public static ItemStack getFirstItemStack(Inventory inventory, Material mat) {
		if (inventory != null) {
			ItemStack[] items = inventory.getContents();
			for(int slot = 0; slot < items.length; slot++) {
				if (items[slot] != null) {
					if (items[slot].getType().equals(mat)) {
						return items[slot];
					}
				}
			}
		}
		return null;
	}
	
	public static int getFirstFreeSlot(Inventory inventory) {
		if (inventory != null) {
			ItemStack[] items = inventory.getContents();
			for(int slot = 0; slot < items.length; slot++) {
				if (items[slot] != null) {
					if (items[slot].getTypeId() == 0) {
						return slot;
					}
				}else{
					return slot;
				}
			}
		}
		return -1;
	}
	
}
