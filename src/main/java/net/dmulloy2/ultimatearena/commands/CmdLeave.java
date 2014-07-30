package net.dmulloy2.ultimatearena.commands;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.LeaveReason;

/**
 * @author dmulloy2
 */

public class CmdLeave extends UltimateArenaCommand
{
	public CmdLeave(UltimateArena plugin)
	{
		super(plugin);
		this.name = "leave";
		this.aliases.add("l");
		this.description = "leave an arena";
		this.mustBePlayer = true;
	}

	@Override
	public void perform()
	{
		ArenaPlayer ap = plugin.getArenaPlayer(player);
		if (ap == null)
		{
			err("You must be in an arena to do this!");
			return;
		}

		ap.leaveArena(LeaveReason.COMMAND);
	}
}