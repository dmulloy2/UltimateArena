package net.dmulloy2.ultimatearena.commands;

import java.util.ArrayList;
import java.util.List;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaZone;

public class CmdList extends UltimateArenaCommand
{
	public CmdList(UltimateArena plugin)
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
		List<String> lines = new ArrayList<String>();
		StringBuilder line = new StringBuilder();
		line.append("&4====[ &6UltimateArenas &4]====");
		lines.add(line.toString());
		
		for (ArenaZone az : plugin.loadedArena)
		{
			line = new StringBuilder();
			line.append("&6[&c" + az.getType().getName() + " Arena&6]");
			line.append(" &c" + az.getArenaName());
			
			if (az.isDisabled())
			{
				line.append(" &4[DISABLED]");
			}
			else
			{
				boolean active = false;
				for (Arena a : plugin.activeArena)
				{
					if (a.getName().equals(az.getArenaName()))
					{
						if (a.isInLobby())
						{
							line.append(" &e[LOBBY | " + a.getStarttimer() + " seconds]");
						}
						else
						{
							line.append(" &e[INGAME]");
						}
						
						active = true;
					}
				}
				
				if (! active)
				{
					line.append(" &a[FREE]");
				}
			}
			
			line.append("        &e[" + az.getTimesPlayed() + "]");
			lines.add(line.toString());
		}
		
		for (String s : lines)
		{
			sendMessage(s);
		}
	}
}