package com.orange451.UltimateArena.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.orange451.UltimateArena.Arenas.Arena;
import com.orange451.UltimateArena.Arenas.Objects.ArenaClass;
import com.orange451.UltimateArena.Arenas.Objects.ArenaPlayer;

public class UltimateArenaDeathEvent extends Event
{
	private static final HandlerList handlers = new HandlerList();
	
	public final ArenaPlayer arenaPlayer;
	public final Arena arena;
	
	/**
	 * Called when a player dies in an arena
	 * @param arenaPlayer - The arena player that died
	 * @param arena - The arena the player was in
	 */
	public UltimateArenaDeathEvent(final ArenaPlayer arenaPlayer, final Arena arena)
	{
		this.arenaPlayer = arenaPlayer;
		this.arena = arena;
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
	
	public ArenaClass getArenaClass()
	{
		return arenaPlayer.mclass;
	}
	
	public int getDeaths()
	{
		return arenaPlayer.deaths;
	}
	
	@Override
	public HandlerList getHandlers()
	{
		return handlers;
	}
}