package com.orange451.UltimateArena.Arenas.Objects;

import org.bukkit.Location;

public class SavedArenaPlayer 
{
	public String name;
	public float exp;
	public Location location;
	
	public SavedArenaPlayer(String name, float exp, Location location)
	{
		this.name = name;
		this.exp = exp;
		this.location = location;
	}
	
	public String getName()
	{
		return name;
	}
	
	public float getExp()
	{
		return exp;
	}
	
	public Location getLocation()
	{
		return location;
	}
}