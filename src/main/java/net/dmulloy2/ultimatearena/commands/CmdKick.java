package net.dmulloy2.ultimatearena.commands;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaPlayer;
import net.dmulloy2.ultimatearena.permissions.PermissionType;
import net.dmulloy2.ultimatearena.util.Util;

import org.bukkit.entity.Player;

public class CmdKick extends UltimateArenaCommand
{
	public CmdKick(UltimateArena plugin) 
	{
		super(plugin);
		this.name = "kick";
		this.aliases.add("k");
		this.requiredArgs.add("player");
		this.mode = "admin";
		this.description = "kick a player from an arena";
		this.permission = PermissionType.CMD_KICK.permission;
		
		this.mustBePlayer = false;
	}
	
	@Override
	public void perform()
	{
		Player p = Util.matchPlayer(args[0]);
		if (p != null) 
		{
			ArenaPlayer ap = plugin.getArenaPlayer(p);
			if (ap != null) 
			{
				Arena a = plugin.getArena(p);
				if (a != null)
				{
					a.endPlayer(ap, false);
					ap.setOut(true);
					ap.setDeaths(999999999);
					ap.setPoints(0);
					ap.setKills(0);
					ap.setGameXP(0);
					
					sendpMessage("&7Kicked player &6{0} &7from arena &6{1}&7!", p.getName(), a.getName());
				}
			}
			else
			{
				err("Player: {0} is not in an Arena!", p.getName());
			}
		}
		else
		{
			err("Could not find an online player by the name of {0}!", args[0]);
		}
	}
}