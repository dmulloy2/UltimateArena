package com.orange451.UltimateArena.commands;

import org.bukkit.ChatColor;

import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.PermissionInterface.PermissionInterface;

public class PCommandSetDone extends PBaseCommand {
	
	public PCommandSetDone(UltimateArena plugin) {
		this.plugin = plugin;
		aliases.add("done");
		aliases.add("d");
		
		mode = "build";
		
		desc = ChatColor.YELLOW + " finalize a step in the UA creation process";
	}
	
	@Override
	public void perform() {
		if (PermissionInterface.checkPermission(player, plugin.uaBuilder) || PermissionInterface.checkPermission(player, plugin.uaAdmin)) {
			plugin.setDone(player);
		}
	}
}
