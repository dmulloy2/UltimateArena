package com.orange451.UltimateArena.permissions;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.orange451.UltimateArena.UltimateArena;

/**
 * @author dmulloy2
 */

public class PermissionHandler 
{
	public UltimateArena plugin;
	public PermissionHandler(UltimateArena plugin)
	{
		this.plugin = plugin;
	}
	
	public boolean hasPermission(CommandSender sender, Permission permission) 
	{
		return (permission == null) ? true : hasPermission(sender, getPermissionString(permission));
	}

	public boolean hasPermission(CommandSender sender, String permission) 
	{
		if (sender instanceof Player) 
		{
			Player p = (Player) sender;
			return (p.hasPermission(permission) || p.isOp());
		}
		
		return true;
	}
	
	private String getPermissionString(Permission permission) 
	{
		return plugin.getName() + "." + permission.getNode().toLowerCase();
	}
}