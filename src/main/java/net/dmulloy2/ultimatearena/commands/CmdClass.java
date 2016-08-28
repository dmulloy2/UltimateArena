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
import net.dmulloy2.ultimatearena.gui.ClassSelectionGUI;
import net.dmulloy2.ultimatearena.types.ArenaClass;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.Permission;
import net.dmulloy2.util.FormatUtil;

/**
 * @author dmulloy2
 */

public class CmdClass extends UltimateArenaCommand
{
	public CmdClass(UltimateArena plugin)
	{
		super(plugin);
		this.name = "class";
		this.aliases.add("cl");
		this.addOptionalArg("class");
		this.description = "switch your class";
		this.permission = Permission.CLASS;
		this.mustBePlayer = true;
	}

	@Override
	public void perform()
	{
		ArenaPlayer ap = plugin.getArenaPlayer(player);
		if (ap == null)
		{
			err(getMessage("notInArena"));
			return;
		}

		if (args.length == 0)
		{
			ClassSelectionGUI csGUI = new ClassSelectionGUI(plugin, player, false);
			plugin.getGuiHandler().open(player, csGUI);
			return;
		}

		ArenaClass cl = plugin.getArenaClass(args[0]);
		if (cl == null)
		{
			err(getMessage("classNotFound"), args[0]);
			return;
		}

		if (! cl.checkAvailability(ap))
			return;

		if (ap.setClass(cl))
		{
			String name = cl.getName();
			String article = FormatUtil.getArticle(name);
			String spawn = ap.getArena().isInGame() ? "respawn" : "spawn";

			sendpMessage(getMessage("classSpawn"), spawn, article, name);
		}
	}
}
