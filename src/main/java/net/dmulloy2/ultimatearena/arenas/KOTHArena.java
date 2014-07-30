package net.dmulloy2.ultimatearena.arenas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lombok.Getter;
import net.dmulloy2.ultimatearena.flags.ArenaFlag;
import net.dmulloy2.ultimatearena.flags.KothFlag;
import net.dmulloy2.ultimatearena.types.ArenaLocation;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.FieldType;
import net.dmulloy2.util.FormatUtil;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class KOTHArena extends Arena
{
	private @Getter int maxPoints;

	public KOTHArena(ArenaZone az)
	{
		super(az);
		this.type = FieldType.KOTH;

		for (ArenaLocation loc : az.getFlags())
		{
			flags.add(new KothFlag(this, loc, plugin));
		}

		spawns.addAll(az.getSpawns());
	}

	@Override
	public void reward(ArenaPlayer ap)
	{
		if (ap.getPoints() >= maxPoints)
		{
			// If you scored at least 60 points
			super.reward(ap);
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
		for (ArenaFlag flag : flags)
		{
			flag.checkNear(getActivePlayers());
		}

		checkPlayerPoints(maxPoints);
		checkEmpty();
	}

	@Override
	public List<ArenaPlayer> getLeaderboard()
	{
		Map<ArenaPlayer, Integer> pointsMap = new HashMap<>();
		for (ArenaPlayer ap : getActivePlayers())
		{
			pointsMap.put(ap, ap.getPoints());
		}

		List<Entry<ArenaPlayer, Integer>> sortedEntries = new ArrayList<>(pointsMap.entrySet());
		Collections.sort(sortedEntries, new Comparator<Entry<ArenaPlayer, Integer>>()
		{
			@Override
			public int compare(Entry<ArenaPlayer, Integer> entry1, Entry<ArenaPlayer, Integer> entry2)
			{
				return -entry1.getValue().compareTo(entry2.getValue());
			}
		});

		List<ArenaPlayer> leaderboard = new ArrayList<>();
		for (Entry<ArenaPlayer, Integer> entry : sortedEntries)
		{
			leaderboard.add(entry.getKey());
		}

		return leaderboard;
	}

	@Override
	public List<String> getLeaderboard(Player player)
	{
		List<String> leaderboard = new ArrayList<String>();

		// Build kills map
		Map<String, Integer> pointsMap = new HashMap<>();
		for (ArenaPlayer ap : active)
		{
			pointsMap.put(ap.getName(), ap.getPoints());
		}

		List<Entry<String, Integer>> sortedEntries = new ArrayList<>(pointsMap.entrySet());
		Collections.sort(sortedEntries, new Comparator<Entry<String, Integer>>()
		{
			@Override
			public int compare(Entry<String, Integer> entry1, Entry<String, Integer> entry2)
			{
				return -entry1.getValue().compareTo(entry2.getValue());
			}
		});

		int pos = 1;
		for (ArenaPlayer ap : getLeaderboard())
		{
			if (ap != null)
			{
				StringBuilder line = new StringBuilder();
				line.append(FormatUtil.format("&3#{0}. ", pos));
				line.append(FormatUtil.format(decideColor(ap)));
				line.append(FormatUtil.format(ap.getName().equals(player.getName()) ? "&l" : ""));
				line.append(FormatUtil.format(ap.getName() + "&r"));
				line.append(FormatUtil.format("  &3Kills: &e{0}", ap.getKills()));
				line.append(FormatUtil.format("  &3Deaths: &e{0}", ap.getDeaths()));
				line.append(FormatUtil.format("  &3Points: &e{0}", ap.getPoints()));
				leaderboard.add(line.toString());
				pos++;
			}
		}

		return leaderboard;
	}

	/**
	 * This is handled in {@link Arena#checkPlayerPoints(int)}
	 */
	@Override
	public void announceWinner()
	{
		//
	}

	@Override
	public void onReload()
	{
		this.maxPoints = getConfig().getMaxPoints();
	}
}