package net.dmulloy2.ultimatearena.types;

import lombok.Getter;

import org.bukkit.Location;
import org.bukkit.World;

/**
 * @author dmulloy2
 */

@Getter
public class Field
{
	protected boolean initialized;

	protected ArenaLocation point1;
	protected ArenaLocation point2;

	protected int maxX;
	protected int maxZ;

	protected int minX;
	protected int minZ;

	public Field(ArenaLocation point1, ArenaLocation point2)
	{
		setParam(point1, point2);
	}

	public Field()
	{
		//
	}

	public void setParam(ArenaLocation point1, ArenaLocation point2)
	{
		this.point1 = point1;
		this.point2 = point2;

		this.maxX = point1.getX();
		this.maxZ = point1.getZ();

		this.minX = point2.getX();
		this.minZ = point2.getZ();

		if (minX > maxX)
		{
			this.maxX = point2.getX();
			this.minX = point1.getX();
		}

		if (minZ > maxZ)
		{
			this.maxZ = point2.getZ();
			this.minZ = point1.getZ();
		}

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
			if (locX >= minX && locX <= maxX)
			{
				return locZ >= minZ && locZ <= maxZ;
			}
		}

		return false;
	}

	public final World getWorld()
	{
		return point1.getWorld();
	}

	public final int getWidth()
	{
		return maxX - minX;
	}

	public final int getLength()
	{
		return maxZ - minZ;
	}
}