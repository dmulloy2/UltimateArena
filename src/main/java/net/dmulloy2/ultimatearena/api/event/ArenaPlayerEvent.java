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
import org.bukkit.entity.Player;

/**
 * ArenaEvents involving players
 * @author dmulloy2
 */

public abstract class ArenaPlayerEvent extends ArenaEvent
{
	protected final ArenaPlayer player;

	public ArenaPlayerEvent(Arena arena, ArenaPlayer player)
	{
		super(arena);

		Validate.notNull(player, "player cannot be null!");
		this.player = player;
	}

	/**
	 * Gets the Bukkit Player involved in this event.
	 * 
	 * @return The player, will not be null
	 */
	public final Player getPlayer()
	{
		return player.getPlayer();
	}

	/**
	 * Gets the ArenaPlayer involved in this event.
	 * 
	 * @return The arena player, will not be null
	 */
	public final ArenaPlayer getArenaPlayer()
	{
		return player;
	}
}