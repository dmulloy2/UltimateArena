package com.orange451.UltimateArena.PermissionInterface;

import org.bukkit.entity.Player;

public class PermissionInterface 
{
	public static boolean checkPermission(Player player, String command)
	{
		try
		{
			if (player.isOp() || player.hasPermission(command))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		catch(Exception e)
		{
			//
		}
		return true;
	}
}