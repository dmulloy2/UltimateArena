package com.orange451.UltimateArena.Arenas.Objects;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SavedArenaPlayer 
{
	public Player player;
	public int exp;
	public Location location;
	
	public SavedArenaPlayer(Player player, int exp, Location location)
	{
		this.player = player;
		this.exp = exp;
		this.location = location;
	}
	
	public Player getPlayer()
	{
		return player;
	}
	
	public int getExp()
	{
		return exp;
	}
	
	public Location getLocation()
	{
		return location;
	}
}