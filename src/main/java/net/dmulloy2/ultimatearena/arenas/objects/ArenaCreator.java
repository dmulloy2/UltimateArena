package net.dmulloy2.ultimatearena.arenas.objects;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.util.Util;

public class ArenaCreator 
{
	private String step;
	private String player;
	private String msg = "";
	private String arenaName = "";
	private String arenaType = "";
	
	private Location lobby1 = null;
	private Location lobby2 = null;
	private Location arena1 = null;
	private Location arena2 = null;
//	private Location flag1point = null;
//	private Location flag2point = null;
	private Location team1spawn = null;
	private Location team2spawn = null;
	private Location lobbyREDspawn = null;
	private Location lobbyBLUspawn = null;
	
	private int amtLobbys = 2;
	private int amtSpawnpoints = 2;

	private ArrayList<String> steps = new ArrayList<String>();
	private ArrayList<Location> spawns = new ArrayList<Location>();
	private ArrayList<Location> flags = new ArrayList<Location>();
	
	private int stepnum;
	
	private final UltimateArena plugin;
	
	public ArenaCreator(UltimateArena plugin, Player player)
	{
		this.player = player.getName();
		this.plugin = plugin;
	}
	
	public void setArena(String arenaName, String arenaType)
	{
		this.setArenaName(arenaName);
		this.arenaType = arenaType;
		
		this.steps.add("Lobby");
		this.steps.add("Arena");
		this.steps.add("LobbySpawn1");
		if (arenaType.equals("pvp") || arenaType.equals("infect")) 
		{
			this.steps.add("LobbySpawn2");
			this.steps.add("ArenaSpawn1");
			this.steps.add("ArenaSpawn2");
		}
		if (arenaType.equals("ctf"))
		{
			this.steps.add("LobbySpawn2");
			this.steps.add("ArenaSpawn1");
			this.steps.add("ArenaSpawn2");
			this.steps.add("FlagSpawn");
		}
		if (arenaType.equals("cq"))
		{
			this.steps.add("LobbySpawn2");
			this.steps.add("ArenaSpawn1");
			this.steps.add("ArenaSpawn2");
			this.steps.add("FlagSpawn");
		}
		if (arenaType.equals("koth"))
		{
			amtLobbys = 1;
			this.steps.add("playerspawn");
			this.steps.add("kothflag");
		}
		if (arenaType.equals("ffa") || arenaType.equals("hunger")) 
		{
			amtLobbys = 1;
			this.steps.add("playerspawn");
		}
		if (arenaType.equals("spleef")) 
		{
			amtLobbys = 1;
			this.steps.add("spleefzone");
			this.steps.add("outzone");
		}
		if (arenaType.equals("bomb"))
		{
			this.steps.add("LobbySpawn2");
			this.steps.add("ArenaSpawn1");
			this.steps.add("ArenaSpawn2");
			this.steps.add("FlagSpawn");
		}
		if (arenaType.equals("mob"))
		{
			amtLobbys = 1;
			amtSpawnpoints = 1;
			this.steps.add("ArenaSpawn1");
			this.steps.add("MobSpawn");
		}
		
		this.step = steps.get(stepnum);
		
		Player pl = Util.matchPlayer(player);
		pl.sendMessage(ChatColor.GRAY + "Arena: " + ChatColor.GOLD + arenaName + ChatColor.GRAY + " has been initialised. Type: " + ChatColor.GOLD + arenaType);
		pl.sendMessage(ChatColor.GRAY + "Please set two points for a lobby! " + ChatColor.GOLD + "/ua setpoint");
	}
	
