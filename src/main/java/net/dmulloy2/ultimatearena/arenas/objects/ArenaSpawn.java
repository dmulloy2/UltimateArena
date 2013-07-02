package net.dmulloy2.ultimatearena.arenas.objects;

import org.bukkit.Location;
import org.bukkit.World;

public class ArenaSpawn
{
	public int x;
	public int y;
	public int z;
	public World world;
	
	public ArenaSpawn(World world, int x, int y, int z) 
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
	}

	public Location getLocation() 
	{
		return new Location(world, x, y, z);
	}
	
	public void setLocation(World world, int x, int y, int z) 
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
	}
}
