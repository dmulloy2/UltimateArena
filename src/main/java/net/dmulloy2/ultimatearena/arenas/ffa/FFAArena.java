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
package net.dmulloy2.ultimatearena.arenas.ffa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.ArenaZone;

import org.bukkit.Location;

/**
 * @author dmulloy2
 */

public class FFAArena extends Arena
{
	protected ArenaPlayer winner;

	public FFAArena(ArenaZone az)
	{
		super(az);
		spawns.addAll(az.getSpawns());
	}

	@Override
	public void announceWinner()
	{
		if (winner != null)
			tellAllPlayers(getMessage("winner"), winner.getName(), name);
	}

	@Override
	protected String decideColor(ArenaPlayer ap)
	{
		return "&d";
	}

	@Override
	public void decideHat(ArenaPlayer ap)
	{
		ap.decideHat(true);
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
	public void onPlayerEnd(ArenaPlayer ap)
	{
		if (isInGame())
		{
			// Last player standing wins
			if (active.size() == 1)
			{
				this.winner = active.get(0);

				setWinningTeam(null);
				stop();
				rewardTeam(null);
			}
		}
	}

	@Override
	public void onReload()
	{
		// Two players required
		this.minPlayers = Math.max(2, minPlayers);
	}

	private ArenaPlayer mostKills()
	{
		List<ArenaPlayer> players = getActive();
		if (players.isEmpty()) return null;
		if (players.size() == 1) return players.get(0);

		Map<ArenaPlayer, Integer> map = new HashMap<>();
		for (ArenaPlayer ap : players)
			map.put(ap, ap.getKills());

		List<Entry<ArenaPlayer, Integer>> sortedEntries = new ArrayList<>(map.entrySet());
		Collections.sort(sortedEntries, new Comparator<Entry<ArenaPlayer, Integer>>()
		{
			@Override
			public int compare(Entry<ArenaPlayer, Integer> entry1, Entry<ArenaPlayer, Integer> entry2)
			{
				return -entry1.getValue().compareTo(entry2.getValue());
			}
		});

		return sortedEntries.get(0).getKey();
	}

	@Override
	public void onPreOutOfTime()
	{
		switch (winCondition)
		{
			case LAST_MAN_STANDING:
				break;
			case MOST_KILLS:
			{
				ArenaPlayer ap = mostKills();
				if (ap != null)
				{
					ap.setCanReward(true);
					this.winner = ap;
				}
				break;
			}
			case BEST_KDR:
			{
				List<ArenaPlayer> leaderboard = getLeaderboard();
				if (! leaderboard.isEmpty())
				{
					ArenaPlayer ap = leaderboard.get(0);
					ap.setCanReward(true);
					this.winner = ap;
				}
				break;
			}
		}
	}

	@Override
	public void onOutOfTime()
	{
		switch (winCondition)
		{
			case LAST_MAN_STANDING:
				break;
			case MOST_KILLS:
			case BEST_KDR:
				rewardTeam(null);
				break;
		}
	}
}