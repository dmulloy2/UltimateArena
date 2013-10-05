/**
 * UltimateArena (C) 2013 MineSworn / dmulloy2
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
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import lombok.Getter;
import net.dmulloy2.ultimatearena.arenas.Arena;
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
import net.dmulloy2.ultimatearena.commands.CmdSpectate;
import net.dmulloy2.ultimatearena.commands.CmdStart;
import net.dmulloy2.ultimatearena.commands.CmdStats;
import net.dmulloy2.ultimatearena.commands.CmdStop;
import net.dmulloy2.ultimatearena.commands.CmdVersion;
import net.dmulloy2.ultimatearena.handlers.CommandHandler;
import net.dmulloy2.ultimatearena.handlers.FileHandler;
import net.dmulloy2.ultimatearena.handlers.LogHandler;
import net.dmulloy2.ultimatearena.handlers.PermissionHandler;
import net.dmulloy2.ultimatearena.handlers.SignHandler;
import net.dmulloy2.ultimatearena.handlers.SpectatingHandler;
import net.dmulloy2.ultimatearena.handlers.WorldEditHandler;
import net.dmulloy2.ultimatearena.listeners.BlockListener;
import net.dmulloy2.ultimatearena.listeners.EntityListener;
import net.dmulloy2.ultimatearena.listeners.PlayerListener;
import net.dmulloy2.ultimatearena.listeners.SwornGunsListener;
import net.dmulloy2.ultimatearena.tasks.ArenaJoinTask;
import net.dmulloy2.ultimatearena.types.ArenaClass;
import net.dmulloy2.ultimatearena.types.ArenaConfig;
import net.dmulloy2.ultimatearena.types.ArenaCreator;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.ArenaSign;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.FieldType;
import net.dmulloy2.ultimatearena.types.LeaveReason;
import net.dmulloy2.ultimatearena.types.Permission;
import net.dmulloy2.ultimatearena.util.FormatUtil;
import net.dmulloy2.ultimatearena.util.InventoryHelper;
import net.dmulloy2.ultimatearena.util.Util;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.earth2me.essentials.Essentials;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

/**
 * @author dmulloy2
 */

public class UltimateArena extends JavaPlugin
{
	// Economy
	private @Getter Economy economy;

	// Handlers
	private @Getter PermissionHandler permissionHandler;
	private @Getter SpectatingHandler spectatingHandler;
	private @Getter CommandHandler commandHandler;
	private @Getter FileHandler fileHandler;
	private @Getter SignHandler signHandler;
	private @Getter LogHandler logHandler;

	// WorldEdit
	private @Getter WorldEditPlugin worldEdit;
	private @Getter WorldEditHandler worldEditHandler;
	
	// Essentials
	private @Getter Essentials essentials;
	private @Getter boolean useEssentials;
	
	// Lists
	private @Getter HashMap<Player, ArenaJoinTask> waiting = new HashMap<Player, ArenaJoinTask>();
	private @Getter List<ArenaCreator> makingArena = new ArrayList<ArenaCreator>();
	private @Getter List<ArenaConfig> configs = new ArrayList<ArenaConfig>();
	private @Getter List<JavaPlugin> pluginsUsingAPI = new ArrayList<JavaPlugin>();
	private @Getter List<ArenaClass> classes = new ArrayList<ArenaClass>();
	private @Getter List<ArenaSign> arenaSigns = new ArrayList<ArenaSign>();
	private @Getter List<ArenaZone> loadedArenas = new ArrayList<ArenaZone>();
	private @Getter List<String> whitelistedCommands = new ArrayList<String>();
	private @Getter List<Arena> activeArenas = new ArrayList<Arena>();
	
	private @Getter boolean stopping;

	// Global prefix
	private @Getter String prefix = FormatUtil.format("&6[&4&lUA&6] ");

	@Override
	public void onEnable()
	{
		long start = System.currentTimeMillis();
		
		// Register Handlers
		permissionHandler = new PermissionHandler(this);
		spectatingHandler = new SpectatingHandler(this);
		commandHandler = new CommandHandler(this);
		fileHandler = new FileHandler(this);
		logHandler = new LogHandler(this);

		// Dependencies
		if (! checkDependencies())
			return;
		
		setupWorldEditIntegration();
		
		worldEditHandler = new WorldEditHandler(this);
		
		// IO Stuff
		checkDirectories();
		saveDefaultConfig();

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
		commandHandler.registerCommand(new CmdSpectate(this));
		commandHandler.registerCommand(new CmdStart(this));
		commandHandler.registerCommand(new CmdStats(this));
		commandHandler.registerCommand(new CmdStop(this));
		commandHandler.registerCommand(new CmdVersion(this));

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
		setupVaultIntegration();

		// Load Files
		loadFiles();

		// Arena Updater
		new ArenaUpdateTask().runTaskTimer(this, 2L, 20L);

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
		
		stopping = true;

		// Stop all arenas
		stopAll();

		// Save Signs
		signHandler.refreshSave();

		// Refresh arena saves
		for (ArenaZone az : loadedArenas)
		{
			az.save();
		}

		// Clear Memory
		clearMemory();

		long finish = System.currentTimeMillis();

		outConsole("{0} has been disabled ({1}ms)", getDescription().getFullName(), finish - start);
	}

