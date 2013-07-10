package net.dmulloy2.ultimatearena.arenas;

import net.dmulloy2.ultimatearena.arenas.objects.ArenaPlayer;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaZone;
import net.dmulloy2.ultimatearena.arenas.objects.BombFlag;
import net.dmulloy2.ultimatearena.arenas.objects.FieldType;

public class BOMBArena extends Arena 
{
	public int REDTEAMPOWER = 100;
	public BombFlag bomb1;
	public BombFlag bomb2;
	
	public BOMBArena(ArenaZone az) 
	{
		super(az);
		
		this.type = FieldType.BOMB;
		setStarttimer(120);
		setGametimer(0);
		setMaxgametime(60 * 15);
		setMaxDeaths(990);
		
		bomb1 = new BombFlag(this, az.getFlags().get(0), plugin);
		bomb2 = new BombFlag(this, az.getFlags().get(1), plugin);
		bomb1.setBnum(1);
		bomb2.setBnum(2);
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		this.REDTEAMPOWER = getAmtPlayersInArena() * 3;
		if (REDTEAMPOWER < 10)
		{
			REDTEAMPOWER = 10;
		}
		if (REDTEAMPOWER > 150) 
		{
			REDTEAMPOWER = 150;
		}
	}
	
	@Override
	public void onOutOfTime() 
	{
		setWinningTeam(2);
		rewardTeam(2, "&9You won!", false);
	}
	
	public synchronized void onPlayerDeath(ArenaPlayer pl)
	{
		if (pl.getTeam() == 1) 
		{
			REDTEAMPOWER--;
			for (int i = 0; i < arenaPlayers.size(); i++)
			{
				ArenaPlayer apl = arenaPlayers.get(i);
				if (apl != null && ! apl.isOut())
				{
					if (apl.getTeam() == 1)
					{
						apl.sendMessage("&cYour power is now: &6{0}", REDTEAMPOWER);
					}
					else
					{
						apl.sendMessage("&cThe other team's power is now: &6{0}", REDTEAMPOWER);
					}
				}
			}
		}
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
			simpleTeamCheck(true);
		}
		bomb1.checkNear(arenaPlayers);
		bomb2.checkNear(arenaPlayers);
		
		if (bomb1.isExploded() && bomb2.isExploded())
		{
			setWinningTeam(1);
			tellPlayers("&6Red team won!");
			stop();
			rewardTeam(1, "&9You won!", false);
			return;
		}
		
		if (REDTEAMPOWER <= 0) 
		{
			setWinningTeam(2);
			tellPlayers("&6Blue team won!");
			for (int i = 0; i < arenaPlayers.size(); i++)
			{
				ArenaPlayer ap = arenaPlayers.get(i);
				if (!ap.isOut()) 
				{
					if (ap.getTeam() == 1)
					{
						ap.setOut(true);
						setUpdatedTeams(true);

						ap.sendMessage("&cYour team lost!");
						endPlayer(ap, false);
					}
				}
			}
			
			stop();
			rewardTeam(2, "&9You won!", false);
		}
	}
}