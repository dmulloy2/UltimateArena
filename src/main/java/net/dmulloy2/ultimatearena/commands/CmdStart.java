package net.dmulloy2.ultimatearena.commands;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.Permission;

/**
 * @author dmulloy2
 */

public class CmdStart extends UltimateArenaCommand
{
	public CmdStart(UltimateArena plugin)
	{
		super(plugin);
		this.name = "start";
		this.addOptionalArg("arena");
		this.description = "force start an arena";
		this.permission = Permission.START;
		this.mustBePlayer = false;
	}

	@Override
	public void perform()
	{
		Arena arena = getArena(0);
		if (arena == null)
		{
			err("Please specify a valid arena!");
			return;
		}

		sendpMessage("&3Force starting arena &e{0}&3...", arena.getName());
		arena.forceStart(player);
	}
}