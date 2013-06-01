package com.orange451.UltimateArena.commands;

import org.bukkit.ChatColor;

import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.Arenas.Arena;
import com.orange451.UltimateArena.Arenas.Objects.ArenaZone;
import com.orange451.UltimateArena.permissions.PermissionType;

public class PCommandDisable extends UltimateArenaCommand
{
	public PCommandDisable(UltimateArena plugin)
	{
		this.plugin = plugin;
		aliases.add("disable");
		aliases.add("di");
		
		mode = "admin";
		
		desc = ChatColor.DARK_RED + "<arena>" + ChatColor.YELLOW + " disable an arena";
		
		this.permission = PermissionType.CMD_DISABLE.permission;
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
					aa.onDisable();
					player.sendMessage(ChatColor.GRAY + "Disabled " + at);
				}
				else if (aa.az.arenaType.equals(at)) 
				{
					aa.onDisable();
					player.sendMessage(ChatColor.GRAY + " Disabled " + at);
				}
			}
			for (int ii = 0; ii < plugin.loadedArena.size(); ii++) 
			{
				ArenaZone aa = plugin.loadedArena.get(ii);
				if (aa.arenaType.equals(at))
				{
					aa.disabled = true;
					player.sendMessage(ChatColor.GRAY + "Disabled " + at);
				}
				else if (aa.arenaName.equals(at)) 
				{
					aa.disabled = true;
					player.sendMessage(ChatColor.GRAY + "Disabled " + at);
				}
			}
		}
		else
		{
			for (int ii = 0; ii < plugin.activeArena.size(); ii++)
				plugin.activeArena.get(ii).onDisable();
			for (int ii = 0; ii < plugin.loadedArena.size(); ii++)
				plugin.loadedArena.get(ii).disabled = true;
			player.sendMessage(ChatColor.GRAY + "Disabled ALL arenas");
		}
	}
}