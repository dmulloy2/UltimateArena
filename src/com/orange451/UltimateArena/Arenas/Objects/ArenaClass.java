package com.orange451.UltimateArena.Arenas.Objects;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.bukkit.entity.Player;

import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.PermissionInterface.PermissionInterface;

public class ArenaClass {
	public String name;
	public String permissionNode = "";
	public int armor1 = 0;
	public int armor2 = 0;
	public int armor3 = 0;
	public int weapon1 = 0;
	public int weapon2 = 0;
	public int weapon3 = 0;
	public int weapon4 = 0;
	public int weapon5 = 0;
	public int weapon6 = 0;
	public int weapon7 = 0;
	public int weapon8 = 0;
	public int weapon9 = 0;
	public int amt1 = 1;
	public int amt2 = 1;
	public int amt3 = 1;
	public int amt4 = 1;
	public int amt5 = 1;
	public int amt6 = 1;
	public int amt7 = 1;
	public int amt8 = 1;
	public int amt9 = 1;
	public byte special1 = 0;
	public byte special2 = 0;
	public byte special3 = 0;
	public byte special4 = 0;
	public byte special5 = 0;
	public byte special6 = 0;
	public byte special7 = 0;
	public byte special8 = 0;
	public byte special9 = 0;
	public boolean loaded = true;
	public UltimateArena plugin;
	public File file;
	
	public ArenaClass(UltimateArena plugin, File file) {
		this.plugin = plugin;
		this.file = file;
		this.load();
	}
	
	public void load() {
		name = file.getName();
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
	    if (loaded) {
	    	System.out.println("[ULTIMATEARENA] CLASS LOADED: " + name);
	    }else{
	    	System.out.println("[ULTIMATEARENA] ERROR LOADING CLASS: " + name);
	    }
	}
	
	public int readWep(String str) {
		int ret = 0;
		if (str.contains(",")) {
			str = str.substring(0, str.indexOf(","));
		}
		try{ ret = Integer.parseInt(str); }catch(Exception e) { }
		if (str.contains(":")) {
			str = str.substring(0, str.indexOf(":"));
			ret = Integer.parseInt(str);
		}
		return ret;
	}
	
	public int readSpec(String str) {
		int ret = 0;
		if (str.contains(",")) {
			str = str.substring(0, str.indexOf(","));
		}
		try{ ret = Integer.parseInt(str); }catch(Exception e) { }
		if (str.contains(":")) {
			str = str.substring(str.indexOf(":") + 1);
			ret = Integer.parseInt(str);
		}
		return ret;
	}
	
	public int readAmt(String str) {
		int ret = 1;
		if (str.contains(",")) {
			str = str.substring(str.indexOf(",") + 1);
			try{ ret = Integer.parseInt(str); }catch(Exception e) { }
		}
		return ret;
	}
	
	public void computeData(String str) {
		try{
			if (str.indexOf("=") >=1 ) {
				String str2 = str.substring(0, str.indexOf("="));
				if (str2.equalsIgnoreCase("chestplate")) {
					int value = Integer.parseInt(str.substring(str.indexOf("=")+1));
					armor1 = value;
				}
				if (str2.equalsIgnoreCase("leggings")) {
					int value = Integer.parseInt(str.substring(str.indexOf("=")+1));
					armor2 = value;
				}
				if (str2.equalsIgnoreCase("boots")) {
					int value = Integer.parseInt(str.substring(str.indexOf("=")+1));
					armor3 = value;
				}
				if (str2.equalsIgnoreCase("node")) {
					String line = str.substring(str.indexOf("=")+1);
					permissionNode = line;
				}
				if (str2.equalsIgnoreCase("tool1")) {
					String line = str.substring(str.indexOf("=")+1);
					int value = readWep(line);
					int value2 = readSpec(line);
					int value3 = readAmt(line);
					weapon1 = value;
					special1 = (byte) value2;
					amt1 = value3;
				}
				if (str2.equalsIgnoreCase("tool2")) {
					String line = str.substring(str.indexOf("=")+1);
					int value = readWep(line);
					int value2 = readSpec(line);
					int value3 = readAmt(line);
					weapon2 = value;
					special2 = (byte) value2;
					amt2 = value3;
				}
				if (str2.equalsIgnoreCase("tool3")) {
					String line = str.substring(str.indexOf("=")+1);
					int value = readWep(line);
					int value2 = readSpec(line);
					int value3 = readAmt(line);
					weapon3 = value;
					special3 = (byte) value2;
					amt3 = value3;
				}
				if (str2.equalsIgnoreCase("tool4")) {
					String line = str.substring(str.indexOf("=")+1);
					int value = readWep(line);
					int value2 = readSpec(line);
					int value3 = readAmt(line);
					weapon4 = value;
					special4 = (byte) value2;
					amt4 = value3;
				}
				if (str2.equalsIgnoreCase("tool5")) {
					String line = str.substring(str.indexOf("=")+1);
					int value = readWep(line);
					int value2 = readSpec(line);
					int value3 = readAmt(line);
					weapon5 = value;
					special5 = (byte) value2;
					amt5 = value3;
				}
				if (str2.equalsIgnoreCase("tool6")) {
					String line = str.substring(str.indexOf("=")+1);
					int value = readWep(line);
					int value2 = readSpec(line);
					int value3 = readAmt(line);
					weapon6 = value;
					special6 = (byte) value2;
					amt6 = value3;
				}
				if (str2.equalsIgnoreCase("tool7")) {
					String line = str.substring(str.indexOf("=")+1);
					int value = readWep(line);
					int value2 = readSpec(line);
					int value3 = readAmt(line);
					weapon7 = value;
					special7 = (byte) value2;
					amt7 = value3;
				}
				if (str2.equalsIgnoreCase("tool8")) {
					String line = str.substring(str.indexOf("=")+1);
					int value = readWep(line);
					int value2 = readSpec(line);
					int value3 = readAmt(line);
					weapon8 = value;
					special8 = (byte) value2;
					amt8 = value3;
				}
				if (str2.equalsIgnoreCase("tool9")) {
					String line = str.substring(str.indexOf("=")+1);
					int value = readWep(line);
					int value2 = readSpec(line);
					int value3 = readAmt(line);
					weapon9 = value;
					special9 = (byte) value2;
					amt9 = value3;
				}
			}
		}catch(Exception e) {
			//problem loading the class
			loaded = false;
		}
	}

	public boolean checkPermission(Player player) {
		if (permissionNode.equals("")) {
			return true;
		}else{
			return PermissionInterface.checkPermission(player, permissionNode);
		}
	}	
}
