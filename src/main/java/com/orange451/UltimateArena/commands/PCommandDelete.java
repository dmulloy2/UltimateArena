package com.orange451.UltimateArena.commands;

import org.bukkit.ChatColor;

import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.permissions.PermissionType;

public class PCommandDelete extends UltimateArenaCommand
{
	public PCommandDelete(UltimateArena plugin) 
	{
		this.plugin = plugin;
		aliases.add("delete");
		aliases.add("x");
		
		mode = "build";
		
		desc = ChatColor.DARK_RED + "<arena>" + ChatColor.YELLOW + " delete an arena";
		
		this.permission = PermissionType.CMD_DELETE.permission;
	}
	
	@Override
	public void perform() 
	{
		if (parameters.size() == 2)
		{
			plugin.deleteArena(player, parameters.get(1));
		}
		else
		{
			player.sendMessage(ChatColor.RED + "Incorrect use of /ua delete");
			player.sendMessage(ChatColor.GOLD + "/ua delete [arena]");
		}
	}
}
