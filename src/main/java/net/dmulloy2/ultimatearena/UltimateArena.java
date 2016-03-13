/**
 * UltimateArena - fully customizable PvP arenas
 * Copyright (C) 2012 - 2015 MineSworn
 * Copyright (C) 2013 - 2015 dmulloy2
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.dmulloy2.ultimatearena;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
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
import net.dmulloy2.integration.VaultHandler;
import net.dmulloy2.types.SimpleVector;
import net.dmulloy2.types.StringJoiner;
import net.dmulloy2.ultimatearena.api.ArenaType;
import net.dmulloy2.ultimatearena.api.ArenaTypeHandler;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.commands.CmdAbandon;
import net.dmulloy2.ultimatearena.commands.CmdClass;
import net.dmulloy2.ultimatearena.commands.CmdClassList;
import net.dmulloy2.ultimatearena.commands.CmdCreate;
import net.dmulloy2.ultimatearena.commands.CmdDelete;
import net.dmulloy2.ultimatearena.commands.CmdDisable;
import net.dmulloy2.ultimatearena.commands.CmdDislike;
import net.dmulloy2.ultimatearena.commands.CmdEnable;
import net.dmulloy2.ultimatearena.commands.CmdForceJoin;
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
import net.dmulloy2.ultimatearena.handlers.MessageHandler;
import net.dmulloy2.ultimatearena.handlers.SpectatingHandler;
import net.dmulloy2.ultimatearena.integration.EssentialsHandler;
import net.dmulloy2.ultimatearena.integration.ProtocolHandler;
import net.dmulloy2.ultimatearena.integration.WorldEditHandler;
import net.dmulloy2.ultimatearena.listeners.BlockListener;
import net.dmulloy2.ultimatearena.listeners.EntityListener;
import net.dmulloy2.ultimatearena.listeners.PlayerListener;
import net.dmulloy2.ultimatearena.signs.ArenaSign;
import net.dmulloy2.ultimatearena.signs.SignHandler;
import net.dmulloy2.ultimatearena.tasks.ArenaJoinTask;
import net.dmulloy2.ultimatearena.types.ArenaClass;
import net.dmulloy2.ultimatearena.types.ArenaCreator;
import net.dmulloy2.ultimatearena.types.ArenaLocation;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.LeaveReason;
import net.dmulloy2.ultimatearena.types.Permission;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.InventoryUtil;
import net.dmulloy2.util.TimeUtil;
import net.dmulloy2.util.Util;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * UltimateArena main class
 * @author dmulloy2
 */

public class UltimateArena extends SwornPlugin
{
	private @Getter SpectatingHandler spectatingHandler;
	private @Getter ArenaTypeHandler arenaTypeHandler;
	private @Getter ResourceHandler resourceHandler;
	private @Getter MessageHandler messageHandler;
	private @Getter FileHandler fileHandler;
	private @Getter SignHandler signHandler;
	private @Getter GUIHandler guiHandler;

	// Integration
	private @Getter EssentialsHandler essentialsHandler;
	private @Getter WorldEditHandler worldEditHandler;
	private @Getter ProtocolHandler protocolHandler;
	private @Getter VaultHandler vaultHandler;

	private @Getter Map<String, ArenaJoinTask> waiting = new HashMap<>();
	private @Getter List<ArenaCreator> makingArena = new ArrayList<>();
	private @Getter List<String> pluginsUsingAPI = new ArrayList<>();
	private @Getter List<ArenaZone> loadedArenas = new ArrayList<>();
	private @Getter List<ArenaClass> classes = new ArrayList<>();

	private List<String> whitelistedCommands;
	private List<Arena> activeArenas = new ArrayList<>();

	private MetadataValue uaIdentifier;
	private String prefix;

	private @Getter boolean stopping;

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

		// Create identifier
		uaIdentifier = new FixedMetadataValue(this, true);

		// Register LogHandler
		logHandler = new LogHandler(this);

		checkVersion();

		// I/O
		checkFiles();

