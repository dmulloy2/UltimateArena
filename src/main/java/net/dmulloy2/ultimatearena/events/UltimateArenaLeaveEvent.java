package net.dmulloy2.ultimatearena.events;

import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaPlayer;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author dmulloy2
 */

public class UltimateArenaLeaveEvent extends Event
{
	private static final HandlerList handlers = new HandlerList();
	
	private final ArenaPlayer arenaPlayer;
	private final Arena arena;
	
	/**
	 * Called when a player leaves an arena
	 * @param arenaPlayer - The ArenaPlayer that left
	 * @param arena - The Arena the player left
	 */
	public UltimateArenaLeaveEvent(final ArenaPlayer arenaPlayer, final Arena arena)
	{
		this.arenaPlayer = arenaPlayer;
		this.arena = arena;
	}
	
	/**
	 * Get the ArenaPlayer who left
	 * @return ArenaPlayer who left
	 */
	public ArenaPlayer getArenaPlayer()
	{
		return arenaPlayer;
	}
	
	/**
	 * Get the Arena the player left
	 * @return Arena the player left
	 */
	public Arena getArena()
	{
		return arena;
	}
	
	/**
	 * Get the type of the arena the player left
	 * @return Type of the arena the player left
	 */
	public String getArenaType()
	{
		return arena.getType();
	}
	
	/**
	 * Get the Player who left
	 * @return Player who left
	 */
	public Player getPlayer()
	{
		return arenaPlayer.getPlayer();
	}
	
	/**
	 * Get whether or not the player is on the winning team
	 * @return Whether or not the player is on the winning team
	 */
	public boolean onWinningTeam()
	{
		return arenaPlayer.getTeam() == arena.getWinningTeam();
	}

	@Override
	public HandlerList getHandlers()
	{
		return handlers;
	}
}