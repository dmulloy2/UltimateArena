package net.dmulloy2.ultimatearena.commands;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.permissions.PermissionType;

public class CmdStop extends UltimateArenaCommand
{
	public CmdStop(UltimateArena plugin) 
	{
		super(plugin);
		this.name = "stop";
		this.aliases.add("s");
		this.description = "stop building an arena";
		this.permission = PermissionType.CMD_STOP.permission;
		
		this.mustBePlayer = true;
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
			err("You are not creating an arena!");
		}
	}
}