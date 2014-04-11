package net.dmulloy2.ultimatearena.commands;

import net.dmulloy2.ultimatearena.UltimateArena;

/**
 * @author dmulloy2
 */

public class CmdDone extends UltimateArenaCommand
{
	public CmdDone(UltimateArena plugin)
	{
		super(plugin);
		this.name = "done";
		this.description = "Legacy command";
		this.mustBePlayer = true;
	}

	@Override
	public void perform()
	{
		sendpMessage("This command has been removed.");
		sendpMessage("If you are trying to finalize the setting of points, use &b/ua sp &3done");
	}
}