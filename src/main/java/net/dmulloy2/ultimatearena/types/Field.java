package net.dmulloy2.ultimatearena.types;

import org.bukkit.Location;
import org.bukkit.World;

/**
 * @author dmulloy2
 */

public class Field
{
	protected boolean initialized;
	
	protected ArenaLocation max;
	protected ArenaLocation min;

	protected int minx;
	protected int maxx;

	protected int minz;
	protected int maxz;

	private int width;
	private int length;

	public Field(ArenaLocation max, ArenaLocation min)
	{
		setParam(max, min);
	}

	public Field()
	{
	}

	public void setParam(ArenaLocation max, ArenaLocation min)
	{
		this.max = max;
		this.min = min;

		this.maxx = max.getX();
		this.minx = min.getX();

		this.maxz = max.getZ();
		this.minz = min.getZ();

		if (minx > maxx)
		{
			this.max = min;
			this.min = max;
			this.maxx = min.getX();
			this.minx = max.getX();
		}

		if (minz > maxz)
		{
			this.min = max;
			this.max = min;
			this.maxz = min.getX();
			this.minz = max.getX();
		}

		this.length = maxx - minx;
		this.width = maxz - minz;

		this.initialized = true;
	}

	public boolean isInside(Location loc)
	{
		if (! initialized)
			return false;
		
		World locw = loc.getWorld();
		int locx = loc.getBlockX();
		int locz = loc.getBlockZ();
		if (getWorld().getUID() == locw.getUID())
		{
			if (locx >= minx && locx <= maxx)
			{
				return locz >= minz && locz <= maxz;
			}
		}

		return false;
	}

	protected final World getWorld()
	{
		return max.getWorld();
	}

	public final ArenaLocation getMax()
	{
		return max;
	}

	public final ArenaLocation getMin()
	{
		return min;
	}

	public final int getArea()
	{
		return getLength() * getWidth();
	}

	public final int getLength()
	{
		return Math.abs(length);
	}

	public final int getWidth()
	{
		return Math.abs(width);
	}
}