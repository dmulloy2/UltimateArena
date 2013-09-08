package net.dmulloy2.ultimatearena.commands;

import java.util.ArrayList;
import java.util.List;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.Permission;

public class CmdList extends UltimateArenaCommand
{
	public CmdList(UltimateArena plugin)
	{
		super(plugin);
		this.name = "list";
		this.aliases.add("li");
		this.description = "view all the UltimateArenas";
		this.permission = Permission.LIST;

		this.mustBePlayer = false;
	}

	@Override
	public void perform()
	{
		List<String> lines = new ArrayList<String>();
		StringBuilder line = new StringBuilder();
		line.append("&3====[ &eUltimateArenas &3]====");
		lines.add(line.toString());

		for (ArenaZone az : plugin.getLoadedArenas())
		{
			line = new StringBuilder();
			line.append("&3[&b" + az.getType().getName() + " &eArena&3]");
			line.append(" &b" + az.getArenaName() + "  ");

			if (az.isDisabled())
			{
				line.append(" &4[DISABLED]");
			}
			else
			{
				boolean active = false;
				for (Arena a : plugin.getActiveArenas())
				{
					if (a.getName().equals(az.getArenaName()))
					{
						if (a.isInLobby())
						{
							line.append(" &e[LOBBY | " + a.getStartTimer() + " seconds]");
						}
						else
						{
							line.append(" &e[INGAME]");
						}

						active = true;
					}
				}

				if (!active)
				{
					line.append(" &a[FREE]");
				}
			}

			line.append("        &e[&b" + az.getTimesPlayed() + "&e]");
			lines.add(line.toString());
		}

		for (String s : lines)
		{
			sendMessage(s);
		}
	}
}