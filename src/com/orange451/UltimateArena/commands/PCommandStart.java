package com.orange451.UltimateArena.commands;

import org.bukkit.ChatColor;

import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.Arenas.Arena;
import com.orange451.UltimateArena.PermissionInterface.PermissionInterface;

public class PCommandStart extends PBaseCommand {

	public PCommandStart(UltimateArena plugin) {
		this.plugin = plugin;
		aliases.add("start");
		
		mode = "admin";
		
		desc = ChatColor.DARK_RED + "<arena>" + ChatColor.YELLOW + " force start an arena";
	}
	
	@Override
	public void perform() {
		if (parameters.size() == 2) {
			if (PermissionInterface.checkPermission(player, plugin.uaAdmin)) {
				String name = parameters.get(1);
				Arena arena = plugin.getArena(name);
				if (arena == null) {
					player.sendMessage(ChatColor.GOLD + "No arena with that name...");
					return;
				}
				
				arena.start();
				player.sendMessage(ChatColor.GOLD + "Starting arena.. " + ChatColor.AQUA + arena.name );
			}
		} else {
			player.sendMessage(ChatColor.RED + "Incorrect use of /ua start");
			player.sendMessage(ChatColor.GOLD + "/ua start [arena]");
		}
	}
	
}
