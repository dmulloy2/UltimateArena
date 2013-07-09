package net.dmulloy2.ultimatearena.commands;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaStatistics;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaZone;

public class CmdStats extends UltimateArenaCommand
{	
	public CmdStats(UltimateArena plugin)
	{
		super(plugin);
		this.name = "stats";
		this.aliases.add("stat");
		this.requiredArgs.add("arena");
		this.description = "view an arena''s stats";
	}
	
	@Override
	public void perform() 
	{
		String arenaname = args[0];
		ArenaZone az = this.plugin.getArenaZone(arenaname);
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
