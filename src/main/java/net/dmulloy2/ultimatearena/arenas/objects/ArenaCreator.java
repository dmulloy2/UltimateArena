package net.dmulloy2.ultimatearena.arenas.objects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.util.FormatUtil;
import net.dmulloy2.ultimatearena.util.Util;

public class ArenaCreator 
{
	protected String step;
	protected String player;
	protected String msg = "";
	protected String arenaName = "";
	protected FieldType arenaType;
	
	protected Location lobby1 = null;
	protected Location lobby2 = null;
	protected Location arena1 = null;
	protected Location arena2 = null;
	protected Location team1spawn = null;
	protected Location team2spawn = null;
	protected Location lobbyREDspawn = null;
	protected Location lobbyBLUspawn = null;
	
	protected int amtLobbys = 2;
	protected int amtSpawnpoints = 2;

	protected List<String> steps = new ArrayList<String>();
	protected List<Location> spawns = new ArrayList<Location>();
	protected List<Location> flags = new ArrayList<Location>();
	
	protected int stepnum;
	
	protected final UltimateArena plugin;
	
	public ArenaCreator(UltimateArena plugin, Player player)
	{
		this.player = player.getName();
		this.plugin = plugin;
	}
	
	public void setArena(String arenaName, String arenaType)
	{
		this.setArenaName(arenaName);
		this.arenaType = FieldType.getByName(arenaType);
		
		this.steps.add("Lobby");
		this.steps.add("Arena");
		this.steps.add("LobbySpawn1");
		if (arenaType.equalsIgnoreCase("pvp") || arenaType.equalsIgnoreCase("infect")) 
		{
			this.steps.add("LobbySpawn2");
			this.steps.add("ArenaSpawn1");
			this.steps.add("ArenaSpawn2");
		}
		if (arenaType.equalsIgnoreCase("ctf"))
		{
			this.steps.add("LobbySpawn2");
			this.steps.add("ArenaSpawn1");
			this.steps.add("ArenaSpawn2");
			this.steps.add("FlagSpawn");
		}
		if (arenaType.equalsIgnoreCase("cq"))
		{
			this.steps.add("LobbySpawn2");
			this.steps.add("ArenaSpawn1");
			this.steps.add("ArenaSpawn2");
			this.steps.add("FlagSpawn");
		}
		if (arenaType.equalsIgnoreCase("koth"))
		{
			amtLobbys = 1;
			this.steps.add("playerspawn");
			this.steps.add("kothflag");
		}
		if (arenaType.equalsIgnoreCase("ffa") || arenaType.equalsIgnoreCase("hunger")) 
		{
			amtLobbys = 1;
			this.steps.add("playerspawn");
		}
		if (arenaType.equalsIgnoreCase("spleef")) 
		{
			amtLobbys = 1;
			this.steps.add("spleefzone");
			this.steps.add("outzone");
		}
		if (arenaType.equalsIgnoreCase("bomb"))
		{
			this.steps.add("LobbySpawn2");
			this.steps.add("ArenaSpawn1");
			this.steps.add("ArenaSpawn2");
			this.steps.add("FlagSpawn");
		}
		if (arenaType.equalsIgnoreCase("mob"))
		{
			amtLobbys = 1;
			amtSpawnpoints = 1;
			this.steps.add("ArenaSpawn1");
			this.steps.add("MobSpawn");
		}
		
		this.step = steps.get(stepnum);
		
		sendMessage("&7Arena: &6{0} &7has been initialized. Type: &6{1}", arenaName, arenaType);
		sendMessage("&7Please set two points for a lobby! &6/ua setpoint");
	}
	
	public void complete()
	{
		// World Check :)
		if (lobby1.getWorld().getUID() != arena1.getWorld().getUID())
		{
			sendMessage("&cCould not create Arena: The lobby and arena must be in the same world!");
			sendMessage("&cEach arena should have its own lobby, one central lobby is not supported!");
			
			plugin.makingArena.remove(this);
			return;
		}
		
		ArenaZone az = new ArenaZone(plugin, this);
		if (az.isLoaded())
		{
			sendMessage("&aSuccessfully created arena &e{0}&a!", az.getArenaName());
			sendMessage("&aUse &e/ua join {0} &ato play!", az.getArenaName());
		}
		else
		{
			sendMessage("&cCould not create arena! Check Console for errors!");
		}
		
		plugin.makingArena.remove(this);
	}

