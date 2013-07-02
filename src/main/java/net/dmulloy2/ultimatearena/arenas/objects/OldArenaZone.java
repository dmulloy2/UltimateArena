package net.dmulloy2.ultimatearena.arenas.objects;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import net.dmulloy2.ultimatearena.Field;
import net.dmulloy2.ultimatearena.UltimateArena;

@Deprecated
public class OldArenaZone
{
	public int amtLobbys = 2;
	public int amtSpawnpoints = 2;
	public int stepnum;
	public int specialType = 20;
	public int timesPlayed = 0;
	public int liked;
	public int disliked;
	public int maxPlayers = 24;
	public boolean loaded = false;
	public boolean disabled = false;
	public String defaultClass;
	public String player;
	public String arenaName = "";
	public String arenaType = "";
	public Location lobby1 = null;
	public Location lobby2 = null;
	public Location arena1 = null;
	public Location arena2 = null;
	public Location flag1point = null;
	public Location flag2point = null;
	public Location team1spawn = null;
	public Location team2spawn = null;
	public Location lobbyREDspawn = null;
	public Location lobbyBLUspawn = null;
	public Field lobby;
	public Field arena;
	public String step;
	public ArrayList<String> voted = new ArrayList<String>();
	public ArrayList<String> steps = new ArrayList<String>();
	public ArrayList<Location> spawns = new ArrayList<Location>();
	public ArrayList<Location> flags = new ArrayList<Location>();
	public World world;
	public UltimateArena plugin;
	
	public OldArenaZone(UltimateArena plugin, File f)
	{
		this.arenaName = f.getName();
		this.plugin = plugin;
		this.lobby = new Field();
		this.arena = new Field();
		
		try
		{
			this.defaultClass = plugin.classes.get(0).name;
		}
		catch(Exception e) 
		{	
			//
		}
		
		loadArena();

		if (loaded)
		{
			lobby.setParam(lobby1.getWorld(), lobby1.getX(), lobby1.getZ(), lobby2.getX(), lobby2.getZ());
			arena.setParam(arena1.getWorld(), arena1.getX(), arena1.getZ(), arena2.getX(), arena2.getZ());
		}
		
	}
	
	public OldArenaZone(UltimateArena plugin, String str) 
	{
		this.arenaName = str;
		this.plugin = plugin;
	}
	
	public boolean checkLocation(Location loc) 
	{
		return lobby.isInside(loc) || arena.isInside(loc);
	}
	
