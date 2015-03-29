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
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.ArenaSpectator;
import net.dmulloy2.ultimatearena.types.Permission;

/**
 * @author dmulloy2
 */

public class CmdSpectate extends UltimateArenaCommand
{
	public CmdSpectate(UltimateArena plugin)
	{
		super(plugin);
		this.name = "spectate";
		this.addOptionalArg("arena");
		this.description = "spectate an arena";
		this.permission = Permission.SPECTATE;
		this.mustBePlayer = true;
	}

	@Override
	public void perform()
	{
		ArenaPlayer ap = plugin.getArenaPlayer(player);
		if (ap != null)
		{
			err("You cannot spectate while in an arena!");
			return;
		}

		ArenaSpectator spectator = plugin.getSpectatingHandler().getSpectator(player);
		if (spectator != null)
		{
			plugin.getSpectatingHandler().removeSpectator(spectator);
			sendpMessage("&3You are no longer spectating!");
			return;
		}

		Arena arena = getArena(0);
		if (arena == null || ! arena.isActive())
		{
			err("You must specify a valid arena!");
			return;
		}

		plugin.getSpectatingHandler().addSpectator(arena, player);

		sendpMessage("&3You are now spectating &e{0}", arena.getName());
		sendpMessage("&3To stop spectating, use &e/ua spectate &3again");
	}
}
