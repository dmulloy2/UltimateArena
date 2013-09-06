package net.dmulloy2.ultimatearena.types;

import org.bukkit.Location;
import org.bukkit.World;

/**
 * Represents a spawn in an Arena
 * @author dmulloy2
 */

public class ArenaSpawn
{
	private int x;
	private int y;
	private int z;
	private World world;

	/**
	 * Creates a new ArenaSpawn with a given {@link Location}
	 * @param location - {@link Location} the spawn is at
	 */
	public ArenaSpawn(Location location)
	{
		setLocation(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}

	/**
	 * Gets the {@link Location} of the Spawn
	 * @return The {@link Location} of the spawn
	 */
	public Location getLocation() 
	{
		return new Location(world, x, y, z);
	}
	
	/**
	 * Sets the {@link Location} of the spawn
	 * @param world - {@link World} the spawn is in
	 * @param x - X coordinate of the spawn
	 * @param y - Y coordinate of the spawn
	 * @param z - Z coordinate of the spawn
	 */
	public void setLocation(World world, int x, int y, int z) 
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
	}
}