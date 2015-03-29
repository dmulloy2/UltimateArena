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
import net.dmulloy2.util.NumberUtil;
import net.dmulloy2.util.Util;

import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class CmdForceJoin extends UltimateArenaCommand
{
	public CmdForceJoin(UltimateArena plugin)
	{
		super(plugin);
		this.name = "forcejoin";
		this.aliases.add("fj");
		this.addRequiredArg("arena");
		this.addRequiredArg("player");
		this.addOptionalArg("team");
		this.description = "force a player into an arena";
		this.permission = Permission.JOIN_FORCE;
	}

	@Override
	public void perform()
	{
		Player player = Util.matchPlayer(args[1]);
		if (player == null)
		{
			err("Player \"&c{0}&4\" not found!", args[1]);
			return;
		}

		int team = -1;
		if (args.length > 2)
			team = NumberUtil.toInt(args[2]);

		plugin.attemptJoin(player, args[0], team);
	}
}
