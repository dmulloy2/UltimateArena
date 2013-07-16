package net.dmulloy2.ultimatearena;

import org.bukkit.Location;
import org.bukkit.World;

public class Field
{
	protected int minx;
	protected int maxx;
	
	protected int minz;
	protected int maxz;

	private int width;
	private int length;
	
	protected World world;

	public Field(World world, int maxx, int maxz, int minx, int minz)
	{
		setParam(world, maxx, minx, maxz, minz);
	}

	public Field() 
	{
	}

	public void setParam(World world, int maxx, int maxz, int minx, int minz)
	{
		this.world = world;
		
		this.maxx = maxx;
		this.minx = minx;
		
		this.maxz = maxz;
		this.minz  = minz;
		
		if (minx > maxx)
		{
			this.maxx = minx;
			this.minx = maxx;
		}
		
		if (minz > maxz)
		{
			this.maxz = minz;
			this.minz = maxz;
		}
		
		this.length = maxx - minx;
		this.width = maxz - minz;
	}
	
	public boolean isInside(Location loc)
	{
		World locw = loc.getWorld();
		int locx = loc.getBlockX();
		int locz = loc.getBlockZ();
		if (world.getUID() == locw.getUID())
		{
			if (locx >= minx && locx <= maxx)
			{
				return (locz >= minz && locz <= maxz);
			}
		}
		
		return false;
	}
	
	public String toString()
	{
		StringBuilder string = new StringBuilder();
		string.append("World: " + world.getName());
		string.append(" MaxX: " + maxx);
		string.append(" MaxZ: " + maxz);
		string.append(" MinX: " + minx);
		string.append(" MinZ: " + minz);
		
		return string.toString();
	}
	
	public int getArea()
	{
		return getLength() * getWidth();
	}

	public int getLength()
	{
		return Math.abs(length);
	}

	public int getWidth() 
	{
		return Math.abs(width);
	}
}