	public void setDone(Player player)
	{
		if (step.equalsIgnoreCase("lobby")) 
		{
			if (lobby1 != null && lobby2 != null)
			{
				sendMessage("&7Done setting up Lobby! please set 2 points for the arena");
				stepUp();
			}
			else
			{
				sendMessage("&7You are not done creating the lobby!");
			}
		}
		else if (step.equalsIgnoreCase("arena")) 
		{
			if (arena1 != null && arena2 != null)
			{
				sendMessage("&7Done setting up Arena! please set a lobby spawn for RED team");
				stepUp();
			}
			else
			{
				sendMessage("&7You are not done creating the arena!");
			}
		}
		else if (step.equalsIgnoreCase("lobbyspawn1")) 
		{
			if (lobbyREDspawn != null) 
			{
				if (amtLobbys > 1) 
				{
					sendMessage("&7Done setting up lobby spawns!");
					sendMessage("&7Please create the RED team arena spawnpoint");
					stepUp();
				}
				else
				{
					if (arenaType.getName().equalsIgnoreCase("koth") || arenaType.getName().equalsIgnoreCase("ffa") || arenaType.getName().equalsIgnoreCase("hunger"))
					{
						sendMessage("&7Please add some player spawnpoints with &6/ua setpoint");
						sendMessage("&7use &6/ua done&7 when done");
					}
					else
					{
						if (arenaType.getName().equalsIgnoreCase("spleef"))
						{
							sendMessage("&7Done with lobby spawns");
							sendMessage("&7Please create 2 spleefzone points");
						}
						else
						{
							sendMessage("&7Done with lobby spawns, please create an arena spawnpoint(s)");
						}
					}
				}
				stepUp();
			}
			else
			{
				sendMessage("&7You are not done creating the lobby 1 spawn!");
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
						sendMessage("&7Done with player spawns");
						stepUp();//get passed arenaspawn2 step, since it's created already :3 (fail coding, I know)
						if (arenaType.getName().equalsIgnoreCase("cq"))
						{
							sendMessage("&7Please add some flag points");
							sendMessage("&7use &6/ua done&7 when done");
						}
						if (arenaType.getName().equalsIgnoreCase("bomb") || arenaType.getName().equalsIgnoreCase("ctf"))
						{
							sendMessage("&7Please add 2 flag points");
							sendMessage("&7use &6/ua done&7 when done");
						}
					}
					else
					{
						sendMessage("&7You are not done creating the BLUE team arena spawn point!");
						stepDown();
					}
				}
				else
				{
					sendMessage("&7Done with player spawns");
					if (arenaType.getName().equalsIgnoreCase("mob"))
					{
						sendMessage("&7Please set some mob spawnpoints");
						sendMessage("&7use &6/ua done&7 when done");
					}
				}
			}
			else
			{
				sendMessage("&7You are not done creating the RED team arena spawn point!");
			}
		}
		else
		{
			if (step.equalsIgnoreCase("playerspawn"))
			{
				if (spawns.size() > 0) 
				{
					stepUp();
					sendMessage("&7Done with player spawns");
					if (arenaType.getName().equalsIgnoreCase("koth"))
					{
						sendMessage("&7please add a flag spawn");	
					}
				}
				else
				{
					sendMessage("&7You need more than 0 player spawns");
				}
			}
			if (step.equalsIgnoreCase("spleefzone"))
			{
				if (flags.size() == 2)
				{
					stepUp();
					sendMessage("&7Done with spleef zone!");
				}
				else
				{
					sendMessage("&7You need 2 points set for the spleef zone!");
				}
			}
			if (step.equalsIgnoreCase("outzone")) 
			{
				if (flags.size() == 4) 
				{
					stepUp();
					sendMessage("&7Done with out-zone!");
				}
				else
				{
					sendMessage("&7You need 2 points set for the out-zone!");
				}
			}
			if (step.equalsIgnoreCase("kothflag")) 
			{
				if (flags.size() > 0) 
				{
					stepUp();
					sendMessage("&7Done with flag point");
				}
				else
				{
					sendMessage("&7You need to add a flag point");
				}
			}
			if (step.equalsIgnoreCase("mobspawn"))
			{
				if (spawns.size() > 0) 
				{
					stepUp();
					sendMessage("&7Done with mob spawnpoints!");
				}
				else
				{
					sendMessage("&7Please set some mob spawnpoints");
				}
			}
			if (step.equalsIgnoreCase("flagspawn"))
			{
				if (flags.size() > 0)
				{
					if (arenaType.getName().equalsIgnoreCase("cq")) 
					{
						if (flags.size() % 2 == 0) 
						{
							sendMessage("&7You need an odd number of flag spawns!");
						}
						else
						{
							stepUp();
							sendMessage("&7Done with flag spawnpoints!");
						}
					}
					if (arenaType.getName().equalsIgnoreCase("bomb") || arenaType.getName().equalsIgnoreCase("ctf"))
					{
						if (flags.size() != 2) 
						{
							sendMessage("&cYou need at least 2 flags");
						}
						else
						{
							stepUp();
							sendMessage("&7Done with flag spawnpoints!");
						}
					}
				}
				else
				{
					sendMessage("&7Please set some flag spawnpoints");
				}
			}
		}
		
		if (stepnum >= steps.size()) 
		{
			complete();
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
			lobby2 = loc; setMsg("Lobby 2 point set, if two points are set, use &6/ua done");
		}
		else if (arena1 == null) 
		{
			arena1 = loc; setMsg("Arena 1 point set, please set a second one!");
		}
		else if (arena2 == null)
		{
			arena2 = loc; setMsg("Arena 2 point set, if two points are set, use &6/ua done");
		}
		else
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
					setMsg(getMsg() + (", if the lobby points are done, use &6/ua done"));
				}
				
				return;
			}
			if (lobbyBLUspawn == null)
			{
				if (amtLobbys > 1)
				{
					setMsg("Blu team lobby point set!, if the lobby points are done, use &6/ua done");
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
						setMsg(getMsg() + (", if the spawn points are done, use &6/ua done"));
					}
					
					return;
				}
				if (team2spawn == null) 
				{
					if (amtSpawnpoints > 1) 
					{
						team2spawn = loc;
						setMsg("BLUE spawn point set!, if the spawn points are done, use &6/ua done");
						return;
					}
				}
			}
			if (step.equalsIgnoreCase("playerspawn"))
			{
				this.spawns.add(player.getLocation());
				sendMessage("&7Added a player spawn!");
				return;
			}
			if (step.equalsIgnoreCase("spleefzone")) 
			{
				this.flags.add(player.getLocation());
				sendMessage("&7Added a spleefzone!");
				return;
			}
			if (step.equalsIgnoreCase("outzone"))
			{
				this.flags.add(player.getLocation());
				sendMessage("&7Added an outzone location!");
				return;
			}
			if (step.equalsIgnoreCase("kothflag"))
			{
				if (flags.size() == 0) 
				{
					this.flags.add(player.getLocation());
					sendMessage("&7Added the flag point!");
					setMsg("please type &6/ua done");
					return;
				}
			}
			if (step.equalsIgnoreCase("MobSpawn"))
			{
				this.spawns.add(player.getLocation());
				sendMessage("&7Added mob spawn!");
				return;
			}
			if (step.equalsIgnoreCase("flagspawn"))
			{
				if (arenaType.getName().equalsIgnoreCase("bomb") || arenaType.getName().equalsIgnoreCase("ctf"))
				{
					if (flags.size() < 2) 
					{
						this.flags.add(player.getLocation());
						sendMessage("&7Added a flag spawn!");
					}
					else
					{
						sendMessage("&7Already have 2 flags!");
					}
				}
				else
				{
					this.flags.add(player.getLocation());
					sendMessage("&7Added a flag spawn!");
				}
				return;
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
	
	public void sendMessage(String string, Object... objects)
	{
		Player player = Util.matchPlayer(this.player);
		if (player != null)
		{
			player.sendMessage(plugin.getPrefix() + FormatUtil.format(string, objects));
		}
	}
}