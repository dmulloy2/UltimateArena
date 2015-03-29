/**
 * UltimateArena - fully customizable PvP arenas
 * Copyright (C) 2012 - 2015 MineSworn
 * Copyright (C) 2013 - 2015 dmulloy2
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.dmulloy2.ultimatearena.arenas.koth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lombok.Getter;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaFlag;
import net.dmulloy2.ultimatearena.types.ArenaLocation;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.ArenaZone;
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

		for (ArenaLocation loc : az.getFlags())
		{
			flags.add(new KOTHFlag(this, loc, plugin));
		}

		spawns.addAll(az.getSpawns());
	}

	@Override
	public void reward(ArenaPlayer ap)
	{
		if (ap.getDataInt("kothPoints") >= maxPoints)
		{
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

	/**
	 * Checks if a player has enough points to win.
	 *
	 * @param max Max points for an arena
	 */
	public final void checkPlayerPoints(int max)
	{
		for (ArenaPlayer ap : getActivePlayers())
		{
			if (ap.getDataInt("kothPoints") >= max)
			{
				tellAllPlayers("&3Player &e{0} &3has won!", ap.getName());

				stop();

				reward(ap);
			}
		}
	}

	@Override
	public List<ArenaPlayer> getLeaderboard()
	{
		Map<ArenaPlayer, Integer> pointsMap = new HashMap<>();
		for (ArenaPlayer ap : getActivePlayers())
		{
			pointsMap.put(ap, ap.getDataInt("kothPoints"));
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
			pointsMap.put(ap.getName(), ap.getDataInt("kothPoints"));
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
				line.append(ap.getTeam().getColor());
				line.append(FormatUtil.format(ap.getName().equals(player.getName()) ? "&l" : ""));
				line.append(FormatUtil.format(ap.getName() + "&r"));
				line.append(FormatUtil.format("  &3Kills: &e{0}", ap.getKills()));
				line.append(FormatUtil.format("  &3Deaths: &e{0}", ap.getDeaths()));
				line.append(FormatUtil.format("  &3Points: &e{0}", ap.getDataInt("kothPoints")));
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

	@Override
	public KOTHConfig getConfig()
	{
		return (KOTHConfig) super.getConfig();
	}
}
