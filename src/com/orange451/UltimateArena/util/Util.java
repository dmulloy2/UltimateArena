package com.orange451.UltimateArena.util;

import java.util.List;
import java.util.Random;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import com.orange451.UltimateArena.UltimateArena;

public class Util {

	public static UltimateArena plugin;
	public static Server server;

	public static void Initialize(UltimateArena plugin) {
		Util.plugin = plugin;
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

	public static void playEffect(Effect e, Location l, int num) {
		for (int i = 0; i < server.getOnlinePlayers().length; i++) {
			server.getOnlinePlayers()[i].playEffect(l, e, num);
		}
	}

	public static double point_distance(Location loc1, Location loc2) {
		double p1x = loc1.getX();
		double p1y = loc1.getY();
		double p1z = loc1.getZ();

		double p2x = loc2.getX();
		double p2y = loc2.getY();
		double p2z = loc2.getZ();
		double xdist = (p1x - p2x);
		double ydist = (p1y - p2y);
		double zdist = (p1z - p2z);
		return Math.sqrt((xdist * xdist) + (ydist * ydist) + (zdist * zdist));
	}
	
    public static int random(int x) {
    	Random rand = new Random();
    	return rand.nextInt(x);
    }
    
    public static double lengthdir_x(double len, double dir) {
        return len * Math.cos(Math.toRadians(dir));
    }

    public static double lengthdir_y(double len, double dir) {
        return -len * Math.sin(Math.toRadians(dir));
    }
    
    public static double point_direction(double x1, double y1, double x2, double y2){
		double d;
		try{
			d = Math.toDegrees(Math.atan((y2-y1)/(x2-x1)));
		}catch(Exception e) {
			d = 0;
		}
		if (x1 > x2 && y1 > y2)
		{
			return -d+180;
		}
		if (x1 < x2 && y1 > y2)
		{
			return -d;
		}
		if (x1 == x2)
		{
			if (y1 > y2)
				return 90;
			if (y1 < y2)
				return 270;
		}
		if (x1 > x2 && y1 < y2)
		{
			return -d+180;
		}
		if (x1 < x2 && y1 < y2)
		{
			return -d+360;
		}
		if (y1 == y2)
		{
			if (x1 > x2)
				return 180;
			if (x1 < x2)
				return 0;
		}
		return 0;
    }
}