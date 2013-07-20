package net.dmulloy2.ultimatearena.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.WordUtils;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaPlayer;
import net.dmulloy2.ultimatearena.util.FormatUtil;
import net.dmulloy2.ultimatearena.util.Util;

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
					for (String s : buildLeaderboard(ar))
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
				
				for (String s : buildLeaderboard(ar))
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
	
	private List<String> buildLeaderboard(Arena ar)
	{
		List<String> leaderboard = new ArrayList<String>();
		
		// Build kills map
		HashMap<String, Double> kdrMap = new HashMap<String, Double>();
		for (ArenaPlayer ap : ar.getArenaPlayers())
		{
			if (ap != null && ! ap.isOut())
			{
				kdrMap.put(ap.getUsername(), ap.getKDR());
			}
		}
		
		final List<Map.Entry<String, Double>> sortedEntries = new ArrayList<Map.Entry<String, Double>>(kdrMap.entrySet());
		Collections.sort(
		sortedEntries, new Comparator<Map.Entry<String, Double>>()
		{
			@Override
			public int compare(final Entry<String, Double> entry1, final Entry<String, Double> entry2)
			{
				return -entry1.getValue().compareTo(entry2.getValue());
			}
		});
		
		int pos = 1;
		for (Map.Entry<String, Double> entry : sortedEntries)
		{
			String string = entry.getKey();
			ArenaPlayer apl = plugin.getArenaPlayer(Util.matchPlayer(string));
			if (apl != null)
			{
				StringBuilder line = new StringBuilder();
				line.append(FormatUtil.format("&6#{0}. ", pos));
				line.append(FormatUtil.format(decideColor(apl)));
				line.append(FormatUtil.format(apl.getUsername().equals(sender.getName()) ? "&l" : ""));
				line.append(FormatUtil.format(apl.getUsername() + "&r"));
				line.append(FormatUtil.format("  &7Kills: &6{0}", apl.getKills()));
				line.append(FormatUtil.format("  &7Deaths: &6{0}", apl.getDeaths()));
				line.append(FormatUtil.format("  &7KDR: &6{0}", entry.getValue()));
				leaderboard.add(line.toString());
				pos++;
			}
		}
		
		return leaderboard;
	}
	
	private String decideColor(ArenaPlayer pl)
	{
		if (pl.getTeam() == 1)
		{
			return "&c";
		}
		else if (pl.getTeam() == 2)
		{
			return "&9";
		}
		else
		{
			return "&d";
		}
	}
}