package com.orange451.UltimateArena.Arenas.Objects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class SavedArenaPlayer 
{
	private String name;
	private int levels;
	private Location location;
	
	private List<ItemStack> savedInventory = new ArrayList<ItemStack>();
	private List<ItemStack> savedArmor = new ArrayList<ItemStack>();
	
	public SavedArenaPlayer(String name, int levels, Location location, List<ItemStack> savedInventory, List<ItemStack> savedArmor)
	{
		this.name = name;
		this.levels = levels;
		this.location = location;
		this.savedInventory = savedInventory;
		this.savedArmor = savedArmor;
	}
	
	public String getName()
	{
		return name;
	}
	
	public int getLevels()
	{
		return levels;
	}
	
	public Location getLocation()
	{
		return location;
	}
	
	public List<ItemStack> getSavedInventory()
	{
		return savedInventory;
	}
	
	public List<ItemStack> getSavedArmor()
	{
		return savedArmor;
	}
}