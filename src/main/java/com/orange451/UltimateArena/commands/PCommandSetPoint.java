package com.orange451.UltimateArena.commands;

import org.bukkit.ChatColor;

import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.permissions.PermissionType;

public class PCommandSetPoint extends UltimateArenaCommand
{	
	public PCommandSetPoint(UltimateArena plugin) 
	{
		this.plugin = plugin;
		aliases.add("setpoint");
		aliases.add("sp");
		
		mode = "build";
		
		desc = ChatColor.YELLOW + "set a point of your field";
		
		this.permission = PermissionType.CMD_SET_POINT.permission;
	}
	
	@Override
	public void perform() 
	{
		plugin.setPoint(player);
	}
}
