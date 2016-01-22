/**
 * UltimateArena - fully customizable PvP arenas
 * Copyright (C) 2012 - 2016 MineSworn
 * Copyright (C) 2013 - 2016 dmulloy2
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

/**
 * The various conditions for winning an Arena.
 * <p>
 * So far this is only supported in FFA and hunger arenas.
 * 
 * @author dmulloy2
 */

@Getter
public enum WinCondition
{
	LAST_MAN_STANDING("Last Man Standing", "lastmanstanding", "default"),
	MOST_KILLS("Most Kills", "mostkills"),
	BEST_KDR("Best KDR", "bestkdr"),
	;

	private final String name;
	private final String[] config;

	private WinCondition(String name, String... config)
	{
		this.name = name;
		this.config = config;
	}

	public static WinCondition fromConfig(String config)
	{
		for (WinCondition condition : values())
		{
			for (String acceptable : condition.getConfig())
			{
				if (acceptable.equalsIgnoreCase(config))
					return condition;
			}
		}

		return null;
	}
}