package com.orange451.UltimateArena.commands;

import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.permissions.PermissionType;

public class PCommandDelete extends UltimateArenaCommand
{
	public PCommandDelete(UltimateArena plugin) 
	{
		super(plugin);
		this.name = "delete";
		this.aliases.add("x");
		this.requiredArgs.add("arena");
		this.mode = "build";
		this.description = "delete an arena";
		this.permission = PermissionType.CMD_DELETE.permission;
	}
	
	@Override
	public void perform() 
	{
		plugin.deleteArena(player, args[0]);
	}
}
