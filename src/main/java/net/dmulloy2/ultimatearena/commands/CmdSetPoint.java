package net.dmulloy2.ultimatearena.commands;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.types.Permission;

/**
 * @author dmulloy2
 */

public class CmdSetPoint extends UltimateArenaCommand
{
	public CmdSetPoint(UltimateArena plugin)
	{
		super(plugin);
		this.name = "setpoint";
		this.aliases.add("sp");
		this.optionalArgs.add("args");
		this.description = "set a point of your field";
		this.permission = Permission.SETPOINT;

		this.mustBePlayer = true;
	}

	@Override
	public void perform()
	{
		plugin.setPoint(player, args);
	}
}
