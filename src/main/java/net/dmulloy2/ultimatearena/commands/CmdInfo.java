package net.dmulloy2.ultimatearena.commands;

import java.util.List;

import org.bukkit.ChatColor;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaPlayer;

public class CmdInfo extends UltimateArenaCommand
{	
	public CmdInfo(UltimateArena plugin)
	{
		super(plugin);
		this.name = "info";
		this.optionalArgs.add("arena");
		this.description = "view info on the arena you''re in";
		
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
					sendMessage(ChatColor.DARK_RED + "==== " + ChatColor.GOLD + ar.getArenaZone().getArenaName() + ChatColor.DARK_RED + " ====");
					ArenaPlayer ap = plugin.getArenaPlayer(player);
					if (ap != null)
					{
						String out = ChatColor.GREEN + "NOT OUT";
						if (ap.isOut()) 
						{
							out = ChatColor.RED + "OUT";
						}
						sendMessage(ChatColor.GRAY + "YOU ARE: " + out);
					}
					List<ArenaPlayer> arr = ar.getArenaplayers();
					for (int i = 0; i < arr.size(); i++)
					{
						try
						{
							if (arr.get(i).isOut() != true)
							{
								String playerName = ChatColor.BLUE + "[" + ChatColor.GRAY + arr.get(i).getPlayer().getName() + ChatColor.BLUE + "]";
								String playerHealth = ChatColor.BLUE + "[" + ChatColor.GRAY + ((arr.get(i).getPlayer().getHealth() / 20.0) * 100) + ChatColor.BLUE + "%]";
								sendMessage(playerName + playerHealth);
							}
						}
						catch (Exception e)
						{
							//
						}
					}
					
				}
			}
			else
			{
				sendMessage(ChatColor.RED + "You are not in an arena!");
			}
		}
		else if (args.length == 1)
		{
			String arenaname = args[0];
			Arena ar = this.plugin.getArena(arenaname);
			if (ar != null)
			{
				sendMessage(ChatColor.GRAY + "Arena: " + ChatColor.GOLD + arenaname);
				sendMessage(ChatColor.GRAY + "Type: " + ChatColor.GOLD + ar.getArenaZone().getArenaType());
				sendMessage(ChatColor.GRAY + "Players:");
				List<ArenaPlayer> arr = ar.getArenaplayers();
				if (arr.size() > 0)
				{
					for (int i = 0; i < arr.size(); i++)
					{
						try
						{
							if (arr.get(i).isOut() != true) 
							{
								String playerName = ChatColor.BLUE + "[" + ChatColor.GRAY + arr.get(i).getPlayer().getName() + ChatColor.BLUE + "]";
								String playerHealth = ChatColor.BLUE + "[" + ChatColor.GRAY + ((arr.get(i).getPlayer().getHealth() / 20.0) * 100) + ChatColor.BLUE + "%]";
								sendMessage(playerName + playerHealth);
							}
						}
						catch(Exception e) 
						{
							//
						}
					}
				}
				else
				{
					sendMessage(ChatColor.RED + "No players in the arena");
				}
			}
			else
			{
				sendMessage(ChatColor.GRAY + "This arena isn't running!");
			}
		}
		else
		{
			sendMessage(ChatColor.GRAY + "Please supply an arena name");
		}
	}
}