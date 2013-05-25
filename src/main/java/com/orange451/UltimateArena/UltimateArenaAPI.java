package com.orange451.UltimateArena;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.orange451.UltimateArena.Arenas.Objects.ArenaPlayer;

public class UltimateArenaAPI {
	
	public UltimateArena plugin;

	public UltimateArenaAPI(Plugin p) {
		this.plugin = (UltimateArena)p;
	}

	public boolean isPlayerPlayingArena(Player p) {
		return plugin.isInArena(p);
	}
	
	public boolean isPlayerInArenaLocation(Player p) {
		return plugin.isInArena(p.getLocation());
	}
	
	public ArenaPlayer getArenaPlayer(Player p) {
		return plugin.getArenaPlayer(p);
	}
	
	public boolean isLocationInArena(Location loc) {
		return plugin.isInArena(loc);
	}

	public int getKills(ArenaPlayer a) {
		return a.kills;
	}
	
	public int getDeaths(ArenaPlayer a) {
		return a.deaths;
	}
	
	public int getTeam(ArenaPlayer a) {
		return a.team;
	}
	
	public int getKillStreak(ArenaPlayer a) {
		return a.killstreak;
	}
	
	public UltimateArena getPlugin() {
		return plugin;
	}
	
	public static UltimateArenaAPI hookIntoUA() { //this method finds the UA plugin, and hooks into its API
		Plugin p = Bukkit.getPluginManager().getPlugin("UltimateArena");
		if (p != null) {
			//found the UA plugin;
			return new UltimateArenaAPI(p);
		}else{
			System.out.println("Could not hook into UltimateArena! Is it installed?");
		}
		return null;
	}

}
