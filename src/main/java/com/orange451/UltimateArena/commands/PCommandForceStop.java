package com.orange451.UltimateArena.commands;

import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.permissions.PermissionType;

public class PCommandForceStop extends UltimateArenaCommand
{
	public PCommandForceStop(UltimateArena plugin) 
	{
		super(plugin);
		this.name = "forcestop";
		this.aliases.add("fs");
		this.requiredArgs.add("arena");
		this.mode = "admin";
		this.description = "force stop an arena";
		this.permission = PermissionType.CMD_FORCE_STOP.permission;
	}
	
	@Override
	public void perform() 
	{
		if (args.length > 0)
		{
			plugin.forceStop(args[0]);
		}
		else
		{
			plugin.forceStop();
		}
	}
}