package com.orange451.UltimateArena;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.orange451.UltimateArena.Arenas.Arena;
import com.orange451.UltimateArena.Arenas.Objects.ArenaPlayer;
import com.orange451.UltimateArena.Arenas.Objects.SavedArenaPlayer;
import com.orange451.UltimateArena.util.Util;

/**
 * @author dmulloy2
 */

public class FileHelper 
{
	public UltimateArena plugin;
	public FileHelper(UltimateArena plugin)
	{
		this.plugin = plugin;
	}
	
	/**Generate Whitelisted Commands File**/
	public void generateWhitelistedCmds()
	{
		String path = plugin.getRoot().getAbsolutePath() + "/whiteListedCommands.txt";
		File file = new File(path);
		if (!file.exists())
		{
			try
			{
				file.createNewFile();
			}
			catch (Exception e)
			{
				plugin.getLogger().severe("Error saving whitelisted cmds file: " + e.getMessage());
			}
			
			List<String> words = new ArrayList<String>();
			words.add("/f c");
			words.add("/msg");
			words.add("/r");
			words.add("/who");
			words.add("/gms");
			words.add("/god");
			words.add("/list");
			words.add("/t");
			words.add("/msg");
			words.add("/tell");
			
			PrintStream ps = null;
			try { ps = new PrintStream(file); }
			catch (FileNotFoundException e) {}
			
			for (String s : words)
			{
				ps.println(s);
			}
			
			ps.close();
		}
	}
	
	/**Generate Arena Configurations**/
	public void generateArenaConfig(String type)
	{
		String path = plugin.getRoot().getAbsolutePath() + "/" + type + "CONFIG.txt";
		File file = new File(path);
		if (!file.exists())
		{
			try
			{
				file.createNewFile();
			}
			catch (Exception e)
			{
				plugin.getLogger().severe("Error generating config \"" + type + "\": " + e.getMessage());
			}
			
			List<String> words = null;
			PrintStream ps = null;
			if (type.equals("bomb"))
			{
				words = new ArrayList<String>();
				words.add("gametime=900");
				words.add("lobbytime=70");
				words.add("maxdeaths=990");
				words.add("allowteamkilling=false");
				words.add("cashreward=100");
				words.add("//REWARDS");
				words.add("266,1");
				words.add("46,4");
			}
			
			if (type.equals("cq"))
			{
				words = new ArrayList<String>();
				words.add("gametime=1200");
				words.add("lobbytime=180");
				words.add("maxdeaths=900");
				words.add("allowteamkilling=false");
				words.add("cashreward=100");
				words.add("//REWARDS");
				words.add("266,3");
			}
			
			if (type.equals("ctf"))
			{
				words = new ArrayList<String>();
				words.add("gametime=440");
				words.add("lobbytime=90");
				words.add("maxdeaths=999");
				words.add("allowteamkilling=false");
				words.add("cashreward=100");
				words.add("//REWARDS");
				words.add("266,4");
			}
			
			if (type.equals("ffa"))
			{
				words = new ArrayList<String>();
				words.add("gametime=600");
				words.add("lobbytime=70");
				words.add("maxdeaths=3");
				words.add("allowteamkilling=true");
				words.add("cashreward=100");
				words.add("//REWARDS");
				words.add("266,9");
				words.add("46,3");
			}
			
			if (type.equals("hunger"))
			{
				words = new ArrayList<String>();
				words.add("gametime=9000");
				words.add("lobbytime=70");
				words.add("maxdeaths=1");
				words.add("allowteamkilling=true");
				words.add("cashreward=1000");
				words.add("//REWARDS");
				words.add("266,3");
				words.add("46,3");
			}
			
			if (type.equals("infect"))
			{
				words = new ArrayList<String>();
				words.add("gametime=180");
				words.add("lobbytime=90");
				words.add("maxdeaths=2");
				words.add("allowteamkilling=false");
				words.add("cashreward=100");
				words.add("//REWARDS");
				words.add("266,6");
				words.add("46,2");
			}
			
			if (type.equals("koth"))
			{
				words = new ArrayList<String>();
				words.add("gametime=1200");
				words.add("lobbytime=80");
				words.add("maxdeaths=900");
				words.add("allowteamkilling=true");
				words.add("cashreward=100");
				words.add("//REWARDS");
				words.add("266,3");
				words.add("46,1");
			}
			
			if (type.equals("mob"))
			{
				words = new ArrayList<String>();
				words.add("gametime=1200");
				words.add("lobbytime=90");
				words.add("maxdeaths=0");
				words.add("maxwave=15");
				words.add("allowteamkilling=false");
				words.add("cashreward=100");
				words.add("//REWARDS");
				words.add("266,3");
				words.add("46,2");
			}
			
			if (type.equals("pvp"))
			{
				words = new ArrayList<String>();
				words.add("gametime=600");
				words.add("lobbytime=90");
				words.add("maxdeaths=3");
				words.add("allowteamkilling=false");
				words.add("cashreward=100");
				words.add("//REWARDS");
				words.add("266,3");
				words.add("46,2");
			}
			
			if (type.equals("spleef"))
			{
				words = new ArrayList<String>();
				words.add("gametime=600");
				words.add("lobbytime=80");
				words.add("maxdeaths=2");
				words.add("allowteamkilling=true");
				words.add("cashreward=100");
				words.add("//REWARDS");
				words.add("266,3");
				words.add("46,2");
			}
			
			try { ps = new PrintStream(file); }
			catch (FileNotFoundException e) {}
			
			for (String s : words)
			{
				ps.println(s);
			}
			
			ps.close();
		}
	}

