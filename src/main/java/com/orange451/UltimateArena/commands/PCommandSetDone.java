package com.orange451.UltimateArena.commands;

import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.permissions.PermissionType;

public class PCommandSetDone extends UltimateArenaCommand
{
	public PCommandSetDone(UltimateArena plugin) 
	{
		super(plugin);
		this.name = "done";
		this.aliases.add("d");
		this.mode = "build";
		this.description = "finalize a step in the UA creation process";
		this.permission = PermissionType.CMD_SET_DONE.permission;
	}
	
	@Override
	public void perform() 
	{
		plugin.setDone(player);
	}
}
