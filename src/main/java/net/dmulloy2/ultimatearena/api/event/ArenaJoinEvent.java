/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.ultimatearena.api.event;

import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * Called when a player joins an arena. If the event is cancelled, it is
 * recommended that you provide a {@code cancelMessage}.
 *
 * @author dmulloy2
 */

public class ArenaJoinEvent extends ArenaEvent
{
	private static final HandlerList handlers = new HandlerList();

	public ArenaJoinEvent(Player player, ArenaPlayer arenaPlayer, Arena arena)
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