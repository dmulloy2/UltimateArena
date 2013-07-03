package net.dmulloy2.ultimatearena.commands;

import org.bukkit.ChatColor;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaZone;

public class CmdLike extends UltimateArenaCommand
{	
	public CmdLike(UltimateArena plugin)
	{
		super(plugin);
		this.name = "like";
		this.requiredArgs.add("arena");
		this.description = "like an arena";
	}
	
	@Override
	public void perform()
	{
		String arenaname = args[0];
		ArenaZone az = plugin.getArenaZone(arenaname);
		if (az != null) 
		{
			if (az.canLike(player)) 
			{
				sendMessage("&aYou have voted for: {0}!", az.getArenaName());
				
				az.setLiked(az.getLiked() + 1);
				az.getVoted().add(player.getName());
			}
			else
			{
				sendMessage(ChatColor.RED + "You already voted for this arena!");
			}
		}
		else
		{
			sendMessage(ChatColor.RED + "This arena doesn't exist!");
		}
	}
}