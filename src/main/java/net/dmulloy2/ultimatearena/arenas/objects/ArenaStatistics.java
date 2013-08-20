package net.dmulloy2.ultimatearena.arenas.objects;

import net.dmulloy2.ultimatearena.util.FormatUtil;

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
		p.sendMessage(FormatUtil.format("&8Stats on arena: &6{0}", arena.getArenaName()));
		p.sendMessage(FormatUtil.format("&7Plays: {0}/{1} (&c{2}%&7)", plays, totalPlays, percentagePlayed));
		p.sendMessage(FormatUtil.format("&7Likes: &a{0}", likes));
		p.sendMessage(FormatUtil.format("&7Disikes: &c{0}", dislikes));
	}
}