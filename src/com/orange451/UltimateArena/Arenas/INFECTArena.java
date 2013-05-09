package com.orange451.UltimateArena.Arenas;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.orange451.UltimateArena.Arenas.Objects.ArenaPlayer;
import com.orange451.UltimateArena.Arenas.Objects.ArenaZone;
import com.orange451.UltimateArena.util.Util;

public class INFECTArena extends PVPArena {

	ArenaPlayer originalZombie = null;
	
	public INFECTArena(ArenaZone az) {
		super(az);
		
		type = "Infect";
		starttimer = 80;
		gametimer = 0;
		maxgametime = (60 * 2) + 10; //2 minutes and 10 seconds (by default)
		maxDeaths = 99;
	}
	
	@Override
	public int getTeam() {
		return 1; //blue team = default
	}
	
	@Override
	public void onStart() {
		super.onStart();
		chooseInfected(0);
	}
	
	public void chooseInfected(int tries) {
		if (tries < 16) {
			ArenaPlayer apl = this.arenaplayers.get(Util.random(this.arenaplayers.size()));
			if (apl != null && apl.player.isOnline()) {
				apl.player = Util.matchPlayer(apl.username);
				apl.team = 2;
				//spawn(apl.player.getName(), true);
				apl.player.sendMessage(ChatColor.BLUE + "You have been chosen for the infected!");
				onSpawn(apl);
				this.tellPlayers(ChatColor.AQUA + apl.player.getName() + ChatColor.BLUE + " is the zombie!");
			}else{
				chooseInfected(tries+1);
			}
		}else{
			this.tellPlayers(ChatColor.RED + "Error starting!");
			stop();
		}
	}

	@Override
	public void onSpawn(ArenaPlayer apl) {
		if (apl.team == 2) {
			Player pl = apl.player;
			normalize(apl.player);
			//pl.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 2400, 1));
			pl.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 2400, 2));
			pl.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 2400, 1));
			pl.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 2400, 1));
			pl.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 2400, 1));
			spawn(apl.player.getName(), true);
			normalize(apl.player);
			apl.decideHat(apl.player);
		}
	}

	@Override
	public void check() {
		try{
			if (starttimer <= 0) {
				if (!simpleTeamCheck(false)) {
					if (this.team1size == 0) {
						this.setWinningTeam(2);
						this.tellPlayers(ChatColor.BLUE + "Infected Win!");
						this.stop();
						this.rewardTeam(2, ChatColor.YELLOW + "You win!", true);
					}else{
						this.tellPlayers(ChatColor.BLUE + "One team is empty! game ended!");
						this.stop();
					}
				}else{
					if (this.amtPlayersStartingInArena <= 1) {
						this.tellPlayers(ChatColor.BLUE + "Not enough people to play!");
						this.stop();
					}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onPreOutOfTime() {
		this.setWinningTeam(1);
	}
	
	@Override
	public void onOutOfTime() {
		this.rewardTeam(1, ChatColor.BLUE + "You survived!", false);
	}
	
	@Override
	public void onPlayerDeath(ArenaPlayer pl) {
		if (pl.team == 1) {
			pl.player.sendMessage(ChatColor.AQUA + "You have joined the Infected!");
		}
		pl.team = 2;
	}
	
}
