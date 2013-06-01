package com.orange451.UltimateArena.commands;

import org.bukkit.ChatColor;

import com.orange451.UltimateArena.UltimateArena;

public class PCommandJoin extends UltimateArenaCommand
{
	public PCommandJoin(UltimateArena plugin) 
	{
		this.plugin = plugin;
		aliases.add("join");
		aliases.add("j");
		
		desc = ChatColor.DARK_RED + "<arena> " + ChatColor.YELLOW + " join/start an UltimateArena";
	}
	
	@Override
	public void perform() 
	{
		if (parameters.size() == 2)
		{
			String name = parameters.get(1);
			plugin.fight(player, name);
		}
		else
		{
			player.sendMessage(ChatColor.RED + "Incorrect use of /ua join");
			player.sendMessage(ChatColor.GOLD + "/ua join [arena]");
		}
	}
}
