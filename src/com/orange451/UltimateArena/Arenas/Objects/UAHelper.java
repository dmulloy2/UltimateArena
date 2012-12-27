package com.orange451.UltimateArena.Arenas.Objects;

import org.bukkit.ChatColor;

public class UAHelper {
	public static String getTeam(int team) { //give an int team, get the string
		if (team == 1)
			return ChatColor.RED + "RED";
		if (team == 2)
			return ChatColor.BLUE + "BLUE";
		return ChatColor.DARK_GRAY + "NOTEAM";
	}
}
