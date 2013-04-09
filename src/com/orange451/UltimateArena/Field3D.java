package com.orange451.UltimateArena;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class Field3D extends Field{
	//I know my Y and Z are inverted... I'm just more used to that
	public World world;
	public int minz;
	public int maxz;
	public int height;
	public UltimateArena plugin;
	
	public Field3D(World world, double x, double y, double z, double x2, double y2, double z2) {
		setParam(world, x, y, z, x2, y2, z2);
	}

	public Field3D() {
	}

	public Field3D(UltimateArena plugin, World world) {
		this.plugin = plugin;
		this.world = world;
	}

	public void setParam(World world, double x, double y, double z, double x2, double y2, double z2) {
		setParam(world, x, y, x2, y2);
		
		this.minz = (int)z;
		this.maxz = (int)z2;
		
		if (minz > maxz) {
			maxz = minz;
			minz = (int)z2;
		}
		
		this.height = maxz-minz;
	}
	
	public Block getBlockAt(int i, int ii, int iii) {
		return world.getBlockAt(minx + i, minz + ii, miny + iii);
	}
	
	public boolean isInside(Location loc) {
		if (super.isInside(loc)) {
			int locy = loc.getBlockY();
			World locw = loc.getWorld();
			if (locy >= minz && locy <= maxz && locw == world) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isUnder(Location loc) {
		if (super.isInside(loc)) {
			if (loc.getBlockY() < maxz) {
				return true;
			}
		}
		return false;
	}
	
	public void setType(Material mat) {
		setType(mat.getId());
	}
	
	public void setType(final int id) {
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				for (int i = minx; i <= maxx; i++) {
					for (int ii = miny; ii <= maxy; ii++) {
						for (int iii = minz; iii <= maxz; iii++) {
							Block b = world.getBlockAt(i, iii, ii);
							b.setTypeId(id);
						}
					}
				}
			}
		});
	}
}

