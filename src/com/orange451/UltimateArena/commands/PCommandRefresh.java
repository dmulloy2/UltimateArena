package com.orange451.UltimateArena.commands;

import org.bukkit.ChatColor;

import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.PermissionInterface.PermissionInterface;

public class PCommandRefresh extends PBaseCommand {
	
	public PCommandRefresh(UltimateArena plugin) {
		this.plugin = plugin;
		aliases.add("refresh");
		aliases.add("r");
		
		mode = "admin";
		
		desc = ChatColor.YELLOW + " reload UltimateArena";
	}
	
	@Override
	public void perform() {
		if (PermissionInterface.checkPermission(player, plugin.uaAdmin)) {
			try{
				plugin.forceStop();
				plugin.clearMemory();
				plugin.onEnable();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
