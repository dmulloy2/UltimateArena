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

import lombok.Getter;
import lombok.Setter;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Represents an event relating to arenas.
 *
 * @author dmulloy2
 */

@Getter
public class ArenaEvent extends PlayerEvent implements Cancellable
{
	private static final HandlerList handlers = new HandlerList();
	private @Setter String cancelMessage;
	private @Setter boolean cancelled;

	private ArenaPlayer arenaPlayer;
	private Arena arena;

	public ArenaEvent(Player player, ArenaPlayer arenaPlayer, Arena arena)
	{
		super(player);
		this.arenaPlayer = arenaPlayer;
		this.arena = arena;
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