	@SuppressWarnings("unchecked")
	public void saveArena(Player player)
	{
		try
		{
			ArenaZone az = new ArenaZone(plugin, getArenaName());
			az.setArenaType(arenaType);
			az.setLobbyBLUspawn(lobbyBLUspawn);
			az.setLobbyREDspawn(lobbyREDspawn);
			az.setTeam1spawn(team1spawn);
			az.setTeam2spawn(team2spawn);
			az.setLobby1(lobby1);
			az.setLobby2(lobby2);
			az.setArena1(arena1);
			az.setArena2(arena2);
			az.setSpawns((ArrayList<Location>) spawns.clone());
			az.setFlags((ArrayList<Location>) flags.clone());
			az.setMaxPlayers(24);
			az.setDefaultClass(plugin.classes.get(0).getName());
			az.setWorld(lobby1.getWorld());
			az.save();
			az.initialize();
			plugin.getLogger().info("Arena created and saved: " + getArenaName() + "  Type: " + arenaType);
			plugin.loadedArena.add(az);
			player.sendMessage(ChatColor.GRAY + "Finished arena!");
			player.sendMessage(ChatColor.GRAY + "Use " + ChatColor.GOLD + "/ua join " + getArenaName() + ChatColor.GRAY + " to play!");
		}
		catch(Exception e)
		{
			player.sendMessage(ChatColor.RED + "Error creating arena: " + e.getMessage());
			player.sendMessage(ChatColor.RED + "Check Console for more details");
			
			plugin.getLogger().severe("Error creating arena: ");
			e.printStackTrace();
		}
		plugin.makingArena.remove(this);
	}
	
