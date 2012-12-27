package com.orange451.UltimateArena;

import org.bukkit.Location;

public class Field {
	public int minx;
	public int miny;
	public int maxx;
	public int maxy;
	public int width;
	public int length;
	
	public Field(double x, double z, double x2, double z2) {
		setParam(x, z, x2, z2);
	}

	public Field() {
	}

	public void setParam(double x, double z, double x2, double z2) {
		this.minx = (int)x;
		this.miny = (int)z;
		this.maxx = (int)x2;
		this.maxy = (int)z2;
		
		if (minx > maxx) {
			maxx = minx;
			minx = (int)x2;
		}
		
		if (miny > maxy) {
			maxy = miny;
			miny = (int)z2;
		}
		
		this.width = maxx-minx;
		this.length = maxy-miny;
	}
	
	public boolean isInside(Location loc) {
		int locx = loc.getBlockX();
		int locz = loc.getBlockZ();
		if (locx >= minx && locx <= maxx) {
			if (locz >= miny && locz <= maxy) {
				return true;
			}
		}
		return false;
	}
}
