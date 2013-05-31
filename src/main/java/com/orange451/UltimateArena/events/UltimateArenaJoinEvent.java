package com.orange451.UltimateArena.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.orange451.UltimateArena.Arenas.Arena;
import com.orange451.UltimateArena.Arenas.Objects.ArenaPlayer;

public class UltimateArenaJoinEvent extends Event
{
	private static final HandlerList handlers = new HandlerList();
	
	public final ArenaPlayer arenaPlayer;
	public final Arena arena;
	
	public UltimateArenaJoinEvent(final ArenaPlayer arenaPlayer, final Arena arena)
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

	@Override
	public HandlerList getHandlers()
	{
		return handlers;
	}
}