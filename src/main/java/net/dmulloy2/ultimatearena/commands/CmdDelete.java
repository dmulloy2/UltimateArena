package net.dmulloy2.ultimatearena.commands;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.permissions.Permission;

public class CmdDelete extends UltimateArenaCommand
{
	public CmdDelete(UltimateArena plugin) 
	{
		super(plugin);
		this.name = "delete";
		this.aliases.add("x");
		this.requiredArgs.add("arena");
		this.description = "delete an arena";
		this.permission = Permission.DELETE;
		
		this.mustBePlayer = false;
	}
	
	@Override
	public void perform() 
	{
		plugin.deleteArena(player, args[0]);
	}
}
