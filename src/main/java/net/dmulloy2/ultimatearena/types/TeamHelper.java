package net.dmulloy2.ultimatearena.types;

import org.bukkit.ChatColor;

/**
 * @author dmulloy2
 */

public class TeamHelper
{
	/**
	 * @param team
	 *            - Team Number
	 * @return Team name and color
	 */
	public static String getTeam(int team)
	{
		if (team == 1)
			return ChatColor.RED + "Red";

		if (team == 2)
			return ChatColor.BLUE + "Blue";

		return ChatColor.GRAY + "No";
	}
}