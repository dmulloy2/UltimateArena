package net.dmulloy2.ultimatearena.arenas;

import java.util.List;

import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.FieldType;

/**
 * @author dmulloy2
 */

public class PVPArena extends Arena
{
	private ArenaPlayer winner;
	
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
			
			if (! simpleTeamCheck(false))
			{
				setWinningTeam(-1);
				
				List<ArenaPlayer> validPlayers = getValidPlayers();
				if (! validPlayers.isEmpty())
				{
					this.winner = validPlayers.get(0);
				}
				
				stop();
				
				rewardTeam(-1, false);
			}
		}
	}
	
	@Override
	public void announceWinner()
	{
		if (winner != null)
			tellAllPlayers("&e{0} &3won the match at &e{1}&3!", winner.getName(), name);
	}
}