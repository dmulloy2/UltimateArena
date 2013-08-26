package net.dmulloy2.ultimatearena.arenas.objects;

import net.dmulloy2.ultimatearena.util.FormatUtil;

import org.apache.commons.lang.WordUtils;
import org.bukkit.entity.Player;

public class ArenaStatistics 
{
	private ArenaZone arena;
	
	private double percentagePlayed;
	
	private int totalPlays;
	private int plays;
	private int likes;
	private int dislikes;
	
	public ArenaStatistics(ArenaZone az)
	{
		this.arena = az;
		stats();
	}
	
	public void stats() 
	{
		// Calculates all the arenas stats
		this.totalPlays = arena.getPlugin().arenasPlayed;
		this.plays = arena.getTimesPlayed();
		this.percentagePlayed = (int) (((double)plays/(double)totalPlays) * 100);
		this.likes = arena.getLiked();
		this.dislikes = arena.getDisliked();
	}
	
	public void dumpStats(Player p) 
	{
		// Dumps the arenas stats to the player
		p.sendMessage(FormatUtil.format("&3====[ &e{0} &3]====", WordUtils.capitalize(arena.getArenaName())));
		p.sendMessage(FormatUtil.format("&bPlays: &e{0}&b/&e{1} &b(&e{2}%&b)", plays, totalPlays, percentagePlayed));
		p.sendMessage(FormatUtil.format("&bLikes: &e{0}", likes));
		p.sendMessage(FormatUtil.format("&bDisikes: &e{0}", dislikes));
	}
}