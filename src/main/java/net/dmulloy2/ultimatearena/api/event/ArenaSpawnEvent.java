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
package net.dmulloy2.ultimatearena.api.event;

import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;

/**
 * Called when a player is spawned in an Arena.
 * @author dmulloy2
 */

public final class ArenaSpawnEvent extends ArenaPlayerEvent
{
	private Location location;

	public ArenaSpawnEvent(Arena arena, ArenaPlayer player, Location location)
	{
		super(arena, player);

		Validate.notNull(location, "location cannot be null!");
		this.location = location;
	}

	/**
	 * Gets the current spawn Location.
	 * 
	 * @return The Location
	 */
	public Location getLocation()
	{
		return location;
	}

	/**
	 * Sets the spawn Location.
	 * 
	 * @param location The new location, cannot be null
	 */
	public void setLocation(Location location)
	{
		Validate.notNull(location, "location cannot be null!");
		this.location = location;
	}
}