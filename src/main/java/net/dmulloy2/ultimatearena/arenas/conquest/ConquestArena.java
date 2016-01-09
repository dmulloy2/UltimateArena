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
package net.dmulloy2.ultimatearena.arenas.conquest;

import java.util.ArrayList;
import java.util.List;

import net.dmulloy2.types.CustomScoreboard;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaFlag;
import net.dmulloy2.ultimatearena.types.ArenaLocation;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.Team;
import net.dmulloy2.util.Util;

import org.bukkit.Location;

/**
 * @author dmulloy2
 */

public class ConquestArena extends Arena
{
	private int blueTeamPower;
	private int redTeamPower;

	public ConquestArena(ArenaZone az)
	{
		super(az);
		this.redTeamPower = 1;
		this.blueTeamPower = 1;
		this.limitSpam = true;

		for (ArenaLocation loc : az.getFlags())
		{
			flags.add(new ConquestFlag(this, loc, plugin));
		}
	}

	@Override
	public void addScoreboardEntries(CustomScoreboard board, ArenaPlayer player)
	{
		board.addEntry("Red Power", redTeamPower);
		board.addEntry("Blue Power", blueTeamPower);
	}

	@Override
	public void check()
	{
		if (isInGame())
		{
			// If either team has no power, the other team wins

			if (blueTeamPower <= 0)
			{
				setWinningTeam(Team.RED);
				stop();
				rewardTeam(Team.RED);
				return;
			}

			if (redTeamPower <= 0)
			{
				setWinningTeam(Team.BLUE);
				stop();
				rewardTeam(Team.BLUE);
				return;
			}

			// If either team has all the flags, they win

			int red = 0;
			int blue = 0;

			for (ArenaFlag flag : getFlags())
			{
				flag.checkNear(getActivePlayers());

				if (flag.isCapped())
				{
					if (flag.getOwningTeam() == Team.RED)
						red++;
					else
						blue++;
				}
			}

			if (red == flags.size())
			{
				setWinningTeam(Team.RED);
				stop();
				rewardTeam(Team.RED);
				return;
			}

			if (blue == flags.size())
			{
				setWinningTeam(Team.BLUE);
				stop();
				rewardTeam(Team.BLUE);
				return;
			}

			// If either team is empty, it's no contest

			if (! simpleTeamCheck())
			{
				setWinningTeam(null);
				stop();
				rewardTeam(null);
			}
		}
	}

	@Override
	public List<String> getExtraInfo()
	{
		List<String> ret = new ArrayList<>();
		ret.add("&3Red Team Power: &e" + redTeamPower);
		ret.add("&3Blue Team Power: &e" + blueTeamPower);
		return ret;
	}

	@Override
	public Location getSpawn(ArenaPlayer ap)
	{
		if (isInLobby())
			return super.getSpawn(ap);

		List<ArenaFlag> spawnto = new ArrayList<ArenaFlag>();
		for (ArenaFlag flag : getFlags())
		{
			if (flag.getOwningTeam() == ap.getTeam())
			{
				if (flag.isCapped())
					spawnto.add(flag);
			}
		}

		if (! spawnto.isEmpty())
		{
			int rand = Util.random(spawnto.size());
			ArenaFlag flag = spawnto.get(rand);
			if (flag != null)
				return flag.getLocation();
		}
		else
		{
			return super.getSpawn(ap);
		}

		return null;
	}

	@Override
	public Team getTeam()
	{
		return getBalancedTeam();
	}

	@Override
	public void onPlayerDeath(ArenaPlayer pl)
	{
		int red = 0;
		int blue = 0;

		for (ArenaFlag flag : getFlags())
		{
			if (flag.getOwningTeam() == Team.RED)
			{
				if (flag.isCapped())
					red++;
			}
			else if (flag.getOwningTeam() == Team.BLUE)
			{
				if (flag.isCapped())
					blue++;
			}
		}

		if (red > blue)
			blueTeamPower -= (red - blue);
		else if (blue > red)
			redTeamPower -= (blue - red);

		if (pl.getTeam() == Team.RED)
		{
			redTeamPower--;
			for (ArenaPlayer ap : active)
			{
				if (ap.getTeam() == Team.RED)
					ap.sendMessage(getMessage("yourPower"), redTeamPower);
				else
					ap.sendMessage(getMessage("theirPower"), redTeamPower);
			}
		}
		else if (pl.getTeam() == Team.BLUE)
		{
			blueTeamPower--;
			for (ArenaPlayer ap : active)
			{
				if (ap.getTeam() == Team.BLUE)
					ap.sendMessage(getMessage("yourPower"), blueTeamPower);
				else
					ap.sendMessage(getMessage("theirPower"), blueTeamPower);
			}
		}
	}

	@Override
	public void onReload()
	{
		this.minPlayers = Math.max(2, minPlayers);
	}

	@Override
	public void onStart()
	{
		this.redTeamPower = Math.max(4, Math.min(150, active.size() * 4));
		this.blueTeamPower = redTeamPower;
	}
}