	/**Generate Stock Classes**/
	public void generateStockClasses() 
	{
		String path = plugin.getRoot().getAbsolutePath() + "/classes";
		File dir = new File(path);
		if (!dir.exists()) dir.mkdir();
		
		String archerPath = path + "/archer";
		File archerFile = new File(archerPath);
		if (!archerFile.exists())
			try
		{ 
				archerFile.createNewFile(); 
		}
		catch (Exception e)
		{
			plugin.getLogger().severe("Error saving archer file: " + e.getMessage()); 
		}
		generateClass(archerFile, "archer");
		
		String brutePath = path + "/brute";
		File bruteFile = new File(brutePath);
		if (!bruteFile.exists())
			try
		{ 
				bruteFile.createNewFile(); 
		}
		catch (Exception e)
		{
			plugin.getLogger().severe("Error saving brute file: " + e.getMessage()); 
		}
		generateClass(bruteFile, "brute");
		
		String dumbassPath = path + "/dumbass";
		File dumbassFile = new File(dumbassPath);
		if (!dumbassFile.exists())
			try
		{ 
				dumbassFile.createNewFile(); 
		}
		catch (Exception e)
		{
			plugin.getLogger().severe("Error saving dumbass file: " + e.getMessage()); 
		}
		generateClass(dumbassFile, "dumbass");
		
		String gunnerPath = path + "/gunner";
		File gunnerFile = new File(gunnerPath);
		if (!gunnerFile.exists())
			try
		{ 
				gunnerFile.createNewFile(); 
		}
		catch (Exception e)
		{
			plugin.getLogger().severe("Error saving gunner file: " + e.getMessage()); 
		}
		generateClass(gunnerFile, "gunner");
		
		String healerPath = path + "/healer";
		File healerFile = new File(healerPath);
		if (!healerFile.exists())
			try
		{ 
				healerFile.createNewFile(); 
		}
		catch (Exception e)
		{
			plugin.getLogger().severe("Error saving healer file: " + e.getMessage()); 
		}
		generateClass(healerFile, "healer");
		
		String shotgunPath = path + "/shotgun";
		File shotgunFile = new File(shotgunPath);
		if (!shotgunFile.exists())
			try
		{ 
				shotgunFile.createNewFile(); 
		}
		catch (Exception e)
		{
			plugin.getLogger().severe("Error saving shotgun file: " + e.getMessage()); 
		}
		generateClass(shotgunFile, "shotgun");
		
		String sniperPath = path + "/sniper";
		File sniperFile = new File(sniperPath);
		if (!sniperFile.exists())
			try
		{ 
				sniperFile.createNewFile(); 
		}
		catch (Exception e)
		{
			plugin.getLogger().severe("Error saving sniper file: " + e.getMessage()); 
		}
		generateClass(sniperFile, "sniper");
		
		String spleefPath = path + "/spleef";
		File spleefFile = new File(spleefPath);
		if (!spleefFile.exists())
			try
		{ 
				spleefFile.createNewFile(); 
		}
		catch (Exception e)
		{
			plugin.getLogger().severe("Error saving spleef file: " + e.getMessage()); 
		}
		generateClass(spleefFile, "spleef");
	}
	
