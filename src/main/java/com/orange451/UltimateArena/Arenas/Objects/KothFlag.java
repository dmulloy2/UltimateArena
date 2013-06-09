package com.orange451.UltimateArena.Arenas.Objects;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.orange451.UltimateArena.Arenas.KOTHArena;
import com.orange451.UltimateArena.util.Util;

public class KothFlag extends ArenaFlag
{
	public KOTHArena marena;
	public KothFlag(KOTHArena arena, Location loc)
	{
		super(arena, loc);
		this.marena = arena;
	}
	
	public void checkNear(ArrayList<ArenaPlayer> arenaplayers) 
	{
		int amt = 0;
		ArenaPlayer capturer = null;
		ArrayList<Player> players = new ArrayList<Player>();
		for (int i = 0; i < arenaplayers.size(); i++)
		{
			Player pl = arenaplayers.get(i).player;
			if (pl != null)
			{
				if (Util.pointDistance(pl.getLocation(), getLoc()) < 3.0 && pl.getHealth() > 0) 
				{
					players.add(pl);
					amt++;
					capturer = arenaplayers.get(i);
				}
			}
		}
		
		if (amt == 1) 
		{
			if (capturer != null) 
			{
				Player pl = capturer.player;
				capturer.points++;
				pl.sendMessage(ChatColor.GRAY + "You have capped for 1 point! " + ChatColor.LIGHT_PURPLE + capturer.points + " / " + marena.MAXPOWER);
			}
		}
	}
}