	public void save() 
	{
		String path = plugin.getDataFolder().getAbsolutePath() + "/arenas/" + arenaName;
        FileWriter outFile = null;
        PrintWriter out = null;
		try 
		{
			outFile = new FileWriter(path);
			out = new PrintWriter(outFile);
			out.println(arenaType);
			out.println(world.getName());
			out.println(lobby1.getBlockX() + "," + lobby1.getBlockZ());
			out.println(lobby2.getBlockX() + "," + lobby2.getBlockZ());
			out.println(arena1.getBlockX() + "," + arena1.getBlockZ());
			out.println(arena2.getBlockX() + "," + arena2.getBlockZ());
			if (arenaType.equals("pvp"))
			{
				out.println(lobbyREDspawn.getBlockX() + "," + lobbyREDspawn.getBlockY() + "," + lobbyREDspawn.getBlockZ());
				out.println(lobbyBLUspawn.getBlockX() + "," + lobbyBLUspawn.getBlockY() + "," + lobbyBLUspawn.getBlockZ());
				out.println(team1spawn.getBlockX() + "," + team1spawn.getBlockY() + "," + team1spawn.getBlockZ());
				out.println(team2spawn.getBlockX() + "," + team2spawn.getBlockY() + "," + team2spawn.getBlockZ());
			}
			if (arenaType.equals("mob"))
			{
				out.println(lobbyREDspawn.getBlockX() + "," + lobbyREDspawn.getBlockY() + "," + lobbyREDspawn.getBlockZ());
				out.println(team1spawn.getBlockX() + "," + team1spawn.getBlockY() + "," + team1spawn.getBlockZ());
				out.println(spawns.size());
				for (int i = 0; i < spawns.size(); i++) 
				{
					Location loc1 = spawns.get(i);
					out.println(loc1.getBlockX() + "," + loc1.getBlockY() + "," + loc1.getBlockZ());
				}
			}
			if (arenaType.equals("cq")) 
			{
				out.println(lobbyREDspawn.getBlockX() + "," + lobbyREDspawn.getBlockY() + "," + lobbyREDspawn.getBlockZ());
				out.println(lobbyBLUspawn.getBlockX() + "," + lobbyBLUspawn.getBlockY() + "," + lobbyBLUspawn.getBlockZ());
				out.println(team1spawn.getBlockX() + "," + team1spawn.getBlockY() + "," + team1spawn.getBlockZ());
				out.println(team2spawn.getBlockX() + "," + team2spawn.getBlockY() + "," + team2spawn.getBlockZ());
				out.println(flags.size());
				for (int i = 0; i < flags.size(); i++) 
				{
					Location loc1 = flags.get(i);
					out.println(loc1.getBlockX() + "," + loc1.getBlockY() + "," + loc1.getBlockZ());
				}
			}
			if (arenaType.equals("koth"))
			{
				out.println(lobbyREDspawn.getBlockX() + "," + lobbyREDspawn.getBlockY() + "," + lobbyREDspawn.getBlockZ());
				out.println(spawns.size());
				for (int i = 0; i < spawns.size(); i++) 
				{
					Location loc1 = spawns.get(i);
					out.println(loc1.getBlockX() + "," + loc1.getBlockY() + "," + loc1.getBlockZ());
				}
				out.println(flags.get(0).getBlockX() + "," + flags.get(0).getBlockY() + "," + flags.get(0).getBlockZ());
			}
			if (arenaType.equals("ffa") || arenaType.equals("hunger"))
			{
				out.println(lobbyREDspawn.getBlockX() + "," + lobbyREDspawn.getBlockY() + "," + lobbyREDspawn.getBlockZ());
				out.println(spawns.size());
				for (int i = 0; i < spawns.size(); i++) 
				{
					Location loc1 = spawns.get(i);
					out.println(loc1.getBlockX() + "," + loc1.getBlockY() + "," + loc1.getBlockZ());
				}
			}
			if (arenaType.equals("spleef"))
			{
				out.println(lobbyREDspawn.getBlockX() + "," + lobbyREDspawn.getBlockY() + "," + lobbyREDspawn.getBlockZ());
				out.println(80);
				for (int i = 0; i < 4; i++) 
				{
					out.println(flags.get(i).getBlockX() + "," + flags.get(i).getBlockY() + "," + flags.get(i).getBlockZ());
				}
			}
			if (arenaType.equals("bomb"))
			{
				out.println(lobbyREDspawn.getBlockX() + "," + lobbyREDspawn.getBlockY() + "," + lobbyREDspawn.getBlockZ());
				out.println(lobbyBLUspawn.getBlockX() + "," + lobbyBLUspawn.getBlockY() + "," + lobbyBLUspawn.getBlockZ());
				out.println(team1spawn.getBlockX() + "," + team1spawn.getBlockY() + "," + team1spawn.getBlockZ());
				out.println(team2spawn.getBlockX() + "," + team2spawn.getBlockY() + "," + team2spawn.getBlockZ());
				out.println(flags.get(0).getBlockX() + "," + flags.get(0).getBlockY() + "," + flags.get(0).getBlockZ());
				out.println(flags.get(1).getBlockX() + "," + flags.get(1).getBlockY() + "," + flags.get(1).getBlockZ());
			}
			if (arenaType.equals("ctf")) 
			{
				out.println(lobbyREDspawn.getBlockX() + "," + lobbyREDspawn.getBlockY() + "," + lobbyREDspawn.getBlockZ());
				out.println(lobbyBLUspawn.getBlockX() + "," + lobbyBLUspawn.getBlockY() + "," + lobbyBLUspawn.getBlockZ());
				out.println(team1spawn.getBlockX() + "," + team1spawn.getBlockY() + "," + team1spawn.getBlockZ());
				out.println(team2spawn.getBlockX() + "," + team2spawn.getBlockY() + "," + team2spawn.getBlockZ());
				out.println(flags.get(0).getBlockX() + "," + flags.get(0).getBlockY() + "," + flags.get(0).getBlockZ());
				out.println(flags.get(1).getBlockX() + "," + flags.get(1).getBlockY() + "," + flags.get(1).getBlockZ());
			}
			if (arenaType.equals("infect"))
			{
				out.println(lobbyREDspawn.getBlockX() + "," + lobbyREDspawn.getBlockY() + "," + lobbyREDspawn.getBlockZ());
				out.println(lobbyBLUspawn.getBlockX() + "," + lobbyBLUspawn.getBlockY() + "," + lobbyBLUspawn.getBlockZ());
				out.println(team1spawn.getBlockX() + "," + team1spawn.getBlockY() + "," + team1spawn.getBlockZ());
				out.println(team2spawn.getBlockX() + "," + team2spawn.getBlockY() + "," + team2spawn.getBlockZ());
			}
			out.println("--config--");
			out.println("maxPlayers=24");
			out.println("defaultClass=" + plugin.classes.get(0).name);
		}
		catch (IOException e1)
		{
			//
		}
		try
		{
	        out.close();
	        outFile.close();
		}
		catch(Exception e) 
		{
			//
		}
		
		this.lobby = new Field();
		this.arena = new Field();
		
		lobby.setParam(lobby1.getWorld(), lobby1.getX(), lobby1.getZ(), lobby2.getX(), lobby2.getZ());
		arena.setParam(arena1.getWorld(), arena1.getX(), arena1.getZ(), arena2.getX(), arena2.getZ());
	}
	
