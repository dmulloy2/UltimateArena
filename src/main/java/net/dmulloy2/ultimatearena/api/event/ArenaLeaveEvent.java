/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.ultimatearena.api.event;

import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * Called when a player leaves an arena. Note that this event isn't actually
 * cancellable.
 *
 * @author dmulloy2
 */

public class ArenaLeaveEvent extends ArenaEvent
{
	private static final HandlerList handlers = new HandlerList();

	public ArenaLeaveEvent(Player player, ArenaPlayer arenaPlayer, Arena arena)
	{
		super(player, arenaPlayer, arena);
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