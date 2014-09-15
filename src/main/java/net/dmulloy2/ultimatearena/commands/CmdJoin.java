/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.ultimatearena.commands;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.types.Permission;

/**
 * @author dmulloy2
 */

public class CmdJoin extends UltimateArenaCommand
{
	public CmdJoin(UltimateArena plugin)
	{
		super(plugin);
		this.name = "join";
		this.aliases.add("j");
		this.addRequiredArg("arena");
		this.description = "join an arena";
		this.permission = Permission.JOIN;
		this.mustBePlayer = true;
	}

	@Override
	public void perform()
	{
		plugin.attemptJoin(player, args[0]);
	}
}