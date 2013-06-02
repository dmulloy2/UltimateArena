package com.orange451.UltimateArena.commands;

import org.bukkit.ChatColor;

import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.Arenas.Objects.ArenaZone;

public class PCommandLike extends UltimateArenaCommand
{	
	public PCommandLike(UltimateArena plugin)
	{
		super(plugin);
		this.name = "like";
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
				az.liked++;
				az.voted.add(player.getName());
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