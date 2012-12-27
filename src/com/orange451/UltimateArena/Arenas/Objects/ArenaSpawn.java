package com.orange451.UltimateArena.Arenas.Objects;

import org.bukkit.Location;

import com.orange451.UltimateArena.util.Util;

public class ArenaSpawn {
	public int x;
	public int y;
	public int z;
	
	public ArenaSpawn(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Location getLocation() {
		return new Location(Util.world, x, y, z);
	}
	
	public void setLocation(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
}
