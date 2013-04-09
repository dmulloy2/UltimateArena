package com.orange451.UltimateArena.commands;

import org.bukkit.ChatColor;

import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.PermissionInterface.PermissionInterface;

public class PCommandStop extends PBaseCommand {
	
	public PCommandStop(UltimateArena plugin) {
		this.plugin = plugin;
		aliases.add("stop");
		aliases.add("s");
		
		mode = "build";
		
		desc = ChatColor.YELLOW + " stop building an arena";
	}
	
	@Override
	public void perform() {
		//plugin.leaveArena(player);
		if (PermissionInterface.checkPermission(player, plugin.uaBuilder) || PermissionInterface.checkPermission(player, plugin.uaAdmin)) {
			if (plugin.isPlayerCreatingArena(player)) {
				plugin.stopCreatingArena(player);
			}
		}
	}
}
