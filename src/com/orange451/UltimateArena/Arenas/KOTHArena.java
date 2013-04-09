package com.orange451.UltimateArena.Arenas;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.orange451.UltimateArena.Arenas.Objects.ArenaPlayer;
import com.orange451.UltimateArena.Arenas.Objects.ArenaSpawn;
import com.orange451.UltimateArena.Arenas.Objects.ArenaZone;
import com.orange451.UltimateArena.Arenas.Objects.KothFlag;
import com.orange451.UltimateArena.util.Util;

public class KOTHArena extends Arena {
	public int MAXPOWER;
	
	public KOTHArena(ArenaZone az) {
		super(az);
		
		type = "Koth";
		allowTeamKilling = true;
		starttimer = 180;
		gametimer = 0;
		maxgametime = 60 * 20;
		maxDeaths = 900; //'dont think anyone will get 900 deaths :P
		MAXPOWER = 60;
		
		for (int i = 0; i < this.az.flags.size(); i++) {
			this.flags.add( new KothFlag(this, this.az.flags.get(i)) );
		}
		for (int i = 0; i < this.az.spawns.size(); i++) {
			this.spawns.add( new ArenaSpawn(this.az.spawns.get(i).getWorld(), this.az.spawns.get(i).getBlockX(), this.az.spawns.get(i).getBlockY(), this.az.spawns.get(i).getBlockZ()) );
		}
	}
	
	@Override
	public void doKillStreak(ArenaPlayer ap) {
		try{
			Player pl = Util.MatchPlayer(ap.player.getName());
			if (pl != null) {
				if (ap.killstreak == 2) {
					giveItem(pl, Material.POTION.getId(), (byte)9, 1, "2 kills! Unlocked strength potion!");
				}
				if (ap.killstreak == 4) {
					giveItem(pl, Material.POTION.getId(), (byte)1, 1, "4 kills! Unlocked Health potion!");
					giveItem(pl, Material.GRILLED_PORK.getId(), (byte)0, 2, "4 kills! Unlocked food!");
				}
				if (ap.killstreak == 12) {
					giveItem(pl, Material.POTION.getId(), (byte)1, 1, "12 kills! Unlocked Health potion!");
					giveItem(pl, Material.GRILLED_PORK.getId(), (byte)0, 2, "12 kills! Unlocked food!");
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void reward(ArenaPlayer p, Player pl, boolean half) {
		if (p.points >= MAXPOWER) { //if you scored at least 60 points
			super.reward(p, pl, half);
		}
	}
	
	@Override
	public void spawn(String name, boolean alreadySpawned) {
		super.spawn(name, false);
		spawnRandom(name);
	}
	
	@Override
	public void onStart() {
		for (int i = 0; i < arenaplayers.size(); i++) {
			spawn(arenaplayers.get(i).username, false);
		}
	}
	
	@Override
	public void check() {
		for (int i = 0; i < flags.size(); i++) {
			flags.get(i).step();
			flags.get(i).checkNear(arenaplayers);
		}
		
		checkPlayerPoints(MAXPOWER);
		checkEmpty();
	}
	
}
