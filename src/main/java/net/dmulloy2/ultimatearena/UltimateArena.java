/**
 * UltimateArena - a bukkit plugin
 * Copyright (C) 2012 - 2014 MineSworn and Affiliates
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.dmulloy2.ultimatearena;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import lombok.Getter;
import net.dmulloy2.SwornAPI;
import net.dmulloy2.SwornPlugin;
import net.dmulloy2.commands.CmdHelp;
import net.dmulloy2.handlers.CommandHandler;
import net.dmulloy2.handlers.LogHandler;
import net.dmulloy2.handlers.PermissionHandler;
import net.dmulloy2.types.Reloadable;
import net.dmulloy2.types.SimpleVector;
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
import net.dmulloy2.ultimatearena.commands.CmdDone;
import net.dmulloy2.ultimatearena.commands.CmdEnable;
import net.dmulloy2.ultimatearena.commands.CmdForceStop;
import net.dmulloy2.ultimatearena.commands.CmdInfo;
import net.dmulloy2.ultimatearena.commands.CmdJoin;
import net.dmulloy2.ultimatearena.commands.CmdKick;
import net.dmulloy2.ultimatearena.commands.CmdLeave;
import net.dmulloy2.ultimatearena.commands.CmdLike;
import net.dmulloy2.ultimatearena.commands.CmdList;
import net.dmulloy2.ultimatearena.commands.CmdOption;
import net.dmulloy2.ultimatearena.commands.CmdPause;
import net.dmulloy2.ultimatearena.commands.CmdReload;
import net.dmulloy2.ultimatearena.commands.CmdSetPoint;
import net.dmulloy2.ultimatearena.commands.CmdSpectate;
import net.dmulloy2.ultimatearena.commands.CmdStart;
import net.dmulloy2.ultimatearena.commands.CmdStats;
import net.dmulloy2.ultimatearena.commands.CmdStop;
import net.dmulloy2.ultimatearena.commands.CmdUndo;
import net.dmulloy2.ultimatearena.commands.CmdVersion;
import net.dmulloy2.ultimatearena.creation.ArenaCreator;
import net.dmulloy2.ultimatearena.creation.BombCreator;
import net.dmulloy2.ultimatearena.creation.CTFCreator;
import net.dmulloy2.ultimatearena.creation.ConquestCreator;
import net.dmulloy2.ultimatearena.creation.FFACreator;
import net.dmulloy2.ultimatearena.creation.HungerCreator;
import net.dmulloy2.ultimatearena.creation.InfectCreator;
import net.dmulloy2.ultimatearena.creation.KOTHCreator;
import net.dmulloy2.ultimatearena.creation.MobCreator;
import net.dmulloy2.ultimatearena.creation.PvPCreator;
import net.dmulloy2.ultimatearena.creation.SpleefCreator;
import net.dmulloy2.ultimatearena.handlers.FileHandler;
import net.dmulloy2.ultimatearena.handlers.SignHandler;
import net.dmulloy2.ultimatearena.handlers.SpectatingHandler;
import net.dmulloy2.ultimatearena.integration.EssentialsHandler;
import net.dmulloy2.ultimatearena.integration.VaultHandler;
import net.dmulloy2.ultimatearena.integration.WorldEditHandler;
import net.dmulloy2.ultimatearena.listeners.BlockListener;
import net.dmulloy2.ultimatearena.listeners.EntityListener;
import net.dmulloy2.ultimatearena.listeners.PlayerListener;
import net.dmulloy2.ultimatearena.tasks.ArenaJoinTask;
import net.dmulloy2.ultimatearena.types.ArenaClass;
import net.dmulloy2.ultimatearena.types.ArenaConfig;
import net.dmulloy2.ultimatearena.types.ArenaLocation;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.ArenaSign;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.FieldType;
import net.dmulloy2.ultimatearena.types.LeaveReason;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.InventoryUtil;
import net.dmulloy2.util.TimeUtil;
import net.dmulloy2.util.Util;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author dmulloy2
 */

public class UltimateArena extends SwornPlugin implements Reloadable
{
	// Handlers
	private @Getter SpectatingHandler spectatingHandler;
	private @Getter FileHandler fileHandler;
	private @Getter SignHandler signHandler;

	// Integration
	private @Getter EssentialsHandler essentialsHandler;
	private @Getter WorldEditHandler worldEditHandler;
	private @Getter VaultHandler vaultHandler;