	public void generateClass(File file, String type)
	{
		List<String> words = null;
		PrintStream ps = null;
		if (type.equals("archer"))
		{
			words = new ArrayList<String>();
			words.add("--Armor");
			words.add("chestplate=307");
			words.add("leggings=308");
			words.add("boots=309");
			words.add("--Tools");
			words.add("tool1=261");
			words.add("tool2=262,1024");
			words.add("tool3=267");
		}
		
		if (type.equals("brute"))
		{
			words = new ArrayList<String>();
			words.add("--Armor");
			words.add("chestplate=307");
			words.add("leggings=308");
			words.add("boots=309");
			words.add("--Tools");
			words.add("tool1=276");
			words.add("tool2=333:1,2");
			words.add("tool3=341,24");
		}
		
		if (type.equals("dumbass"))
		{
			words = new ArrayList<String>();
			words.add("--Armor");
			words.add("chestplate=307");
			words.add("leggings=308");
			words.add("boots=309");
			words.add("--Tools");
			words.add("tool1=283");
			words.add("tool2=259");
		}
		
		if (type.equals("gunner"))
		{
			words = new ArrayList<String>();
			words.add("--Armor");
			words.add("chestplate=307");
			words.add("leggings=308");
			words.add("boots=309");
			words.add("--Tools");
			words.add("tool1=292");
			words.add("tool2=318,7070");
			words.add("tool3=341,24");
			words.add("tool4=322,2");
			words.add("tool5=267,1,sharp:1");
		}
		
		if (type.equals("healer"))
		{
			words = new ArrayList<String>();
			words.add("--Armor");
			words.add("chestplate=307");
			words.add("leggings=308");
			words.add("boots=309");
			words.add("--Tools");
			words.add("tool1=267");
			words.add("tool2=373:8261");
			words.add("tool3=373:16453");
		}
		
		if (type.equals("shotgun"))
		{
			words = new ArrayList<String>();
			words.add("--Armor");
			words.add("chestplate=307");
			words.add("leggings=308");
			words.add("boots=309");
			words.add("--Tools");
			words.add("tool1=291");
			words.add("tool2=295,1024");
			words.add("tool3=341,24");
			words.add("322,2");
			words.add("tool5=267");
		}
		
		if (type.equals("sniper"))
		{
			words = new ArrayList<String>();
			words.add("--Armor");
			words.add("chestplate=307");
			words.add("leggings=308");
			words.add("boots=309");
			words.add("--Tools");
			words.add("tool1=294");
			words.add("tool2=337,1024");
			words.add("tool3=341,24");
			words.add("tool4=322,2");
			words.add("tool5=267");
		}
		
		if (type.equals("spleef"))
		{
			words = new ArrayList<String>();
			words.add("--Armor");
			words.add("chestplate=307");
			words.add("leggings=308");
			words.add("boots=309");
			words.add("--Tools");
			words.add("tool1=277");
		}
		
		try { ps = new PrintStream(file); }
		catch (FileNotFoundException e) {}
		
		for (String s : words)
		{
			ps.println(s);
		}
		
		ps.close();
	}

