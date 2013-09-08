package net.dmulloy2.ultimatearena.commands;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.Permission;

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

		this.mustBePlayer = false;
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

			a.stop();
		}
		else
		{
			plugin.stopAll();
		}
	}
}