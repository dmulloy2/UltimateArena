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
package net.dmulloy2.ultimatearena.arenas.pvp;

import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.Team;

/**
 * @author dmulloy2
 */

public class PvPArena extends Arena
{
	public PvPArena(ArenaZone az)
	{
		super(az);
	}

	@Override
	public void announceWinner()
	{
		tellAllPlayers("&e{0} team &3won the match at &e{1}", getWinningTeam(), name);
	}

	@Override
	public void check()
	{
		if (isInGame())
		{
			if (startingAmount <= 1)
			{
				tellPlayers("&3Not enough people to play!");

				stop();
				return;
			}

			if (! simpleTeamCheck())
			{
				setWinningTeam(null);

				stop();

				rewardTeam(null);
			}
		}
	}

	@Override
	public Team getTeam()
	{
		return getBalancedTeam();
	}

	@Override
	public Team getWinningTeam()
	{
		if (winningTeam == null)
		{
			if (redTeamSize > 0)
				return Team.RED;

			if (blueTeamSize > 0)
				return Team.BLUE;
		}

		return winningTeam;
	}
}
