package net.dmulloy2.ultimatearena.commands;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.permissions.PermissionType;

public class CmdDelete extends UltimateArenaCommand
{
	public CmdDelete(UltimateArena plugin) 
	{
		super(plugin);
		this.name = "delete";
		this.aliases.add("x");
		this.requiredArgs.add("arena");
		this.mode = "build";
		this.description = "delete an arena";
		this.permission = PermissionType.CMD_DELETE.permission;
		
		this.mustBePlayer = false;
	}
	
	@Override
	public void perform() 
	{
		plugin.deleteArena(player, args[0]);
	}
}
