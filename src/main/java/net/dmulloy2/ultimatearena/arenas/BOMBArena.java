package net.dmulloy2.ultimatearena.arenas;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.dmulloy2.ultimatearena.arenas.objects.ArenaPlayer;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaZone;
import net.dmulloy2.ultimatearena.arenas.objects.BombFlag;
import net.dmulloy2.ultimatearena.util.Util;

public class BOMBArena extends Arena 
{
	public int REDTEAMPOWER = 100;
	public BombFlag bomb1;
	public BombFlag bomb2;
	
	public BOMBArena(ArenaZone az) 
	{
		super(az);
		
		setType("Bomb");
		setStarttimer(120);
		setGametimer(0);
		setMaxgametime(60 * 15);
		setMaxDeaths(990);
		
		bomb1 = new BombFlag(this, az.getFlags().get(0));
		bomb2 = new BombFlag(this, az.getFlags().get(1));
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
		rewardTeam(2, ChatColor.BLUE + "You won!", false);
	}
	
	public synchronized void onPlayerDeath(ArenaPlayer pl)
	{
		getPlugin().debug("Bomb: Player {0} has died!", pl.getUsername());
		
		if (pl.getTeam() == 1) 
		{
			REDTEAMPOWER--;
			for (int i = 0; i < getArenaplayers().size(); i++) 
			{
				if (!getArenaplayers().get(i).isOut())
				{ 
					Player pl1 = getArenaplayers().get(i).getPlayer();
					if (pl1 != null) {
						if (getArenaplayers().get(i).getTeam() == 1) 
						{
							pl1.sendMessage(ChatColor.RED + "Your power is now: " + ChatColor.GOLD + REDTEAMPOWER);
						}
						else
						{
							pl1.sendMessage(ChatColor.RED + "Other teams' power is now: " + ChatColor.GOLD + REDTEAMPOWER);
						}
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
		bomb1.checkNear(getArenaplayers());
		bomb2.checkNear(getArenaplayers());
		
		if (bomb1.isExploded() && bomb2.isExploded())
		{
			setWinningTeam(1);
			tellPlayers(ChatColor.GRAY + "Red team won!");
			stop();
			rewardTeam(1, ChatColor.BLUE + "You won!", false);
			return;
		}
		
		if (REDTEAMPOWER <= 0) 
		{
			setWinningTeam(2);
			tellPlayers(ChatColor.GRAY + "Blue team won!");
			for (int i = 0; i < getArenaplayers().size(); i++)
			{
				ArenaPlayer ap = getArenaplayers().get(i);
				if (!ap.isOut()) {
					if (ap.getTeam() == 1)
					{
						ap.setOut(true);
						setUpdatedTeams(true);
						Player p = Util.matchPlayer(ap.getPlayer().getName());
						if (p != null) 
						{
							p.sendMessage(ChatColor.RED + "Your team lost! :(");
							endPlayer(ap, false);
						}
					}
				}
			}
			stop();
			rewardTeam(2, ChatColor.BLUE + "You won!", false);
		}
	}
}