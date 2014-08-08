package net.dmulloy2.ultimatearena.types;

import lombok.Getter;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * @author dmulloy2
 */

@Getter
public class Field
{
	protected boolean initialized;

	protected ArenaLocation max;
	protected ArenaLocation min;

	public Field() { }

	public Field(ArenaLocation point1, ArenaLocation point2)
	{
		setParam(point1, point2);
	}

	public void setParam(ArenaLocation point1, ArenaLocation point2)
	{
		Validate.notNull(point1, "point1 cannot be null!");
		Validate.notNull(point2, "point2 cannot be null!");

		this.max = ArenaLocation.getMaximum(point1, point2);
		this.min = ArenaLocation.getMinimum(point1, point2);
		this.initialized = true;
	}

	public boolean isInside(ArenaLocation loc)
	{
		return isInside(loc.getLocation());
	}

	public boolean isInside(Location loc)
	{
		if (! initialized)
			return false;

		World world = loc.getWorld();
		int locX = loc.getBlockX();
		int locZ = loc.getBlockZ();
		if (getWorld().getUID() == world.getUID())
		{
			if (locX >= min.getX() && locX <= max.getX())
				return locZ >= min.getZ() && locZ <= max.getZ();
		}

		return false;
	}

	public final World getWorld()
	{
		return max.getWorld();
	}

	public final int getWidth()
	{
		return max.getX() - min.getX();
	}

	public final int getLength()
	{
		return max.getZ() - min.getZ();
	}
}