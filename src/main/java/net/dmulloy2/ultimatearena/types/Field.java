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

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import lombok.Getter;

/**
 * @author dmulloy2
 */

@Getter
public class Field
{
	protected boolean init;
	protected ArenaLocation min;
	protected ArenaLocation max;

	public Field(ArenaLocation loc1, ArenaLocation loc2)
	{
		setParam(loc1, loc2);
	}

	public Field() { }

	public void setParam(ArenaLocation loc1, ArenaLocation loc2)
	{
		Validate.notNull(loc1, "loc1 cannot be null!");
		Validate.notNull(loc2, "loc2 cannot be null!");

		this.min = ArenaLocation.getMinimum(loc1, loc2);
		this.max = ArenaLocation.getMaximum(loc1, loc2);
		this.init = true;
	}

	public World getWorld()
	{
		return max.getWorld();
	}

	public boolean isInside(Location loc)
	{
		Validate.notNull(loc, "loc cannot be null!");

		int x = loc.getBlockX();
		int z = loc.getBlockZ();

		return init
				&& loc.getWorld().equals(getWorld())
				&& x <= max.getX() && x >= min.getX()
				&& z <= max.getZ() && z >= min.getZ();
	}

	private boolean isInside(ArenaLocation loc)
	{
		return isInside(loc.getLocation());
	}

	public boolean checkOverlap(Field that)
	{
		return isInside(that.min) || isInside(that.max) || that.checkOverlap(this, false);
	}

	private boolean checkOverlap(Field that, boolean recurse)
	{
		return isInside(that.min) || isInside(that.max) || (recurse && that.checkOverlap(this));
	}

	public void removeMaterials(List<Material> clear)
	{
		for (int x = min.getX(); x <= max.getX(); x++)
		{
			for (int y = min.getY(); y <= max.getY(); y++)
			{
				for (int z = min.getZ(); z <= max.getZ(); z++)
				{
					Block block = getWorld().getBlockAt(x, y, z);
					if (clear.contains(block.getType()))
						block.setType(Material.AIR);
				}
			}
		}
	}

	public int getWidth()
	{
		return max.getX() - min.getX();
	}

	public int getLength()
	{
		return max.getZ() - min.getZ();
	}
}
