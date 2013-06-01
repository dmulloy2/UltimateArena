package com.orange451.UltimateArena.commands;

import org.bukkit.ChatColor;

import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.Arenas.Arena;
import com.orange451.UltimateArena.Arenas.Objects.ArenaZone;
import com.orange451.UltimateArena.permissions.PermissionType;

public class PCommandEnable extends UltimateArenaCommand
{	
	public PCommandEnable(UltimateArena plugin) 
	{
		this.plugin = plugin;
		aliases.add("enable");
		aliases.add("en");
		
		mode = "admin";
		
		desc = ChatColor.DARK_RED + "<arena>" + ChatColor.YELLOW + " enable an arena";
		
		this.permission = PermissionType.CMD_ENABLE.permission;
	}
	
	@Override
	public void perform()
	{
		if (parameters.size() == 2)
		{
			String at = parameters.get(1);
			for (int ii = 0; ii < plugin.activeArena.size(); ii++) 
			{
				Arena aa = plugin.activeArena.get(ii);
				if (aa.name.equals(at))
				{
					aa.disabled = false;
					player.sendMessage(ChatColor.GRAY + "Enabled " + at);
				}
				else if (aa.az.arenaType.equals(at))
				{
					aa.disabled = false;
					player.sendMessage(ChatColor.GRAY + "Enabled " + at);
				}
			}
			for (int ii = 0; ii < plugin.loadedArena.size(); ii++)
			{
				ArenaZone aa = plugin.loadedArena.get(ii);
				if (aa.arenaType.equals(at))
				{
					aa.disabled = false;
					player.sendMessage(ChatColor.GRAY + "Enabled " + at);
				}
				else if (aa.arenaName.equals(at)) 
				{
					aa.disabled = false;
					player.sendMessage(ChatColor.GRAY + "Enabled " + at);
				}
			}
		}
		else
		{
			for (int ii = 0; ii < plugin.activeArena.size(); ii++)
				plugin.activeArena.get(ii).disabled = false;
			for (int ii = 0; ii < plugin.loadedArena.size(); ii++)
				plugin.loadedArena.get(ii).disabled = false;
			player.sendMessage(ChatColor.GRAY + "Enabled ALL arenas");
		}
	}
}
