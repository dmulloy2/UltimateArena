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

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dmulloy2.swornapi.util.NumberUtil;

import org.bukkit.ChatColor;

/**
 * Represents the available Teams in an Arena.
 * @author dmulloy2
 */

@Getter
@AllArgsConstructor
public enum Team
{
	RED(1, ChatColor.RED, "Red"),
	BLUE(2, ChatColor.BLUE, "Blue"),
	NONE(-1, ChatColor.LIGHT_PURPLE, "None");

	private final int id;
	private final ChatColor color;
	private final String name;

	/**
	 * Gets a team by its name or id.
	 * @param id Team name/id
	 * @return Team, or NONE if not found
	 */
	public static Team get(String id)
	{
		return NumberUtil.isInt(id) ? getById(NumberUtil.toInt(id)) : getByName(id);
	}

	/**
	 * Gets a team by its id.
	 * @param id Team id
	 * @return Team, or NONE if not found
	 */
	public static Team getById(int id)
	{
		for (Team team : values())
		{
			if (team.getId() == id)
				return team;
		}

		return NONE;
	}

	/**
	 * Gets a team by its name.
	 * @param name Team name
	 * @return Team, or NONE if not found
	 */
	public static Team getByName(String name)
	{
		for (Team team : values())
		{
			if (team.getName().equalsIgnoreCase(name))
				return team;
		}

		return NONE;
	}

	@Override
	public String toString()
	{
		return color + name;
	}
}
