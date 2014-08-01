/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.ultimatearena.arenas.pvp;

import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.TeamHelper;

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
		tellAllPlayers("&e{0} team &3won the match at &e{1}", TeamHelper.getTeam(getWinningTeam()), name);
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
				setWinningTeam(-1);

				stop();

				rewardTeam(-1);
			}
		}
	}

	@Override
	public int getTeam()
	{
		return getBalancedTeam();
	}

	@Override
	public int getWinningTeam()
	{
		if (winningTeam == -1)
		{
			if (team1size > 0)
				return 1;

			if (team2size > 0)
				return 2;
		}

		return winningTeam;
	}
}