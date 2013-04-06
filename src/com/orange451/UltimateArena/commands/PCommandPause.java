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
public class PCommandPause extends PBaseCommand {

	public PCommandPause(UltimateArena plugin) {
		this.plugin = plugin;
		aliases.add("pause");
		
		mode = "admin";
		
		desc = "To pause the start timer on an arena";
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
				
				arena.pauseStartTimer = !arena.pauseStartTimer;
				player.sendMessage(ChatColor.GOLD + "Start timer for arena " + ChatColor.AQUA + arena.name + ChatColor.GOLD + "is now " + (arena.pauseStartTimer ? "paused" : "unpaused"));
			}
		} else {
			player.sendMessage(ChatColor.RED + "Incorrect use of /ua pause");
			player.sendMessage(ChatColor.GOLD + "/ua pause [ARENANAME]");
		}

	}
	
}
