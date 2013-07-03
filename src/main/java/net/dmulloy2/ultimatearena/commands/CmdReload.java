package net.dmulloy2.ultimatearena.commands;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.permissions.PermissionType;

public class CmdReload extends UltimateArenaCommand
{
	public CmdReload(UltimateArena plugin) 
	{
		super(plugin);
		this.name = "reload";
		this.aliases.add("rl");
		this.mode = "admin";
		this.description = "reload UltimateArena";
		this.permission = PermissionType.CMD_RELOAD.permission;
		
		this.mustBePlayer = false;
	}
	
	@Override
	public void perform()
	{
		sendMessage("&aReloading UltimateArena...");
		
		long start = System.currentTimeMillis();
			
		plugin.onDisable();
		plugin.onEnable();
		
		long finish = System.currentTimeMillis();
			
		sendMessage("&aReload Complete! Took {0} ms!", finish - start);
	}
}