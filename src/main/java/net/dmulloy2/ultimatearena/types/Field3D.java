package net.dmulloy2.ultimatearena.types;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * @author dmulloy2
 */

public class Field3D extends Field
{
	protected int miny;
	protected int maxy;

	private int height;

	public Field3D(ArenaLocation max, ArenaLocation min)
	{
		setParam(max, min);
	}

	public Field3D()
	{
	}

	@Override
	public void setParam(ArenaLocation max, ArenaLocation min)
	{
		super.setParam(max, min);

		this.maxy = max.getY();
		this.miny = min.getY();

		if (miny > maxy)
		{
			this.maxy = min.getY();
			this.miny = max.getY();
		}

		this.height = maxy - miny;
	}

	public Block getBlockAt(int x, int y, int z)
	{
		return getWorld().getBlockAt(minx + x, miny + y, minz + z);
	}

	@Override
	public boolean isInside(Location loc)
	{
		if (super.isInside(loc))
		{
			int locy = loc.getBlockY();
			return locy >= miny && locy <= maxy;
		}

		return false;
	}

	public boolean isUnder(Location loc)
	{
		if (super.isInside(loc))
		{
			return loc.getBlockY() < miny;
		}

		return false;
	}

	public void setType(final Material mat)
	{
		for (int x = minx; x <= maxx; x++)
		{
			for (int y = miny; y <= maxy; y++)
			{
				for (int z = minz; z <= maxz; z++)
				{
					Block b = getWorld().getBlockAt(x, y, z);
					b.setType(mat);
				}
			}
		}
	}

	public int getVolume()
	{
		return getHeight() * getArea();
	}

	public int getHeight()
	{
		return Math.abs(height);
	}
}