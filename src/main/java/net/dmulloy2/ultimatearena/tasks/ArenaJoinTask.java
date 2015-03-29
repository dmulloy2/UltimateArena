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
package net.dmulloy2.ultimatearena.tasks;

import lombok.AllArgsConstructor;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.util.Util;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author dmulloy2
 */

@AllArgsConstructor
public class ArenaJoinTask extends BukkitRunnable
{
	private final String name;
	private final String arenaName;
	private final UltimateArena plugin;
	private final int team;

	@Override
	public void run()
	{
		Player player = getPlayer();
		if (player != null)
			plugin.addPlayer(player, arenaName, team);

		plugin.getWaiting().remove(name);
	}

	public final Player getPlayer()
	{
		return Util.matchPlayer(name);
	}
}
