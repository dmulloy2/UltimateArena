package com.orange451.UltimateArena.commands;

import org.bukkit.ChatColor;

import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.permissions.PermissionType;

public class PCommandForceJoin extends UltimateArenaCommand
{
	public PCommandForceJoin(UltimateArena plugin)
	{
		this.plugin = plugin;
		aliases.add("forcejoin");
		aliases.add("fj");
		
		mode = "admin";
		
		desc = ChatColor.DARK_RED + "<arena>" + ChatColor.YELLOW + " force join an arena";
		
		this.permission = PermissionType.CMD_FORCE_JOIN.permission;
	}
	
	@Override
	public void perform() 
	{
		if (parameters.size() == 2) 
		{
			String name = parameters.get(1);
			player.sendMessage(ChatColor.GOLD + "Attempthing to join arena: " + name);
			plugin.joinBattle(true, player, name);
		}
		else
		{
			player.sendMessage(ChatColor.RED + "Incorrect use of /ua fj");
			player.sendMessage(ChatColor.GOLD + "/ua fj [arena]");
		}
	}
}
