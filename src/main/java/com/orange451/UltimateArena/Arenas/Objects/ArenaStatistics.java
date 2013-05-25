package com.orange451.UltimateArena.Arenas.Objects;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ArenaStatistics {
	public ArenaZone arena;
	public double percentagePlayed;
	public int totalPlays;
	public int plays;
	public int likes;
	public int dislikes;
	
	public ArenaStatistics(ArenaZone az) {
		this.arena = az;
		stats();
	}
	
	public void stats() {
		//calculates all the arenas stats
		this.totalPlays = arena.plugin.arenasPlayed;
		this.plays = arena.timesPlayed;
		//this.percentagePlayed = (int)(((int) ((double)plays/(double)totalPlays) * 100) / 100.0);
		this.percentagePlayed = (int) (((double)plays/(double)totalPlays) * 100);
		this.likes = arena.liked;
		this.dislikes = arena.disliked;
	}
	
	public void dumpStats(Player p) {
		//dumps the arenas stats to the player
		p.sendMessage(ChatColor.DARK_GRAY + "Stats on arena: " + ChatColor.GOLD + arena.arenaName);
		p.sendMessage(ChatColor.GRAY + "Plays: " + Integer.toString(plays) + "/" + Integer.toString(totalPlays));
		p.sendMessage(ChatColor.RED + "    " + Double.toString(percentagePlayed) + "%");
		p.sendMessage(ChatColor.GRAY + "Likes:  " + ChatColor.GREEN + Integer.toString(likes));
		p.sendMessage(ChatColor.GRAY + "Disikes:" + ChatColor.RED + Integer.toString(dislikes));
	}
}
