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
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
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
		Team winner = getWinningTeam();
		if (winner != null)
			tellAllPlayers(getMessage("teamWon"), winner, name);
	}

	@Override
	public Team getTeam()
	{
		return getBalancedTeam();
	}

	@Override
	public void onPlayerEnd(ArenaPlayer ap)
	{
		if (isInGame())
		{
			// Make sure both teams still have players on them
			if (redTeamSize == 0)
			{
				// Blue team wins
				setWinningTeam(Team.BLUE);
				stop();
				rewardTeam(Team.BLUE);
			}
			else if (blueTeamSize == 0)
			{
				// Red team wins
				setWinningTeam(Team.RED);
				stop();
				rewardTeam(Team.RED);
			}
		}
	}

	@Override
	public void onReload()
	{
		// There must be at least 2 players or the game won't work properly
		this.minPlayers = Math.max(2, minPlayers);
	}
}