	public void setDone(Player player)
	{
		if (step.equalsIgnoreCase("lobby")) 
		{
			if (lobby1 != null && lobby2 != null)
			{
				player.sendMessage(ChatColor.GRAY + "Done setting up Lobby! please set 2 points for the arena");
				stepUp();
				return;
			}
			else
			{
				player.sendMessage(ChatColor.GRAY + "You are not done creating the lobby!");
			}
		}
		else if (step.equalsIgnoreCase("arena")) 
		{
			if (arena1 != null && arena2 != null)
			{
				player.sendMessage(ChatColor.GRAY + "Done setting up Arena! please set a lobby spawn for RED team");
				stepUp();
				return;
			}
			else
			{
				player.sendMessage(ChatColor.GRAY + "You are not done creating the arena!");
			}
		}
		else if (step.equalsIgnoreCase("lobbyspawn1")) 
		{
			if (lobbyREDspawn != null) 
			{
				if (amtLobbys > 1) 
				{
					player.sendMessage(ChatColor.GRAY + "Done setting up lobby spawns!");
					player.sendMessage(ChatColor.GRAY + "Please create the RED team arena spawnpoint");
					stepUp();
				}
				else
				{
					if (arenaType.equals("koth") || arenaType.equals("ffa") || arenaType.equals("hunger"))
					{
						player.sendMessage(ChatColor.GRAY + "Please add some player spawnpoints  " + ChatColor.LIGHT_PURPLE + "/ua setpoint");
						player.sendMessage(ChatColor.GRAY + "use " + ChatColor.GOLD + "/ua done" + ChatColor.GRAY + " when done");
					}
					else
					{
						if (arenaType.equals("spleef"))
						{
							player.sendMessage(ChatColor.GRAY + "Done with lobby spawns");
							player.sendMessage(ChatColor.GRAY + "Please create 2 spleefzone points");
						}
						else
						{
							player.sendMessage(ChatColor.GRAY + "Done with lobby spawns, please create an arena spawnpoint(s)");
						}
					}
				}
				stepUp();
				return;
			}
			else
			{
				player.sendMessage(ChatColor.GRAY + "You are not done creating the lobby 1 spawn!");
			}
		}
		else if (step.equalsIgnoreCase("arenaspawn1")) 
		{
			if (team1spawn != null)
			{
				stepUp();
				if (steps.contains("ArenaSpawn2"))
				{
					if (team2spawn != null) 
					{
						player.sendMessage(ChatColor.GRAY + "Done with player spawns");
						stepUp();//get passed arenaspawn2 step, since it's created already :3 (fail coding, I know)
						if (arenaType.equals("cq"))
						{
							player.sendMessage(ChatColor.GRAY + "Please add some flag points");
							player.sendMessage(ChatColor.GRAY + "use " + ChatColor.GOLD + "/ua done" + ChatColor.GRAY + " when done");
						}
						if (arenaType.equals("bomb") || arenaType.equals("ctf"))
						{
							player.sendMessage(ChatColor.GRAY + "Please add 2 flag points");
							player.sendMessage(ChatColor.GRAY + "use " + ChatColor.GOLD + "/ua done" + ChatColor.GRAY + " when done");
						}
					}
					else
					{
						player.sendMessage(ChatColor.GRAY + "You are not done creating the BLUE team arena spawn point!");
						stepDown();
					}
				}
				else
				{
					player.sendMessage(ChatColor.GRAY + "Done with player spawns");
					if (arenaType.equals("mob"))
					{
						player.sendMessage(ChatColor.GRAY + "Please set some mob spawnpoints");
						player.sendMessage(ChatColor.GRAY + "use " + ChatColor.GOLD + "/ua done" + ChatColor.GRAY + " when done");
					}
				}
			}
			else
			{
				player.sendMessage(ChatColor.GRAY + "You are not done creating the RED team arena spawn point!");
			}
		}
		else
		{
			if (step.equalsIgnoreCase("playerspawn"))
			{
				if (spawns.size() > 0) 
				{
					stepUp();
					player.sendMessage(ChatColor.GRAY + "Done with player spawns");
					if (arenaType.equals("koth"))
					{
						player.sendMessage(ChatColor.GRAY + "please add a flag spawn");	
					}
				}
				else
				{
					player.sendMessage(ChatColor.GRAY + "You need more than 0 player spawns");
				}
			}
			if (step.equalsIgnoreCase("spleefzone"))
			{
				if (flags.size() == 2)
				{
					stepUp();
					player.sendMessage(ChatColor.GRAY + "Done with spleef zone!");
				}
				else
				{
					player.sendMessage(ChatColor.GRAY + "You need 2 points set for the spleef zone!");
				}
			}
			if (step.equalsIgnoreCase("outzone")) 
			{
				if (flags.size() == 4) 
				{
					stepUp();
					player.sendMessage(ChatColor.GRAY + "Done with out-zone!");
				}
				else
				{
					player.sendMessage("You need 2 points set for the out-zone!");
				}
			}
			if (step.equalsIgnoreCase("kothflag")) 
			{
				if (flags.size() > 0) 
				{
					stepUp();
					player.sendMessage(ChatColor.GRAY + "Done with flag point");
				}
				else
				{
					player.sendMessage(ChatColor.GRAY + "You need to add a flag point");
				}
			}
			if (step.equalsIgnoreCase("mobspawn"))
			{
				if (spawns.size() > 0) 
				{
					stepUp();
					player.sendMessage(ChatColor.GRAY + "Done with mob spawnpoints!");
				}
				else
				{
					player.sendMessage(ChatColor.GRAY + "Please set some mob spawnpoints");
				}
			}
			if (step.equalsIgnoreCase("flagspawn"))
			{
				if (flags.size() > 0)
				{
					if (arenaType.equals("cq")) 
					{
						if (flags.size() % 2 == 0) 
						{
							player.sendMessage(ChatColor.GRAY + "You need an odd number of flag spawns!");
						}
						else
						{
							stepUp();
							player.sendMessage(ChatColor.GRAY + "Done with flag spawnpoints!");
						}
					}
					if (arenaType.equals("bomb") || arenaType.equals("ctf"))
					{
						if (flags.size() != 2) 
						{
							player.sendMessage("You need at least 2 flags");
						}
						else
						{
							stepUp();
							player.sendMessage(ChatColor.GRAY + "Done with flag spawnpoints!");
						}
					}
				}
				else
				{
					player.sendMessage(ChatColor.GRAY + "Please set some flag spawnpoints");
				}
			}
		}
		if (stepnum >= steps.size()) 
		{
			saveArena(player);
		}
	}
	
	public void stepUp()
	{
		stepnum++;
		if (stepnum < steps.size()) 
		{
			step = steps.get(stepnum);
		}
	}
	
	public void stepDown()
	{
		stepnum--;
		step = steps.get(stepnum);
	}
	
