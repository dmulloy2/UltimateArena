package com.orange451.UltimateArena.Arenas.Objects;

import org.bukkit.Location;

public class SavedArenaPlayer 
{
	public String name;
	public int levels;
	public Location location;
	
	public SavedArenaPlayer(String name, int levels, Location location)
	{
		this.name = name;
		this.levels = levels;
		this.location = location;
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
}