	/**Save players on disable**/
	public void savePlayers(List<Arena> activeArena) 
	{
		PrintStream ps = null;
		String path = plugin.getRoot().getAbsolutePath() + "/players.txt";
		File file = new File(path);
		if (!file.exists())
			try 
		{
			file.createNewFile();
		} 
		catch (IOException e)
		{
			plugin.getLogger().severe("Error saving players file: " + e.getMessage());
			return;
		}
		
		if (activeArena.size() == 0)
		{
			return;
		}
		
		for (int i=0; i<activeArena.size(); i++)
		{
			Arena a = activeArena.get(i);
			for (int ii=0; ii<a.arenaplayers.size(); i++)
			{
				ArenaPlayer ap = a.arenaplayers.get(ii);
				if (ap != null && !ap.out)
				{
					Player player = Util.matchPlayer(ap.player.getName());
					if (player != null)
					{
						int exp = Integer.valueOf(Math.round(ap.startxp));
						Location loc = ap.spawnBack;
						
						try { ps = new PrintStream(file); }
						catch (Exception e) {}
						
						StringBuilder line = new StringBuilder();
						line.append(player.getName() + ",");
						
						line.append(exp + ",");
						
						int x = loc.getBlockX();
						int y = loc.getBlockY();
						int z = loc.getBlockZ();
						World world = loc.getWorld();
						
						line.append(x + "," + y + "," + z + "," + world.getName());
						
						ps.println(line.toString());
						
						ps.close();
					}
				}
			}
		}
	}
	
	/**Normalize Players on enable**/
	public List<SavedArenaPlayer> getSavedPlayers()
	{
		List<SavedArenaPlayer> players = new ArrayList<SavedArenaPlayer>();
		String path = plugin.getRoot().getAbsolutePath() + "/players.txt";
		File file = new File(path);
		FileInputStream fstream = null;
		DataInputStream in = null;
		BufferedReader br = null;
		if (file.exists())
		{
			try
			{
				for (int i=0; i<file.length(); i++)
				{
					fstream = new FileInputStream(path);
					in = new DataInputStream(fstream);
					br = new BufferedReader(new InputStreamReader(in));
					
					String str = br.readLine();
					String[] value = str.split(",");
					Player player = Util.matchPlayer(value[0]);
					int exp = Integer.parseInt(value[1]);
					int x = Integer.parseInt(value[2]);
					int y = Integer.parseInt(value[3]);
					int z = Integer.parseInt(value[4]);
					World world = plugin.getServer().getWorld(value[5]);
					Location loc = new Location(world, x, y, z);

					SavedArenaPlayer savedPlayer = new SavedArenaPlayer(player, exp, loc);
					players.add(savedPlayer);
					
					br.close();
				}
			}
			catch (Exception e)
			{
				plugin.getLogger().severe("Error loading saved players: " + e.getMessage());
			}
		}
		return players;
	}
	
	/**Removes a player from the saved file**/
	public void deletePlayer(Player player)
	{
		List<SavedArenaPlayer> savedPlayers = getSavedPlayers();
		for (SavedArenaPlayer savedPlayer : savedPlayers)
		{
			if (savedPlayer.getPlayer().getName().equals(player.getName()))
			{
				savedPlayers.remove(savedPlayer);
			}
		}
		
		PrintStream ps = null;
		String path = plugin.getRoot().getAbsolutePath() + "/players.txt";
		File file = new File(path);
		if (!file.exists()) file.delete();
		
		try 
		{
			file.createNewFile();
		} 
		catch (IOException e)
		{
			plugin.getLogger().severe("Error saving players file: " + e.getMessage());
			return;
		}
		
		for (SavedArenaPlayer savedPlayer1 : savedPlayers)
		{
			int exp = savedPlayer1.getExp();
			Location loc = savedPlayer1.getLocation();
			
			StringBuilder line = new StringBuilder();
			line.append(player.getName() + ",");
						
			line.append(exp + ",");
			
			int x = loc.getBlockX();
			int y = loc.getBlockY();
			int z = loc.getBlockZ();
			World world = loc.getWorld();
						
			line.append(x + "," + y + "," + z + "," + world.getName());
						
			try { ps = new PrintStream(file); }
			catch (Exception e) {}
			
			ps.println(line.toString());
						
			ps.close();
		}
	}
}