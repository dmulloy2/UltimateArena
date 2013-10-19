package net.dmulloy2.ultimatearena.commands;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.Permission;

/**
 * @author dmulloy2
 */

public class CmdForceStop extends UltimateArenaCommand
{
	public CmdForceStop(UltimateArena plugin)
	{
		super(plugin);
		this.name = "forcestop";
		this.aliases.add("fs");
		this.optionalArgs.add("arena");
		this.description = "force stop an arena";
		this.permission = Permission.FORCESTOP;
	}

	@Override
	public void perform()
	{
		if (args.length > 0)
		{
			Arena a = plugin.getArena(args[0]);
			if (a == null)
			{
				err("This arena is not currently active!");
				return;
			}

			sendpMessage("&3Stopping arena: &e{0}", a.getName());

			a.stop();
		}
		else
		{
			sendpMessage("&eStopping all arenas!");
			
			plugin.stopAll();
		}
	}
}