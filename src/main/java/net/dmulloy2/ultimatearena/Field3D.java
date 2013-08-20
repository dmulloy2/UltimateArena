package net.dmulloy2.ultimatearena;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class Field3D extends Field
{
	protected int miny;
	protected int maxy;
	
	private int height;
	
	public Field3D(World world, int maxx, int maxy, int maxz, int minx, int miny, int minz)
	{
		setParam(world, maxx, maxy, maxz, minx, miny, minz);
	}

	public Field3D() 
	{
	}
	
	public void setParam(World world, int maxx, int maxy, int maxz, int minx, int miny, int minz)
	{
		super.setParam(world, maxx, maxz, minx, minz);
		
		this.maxy = maxy;
		this.miny = miny;
		
		if (miny > maxy)
		{
			this.maxy = miny;
			this.miny = maxy;
		}

		this.height = maxy - miny;
	}
	
	public Block getBlockAt(int x, int y, int z) 
	{
		return world.getBlockAt(minx + x, miny + y, minz + z);
	}
	
	@Override
	public boolean isInside(Location loc) 
	{
		if (super.isInside(loc)) 
		{
			int locy = loc.getBlockY();
			return (locy >= miny && locy <= maxy);
		}
		
		return false;
	}
	
	public boolean isUnder(Location loc)
	{
		if (super.isInside(loc))
		{
			return (loc.getBlockY() < miny);
		}
		
		return false;
	}
	
	public void setType(Material mat) 
	{
		setType(mat.getId());
	}
	
	public void setType(final int id)
	{
		for (int i = minx; i <= maxx; i++)
		{
			for (int ii = miny; ii <= maxy; ii++)
			{
				for (int iii = minz; iii <= maxz; iii++)
				{
					Block b = world.getBlockAt(i, ii, iii);
					b.setTypeId(id);
				}
			}
		}
	}
	
	public int getVolume()
	{
		return getHeight() * getArea();
	}

	public int getHeight()
	{
		return Math.abs(height);
	}
}