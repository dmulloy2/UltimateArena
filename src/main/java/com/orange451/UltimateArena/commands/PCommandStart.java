package com.orange451.UltimateArena.commands;

import org.bukkit.ChatColor;

import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.Arenas.Arena;
import com.orange451.UltimateArena.permissions.PermissionType;

public class PCommandStart extends UltimateArenaCommand
{
	public PCommandStart(UltimateArena plugin) 
	{
		super(plugin);
		this.name = "start";
		this.requiredArgs.add("arena");
		this.mode = "admin";
		this.description = "force start an arena";
		this.permission = PermissionType.CMD_START.permission;
		
		this.mustBePlayer = false;
	}
	
	@Override
	public void perform() 
	{
		String name = args[0];
		Arena arena = plugin.getArena(name);
		if (arena == null)
		{
			player.sendMessage(ChatColor.GOLD + "No arena with that name...");
			return;
		}
			
		arena.start();
		player.sendMessage(ChatColor.GOLD + "Starting arena.. " + ChatColor.AQUA + arena.name );
	}
}