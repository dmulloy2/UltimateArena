package com.orange451.UltimateArena.commands;

import java.util.List;

import org.bukkit.ChatColor;

import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.Arenas.Arena;
import com.orange451.UltimateArena.Arenas.Objects.ArenaZone;

public class PCommandList extends PBaseCommand {
	
	public PCommandList(UltimateArena plugin) {
		this.plugin = plugin;
		aliases.add("list");
		aliases.add("li");
		
		desc = ChatColor.YELLOW + "view all the UltimateArenas";
	}
	
	@Override
	public void perform() {
		if (parameters.size() == 1) {
			sendMessage(ChatColor.DARK_RED + "==== " + ChatColor.GOLD + "UltimateArenas" + ChatColor.DARK_RED + " ====");
			List<ArenaZone> arenas = this.plugin.loadedArena;
			List<Arena> activearenas = this.plugin.activeArena;
			
			for (int i = 0; i < arenas.size(); i++) {
				String arena = arenas.get(i).arenaName;
				String type = arenas.get(i).arenaType;
				
				String arenaType = ChatColor.GOLD + "[" + ChatColor.RED + type + " Arena" + ChatColor.GOLD + "]";
				String arenaName = ChatColor.RED + arena;
				String arenaMode = "";
				String plays = ChatColor.YELLOW + "[" + arenas.get(i).timesPlayed + "]";
				arenaMode = ChatColor.GREEN + "[FREE]";
				if (arenas.get(i).disabled)
					arenaMode = ChatColor.DARK_RED + "[DISABLED]";
				
				for (int ii = 0; ii < activearenas.size(); ii++) {
					Arena ar = activearenas.get(ii);
					if (ar.az.equals(arenas.get(i))) {
						if (!ar.disabled) {
							if (ar.starttimer > 0) {
								arenaMode = ChatColor.YELLOW + "[LOBBY  |  " + Integer.toString(ar.starttimer) + " seconds]";
							}else{
								arenaMode = ChatColor.DARK_RED + "[BUSY]";
							}
						}else{
							arenaMode = ChatColor.DARK_RED + "[DISABLED]";
						}
					}
				}
				String sendto = arenaType + " " + arenaName + " " + arenaMode + "        " + plays;
				player.sendMessage(sendto);
			}
		}
	}
}