		// Configuration
		saveDefaultConfig();
		reloadConfig();

		Config.load(this);

		messageHandler = new MessageHandler(this);
		prefix = FormatUtil.format(messageHandler.getMessage("prefix"));

		// Register generic handlers
		permissionHandler = new PermissionHandler(this);
		commandHandler = new CommandHandler(this);
		guiHandler = new GUIHandler(this);

		spectatingHandler = new SpectatingHandler(this);
		fileHandler = new FileHandler(this);

		// Order is important here
		loadClasses();
		arenaTypeHandler = new ArenaTypeHandler(this);
		loadArenas();
		loadSigns();

		// Integration
		setupIntegration();

		// Register Commands
		commandHandler.setCommandPrefix("ua");
		commandHandler.registerPrefixedCommand(new CmdAbandon(this));
		commandHandler.registerPrefixedCommand(new CmdClass(this));
		commandHandler.registerPrefixedCommand(new CmdClassList(this));
		commandHandler.registerPrefixedCommand(new CmdCreate(this));
		commandHandler.registerPrefixedCommand(new CmdDelete(this));
		commandHandler.registerPrefixedCommand(new CmdDisable(this));
		commandHandler.registerPrefixedCommand(new CmdDislike(this));
		commandHandler.registerPrefixedCommand(new CmdEnable(this));
		commandHandler.registerPrefixedCommand(new CmdForceJoin(this));
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

		// Arena Updater, runs every second
		new ArenaUpdateTask().runTaskTimer(this, TimeUtil.toTicks(1), TimeUtil.toTicks(1));

		logHandler.log("{0} has been enabled. Took {1} ms.", getDescription().getFullName(), System.currentTimeMillis() - start);
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

		// Disable types
		arenaTypeHandler.disable();

		// Save Signs
		signHandler.onDisable();

		// Refresh arena saves
		for (ArenaZone az : loadedArenas)
		{
			az.saveToDisk();
		}

		// Clear Memory
		clearMemory();

