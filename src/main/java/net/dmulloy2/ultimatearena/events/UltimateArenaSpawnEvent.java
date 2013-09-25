package net.dmulloy2.ultimatearena.events;

import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaClass;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.FieldType;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when a {@link Player} spawns in a {@link Arena}
 * 
 * @author dmulloy2
 */

public class UltimateArenaSpawnEvent extends Event
{
	private static final HandlerList handlers = new HandlerList();

	private final ArenaPlayer arenaPlayer;
	private final Arena arena;
	private final Location spawn;
	private final boolean lobby;

	public UltimateArenaSpawnEvent(final ArenaPlayer arenaPlayer, final Arena arena, final Location spawn, final boolean lobby)
	{
		this.arenaPlayer = arenaPlayer;
		this.arena = arena;
		this.spawn = spawn;
		this.lobby = lobby;
	}

	/**
	 * Gets the {@link ArenaPlayer} who spawned
	 * 
	 * @return The {@link ArenaPlayer} who spawned
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
	 * Gets the player's spawn {@link Location}
	 * 
	 * @return The player's spawn {@link Location}
	 */
	public Location getSpawn()
	{
		return spawn;
	}
	
	/**
	 * Gets whether or not the spawn occured in the lobby
	 * 
	 * @return Whether or not the spawn occured in the lobby
	 */
	public final boolean isLobbySpawn()
	{
		return lobby;
	}

	/**
	 * Gets the player's {@link ArenaClass}
	 * 
	 * @return The player's {@link ArenaClass}
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