	// Lists and Maps
	private @Getter Map<String, ArenaJoinTask> waiting = new HashMap<String, ArenaJoinTask>();
	private @Getter List<ArenaCreator> makingArena = new ArrayList<ArenaCreator>();
	private @Getter List<ArenaConfig> configs = new ArrayList<ArenaConfig>();
	private @Getter List<ArenaClass> classes = new ArrayList<ArenaClass>();
	private @Getter List<ArenaZone> loadedArenas = new ArrayList<ArenaZone>();
	private @Getter List<String> whitelistedCommands = new ArrayList<String>();
	private @Getter List<String> pluginsUsingAPI = new ArrayList<String>();

	private List<Arena> activeArenas = new ArrayList<Arena>();

	private @Getter boolean stopping;

	// Global prefix
	private @Getter String prefix = FormatUtil.format("&6[&4&lUA&6] ");

	@Override
	public void onLoad()
	{
		SwornAPI.checkRegistrations();
		ConfigurationSerialization.registerClass(ArenaLocation.class);
	}

	@Override
	public void onEnable()
	{
		long start = System.currentTimeMillis();

		// Register LogHandler
		logHandler = new LogHandler(this);

		// Directories
		checkDirectories();

		// Configuration
		saveDefaultConfig();
		reloadConfig();

		// Register other handlers
		permissionHandler = new PermissionHandler(this);
		spectatingHandler = new SpectatingHandler(this);
		commandHandler = new CommandHandler(this);
		fileHandler = new FileHandler(this);

		// Integration
		essentialsHandler = new EssentialsHandler(this);
		worldEditHandler = new WorldEditHandler(this);
		vaultHandler = new VaultHandler(this);

		// Register Commands
		commandHandler.setCommandPrefix("ua");
		commandHandler.registerPrefixedCommand(new CmdClass(this));
		commandHandler.registerPrefixedCommand(new CmdClassList(this));
		commandHandler.registerPrefixedCommand(new CmdCreate(this));
		commandHandler.registerPrefixedCommand(new CmdDelete(this));
		commandHandler.registerPrefixedCommand(new CmdDisable(this));
		commandHandler.registerPrefixedCommand(new CmdDislike(this));
		commandHandler.registerPrefixedCommand(new CmdDone(this));
		commandHandler.registerPrefixedCommand(new CmdEnable(this));
		commandHandler.registerPrefixedCommand(new CmdForceStop(this));
		commandHandler.registerPrefixedCommand(new CmdHelp(this));
		commandHandler.registerPrefixedCommand(new CmdInfo(this));
		commandHandler.registerPrefixedCommand(new CmdJoin(this));
		commandHandler.registerPrefixedCommand(new CmdKick(this));
		commandHandler.registerPrefixedCommand(new CmdLeave(this));
		commandHandler.registerPrefixedCommand(new CmdLike(this));
		commandHandler.registerPrefixedCommand(new CmdList(this));
		commandHandler.registerPrefixedCommand(new CmdOption(this));
		commandHandler.registerPrefixedCommand(new CmdPause(this));
		commandHandler.registerPrefixedCommand(new CmdReload(this));
		commandHandler.registerPrefixedCommand(new CmdSetPoint(this));
		commandHandler.registerPrefixedCommand(new CmdSpectate(this));
		commandHandler.registerPrefixedCommand(new CmdStart(this));
		commandHandler.registerPrefixedCommand(new CmdStats(this));
		commandHandler.registerPrefixedCommand(new CmdStop(this));
		commandHandler.registerPrefixedCommand(new CmdUndo(this));
		commandHandler.registerPrefixedCommand(new CmdVersion(this));

		// Register Listeners
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new EntityListener(this), this);
		pm.registerEvents(new BlockListener(this), this);
		pm.registerEvents(new PlayerListener(this), this);

		// Register serializables
		ConfigurationSerialization.registerClass(ArenaLocation.class);
		ConfigurationSerialization.registerClass(SimpleVector.class);
		ConfigurationSerialization.registerClass(ArenaSign.class);

		// Load files
		loadFiles();

		// Arena Updater, runs every second
		new ArenaUpdateTask().runTaskTimer(this, TimeUtil.toTicks(1), TimeUtil.toTicks(1));

		outConsole("{0} has been enabled ({1}ms)", getDescription().getFullName(), System.currentTimeMillis() - start);
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
		signHandler.onDisable();

		// Refresh arena saves
		for (ArenaZone az : loadedArenas)
		{
			az.saveToDisk();
		}

		// Clear Memory
		clearMemory();

