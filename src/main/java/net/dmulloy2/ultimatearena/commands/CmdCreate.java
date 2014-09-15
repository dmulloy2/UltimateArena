package net.dmulloy2.ultimatearena.commands;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.types.Permission;

/**
 * @author dmulloy2
 */

public class CmdCreate extends UltimateArenaCommand
{
	public CmdCreate(UltimateArena plugin)
	{
		super(plugin);
		this.name = "create";
		this.aliases.add("c");
		this.addRequiredArg("name");
		this.addRequiredArg("type");
		this.description = "create an arena";
		this.permission = Permission.CREATE;

		this.mustBePlayer = true;
	}

	@Override
	public void perform()
	{
		plugin.createArena(player, args[0], args[1]);
	}
}