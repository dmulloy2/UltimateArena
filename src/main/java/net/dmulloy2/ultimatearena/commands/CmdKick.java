package net.dmulloy2.ultimatearena.commands;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaPlayer;
import net.dmulloy2.ultimatearena.permissions.Permission;
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
		this.description = "kick a player from an arena";
		this.permission = Permission.CMD_KICK;
		
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
					a.endPlayer(ap, p, false);
					
					sendpMessage("&7Kicked player &6{0} &7from arena &6{1}&7!", p.getName(), a.getName());
					
					ap.sendMessage("&cYou have been kicked from the arena!");
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