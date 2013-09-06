package net.dmulloy2.ultimatearena.events;

import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaClass;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.FieldType;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class UltimateArenaDeathEvent extends Event
{
	private static final HandlerList handlers = new HandlerList();

	private final ArenaPlayer arenaPlayer;
	private final Arena arena;

	/**
	 * Called when a player dies in an arena
	 * 
	 * @param arenaPlayer
	 *            - The arena player that died
	 * @param arena
	 *            - The arena the player was in
	 */
	public UltimateArenaDeathEvent(final ArenaPlayer arenaPlayer, final Arena arena)
	{
		this.arenaPlayer = arenaPlayer;
		this.arena = arena;
	}

	/**
	 * Get the dead ArenaPlayer
	 * 
	 * @return Dead player's ArenaPlayer instance
	 */
	public ArenaPlayer getArenaPlayer()
	{
		return arenaPlayer;
	}

	/**
	 * Get the arena the event occured in
	 * 
	 * @return The arena the event occured in
	 */
	public Arena getArena()
	{
		return arena;
	}

	/**
	 * Gets the type of the arena in which the event occured
	 * 
	 * @return The type of the arena in which the event occured
	 */
	public FieldType getArenaType()
	{
		return arena.getType();
	}

	/**
	 * Gets the dead player
	 * 
	 * @return The dead player
	 */
	public Player getPlayer()
	{
		return arenaPlayer.getPlayer();
	}

	/**
	 * Gets the class the dead player had
	 * 
	 * @return The class the dead player had
	 */
	public ArenaClass getArenaClass()
	{
		return arenaPlayer.getArenaClass();
	}

	/**
	 * Gets the total deaths of the dead player
	 * 
	 * @return The total deaths of the dead player
	 */
	public int getDeaths()
	{
		return arenaPlayer.getDeaths();
	}

	@Override
	public HandlerList getHandlers()
	{
		return handlers;
	}
}