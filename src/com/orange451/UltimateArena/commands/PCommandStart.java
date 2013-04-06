/**
 * Copyright (C) 2012 t7seven7t
 */
package com.orange451.UltimateArena.commands;

import org.bukkit.ChatColor;

import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.Arenas.Arena;
import com.orange451.UltimateArena.PermissionInterface.PermissionInterface;

/**
 * @author t7seven7t
 */
public class PCommandStart extends PBaseCommand {

	public PCommandStart(UltimateArena plugin) {
		this.plugin = plugin;
		aliases.add("start");
		
		mode = "admin";
		
		desc = ChatColor.WHITE + "[fieldName] " + ChatColor.YELLOW + "to force start an arena";
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
			player.sendMessage(ChatColor.GOLD + "/ua start [ARENANAME]");
		}
	}
	
}
