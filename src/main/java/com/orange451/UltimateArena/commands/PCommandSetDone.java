package com.orange451.UltimateArena.commands;

import org.bukkit.ChatColor;

import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.permissions.PermissionType;

public class PCommandSetDone extends UltimateArenaCommand
{
	public PCommandSetDone(UltimateArena plugin) 
	{
		this.plugin = plugin;
		aliases.add("done");
		aliases.add("d");
		
		mode = "build";
		
		desc = ChatColor.YELLOW + " finalize a step in the UA creation process";
		
		this.permission = PermissionType.CMD_SET_DONE.permission;
	}
	
	@Override
	public void perform() 
	{
		plugin.setDone(player);
	}
}
