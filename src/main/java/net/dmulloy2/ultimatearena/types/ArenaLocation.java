/**
 * (c) 2013 - 2014 dmulloy2
 */
package net.dmulloy2.ultimatearena.types;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import net.dmulloy2.types.SimpleVector;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

/**
 * This class provides a lazy-load Location, so that World doesn't need to be
 * initialized yet when an object of this class is created, only when the
 * {@link Location} is first accessed. This class is also serializable and
 * can easily be converted into a {@link Location} or {@link SimpleVector}
 *
 * @author dmulloy2
 */

@Getter @Setter
@SerializableAs("net.dmulloy2.ArenaLocation")
public final class ArenaLocation implements ConfigurationSerializable, Cloneable
{
	private final int x, y, z;
	private final String worldName;

	private transient World world;
	private transient Location location;
	private transient SimpleVector simpleVector;

	public ArenaLocation(String worldName, int x, int y, int z)
	{
		Validate.notNull(worldName, "worldName cannot be null!");
		this.worldName = worldName;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public ArenaLocation(String worldName, int x, int z)
	{
		this(worldName, x, 0, z);
	}

	public ArenaLocation(World world, int x, int y, int z)
	{
		this(world.getName(), x, y, z);
	}

	public ArenaLocation(World world, int x, int z)
	{
		this(world.getName(), x, z);
	}

	public ArenaLocation(Location location)
	{
		this(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}

	public ArenaLocation(Player player)
	{
		this(player.getLocation());
	}

	public ArenaLocation(Map<String, Object> args)
	{
		Validate.notNull(args, "args cannot be null!");
		this.worldName = (String) args.get("worldName");
		this.x = (int) args.get("x");
		this.y = (int) args.get("y");
		this.z = (int) args.get("z");
	}

	/**
	 * Returns the {@link World} associated with this
	 */
	public World getWorld()
	{
		if (world == null)
			world = Bukkit.getWorld(worldName);

		return world;
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
		World world = getWorld();
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

	public static ArenaLocation getMaximum(ArenaLocation l1, ArenaLocation l2)
	{
		return new ArenaLocation(l1.worldName, Math.max(l1.x, l2.x), Math.max(l1.y, l2.y), Math.max(l1.z, l2.z));
	}

	public static ArenaLocation getMinimum(ArenaLocation l1, ArenaLocation l2)
	{
		return new ArenaLocation(l1.worldName, Math.min(l1.x, l2.x), Math.min(l1.y, l2.y), Math.min(l1.z, l2.z));
	}

	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof ArenaLocation)
		{
			ArenaLocation that = (ArenaLocation) obj;
			if (x != that.x || y != that.y || z != that.z)
				return false;

			return worldName.equals(that.worldName);
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode()
	{
		int hash = 39;
		hash *= x;
		hash *= y;
		hash *= z;
		hash *= worldName.hashCode();
		return hash;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString()
	{
		return "ArenaLocation { x = " + x + ", y = " + y + ", z = " + z + ", worldName = " + worldName + " }";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArenaLocation clone()
	{
		return new ArenaLocation(worldName, x, y, z);
	}
}