	public Location getLocationFromString(String str) 
	{
		String[] arr = str.split(",");
		if (arr.length == 2) 
		{
			return new Location(world, Integer.parseInt(arr[0]), 0, Integer.parseInt(arr[1]));
		}
		else if (arr.length == 3) 
		{
			return new Location(world, Integer.parseInt(arr[0]), Integer.parseInt(arr[1]), Integer.parseInt(arr[2]));
		}
		return null;
	}
	
	public World getWorldFromString(String str)
	{
		return str == null ? null : plugin.getServer().getWorld(str);
	}
	
	public void loadArena() 
	{
		String path = plugin.getDataFolder().getAbsolutePath() + "/arenas/" + arenaName;
		FileInputStream fstream = null;
		DataInputStream in = null;
		BufferedReader br = null;
		try
		{
			fstream = new FileInputStream(path);
			in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			strLine = br.readLine();
			this.arenaType = strLine.toLowerCase();
			strLine = br.readLine();
			this.world = getWorldFromString(strLine);
			if (this.world == null)
			{
				this.world = plugin.getServer().getWorlds().get(0);
				try {br.close();} catch (Exception ex) {}
				br = new BufferedReader(new InputStreamReader(in));
				br.readLine();
			}
			if (arenaType.equalsIgnoreCase("pvp"))
			{
				readPVP(br);
			}
			else if (arenaType.equalsIgnoreCase("mob"))
			{
				readMOB(br);
			}
			else if (arenaType.equalsIgnoreCase("cq")) 
			{
				readCONQUEST(br);
			}
			else if (arenaType.equalsIgnoreCase("koth"))
			{
				readKOTH(br);
			}
			else if (arenaType.equalsIgnoreCase("bomb"))
			{
				readBOMB(br);
			}
			else if (arenaType.equalsIgnoreCase("ffa")||arenaType.equalsIgnoreCase("hunger")) 
			{
				readFFA(br);
			}
			else if (arenaType.equalsIgnoreCase("spleef"))
			{
				readSPLEEF(br);
			}
			else if (arenaType.equalsIgnoreCase("infect")) 
			{
				readINFECT(br);
			}
			else if (arenaType.equalsIgnoreCase("ctf"))
			{
				readCTF(br);
			}
		} 
		catch (Exception e) 
		{
			plugin.getLogger().severe("Error loading arena " + arenaType + ": " + e.getMessage());
		}
		loadConfig(br);
		try { br.close(); } catch(Exception e) { }
		try { in.close(); } catch(Exception e) { }
		try { fstream.close(); } catch(Exception e) { }
	}
	
