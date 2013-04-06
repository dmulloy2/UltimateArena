package com.orange451.UltimateArena.Arenas;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import com.orange451.UltimateArena.Arenas.Objects.ArenaPlayer;
import com.orange451.UltimateArena.Arenas.Objects.ArenaSpawn;
import com.orange451.UltimateArena.Arenas.Objects.ArenaZone;
import com.orange451.UltimateArena.util.Util;

public class HUNGERArena extends Arena {

	public HUNGERArena(ArenaZone az) {
		super(az);
		
		type = "Hunger";
		starttimer = 300;
		maxgametime = 60 * 60 * 4;
		maxDeaths = 0;
		allowTeamKilling = true;
		
		for (int i = 0; i < this.az.spawns.size(); i++) {
			this.spawns.add( new ArenaSpawn(this.az.spawns.get(i).getBlockX(), this.az.spawns.get(i).getBlockY(), this.az.spawns.get(i).getBlockZ()) );
		}
	}
	
	@Override
	public void spawn(String name, boolean alreadySpawned) {
		super.spawn(name, false);
		spawnRandom(name);
		Player p = Util.MatchPlayer(name);
		if (p != null) {
			Random rand = new Random();
			int dat = rand.nextInt(16);
		
			MaterialData data = new MaterialData(Material.WOOL.getId());
			data.setData((byte) dat);
			ItemStack itm = data.toItemStack(1); 
			p.getInventory().setHelmet(itm);
		}
	}

	@Override
	public void check() {
		if (starttimer <= 0) {
			if (isEmpty()) {
				if (amtPlayersInArena == 1) {
					this.setWinningTeam(-1);
					stop();
					for (int i = 0; i < arenaplayers.size(); i++) {
						spawn(arenaplayers.get(i).username, false);
					}
					if (this.amtPlayersStartingInArena > 1) {
						this.rewardTeam(winningTeam, ChatColor.BLUE + "You won!", false);
					}else{
						this.tellPlayers(ChatColor.BLUE + "Not enough people to play!");
					}
				}
			}
		}
	}
	
	@Override
	public void onPlayerDeath(ArenaPlayer pl) {
		super.onPlayerDeath(pl);
		pl.player.getWorld().strikeLightningEffect(pl.player.getLocation());
	}
	
}
