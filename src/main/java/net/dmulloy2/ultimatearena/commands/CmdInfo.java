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

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.Permission;

import org.apache.commons.lang.WordUtils;

/**
 * @author dmulloy2
 */

public class CmdInfo extends UltimateArenaCommand
{
	public CmdInfo(UltimateArena plugin)
	{
		super(plugin);
		this.name = "info";
		this.aliases.add("lb");
		this.addOptionalArg("arena");
		this.description = "view info on the arena you are in";
		this.permission = Permission.INFO;
		this.mustBePlayer = false;
	}

	@Override
	public void perform()
	{
		Arena arena;
		if (isPlayer())
		{
			arena = getArena(0, false);
			if (arena == null)
			{
				ArenaZone az = plugin.getZoneInside(player.getLocation());
				if (az != null)
				{
					sendMessage(getMessage("genericHeader"), WordUtils.capitalize(az.getName()));
					sendMessage("&3Type: &e{0}", az.getStylized());
					sendMessage("&3Max: &e{0}", az.getArena().getMax().toCommandString());
					sendMessage("&3Min: &e{0}", az.getArena().getMin().toCommandString());
					// TODO: Possibly expand this?
				}

				return;
			}
		}
		else
		{
			arena = getArena(0);
			if (arena == null)
				return;
		}

		sendMessage(getMessage("genericHeader"), WordUtils.capitalize(arena.getName()));

		if (arena.getExtraInfo() != null)
		{
			for (String extraInfo : arena.getExtraInfo())
				sendMessage(extraInfo);
		}

		sendMessage(""); // Empty line

		sendMessage(getMessage("activePlayers"));
		for (String s : arena.getLeaderboard(player))
		{
			sendMessage(s);
		}
	}
}
