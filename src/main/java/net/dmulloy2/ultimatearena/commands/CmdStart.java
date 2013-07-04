package net.dmulloy2.ultimatearena.commands;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.permissions.PermissionType;

public class CmdStart extends UltimateArenaCommand
{
	public CmdStart(UltimateArena plugin) 
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
			sendMessage("&cCould not find an arena by the name \"{0}\"!", name);
			return;
		}
			
		sendMessage("&6Starting Arena &b{0}&6...", arena.getName());
		
		arena.forceStart();
	}
}