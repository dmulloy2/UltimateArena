package net.dmulloy2.ultimatearena.arenas.objects;

import org.bukkit.Location;
import org.bukkit.World;

public class ArenaSpawn
{
	private int x;
	private int y;
	private int z;
	private World world;
	
	public ArenaSpawn(World world, int x, int y, int z) 
	{
		setLocation(world, x, y, z);
	}
	
	public ArenaSpawn(Location location)
	{
		setLocation(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
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