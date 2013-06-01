package com.orange451.UltimateArena.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import com.orange451.UltimateArena.Arenas.Arena;
import com.orange451.UltimateArena.Arenas.Objects.ArenaPlayer;

public class UltimateArenaKillEvent extends Event
{
	private static final HandlerList handlers = new HandlerList();
	
	public final ArenaPlayer killer;
	public final ArenaPlayer killed;
	public final Arena arena;
	
	/**
	 * Called when a player kills another player
	 * @param killer - The ArenaPlayer that killed
	 * @param killed - The ArenaPlayer that was killed
	 * @param arena - The Arena the player joined
	 */
	public UltimateArenaKillEvent(final ArenaPlayer killed, final ArenaPlayer killer, final Arena arena)
	{
		this.killed = killed;
		this.killer = killer;
		this.arena = arena;
	}
	
	public ArenaPlayer getKiller()
	{
		return killer;
	}
	
	public ArenaPlayer getKilled()
	{
		return killed;
	}
	
	public Arena getArena()
	{
		return arena;
	}
	
	public String getArenaType()
	{
		return arena.type;
	}
	
	public Player getKillerPlayer()
	{
		return killer.player;
	}
	
	public Player getKilledPlayer()
	{
		return killed.player;
	}
	
	public ItemStack getWeapon()
	{
		return killer.player.getItemInHand();
	}
	
	public int getHeartsLeft()
	{
		return killer.player.getHealth() / 2;
	}

	@Override
	public HandlerList getHandlers()
	{
		return handlers;
	}
}