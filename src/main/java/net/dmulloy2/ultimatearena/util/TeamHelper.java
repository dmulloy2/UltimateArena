package net.dmulloy2.ultimatearena.util;

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
			return ChatColor.RED + "RED";

		if (team == 2)
			return ChatColor.BLUE + "BLUE";

		return ChatColor.DARK_GRAY + "NOTEAM";
	}
}