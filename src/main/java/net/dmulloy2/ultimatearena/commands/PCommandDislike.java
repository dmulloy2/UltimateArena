package net.dmulloy2.ultimatearena.commands;

import org.bukkit.ChatColor;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaZone;

public class PCommandDislike extends UltimateArenaCommand
{	
	public PCommandDislike(UltimateArena plugin)
	{
		super(plugin);
		this.name = "dislike";
		this.aliases.add("d");
		this.requiredArgs.add("arena");
		this.description = "dislike an arena";
	}
	
	@Override
	public void perform()
	{
		String arenaname = args[0];
		ArenaZone az = this.plugin.getArenaZone(arenaname);
		if (az != null)
		{
			if (az.canLike(player))
			{
				sendMessage("&cYou have disliked: " + az.arenaName);
				
				az.disliked++;
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