	private void loadConfig(BufferedReader br) 
	{
		ArrayList<String> file = new ArrayList<String>();
		String strLine;
		try
		{
			String str;
			while ((str = br.readLine()) != null) 
			{
				if (str.equals("--config--"))
				{
					while ((strLine = br.readLine()) != null) 
					{
						file.add(strLine);
					}
					for (int i = 0; i < file.size(); i++) 
					{
				    	computeConfigData(file.get(i));
				    }
				}
			}
		}
		catch (IOException e) 
		{
			//
		}
	}
	
	private void computeConfigData(String str)
	{
		if (str.indexOf("=") > 0 )
		{
			String str2 = str.substring(0, str.indexOf("="));
			if (str2.equalsIgnoreCase("maxPlayers"))
			{
				int value = Integer.parseInt(str.substring(str.indexOf("=")+1));
				maxPlayers = value;
			}
			if (str2.equalsIgnoreCase("defaultClass")) 
			{
				String value = str.substring(str.indexOf("=")+1);
				defaultClass = value;
			}
		}
	}

	public void readPVP(BufferedReader br)
	{
		try 
		{
			this.lobby1 = getLocationFromString(br.readLine());
			this.lobby2 = getLocationFromString(br.readLine());
			this.arena1 = getLocationFromString(br.readLine());
			this.arena2 = getLocationFromString(br.readLine());
			this.lobbyREDspawn = getLocationFromString(br.readLine());
			this.lobbyBLUspawn = getLocationFromString(br.readLine());
			this.team1spawn = getLocationFromString(br.readLine());
			this.team2spawn = getLocationFromString(br.readLine());
			loaded = true;
		} 
		catch (IOException e) 
		{
			//
		}
	}
	
	public void readINFECT(BufferedReader br) 
	{
		try 
		{
			this.lobby1 = getLocationFromString(br.readLine());
			this.lobby2 = getLocationFromString(br.readLine());
			this.arena1 = getLocationFromString(br.readLine());
			this.arena2 = getLocationFromString(br.readLine());
			this.lobbyREDspawn = getLocationFromString(br.readLine());
			this.lobbyBLUspawn = getLocationFromString(br.readLine());
			this.team1spawn = getLocationFromString(br.readLine());
			this.team2spawn = getLocationFromString(br.readLine());
			loaded = true;
		}
		catch (IOException e) 
		{
			//
		}
	}
	
	public void readCONQUEST(BufferedReader br) 
	{
		try 
		{
			this.lobby1 = getLocationFromString(br.readLine());
			this.lobby2 = getLocationFromString(br.readLine());
			this.arena1 = getLocationFromString(br.readLine());
			this.arena2 = getLocationFromString(br.readLine());
			this.lobbyREDspawn = getLocationFromString(br.readLine());
			this.lobbyBLUspawn = getLocationFromString(br.readLine());
			this.team1spawn = getLocationFromString(br.readLine());
			this.team2spawn = getLocationFromString(br.readLine());
			int amtSpawns = Integer.parseInt(br.readLine());
			for (int i = 0; i < amtSpawns; i++)
			{
				this.flags.add(getLocationFromString(br.readLine()));
			}
			loaded = true;
		} 
		catch (IOException e)
		{
			//
		}
	}

	public void readKOTH(BufferedReader br) 
	{
		try
		{
			this.lobby1 = getLocationFromString(br.readLine());
			this.lobby2 = getLocationFromString(br.readLine());
			this.arena1 = getLocationFromString(br.readLine());
			this.arena2 = getLocationFromString(br.readLine());
			this.lobbyREDspawn = getLocationFromString(br.readLine());
			int amtSpawns = Integer.parseInt(br.readLine());
			for (int i = 0; i < amtSpawns; i++)
			{
				this.spawns.add(getLocationFromString(br.readLine()));
			}
			this.flags.add(getLocationFromString(br.readLine()));
			loaded = true;
		} 
		catch (IOException e) 
		{
			//
		}
	}
	
