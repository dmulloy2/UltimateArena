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

	public boolean isPlayerPlayingArena(Player p)
	{
		return plugin.isInArena(p);
	}
	
	public boolean isPlayerInArenaLocation(Player p)
	{
		return plugin.isInArena(p.getLocation());
	}
	
	public ArenaPlayer getArenaPlayer(Player p)
	{
		return plugin.getArenaPlayer(p);
	}
	
	public boolean isLocationInArena(Location loc)
	{
		return plugin.isInArena(loc);
	}

	public int getKills(ArenaPlayer a)
	{
		return a.kills;
	}
	
	public int getDeaths(ArenaPlayer a)
	{
		return a.deaths;
	}
	
	public int getTeam(ArenaPlayer a)
	{
		return a.team;
	}
	
	public int getKillStreak(ArenaPlayer a)
	{
		return a.killstreak;
	}
	
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