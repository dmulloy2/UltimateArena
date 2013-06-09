package com.orange451.UltimateArena.util;

import java.util.List;
import java.util.Random;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import com.orange451.UltimateArena.UltimateArena;

public class Util 
{
	public static Server server;
	public static void initialize(UltimateArena plugin)
	{
		Util.server = plugin.getServer();
	}
	
	public static Player matchPlayer(String pl)
	{
		List<Player> players = server.matchPlayer(pl);
		
		if (players.size() >= 1)
			return players.get(0);
		
		return null;
	}
	
	public static OfflinePlayer matchOfflinePlayer(String pl)
	{
		if (matchPlayer(pl) != null)
			return matchPlayer(pl);
		
		for (OfflinePlayer o : server.getOfflinePlayers())
		{
			if (o.getName().equalsIgnoreCase(pl))
				return o;
		}
		
		return null;
	}
	
	public static boolean isBanned(OfflinePlayer p)
	{
		for (OfflinePlayer banned : server.getBannedPlayers()) 
		{
			if (p.getName().equalsIgnoreCase(banned.getName()))
				return true;
		}
		return false;
	}

	public static int random(int x)
	{
		Random rand = new Random();
		return rand.nextInt(x);
	}

	public static double pointDistance(Location loc1, Location loc2)
	{
		int xdist = loc1.getBlockX() - loc2.getBlockX();
		int ydist = loc1.getBlockY() - loc2.getBlockY();
		int zdist = loc2.getBlockY() - loc2.getBlockZ();
		
		return Math.sqrt(xdist * xdist + ydist * ydist + zdist * zdist);
	}

	public static void playEffect(Effect effect, Location loc, int i) 
	{
		for (Player player : server.getOnlinePlayers())
		{
			player.playEffect(loc, effect, i);
		}
	}
}