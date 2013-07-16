package net.dmulloy2.ultimatearena.arenas;

import net.dmulloy2.ultimatearena.arenas.objects.ArenaZone;
import net.dmulloy2.ultimatearena.arenas.objects.FieldType;

public class PVPArena extends Arena 
{
	public PVPArena(ArenaZone az) 
	{
		super(az);
		
		this.type = FieldType.PVP;
		setStarttimer(120);
		setGametimer(0);
		setMaxgametime(60 * 10);
		setMaxDeaths(3);
	}
	
	@Override
	public int getTeam() 
	{
		return getBalancedTeam();
	}

	@Override
	public void check() 
	{
		if (getStarttimer() <= 0)
		{
			if (!simpleTeamCheck(false)) 
			{
				this.setWinningTeam(-1);
				this.stop();
				this.rewardTeam(-1, "&9You won!", false);
			}
			else
			{
				if (this.getAmtPlayersStartingInArena() <= 1) 
				{
					this.tellPlayers("&9Not enough people to play!");
					this.stop();
				}
			}
		}
	}
}