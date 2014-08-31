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
import net.dmulloy2.gui.GUIHandler;
import net.dmulloy2.handlers.CommandHandler;
import net.dmulloy2.handlers.LogHandler;
import net.dmulloy2.handlers.PermissionHandler;
import net.dmulloy2.handlers.ResourceHandler;
import net.dmulloy2.types.Reloadable;
import net.dmulloy2.types.SimpleVector;
import net.dmulloy2.types.StringJoiner;
import net.dmulloy2.ultimatearena.api.ArenaType;
import net.dmulloy2.ultimatearena.api.ArenaTypeHandler;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.commands.CmdClass;
import net.dmulloy2.ultimatearena.commands.CmdClassList;
import net.dmulloy2.ultimatearena.commands.CmdCreate;
import net.dmulloy2.ultimatearena.commands.CmdDelete;
import net.dmulloy2.ultimatearena.commands.CmdDisable;
import net.dmulloy2.ultimatearena.commands.CmdDislike;
import net.dmulloy2.ultimatearena.commands.CmdDone;
import net.dmulloy2.ultimatearena.commands.CmdEnable;
import net.dmulloy2.ultimatearena.commands.CmdForceJoin;
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
import net.dmulloy2.ultimatearena.commands.CmdTeleport;
import net.dmulloy2.ultimatearena.commands.CmdUndo;
import net.dmulloy2.ultimatearena.commands.CmdVersion;
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
import net.dmulloy2.ultimatearena.types.ArenaCreator;
import net.dmulloy2.ultimatearena.types.ArenaLocation;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.ArenaSign;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.LeaveReason;
import net.dmulloy2.ultimatearena.types.Permission;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.InventoryUtil;
import net.dmulloy2.util.TimeUtil;
import net.dmulloy2.util.Util;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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
	private @Getter ArenaTypeHandler arenaTypeHandler;
	private @Getter ResourceHandler resourceHandler;
	private @Getter FileHandler fileHandler;
	private @Getter SignHandler signHandler;

	// Integration
	private @Getter EssentialsHandler essentialsHandler;
	private @Getter WorldEditHandler worldEditHandler;
	private @Getter VaultHandler vaultHandler;

	// Public lists and maps
	private @Getter Map<String, ArenaJoinTask> waiting = new HashMap<>();
	private @Getter List<ArenaCreator> makingArena = new ArrayList<>();
	private @Getter List<String> pluginsUsingAPI = new ArrayList<>();
	private @Getter List<ArenaZone> loadedArenas = new ArrayList<>();
	private @Getter List<ArenaConfig> configs = new ArrayList<>();
	private @Getter List<ArenaClass> classes = new ArrayList<>();

	// Private lists
	private List<String> whitelistedCommands;
	private List<Arena> activeArenas = new ArrayList<>();

	private @Getter boolean stopping;

	// Global prefix
	private @Getter String prefix = FormatUtil.format("&6[&4&lUA&6] ");

	@Override
	public void onLoad()
	{
		// Serializables
		SwornAPI.checkRegistrations();
		ConfigurationSerialization.registerClass(ArenaLocation.class);
		ConfigurationSerialization.registerClass(SimpleVector.class);
		ConfigurationSerialization.registerClass(ArenaSign.class);
	}

	@Override
	public void onEnable()
	{
		long start = System.currentTimeMillis();

		// Register LogHandler
		logHandler = new LogHandler(this);

		// I/O
		checkFiles();

		// Configuration
		saveDefaultConfig();
		reloadConfig();

		// Register generic handlers
		permissionHandler = new PermissionHandler(this);
		// resourceHandler = new ResourceHandler(this);
		commandHandler = new CommandHandler(this);

		// Register UA handlers
		spectatingHandler = new SpectatingHandler(this);
		arenaTypeHandler = new ArenaTypeHandler(this);
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
		commandHandler.registerPrefixedCommand(new CmdForceJoin(this));
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
		commandHandler.registerPrefixedCommand(new CmdTeleport(this));
		commandHandler.registerPrefixedCommand(new CmdUndo(this));
		commandHandler.registerPrefixedCommand(new CmdVersion(this));

		// Register Listeners
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new EntityListener(this), this);
		pm.registerEvents(new BlockListener(this), this);
		pm.registerEvents(new PlayerListener(this), this);

		GUIHandler.registerEvents(this);

		// Load files
		loadFiles();

		// Arena Updater, runs every second
		new ArenaUpdateTask().runTaskTimer(this, TimeUtil.toTicks(1), TimeUtil.toTicks(1));

		logHandler.log("{0} has been enabled ({1} ms)", getDescription().getFullName(), System.currentTimeMillis() - start);
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

		logHandler.log("{0} has been disabled ({1}ms)", getDescription().getFullName(), System.currentTimeMillis() - start);
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

	// Messaging
	public final String getMessage(String key)
	{
		// return resourceHandler.getMessage(key);
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void broadcast(String string, Object... objects)
	{
		if (getConfig().getBoolean("globalMessages", true))
		{
			String broadcast = FormatUtil.format(string, objects);
			getServer().broadcastMessage(prefix + broadcast);

			debug("Broadcasted message: {0}", broadcast);
		}
	}

	// Loading
	private final void loadFiles()
	{
		loadClasses();
//		loadConfigs();
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

		arenaTypeHandler.reload();

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

	private final void checkFiles()
	{
		File dataFolder = getDataFolder();
		if (! dataFolder.exists())
		{
			dataFolder.mkdirs();
		}

		File arenaFile = new File(dataFolder, "arenas");
		if (! arenaFile.exists())
		{
			arenaFile.mkdir();
		}

		File classFile = new File(dataFolder, "classes");
		if (! classFile.exists())
		{
			classFile.mkdir();
		}

		File configsFile = new File(dataFolder, "configs");
		if (configsFile.exists())
		{
			if (configsFile.listFiles().length == 0)
				configsFile.mkdir();
		}

		File typesFile = new File(dataFolder, "types");
		if (! typesFile.exists())
		{
			typesFile.mkdir();
		}

		File whitelistedCommands = new File(dataFolder, "whiteListedCommands.yml");
		if (whitelistedCommands.exists())
		{
			whitelistedCommands.delete();
		}
	}

	// Load Stuff
	private final void loadArenas()
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

		for (File file : children)
		{
			loadArena(file);
		}

		logHandler.log("Loaded {0} arenas!", loadedArenas.size());
	}

	private final void loadArena(File file)
	{
		try
		{
			for (ArenaZone loaded : loadedArenas)
			{
				if (loaded.getFile().equals(file))
					return;
			}

			FileConfiguration fc = YamlConfiguration.loadConfiguration(file);
			String typeString = fc.getString("typeString");
			ArenaType type = arenaTypeHandler.getArenaType(typeString);
			if (type == null)
			{
				logHandler.log(Level.WARNING, "Failed to find ArenaType \"{0}\" for arena {1}", typeString, file.getName());
				return;
			}

			ArenaZone az = type.getArenaZone(file);
			if (az.isLoaded())
				debug("Successfully loaded arena {0}!", az.getName());
		}
		catch (Throwable ex)
		{
			logHandler.log(Level.WARNING, Util.getUsefulStack(ex, "loading arena " + file.getName()));
		}
	}

//	private final void loadConfigs()
//	{
//		int total = 0;
//		for (FieldType type : FieldType.values())
//		{
//			if (loadConfig(type.getName()))
//				total++;
//		}
//
//		debug("Loaded {0} arena configs!", total);
//	}
//
//	private boolean loadConfig(String str)
//	{
//		File folder = new File(getDataFolder(), "configs");
//		File file = new File(folder, str + "Config.yml");
//		if (! file.exists())
//		{
//			debug("Generating config for: {0}", str);
//			generateArenaConfig(str);
//		}
//
//		ArenaConfig a = new ArenaConfig(this, str, file);
//		if (a.isLoaded())
//		{
//			configs.add(a);
//			return true;
//		}
//
//		return false;
//	}

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

			if (alreadyLoaded)
				continue;

			ArenaClass ac = new ArenaClass(this, file);
			if (ac.isLoaded())
			{
				classes.add(ac);
				total++;

				if (ac.isNeedsPermission())
				{
					// Fully qualified names, conflicts with UA permissions
					PluginManager pm = getServer().getPluginManager();
					org.bukkit.permissions.Permission perm = pm.getPermission(ac.getPermissionNode());
					if (perm == null)
					{
						perm = new org.bukkit.permissions.Permission(ac.getPermissionNode(), PermissionDefault.OP);
						pm.addPermission(perm);
					}
				}
			}
		}

		outConsole("Loaded {0} classes!", total);
	}

