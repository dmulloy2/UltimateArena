package com.orange451.UltimateArena.commands;

import org.bukkit.ChatColor;

import com.orange451.UltimateArena.UltimateArena;

public class PCommandLeave extends UltimateArenaCommand
{	
	public PCommandLeave(UltimateArena plugin) {
		this.plugin = plugin;
		aliases.add("leave");
		aliases.add("l");
		
		desc = ChatColor.YELLOW + "leave an arena";
	}
	
	@Override
	public void perform() {
		plugin.leaveArena(player);
	}
}
