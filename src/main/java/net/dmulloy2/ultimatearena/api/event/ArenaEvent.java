/**
 * (c) 2015 dmulloy2
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