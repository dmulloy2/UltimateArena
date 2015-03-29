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

import lombok.Getter;

import org.bukkit.Location;

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

	public final boolean isUnder(Location loc)
	{
		return super.isInside(loc) && loc.getBlockY() < min.getY();
	}

	public final int getHeight()
	{
		return max.getY() - min.getY();
	}
}
