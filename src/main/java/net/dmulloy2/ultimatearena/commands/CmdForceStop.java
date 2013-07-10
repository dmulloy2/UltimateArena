package net.dmulloy2.ultimatearena.commands;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.permissions.PermissionType;

public class CmdForceStop extends UltimateArenaCommand
{
	public CmdForceStop(UltimateArena plugin) 
	{
		super(plugin);
		this.name = "forcestop";
		this.aliases.add("fs");
		this.optionalArgs.add("arena");
		this.mode = "admin";
		this.description = "force stop an arena";
		this.permission = PermissionType.CMD_FORCE_STOP.permission;
		
		this.mustBePlayer = false;
	}
	
	@Override
	public void perform() 
	{
		if (args.length > 0)
		{
			boolean found = false;
			for (int i = 0; i < plugin.activeArena.size(); i++)
			{
				Arena a = plugin.activeArena.get(i);
				a.stop();
				found = true;
			}
			
			if (! found)
			{
				err("No arena by that name exists!");
			}
		}
		else
		{
			plugin.stopAll();
		}
	}
}