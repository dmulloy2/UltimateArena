package net.dmulloy2.ultimatearena.arenas.objects;

import java.util.ArrayList;
import java.util.List;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.util.FormatUtil;
import net.dmulloy2.ultimatearena.util.Util;

import org.bukkit.Location;
import org.bukkit.entity.Player;

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
		
		sendMessage("&3Arena: &e{0} &3has been initialized. Type: &e{1}", arenaName, arenaType);
		sendMessage("&3Please set two points for a lobby! &e/ua setpoint");
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
				sendMessage("&3Done setting up Lobby! Please set &e2 &3points for the arena");
				stepUp();
			}
			else
			{
				sendMessage("&3You are not done creating the lobby!");
			}
		}
		else if (step.equalsIgnoreCase("arena")) 
		{
			if (arena1 != null && arena2 != null)
			{
				sendMessage("&3Done setting up Arena! please set a lobby spawn for RED team");
				stepUp();
			}
			else
			{
				sendMessage("&3You are not done creating the arena!");
			}
		}
		else if (step.equalsIgnoreCase("lobbyspawn1")) 
		{
			if (lobbyREDspawn != null) 
			{
				if (amtLobbys > 1) 
				{
					sendMessage("&3Done setting up lobby spawns!");
					sendMessage("&3Please create the RED team arena spawnpoint");
					stepUp();
				}
				else
				{
					if (arenaType.getName().equalsIgnoreCase("koth") || arenaType.getName().equalsIgnoreCase("ffa") || arenaType.getName().equalsIgnoreCase("hunger"))
					{
						sendMessage("&3Please add some player spawnpoints with &e/ua setpoint");
						sendMessage("&3use &e/ua done &3when done");
					}
					else
					{
						if (arenaType.getName().equalsIgnoreCase("spleef"))
						{
							sendMessage("&3Done with lobby spawns");
							sendMessage("&3Please create &e2 &3spleefzone points");
						}
						else
						{
							sendMessage("&3Done with lobby spawns, please create an arena spawnpoint(s)");
						}
					}
				}
				stepUp();
			}
			else
			{
				sendMessage("&3You are not done creating the lobby 1 spawn!");
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
						sendMessage("&3Done with player spawns");
						stepUp();//get passed arenaspawn2 step, since it's created already :3 (fail coding, I know)
						if (arenaType.getName().equalsIgnoreCase("cq"))
						{
							sendMessage("&3Please add some flag points");
							sendMessage("&3Use &e/ua done&3 when done");
						}
						if (arenaType.getName().equalsIgnoreCase("bomb") || arenaType.getName().equalsIgnoreCase("ctf"))
						{
							sendMessage("&3Please add &e2 &3flag points");
							sendMessage("&3Use &e/ua done&3 when done");
						}
					}
					else
					{
						sendMessage("&3You are not done creating the BLUE team arena spawn point!");
						stepDown();
					}
				}
				else
				{
					sendMessage("&3Done with player spawns");
					if (arenaType.getName().equalsIgnoreCase("mob"))
					{
						sendMessage("&3Please set some mob spawnpoints");
						sendMessage("&3use &e/ua done &3when done");
					}
				}
			}
			else
			{
				sendMessage("&3You are not done creating the RED team arena spawn point!");
			}
		}
		else
		{
			if (step.equalsIgnoreCase("playerspawn"))
			{
				if (spawns.size() > 0) 
				{
					stepUp();
					sendMessage("&3Done with player spawns");
					if (arenaType.getName().equalsIgnoreCase("koth"))
					{
						sendMessage("&3Please add a flag spawn");	
					}
				}
				else
				{
					sendMessage("&3You need more than &e0 &3player spawns");
				}
			}
			if (step.equalsIgnoreCase("spleefzone"))
			{
				if (flags.size() == 2)
				{
					stepUp();
					sendMessage("&3Done with spleef zone!");
				}
				else
				{
					sendMessage("&3You need &e2 &3points set for the spleef zone!");
				}
			}
			if (step.equalsIgnoreCase("outzone")) 
			{
				if (flags.size() == 4) 
				{
					stepUp();
					sendMessage("&3Done with out-zone!");
				}
				else
				{
					sendMessage("&3You need &e2 &3points set for the out-zone!");
				}
			}
			if (step.equalsIgnoreCase("kothflag")) 
			{
				if (flags.size() > 0) 
				{
					stepUp();
					sendMessage("&3Done with flag point");
				}
				else
				{
					sendMessage("&3You need to add a flag point");
				}
			}
			if (step.equalsIgnoreCase("mobspawn"))
			{
				if (spawns.size() > 0) 
				{
					stepUp();
					sendMessage("&3Done with mob spawnpoints!");
				}
				else
				{
					sendMessage("&3Please set some mob spawnpoints");
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
							sendMessage("&3You need an odd number of flag spawns!");
						}
						else
						{
							stepUp();
							sendMessage("&3Done with flag spawnpoints!");
						}
					}
					if (arenaType.getName().equalsIgnoreCase("bomb") || arenaType.getName().equalsIgnoreCase("ctf"))
					{
						if (flags.size() != 2) 
						{
							sendMessage("&cYou need at least &e2 &3flags");
						}
						else
						{
							stepUp();
							sendMessage("&3Done with flag spawnpoints!");
						}
					}
				}
				else
				{
					sendMessage("&3Please set some flag spawnpoints");
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
			lobby1 = loc; setMsg("Lobby &e1 &3point set, please set one more!");
		}
		else if (lobby2 == null)
		{
			lobby2 = loc; setMsg("Lobby &e2 &3point set, if two points are set, use &e/ua done");
		}
		else if (arena1 == null) 
		{
			arena1 = loc; setMsg("Arena &e1 &3point set, please set a second one!");
		}
		else if (arena2 == null)
		{
			arena2 = loc; setMsg("Arena &e2 &3point set, if two points are set, use &e/ua done");
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
					setMsg(getMsg() + (", if the lobby points are done, use &e/ua done"));
				}
				
				return;
			}
			if (lobbyBLUspawn == null)
			{
				if (amtLobbys > 1)
				{
					setMsg("BLUE team lobby point set!, if the lobby points are done, use &e/ua done");
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
						setMsg(getMsg() + ", please set a second one for the BLUE team");
					}
					else
					{
						setMsg(getMsg() + (", if the spawn points are done, use &e/ua done"));
					}
					
					return;
				}
				if (team2spawn == null) 
				{
					if (amtSpawnpoints > 1) 
					{
						team2spawn = loc;
						setMsg("BLUE spawn point set!, if the spawn points are done, use &e/ua done");
						return;
					}
				}
			}
			if (step.equalsIgnoreCase("playerspawn"))
			{
				this.spawns.add(player.getLocation());
				sendMessage("&3Added a player spawn!");
				return;
			}
			if (step.equalsIgnoreCase("spleefzone")) 
			{
				this.flags.add(player.getLocation());
				sendMessage("&3Added a spleefzone!");
				return;
			}
			if (step.equalsIgnoreCase("outzone"))
			{
				this.flags.add(player.getLocation());
				sendMessage("&3Added an outzone location!");
				return;
			}
			if (step.equalsIgnoreCase("kothflag"))
			{
				if (flags.size() == 0) 
				{
					this.flags.add(player.getLocation());
					sendMessage("&3Added the flag point!");
					setMsg("please type &e/ua done");
					return;
				}
			}
			if (step.equalsIgnoreCase("MobSpawn"))
			{
				this.spawns.add(player.getLocation());
				sendMessage("&3Added mob spawn!");
				return;
			}
			if (step.equalsIgnoreCase("flagspawn"))
			{
				if (arenaType.getName().equalsIgnoreCase("bomb") || arenaType.getName().equalsIgnoreCase("ctf"))
				{
					if (flags.size() < 2) 
					{
						this.flags.add(player.getLocation());
						sendMessage("&3Added a flag spawn!");
					}
					else
					{
						sendMessage("&3Already have &e2 &3flags!");
					}
				}
				else
				{
					this.flags.add(player.getLocation());
					sendMessage("&3Added a flag spawn!");
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