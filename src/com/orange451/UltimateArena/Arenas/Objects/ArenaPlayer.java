package com.orange451.UltimateArena.Arenas.Objects;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import com.orange451.UltimateArena.Arenas.Arena;
import com.orange451.UltimateArena.util.Util;

public class ArenaPlayer {
	public int kills      = 0;
	public int deaths     = 0;
	public int killstreak = 0;
	public int XP         = 0;
	public int team       = 1;
	public int points     = 0;
	public int baselevel  = 0;
	public int amtkicked = 0;
	public float startxp  = 0;
	public boolean out    = false;
	public boolean canReward = false;
	public String username;
	public int healtimer = 0;
	public ArenaClass mclass;
	public Location spawnBack;
	public Arena inArena;
	public Player player;
	
	public ArenaPlayer(Player p, Arena a){
		this.player = p;
		this.username = p.getName();
		this.inArena = a;
		this.spawnBack = p.getLocation().clone();
		this.mclass = a.az.plugin.getArenaClass(a.az.defaultClass);
		this.baselevel = p.getLevel();
		this.startxp = p.getExp();
	}
	
	public void decideHat(Player p) {
		if (p.getInventory().getHelmet() == null) {
			int teamcolor = 14;
			if (team == 2) {
				teamcolor = 11;
			}
			MaterialData data = new MaterialData(Material.WOOL.getId());
			data.setData((byte) teamcolor);
			ItemStack itm = data.toItemStack(1); 
			p.getInventory().setHelmet(itm);
		}
	}
	
	public void giveItem(Player p, int weapon1, byte dat, int amt, int slot) {
		if (weapon1 > 0) {
			Material mat = Material.getMaterial(weapon1);
			if (mat != null) {
				if (!mat.equals(Material.AIR)) {
					MaterialData data = new MaterialData(mat);
					data.setData(dat);
					ItemStack itm = data.toItemStack(amt);
					p.getInventory().setItem(slot, itm);
				}
			}
		}
	}
	
	public void spawn() {
		try{
			if (this.amtkicked > 10) {
				this.inArena.az.plugin.leaveArena(this.player);
			}
			Player p = Util.MatchPlayer(player.getName());
			p.getInventory().clear();
			if (inArena.starttimer <= 0 && inArena.gametimer >= 2) {
				if (mclass == null) {
					p.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE, 1));
					p.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS, 1));
					p.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS, 1));
					p.getInventory().setItem(0, new ItemStack(Material.DIAMOND_SWORD, 1));
				}else{
					try{
						if (mclass.armor1 > 0) { p.getInventory().setChestplate(new ItemStack(Material.getMaterial(mclass.armor1), 1)); }
						if (mclass.armor2 > 0) { p.getInventory().setLeggings(new ItemStack(Material.getMaterial(mclass.armor2), 1)); }
						if (mclass.armor3 > 0) { p.getInventory().setBoots(new ItemStack(Material.getMaterial(mclass.armor3), 1)); }
						
						giveItem(p, mclass.weapon1, mclass.special1, mclass.amt1, 0);
						giveItem(p, mclass.weapon2, mclass.special2, mclass.amt2, 1);
						giveItem(p, mclass.weapon3, mclass.special3, mclass.amt3, 2);
						giveItem(p, mclass.weapon4, mclass.special4, mclass.amt4, 3);
						giveItem(p, mclass.weapon5, mclass.special5, mclass.amt5, 4);
						giveItem(p, mclass.weapon6, mclass.special6, mclass.amt6, 5);
						giveItem(p, mclass.weapon7, mclass.special7, mclass.amt7, 6);
						giveItem(p, mclass.weapon8, mclass.special8, mclass.amt8, 7);
						giveItem(p, mclass.weapon9, mclass.special9, mclass.amt9, 8);
					}catch(Exception e) {
						//e.printStackTrace();
						System.out.println("[ULTIMATEARENA] Error giving player class items!");
					}
				}
			}else{
			}
			//set team color
			decideHat(p);
		}catch(Exception e) {
			//
		}
	}
}