		logHandler.log("{0} has been disabled. Took {1} ms.", getDescription().getFullName(), System.currentTimeMillis() - start);
	}

	// Console logging
	public void log(Level level, String string, Object... objects)
	{
		logHandler.log(level, FormatUtil.format(string, objects));
	}

	public void log(String string, Object... objects)
	{
		logHandler.log(string, objects);
	}

	public void debug(String string, Object... objects)
	{
		logHandler.debug(string, objects);
	}

	public final String getMessage(String key)
	{
		return messageHandler.getMessage(key);
	}

	public void broadcast(String string, Object... objects)
	{
		Validate.notNull(string, "string cannot be null!");

		if (Config.globalMessages && ! string.isEmpty())
		{
			String broadcast = FormatUtil.format(string, objects);
			getServer().broadcastMessage(prefix + broadcast);
		}
	}

	private void setupIntegration()
	{
		try
		{
			essentialsHandler = new EssentialsHandler(this);
		} catch (Throwable ex) { }

		try
		{
			protocolHandler = new ProtocolHandler(this);
		} catch (Throwable ex) { }

		try
		{
			vaultHandler = new VaultHandler(this);
		} catch (Throwable ex) { }

		try
		{
			worldEditHandler = new WorldEditHandler(this);
		} catch (Throwable ex) { }
	}

	public final boolean isEssentialsEnabled()
	{
		return essentialsHandler != null && essentialsHandler.isEnabled();
	}

	public final boolean isProtocolEnabled()
	{
		return protocolHandler != null && protocolHandler.isEnabled();
	}

	public final boolean isVaultEnabled()
	{
		return vaultHandler != null && vaultHandler.isEnabled();
	}

	public final boolean isWorldEditEnabled()
	{
		return worldEditHandler != null && worldEditHandler.isEnabled();
	}

	public MetadataValue getUAIdentifier()
	{
		return uaIdentifier;
	}

	@Override
	public String getPrefix()
	{
		return prefix;
	}

	@Override
	public void reload()
	{
		// Reload config
		reloadConfig();
		Config.load(this);

		messageHandler.reload();
		prefix = FormatUtil.format(messageHandler.getMessage("prefix"));

		arenaTypeHandler.reload();

		// Reload ArenaZones
		for (ArenaZone az : loadedArenas.toArray(new ArenaZone[loadedArenas.size()]))
		{
			az.reload();
		}

		// Reload active arenas
		for (Arena arena : activeArenas.toArray(new Arena[activeArenas.size()]))
		{
			arena.reload();
		}

		// Load any new arenas
		loadArenas();

		// Reload classes
		for (ArenaClass ac : classes.toArray(new ArenaClass[classes.size()]))
		{
			ac.reload();
		}

		// Load any new classes
		loadClasses();
	}

	private void checkFiles()
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
			String[] children = configsFile.list();
			if (children == null || children.length == 0)
				configsFile.delete();
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

		if (children == null || children.length == 0)
			return;

		for (File file : children)
		{
			loadArena(file);
		}

		logHandler.log("Loaded {0} arenas.", loadedArenas.size());
	}

	private void loadArena(File file)
	{
		try
		{
			for (ArenaZone loaded : loadedArenas)
			{
				if (loaded.getFile().equals(file))
					return;
			}

			YamlConfiguration config = new YamlConfiguration();
			config.load(file);

			String typeString = config.isSet("typeString") ? config.getString("typeString") : config.getString("type");
			ArenaType type = arenaTypeHandler.getArenaType(typeString);
			if (type == null)
			{
				logHandler.log(Level.WARNING, "Failed to find ArenaType \"{0}\" for arena {1}.", typeString, file.getName());
				return;
			}

			ArenaZone az = type.getArenaZone(file);
			if (az != null && az.isLoaded())
				logHandler.debug("Arena {0} loaded.", az.getName());
		}
		catch (Throwable ex)
		{
			logHandler.log(Level.WARNING, Util.getUsefulStack(ex, "loading arena " + file.getName()));
		}
	}

	private void loadClasses()
	{
		File folder = new File(getDataFolder(), "classes");
		FileFilter filter = new FileFilter()
		{
			@Override
			public boolean accept(File file)
			{
				return file.getName().contains(".yml");
			}
		};

		File[] files = folder.listFiles(filter);
		if (files == null || files.length == 0)
		{
			generateStockClasses();
			files = folder.listFiles(filter);
		}

		int total = 0;

		files:
		for (File file : files)
		{
			// Make sure it isn't already loaded
			for (ArenaClass loaded : classes)
			{
				if (loaded.getFile().equals(file))
					continue files;
			}

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

		log("Loaded {0} classes.", total);
	}

	private static final List<String> STOCK_CLASSES = Arrays.asList(
			"archer", "brute", "dumbass", "gunner", "healer", "shotgun", "sniper", "spleef"
	);

	private void generateStockClasses()
	{
		for (String stockClass : STOCK_CLASSES)
		{
			saveResource("classes" + File.separator + stockClass + ".yml", false);
		}
	}

	private void loadSigns()
	{
		signHandler = new SignHandler(this);
		log("Loaded {0} signs.", signHandler.getSignCount());
	}

	/**
	 * Stops all running arenas.
	 */
	public final void stopAll()
	{
		for (Arena a : getActiveArenas())
		{
			a.stop();
		}

		activeArenas.clear();
	}

	/**
	 * Gets an ArenaClass by its name.
	 * 
	 * @param name Name
	 * @return The class or null if not found
	 */
	public final ArenaClass getArenaClass(String name)
	{
		for (ArenaClass ac : classes)
		{
			if (ac.getName().equalsIgnoreCase(name))
				return ac;
		}

		return null;
	}

	/**
	 * Gets an ArenaClass by its icon.
	 * 
	 * @param icon Icon
	 * @return The class or null if not found
	 */
	public final ArenaClass getArenaClass(ItemStack icon)
	{
		for (ArenaClass ac : classes)
		{
			if (ac.getIcon().equals(icon))
				return ac;
		}

		return null;
	}

	/**
	 * Deletes an arena with a given name.
	 * 
	 * @param player Player deleting the arena
	 * @param name Arena name
	 */
	public final void deleteArena(Player player, String name)
	{
		ArenaZone az = getArenaZone(name);
		if (az != null)
		{
			// Delete the file
			File file = az.getFile();
			if (file.exists())
				file.delete();

			// Stop the active arena, if applicable
			for (Arena arena : getActiveArenas())
			{
				if (arena.getName().equalsIgnoreCase(name))
					arena.stop();
			}

			// Delete any signs
			for (ArenaSign sign : signHandler.getSigns(az))
			{
				signHandler.deleteSign(sign);
			}

			// Remove it from the list
			loadedArenas.remove(az);

			sendpMessage(player, FormatUtil.format(getMessage("arenaDelete"), name));

			log("Successfully deleted arena: {0}!", name);
		}
		else
		{
			sendpMessage(player, FormatUtil.format(getMessage("arenaNotFound"), name));
		}
	}

	// ---- Location Checks

	/**
	 * Gets the ArenaZone containing the given location.
	 * 
	 * @param location Location
	 * @return The ArenaZone or null if not found
	 */
	public final ArenaZone getZoneInside(Location location)
	{
		for (ArenaZone az : loadedArenas)
		{
			if (az.isInside(location))
				return az;
		}

		return null;
	}

	/**
	 * Whether or not a given location is inside an arena.
	 * 
	 * @param loc Location
	 * @return True if inside, false if not
	 */
	public final boolean isInArena(Location loc)
	{
		return getZoneInside(loc) != null;
	}

	/**
	 * Whether or not a given ArenaLocation is inside an arena.
	 * 
	 * @param loc Location
	 * @return True if inside, false if not
	 */
	public final boolean isInArena(ArenaLocation loc)
	{
		return isInArena(loc.getLocation());
	}

	/**
	 * Gets the Arena that a Player is in.
	 * 
	 * @param player Player
	 * @return The Arena, or null if not found.
	 */
	public final Arena getArena(Player player)
	{
		ArenaPlayer ap = getArenaPlayer(player);
		return ap != null ? ap.getArena() : null;
	}

	/**
	 * Whether or not a given player is in an arena.
	 * 
	 * @param player Player
	 * @return True if they are in an arena, false if not
	 */
	public final boolean isInArena(Player player)
	{
		return getArenaPlayer(player) != null;
	}

	/**
	 * Gets the ArenaPlayer associated with a given player.
	 * 
	 * @param player Player
	 * @return The ArenaPlayer, or null if not found
	 */
	public final ArenaPlayer getArenaPlayer(Player player)
	{
		return getArenaPlayer(player, false);
	}

	/**
	 * Gets the ArenaPlayer associated with a given player.
	 * 
	 * @param player Player
	 * @param inactive Check the inactive list
	 * @return The ArenaPlayer, or null if not found
	 */
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

	/**
	 * Attempt to join an arena.
	 * 
	 * @param player Player
	 * @param name Arena name
	 */
	public final void attemptJoin(Player player, String name)
	{
		attemptJoin(player, name, null);
	}

	/**
	 * Attempt to join an arena on a given team.
	 * 
	 * @param player Player joining
	 * @param name Arena to join
	 * @param team Team to join
	 */
	public final void attemptJoin(Player player, String name, String team)
	{
		// Make sure they can join
		if (waiting.containsKey(player.getName()))
		{
			sendpMessage(player, FormatUtil.format(getMessage("alreadyWaiting")));
			return;
		}

		if (isInArena(player))
		{
			sendpMessage(player, FormatUtil.format(getMessage("alreadyInArena")));
			return;
		}

		if (isCreatingArena(player))
		{
			sendpMessage(player, FormatUtil.format(getMessage("makingArena")));
			return;
		}

		if (! Config.saveInventories)
		{
			if (! InventoryUtil.isEmpty(player.getInventory()))
			{
				sendpMessage(player, FormatUtil.format(getMessage("clearInventory")));
				return;
			}
		}

		// Make sure the arena exists
		ArenaZone az = getArenaZone(name);
		if (az == null)
		{
			sendpMessage(player, FormatUtil.format(getMessage("unknownArena"), name));

			List<ArenaZone> matches = matchArena(name);
			if (matches.size() > 0)
			{
				StringJoiner joiner = new StringJoiner("&3, &e");
				for (ArenaZone match : matches)
				{
					joiner.append(match.getName());
				}

				sendpMessage(player, FormatUtil.format(getMessage("didYouMean"), joiner.toString()));
			}

			return;
		}

		if (az.isDisabled())
		{
			sendpMessage(player, FormatUtil.format(getMessage("arenaDisabled")));
			return;
		}

		// Permission check
		String permission = "ultimatearena.join." + az.getName().toLowerCase();
		if (az.isNeedsPermission() && ! player.hasPermission(permission))
		{
			sendpMessage(player, FormatUtil.format(getMessage("noPermission")));
			sendpMessage(player, FormatUtil.format(getMessage("permissionRequired"), permission));
			return;
		}

		// Make sure they weren't in this arena already
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
						sendpMessage(player, FormatUtil.format(getMessage("cantLeaveAndRejoin")));
						return;
					}
				}
			}
		}

		// Check the arena
		Arena arena = getArena(name);
		if (arena != null)
		{
			if (arena.isStopped())
			{
				sendpMessage(player, FormatUtil.format(getMessage("arenaStopping")));
				return;
			}

			if (arena.isInGame() && ! arena.isJoinInProgress())
			{
				sendpMessage(player, FormatUtil.format(getMessage("arenaStarted")));
				return;
			}

			if (arena.getPlayerCount() >= arena.getMaxPlayers())
			{
				if (! permissionHandler.hasPermission(player, Permission.JOIN_FULL))
				{
					sendpMessage(player, FormatUtil.format(getMessage("arenaFull")));
					return;
				}
			}
		}

		// Make 'em wait so they dont abuse the teleportation
		ArenaJoinTask join = new ArenaJoinTask(player.getName(), name, this, team);
		if (Config.joinTimerEnabled)
		{
			int seconds = Config.joinTimerWait;
			int wait = seconds * 20;

			join.runTaskLater(this, wait);
			waiting.put(player.getName(), join);

			sendpMessage(player, FormatUtil.format(getMessage("standStill"), seconds));
		}
		else
		{
			join.run();
		}
	}

	/**
	 * Adds a player to an arena.
	 * 
	 * @param player Player
	 * @param name Arena name
	 * @param team Team name/id
	 */
	public final void addPlayer(Player player, String name, String team)
	{
		try
		{
			ArenaZone az = getArenaZone(name);
			Arena active = getArena(name);
			if (active != null)
			{
				// Some redundant checks in case anything changed
				if (active.isStopped())
				{
					sendpMessage(player, FormatUtil.format(getMessage("arenaStopping")));
					return;
				}

				if (active.isInGame() && ! active.isJoinInProgress())
				{
					sendpMessage(player, FormatUtil.format(getMessage("arenaStarted")));
					return;
				}

				if (active.getPlayerCount() < active.getMaxPlayers())
				{
					active.addPlayer(player, team);
					return;
				}

				if (! permissionHandler.hasPermission(player, Permission.JOIN_FULL))
				{
					sendpMessage(player, FormatUtil.format(getMessage("arenaFull")));
					return;
				}

				// Sorry, random person!
				if (kickRandomPlayer(active))
				{
					active.addPlayer(player, team);
					return;
				}

				sendpMessage(player, FormatUtil.format(getMessage("couldntForceJoin")));
				return;
			}

			ArenaType type = az.getType();
			if (type == null)
			{
				sendpMessage(player, FormatUtil.format(getMessage("invalidType"), az.getName()));
				return;
			}

			// Try to create the arena
			Arena arena;

			try
			{
				arena = type.newArena(az);
				if (arena == null)
					throw new NullPointerException();
			}
			catch (Throwable ex)
			{
				type.getLogger().log(Level.WARNING, "Failed to obtain new arena: ", ex);
				sendpMessage(player, FormatUtil.format(getMessage("invalidArena"), type.getName()));
				return;
			}

			activeArenas.add(arena);
			arena.addPlayer(player, team);
			arena.announce();
		}
		catch (Throwable ex)
		{
			logHandler.log(Level.SEVERE, Util.getUsefulStack(ex, "adding " + player.getName() + " to arena " + name));
			sendpMessage(player, FormatUtil.format(getMessage("failedToAdd")));
		}
	}

	// Kicks a random player if the arena is full
	private final boolean kickRandomPlayer(Arena arena)
	{
		List<ArenaPlayer> validPlayers = new ArrayList<>();
		for (ArenaPlayer ap : arena.getActivePlayers())
		{
			if (! permissionHandler.hasPermission(ap.getPlayer(), net.dmulloy2.ultimatearena.types.Permission.JOIN_FULL))
				validPlayers.add(ap);
		}

		if (validPlayers.isEmpty())
			return false;

		ArenaPlayer ap = validPlayers.get(Util.random(validPlayers.size()));
		ap.leaveArena(LeaveReason.KICK);
		return true;
	}

	/**
	 * Gets an Arena by its name.
	 * 
	 * @param name Arena name
	 * @return Arena or null if not found
	 */
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

	/**
	 * Gets an ArenaZone by its name.
	 * 
	 * @param name Arena name
	 * @return ArenaZone or null if not found
	 */
	public final ArenaZone getArenaZone(String name)
	{
		for (ArenaZone az : loadedArenas)
		{
			if (az.getName().equalsIgnoreCase(name))
				return az;
		}

		return null;
	}

	// ---- Arena Creation

	/**
	 * Attempts to create a new {@link Arena}.
	 *
	 * @param player {@link Player} who is creating the arena
	 * @param name Name of the new arena
	 * @param type Type of the new arena
	 */
	public final void createArena(Player player, String name, String type)
	{
		if (isCreatingArena(player))
		{
			sendpMessage(player, FormatUtil.format(getMessage("alreadyCreating")));
			return;
		}

		for (ArenaZone az : loadedArenas)
		{
			if (az.getName().equalsIgnoreCase(name))
			{
				sendpMessage(player, FormatUtil.format(getMessage("arenaAlreadyExists")));
				return;
			}
		}

		ArenaType at = arenaTypeHandler.getArenaType(type);
		if (at == null)
		{
			sendpMessage(player, FormatUtil.format(getMessage("typeDoesntExist"), type));
			return;
		}

		ArenaCreator ac;

		try
		{
			ac = at.newCreator(player, name);
			if (ac == null)
				throw new NullPointerException();
		}
		catch (Throwable ex)
		{
			sendpMessage(player, FormatUtil.format(getMessage("creationFailed"), type));
			at.getLogger().log(Level.WARNING, "Failed to obtain ArenaCreator for " + type + ": ", ex);
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
			sendpMessage(player, FormatUtil.format(getMessage("notCreating")));
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
			sendpMessage(player, FormatUtil.format(getMessage("stoppedCreating"), ac.getArenaName()));
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
	 * @throws IllegalArgumentException if command is null
	 */
	public final boolean isWhitelistedCommand(String command)
	{
		if (! Config.restrictCommands)
			return true;

		Validate.notNull(command, "command cannot be null!");

		// Lazy-load whitelistedCommands
		if (whitelistedCommands == null)
		{
			whitelistedCommands = new ArrayList<>();
			for (String whitelisted : Config.whitelistedCommands)
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
	public final Arena[] getActiveArenas()
	{
		return activeArenas.toArray(new Arena[0]);
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

	// People want to remove messages :/

	private void sendpMessage(Player player, String message)
	{
		if (message.isEmpty()) return;
		player.sendMessage(prefix + message);
	}
}
