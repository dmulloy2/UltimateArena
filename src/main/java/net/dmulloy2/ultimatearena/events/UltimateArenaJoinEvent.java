package net.dmulloy2.ultimatearena.events;

import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaPlayer;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author dmulloy2
 */

public class UltimateArenaJoinEvent extends Event
{
	private static final HandlerList handlers = new HandlerList();
	
	private final ArenaPlayer arenaPlayer;
	private final Arena arena;
	
	/**
	 * Called when a player joins an arena
	 * @param arenaPlayer - The ArenaPlayer that joined
	 * @param arena - The Arena the player joined
	 */
	public UltimateArenaJoinEvent(final ArenaPlayer arenaPlayer, final Arena arena)
	{
		this.arenaPlayer = arenaPlayer;
		this.arena = arena;
	}
	
	/**
	 * Get the ArenaPlayer who joined
	 * @return The ArenaPlayer who joined
	 */
	public ArenaPlayer getArenaPlayer()
	{
		return arenaPlayer;
	}
	
	/**
	 * Get the Arena joined
	 * @return The arena joined
	 */
	public Arena getArena()
	{
		return arena;
	}
	
	/**
	 * Get the type of the Arena joined
	 * @return The type of the arena joined
	 */
	public String getArenaType()
	{
		return arena.getType();
	}
	
	/**
	 * Get the player that joined
	 * @return The player that joined
	 */
	public Player getPlayer()
	{
		return arenaPlayer.getPlayer();
	}

	@Override
	public HandlerList getHandlers()
	{
		return handlers;
	}
}