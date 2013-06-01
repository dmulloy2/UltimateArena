package com.orange451.UltimateArena.commands;

import org.bukkit.ChatColor;

import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.permissions.PermissionType;

public class PCommandStop extends UltimateArenaCommand
{
	public PCommandStop(UltimateArena plugin) 
	{
		this.plugin = plugin;
		aliases.add("stop");
		aliases.add("s");
		
		mode = "build";
		
		desc = ChatColor.YELLOW + " stop building an arena";
		
		this.permission = PermissionType.CMD_STOP.permission;
	}
	
	@Override
	public void perform() 
	{
		if (plugin.isPlayerCreatingArena(player)) 
		{
			plugin.stopCreatingArena(player);
		}
	}
}