package com.orange451.UltimateArena.commands;

import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.permissions.PermissionType;

public class PCommandCreate extends UltimateArenaCommand
{
	public PCommandCreate(UltimateArena plugin) 
	{
		super(plugin);
		this.name = "create";
		this.aliases.add("c");
		this.requiredArgs.add("name");
		this.requiredArgs.add("type");
		this.mode = "build";
		this.description = "create an UltimateArena";
		this.permission = PermissionType.CMD_CREATE.permission;
	}
	
	@Override
	public void perform()
	{
		String name = args[0];
		String type = args[1];
		plugin.createField(player, name, type);
	}
}