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
package net.dmulloy2.ultimatearena.arenas.bomb;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.Team;

/**
 * @author dmulloy2
 */

@Getter
public class BombArena extends Arena
{
	private BombFlag bomb1;
	private BombFlag bomb2;
	private int redTeamPower;

	public BombArena(ArenaZone az)
	{
		super(az);
		this.redTeamPower = 1;

		this.bomb1 = new BombFlag(this, az.getFlags().get(0), plugin);
		this.bomb1.setBombNumber(1);

		this.bomb2 = new BombFlag(this, az.getFlags().get(1), plugin);
		this.bomb2.setBombNumber(2);
	}

	@Override
	public void check()
	{
		if (startTimer <= 0)
		{
			if (! simpleTeamCheck())
			{
				tellPlayers("&3One team is empty! Game ended!");

				stop();
				return;
			}
		}

		bomb1.checkNear(getActivePlayers());
		bomb2.checkNear(getActivePlayers());

		if (bomb1.isExploded() && bomb2.isExploded())
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
	}

	@Override
	public List<String> getExtraInfo()
	{
		return Arrays.asList("&3Red Team Power: &e" + redTeamPower);
	}

	@Override
	public Team getTeam()
	{
		return getBalancedTeam();
	}

	@Override
	public void onOutOfTime()
	{
		rewardTeam(Team.BLUE);
	}

	@Override
	public void onPlayerDeath(ArenaPlayer pl)
	{
		if (pl.getTeam() == Team.RED)
		{
			redTeamPower--;
			for (ArenaPlayer ap : active)
			{
				if (ap.getTeam() == Team.RED)
				{
					ap.sendMessage("&3Your power is now: &e{0}", redTeamPower);
				}
				else
				{
					ap.sendMessage("&3The other team''s power is now: &e{0}", redTeamPower);
				}
			}
		}
	}

	@Override
	public void onPreOutOfTime()
	{
		setWinningTeam(Team.BLUE);
	}

	@Override
	public void onStart()
	{
		this.redTeamPower = Math.min(150, Math.max(10, active.size() * 3));
	}
}
