package net.dmulloy2.ultimatearena.commands;

import java.util.List;

import org.bukkit.ChatColor;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaZone;

public class PCommandList extends UltimateArenaCommand
{
	public PCommandList(UltimateArena plugin)
	{
		super(plugin);
		this.name = "list";
		this.aliases.add("li");
		this.description = "view all the UltimateArenas";
		
		this.mustBePlayer = false;
	}
	
	@Override
	public void perform()
	{
		sendMessage(ChatColor.DARK_RED + "==== " + ChatColor.GOLD + "UltimateArenas" + ChatColor.DARK_RED + " ====");
		List<ArenaZone> arenas = this.plugin.loadedArena;
		List<Arena> activearenas = this.plugin.activeArena;
			
		for (int i = 0; i < arenas.size(); i++) 
		{
			String arena = arenas.get(i).getArenaName();
			String type = arenas.get(i).getArenaType();
				
			String arenaType = ChatColor.GOLD + "[" + ChatColor.RED + type + " Arena" + ChatColor.GOLD + "]";
			String arenaName = ChatColor.RED + arena;
			String arenaMode = "";
			String plays = ChatColor.YELLOW + "[" + arenas.get(i).getTimesPlayed() + "]";
			arenaMode = ChatColor.GREEN + "[FREE]";
			if (arenas.get(i).isDisabled())
				arenaMode = ChatColor.DARK_RED + "[DISABLED]";
				
			for (int ii = 0; ii < activearenas.size(); ii++) 
			{
				Arena ar = activearenas.get(ii);
				if (ar.getArenaZone().equals(arenas.get(i))) 
				{
					if (!ar.isDisabled())
					{
						if (ar.getStarttimer() > 0)
						{
							arenaMode = ChatColor.YELLOW + "[LOBBY  |  " + Integer.toString(ar.getStarttimer()) + " seconds]";
						}
						else
						{
							arenaMode = ChatColor.DARK_RED + "[BUSY]";
						}
					}
					else
					{
						arenaMode = ChatColor.DARK_RED + "[DISABLED]";
					}
				}
			}
			sendMessage(arenaType + " " + arenaName + " " + arenaMode + "        " + plays);
		}
	}
}