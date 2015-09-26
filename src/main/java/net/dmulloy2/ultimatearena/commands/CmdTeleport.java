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
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.Permission;

import org.bukkit.Location;

/**
 * @author dmulloy2
 */

public class CmdTeleport extends UltimateArenaCommand
{
	public CmdTeleport(UltimateArena plugin)
	{
		super(plugin);
		this.name = "teleport";
		this.aliases.add("tp");
		this.addRequiredArg("arena");
		this.description = "teleport to an arena";
		this.permission = Permission.TELEPORT;
		this.mustBePlayer = true;
	}

	@Override
	public void perform()
	{
		if (plugin.isInArena(player))
		{
			err(getMessage("teleportInArena"));
			return;
		}

		ArenaZone az = plugin.getArenaZone(args[0]);
		if (az == null)
		{
			err(getMessage("arenaNotFound"), args[0]);
			return;
		}

		Location loc = az.getLobby1().getLocation();
		player.teleport(loc.clone().add(0.0D, 1.0D, 0.0D));

		sendpMessage(getMessage("teleported"), az.getName());
	}
}
