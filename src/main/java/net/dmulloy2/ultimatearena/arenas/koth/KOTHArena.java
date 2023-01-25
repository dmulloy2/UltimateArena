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
import java.util.List;

import net.dmulloy2.swornapi.types.CustomScoreboard;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaFlag;
import net.dmulloy2.ultimatearena.types.ArenaLocation;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.swornapi.util.FormatUtil;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import lombok.Getter;

/**
 * @author dmulloy2
 */

public class KOTHArena extends Arena
{
	private static final String POINTS_KEY = "kothPoints";

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
		if (ap.getDataInt(POINTS_KEY) >= maxPoints)
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
			flag.checkNear(getActivePlayers());

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
			if (ap.getDataInt(POINTS_KEY) >= max)
			{
				tellAllPlayers(getMessage("playerWon"), ap.getName());

				stop();

				reward(ap);
			}
		}
	}

	@Override
	public List<ArenaPlayer> getLeaderboard()
	{
		if (leaderboard != null) return leaderboard;

		return this.leaderboard = ArenaPlayer.dataSorter(POINTS_KEY, 0).sort(getActivePlayers());
	}

	@Override
	public List<String> getLeaderboard(Player player)
	{
		List<String> leaderboard = new ArrayList<>();

		int pos = 1;
		for (ArenaPlayer ap : getLeaderboard())
		{
			leaderboard.add(FormatUtil.format(getMessage("kothLb"),
					pos, decideColor(ap), ap.getName().equals(player.getName()) ? "&l" : "", ap.getName(), ap.getKills(), ap.getDeaths(), ap.getDataInt(POINTS_KEY, 0)
			));
			pos++;
		}

		return leaderboard;
	}

	/**
	 * This is handled in {@link #checkPlayerPoints(int)}
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

	@Override
	public void addScoreboardEntries(CustomScoreboard board, ArenaPlayer player)
	{
		board.addEntry("Points", player.getDataInt(POINTS_KEY));
	}
}
