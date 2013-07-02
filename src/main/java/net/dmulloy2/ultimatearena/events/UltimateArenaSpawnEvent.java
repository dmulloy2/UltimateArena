package net.dmulloy2.ultimatearena.events;

import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaClass;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaPlayer;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaSpawn;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class UltimateArenaSpawnEvent extends Event
{
	private static final HandlerList handlers = new HandlerList();
	
	private final ArenaPlayer arenaPlayer;
	private final Arena arena;
	private final ArenaSpawn spawn;
	
	/**
	 * Called when a player spawns in an arena
	 * @param arenaPlayer - The ArenaPlayer that spawned
	 * @param arena - The Arena the player spawned in
	 * @param spawn - The spawn location
	 */
	public UltimateArenaSpawnEvent(final ArenaPlayer arenaPlayer, final Arena arena, final ArenaSpawn spawn)
	{
		this.arenaPlayer = arenaPlayer;
		this.arena = arena;
		this.spawn = spawn;
	}
	
	public ArenaPlayer getArenaPlayer()
	{
		return arenaPlayer;
	}
	
	public Arena getArena()
	{
		return arena;
	}
	
	public String getArenaType()
	{
		return arena.type;
	}
	
	public Player getPlayer()
	{
		return arenaPlayer.player;
	}
	
	public ArenaSpawn getSpawn()
	{
		return spawn;
	}
	
	public Location getArenaSpawnAsLocation()
	{
		return spawn.getLocation();
	}
	
	public ArenaClass getArenaClass()
	{
		return arenaPlayer.mclass;
	}
	
	@Override
	public HandlerList getHandlers()
	{
		return handlers;
	}
}