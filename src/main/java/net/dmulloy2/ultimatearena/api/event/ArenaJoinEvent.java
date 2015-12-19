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

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Called when a player joins an arena. If the event is cancelled, it is
 * recommended that you provide a {@code cancelMessage}.
 *
 * @author dmulloy2
 */

public final class ArenaJoinEvent extends ArenaPlayerEvent implements Cancellable
{
	private static final HandlerList handlers = new HandlerList();

	private String cancelMessage;
	private boolean cancelled;

	public ArenaJoinEvent(Arena arena, ArenaPlayer player)
	{
		super(arena, player);
	}

	/**
	 * Sets the cancellation state of this event. Cancelling the join event
	 * prevents a player from joining the Arena. If you cancel this event, you
	 * should set the {@code cancelMessage} so players aren't confused!
	 * 
	 * @param cancel cancellation state
	 */
	@Override
	public void setCancelled(boolean cancel)
	{
		this.cancelled = cancel;
	}

	/**
	 * Gets the cancellation state of this event.
	 * 
	 * @return True if it is cancelled, false if not.
	 */
	@Override
	public boolean isCancelled()
	{
		return cancelled;
	}

	/**
	 * Sets this event's cancel message. The cancel message displays if the
	 * event is cancelled.
	 * 
	 * @param message cancel message, shouldn't be empty
	 */
	public void setCancelMessage(String message)
	{
		this.cancelMessage = message;
	}

	/**
	 * Gets the cancel message of this event.
	 * 
	 * @return The cancel message, may be null
	 */
	public String getCancelMessage()
	{
		return cancelMessage;
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