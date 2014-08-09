/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.ultimatearena.types;

import org.bukkit.ChatColor;

/**
 * @author dmulloy2
 */

public class TeamHelper
{
	/**
	 * Gets a team's name and color.
	 *
	 * @param team Team Number
	 * @return The team's name and color
	 */
	public static String getTeam(int team)
	{
		if (team == 1)
			return ChatColor.RED + "Red";
		else if (team == 2)
			return ChatColor.BLUE + "Blue";
		else
			return ChatColor.GRAY + "N/A";
	}
}