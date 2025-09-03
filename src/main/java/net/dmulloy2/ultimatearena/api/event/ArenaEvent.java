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

import net.dmulloy2.swornapi.util.Validate;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Represents an event relating to arenas.
 *
 * @author dmulloy2
 */

public abstract class ArenaEvent extends Event
{
	private static final HandlerList handlers = new HandlerList();

	protected final Arena arena;

	public ArenaEvent(Arena arena)
	{
		Validate.notNull(arena, "arena cannot be null!");
		this.arena = arena;
	}

	/**
	 * Gets the {@link Arena} involved in this event.
	 * 
	 * @return The Arena, will not be null
	 */
	public final Arena getArena()
	{
		return arena;
	}

	@Override
	public HandlerList getHandlers()
	{
		return handlers;
	}

	public static HandlerList getHandlerList()
	{
		return handlers;
	}
}