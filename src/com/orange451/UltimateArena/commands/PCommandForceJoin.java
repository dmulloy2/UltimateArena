package com.orange451.UltimateArena.commands;

import org.bukkit.ChatColor;

import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.PermissionInterface.PermissionInterface;

public class PCommandForceJoin extends PBaseCommand {
	
	public PCommandForceJoin(UltimateArena plugin) {
		this.plugin = plugin;
		aliases.add("forcejoin");
		aliases.add("fj");
		
		mode = "admin";
		
		desc = ChatColor.DARK_RED + "<arena>" + ChatColor.YELLOW + " force join an arena";
	}
	
	@Override
	public void perform() {
		if (parameters.size() == 2) {
			if (PermissionInterface.checkPermission(player, plugin.uaAdmin)) {
				String name = parameters.get(1);
				player.sendMessage(ChatColor.GOLD + "Attempthing to join arena: " + name);
				plugin.joinBattle(true, player, name);
			}else{
				player.sendMessage("You do not have permission to force join arenas");
			}
		}else{
			player.sendMessage(ChatColor.RED + "Incorrect use of /ua fj");
			player.sendMessage(ChatColor.GOLD + "/ua fj [arena]");
		}
	}
}
