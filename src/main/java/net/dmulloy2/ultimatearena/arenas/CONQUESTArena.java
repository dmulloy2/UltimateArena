package net.dmulloy2.ultimatearena.arenas;

import java.util.ArrayList;
import java.util.List;

import net.dmulloy2.ultimatearena.arenas.objects.ArenaFlag;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaPlayer;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaZone;
import net.dmulloy2.ultimatearena.util.Util;

import org.bukkit.entity.Player;

public class CONQUESTArena extends Arena
{
	public int REDTEAMPOWER = 100;
	public int BLUETEAMPOWER = 100;
	
	public CONQUESTArena(ArenaZone az) 
	{
		super(az);
		
		setType("Cq");
		setStarttimer(180);
		setGametimer(0);
		setMaxgametime(60 * 20);
		setMaxDeaths(900);
		
		for (int i = 0; i < this.getArenaZone().getFlags().size(); i++) 
		{
			this.getFlags().add( new ArenaFlag(this, this.getArenaZone().getFlags().get(i)) );
		}
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		this.REDTEAMPOWER = getAmtPlayersInArena() * 4;
		this.BLUETEAMPOWER = REDTEAMPOWER;
		if (REDTEAMPOWER < 4) 
		{
			REDTEAMPOWER = 4;
		}
		if (REDTEAMPOWER > 150) 
		{
			REDTEAMPOWER = 150;
		}
		if (BLUETEAMPOWER < 4) 
		{
			BLUETEAMPOWER = 4;
		}
		if (BLUETEAMPOWER > 150)
		{
			BLUETEAMPOWER = 150;
		}
	}
	
	@Override
	public void spawn(String name, boolean alreadySpawned) 
	{
		super.spawn(name, false);
		Player p = Util.matchPlayer(name);
		if (p != null) 
		{
			ArenaPlayer ap = this.getArenaZone().getPlugin().getArenaPlayer(p);
			if (ap != null) 
			{
				if (!ap.isOut())
				{
					List<ArenaFlag> spawnto = new ArrayList<ArenaFlag>();
					for (int i = 0; i < getFlags().size(); i++)
					{
						if (getFlags().get(i).team == ap.getTeam())
						{
							if (getFlags().get(i).capped)
							{
								spawnto.add(getFlags().get(i));
							}
						}
					}
					
					if (spawnto.size() > 0) 
					{
						p.teleport((spawnto.get(Util.random(spawnto.size())).getLoc().clone()).add(0,2,0));
					}
				}
			}
		}
	}
	
	public void onPlayerDeath(ArenaPlayer pl) 
	{
		int majority = 0;
		int red = 0;
		int blu = 0;
		for (int i = 0; i < getFlags().size(); i++) 
		{
			if (getFlags().get(i).color == 14) 
			{
				if (getFlags().get(i).capped == true) 
				{
					red++;
				}
			}
			else if (getFlags().get(i).color == 11) 
			{
				if (getFlags().get(i).capped == true) 
				{
					blu++;
				}
			}
		}
		
		if (blu > red) { majority = 1; }
		if (red > blu) { majority = 2; }
	
		if (majority == 1) 
		{
			REDTEAMPOWER--;
		}
		else if (majority == 2) 
		{
			BLUETEAMPOWER--;
		}
		
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
		else if (pl.getTeam() == 2) 
		{
			BLUETEAMPOWER--;
			for (int i = 0; i < arenaPlayers.size(); i++) 
			{
				ArenaPlayer apl = arenaPlayers.get(i);
				if (apl != null && ! apl.isOut())
				{ 
					if (apl.getTeam() == 2)
					{
						apl.sendMessage("&cYour power is now: &6{0}", BLUETEAMPOWER);
					}
					else
					{
						apl.sendMessage("&cThe other team's power is now: &6{0}", BLUETEAMPOWER);
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
		for (ArenaPlayer ap : arenaPlayers)
		{
			if (ap != null && !ap.isOut())
			{
				if (BLUETEAMPOWER <= 0) 
				{
					if (ap.getTeam() == 2)
					{
						ap.setOut(true);
						setUpdatedTeams(true);
						
						ap.sendMessage("&cYour team lost!");
						endPlayer(ap, false);
					}
				}
				else if (REDTEAMPOWER <= 0) 
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
		}
		
		if (BLUETEAMPOWER <= 0)
		{
			this.tellPlayers("&9Red team won!");
			this.setWinningTeam(1);
		}
		
		if (REDTEAMPOWER <= 0) 
		{
			this.tellPlayers("&9Blue team won!");
			this.setWinningTeam(2);
		}
			
		for (int i = 0; i < getFlags().size(); i++)
		{
			ArenaFlag flag = getFlags().get(i);
			
			flag.step();
			flag.checkNear(arenaPlayers);
		}
		
		if (getStarttimer() <= 0) 
		{
			if (!simpleTeamCheck(false)) 
			{
				stop();
				this.rewardTeam(-1, "&9You won!", false);
			}
		}
	}
}