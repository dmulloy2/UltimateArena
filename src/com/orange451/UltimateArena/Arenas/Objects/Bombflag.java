package com.orange451.UltimateArena.Arenas.Objects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.orange451.UltimateArena.Arenas.Arena;
import com.orange451.UltimateArena.Arenas.BOMBArena;
import com.orange451.UltimateArena.util.Util;

public class Bombflag extends ArenaFlag{
	public int timer = 45;
	public int fuser = 0;
	public boolean fused = false;
	public boolean exploded = false;
	public int bnum;
	
	public Bombflag(Arena arena, Location loc) {
		super(arena, loc);
	}
	
	@Override
	public void checkNear(List<ArenaPlayer> arenaplayers) {
		if (fused) {
			timer--;
			Util.playEffect(Effect.STEP_SOUND, this.getLoc(), 4);

			if (timer == 30 || timer == 20 || timer == 10 || timer <= 5) {
				arena.tellPlayers(ChatColor.GRAY + "BOMB " + ChatColor.LIGHT_PURPLE + bnum + ChatColor.GRAY + " WILL EXPLODE IN " + ChatColor.LIGHT_PURPLE + timer + ChatColor.GRAY + "SECONDS");
			}
			
			if (timer < 1) {
				if (!exploded) {
					Util.playEffect(Effect.EXTINGUISH, this.getLoc(), 4);
					BOMBArena ba = null;
					try{
						ba = ((BOMBArena)arena);
					}catch(Exception e) {}
					if (ba != null) {
						int amte = 0;
						if (ba.bomb1.exploded)
							amte++;
						if (ba.bomb2.exploded)
							amte++;
						
						if (amte == 0)
							arena.killAllNear(this.getLoc(), 12);
					}
					exploded = true;
					fused = false;
					arena.tellPlayers(ChatColor.GRAY + "RED team blew up bomb " + ChatColor.LIGHT_PURPLE + bnum);
				}
			}
		}
		boolean fuse = false;
		boolean defuse = false;
		ArenaPlayer capturer = null;
		ArrayList<Player> players = new ArrayList<Player>();
		for (int i = 0; i < arenaplayers.size(); i++) {
			ArenaPlayer apl = arenaplayers.get(i);
			Player pl = apl.player;
			if (pl != null) {
				if (Util.point_distance(pl.getLocation(), getLoc()) < 3.0 && pl.getHealth() > 0) {
					players.add(pl);
					capturer = apl;
					if (apl.team == 1) {
						fuse = true;
					}else{
						defuse = true;
					}
				}
			}
		}
		
		if (!(fuse && defuse) && !exploded) {
			if (capturer != null) {
				Player pl = capturer.player;
				if (fuse) {
					if (!fused) { //team 1 is fusing
						fuser++;
						pl.sendMessage(ChatColor.GRAY + "FUSING BOMB " + bnum + ChatColor.LIGHT_PURPLE + " " + fuser + ChatColor.GRAY + "/10");
						if (fuser > 10) {
							fuser = 0;
							fused = true;
							arena.tellPlayers(ChatColor.GRAY + "BOMB " + bnum + " IS NOW: " + ChatColor.LIGHT_PURPLE + "FUSED");
						}
					}
				}else if (defuse) { //team 2 is desfusing
					if (fused) {
						fuser++;
						pl.sendMessage(ChatColor.GRAY + "DEFUSING BOMB " + bnum + ChatColor.LIGHT_PURPLE + " " + fuser + ChatColor.GRAY + "/10");
						if (fuser > 10) {
							fuser = 0;
							fused = false;
							timer = 45;
							arena.tellPlayers(ChatColor.GRAY + "BOMB " + bnum + " IS NOW: " + ChatColor.LIGHT_PURPLE + "DEFUSED");
						}
					}
				}
				
			}
		}
	}

}
