/**
 * UltimateArena - fully customizable PvP arenas
 * Copyright (C) 2016 dmulloy2
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

import java.util.List;

import net.dmulloy2.ultimatearena.UltimateArena;

import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import lombok.AllArgsConstructor;

/**
 * Executes class commands
 * @author dmulloy2
 */
@AllArgsConstructor
public class CommandRunner extends BukkitRunnable
{
	private final String player;
	private final List<String> commands;
	private final UltimateArena plugin;

	@Override
	public void run()
	{
		CommandSender console = plugin.getServer().getConsoleSender();
		
		for (String command : commands)
		{
			plugin.getServer().dispatchCommand(console, command
					.replace("@p", player)
					.replace("%p", player));
		}
	}
}