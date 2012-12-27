package com.orange451.UltimateArena.Arenas.Objects;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.Arenas.Arena;

public class flagBase {
	public Location loc;
	public Block notify = null;
	public Arena arena;
	public UltimateArena plugin;
	
	public flagBase(Arena arena, Location loc) {
		this.arena = arena;
		Location safe = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
		this.setLoc(safe.clone().subtract(0, 1, 0));
		this.plugin = arena.az.plugin;
		
		setup();
	}
	
	public void setup() {
		//set up flag
		try{
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			    public void run() {
			    	Location flag = getLoc().clone().add(0, 5, 0);
			    	notify = flag.getBlock();
					notify.setType(Material.WOOL);
					((getLoc().clone()).add(1, 0, 0)).getBlock().setType(Material.STONE);
					((getLoc().clone()).add(1, 0, 1)).getBlock().setType(Material.STONE);
					((getLoc().clone()).add(-1, 0, -1)).getBlock().setType(Material.STONE);
					((getLoc().clone()).add(1, 0, -1)).getBlock().setType(Material.STONE);
					((getLoc().clone()).add(-1, 0, 1)).getBlock().setType(Material.STONE);
					((getLoc().clone()).add(2, 0, 0)).getBlock().setType(Material.STONE);
					((getLoc().clone()).add(-1, 0, 0)).getBlock().setType(Material.STONE);
					((getLoc().clone()).add(-2, 0, 0)).getBlock().setType(Material.STONE);
					((getLoc().clone()).add(0, 0, 1)).getBlock().setType(Material.STONE);
					((getLoc().clone()).add(0, 0, 2)).getBlock().setType(Material.STONE);
					((getLoc().clone()).add(0, 0, -1)).getBlock().setType(Material.STONE);
					((getLoc().clone()).add(0, 0, -2)).getBlock().setType(Material.STONE);
				}
			});
		}catch(Exception e) {
			System.out.println("[ULTIMATEARENA] Error setting up flag. Arena: " + arena);
		}
	}
	
	public synchronized void checkNear(ArrayList<ArenaPlayer> arenaplayers) {
		
	}

	public Location getLoc() {
		return loc;
	}

	public void setLoc(Location loc) {
		this.loc = loc;
	}
}