	// Console logging
	public void outConsole(Level level, String string, Object... objects)
	{
		logHandler.log(level, FormatUtil.format(string, objects));
	}

	public void outConsole(String string, Object... objects)
	{
		logHandler.log(string, objects);
	}

	public void debug(String string, Object... objects)
	{
		logHandler.debug(string, objects);
	}

	public void broadcast(String string, Object... objects)
	{
		String broadcast = FormatUtil.format(string, objects);
		getServer().broadcastMessage(prefix + broadcast);

		debug("Broadcasted message: {0}", broadcast);
	}
	
	/** 
	 * Loads all files.
	 */
	public void loadFiles()
	{
		loadClasses();
		loadConfigs();
		loadArenas();
		loadSigns();
	}

	/**
	 * Basic reload method.
	 */
	public void reload()
	{
		loadedArenas.clear();
		configs.clear();
		classes.clear();
		
		reloadConfig();
		
		loadClasses();
		loadConfigs();
		loadArenas();
		
//		for (Arena a : Collections.unmodifiableList(activeArenas))
//   	{
//			a.reloadConfig();
//		}
	}

	/**
	 * Checks the {@link Bukkit} version.
	 * <p>
	 * UltimateArena is not compatible below 1.6.
	 * 
	 * @return Whether or not the Bukkit version is up-to-date
	 */
	public boolean checkDependencies()
	{
		PluginManager pm = getServer().getPluginManager();
		
		try 
		{
			Class.forName("org.bukkit.entity.Horse");
		}
		catch (ClassNotFoundException e)
		{
			outConsole(Level.WARNING, "UltimateArena has detected that you are using an outdated {0} build!", getServer().getName());
			outConsole(Level.WARNING, "Using older builds has been known to cause game-ending errors!");
			outConsole(Level.WARNING, "Consider updating to the latest build!");

			pm.disablePlugin(this);
			return false;
		}

		return true;
	}
	
	/**
	 * Sets up integration with WorldEdit
	 */
	public void setupWorldEditIntegration()
	{
		PluginManager pm = getServer().getPluginManager();
		
		if (pm.isPluginEnabled("WorldEdit"))
		{
			Plugin plugin = pm.getPlugin("WorldEdit");
			if (plugin instanceof WorldEditPlugin)
			{
				worldEdit = (WorldEditPlugin) plugin;
				
				outConsole("Integration with WorldEdit successful!");
				return;
			}
		}
		
		outConsole(Level.WARNING, "Could not hook into WorldEdit!");
	}
	
	/**
	 * Sets up integration with Essentials
	 */
	public void setupEssentialsIntegration()
	{
		PluginManager pm = getServer().getPluginManager();
		
		if (pm.isPluginEnabled("Essentials"))
		{
			Plugin plugin = pm.getPlugin("Essentials");
			essentials = (Essentials) plugin;
			useEssentials = true;
		}
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
		if (! file.exists())
		{
			generateWhitelistedCommands();
			
			debug("Generating Whitelisted Commands file!");
		}

		YamlConfiguration fc = YamlConfiguration.loadConfiguration(file);
		
		for (String cmd : fc.getStringList("whiteListedCmds"))
		{
			whitelistedCommands.add(cmd);
		}

		debug("Loaded {0} whitelisted commands!", fc.getStringList("whiteListedCmds").size());
	}

