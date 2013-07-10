package net.dmulloy2.ultimatearena.commands;

import org.bukkit.ChatColor;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaZone;
import net.dmulloy2.ultimatearena.permissions.PermissionType;

public class CmdDisable extends UltimateArenaCommand
{
	public CmdDisable(UltimateArena plugin)
	{
		super(plugin);
		this.name = "disable";
		this.aliases.add("di");
		this.optionalArgs.add("arena");
		this.mode = "admin";
		this.description = "disable an arena";
		this.permission = PermissionType.CMD_DISABLE.permission;
		
		this.mustBePlayer = false;
	}
	
	@Override
	public void perform()
	{
		if (args.length == 1)
		{
			String at = args[0];
			for (int ii = 0; ii < plugin.activeArena.size(); ii++)
			{		
				Arena aa = plugin.activeArena.get(ii);
				if (aa.getName().equals(at)) 
				{
					aa.onDisable();
					sendpMessage(ChatColor.GRAY + "Disabled " + at);
					return;
				}
				else if (aa.getArenaZone().getType().name().equals(at)) 
				{
					aa.onDisable();
					sendpMessage(ChatColor.GRAY + " Disabled " + at);
					return;
				}
			}
			for (int ii = 0; ii < plugin.loadedArena.size(); ii++) 
			{
				ArenaZone aa = plugin.loadedArena.get(ii);
				if (aa.getType().name().equals(at))
				{
					aa.setDisabled(true);
					sendpMessage(ChatColor.GRAY + "Disabled " + at);
					return;
				}
				else if (aa.getArenaName().equals(at)) 
				{
					aa.setDisabled(true);
					sendpMessage(ChatColor.GRAY + "Disabled " + at);
					return;
				}
			}
		}
		else
		{
			for (int ii = 0; ii < plugin.activeArena.size(); ii++)
				plugin.activeArena.get(ii).onDisable();
			for (int ii = 0; ii < plugin.loadedArena.size(); ii++)
				plugin.loadedArena.get(ii).setDisabled(true);
			sendpMessage(ChatColor.GRAY + "Disabled ALL arenas");
			return;
		}
		
		err("&cCould not find an arena by that name/type!");
	}
}