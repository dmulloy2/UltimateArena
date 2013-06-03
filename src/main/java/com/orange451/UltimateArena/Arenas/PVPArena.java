package com.orange451.UltimateArena.Arenas;

import org.bukkit.ChatColor;

import com.orange451.UltimateArena.Arenas.Objects.ArenaZone;

public class PVPArena extends Arena 
{
	public PVPArena(ArenaZone az) 
	{
		super(az);
		
		type = "Pvp";
		starttimer = 120;
		gametimer = 0;
		maxgametime = 60 * 10;
		maxDeaths = 3;
	}
	
	@Override
	public int getTeam() 
	{
		return getBalancedTeam();
	}

	@Override
	public void check() 
	{
		if (starttimer <= 0)
		{
			if (!simpleTeamCheck(false)) 
			{
				this.setWinningTeam(-1);
				this.stop();
				this.rewardTeam(-1, ChatColor.BLUE + "You won!", false);
			}
		}
	}
}