package com.orange451.UltimateArena.Arenas.Objects;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import com.orange451.UltimateArena.UltimateArena;

public class ArenaConfig {
	public int gameTime;
	public int lobbyTime;
	public int maxDeaths;
	public int maxwave;
	public boolean allowTeamKilling;
	public ArrayList<ArenaReward> rewards = new ArrayList<ArenaReward>();
	public String arenaName;
	public File file;
	public UltimateArena plugin;
	
	public ArenaConfig(UltimateArena plugin, String str, File f) {
		this.arenaName = str;
		this.file = f;
		this.plugin = plugin;
		
		load();
	}
	
	public void computeData(String str) {
		if (str.indexOf("=") > 0 ) {
			String str2 = str.substring(0, str.indexOf("="));
			if (str2.equalsIgnoreCase("maxwave")) {
				int value = Integer.parseInt(str.substring(str.indexOf("=")+1));
				maxwave = value;
			}
			if (str2.equalsIgnoreCase("gametime")) {
				int value = Integer.parseInt(str.substring(str.indexOf("=")+1));
				gameTime = value;
			}
			if (str2.equalsIgnoreCase("lobbytime")) {
				int value = Integer.parseInt(str.substring(str.indexOf("=")+1));
				lobbyTime = value;
			}
			if (str2.equalsIgnoreCase("maxdeaths")) {
				int value = Integer.parseInt(str.substring(str.indexOf("=")+1));
				maxDeaths = value;
			}
			if (str2.equalsIgnoreCase("allowteamkilling")) {
				boolean value = Boolean.parseBoolean(str.substring(str.indexOf("=")+1));
				allowTeamKilling = value;
			}
		}else if (str.indexOf(",") > 0 ) {
			getReward(str);
		}
	}
	
	public void giveRewards(final Player pl, final boolean half) {
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				for (int i = 0; i < rewards.size(); i++) {
					ArenaReward a = rewards.get(i);
					Material mat = Material.getMaterial(a.type);
					int amt = a.amt;
					byte dat = a.data;
					Inventory inv = pl.getInventory();
					MaterialData data = new MaterialData(mat.getId());
					
					if (amt < 1)
						amt = 1;
					
					if (dat > 0) {
						data.setData(dat);
						//itm = data.toItemStack(1);
					}
					if (half)
						amt = (int)(Math.floor(amt / 2.0));
					inv.setItem(i, new ItemStack(mat));
					inv.getItem(i).setAmount(amt);
					if (dat > 0) {
						inv.getItem(i).setData(data);
					}
					//itm.setAmount(amt);
					//inv.setItem(i, itm);
				}
			}
		});
	}
	
	public void getReward(String str) {
		String[] ret = null;
		ret = str.split(",");
		if (ret.length == 2) {
			try{
				ArenaReward ar = new ArenaReward();
				int type = getRewardType(ret[0]);
				int data = getRewardData(ret[0]);
				int amt = Integer.parseInt(ret[1]);
				if (type > -1 && data > -1 && amt > 0) {
					ar.amt = amt;
					ar.type = type;
					ar.data = (byte)data;
					rewards.add(ar);
				}
			}catch(Exception e) {
				//FUCK YOU
			}
		}else{
			System.out.println("[UltimateArena] Failed to load reward to arena: " + arenaName);
		}
	}
	
	public int getRewardType(String str) {
		int ret = -1;
		try{ ret = Integer.parseInt(str); }catch(Exception e) { }
		if (str.contains(":")) {
			str = str.substring(0, str.indexOf(":"));
			ret = Integer.parseInt(str);
		}
		return ret;
	}
	
	public int getRewardData(String str) {
		int ret = -1;
		try{ ret = Integer.parseInt(str); }catch(Exception e) { }
		if (str.contains(":")) {
			str = str.substring(str.indexOf(":") + 1);
			ret = Integer.parseInt(str);
		}
		return ret;
	}
	
	public void load() {
		ArrayList<String> file = new ArrayList<String>();
	    try{
			FileInputStream fstream = new FileInputStream(this.file.getAbsolutePath());
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			while ((strLine = br.readLine()) != null) {
				file.add(strLine);
			}
			br.close();
			in.close();
			fstream.close();
        }catch (Exception e){
            System.err.println("Error: " + e.getMessage());
        }
	    
	    for (int i= 0; i < file.size(); i++) {
	    	computeData(file.get(i));
	    }
	}
}
