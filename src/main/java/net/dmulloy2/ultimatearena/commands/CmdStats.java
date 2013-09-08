package net.dmulloy2.ultimatearena.commands;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.types.ArenaStatistics;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.Permission;

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
		String arenaname = args[0];
		ArenaZone az = plugin.getArenaZone(arenaname);
		if (az != null)
		{
			ArenaStatistics as = new ArenaStatistics(az);
			as.dumpStats(player);
		}
		else
		{
			err("This arena doesn't exist!");
		}
	}
}
