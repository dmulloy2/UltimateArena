package net.dmulloy2.ultimatearena.commands;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.Permission;

/**
 * @author dmulloy2
 */

public class CmdEnable extends UltimateArenaCommand
{
	public CmdEnable(UltimateArena plugin)
	{
		super(plugin);
		this.name = "enable";
		this.aliases.add("en");
		this.requiredArgs.add("arena");
		this.description = "enable an arena";
		this.permission = Permission.ENABLE;

		this.mustBePlayer = false;
	}

	@Override
	public void perform()
	{
		for (Arena a : plugin.getActiveArenas())
		{
			if (a.getName().equalsIgnoreCase(args[0]))
			{
				a.setDisabled(false);
				sendpMessage("&aYou have enabled {0}!", a.getName());
				return;
			}
		}

		for (ArenaZone az : plugin.getLoadedArenas())
		{
			if (az.getArenaName().equalsIgnoreCase(args[0]))
			{
				az.setDisabled(false);
				sendpMessage("&aYou have enabled {0}!", az.getArenaName());
				return;
			}
		}

		err("Could not find an Arena by the name of \"&c{0}&4\"!", args[0]);
	}
}