package com.orange451.UltimateArena.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.orange451.UltimateArena.Arenas.Arena;
import com.orange451.UltimateArena.Arenas.Objects.ArenaClass;
import com.orange451.UltimateArena.Arenas.Objects.ArenaPlayer;
import com.orange451.UltimateArena.Arenas.Objects.ArenaSpawn;

public class UltimateArenaSpawnEvent extends Event
{
	private static final HandlerList handlers = new HandlerList();
	
	public final ArenaPlayer arenaPlayer;
	public final Arena arena;
	public final ArenaSpawn spawn;
	
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