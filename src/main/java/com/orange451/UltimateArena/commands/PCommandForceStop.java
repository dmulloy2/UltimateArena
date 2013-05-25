package com.orange451.UltimateArena.commands;

import org.bukkit.ChatColor;

import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.PermissionInterface.PermissionInterface;

public class PCommandForceStop extends PBaseCommand {
	
	public PCommandForceStop(UltimateArena plugin) {
		this.plugin = plugin;
		aliases.add("forcestop");
		aliases.add("fs");
		
		mode = "admin";
		
		desc = ChatColor.DARK_RED + "<arena>" + ChatColor.YELLOW  + " force stop an arena";
	}
	
	@Override
	public void perform() {
		if (PermissionInterface.checkPermission(player, plugin.uaAdmin)) {
			if (parameters.size() == 2) {
				plugin.forceStop(parameters.get(1));
			}else if (parameters.size() == 1) {
				plugin.forceStop();
			}else{
				player.sendMessage(ChatColor.RED + "Incorrect use of /ua forcestop");
				player.sendMessage(ChatColor.GOLD + "/ua forcestop [arena]");
			}
		}
	}
}
