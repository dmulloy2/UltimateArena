package net.dmulloy2.ultimatearena.commands;

import org.bukkit.ChatColor;

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
		this.optionalArgs.add("arena");
		this.mode = "admin";
		this.description = "enable an arena";
		this.permission = PermissionType.CMD_ENABLE.permission;
		
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
					aa.setDisabled(false);
					sendMessage(ChatColor.GRAY + "Enabled " + at);
					return;
				}
				else if (aa.getArenaZone().getType().name().equals(at))
				{
					aa.setDisabled(false);
					sendMessage(ChatColor.GRAY + "Enabled " + at);
					return;
				}
			}
			for (int ii = 0; ii < plugin.loadedArena.size(); ii++)
			{
				ArenaZone aa = plugin.loadedArena.get(ii);
				if (aa.getType().name().equals(at))
				{
					aa.setDisabled(false);
					sendMessage(ChatColor.GRAY + "Enabled " + at);
					return;
				}
				else if (aa.getArenaName().equals(at)) 
				{
					aa.setDisabled(false);
					sendMessage(ChatColor.GRAY + "Enabled " + at);
					return;
				}
			}
		}
		else
		{
			for (int ii = 0; ii < plugin.activeArena.size(); ii++)
				plugin.activeArena.get(ii).setDisabled(false);
			for (int ii = 0; ii < plugin.loadedArena.size(); ii++)
				plugin.loadedArena.get(ii).setDisabled(false);
			sendMessage(ChatColor.GRAY + "Enabled ALL arenas");
			return;
		}
		
		err("Could not find an arena/type by that name!");
	}
}
