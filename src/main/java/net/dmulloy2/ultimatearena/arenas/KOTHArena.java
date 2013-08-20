package net.dmulloy2.ultimatearena.arenas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.dmulloy2.ultimatearena.arenas.objects.ArenaFlag;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaPlayer;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaSpawn;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaZone;
import net.dmulloy2.ultimatearena.arenas.objects.FieldType;
import net.dmulloy2.ultimatearena.arenas.objects.KothFlag;
import net.dmulloy2.ultimatearena.util.FormatUtil;
import net.dmulloy2.ultimatearena.util.Util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class KOTHArena extends Arena
{
	public int MAXPOWER;
	public KOTHArena(ArenaZone az) 
	{
		super(az);
		
		this.type = FieldType.KOTH;
		this.startTimer = 180;
		this.maxGameTime = 60 * 20;
		this.maxDeaths = 900;
		this.allowTeamKilling = true;
		this.MAXPOWER = 60;

		for (int i = 0; i < az.getFlags().size(); i++)
		{
			flags.add( new KothFlag(this, az.getFlags().get(i), plugin) );
		}
		for (int i = 0; i < az.getSpawns().size(); i++) 
		{
			spawns.add( new ArenaSpawn(az.getSpawns().get(i)) );
		}
	}
	
	@Override
	public void doKillStreak(ArenaPlayer ap) 
	{
		Player pl = Util.matchPlayer(ap.getPlayer().getName());
		if (pl != null) 
		{
			if (ap.getKillstreak() == 2) 
			{
				givePotion(pl, "strength", 1, 1, false, "2 kills! Unlocked strength potion!");
			}
			if (ap.getKillstreak() == 4) 
			{
				givePotion(pl, "heal", 1, 1, false, "4 kills! Unlocked health potion!");
				giveItem(pl, Material.GRILLED_PORK.getId(), (byte)0, 2, "4 kills! Unlocked food!");
			}
			if (ap.getKillstreak() == 12) 
			{
				givePotion(pl, "regen", 1, 1, false, "12 kills! Unlocked regen potion!");
				giveItem(pl, Material.GRILLED_PORK.getId(), (byte)0, 2, "12 kills! Unlocked food!");
			}
		}
	}
	
	@Override
	public void reward(ArenaPlayer p, Player pl, boolean half) 
	{
		if (p.getPoints() >= MAXPOWER) 
		{ 
			// If you scored at least 60 points
			super.reward(p, pl, half);
		}
	}
	
	@Override
	public Location getSpawn(ArenaPlayer ap)
	{
		if (isInLobby())
		{
			return super.getSpawn(ap);
		}
		
		return getRandomSpawn(ap);
	}

	@Override
	public void check()
	{
		for (ArenaFlag flag : getFlags())
		{
			flag.step();
			flag.checkNear(arenaPlayers);
		}

		checkPlayerPoints(MAXPOWER);
		checkEmpty();
	}
	
	@Override
	public List<String> buildLeaderboard(Player player)
	{
		List<String> leaderboard = new ArrayList<String>();
		
		// Build kills map
		HashMap<String, Integer> pointsMap = new HashMap<String, Integer>();
		for (int i = 0; i < arenaPlayers.size(); i++)
		{
			ArenaPlayer ap = arenaPlayers.get(i);
			if (ap != null && ! ap.isOut())
			{
				pointsMap.put(ap.getName(), ap.getPoints());
			}
		}
		
		final List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<Map.Entry<String, Integer>>(pointsMap.entrySet());
		Collections.sort(
		sortedEntries, new Comparator<Map.Entry<String, Integer>>()
		{
			@Override
			public int compare(final Entry<String, Integer> entry1, final Entry<String, Integer> entry2)
			{
				return -entry1.getValue().compareTo(entry2.getValue());
			}
		});
		
		int pos = 1;
		for (Map.Entry<String, Integer> entry : sortedEntries)
		{
			String string = entry.getKey();
			ArenaPlayer apl = plugin.getArenaPlayer(Util.matchPlayer(string));
			if (apl != null)
			{
				StringBuilder line = new StringBuilder();
				line.append(FormatUtil.format("&6#{0}. ", pos));
				line.append(FormatUtil.format(decideColor(apl)));
				line.append(FormatUtil.format(apl.getName().equals(player.getName()) ? "&l" : ""));
				line.append(FormatUtil.format(apl.getName() + "&r"));
				line.append(FormatUtil.format("  &7Kills: &6{0}", apl.getKills()));
				line.append(FormatUtil.format("  &7Deaths: &6{0}", apl.getDeaths()));
				line.append(FormatUtil.format("  &7Points: &6{0}", entry.getValue()));
				leaderboard.add(line.toString());
				pos++;
			}
		}
		
		return leaderboard;
	}
	
	@Override
	public void announceWinner()
	{
		// Do nothing...
	}
}