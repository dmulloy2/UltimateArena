package net.dmulloy2.ultimatearena.types;

import lombok.Getter;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * @author dmulloy2
 */

@Getter
public final class Field3D extends Field
{
	public Field3D() { }

	public Field3D(ArenaLocation point1, ArenaLocation point2)
	{
		super(point1, point2);
	}

	@Override
	public boolean isInside(Location loc)
	{
		if (super.isInside(loc))
		{
			int locY = loc.getBlockY();
			return locY >= min.getY() && locY <= max.getY();
		}

		return false;
	}

	public final Block getBlockAt(int x, int y, int z)
	{
		return getWorld().getBlockAt(min.getX() + x, min.getY() + y, min.getZ() + z);
	}

	public final boolean isUnder(Location loc)
	{
		return super.isInside(loc) && loc.getBlockY() < min.getY();
	}

	public final void setType(Material mat)
	{
		World world = getWorld();
		for (int x = min.getX(); x <= max.getX(); x++)
		{
			for (int y = min.getY(); y <= max.getY(); y++)
			{
				for (int z = min.getZ(); z <= max.getZ(); z++)
				{
					Block block = world.getBlockAt(x, y, z);
					block.setType(mat);
				}
			}
		}
	}

	public final int getHeight()
	{
		return max.getY() - min.getY();
	}
}