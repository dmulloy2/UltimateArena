package net.dmulloy2.ultimatearena.commands;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaPlayer;

import org.apache.commons.lang.WordUtils;

public class CmdInfo extends UltimateArenaCommand
{	
	public CmdInfo(UltimateArena plugin)
	{
		super(plugin);
		this.name = "info";
		this.optionalArgs.add("arena");
		this.description = "view info on the arena you are in";
		
		this.mustBePlayer = true;
	}
	
	@Override
	public void perform()
	{
		if (args.length == 0)
		{
			if (plugin.isInArena(player)) 
			{
				Arena ar = plugin.getArena(player);
				if (ar != null) 
				{
					sendMessage("&4====[ &6{0} &4]====", WordUtils.capitalize(ar.getName()));

					ArenaPlayer ap = plugin.getArenaPlayer(player);
					if (ap != null)
					{
						if (ap.isOut())
						{
							sendMessage("&7You are &cOUT&7!");
						}
						else
						{
							sendMessage("&7You are &aNOT OUT&7!");
						}
					}

					sendMessage(""); // Empty line
					
					sendMessage("&6Active Players:");
					for (String s : ar.buildLeaderboard(player))
					{
						sendMessage(s);
					}
				}
			}
			else
			{
				err("You are not in an arena!");
			}
		}
		else if (args.length == 1)
		{
			String arenaname = args[0];
			Arena ar = plugin.getArena(arenaname);
			if (ar != null)
			{
				sendMessage("&4====[ &6{0} &4]====", ar.getName());
				
				sendMessage("&7Type: &6{0}", ar.getType());
				
				sendMessage(""); // Empty line
				
				sendMessage("&6Active Players:");
				
				for (String s : ar.buildLeaderboard(player))
				{
					sendMessage(s);
				}
			}
			else
			{
				sendMessage("&7This arena isn't running!");
			}
		}
		else
		{
			sendMessage("&7Please supply an arena name");
		}
	}
}