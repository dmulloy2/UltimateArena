package net.dmulloy2.ultimatearena.handlers;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.types.Permission;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class PermissionHandler
{
	private final UltimateArena plugin;
	public PermissionHandler(UltimateArena plugin)
	{
		this.plugin = plugin;
	}

	public boolean hasPermission(CommandSender sender, Permission permission)
	{
		return permission == null ? true : hasPermission(sender, getPermissionString(permission));
	}

	public boolean hasPermission(CommandSender sender, String permission)
	{
		if (sender instanceof Player)
		{
			Player p = (Player) sender;
			return p.hasPermission(permission) || p.isOp();
		}

		return true;
	}

	private String getPermissionString(Permission permission)
	{
		return plugin.getName() + "." + permission.getNode().toLowerCase();
	}
}