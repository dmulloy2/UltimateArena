/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.ultimatearena.commands;

import net.dmulloy2.ultimatearena.UltimateArena;

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
		this.requiredArgs.add("arena");
		this.description = "join an arena";
		this.mustBePlayer = true;
	}

	@Override
	public void perform()
	{
		plugin.attemptJoin(player, args[0]);
	}
}