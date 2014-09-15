package net.dmulloy2.ultimatearena.commands;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.Permission;

/**
 * @author dmulloy2
 */

public class CmdStop extends UltimateArenaCommand
{
	public CmdStop(UltimateArena plugin)
	{
		super(plugin);
		this.name = "stop";
		this.aliases.add("fs");
		this.addRequiredArg("arena");
		this.description = "stop an active arena";
		this.permission = Permission.STOP;
	}

	@Override
	public void perform()
	{
		Arena arena = getArena(0);
		if (arena == null)
		{
			if (! args[0].equalsIgnoreCase("all"))
			{
				sendpMessage("&3Stopping all arenas!");
				plugin.stopAll();
				return;
			}

			err("Arena \"&c{0}&4\" not found!", args[0]);
			return;
		}

		sendpMessage("&3Stopping arena &e{0}&3!", arena.getName());
		arena.stop();
	}
}