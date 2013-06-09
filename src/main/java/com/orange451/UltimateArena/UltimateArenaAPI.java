package com.orange451.UltimateArena;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.orange451.UltimateArena.Arenas.Objects.ArenaPlayer;

public class UltimateArenaAPI 
{
	public UltimateArena plugin;
	public UltimateArenaAPI(Plugin p)
	{
		this.plugin = (UltimateArena)p;
	}

	/**
	 * @param p - Player in question
	 * @return whether or not they're playing in an arena (boolean)
	 */
	public boolean isPlayerPlayingArena(Player p)
	{
		return plugin.isInArena(p);
	}
	
	/**
	 * @param p - Player in question
	 * @return whether or not they are standing in an arena (boolean)
	 */
	public boolean isPlayerInArenaLocation(Player p)
	{
		return plugin.isInArena(p.getLocation());
	}
	
	/**
	 * @param p - Player instance
	 * @return - The player's ArenaPlayer instance
	 */
	public ArenaPlayer getArenaPlayer(Player p)
	{
		return plugin.getArenaPlayer(p);
	}
	
	/**
	 * @param loc - Location in question
	 * @return - Whether or not an arena is at that location
	 */
	public boolean isLocationInArena(Location loc)
	{
		return plugin.isInArena(loc);
	}

	/**
	 * @param a - ArenaPlayer
	 * @return - The ArenaPlayer's kills
	 */
	public int getKills(ArenaPlayer a)
	{
		return a.kills;
	}
	
	/**
	 * @param a - ArenaPlayer
	 * @return - The ArenaPlayer's deaths
	 */
	public int getDeaths(ArenaPlayer a)
	{
		return a.deaths;
	}
	
	/**
	 * @param a - ArenaPlayer
	 * @return - The ArenaPlayer's team
	 */
	public int getTeam(ArenaPlayer a)
	{
		return a.team;
	}
	
	/**
	 * @param a - ArenaPlayer
	 * @return - The ArenaPlayer's killstreak
	 */
	public int getKillStreak(ArenaPlayer a)
	{
		return a.killstreak;
	}
	
	/**
	 * @return UltimateArena plugin
	 */
	public UltimateArena getPlugin()
	{
		return plugin;
	}
	
	/**Find the UltimateArena plugin and hook into its API**/
	public static UltimateArenaAPI hookIntoUA()
	{
		Plugin p = Bukkit.getPluginManager().getPlugin("UltimateArena");
		if (p != null)
		{
			return new UltimateArenaAPI(p);
		}
		else
		{
			Bukkit.getLogger().severe("Could not hook into UltimateArena! Is it installed?");
		}
		return null;
	}
}