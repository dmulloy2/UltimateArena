package com.orange451.UltimateArena.Arenas;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import com.orange451.UltimateArena.Arenas.Objects.ArenaFlag;
import com.orange451.UltimateArena.Arenas.Objects.ArenaPlayer;
import com.orange451.UltimateArena.Arenas.Objects.ArenaZone;
import com.orange451.UltimateArena.util.Util;

public class CONQUESTArena extends Arena
{
	public int REDTEAMPOWER = 100;
	public int BLUETEAMPOWER = 100;
	
	public CONQUESTArena(ArenaZone az) 
	{
		super(az);
		
		type = "Cq";
		starttimer = 180;
		gametimer = 0;
		maxgametime = 60 * 20;
		maxDeaths = 900;
		
		for (int i = 0; i < this.az.flags.size(); i++) 
		{
			this.flags.add( new ArenaFlag(this, this.az.flags.get(i)) );
		}
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		this.REDTEAMPOWER = amtPlayersInArena * 4;
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
			ArenaPlayer ap = this.az.plugin.getArenaPlayer(p);
			if (ap != null) 
			{
				if (!ap.out)
				{
					List<ArenaFlag> spawnto = new ArrayList<ArenaFlag>();
					for (int i = 0; i < flags.size(); i++)
					{
						if (flags.get(i).team == ap.team)
						{
							if (flags.get(i).capped)
							{
								spawnto.add(flags.get(i));
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
		for (int i = 0; i < flags.size(); i++) 
		{
			if (flags.get(i).color == 14) 
			{
				if (flags.get(i).capped == true) 
				{
					red++;
				}
			}
			else if (flags.get(i).color == 11) 
			{
				if (flags.get(i).capped == true) 
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
		
		if (pl.team == 1) 
		{
			REDTEAMPOWER--;
			for (int i = 0; i < arenaplayers.size(); i++) 
			{
				if (!arenaplayers.get(i).out){ 
					Player pl1 = arenaplayers.get(i).player;
					if (pl1 != null) {
						if (arenaplayers.get(i).team == 1) 
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
		else if (pl.team == 2) 
		{
			BLUETEAMPOWER--;
			for (int i = 0; i < arenaplayers.size(); i++) 
			{
				if (!arenaplayers.get(i).out)
				{
					Player pl1 = arenaplayers.get(i).player;
					if (pl1 != null) 
					{
						if (arenaplayers.get(i).team == 2)
						{
							pl1.sendMessage(ChatColor.RED + "Your power is now: " + ChatColor.GOLD + BLUETEAMPOWER);
						}
						else
						{
							pl1.sendMessage(ChatColor.RED + "Other teams' power is now: " + ChatColor.GOLD + BLUETEAMPOWER);
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
		for (ArenaPlayer ap : arenaplayers)
		{
			if (ap != null && !ap.out)
			{
				if (BLUETEAMPOWER <= 0) 
				{
					if (ap.team == 2)
					{
						ap.out = true;
						updatedTeams = true;
						Player p = Util.matchPlayer(ap.player.getName());
						if (p != null) 
						{
							p.sendMessage(ChatColor.RED + "Your team lost!");
							endPlayer(ap, false);
						}
					}
				}
				else if (REDTEAMPOWER <= 0) 
				{
					if (ap.team == 1)
					{
						ap.out = true;
						updatedTeams = true;
						Player p = Util.matchPlayer(ap.player.getName());
						if (p != null)
						{
							p.sendMessage(ChatColor.RED + "Your team lost!");
							endPlayer(ap, false);
						}
					}
				}
			}
		}
		
		if (BLUETEAMPOWER <= 0)
		{
			this.tellPlayers(ChatColor.RED + "Red team won!");
			this.setWinningTeam(1);
		}
		
		if (REDTEAMPOWER <= 0) 
		{
			this.tellPlayers(ChatColor.RED + "Blue team won!");
			this.setWinningTeam(2);
		}
			
		for (int i = 0; i < flags.size(); i++)
		{
			flags.get(i).step();
			flags.get(i).checkNear(arenaplayers);
		}
		
		if (starttimer <= 0) 
		{
			if (!simpleTeamCheck(false)) 
			{
				stop();
				this.rewardTeam(-1, ChatColor.BLUE + "You won!", false);
			}
		}
	}
}