/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.ultimatearena.types;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.bukkit.ChatColor;

/**
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

	public static Team getById(int id)
	{
		for (Team team : values())
		{
			if (team.getId() == id)
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
