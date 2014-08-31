package net.dmulloy2.ultimatearena.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.Permission;

/**
 * @author dmulloy2
 */

public class CmdList extends UltimateArenaCommand
{
	public CmdList(UltimateArena plugin)
	{
		super(plugin);
		this.name = "list";
		this.aliases.add("li");
		this.description = "list all arenas";
		this.permission = Permission.LIST;

		this.mustBePlayer = false;
	}

	@Override
	public void perform()
	{
		if (plugin.getLoadedArenas().isEmpty())
		{
			err("There are no arenas to display!");
			return;
		}

		List<String> lines = new ArrayList<String>();
		StringBuilder line = new StringBuilder();
		line.append("&3====[ &eUltimateArenas &3]====");
		lines.add(line.toString());

		// Sort by total plays
		HashMap<ArenaZone, Integer> playMap = new HashMap<ArenaZone, Integer>();

		for (ArenaZone az : plugin.getLoadedArenas())
		{
			playMap.put(az, az.getTimesPlayed());
		}

		List<Entry<ArenaZone, Integer>> sortedEntries = new ArrayList<Entry<ArenaZone, Integer>>(playMap.entrySet());
		Collections.sort(sortedEntries, new Comparator<Entry<ArenaZone, Integer>>()
		{
			@Override
			public int compare(Entry<ArenaZone, Integer> entry1, Entry<ArenaZone, Integer> entry2)
			{
				return -entry1.getValue().compareTo(entry2.getValue());
			}
		});

		// Now display them
		for (Entry<ArenaZone, Integer> entry : sortedEntries)
		{
			ArenaZone az = entry.getKey();

			line = new StringBuilder();
			line.append("&3[&b" + az.getType().getStylizedName() + " &eArena&3]");
			line.append("  " + "&b" + az.getName() + "  ");

			if (az.isDisabled())
			{
				line.append(" &4[DISABLED]");
			}
			else
			{
				boolean active = false;
				for (Arena a : plugin.getActiveArenas())
				{
					if (a.getName().equals(az.getName()))
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