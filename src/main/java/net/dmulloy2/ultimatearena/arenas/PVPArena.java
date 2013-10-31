package net.dmulloy2.ultimatearena.arenas;

import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.FieldType;
import net.dmulloy2.ultimatearena.util.TeamHelper;

/**
 * @author dmulloy2
 */

public class PVPArena extends Arena
{
	public PVPArena(ArenaZone az)
	{
		super(az);

		this.type = FieldType.PVP;
		this.startTimer = 120;
		this.maxGameTime = 60 * 10;
		this.maxDeaths = 3;
	}

	@Override
	public int getTeam()
	{
		return getBalancedTeam();
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

	@Override
	public void announceWinner()
	{
		tellAllPlayers("&e{0} team &3won the match at &e{1}", TeamHelper.getTeam(getWinningTeam()), name);
	}
}