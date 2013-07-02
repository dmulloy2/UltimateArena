package net.dmulloy2.ultimatearena.commands;

import org.bukkit.ChatColor;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaZone;
import net.dmulloy2.ultimatearena.permissions.PermissionType;

public class PCommandEnable extends UltimateArenaCommand
{	
	public PCommandEnable(UltimateArena plugin) 
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
				if (aa.name.equals(at))
				{
					aa.disabled = false;
					sendMessage(ChatColor.GRAY + "Enabled " + at);
				}
				else if (aa.az.arenaType.equals(at))
				{
					aa.disabled = false;
					sendMessage(ChatColor.GRAY + "Enabled " + at);
				}
			}
			for (int ii = 0; ii < plugin.loadedArena.size(); ii++)
			{
				ArenaZone aa = plugin.loadedArena.get(ii);
				if (aa.arenaType.equals(at))
				{
					aa.disabled = false;
					sendMessage(ChatColor.GRAY + "Enabled " + at);
				}
				else if (aa.arenaName.equals(at)) 
				{
					aa.disabled = false;
					sendMessage(ChatColor.GRAY + "Enabled " + at);
				}
			}
		}
		else
		{
			for (int ii = 0; ii < plugin.activeArena.size(); ii++)
				plugin.activeArena.get(ii).disabled = false;
			for (int ii = 0; ii < plugin.loadedArena.size(); ii++)
				plugin.loadedArena.get(ii).disabled = false;
			sendMessage(ChatColor.GRAY + "Enabled ALL arenas");
		}
	}
}