		outConsole("{0} has been disabled ({1}ms)", getDescription().getFullName(), System.currentTimeMillis() - start);
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
	@Override
	public void reload()
	{
		// Reload config
		reloadConfig();

		// Reload configs
		for (ArenaConfig conf : Util.newList(configs))
		{
			conf.reload();
		}

		// Reload ArenaZones
		for (ArenaZone az : Util.newList(loadedArenas))
		{
			az.reload();
		}

		// Reload active arenas
		for (Arena a : Util.newList(activeArenas))
		{
			a.reload();
		}

		// Load any new arenas
		loadArenas();

		// Reload classes
		for (ArenaClass ac : Util.newList(classes))
		{
			ac.reload();
		}

		// Load any new classes
		loadClasses();
	}

	// Create Directories
	private void checkDirectories()
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
	private void loadArenas()
	{
		File folder = new File(getDataFolder(), "arenas");
		File[] children = folder.listFiles(new FileFilter()
		{
			@Override
			public boolean accept(File file)
			{
				return file.getName().endsWith(".dat");
			}
		});

		int total = 0;
		for (File file : children)
		{
			boolean alreadyLoaded = false;
			for (ArenaZone loaded : loadedArenas)
			{
				if (loaded.getFile().equals(file))
					alreadyLoaded = true;
			}

			if (alreadyLoaded) continue;

			ArenaZone az = new ArenaZone(this, file);
			if (az.isLoaded())
			{
				debug("Successfully loaded arena {0}!", az.getName());
				total++;
			}
		}

		outConsole("Loaded {0} arenas!", total);
	}

	private void loadConfigs()
	{
		int total = 0;
		for (FieldType type : FieldType.values())
		{
			if (loadConfig(type.getName()))
				total++;
		}

		debug("Loaded {0} arena configs!", total);

		loadWhiteListedCommands();
	}

