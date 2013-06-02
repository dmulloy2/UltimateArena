package com.orange451.UltimateArena.commands;

import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.permissions.PermissionType;

public class PCommandStop extends UltimateArenaCommand
{
	public PCommandStop(UltimateArena plugin) 
	{
		super(plugin);
		this.name = "stop";
		this.aliases.add("s");
		this.mode = "build";
		this.description = "stop building an arena";
		this.permission = PermissionType.CMD_STOP.permission;
	}
	
	@Override
	public void perform() 
	{
		if (plugin.isPlayerCreatingArena(player)) 
		{
			plugin.stopCreatingArena(player);
		}
		else
		{
			sendMessage("&cYou are not creating an arena!");
		}
	}
}