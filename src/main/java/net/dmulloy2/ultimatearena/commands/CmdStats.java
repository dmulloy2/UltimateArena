package net.dmulloy2.ultimatearena.commands;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.Permission;

/**
 * @author dmulloy2
 */

public class CmdStats extends UltimateArenaCommand
{
	public CmdStats(UltimateArena plugin)
	{
		super(plugin);
		this.name = "stats";
		this.aliases.add("stat");
		this.requiredArgs.add("arena");
		this.description = "view an arena''s stats";
		this.permission = Permission.STATS;

		this.mustBePlayer = false;
	}

	@Override
	public void perform()
	{
		ArenaZone az = plugin.getArenaZone(args[0]);
		if (az == null)
		{
			err("Could not find an arena by the name of \"&c{0}&4\"!", args[0]);
			return;
		}

		for (String s : az.getStats())
			sendMessage(s);
	}
}