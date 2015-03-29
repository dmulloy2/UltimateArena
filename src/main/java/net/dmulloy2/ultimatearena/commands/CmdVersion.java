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
import net.dmulloy2.ultimatearena.types.Permission;

/**
 * @author dmulloy2
 */

public class CmdVersion extends UltimateArenaCommand
{
	public CmdVersion(UltimateArena plugin)
	{
		super(plugin);
		this.name = "version";
		this.aliases.add("v");
		this.description = "display version information";
		this.permission = Permission.VERSION;
	}

	@Override
	public void perform()
	{
		sendMessage("&3====[ &eUltimateArena &3]====");
		sendMessage("&bVersion&e: {0}", plugin.getDescription().getVersion());
		sendMessage("&bAuthor&e: dmulloy2");
		sendMessage("&bIssues&e: https://github.com/MineSworn/UltimateArena/issues");
	}
}
