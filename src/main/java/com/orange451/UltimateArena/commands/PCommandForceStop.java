package com.orange451.UltimateArena.commands;

import org.bukkit.ChatColor;

import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.permissions.PermissionType;

public class PCommandForceStop extends UltimateArenaCommand
{
	public PCommandForceStop(UltimateArena plugin) 
	{
		this.plugin = plugin;
		aliases.add("forcestop");
		aliases.add("fs");
		
		mode = "admin";
		
		desc = ChatColor.DARK_RED + "<arena>" + ChatColor.YELLOW  + " force stop an arena";
		
		this.permission = PermissionType.CMD_FORCE_STOP.permission;
	}
	
	@Override
	public void perform() 
	{
		if (parameters.size() == 2)
		{
			plugin.forceStop(parameters.get(1));
		}
		else if (parameters.size() == 1) 
		{
			plugin.forceStop();
		}
		else
		{
			player.sendMessage(ChatColor.RED + "Incorrect use of /ua forcestop");
			player.sendMessage(ChatColor.GOLD + "/ua forcestop [arena]");
		}
	}
}