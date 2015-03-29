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
		this.addRequiredArg("arena");
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
			if (az.getName().equalsIgnoreCase(args[0]))
			{
				az.setDisabled(false);
				sendpMessage("&aYou have enabled {0}!", az.getName());
				return;
			}
		}

		err("Could not find an Arena by the name of \"&c{0}&4\"!", args[0]);
	}
}
