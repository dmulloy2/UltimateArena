/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.ultimatearena.commands;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.types.Permission;

/**
 * @author dmulloy2
 */

public class CmdAbandon extends UltimateArenaCommand
{
	public CmdAbandon(UltimateArena plugin)
	{
		super(plugin);
		this.name = "abandon";
		this.description = "stop creating an arena";
		this.permission = Permission.ABANDON;
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

		plugin.stopCreatingArena(player);
	}
}