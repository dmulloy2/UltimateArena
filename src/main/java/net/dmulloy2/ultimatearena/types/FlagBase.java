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
package net.dmulloy2.ultimatearena.types;

import lombok.Getter;
import lombok.Setter;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;

import net.dmulloy2.swornapi.util.Validate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

@Getter @Setter
public abstract class FlagBase
{
	protected Location location;
	protected Block notify;
	protected Arena arena;

	protected final UltimateArena plugin;

	public FlagBase(Arena arena, ArenaLocation location, UltimateArena plugin)
	{
		Validate.notNull(arena, "arena cannot be null!");
		Validate.notNull(location, "location cannot be null!");
		Validate.notNull(plugin, "plugin cannot be null!");

		this.arena = arena;
		this.location = location.getLocation().clone().subtract(0.0D, 1.0D, 0.0D);
		this.plugin = plugin;
		this.setup();
	}

	/**
	 * Sets up this flag. Used to create the actual flag.
	 */
	protected void setup()
	{
		this.notify = location.clone().add(0.0D, 5.0D, 0.0D).getBlock();
		this.notify.setType(Material.WHITE_WOOL);

		location.clone().add(1, 0, 0).getBlock().setType(Material.QUARTZ_BLOCK);
		location.clone().add(1, 0, 1).getBlock().setType(Material.QUARTZ_BLOCK);
		location.clone().add(-1, 0, -1).getBlock().setType(Material.QUARTZ_BLOCK);
		location.clone().add(1, 0, -1).getBlock().setType(Material.QUARTZ_BLOCK);
		location.clone().add(-1, 0, 1).getBlock().setType(Material.QUARTZ_BLOCK);
		//location.clone().add(2, 0, 0).getBlock().setType(Material.QUARTZ_BLOCK);
		location.clone().add(-1, 0, 0).getBlock().setType(Material.QUARTZ_BLOCK);
		//location.clone().add(-2, 0, 0).getBlock().setType(Material.QUARTZ_BLOCK);
		location.clone().add(0, 0, 1).getBlock().setType(Material.QUARTZ_BLOCK);
		//location.clone().add(0, 0, 2).getBlock().setType(Material.QUARTZ_BLOCK);
		location.clone().add(0, 0, -1).getBlock().setType(Material.QUARTZ_BLOCK);
		//location.clone().add(0, 0, -2).getBlock().setType(Material.QUARTZ_BLOCK);
	}

	public abstract void checkNear(ArenaPlayer[] arenaPlayers);

	protected String getMessage(String key)
	{
		return plugin.getMessage(key);
	}

	protected ArenaPlayer findClosest(ArenaPlayer[] players, double radius)
	{
		return findClosest(players, location, radius);
	}

	public static ArenaPlayer findClosest(ArenaPlayer[] players, Location location, double radius)
	{
		double radSquared = radius * radius;

		ArenaPlayer closest = null;
		double distance = -1;

		for (ArenaPlayer ap : players)
		{
			Player player = ap.getPlayer();
			if (player.getHealth() > 0.0D && player.getWorld().equals(location.getWorld()))
			{
				double dist = location.distanceSquared(player.getLocation());
				if (distance < radSquared && (distance == -1 || dist < distance))
				{
					closest = ap;
					distance = dist;
				}
			}
		}

		return closest;
	}
}
