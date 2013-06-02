package com.orange451.UltimateArena.commands;

import org.bukkit.ChatColor;

import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.Arenas.Objects.ArenaStatistics;
import com.orange451.UltimateArena.Arenas.Objects.ArenaZone;

public class PCommandStats extends UltimateArenaCommand
{	
	public PCommandStats(UltimateArena plugin)
	{
		super(plugin);
		this.name = "stats";
		this.aliases.add("stat");
		this.requiredArgs.add("arena");
		this.description = "view an arena's stats";
	}
	
	@Override
	public void perform() 
	{
		String arenaname = args[0];
		ArenaZone az = this.plugin.getArenaZone(arenaname);
		if (az != null) 
		{
			ArenaStatistics as = new ArenaStatistics(az);
			as.dumpStats(player);
		}
		else
		{
			sendMessage(ChatColor.RED + "This arena doesn't exist!");
		}
	}
}
