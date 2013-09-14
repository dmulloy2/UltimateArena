package net.dmulloy2.ultimatearena.events;

import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaClass;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.ArenaSpawn;
import net.dmulloy2.ultimatearena.types.FieldType;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author dmulloy2
 */

public class UltimateArenaSpawnEvent extends Event
{
	private static final HandlerList handlers = new HandlerList();

	private final ArenaPlayer arenaPlayer;
	private final Arena arena;
	private final ArenaSpawn spawn;

	/**
	 * Called when a player spawns in an arena
	 * 
	 * @param arenaPlayer
	 *            - The ArenaPlayer that spawned
	 * @param arena
	 *            - The Arena the player spawned in
	 * @param spawn
	 *            - The spawn location
	 */
	public UltimateArenaSpawnEvent(final ArenaPlayer arenaPlayer, final Arena arena, final ArenaSpawn spawn)
	{
		this.arenaPlayer = arenaPlayer;
		this.arena = arena;
		this.spawn = spawn;
	}

	/**
	 * Get the ArenaPlayer getting spawned
	 * 
	 * @return ArenaPlayer getting spawned
	 */
	public ArenaPlayer getArenaPlayer()
	{
		return arenaPlayer;
	}

	/**
	 * Get the Arena the ArenaPlayer is in
	 * 
	 * @return Arena the ArenaPlayer is in
	 */
	public Arena getArena()
	{
		return arena;
	}

	/**
	 * Get the type of the arena
	 * 
	 * @return Type of the arena
	 */
	public FieldType getArenaType()
	{
		return arena.getType();
	}

	/**
	 * Get the Player getting spawned
	 * 
	 * @return Player getting spawned
	 */
	public Player getPlayer()
	{
		return arenaPlayer.getPlayer();
	}

	/**
	 * Get the ArenaSpawn
	 * 
	 * @return ArenaSpawn
	 */
	public ArenaSpawn getSpawn()
	{
		return spawn;
	}

	/**
	 * Get the Location
	 * 
	 * @return Location
	 */
	public Location getArenaSpawnAsLocation()
	{
		return spawn.getLocation();
	}

	/**
	 * Get the player's ArenaClass
	 * 
	 * @return Player's ArenaClass
	 */
	public ArenaClass getArenaClass()
	{
		return arenaPlayer.getArenaClass();
	}

	@Override
	public HandlerList getHandlers()
	{
		return handlers;
	}
}