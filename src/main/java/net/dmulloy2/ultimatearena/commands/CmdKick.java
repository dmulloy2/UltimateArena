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
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.LeaveReason;
import net.dmulloy2.ultimatearena.types.Permission;
import net.dmulloy2.util.Util;

import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class CmdKick extends UltimateArenaCommand
{
	public CmdKick(UltimateArena plugin)
	{
		super(plugin);
		this.name = "kick";
		this.aliases.add("k");
		this.addRequiredArg("player");
		this.description = "kick a player from an arena";
		this.permission = Permission.KICK;

		this.mustBePlayer = false;
	}

	@Override
	public void perform()
	{
		Player player = Util.matchPlayer(args[0]);
		if (player == null)
		{
			err(getMessage("playerNotFound"), args[0]);
			return;
		}

		ArenaPlayer ap = plugin.getArenaPlayer(player);
		if (ap == null)
		{
			err(getMessage("playerNotInArena"));
			return;
		}

		ap.leaveArena(LeaveReason.KICK);
	}
}
