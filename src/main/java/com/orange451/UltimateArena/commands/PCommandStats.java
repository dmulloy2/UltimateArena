package com.orange451.UltimateArena.commands;

import org.bukkit.ChatColor;

import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.Arenas.Objects.ArenaStatistics;
import com.orange451.UltimateArena.Arenas.Objects.ArenaZone;

public class PCommandStats extends UltimateArenaCommand
{	
	public PCommandStats(UltimateArena plugin) {
		this.plugin = plugin;
		aliases.add("stats");
		aliases.add("stat");
		
		desc = ChatColor.DARK_RED + "<arena>" + ChatColor.YELLOW + " view an arenas stats";
	}
	
	@Override
	public void perform() {
		if (parameters.size() == 2) {
			String arenaname = parameters.get(1);
			ArenaZone az = this.plugin.getArenaZone(arenaname);
			if (az != null) {
				ArenaStatistics as = new ArenaStatistics(az);
				as.dumpStats(player);
			}else{
				sendMessage(ChatColor.RED + "This arena doesn't exist!");
			}
		}
	}
}
