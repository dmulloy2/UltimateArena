/**
 * UltimateArena - fully customizable PvP arenas
 * Copyright (C) 2012 - 2015 MineSworn
 * Copyright (C) 2013 - 2015 dmulloy2
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.dmulloy2.ultimatearena.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.Permission;
import net.dmulloy2.util.FormatUtil;

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
		line.append(FormatUtil.format(getMessage("genericHeader"), "UltimateArenas"));
		lines.add(line.toString());

		// Sort by total plays
		Map<ArenaZone, Integer> playMap = new HashMap<>();

		for (ArenaZone az : plugin.getLoadedArenas())
		{
			playMap.put(az, az.getTimesPlayed());
		}

		List<Entry<ArenaZone, Integer>> sortedEntries = new ArrayList<>(playMap.entrySet());
		Collections.sort(sortedEntries, new Comparator<Entry<ArenaZone, Integer>>()
		{
			@Override
			public int compare(Entry<ArenaZone, Integer> entry1, Entry<ArenaZone, Integer> entry2)
			{
				return -entry1.getValue().compareTo(entry2.getValue());
			}
		});

		// Now display them
		// TODO: Add this to messages.yml?
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