	private void loadWhiteListedCommands()
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
			if (! cmd.startsWith("/")) cmd = "/" + cmd;
			whitelistedCommands.add(cmd);
		}

		debug("Loaded {0} whitelisted commands!", fc.getStringList("whiteListedCmds").size());
	}

	private boolean loadConfig(String str)
	{
		File folder = new File(getDataFolder(), "configs");
		File file = new File(folder, str + "Config.yml");
		if (! file.exists())
		{
			debug("Generating config for: {0}", str);
			generateArenaConfig(str);
		}

		ArenaConfig a = new ArenaConfig(this, str, file);
		if (a.isLoaded())
		{
			configs.add(a);
			return true;
		}

		return false;
	}

	private Map<ArenaClass, Permission> classPermissions = new HashMap<>();

	private void loadClasses()
	{
		File folder = new File(getDataFolder(), "classes");
		File[] children = folder.listFiles(new FileFilter()
		{
			@Override
			public boolean accept(File file)
			{
				return file.getName().contains(".yml");
			}
		});

		if (children.length == 0)
		{
			generateStockClasses();
		}

		children = folder.listFiles();

		int total = 0;
		for (File file : children)
		{
			boolean alreadyLoaded = false;
			for (ArenaClass loaded : classes)
			{
				if (loaded.getFile().equals(file))
					alreadyLoaded = true;
			}

			if (alreadyLoaded) continue;

			ArenaClass ac = new ArenaClass(this, file);
			if (ac.isLoaded())
			{
				classes.add(ac);
				total++;

				if (ac.isNeedsPermission())
				{
					Permission perm = new Permission(ac.getPermissionNode(), PermissionDefault.OP);
					getServer().getPluginManager().addPermission(perm);
					classPermissions.put(ac, perm);
				}
			}
		}

		outConsole("Loaded {0} classes!", total);
	}

	/**
	 * Generates the WhiteListedCommands file
	 */
	private void generateWhitelistedCommands()
	{
		saveResource("whiteListedCommands.yml", false);
	}

	/**
	 * Generates an arena config for a particular field
	 *
	 * @param field
	 *        - Field to generate config for
	 */
	private void generateArenaConfig(String field)
	{
		saveResource("configs" + File.separator + field + "Config.yml", false);
	}

	/**
	 * Generates stock classes
	 */
	private void generateStockClasses()
	{
		String[] stockClasses = new String[]
		{
				"archer", "brute", "dumbass", "gunner", "healer", "shotgun", "sniper", "spleef"
		};

		for (String stockClass : stockClasses)
		{
			saveResource("classes" + File.separator + stockClass + ".yml", false);
		}
	}

	private void loadSigns()
	{
		signHandler = new SignHandler(this);
		outConsole("Loaded {0} signs!", signHandler.getSigns().size());
	}

	public ArenaConfig getConfig(FieldType type)
	{
		return getConfig(type.getName());
	}

	public ArenaConfig getConfig(String type)
	{
		for (ArenaConfig ac : Util.newList(configs))
		{
			if (ac.getType().equalsIgnoreCase(type))
				return ac;
		}

		return null;
	}

	public void stopAll()
	{
		for (Arena a : Util.newList(activeArenas))
		{
			a.stop();
		}

		activeArenas.clear();
	}

	public ArenaClass getArenaClass(String line)
	{
		for (ArenaClass ac : Util.newList(classes))
		{
			if (ac.getName().equalsIgnoreCase(line))
				return ac;
		}

		return null;
	}

	// Delete Stuff!
	public void deleteArena(Player player, String str)
	{
		ArenaZone az = getArenaZone(str);
		if (az != null)
		{
			// Delete the file
			File file = az.getFile();
			if (file.exists())
			{
				file.delete();
			}

			// Stop the active arena, if applicable
			for (Arena a : Util.newList(activeArenas))
			{
				if (a.getName().equalsIgnoreCase(str))
				{
					a.stop();
				}
			}

			// Delete any signs
			for (ArenaSign sign : signHandler.getSigns(az))
			{
				signHandler.deleteSign(sign);
			}

			// Remove it from the list
			loadedArenas.remove(az);

			player.sendMessage(prefix + FormatUtil.format("&3Successfully deleted arena: &e{0}", str));

			outConsole("Successfully deleted arena: {0}!", str);
		}
		else
		{
			player.sendMessage(prefix + FormatUtil.format("&cCould not find an arena by the name of \"{0}\"!", str));
		}
	}

	// ---- Locational Checks

	public ArenaZone getZoneInside(Location location)
	{
		for (ArenaZone az : Util.newList(loadedArenas))
		{
			if (az.checkLocation(location))
				return az;
		}

		return null;
	}

	public Arena getArenaInside(Location location)
	{
		ArenaZone az = getZoneInside(location);
		if (az != null)
			return getArena(az.getName());

		return null;
	}

	public boolean isInArena(Location loc)
	{
		return getZoneInside(loc) != null;
	}

	public boolean isInArena(ArenaLocation loc)
	{
		return isInArena(loc.getLocation());
	}

	// Special case for player
	public boolean isInArena(Player player)
	{
		return getArenaPlayer(player) != null;
	}

	public ArenaPlayer getArenaPlayer(Player player, boolean inactive)
	{
		for (Arena a : Util.newList(activeArenas))
		{
			ArenaPlayer ap = a.getArenaPlayer(player, inactive);
			if (ap != null)
				return ap;
		}

		return null;
	}

	public ArenaPlayer getArenaPlayer(Player player)
	{
		return getArenaPlayer(player, false);
	}

	public void attemptJoin(Player player, String arena)
	{
		if (! permissionHandler.hasPermission(player, net.dmulloy2.ultimatearena.types.Permission.JOIN))
		{
			player.sendMessage(prefix + FormatUtil.format("&cYou do not have permission to do this!"));
			return;
		}

		if (isCreatingArena(player))
		{
			player.sendMessage(prefix + FormatUtil.format("&cYou are in the middle of making an arena!"));
			return;
		}

		if (! InventoryUtil.isEmpty(player.getInventory()))
		{
			if (! getConfig().getBoolean("saveInventories"))
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
					matchString.append("&e" + match.getName() + "&3, ");
				}

				matchString.replace(matchString.lastIndexOf(","), matchString.lastIndexOf(" "), "?");

				if (matchString.lastIndexOf(",") >= 0)
				{
					matchString.replace(matchString.lastIndexOf(","), matchString.lastIndexOf(","), "or");
				}

				player.sendMessage(prefix + FormatUtil.format("&3Did you mean {0}", matchString.toString()));
			}

			return;
		}

		if (isInArena(player))
		{
			player.sendMessage(prefix + FormatUtil.format("&cYou are already in an arena!"));
			return;
		}

		ArenaPlayer ap = getArenaPlayer(player, true);
		if (ap != null)
		{
			Arena ar = ap.getArena();
			if (ar != null)
			{
				if (ar.getName().equalsIgnoreCase(arena))
				{
					if (ar.isInGame())
					{
						player.sendMessage(prefix + FormatUtil.format("&cYou cannot leave and rejoin this arena!"));
						return;
					}
				}
			}
		}

		if (isPlayerWaiting(player))
		{
			player.sendMessage(prefix + FormatUtil.format("&cYou are already waiting!"));
			return;
		}

		ArenaJoinTask join = new ArenaJoinTask(player.getName(), arena, this);
		if (getConfig().getBoolean("joinTimer.enabled"))
		{
			int seconds = getConfig().getInt("joinTimer.wait");
			int wait = seconds * 20;

			join.runTaskLater(this, wait);
			waiting.put(player.getName(), join);

			player.sendMessage(prefix + FormatUtil.format("&3Please stand still for &e{0} &3seconds!", seconds));
		}
		else
		{
			join.run();
		}
	}

	public void join(Player player, String name)
	{
		boolean forced = permissionHandler.hasPermission(player, net.dmulloy2.ultimatearena.types.Permission.JOIN_FORCE);

		ArenaZone az = getArenaZone(name);
		Arena a = getArena(name);
		if (a != null)
		{
			if (a.isStopped())
			{
				// Will only occur while the arena is stopping, so this message is valid
				player.sendMessage(prefix + FormatUtil.format("&cThis arena is currently stopping"));
				return;
			}

			if (a.isInLobby())
			{
				if (a.getPlayerCount() + 1 <= az.getMaxPlayers())
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
			if (az.isDisabled())
			{
				player.sendMessage(prefix + FormatUtil.format("&cThis arena is disabled!"));
				return;
			}

			Arena arena = null;

			switch (az.getType())
			{
				case BOMB:
					arena = new BOMBArena(az);
					break;
				case CONQUEST:
					arena = new CONQUESTArena(az);
					break;
				case CTF:
					arena = new CTFArena(az);
					break;
				case FFA:
					arena = new FFAArena(az);
					break;
				case HUNGER:
					arena = new HUNGERArena(az);
					break;
				case INFECT:
					arena = new INFECTArena(az);
					break;
				case KOTH:
					arena = new KOTHArena(az);
					break;
				case MOB:
					arena = new MOBArena(az);
					break;
				case PVP:
					arena = new PVPArena(az);
					break;
				case SPLEEF:
					arena = new SPLEEFArena(az);
					break;
			}

			// Won't ever be null, but just in case
			if (arena == null)
			{
				player.sendMessage(prefix + FormatUtil.format("&cCould not find a valid arena for the type: {0}", az.getType()));
				return;
			}

			activeArenas.add(arena);
			arena.addPlayer(player);
			arena.announce();
		}
	}

	// Kicks a random player if the arena is full
	// This will only be called if someone with forcejoin joins
	public boolean kickRandomPlayer(Arena arena)
	{
		List<ArenaPlayer> validPlayers = new ArrayList<ArenaPlayer>();
		List<ArenaPlayer> totalPlayers = arena.getActivePlayers();
		for (ArenaPlayer ap : totalPlayers)
		{
			if (! permissionHandler.hasPermission(ap.getPlayer(), net.dmulloy2.ultimatearena.types.Permission.JOIN_FORCE))
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
			if (az.getName().contains(partial))
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
			if (az.getName().equalsIgnoreCase(name))
				return az;
		}

		return null;
	}

	// ---- Arena Creation

	/**
	 * Attempts to create a new {@link Arena}
	 *
	 * @param player
	 *        - {@link Player} who is creating the arena
	 * @param name
	 *        - Name of the new arena
	 * @param type
	 *        - Type of the new arena
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
			player.sendMessage(prefix + FormatUtil.format("\"{0}\" is not a valid field type!", type));
			return;
		}

		FieldType fieldType = FieldType.getByName(type);

		for (ArenaZone az : Util.newList(loadedArenas))
		{
			if (az.getName().equalsIgnoreCase(name))
			{
				player.sendMessage(prefix + FormatUtil.format("&cAn arena by this name already exists!"));
				return;
			}
		}

		ArenaCreator ac = null;

		switch (fieldType)
		{
			case BOMB:
				ac = new BombCreator(player, name, this);
				break;
			case CONQUEST:
				ac = new ConquestCreator(player, name, this);
				break;
			case CTF:
				ac = new CTFCreator(player, name, this);
				break;
			case FFA:
				ac = new FFACreator(player, name, this);
				break;
			case HUNGER:
				ac = new HungerCreator(player, name, this);
				break;
			case INFECT:
				ac = new InfectCreator(player, name, this);
				break;
			case KOTH:
				ac = new KOTHCreator(player, name, this);
				break;
			case MOB:
				ac = new MobCreator(player, name, this);
				break;
			case PVP:
				ac = new PvPCreator(player, name, this);
				break;
			case SPLEEF:
				ac = new SpleefCreator(player, name, this);
				break;
		}

		// It won't ever be null, but just in case...
		if (ac == null)
		{
			player.sendMessage(prefix + FormatUtil.format("&cCould not find an applicable ArenaCreator for \"{0}\"", type));
			return;
		}

		outConsole("{0} has started the creation of Arena: {1}. Type: {2}", player.getName(), name, type);
		makingArena.add(ac);
	}

	/**
	 * Returns a player's {@link ArenaCreator} instance
	 * <p>
	 * Will return <code>null</code> if the player is not creating an arena.
	 *
	 * @param player
	 *        - {@link Player} to get {@link ArenaCreator} instance for.
	 *
	 * @return The player's {@link ArenaCreator} instance
	 */
	public ArenaCreator getArenaCreator(Player player)
	{
		for (ArenaCreator ac : Util.newList(makingArena))
		{
			if (ac.getPlayer().getName().equalsIgnoreCase(player.getName()))
				return ac;
		}

		return null;
	}

	/**
	 * Returns whether or not a {@link Player} is creating an arena.
	 *
	 * @param player
	 *        - {@link Player} to check
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
	 *        - {@link Player} setting the point
	 */
	public void setPoint(Player player, String[] args)
	{
		if (! isCreatingArena(player))
		{
			player.sendMessage(prefix + FormatUtil.format("&cYou are not creating an arena!"));
			return;
		}

		ArenaCreator ac = getArenaCreator(player);
		ac.setPoint(args);
	}

	/**
	 * Stops the creation of an arena
	 *
	 * @param player
	 *        - {@link Player} who is stopping
	 */
	public void stopCreatingArena(Player player)
	{
		ArenaCreator ac = getArenaCreator(player);
		if (ac.getPlayer().getName().equalsIgnoreCase(player.getName()))
		{
			makingArena.remove(ac);
			player.sendMessage(prefix + FormatUtil.format("&3Stopped the creation of arena: &e{0}", ac.getArenaName()));
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
		waiting.clear();
		classes.clear();
		configs.clear();
	}

	/**
	 * Accepts registration from a {@link JavaPlugin}
	 *
	 * @param plugin
	 *        - {@link JavaPlugin} to accept the registration from
	 */
	public void acceptRegistration(Plugin plugin)
	{
		outConsole("Accepted API registration from {0}", plugin.getName());

		pluginsUsingAPI.add(plugin.getName());
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

		for (String name : pluginsUsingAPI)
		{
			line.append(name + ", ");
		}

		line.replace(line.lastIndexOf(","), line.lastIndexOf(" "), "");

		outConsole(line.toString());
	}

	/**
	 * Returns whether or not a command is whitelisted
	 *
	 * @param command
	 *        - Command to check
	 * @return Whether or not a command is whitelisted
	 */
	public final boolean isWhitelistedCommand(String command)
	{
		for (String cmd : whitelistedCommands)
		{
			if (command.matches(cmd + ".*"))
				return true;
		}

		return false;
	}

	public final boolean isPlayerWaiting(Player player)
	{
		return waiting.containsKey(player.getName());
	}

	/**
	 * Returns how many arenas have been played
	 * <p>
	 * Will return 1 if none have been played
	 *
	 * @return How many arenas have been played
	 */
	public final int getTotalArenasPlayed()
	{
		int ret = 0;

		for (ArenaZone az : loadedArenas)
		{
			ret += az.getTimesPlayed();
		}

		return ret > 0 ? ret : 1;
	}

	/**
	 * Returns a list of active arenas
	 * <p>
	 * Should not be used to add or remove
	 */
	public final List<Arena> getActiveArenas()
	{
		return Util.newList(activeArenas);
	}

	/**
	 * Removes an active {@link Arena}
	 *
	 * @param a
	 *        - Arena to remove
	 */
	public final void removeActiveArena(Arena a)
	{
		activeArenas.remove(a);
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
			for (Arena arena : getActiveArenas())
			{
				try
				{
					arena.update();
				}
				catch (Throwable ex)
				{
					logHandler.log(Level.WARNING, Util.getUsefulStack(ex, "updating " + arena.getName()));
				}
			}
		}
	}
}