	public void readFFA(BufferedReader br)
	{
		try
		{
			this.lobby1 = getLocationFromString(br.readLine());
			this.lobby2 = getLocationFromString(br.readLine());
			this.arena1 = getLocationFromString(br.readLine());
			this.arena2 = getLocationFromString(br.readLine());
			this.lobbyREDspawn = getLocationFromString(br.readLine());
			int amtSpawns = Integer.parseInt(br.readLine());
			for (int i = 0; i < amtSpawns; i++)
			{
				this.spawns.add(getLocationFromString(br.readLine()));
			}
			loaded = true;
		} 
		catch (IOException e) 
		{
			//
		}
	}
	
	public void readSPLEEF(BufferedReader br)
	{
		try
		{
			this.lobby1 = getLocationFromString(br.readLine());
			this.lobby2 = getLocationFromString(br.readLine());
			this.arena1 = getLocationFromString(br.readLine());
			this.arena2 = getLocationFromString(br.readLine());
			this.lobbyREDspawn = getLocationFromString(br.readLine());
			this.specialType = Integer.parseInt(br.readLine());
			for (int i = 0; i < 4; i++) 
			{
				this.flags.add(getLocationFromString(br.readLine()));
			}
			loaded = true;
		}
		catch (IOException e) 
		{
			//
		}
	}
	
	public void readBOMB(BufferedReader br)
	{
		try
		{
			this.lobby1 = getLocationFromString(br.readLine());
			this.lobby2 = getLocationFromString(br.readLine());
			this.arena1 = getLocationFromString(br.readLine());
			this.arena2 = getLocationFromString(br.readLine());
			this.lobbyREDspawn = getLocationFromString(br.readLine());
			this.lobbyBLUspawn = getLocationFromString(br.readLine());
			this.team1spawn = getLocationFromString(br.readLine());
			this.team2spawn = getLocationFromString(br.readLine());
			
			this.flags.add(getLocationFromString(br.readLine()));
			this.flags.add(getLocationFromString(br.readLine()));

			loaded = true;
		}
		catch (IOException e) 
		{
			//
		}
	}
	
	public void readCTF(BufferedReader br)
	{
		try 
		{
			this.lobby1 = getLocationFromString(br.readLine());
			this.lobby2 = getLocationFromString(br.readLine());
			this.arena1 = getLocationFromString(br.readLine());
			this.arena2 = getLocationFromString(br.readLine());
			this.lobbyREDspawn = getLocationFromString(br.readLine());
			this.lobbyBLUspawn = getLocationFromString(br.readLine());
			this.team1spawn = getLocationFromString(br.readLine());
			this.team2spawn = getLocationFromString(br.readLine());
			
			this.flags.add(getLocationFromString(br.readLine()));
			this.flags.add(getLocationFromString(br.readLine()));

			loaded = true;
		}
		catch (IOException e)
		{
			//
		}
	}
	
	public void readMOB(BufferedReader br)
	{
		try
		{
			this.lobby1 = getLocationFromString(br.readLine());
			this.lobby2 = getLocationFromString(br.readLine());
			this.arena1 = getLocationFromString(br.readLine());
			this.arena2 = getLocationFromString(br.readLine());
			this.lobbyREDspawn = getLocationFromString(br.readLine());
			this.team1spawn = getLocationFromString(br.readLine());
			int amtSpawns = Integer.parseInt(br.readLine());
			for (int i = 0; i < amtSpawns; i++) 
			{
				this.spawns.add(getLocationFromString(br.readLine()));
			}

			loaded = true;
		}
		catch (IOException e) 
		{
			//
		}
	}

	public boolean canLike(Player player)
	{
		for (int i = 0; i < voted.size(); i++)
		{
			if (voted.get(i).equals(player.getName())) 
			{
				return false;
			}
		}
		return true;
	}
}