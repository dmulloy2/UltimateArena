/**
 * (c) 2013 dmulloy2
 */
package net.dmulloy2.ultimatearena.types;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

/**
 * This class provides a lazy-load Location, so that World doesn't need to be
 * initialized yet when an object of this class is created, only when the
 * {@link Location} is first accessed.
 * 
 * @author dmulloy2
 */

@Getter
public class LazyLocation implements ConfigurationSerializable
{
	private transient Location location;
	private transient SimpleVector simpleVector;

	private String worldName;
	private int x;
	private int y;
	private int z;

	public LazyLocation(String worldName, int x, int y, int z)
	{
		this.worldName = worldName;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public LazyLocation(String worldName, int x, int z)
	{
		this(worldName, x, 0, z);
	}

	public LazyLocation(Location location)
	{
		this(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}

	public LazyLocation(Map<String, Object> args)
	{
		this.worldName = (String) args.get("worldName");
		this.x = (int) args.get("x");
		this.y = (int) args.get("y");
		this.z = (int) args.get("z");
	}

	public World getWorld()
	{
		return Bukkit.getWorld(worldName);
	}

	/**
	 * Initializes the Location
	 */
	private void initLocation()
	{
		// if location is already initialized, simply return
		if (location != null)
			return;

		// get World; hopefully it's initialized at this point
		World world = Bukkit.getWorld(worldName);
		if (world == null)
			return;

		// store the Location for future calls, and pass it on
		location = new Location(world, x, y, z);
	}

	/**
	 * Initializes the SimpleVector
	 */
	private void initSimpleVector()
	{
		if (simpleVector != null)
			return;

		if (getLocation() == null)
			return;

		simpleVector = new SimpleVector(getLocation());
	}

	/**
	 * Returns the actual {@link Location}
	 */
	public Location getLocation()
	{
		initLocation();
		return location;
	}

	/**
	 * Returns a new {@link SimpleVector} based around this
	 */
	public SimpleVector getSimpleVector()
	{
		initSimpleVector();
		return simpleVector;
	}

	@Override
	public Map<String, Object> serialize()
	{
		Map<String, Object> result = new LinkedHashMap<String, Object>();

		result.put("worldName", worldName);
		result.put("x", x);
		result.put("y", y);
		result.put("z", z);

		return result;
	}
}