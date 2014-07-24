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
		Arena arena = getArena(0);
		if (arena == null)
		{
			if (args.length != 0)
			{
				err("Please specify a valid arena!");
				return;
			}

			sendpMessage("&3Stopping all arenas!");
			plugin.stopAll();
			return;
		}

		sendpMessage("&3Stopping arena &e{0}&3!", arena.getName());
		arena.stop();
	}
}