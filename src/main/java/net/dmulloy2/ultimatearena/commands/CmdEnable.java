package net.dmulloy2.ultimatearena.commands;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaZone;
import net.dmulloy2.ultimatearena.permissions.PermissionType;

public class CmdEnable extends UltimateArenaCommand
{	
	public CmdEnable(UltimateArena plugin) 
	{
		super(plugin);
		this.name = "enable";
		this.aliases.add("en");
		this.requiredArgs.add("arena");
		this.description = "enable an arena";
		this.permission = PermissionType.CMD_ENABLE.permission;
		
		this.mustBePlayer = false;
	}
	
	@Override
	public void perform()
	{
		for (Arena a : plugin.activeArena)
		{
			if (a.getName().equalsIgnoreCase(args[0]))
			{
				a.setDisabled(false);
				sendpMessage("&aYou have enabled {0}!", a.getName());
				return;
			}
		}
			
		for (ArenaZone az : plugin.loadedArena)
		{
			if (az.getArenaName().equalsIgnoreCase(args[0]))
			{
				az.setDisabled(false);
				sendpMessage("&aYou have enabled {0}!", az.getArenaName());
				return;
			}
		}
		
		err("Could not find an Arena by that name/type!");
	}
}