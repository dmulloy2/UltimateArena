package net.dmulloy2.ultimatearena.arenas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.dmulloy2.ultimatearena.flags.ArenaFlag;
import net.dmulloy2.ultimatearena.flags.KothFlag;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.FieldType;
import net.dmulloy2.ultimatearena.util.FormatUtil;
import net.dmulloy2.ultimatearena.util.Util;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class KOTHArena extends Arena
{
	protected int maxPower;

	public KOTHArena(ArenaZone az)
	{
		super(az);

		this.type = FieldType.KOTH;
		this.startTimer = 180;
		this.maxGameTime = 60 * 20;
		this.maxDeaths = 900;
		this.allowTeamKilling = true;
		this.maxPower = 60;

		for (int i = 0; i < az.getFlags().size(); i++)
		{
			flags.add(new KothFlag(this, az.getFlags().get(i), plugin));
		}
		for (int i = 0; i < az.getSpawns().size(); i++)
		{
			spawns.add(az.getSpawns().get(i));
		}
	}

	@Override
	public void reward(ArenaPlayer ap)
	{
		if (ap.getPoints() >= maxPower)
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
			flag.step();
			flag.checkNear(getActivePlayers());
		}

		checkPlayerPoints(maxPower);
		checkEmpty();
	}

	@Override
	public List<String> getLeaderboard(Player player)
	{
		List<String> leaderboard = new ArrayList<String>();

		// Build kills map
		HashMap<String, Integer> pointsMap = new HashMap<String, Integer>();
		for (ArenaPlayer ap : active)
		{
			pointsMap.put(ap.getName(), ap.getPoints());
		}

		final List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<Map.Entry<String, Integer>>(pointsMap.entrySet());
		Collections.sort(sortedEntries, new Comparator<Map.Entry<String, Integer>>()
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
				line.append(FormatUtil.format("&3#{0}. ", pos));
				line.append(FormatUtil.format(decideColor(apl)));
				line.append(FormatUtil.format(apl.getName().equals(player.getName()) ? "&l" : ""));
				line.append(FormatUtil.format(apl.getName() + "&r"));
				line.append(FormatUtil.format("  &3Kills: &e{0}", apl.getKills()));
				line.append(FormatUtil.format("  &3Deaths: &e{0}", apl.getDeaths()));
				line.append(FormatUtil.format("  &3Points: &e{0}", entry.getValue()));
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
}