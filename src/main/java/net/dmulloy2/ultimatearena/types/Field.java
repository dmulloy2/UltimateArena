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

import java.util.List;

import lombok.Getter;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

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

	public boolean isInside(Location loc)
	{
		if (! initialized)
			return false;

		Validate.notNull(loc, "loc cannot be null!");

		World world = loc.getWorld();
		if (getWorld().equals(world))
		{
			int locX = loc.getBlockX();
			int locZ = loc.getBlockZ();

			if (locX >= min.getX() && locX <= max.getX())
				return locZ >= min.getZ() && locZ <= max.getZ();
		}

		return false;
	}

	public Block getBlockAt(int x, int y, int z)
	{
		return getWorld().getBlockAt(min.getX() + x, min.getY() + y, min.getZ() + z);
	}

	public final void setType(Material mat)
	{
		World world = getWorld();
		for (int x = min.getX(); x < max.getX() + 1; x++)
		{
			for (int y = min.getY(); y < max.getY() + 1; y++)
			{
				for (int z = min.getZ(); z < max.getZ() + 1; z++)
				{
					Block block = world.getBlockAt(x, y, z);
					block.setType(mat);
				}
			}
		}
	}

	public void removeMaterials(List<Material> materials)
	{
		World world = getWorld();
		for (int x = min.getX(); x < max.getX() + 1; x++)
		{
			for (int y = min.getY(); y < max.getY() + 1; y++)
			{
				for (int z = min.getZ(); z < max.getZ() + 1; z++)
				{
					Block block = world.getBlockAt(x, y, z);
					if (materials.contains(block.getType()))
						block.setType(Material.AIR);
				}
			}
		}
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
