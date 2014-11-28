/**
 * (c) 2014 dmulloy2
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