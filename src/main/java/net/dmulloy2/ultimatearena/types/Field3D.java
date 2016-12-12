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

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import lombok.Getter;

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

	public boolean isUnder(Location loc)
	{
		return super.isInside(loc) && loc.getBlockY() < min.getY();
	}

	public int getHeight()
	{
		return max.getY() - min.getY();
	}

	public Block getBlockAt(int x, int y, int z)
	{
		return getWorld().getBlockAt(min.getX() + x, min.getY() + y, min.getZ() + z);
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
}