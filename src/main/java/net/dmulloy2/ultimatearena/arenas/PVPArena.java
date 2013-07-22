package net.dmulloy2.ultimatearena.arenas;

import net.dmulloy2.ultimatearena.arenas.objects.ArenaZone;
import net.dmulloy2.ultimatearena.arenas.objects.FieldType;

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
		if (startTimer <= 0)
		{
			if (!simpleTeamCheck(false)) 
			{
				this.setWinningTeam(-1);
				this.stop();
				this.rewardTeam(-1, "&9You won!", false);
			}
			else
			{
				if (getStartingAmount() <= 1) 
				{
					this.tellPlayers("&9Not enough people to play!");
					this.stop();
				}
			}
		}
	}
}