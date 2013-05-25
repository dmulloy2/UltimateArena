package com.orange451.UltimateArena.commands;

import org.bukkit.ChatColor;

import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.PermissionInterface.PermissionInterface;

public class PCommandDelete extends PBaseCommand {
	
	public PCommandDelete(UltimateArena plugin) {
		this.plugin = plugin;
		aliases.add("delete");
		aliases.add("x");
		
		mode = "build";
		
		desc = ChatColor.DARK_RED + "<arena>" + ChatColor.YELLOW + " delete an arena";
	}
	
	@Override
	public void perform() {
		if (parameters.size() == 2) {
			if (PermissionInterface.checkPermission(player, plugin.uaAdmin)) {
				plugin.deleteArena(player, parameters.get(1));
			}
		}else{
			player.sendMessage(ChatColor.RED + "Incorrect use of /ua delete");
			player.sendMessage(ChatColor.GOLD + "/ua delete [arena]");
		}
	}
}
