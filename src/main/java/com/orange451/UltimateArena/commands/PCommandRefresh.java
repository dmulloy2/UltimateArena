package com.orange451.UltimateArena.commands;

import java.util.logging.Level;

import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.permissions.PermissionType;

public class PCommandRefresh extends UltimateArenaCommand
{
	public PCommandRefresh(UltimateArena plugin) 
	{
		super(plugin);
		this.name = "refresh";
		this.aliases.add("reload");
		this.aliases.add("rl");
		this.mode = "admin";
		this.description = "reload UltimateArena";
		this.permission = PermissionType.CMD_REFRESH.permission;
		
		this.mustBePlayer = false;
	}
	
	@Override
	public void perform()
	{
		try
		{
			sendMessage("&aReloading UltimateArena...");
			plugin.forceStop();
			plugin.clearMemory();
			plugin.onEnable();
			sendMessage("&aReload Complete!");
		}
		catch(Exception e) 
		{
			log(Level.SEVERE, "Error while reloading: " + e.getMessage());
		}
	}
}