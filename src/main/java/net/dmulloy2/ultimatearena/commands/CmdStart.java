package net.dmulloy2.ultimatearena.commands;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.permissions.PermissionType;

public class CmdStart extends UltimateArenaCommand
{
	public CmdStart(UltimateArena plugin) 
	{
		super(plugin);
		this.name = "start";
		this.optionalArgs.add("arena");
		this.mode = "admin";
		this.description = "force start an arena";
		this.permission = PermissionType.CMD_START.permission;
		
		this.mustBePlayer = false;
	}
	
	@Override
	public void perform() 
	{
		Arena arena = null;
		if (isPlayer())
		{
			if (args.length == 0)
			{
				if (! plugin.isInArena(player))
				{
					err("Please specify a valid arena!");
					return;
				}
				
				arena = plugin.getArena(player);
			}
			else
			{
				arena = plugin.getArena(args[0]);
			}
		}
		else
		{
			if (args.length == 0)
			{
				err("Please specify a valid arena!");
				return;
			}
			
			arena = plugin.getArena(args[0]);
		}

		if (arena == null)
		{
			err("Could not find an arena by the name \"{0}\"!", args[0]);
			return;
		}
			
		sendpMessage("&6Starting Arena &b{0}&6...", arena.getName());
		
		arena.forceStart(player);
	}
}