	public boolean loadConfig(String str)
	{
		File folder = new File(getDataFolder(), "configs");
		File file = new File(folder, str + "Config.yml");
		if (! file.exists())
		{
			generateArenaConfig(str);

			debug("Generating config for: {0}", str);
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
			generateStockClasses();
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
	
	/**
	 * Generates the WhiteListedCommands file
	 */
	public void generateWhitelistedCommands()
	{
		saveResource("whiteListedCommands.yml", false);
	}
	
	/**
	 * Generates an arena config for a particular field
	 * 
	 * @param field - Field to generate config for
	 */
	public void generateArenaConfig(String field)
	{
		saveResource("configs" + File.separator + field + "Config.yml", false);
	}
	
	/**
	 * Generates stock classes
	 */
	public void generateStockClasses()
	{
		String[] stockClasses = new String[] { "archer", "brute", "dumbass", "gunner", "healer", "shotgun", "sniper", "spleef" };
		
		for (String stockClass : stockClasses)
		{
			saveResource("classes" + File.separator + stockClass + ".yml", false);
		}
	}

	public void loadSigns()
	{
		signHandler = new SignHandler(this);
		outConsole("Loaded {0} arena signs!", arenaSigns.size());
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
		for (int i = 0; i < activeArenas.size(); i++)
		{
			Arena arena = activeArenas.get(i);
			if (arena != null)
			{
				arena.stop();
			}
		}

		activeArenas.clear();
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
			for (int i = 0; i < activeArenas.size(); i++)
			{
				Arena a = activeArenas.get(i);
				if (a.getName().equalsIgnoreCase(str))
				{
					a.stop();
				}
			}

			loadedArenas.remove(getArenaZone(str));

			file.delete();

			for (ArenaSign sign : arenaSigns)
			{
				if (sign.getName().equalsIgnoreCase(str))
					signHandler.deleteSign(sign);
			}

			player.sendMessage(prefix + FormatUtil.format("&3Successfully deleted arena: &e{0}", str));

			outConsole("Successfully deleted arena: {0}!", str);
		}
		else
		{
			player.sendMessage(prefix + FormatUtil.format("&cCould not find an arena by the name of \"{0}\"!", str));
		}
	}

	// Checks for whether or not something is in an arena
	public boolean isInArena(Location loc)
	{
		for (int i = 0; i < loadedArenas.size(); i++)
		{
			ArenaZone az = loadedArenas.get(i);
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
		for (int i = 0; i < loadedArenas.size(); i++)
		{
			ArenaZone az = loadedArenas.get(i);
			if (az.checkLocation(block.getLocation()))
				return getArena(az.getArenaName());
		}

		return null;
	}
	
	public Arena getArenaInside(Entity entity)
	{
		for (int i = 0; i < loadedArenas.size(); i++)
		{
			ArenaZone az = loadedArenas.get(i);
			if (az.checkLocation(entity.getLocation()))
				return getArena(az.getArenaName());
		}
		
		return null;
	}

	public ArenaPlayer getArenaPlayer(Player player)
	{
		for (int i = 0; i < activeArenas.size(); i++)
		{
			Arena a = activeArenas.get(i);

			ArenaPlayer ap = a.getArenaPlayer(player);
			if (ap != null)
				return ap;
		}

		return null;
	}

	public void join(Player player, String arena)
	{
		if (! permissionHandler.hasPermission(player, Permission.JOIN))
		{
			player.sendMessage(prefix + FormatUtil.format("&cYou do not have permission to do this!"));
			return;
		}

		if (isCreatingArena(player))
		{
			player.sendMessage(prefix + FormatUtil.format("&cYou are in the middle of making an arena!"));
			return;
		}

		if (! InventoryHelper.isEmpty(player.getInventory()))
		{
			if (!getConfig().getBoolean("saveInventories"))
			{
				player.sendMessage(prefix + FormatUtil.format("&cPlease clear your inventory!"));
				return;
			}
		}

		ArenaZone a = getArenaZone(arena);
		if (a == null)
		{
			player.sendMessage(prefix + FormatUtil.format("&3Unknown arena: &e{0}", arena));

			List<ArenaZone> matches = matchArena(arena);
			if (matches.size() > 0)
			{
				StringBuilder matchString = new StringBuilder();
				for (ArenaZone match : matches)
				{
					matchString.append("&e" + match.getArenaName() + "&3, ");
				}

				matchString.replace(matchString.lastIndexOf(","), matchString.lastIndexOf(" "), "?");

				player.sendMessage(prefix + FormatUtil.format("&3Did you mean: &e{0}", matchString.toString()));
			}

			return;
		}

		if (isInArena(player))
		{
			player.sendMessage(prefix + FormatUtil.format("&cYou are already in an arena!"));
			return;
		}

		ArenaPlayer ap = getArenaPlayer(player);
		if (ap != null)
		{
			player.sendMessage(prefix + FormatUtil.format("&cYou cannot leave and rejoin an arena!"));
			return;
		}
		
		if (isPlayerWaiting(player))
		{
			player.sendMessage(prefix + FormatUtil.format("&cYou are already waiting!"));
			return;
		}

		ArenaJoinTask join = new ArenaJoinTask(this, player, arena);
		if (getConfig().getBoolean("joinTimer.enabled"))
		{
			int seconds = getConfig().getInt("joinTimer.wait");
			int wait = seconds * 20;

			join.runTaskLater(this, wait);
			waiting.put(player, join);

			player.sendMessage(prefix + FormatUtil.format("&3Please stand still for &e{0} &3seconds!", seconds));
		}
		else
		{
			join.run();
		}
	}

	public void fight(Player player, String name)
	{
		boolean forced = permissionHandler.hasPermission(player, Permission.JOIN_FORCE);

		ArenaZone az = getArenaZone(name);
		Arena a = getArena(name);
		if (a != null)
		{
			if (a.isInLobby())
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
				// TODO: Allow joins?
				player.sendMessage(prefix + FormatUtil.format("&cThis arena has already started!"));
			}
		}
		else
		{
			Arena ar = null;
			boolean disabled = false;
			for (int i = 0; i < activeArenas.size(); i++)
			{
				Arena aar = activeArenas.get(i);
				if (aar.getName().equalsIgnoreCase(name))
				{
					disabled = aar.isDisabled();
				}
			}

			for (int ii = 0; ii < loadedArenas.size(); ii++)
			{
				ArenaZone aaz = loadedArenas.get(ii);
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
					activeArenas.add(ar);
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
			if (! permissionHandler.hasPermission(ap.getPlayer(), Permission.JOIN_FORCE))
			{
				validPlayers.add(ap);
			}
		}

		int rand = Util.random(validPlayers.size());
		ArenaPlayer apl = validPlayers.get(rand);
		if (apl != null)
		{
			apl.leaveArena(LeaveReason.KICK);
			return true;
		}

		return false;
	}

	// Gets the arena a player is in
	public Arena getArena(Player player)
	{
		for (int i = 0; i < activeArenas.size(); i++)
		{
			Arena a = activeArenas.get(i);
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
		for (int i = 0; i < activeArenas.size(); i++)
		{
			Arena ac = activeArenas.get(i);
			if (ac.getName().equalsIgnoreCase(name))
				return ac;
		}

		return null;
	}

	public List<ArenaZone> matchArena(String partial)
	{
		List<ArenaZone> ret = new ArrayList<ArenaZone>();

		for (int i = 0; i < loadedArenas.size(); i++)
		{
			ArenaZone az = loadedArenas.get(i);
			if (az.getArenaName().contains(partial))
				ret.add(az);
		}

		return ret;
	}

	// Gets an arena zone by its name
	public ArenaZone getArenaZone(String name)
	{
		for (int i = 0; i < loadedArenas.size(); i++)
		{
			ArenaZone az = loadedArenas.get(i);
			if (az.getArenaName().equalsIgnoreCase(name))
				return az;
		}

		return null;
	}
	
	//---- Arena Creation ----//
	
	/**
	 * Attempts to create a new {@link Arena}
	 * 
	 * @param player 
	 *            - {@link Player} who is creating the arena
	 * @param name 
	 *            - Name of the new arena
	 * @param type 
	 *            - Type of the new arena
	 */
	public void createArena(Player player, String name, String type)
	{
		if (isCreatingArena(player))
		{
			player.sendMessage(prefix + FormatUtil.format("&cYou are already creating an arena!"));
			return;
		}

		if (! FieldType.contains(type.toLowerCase()))
		{
			player.sendMessage(prefix + FormatUtil.format("&cThis is not a valid field type!"));
			return;
		}

		for (int i = 0; i < loadedArenas.size(); i++)
		{
			ArenaZone az = loadedArenas.get(i);
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
	
	/**
	 * Returns a player's {@link ArenaCreator} instance
	 * <p>
	 * Will return <code>null</code> if the player is not creating an arena.
	 * 
	 * @param player 
	 *            - {@link Player} to get {@link ArenaCreator} instance for.
	 * 
	 * @return The player's {@link ArenaCreator} instance
	 */
	public ArenaCreator getArenaCreator(Player player)
	{
		for (ArenaCreator ac : makingArena)
		{
			if (ac.getPlayer().equalsIgnoreCase(player.getName()))
				return ac;
		}

		return null;
	}
	
	/**
	 * Returns whether or not a {@link Player} is creating an arena.
	 * 
	 * @param player 
	 *            - {@link Player} to check
	 * @return Whether or not a {@link Player} is creating an arena.
	 */
	public boolean isCreatingArena(Player player)
	{
		return getArenaCreator(player) != null;
	}
	
	/**
	 * Sets a point in the arena creation process
	 * 
	 * @param player 
	 *            - {@link Player} setting the point
	 */
	public void setPoint(Player player)
	{
		if (! isCreatingArena(player))
		{
			player.sendMessage(prefix + FormatUtil.format("&cYou are not editing a field!"));
			return;
		}
		
		ArenaCreator ac = getArenaCreator(player);
		
		ac.setPoint(player);
		
		if (! ac.getMsg().isEmpty())
		{
			player.sendMessage(prefix + FormatUtil.format("&3" + ac.getMsg()));
		}
	}

	/**
	 * Finalizes a step in the arena creation process.
	 * 
	 * @param player 
	 *            - {@link Player} who is finalizing
	 */
	public void setDone(Player player)
	{
		if (! isCreatingArena(player))
		{
			player.sendMessage(prefix + FormatUtil.format("&cYou are not editing a field!"));
			return;
		}
		
		ArenaCreator ac = getArenaCreator(player);
		ac.setDone(player);
	}

	/**
	 * Stops the creation of an arena
	 * 
	 * @param player 
	 *            - {@link Player} who is stopping
	 */
	public void stopCreatingArena(Player player)
	{
		for (int i = 0; i < makingArena.size(); i++)
		{
			ArenaCreator ac = makingArena.get(i);
			if (ac.getPlayer().equalsIgnoreCase(player.getName()))
			{
				makingArena.remove(ac);
				player.sendMessage(prefix + FormatUtil.format("&3Stopped the creation of arena: &e{0}", ac.getArenaName()));
			}
		}
	}

	/**
	 * Clears memory
	 */
	public void clearMemory()
	{
		whitelistedCommands.clear();

		loadedArenas.clear();
		activeArenas.clear();
		makingArena.clear();
		arenaSigns.clear();
		waiting.clear();
		classes.clear();
		configs.clear();
	}
	
	/**
	 * Vault {@link Economy} integration
	 */
	public void setupVaultIntegration()
	{
		PluginManager pm = getServer().getPluginManager();
		if (pm.isPluginEnabled("Vault"))
		{
			RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
			if (economyProvider != null)
			{
				economy = economyProvider.getProvider();
				
				outConsole("Enabled economy through {0}!", economy.getName());
			}
			else
			{
				outConsole("Failed to hook into Vault economy.");
			}
		}
	}

	/**
	 * Accepts registration from a {@link JavaPlugin}
	 * 
	 * @param plugin 
	 *            - {@link JavaPlugin} to accept the registration from
	 */
	public void acceptRegistration(JavaPlugin plugin)
	{
		outConsole("Accepted API registration from {0}", plugin.getName());
		
		pluginsUsingAPI.add(plugin);
	}
	
	/**
	 * Dumps plugins currently using the UltimateArena API
	 */
	public void dumpRegistrations()
	{
		if (pluginsUsingAPI.isEmpty())
		{
			outConsole("No plugins currently using the UltimateArena API");
			return;
		}
		
		StringBuilder line = new StringBuilder();
		line.append("Plugins currently using the UltimateArena API: ");
		
		for (JavaPlugin plugin : pluginsUsingAPI)
		{
			line.append(plugin.getName() + ", ");
		}
		
		line.replace(line.lastIndexOf(","), line.lastIndexOf(" "), ".");
		
		outConsole(line.toString());
	}
	
	/**
	 * Returns whether or not a command is whitelisted
	 * 
	 * @param command 
	 *            - Command to check
	 * @return Whether or not a command is whitelisted
	 */
	public boolean isWhitelistedCommand(String command)
	{
		for (String cmd : whitelistedCommands)
		{
			if (cmd.matches(cmd.contains("/") ? "" : "/" + cmd + ".*"))
				return true;
		}

		return false;
	}
	
	public boolean isPlayerWaiting(Player player)
	{
		return waiting.containsKey(player);
	}
	
	/**
	 * Returns how many arenas have been played
	 * <p>
	 * Will return 1 if none have been played
	 * 
	 * @return How many arenas have been played
	 */
	public int getTotalArenasPlayed()
	{
		int ret = 0;
		
		for (ArenaZone az : loadedArenas)
		{
			ret += az.getTimesPlayed();
		}
		
		return ret > 0 ? ret : 1;
	}

	/**
	 * Arena Update Task
	 * <p>
	 * While I hate to use it, it works.
	 */
	public class ArenaUpdateTask extends BukkitRunnable
	{
		@Override
		public void run()
		{
			for (int i = 0; i < activeArenas.size(); i++)
			{
				Arena arena = activeArenas.get(i);
				arena.update();
			}
		}
	}
}