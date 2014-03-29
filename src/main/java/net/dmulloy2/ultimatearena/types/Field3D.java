package net.dmulloy2.ultimatearena.types;

import lombok.Getter;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * @author dmulloy2
 */

@Getter
public class Field3D extends Field
{
	protected int maxY;
	protected int minY;

	public Field3D(ArenaLocation point1, ArenaLocation point2)
	{
		setParam(point1, point2);
	}

	public Field3D()
	{

	}

	@Override
	public void setParam(ArenaLocation point1, ArenaLocation point2)
	{
		super.setParam(point1, point2);

		this.maxY = point1.getY();
		this.minY = point2.getY();

		if (minY > maxY)
		{
			this.maxY = point2.getY();
			this.minY = point1.getY();
		}
	}

	@Override
	public boolean isInside(Location loc)
	{
		if (super.isInside(loc))
		{
			int locY = loc.getBlockY();
			return locY >= minY && locY <= maxY;
		}

		return false;
	}

	public final Block getBlockAt(int x, int y, int z)
	{
		return getWorld().getBlockAt(minX + x, minY + y, minZ + z);
	}

	public final boolean isUnder(Location loc)
	{
		if (super.isInside(loc))
		{
			return loc.getBlockY() < minY;
		}

		return false;
	}

	public final void setType(Material mat)
	{
		for (int x = minX; x <= maxX; x++)
		{
			for (int y = minY; y <= maxY; y++)
			{
				for (int z = minZ; z <= maxZ; z++)
				{
					Block b = getWorld().getBlockAt(x, y, z);
					b.setType(mat);
				}
			}
		}
	}

	public final int getHeight()
	{
		return maxY - minY;
	}
}