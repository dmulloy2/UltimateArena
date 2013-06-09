package com.orange451.UltimateArena.Arenas.Objects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.orange451.UltimateArena.Arenas.Arena;
import com.orange451.UltimateArena.util.Util;

public class ArenaFlag extends flagBase
{
	public int power;
	public int color = 14;
	public int team = 0;
	public int added = 0;
	public boolean capped = false;
	
	public ArenaFlag(Arena arena, Location loc) 
	{
		super(arena, loc);
	}
	
	public synchronized void setup() 
	{
		super.setup();
		getLoc().getBlock().setType(Material.WOOL);
	}
	
	public void step() 
	{
		capped = false;
		color = 8;
		team = 0;
		if (added > 50) 
		{
			color = 14;
			team = 1;
		}
		if (added < -50)
		{
			color = 11;
			team = 2;
		}
		if (added >= 150)
		{ 
			added = 150; 
			capped = true; 
		}
		if (added <= -150)
		{
			added = -150;
			capped = true;
		}
		try
		{
			class FlagTask extends BukkitRunnable
			{
				@Override
				public void run()
				{
					if (capped)
					{
						notify.setData((byte) color);
					}
					else
					{
						notify.setData((byte) 8);
					}
				}
			}
			
			new FlagTask().runTask(plugin);
		}
		catch(Exception e) 
		{
			//
		}
	}
	
	@Override
	public synchronized void checkNear(List<ArenaPlayer> arenaplayers) 
	{
		int team1 = 0;
		int team2 = 0;
		List<Player> players = new ArrayList<Player>();
		synchronized(players) 
		{
			for (int i = 0; i < arenaplayers.size(); i++)
			{
				Player pl = arenaplayers.get(i).player;
				if (pl != null) 
				{
					if (Util.pointDistance(pl.getLocation(), getLoc()) < 4.5 && pl.getHealth() > 0)
					{
						players.add(pl);
						if (arenaplayers.get(i).team == 1)
						{
							team1++;
						}
						else
						{
							team2++;
						}
					}
				}
			}
		}
		
		int percent = 0;
		if (color == 14)
		{
			percent = added-50;
		}
		else if (color == 11)
		{
			percent = Math.abs(added)-50;
		}
		else
		{
			percent = added+50;
		}
		
		if (team1 > team2)
		{
			added += (team1-team2) * 5;
			for (int i = 0; i < players.size(); i++)
			{
				if (percent < 100) 
				{
					players.get(i).sendMessage(ChatColor.GRAY + "Capping! " + ChatColor.GOLD + percent  +"%");
				}
			}
		}
		else if (team2 > team1) 
		{
			added -= (team2-team1) * 5;
			for (int i = 0; i < players.size(); i++) 
			{
				if (percent < 100) 
				{
					players.get(i).sendMessage(ChatColor.GRAY + "Capping! " + ChatColor.GOLD + percent  +"%");
				}
			}
		}
	}
}