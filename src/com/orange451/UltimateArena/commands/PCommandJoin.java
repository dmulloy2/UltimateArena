package com.orange451.UltimateArena.commands;

import org.bukkit.ChatColor;

import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.PermissionInterface.PermissionInterface;

public class PCommandJoin extends PBaseCommand {
	
	public PCommandJoin(UltimateArena plugin) {
		this.plugin = plugin;
		aliases.add("join");
		aliases.add("j");
		
		desc = ChatColor.WHITE + "[fieldName] " + ChatColor.YELLOW + "to join/start an UltimateArena field";
	}
	
	@Override
	public void perform() {
		if (parameters.size() == 2) {
			if (PermissionInterface.checkPermission(player, plugin.uaUser)) {
				String name = parameters.get(1);
				plugin.fight(player, name);
			}else{
				player.sendMessage("You do not have permission to do this");
			}
		}else{
			player.sendMessage(ChatColor.RED + "Incorrect use of /ua join");
			player.sendMessage(ChatColor.GOLD + "/ua join [ARENANAME]");
		}
	}
}
