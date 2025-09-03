/**
 * UltimateArena - fully customizable PvP arenas
 * Copyright (C) 2012 - 2015 MineSworn
 * Copyright (C) 2013 - 2015 dmulloy2
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.dmulloy2.ultimatearena.types;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;
import net.dmulloy2.swornapi.types.SimpleVector;

import net.dmulloy2.swornapi.util.Validate;
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
	private final float pitch, yaw;
	private final String worldName;

	private transient World world;
	private transient Location location;
	private transient SimpleVector simpleVector;

	public ArenaLocation(String worldName, int x, int y, int z, float pitch, float yaw)
	{
		Validate.notNull(worldName, "worldName cannot be null!");
		this.worldName = worldName;
		this.x = x;
		this.y = y;
		this.z = z;
		this.pitch = pitch;
		this.yaw = yaw;
	}

	public ArenaLocation(String worldName, int x, int z)
	{
		this(worldName, x, 0, z, 0, 0);
	}

	public ArenaLocation(String worldName, int x, int y, int z)
	{
		this(worldName, x, y, z, 0, 0);
	}

	public ArenaLocation(Location location)
	{
		this(location.getWorld().getName(), location.getBlockX(), location.getBlockY(),
				location.getBlockZ(), location.getPitch(), location.getYaw());
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

		this.pitch = getFloat(args, "pitch");
		this.yaw = getFloat(args, "yaw");
	}

	// Safely gets the float from the map
	private float getFloat(Map<String, Object> map, String key)
	{
		if (map.containsKey(key))
		{
			Number number = (Number) map.get(key);
			return number.floatValue();
		}

		return 0.0F;
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
		location = new Location(world, x, y, z, yaw, pitch);
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
		return new ArenaLocation(l1.worldName, Math.max(l1.x, l2.x), Math.max(l1.y, l2.y), Math.max(l1.z, l2.z), 0, 0);
	}

	public static ArenaLocation getMinimum(ArenaLocation l1, ArenaLocation l2)
	{
		return new ArenaLocation(l1.worldName, Math.min(l1.x, l2.x), Math.min(l1.y, l2.y), Math.min(l1.z, l2.z), 0, 0);
	}

	public String toCommandString()
	{
		return String.format("%s, %s, %s", x, y, z);
	}

	@Override
	public Map<String, Object> serialize()
	{
		Map<String, Object> result = new LinkedHashMap<>();

		result.put("worldName", worldName);
		result.put("x", x);
		result.put("y", y);
		result.put("z", z);
		result.put("pitch", pitch);
		result.put("yaw", yaw);

		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this) return true;
		
		if (obj instanceof ArenaLocation)
		{
			ArenaLocation that = (ArenaLocation) obj;
			return x == that.x &&
					y == that.y &&
					z == that.z &&
					Float.floatToIntBits(pitch) == Float.floatToIntBits(that.pitch) &&
					Float.floatToIntBits(yaw) == Float.floatToIntBits(that.yaw) &&
					worldName.equals(that.worldName);
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(x, y, z, worldName, pitch, yaw);
	}

	@Override
	public String toString()
	{
		return "ArenaLocation[world=" + worldName + ", x=" + x + ", y=" + y +
				", z=" + z + ", pitch=" + pitch + ", yaw=" + yaw + "]";
	}

	@Override
	public ArenaLocation clone()
	{
		return new ArenaLocation(worldName, x, y, z, pitch, yaw);
	}
}
