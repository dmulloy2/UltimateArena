package com.orange451.UltimateArena.commands;

import org.bukkit.ChatColor;

import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.permissions.PermissionType;

public class PCommandRefresh extends UltimateArenaCommand
{
	public PCommandRefresh(UltimateArena plugin) 
	{
		this.plugin = plugin;
		aliases.add("refresh");
		aliases.add("r");
		
		mode = "admin";
		
		desc = ChatColor.YELLOW + " reload UltimateArena";
		
		this.permission = PermissionType.CMD_REFRESH.permission;
	}
	
	@Override
	public void perform()
	{
		try
		{
			plugin.forceStop();
			plugin.clearMemory();
			plugin.onEnable();
			player.sendMessage(ChatColor.GREEN + "Reloaded UltimateArena!");
		}
		catch(Exception e) 
		{
			plugin.getLogger().severe("Error while reloading: " + e.getMessage());
		}
	}
}
