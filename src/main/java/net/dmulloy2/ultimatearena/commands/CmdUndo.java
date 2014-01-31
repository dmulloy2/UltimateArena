package net.dmulloy2.ultimatearena.commands;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.types.Permission;

/**
 * @author dmulloy2
 */

public class CmdUndo extends UltimateArenaCommand
{
	public CmdUndo(UltimateArena plugin)
	{
		super(plugin);
		this.name = "undo";
		this.description = "Undo the last step in arena creation";
		this.permission = Permission.UNDO;

		this.mustBePlayer = true;
	}

	@Override
	public void perform()
	{
		if (! plugin.isCreatingArena(player))
		{
			err("You are not creating an arena!");
			return;
		}

		plugin.getArenaCreator(player).undo();
	}
}