//	private final void generateArenaConfig(String field)
//	{
//		saveResource("configs" + File.separator + field + "Config.yml", false);
//	}

	private final void generateStockClasses()
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

	private final void loadSigns()
	{
		signHandler = new SignHandler(this);
		outConsole("Loaded {0} signs!", signHandler.getSigns().size());
	}

	// TODO: JavaDoc need - start

	public final ArenaConfig getConfig(String type)
	{
		for (ArenaConfig ac : configs)
		{
			if (ac.getType().equalsIgnoreCase(type))
				return ac;
		}

		return null;
	}

	public final void stopAll()
	{
		for (Arena a : getActiveArenas())
		{
			a.stop();
		}

		activeArenas.clear();
	}

	public final ArenaClass getArenaClass(String name)
	{
		for (ArenaClass ac : classes)
		{
			if (ac.getName().equalsIgnoreCase(name))
				return ac;
		}

		return null;
	}

	public final ArenaClass getArenaClass(ItemStack icon)
	{
		for (ArenaClass ac : classes)
		{
			if (ac.getIcon().equals(icon))
				return ac;
		}

		return null;
	}

	public final void deleteArena(Player player, String str)
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

	public final ArenaZone getZoneInside(Location location)
	{
		for (ArenaZone az : loadedArenas)
		{
			if (az.isInside(location))
				return az;
		}

		return null;
	}

	public final Arena getArenaInside(Location location)
	{
		for (Arena arena : getActiveArenas())
		{
			if (arena.isInside(location))
				return arena;
		}

		return null;
	}

	public final boolean isInArena(Location loc)
	{
		return getZoneInside(loc) != null;
	}

	public final boolean isInArena(ArenaLocation loc)
	{
		return isInArena(loc.getLocation());
	}

	// Special case for player
	public final boolean isInArena(Player player)
	{
		return getArenaPlayer(player) != null;
	}

	public final ArenaPlayer getArenaPlayer(Player player)
	{
		return getArenaPlayer(player, false);
	}

	public final ArenaPlayer getArenaPlayer(Player player, boolean inactive)
	{
		for (Arena arena : getActiveArenas())
		{
			ArenaPlayer ap = arena.getArenaPlayer(player, inactive);
			if (ap != null)
				return ap;
		}

		return null;
	}

	public final void attemptJoin(Player player, String name)
	{
		attemptJoin(player, name, -1);
	}

	public final void attemptJoin(Player player, String name, int team)
	{
		if (waiting.containsKey(player.getName()))
		{
			player.sendMessage(prefix + FormatUtil.format("&cYou are already waiting!"));
			return;
		}

		if (isInArena(player))
		{
			player.sendMessage(prefix + FormatUtil.format("&cYou are already in an arena!"));
			return;
		}

		if (isCreatingArena(player))
		{
			player.sendMessage(prefix + FormatUtil.format("&cYou are in the middle of making an arena!"));
			return;
		}

		if (! getConfig().getBoolean("saveInventories", true))
		{
			if (! InventoryUtil.isEmpty(player.getInventory()))
			{
				player.sendMessage(prefix + FormatUtil.format("&cPlease clear your inventory!"));
				return;
			}
		}

		ArenaZone az = getArenaZone(name);
		if (az == null)
		{
			player.sendMessage(prefix + FormatUtil.format("&3Unknown arena: &e{0}", name));

			List<ArenaZone> matches = matchArena(name);
			if (matches.size() > 0)
			{
				StringJoiner joiner = new StringJoiner("&3, &e");
				for (ArenaZone match : matches)
				{
					joiner.append(match.getName());
				}

				player.sendMessage(prefix + FormatUtil.format("&3Did you mean: &e{0}&3?", joiner.toString()));
			}

			return;
		}

		ArenaPlayer ap = getArenaPlayer(player, true);
		if (ap != null)
		{
			Arena arena = ap.getArena();
			if (arena != null)
			{
				if (arena.getName().equalsIgnoreCase(name))
				{
					if (arena.isInGame())
					{
						player.sendMessage(prefix + FormatUtil.format("&cYou cannot leave and rejoin this arena!"));
						return;
					}
				}
			}
		}

		ArenaJoinTask join = new ArenaJoinTask(player.getName(), name, this, team);
		if (getConfig().getBoolean("joinTimer.enabled", true))
		{
			int seconds = getConfig().getInt("joinTimer.wait", 3);
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

	public final void addPlayer(Player player, String name, int team)
	{
		try
		{
			ArenaZone az = getArenaZone(name);
			if (az.isDisabled())
			{
				player.sendMessage(prefix + FormatUtil.format("&cThis arena is disabled!"));
				return;
			}

			Arena active = getArena(name);
			if (active != null)
			{
				if (active.isStopped())
				{
					player.sendMessage(prefix + FormatUtil.format("&cThis arena is currently stopping"));
					return;
				}

				if (active.isInGame())
				{
					player.sendMessage(prefix + FormatUtil.format("&cThis arena has already started!"));
					return;
				}

				if (active.getPlayerCount() < az.getMaxPlayers())
				{
					active.addPlayer(player, team);
					return;
				}

				if (! permissionHandler.hasPermission(player, Permission.JOIN_FULL))
				{
					player.sendMessage(prefix + FormatUtil.format("&cThis arena is full!"));
					return;
				}

				if (kickRandomPlayer(active))
				{
					active.addPlayer(player, team);
					return;
				}

				player.sendMessage(prefix + FormatUtil.format("&cCould not force join the arena!"));
				return;
			}

			ArenaType type = az.getType();
			if (type == null)
			{
				player.sendMessage(prefix + FormatUtil.format("&cCould not find a valid type for arena: {0}", az.getName()));
				return;
			}

			Arena arena = type.newArena(az);
			if (arena == null)
			{
				player.sendMessage(prefix + FormatUtil.format("&c{0} does not define a valid arena!", type.getName()));
				return;
			}

			activeArenas.add(arena);
			arena.addPlayer(player, team);
			arena.announce();
		}
		catch (Throwable ex)
		{
			logHandler.log(Level.SEVERE, Util.getUsefulStack(ex, "adding " + player.getName() + " to arena " + name));
			player.sendMessage(prefix + FormatUtil.format("&cFailed to add you to the arena! Contact an Administrator!"));
		}
	}

	// Kicks a random player if the arena is full
	private final boolean kickRandomPlayer(Arena arena)
	{
		List<ArenaPlayer> validPlayers = new ArrayList<>();
		List<ArenaPlayer> totalPlayers = arena.getActivePlayers();
		for (ArenaPlayer ap : totalPlayers)
		{
			if (! permissionHandler.hasPermission(ap.getPlayer(), net.dmulloy2.ultimatearena.types.Permission.JOIN_FULL))
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

	// Gets the arena by an active player
	public final Arena getArena(Player player)
	{
		for (Arena arena : getActiveArenas())
		{
			ArenaPlayer ap = arena.getArenaPlayer(player);
			if (ap != null)
			{
				if (ap.getUniqueId().equals(player.getUniqueId()))
					return arena;
			}
		}

		return null;
	}

	// Gets an arena by its name
	public final Arena getArena(String name)
	{
		for (Arena arena : getActiveArenas())
		{
			if (arena.getName().equalsIgnoreCase(name))
				return arena;
		}

		return null;
	}

	private final List<ArenaZone> matchArena(String partial)
	{
		List<ArenaZone> ret = new ArrayList<>();

		for (ArenaZone az : loadedArenas)
		{
			if (az.getName().contains(partial))
				ret.add(az);
		}

		return ret;
	}

	// Gets an arena zone by its name
	public final ArenaZone getArenaZone(String name)
	{
		for (ArenaZone az : loadedArenas)
		{
			if (az.getName().equalsIgnoreCase(name))
				return az;
		}

		return null;
	}

	// TODO: JavaDoc need - end

	// ---- Arena Creation

	/**
	 * Attempts to create a new {@link Arena}
	 *
	 * @param player {@link Player} who is creating the arena
	 * @param name Name of the new arena
	 * @param type Type of the new arena
	 */
	public final void createArena(Player player, String name, String type)
	{
		if (isCreatingArena(player))
		{
			player.sendMessage(prefix + FormatUtil.format("&cYou are already creating an arena!"));
			return;
		}

		for (ArenaZone az : loadedArenas)
		{
			if (az.getName().equalsIgnoreCase(name))
			{
				player.sendMessage(prefix + FormatUtil.format("&cAn arena by this name already exists!"));
				return;
			}
		}

		ArenaType at = arenaTypeHandler.getArenaType(type);
		if (at == null)
		{
			player.sendMessage(prefix + FormatUtil.format("&cArena Type {0} does not exist!", type));
			return;
		}

		ArenaCreator ac = at.newCreator(player, name);
		if (ac == null)
		{
			player.sendMessage(prefix + FormatUtil.format("&cCould not find an applicable ArenaCreator for \"{0}\"", type));
			return;
		}

		logHandler.log("{0} has started the creation of Arena: {1}. Type: {2}", player.getName(), name, type);
		makingArena.add(ac);
	}

	/**
	 * Gets a player's {@link ArenaCreator} instance.
	 *
	 * @param player {@link Player} to get {@link ArenaCreator} instance for.
	 * @return The player's {@link ArenaCreator} instance, or null if none
	 */
	public final ArenaCreator getArenaCreator(Player player)
	{
		for (ArenaCreator ac : makingArena)
		{
			if (ac.getPlayer().getUniqueId().equals(player.getUniqueId()))
				return ac;
		}

		return null;
	}

	/**
	 * Whether or not a {@link Player} is creating an arena.
	 * <p>
	 * If you are going to use their ArenaCreator instance for other purposes,
	 * get their instance, then use a null check.
	 *
	 * @param player {@link Player} to check
	 * @return True if they are creating an arena, false if not.
	 */
	public final boolean isCreatingArena(Player player)
	{
		return getArenaCreator(player) != null;
	}

	/**
	 * Sets a point in the arena creation process
	 *
	 * @param player {@link Player} setting the point
	 * @param args Command-line args, if any
	 */
	public final void setPoint(Player player, String[] args)
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
	 * Stops the creation of an arena.
	 *
	 * @param player {@link Player} who is stopping
	 */
	public void stopCreatingArena(Player player)
	{
		ArenaCreator ac = getArenaCreator(player);
		if (ac != null)
		{
			makingArena.remove(ac);
			player.sendMessage(prefix + FormatUtil.format("&3Stopped the creation of arena: &e{0}", ac.getArenaName()));
		}
	}

	/**
	 * Clears lists and maps.
	 */
	public final void clearMemory()
	{
		whitelistedCommands = null;

		loadedArenas.clear();
		activeArenas.clear();
		makingArena.clear();
		waiting.clear();
		classes.clear();
		configs.clear();
	}

	/**
	 * Accepts registration from a {@link JavaPlugin}.
	 *
	 * @param plugin {@link Plugin} to accept the registration from
	 */
	public void acceptRegistration(Plugin plugin)
	{
		logHandler.log("Accepted API registration from {0}", plugin.getName());
		pluginsUsingAPI.add(plugin.getName());
	}

	/**
	 * Dumps plugins currently using the UltimateArena API to console.
	 *
	 * @return Plugins using the API, or null if none
	 */
	public final List<String> dumpRegistrations()
	{
		if (pluginsUsingAPI.isEmpty())
		{
			logHandler.log("No plugins currently using the UltimateArena API");
			return null;
		}

		StringJoiner joiner = new StringJoiner(", ");
		joiner.appendAll(pluginsUsingAPI);

		logHandler.log("Plugins using the API: {0}", joiner.toString());
		return pluginsUsingAPI;
	}

	/**
	 * Whether or not a command is whitelisted.
	 *
	 * @param command Command to check
	 * @return True if the command is whitelisted, false if not
	 * @throws IllegalException if command is null
	 */
	public final boolean isWhitelistedCommand(String command)
	{
		if (! getConfig().getBoolean("restrictCommands", true))
			return true;

		Validate.notNull(command, "command cannot be null!");

		// Lazy-load whitelistedCommands
		if (whitelistedCommands == null)
		{
			whitelistedCommands = new ArrayList<>();
			for (String whitelisted : getConfig().getStringList("whitelistedCommands"))
			{
				// Normalize whitelisted
				if (! whitelisted.startsWith("/"))
					whitelisted = "/" + whitelisted;
				whitelistedCommands.add(whitelisted.toLowerCase());
			}
		}

		// Normalize command
		command = command.toLowerCase();
		if (! command.startsWith("/"))
			command = "/" + command;

		// Iterate to find a match
		for (String cmd : whitelistedCommands)
		{
			if (command.startsWith(cmd))
				return true;
		}

		return false;
	}

	/**
	 * Gets whether or not a {@link Player} is waiting to join an arena.
	 *
	 * @param player Player to check
	 * @return True if they are waiting, false if not
	 */
	public final boolean isPlayerWaiting(Player player)
	{
		return waiting.containsKey(player.getUniqueId());
	}

	/**
	 * Gets how many arenas have been played.
	 *
	 * @return How many arenas have been played, or 1 if none
	 */
	public final int getTotalArenasPlayed()
	{
		int ret = 0;

		for (ArenaZone az : loadedArenas)
		{
			ret += az.getTimesPlayed();
		}

		return Math.max(1, ret);
	}

	/**
	 * Gets a list of active arenas.
	 * <p>
	 * Can not be used for modification.
	 */
	public final List<Arena> getActiveArenas()
	{
		return Util.newList(activeArenas);
	}

	/**
	 * Removes an active {@link Arena}.
	 *
	 * @param arena Arena to remove
	 */
	public final void removeActiveArena(Arena arena)
	{
		Validate.isTrue(arena.isStopped(), "You cannot remove an arena that hasn't stopped yet!");
		activeArenas.remove(arena);
	}

	/**
	 * Arena Update Task
	 * <p>
	 * While I hate to use it, it works well enough.
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
					logHandler.log(Level.SEVERE, Util.getUsefulStack(ex, "updating " + arena.getName()));
				}
			}
		}
	}
}