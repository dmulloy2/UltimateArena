/**
* UltimateArena - a bukkit plugin
* Copyright (C) 2013 Minesworn/dmulloy2
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package net.dmulloy2.ultimatearena;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import lombok.Getter;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.arenas.Arena.Mode;
import net.dmulloy2.ultimatearena.arenas.BOMBArena;
import net.dmulloy2.ultimatearena.arenas.CONQUESTArena;
import net.dmulloy2.ultimatearena.arenas.CTFArena;
import net.dmulloy2.ultimatearena.arenas.FFAArena;
import net.dmulloy2.ultimatearena.arenas.HUNGERArena;
import net.dmulloy2.ultimatearena.arenas.INFECTArena;
import net.dmulloy2.ultimatearena.arenas.KOTHArena;
import net.dmulloy2.ultimatearena.arenas.MOBArena;
import net.dmulloy2.ultimatearena.arenas.PVPArena;
import net.dmulloy2.ultimatearena.arenas.SPLEEFArena;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaClass;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaConfig;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaCreator;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaPlayer;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaSign;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaZone;
import net.dmulloy2.ultimatearena.arenas.objects.FieldType;
import net.dmulloy2.ultimatearena.arenas.objects.WhiteListCommands;
import net.dmulloy2.ultimatearena.commands.CmdClass;
import net.dmulloy2.ultimatearena.commands.CmdClassList;
import net.dmulloy2.ultimatearena.commands.CmdCreate;
import net.dmulloy2.ultimatearena.commands.CmdDelete;
import net.dmulloy2.ultimatearena.commands.CmdDisable;
import net.dmulloy2.ultimatearena.commands.CmdDislike;
import net.dmulloy2.ultimatearena.commands.CmdEnable;
import net.dmulloy2.ultimatearena.commands.CmdForceStop;
import net.dmulloy2.ultimatearena.commands.CmdHelp;
import net.dmulloy2.ultimatearena.commands.CmdInfo;
import net.dmulloy2.ultimatearena.commands.CmdJoin;
import net.dmulloy2.ultimatearena.commands.CmdKick;
import net.dmulloy2.ultimatearena.commands.CmdLeave;
import net.dmulloy2.ultimatearena.commands.CmdLike;
import net.dmulloy2.ultimatearena.commands.CmdList;
import net.dmulloy2.ultimatearena.commands.CmdPause;
import net.dmulloy2.ultimatearena.commands.CmdReload;
import net.dmulloy2.ultimatearena.commands.CmdSetDone;
import net.dmulloy2.ultimatearena.commands.CmdSetPoint;
import net.dmulloy2.ultimatearena.commands.CmdStart;
import net.dmulloy2.ultimatearena.commands.CmdStats;
import net.dmulloy2.ultimatearena.commands.CmdStop;
import net.dmulloy2.ultimatearena.commands.CommandHandler;
import net.dmulloy2.ultimatearena.listeners.BlockListener;
import net.dmulloy2.ultimatearena.listeners.EntityListener;
import net.dmulloy2.ultimatearena.listeners.PlayerListener;
import net.dmulloy2.ultimatearena.listeners.SwornGunsListener;
import net.dmulloy2.ultimatearena.permissions.Permission;
import net.dmulloy2.ultimatearena.permissions.PermissionHandler;
import net.dmulloy2.ultimatearena.util.FormatUtil;
import net.dmulloy2.ultimatearena.util.InventoryHelper;
import net.dmulloy2.ultimatearena.util.Util;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

public class UltimateArena extends JavaPlugin
{
	private @Getter FileHelper fileHelper;
	private @Getter Economy economy;
	
	private @Getter PermissionHandler permissionHandler;
	private @Getter CommandHandler commandHandler;
	
	private @Getter SignManager signManager;
	
	public List<ArenaJoinTask> waiting = new ArrayList<ArenaJoinTask>();
	public List<ArenaCreator> makingArena = new ArrayList<ArenaCreator>();
	public List<ArenaConfig> configs = new ArrayList<ArenaConfig>();
	public List<ArenaClass> classes = new ArrayList<ArenaClass>();
	public List<ArenaSign> arenaSigns = new ArrayList<ArenaSign>();
	public List<ArenaZone> loadedArena = new ArrayList<ArenaZone>();
	public List<Arena> activeArena = new ArrayList<Arena>();
	
	public WhiteListCommands wcmd = new WhiteListCommands();
	
	public int arenasPlayed = 0;
	
	private @Getter String prefix = FormatUtil.format("&6[&4&lUA&6] ");

	@Override
	public void onEnable()
	{
		long start = System.currentTimeMillis();

		// IO Stuff
		checkDirectories();
		saveDefaultConfig();

		// Register Handlers and Helpers
		permissionHandler =  new PermissionHandler(this);
		commandHandler = new CommandHandler(this);
		
		fileHelper = new FileHelper(this);

		// Register Commands
		commandHandler.setCommandPrefix("ua");
		commandHandler.registerCommand(new CmdClass(this));
		commandHandler.registerCommand(new CmdClassList(this));
		commandHandler.registerCommand(new CmdCreate(this));
		commandHandler.registerCommand(new CmdDelete(this));
		commandHandler.registerCommand(new CmdDisable(this));
		commandHandler.registerCommand(new CmdDislike(this));
		commandHandler.registerCommand(new CmdEnable(this));
		commandHandler.registerCommand(new CmdForceStop(this));
		commandHandler.registerCommand(new CmdHelp(this));
		commandHandler.registerCommand(new CmdInfo(this));		
		commandHandler.registerCommand(new CmdJoin(this));
		commandHandler.registerCommand(new CmdKick(this));
		commandHandler.registerCommand(new CmdLeave(this));
		commandHandler.registerCommand(new CmdLike(this));
		commandHandler.registerCommand(new CmdList(this));
		commandHandler.registerCommand(new CmdPause(this));
		commandHandler.registerCommand(new CmdReload(this));
		commandHandler.registerCommand(new CmdSetDone(this));
		commandHandler.registerCommand(new CmdSetPoint(this));
		commandHandler.registerCommand(new CmdStart(this));
		commandHandler.registerCommand(new CmdStats(this));	
		commandHandler.registerCommand(new CmdStop(this));
		
		// SwornGuns
		PluginManager pm = getServer().getPluginManager();
		if (pm.isPluginEnabled("SwornGuns"))
		{
			pm.registerEvents(new SwornGunsListener(this), this);
			debug("Enabling SwornGuns integration!");
		}
		
		// Register Other Listeners
		pm.registerEvents(new EntityListener(this), this);
		pm.registerEvents(new BlockListener(this), this);
		pm.registerEvents(new PlayerListener(this), this);
		
		// Vault
		checkVault(pm);

		// Arena Updater
		new ArenaUpdateTask().runTaskTimer(this, 2L, 20L);

		// Load Arenas
		loadFiles();
		
		// Arena Signs
		signManager = new SignManager(this);
		outConsole("Loaded {0} arena signs!", arenaSigns.size());

		long finish = System.currentTimeMillis();
		
		outConsole("{0} has been enabled ({1}ms)", getDescription().getFullName(), finish - start);
	}

	@Override
	public void onDisable()
	{
		long start = System.currentTimeMillis();
		
		// Unregister
		getServer().getServicesManager().unregisterAll(this);
		getServer().getScheduler().cancelTasks(this);

		// Stop all arenas
		stopAll();
		
		// Save Signs
		signManager.refreshSave();
		
		// Refresh arena saves
		for (ArenaZone az : loadedArena)
		{
			az.save();
		}
		
		// Clear Memory
		clearMemory();
		
		long finish = System.currentTimeMillis();
		
		outConsole("{0} has been disabled ({1}ms)", getDescription().getFullName(), finish - start);
	}
	
	// Console logging
	public void outConsole(Level level, String string, Object...objects)
	{
		getLogger().log(level, FormatUtil.format(string, objects));
	}
	
	
	public void outConsole(String string, Object...objects)
	{
		outConsole(Level.INFO, string, objects);
	}
	
	public void debug(String string, Object...objects)
	{
		if (getConfig().getBoolean("debug", false))
		{
			outConsole("[Debug] " + string, objects);
		}
	}
	
	public void broadcast(String string, Object...objects)
	{
		String broadcast = FormatUtil.format(string, objects);
		getServer().broadcastMessage(prefix + broadcast);
		
		debug("Broadcasted message: {0}", broadcast);
	}
	
	// Create Directories
	public void checkDirectories()
	{
		debug("Checking directories!");
		
		File dataFile = getDataFolder();
		if (! dataFile.exists())
		{
			dataFile.mkdir();
			debug("Created data file!");
		}
		
		File arenaFile = new File(getDataFolder(), "arenas");
		if (! arenaFile.exists())
		{
			arenaFile.mkdir();
			debug("Created arenas directory!");
		}
		
		File playersFile = new File(getDataFolder(), "players");
		if (playersFile.exists())
		{
			File[] children = playersFile.listFiles();
			if (children != null && children.length > 0)
			{
				for (File file : children)
				{
					file.delete();
				}
			}
			
			playersFile.delete();
			debug("Deleted players directory!");
		}
		
		File classFile = new File(getDataFolder(), "classes");
		if (! classFile.exists())
		{
			classFile.mkdir();
			debug("Created classes directory!");
		}
		
		File configsFile = new File(getDataFolder(), "configs");
		if (! configsFile.exists())
		{
			configsFile.mkdir();
			debug("Created configs directory!");
		}
	}

	// Normalize player if in arena
	public void onQuit(Player player)
	{
		if (isPlayerCreatingArena(player)) 
		{
			debug("Player {0} left the game, stopping the creation of an arena", player.getName());
			makingArena.remove(getArenaCreator(player));
		}
		
		if (isInArena(player))
		{
			debug("Player {0} leaving arena from quit", player.getName());

			Arena a = getArena(player);
			a.endPlayer(getArenaPlayer(player), false);
			a.tellPlayers("&b{0} has left the arena!", player.getName());
			
		}
	} 
	
	// Load Stuff
	public void loadArenas()
	{
		File folder = new File(getDataFolder(), "arenas");
		File[] children = folder.listFiles();
		
		int total = 0;
		for (File file : children)
		{
			ArenaZone az = new ArenaZone(this, file);
			if (az.isLoaded())
			{
				loadedArena.add(az);
				debug("Successfully loaded arena {0}!", az.getArenaName());
				total++;
			}
		}
		
		outConsole("Loaded {0} arena files!", total);
	}
	
	public void loadConfigs() 
	{
		int total = 0;
		for (FieldType type : FieldType.values())
		{
			if (loadConfig(type.getName()))
				total++;
		}
		
		outConsole("Loaded {0} arena config files!", total);
		
		loadWhiteListedCommands();
	}
	
	public void loadWhiteListedCommands()
	{
		File file = new File(getDataFolder(), "whiteListedCommands.yml");
		if (!file.exists())
		{
			outConsole("Generating WhiteListedCommands file!");
			fileHelper.generateWhitelistedCmds();
		}
		
		YamlConfiguration fc = YamlConfiguration.loadConfiguration(file);
		List<String> whiteListedCommands = fc.getStringList("whiteListedCmds");
		for (String whiteListed : whiteListedCommands)
		{
			wcmd.addCommand(whiteListed);
			debug("Added whitelisted command: \"{0}\"!", whiteListed);
		}
		
		outConsole("Loaded {0} whitelisted commands!", wcmd.size());
	}
	
	public boolean loadConfig(String str)
	{
		File folder = new File(getDataFolder(), "configs");
		File file = new File(folder, str + "Config.yml");
		if (!file.exists())
		{
			outConsole("Generating config for: {0}", str);
			fileHelper.generateArenaConfig(str);
		}
		
		ArenaConfig a = new ArenaConfig(this, str, file);
		if (a.isLoaded())
		{
			configs.add(a);
			return true;
		}
		
		return false;
	}
	
	public void loadClasses() 
	{
		File folder = new File(getDataFolder(), "classes");
		File[] children = folder.listFiles();
		if (children.length == 0)
		{
			fileHelper.generateStockClasses();
			outConsole("Generating stock classes!");
		}

		children = folder.listFiles();

		int total = 0;
		for (File file : children)
		{
			ArenaClass ac = new ArenaClass(this, file);
			if (ac.isLoaded())
			{
				classes.add(ac);
				total++;
			}
		}
		
		outConsole("Loaded {0} Arena Classes!", total);
	}
	
	public ArenaConfig getConfig(String type) 
	{
		for (int i = 0; i < configs.size(); i++)
		{
			ArenaConfig ac = configs.get(i);
			if (ac.getArenaName().equalsIgnoreCase(type))
				return ac;
		}
		
		return null;
	}

	public void stopAll()
	{
		for (int i = 0; i < activeArena.size(); i++)
		{
			Arena arena = activeArena.get(i);
			if (arena != null)
			{
				arena.stop();
			}
		}
		
		activeArena.clear();
	}

	public ArenaSign getArenaSign(Location loc)
	{
		for (int i = 0; i < arenaSigns.size(); i++)
		{
			ArenaSign sign = arenaSigns.get(i);
			if (Util.checkLocation(sign.getLocation(), loc))
				return sign;
		}
		
		return null;
	}
	
	public ArenaClass getArenaClass(String line)
	{
		for (int i = 0; i < classes.size(); i++)
		{
			ArenaClass ac = classes.get(i);
			if (ac.getName().equalsIgnoreCase(line))
				return ac;
		}
		
		return null;
	}
	
	// Delete Stuff!
	public void deleteArena(Player player, String str) 
	{
		File folder = new File(getDataFolder(), "arenas");
		File file = new File(folder, str + ".dat");
		if (file.exists())
		{
			for (int i = 0; i < activeArena.size(); i++)
			{
				Arena a = activeArena.get(i);
				if (a.getName().equalsIgnoreCase(str))
				{
					a.stop();
				}
			}
				
			loadedArena.remove(getArenaZone(str));
				
			file.delete();
			
			for (int i = 0; i < arenaSigns.size(); i++)
			{
				ArenaSign as = arenaSigns.get(i);
				if (as.getArena().equalsIgnoreCase(str))
				{
					deleteSign(as);
				}
			}
			
			player.sendMessage(prefix + FormatUtil.format("&7Successfully deleted arena: &6{0}&7!", str));
			
			outConsole("Successfully deleted arena: {0}!", str);
		}
		else
		{
			player.sendMessage(prefix + FormatUtil.format("&cCould not find an arena by the name of \"{0}\"!", str));
		}
	}
	
	public void deleteSign(ArenaSign sign)
	{
		debug("Deleting sign {0}!", sign.getId());
		
		signManager.deleteSign(sign);
	}
	
	// Checks for whether or not something is in an arena
	public boolean isInArena(Location loc)
	{
		for (int i = 0; i < loadedArena.size(); i++)
		{
			ArenaZone az = loadedArena.get(i);
			if (az.checkLocation(loc))
				return true;
		}
		
		return false;
	}
	
	public boolean isInArena(Entity entity)
	{
		return isInArena(entity.getLocation());
	}
	
	public boolean isInArena(Block block) 
	{
		return isInArena(block.getLocation());
	}
	
	// Special case for player
	public boolean isInArena(Player player) 
	{
		return (getArenaPlayer(player) != null);
	}
	
	public Arena getArenaInside(Block block)
	{
		for (int i = 0; i < loadedArena.size(); i++)
		{
			ArenaZone az = loadedArena.get(i);
			if (az.checkLocation(block.getLocation()))
				return getArena(az.getArenaName());
		}
		
		return null;
	}

	public ArenaPlayer getArenaPlayer(Player player) 
	{
		for (int i = 0; i < activeArena.size(); i++)
		{
			Arena a = activeArena.get(i);
			ArenaPlayer ap = a.getArenaPlayer(player);
			if (ap != null && !ap.isOut())
			{
				if (ap.getPlayer().getName().equals(player.getName())) 
					return ap;
			}
		}
		
		return null;
	}
	
	public void leaveArena(Player player)
	{
		if (isInArena(player))
		{
			Arena a = getArena(player);
			ArenaPlayer ap = getArenaPlayer(player);
			a.endPlayer(ap, false);
			
			ap.sendMessage("&bYou have left the arena!");
			
			a.tellPlayers("&b{0} has left the arena!", player.getName());
		}
		else
		{
			player.sendMessage(prefix + FormatUtil.format("&cError, you are not in an arena"));
		}
	}

	// Pre-Join Stuff
	public void fight(Player player, String name, boolean forced)
	{
		if (! permissionHandler.hasPermission(player, Permission.JOIN))
		{
			player.sendMessage(prefix + FormatUtil.format("&cYou do not have permission to do this!"));
			return;
		}
		
		if (isPlayerCreatingArena(player))
		{
			player.sendMessage(prefix + FormatUtil.format("&cYou are in the middle of making an arena!"));
			return;
		}
		
		if (! InventoryHelper.isEmpty(player.getInventory()))
		{
			if (! getConfig().getBoolean("saveInventories"))
			{
				player.sendMessage(prefix + FormatUtil.format("&cPlease clear your inventory!"));
				return;
			}
		}
		
		ArenaZone a = getArenaZone(name);
		if (a == null)
		{
			player.sendMessage(prefix + FormatUtil.format("&cThat arena doesn't exist!"));
			return;
		}
		
		if (isInArena(player))
		{
			player.sendMessage(prefix + FormatUtil.format("&cYou''re already in an arena!"));
			return;
		}
		
		ArenaPlayer ap = getArenaPlayer(player);
		if (ap != null)
		{
			player.sendMessage(prefix + FormatUtil.format("&cYou cannot leave and rejoin an arena!"));
			return;
		}
		
		for (int i = 0; i < waiting.size(); i++)
		{
			ArenaJoinTask task = waiting.get(i);
			if (task.getPlayer().getName().equals(player.getName()))
			{
				player.sendMessage(prefix + FormatUtil.format("&cYou''re already waiting!"));
				return;
			}
		}
		
		ArenaJoinTask join = new ArenaJoinTask(this, player, name, forced);
		if (getConfig().getBoolean("joinTimer.enabled"))
		{
			int seconds = getConfig().getInt("joinTimer.wait");
			int wait = seconds * 20;
			
			join.runTaskLater(this, wait);
			waiting.add(join);
			
			player.sendMessage(prefix + FormatUtil.format("&6Please stand still for {0} seconds!", seconds));
		}
		else
		{
			join.run();
		}			
	}
	
	// Actually join battle
	public void joinBattle(boolean forced, Player player, String name)
	{
		debug("Player {0} is attempting to join arena {1}. Forced: {2}", player.getName(), name, forced);
		
		ArenaZone az = getArenaZone(name);
		Arena a = getArena(name);
		if (a != null)
		{
			if (a.getGameMode() == Mode.LOBBY)
			{
				if (a.getActivePlayers() + 1 <= az.getMaxPlayers())
				{
					a.addPlayer(player);
				}
				else
				{
					if (! forced)
					{
						player.sendMessage(prefix + FormatUtil.format("&cThis arena is full!"));
					}
					else
					{
						if (kickRandomPlayer(a))
						{
							a.addPlayer(player);
						}
						else
						{
							player.sendMessage(prefix + FormatUtil.format("&cCould not join the arena!"));
						}
					}
				}
			}
			else
			{
				player.sendMessage(prefix + FormatUtil.format("&cThis arena has already started!"));
			}
		}
		else
		{
			Arena ar = null;
			boolean disabled = false;
			for (int i = 0; i < activeArena.size(); i++)
			{
				Arena aar = activeArena.get(i);
				if (aar.getName().equalsIgnoreCase(name))
				{
					disabled = aar.isDisabled();
				}
			}
			
			for (int ii = 0; ii < loadedArena.size(); ii++)
			{
				ArenaZone aaz = loadedArena.get(ii);
				if (aaz.getArenaName().equalsIgnoreCase(name))
				{
					disabled = aaz.isDisabled();
				}
			}
			
			if (! disabled)
			{
				String arenaType = az.getType().getName().toLowerCase();
				if (arenaType.equals("pvp"))
				{
					ar = new PVPArena(az);
				}
				else if (arenaType.equals("mob")) 
				{
					ar = new MOBArena(az);
				}
				else if (arenaType.equals("cq"))
				{
					ar = new CONQUESTArena(az);
				}
				else if (arenaType.equals("koth")) 
				{
					ar = new KOTHArena(az);
				}
				else if (arenaType.equals("bomb")) 
				{
					ar = new BOMBArena(az);
				}
				else if (arenaType.equals("ffa"))
				{
					ar = new FFAArena(az);
				}
				else if (arenaType.equals("hunger")) 
				{
					ar = new HUNGERArena(az);
				}
				else if (arenaType.equals("spleef")) 
				{
					ar = new SPLEEFArena(az);
				}
				else if (arenaType.equals("infect"))
				{
					ar = new INFECTArena(az);
				}
				else if (arenaType.equals("ctf"))
				{	
					ar = new CTFArena(az);
				}
				if (ar != null) 
				{
					activeArena.add(ar);
					ar.addPlayer(player);
					ar.announce();
				}
			}
			else
			{
				player.sendMessage(prefix + FormatUtil.format("&cThis arena is disabled!"));
			}
		}
	}
	
	// Kicks a random player if the arena is full
	// This will only be called if someone with forcejoin joins
	public boolean kickRandomPlayer(Arena arena)
	{
		List<ArenaPlayer> validPlayers = new ArrayList<ArenaPlayer>();
		List<ArenaPlayer> totalPlayers = arena.getArenaPlayers();
		for (ArenaPlayer ap : totalPlayers)
		{
			if (! permissionHandler.hasPermission(ap.getPlayer(), Permission.FORCE_JOIN))
			{
				validPlayers.add(ap);
			}
		}
		
		int rand = Util.random(validPlayers.size());
		ArenaPlayer apl = validPlayers.get(rand);
		if (apl != null)
		{
			apl.sendMessage("&cYou have been kicked from the arena!");
			apl.getArena().endPlayer(apl, false);
			return true;
		}
		
		return false;
	}

	// Gets the arena a player is in
	public Arena getArena(Player player)
	{
		for (int i = 0; i < activeArena.size(); i++)
		{
			Arena a = activeArena.get(i);
			ArenaPlayer ap = a.getArenaPlayer(player);
			if (ap != null)
			{
				if (ap.getName().equals(player.getName()))
					return a;
			}
		}
		
		return null;
	}
	
	// Gets an arena by its name
	public Arena getArena(String name) 
	{
		for (int i = 0; i < activeArena.size(); i++)
		{
			Arena ac = activeArena.get(i);
			if (ac.getName().equals(name))
				return ac;
		}
		
		return null;
	}
	
	// Gets an arena zone by its name
	public ArenaZone getArenaZone(String name)
	{
		for (int i = 0; i < loadedArena.size(); i++)
		{
			ArenaZone az = loadedArena.get(i);
			if (az.getArenaName().equals(name)) 
				return az;
		}
		
		return null;
	}
	
	// Arena Creator stuff
	public void setPoint(Player player) 
	{
		ArenaCreator ac = getArenaCreator(player);
		if (ac != null)
		{
			ac.setPoint(player);
			if (!ac.getMsg().equals(""))
			{
				player.sendMessage(prefix + FormatUtil.format("&7" + ac.getMsg()));
			}
		}
		else
		{
			player.sendMessage(prefix + FormatUtil.format("&cYou aren''t editing a field!"));
		}
	}

	public void setDone(Player player)
	{
		ArenaCreator ac = getArenaCreator(player);
		if (ac != null) 
		{
			ac.setDone(player);
		}
		else
		{
			player.sendMessage(prefix + FormatUtil.format("&cYou aren''t editing a field!"));
		}
	}
	
	public boolean isPlayerCreatingArena(Player player) 
	{
		return (getArenaCreator(player) != null);
	}
	
	public void stopCreatingArena(Player player)
	{ 
		for (int i = 0; i < makingArena.size(); i++)
		{
			ArenaCreator ac = makingArena.get(i);
			if (ac.getPlayer().equalsIgnoreCase(player.getName()))
			{
				makingArena.remove(ac);
				player.sendMessage(prefix + FormatUtil.format("&7Stopped the creation of arena: &6{0}&7!", ac.getArenaName()));
			}
		}
	}

	public ArenaCreator getArenaCreator(Player player)
	{
		for (int i = 0; i < makingArena.size(); i++)
		{
			ArenaCreator ac = makingArena.get(i);
			if (ac.getPlayer().equalsIgnoreCase(player.getName()))
				return ac;
		}
		
		return null;
	}
	
	public void createField(Player player, String name, String type)
	{
		if (isPlayerCreatingArena(player))
		{
			player.sendMessage(prefix + FormatUtil.format("&cYou are already creating an arena!"));
			return;
		}
		
		if (! FieldType.contains(type.toLowerCase()))
		{
			player.sendMessage(prefix + FormatUtil.format("&cThis is not a valid field type!"));
			return;
		}
		
		for (int i = 0; i < loadedArena.size(); i++)
		{
			ArenaZone az = loadedArena.get(i);
			if (az.getArenaName().equalsIgnoreCase(name))
			{
				player.sendMessage(prefix + FormatUtil.format("&cAn arena by this name already exists!"));
				return;
			}
		}
		
		outConsole("Player {0} has started the creation of {1}. Type: {2}", player.getName(), name, type);

		ArenaCreator ac = new ArenaCreator(this, player);
		ac.setArena(name, type);
		makingArena.add(ac);
	}
	
	// Normalization
	public void normalizeAll()
	{
		for (Player player : getServer().getOnlinePlayers())
		{
			Location loc = player.getLocation();
			if (isInArena(loc))
			{
				normalize(player);
				if (isInArena(player))
				{
					leaveArena(player);
				}
			}
		}
	}
	
	public void normalize(Player player)
	{
		PlayerInventory inv = player.getInventory();
		
		inv.setHelmet(null);
		inv.setChestplate(null);
		inv.setLeggings(null);
		inv.setBoots(null);
		inv.clear();
	}

	// Load files
	public void loadFiles() 
	{
		loadClasses();
		loadConfigs();
		loadArenas();
	}
	
	// Clear memory
	public void clearMemory()
	{
		loadedArena.clear();
		activeArena.clear();
		makingArena.clear();
		arenaSigns.clear();
		waiting.clear();
		classes.clear();
		configs.clear();
		wcmd.clear();
	}
	
	// Removes potion effects
	public void removePotions(Player pl) 
	{
		for (PotionEffect effect : pl.getActivePotionEffects())
		{
			pl.removePotionEffect(effect.getType());
		}
	}
	
    // Vault Stuff
	private void checkVault(PluginManager pm) 
	{
		if (pm.isPluginEnabled("Vault"))
		{
			setupEconomy();
			
			if (economy != null)
			{
				outConsole("Enabled economy through {0}!", economy.getName());
			}
		}
	}

    private boolean setupEconomy() 
	{
    	debug("Setting up Vault economy");
    	
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
		if (economyProvider != null) 
		{
			economy = ((Economy)economyProvider.getProvider());
		}
 
		return economy != null;
	}

    // TODO: Replace these with much more active updaters
    public class ArenaUpdateTask extends BukkitRunnable
	{
		@Override
		public void run()
		{
			for (int i = 0; i < activeArena.size(); i++) 
			{
				Arena arena = activeArena.get(i);
				arena.update();
			}
		}
	}
}