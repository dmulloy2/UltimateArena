package net.dmulloy2.ultimatearena.commands;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.Permission;

public class CmdDisable extends UltimateArenaCommand
{
	public CmdDisable(UltimateArena plugin)
	{
		super(plugin);
		this.name = "disable";
		this.aliases.add("di");
		this.requiredArgs.add("arena");
		this.description = "disable an arena";
		this.permission = Permission.DISABLE;

		this.mustBePlayer = false;
	}

	@Override
	public void perform()
	{
		for (Arena a : plugin.getActiveArenas())
		{
			if (a.getName().equalsIgnoreCase(args[0]))
			{
				a.onDisable();
				sendpMessage("&cYou have disabled {0}!", a.getName());
				return;
			}
		}

		for (ArenaZone az : plugin.getLoadedArenas())
		{
			if (az.getArenaName().equalsIgnoreCase(args[0]))
			{
				az.setDisabled(true);
				sendpMessage("&cYou have disabled {0}!", az.getArenaName());
				return;
			}
		}

		err("Could not find an Arena by that name/type!");
	}
}