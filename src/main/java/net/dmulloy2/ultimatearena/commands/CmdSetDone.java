package net.dmulloy2.ultimatearena.commands;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.permissions.PermissionType;

public class CmdSetDone extends UltimateArenaCommand
{
	public CmdSetDone(UltimateArena plugin) 
	{
		super(plugin);
		this.name = "done";
		this.aliases.add("d");
		this.description = "finalize a step in the UA creation process";
		this.permission = PermissionType.CMD_SET_DONE.permission;
		
		this.mustBePlayer = true;
	}
	
	@Override
	public void perform() 
	{
		plugin.setDone(player);
	}
}