	public void setPoint(Player player) 
	{
		Location loc = player.getLocation();
		setMsg("");
		if (lobby1 == null)
		{
			lobby1 = loc; setMsg("Lobby 1 point set, please set one more!");
		}
		else if (lobby2 == null)
		{
			lobby2 = loc; setMsg("Lobby 2 point set, if two points are set, use" + ChatColor.GOLD + " /ua done");
		}
		else if (arena1 == null) 
		{
			arena1 = loc; setMsg("Arena 1 point set, please set a second one!");
		}
		else if (arena2 == null)
		{
			arena2 = loc; setMsg("Arena 2 point set, if two points are set, use" + ChatColor.GOLD + " /ua done");
		}
		else
		{
			try
			{
				if (lobbyREDspawn == null) 
				{
					lobbyREDspawn = loc; setMsg("Red Team Lobby point set");
					if (amtLobbys > 1) 
					{
						setMsg(getMsg() + ", please set a second one for the BLU team");
					}
					else
					{
						setMsg(getMsg() + (", if the lobby points are done, use" + ChatColor.GOLD + " /ua done"));
					}
					return;
				}
				if (lobbyBLUspawn == null)
				{
					if (amtLobbys>1)
					{
						setMsg("Blu team lobby point set!, if the lobby points are done, use" + ChatColor.GOLD + " /ua done");
						lobbyBLUspawn = loc;
						return;
					}
				}
				if (step.contains("ArenaSpawn")) 
				{
					if (team1spawn == null)
					{
						team1spawn = loc; setMsg("RED spawn point set");
						if (amtSpawnpoints > 1)
						{
							setMsg(getMsg() + ", please set a second one for the BLU team");
						}
						else
						{
							setMsg(getMsg() + (", if the spawn points are done, use" + ChatColor.GOLD + " /ua done"));
						}
						return;
					}
					if (team2spawn == null) 
					{
						if (amtSpawnpoints > 1) 
						{
							team2spawn = loc;
							setMsg("BLUE spawn point set!, if the spawn points are done, use" + ChatColor.GOLD + " /ua done");
							return;
						}
					}
				}
				if (step.equalsIgnoreCase("playerspawn"))
				{
					this.spawns.add(player.getLocation());
					player.sendMessage(ChatColor.GRAY + "Added a player spawn!");
					return;
				}
				if (step.equalsIgnoreCase("spleefzone")) 
				{
					this.flags.add(player.getLocation());
					player.sendMessage(ChatColor.GRAY + "Added a spleefzone!");
					return;
				}
				if (step.equalsIgnoreCase("outzone"))
				{
					this.flags.add(player.getLocation());
					player.sendMessage(ChatColor.GRAY + "Added an outzone location!");
					return;
				}
				if (step.equalsIgnoreCase("kothflag"))
				{
					if (flags.size() == 0) {
						this.flags.add(player.getLocation());
						player.sendMessage("Added the flag point!");
						setMsg("please type " + ChatColor.GOLD + "/ua done");
						return;
					}
				}
				if (step.equalsIgnoreCase("MobSpawn"))
				{
					this.spawns.add(player.getLocation());
					player.sendMessage("Added mob spawn!");
					return;
				}
				if (step.equalsIgnoreCase("flagspawn"))
				{
					if (arenaType.equals("bomb") || arenaType.equals("ctf"))
					{
						if (flags.size() < 2) 
						{
							this.flags.add(player.getLocation());
							player.sendMessage(ChatColor.GRAY + "Added a flag spawn!");
						}
						else
						{
							player.sendMessage(ChatColor.GRAY + "Already have 2 flags!");
						}
					}
					else
					{
						this.flags.add(player.getLocation());
						player.sendMessage(ChatColor.GRAY + "Added a flag spawn!");
					}
					return;
				}
			}
			catch(Exception e) 
			{
				player.sendMessage(ChatColor.RED + "Error creating arena. Check console.");
				plugin.getLogger().severe("Error creating arena:");
				e.printStackTrace();
			}
		}
	}

	public String getMsg()
	{
		return msg;
	}
	
	public void setMsg(String msg)
	{
		this.msg = msg;
	}
	
	public String getPlayer()
	{
		return player;
	}

	public String getArenaName()
	{
		return arenaName;
	}

	public void setArenaName(String arenaName) 
	{
		this.arenaName = arenaName;
	}
}