package com.orange451.UltimateArena;

import org.bukkit.Location;
import org.bukkit.World;

public class Field {
	public int minx;
	public int miny;
	public int maxx;
	public int maxy;
	public int width;
	public int length;
	private World world;
	
	public Field(World world, double x, double z, double x2, double z2) 
	{
		setParam(world, x, z, x2, z2);
	}

	public Field() 
	{
	}

	public void setParam(World world, double x, double z, double x2, double z2) 
	{
		this.minx = (int)x;
		this.miny = (int)z;
		this.maxx = (int)x2;
		this.maxy = (int)z2;
		
		if (minx > maxx) 
		{
			maxx = minx;
			minx = (int)x2;
		}
		
		if (miny > maxy)
		{
			maxy = miny;
			miny = (int)z2;
		}
		
		this.width = maxx-minx;
		this.length = maxy-miny;
		
		this.world = world;
	}
	
	public boolean isInside(Location loc)
	{
		int locx = loc.getBlockX();
		int locz = loc.getBlockZ();
		World locw = loc.getWorld();
		if (locx >= minx && locx <= maxx && locw == world)
		{
			if (locz >= miny && locz <= maxy)
			{
				return true;
			}
		}
		return false;
	}
}