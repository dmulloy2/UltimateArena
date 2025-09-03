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
package net.dmulloy2.ultimatearena.types;

import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.dmulloy2.swornapi.io.FileSerialization;
import net.dmulloy2.swornapi.io.IOUtil;
import net.dmulloy2.swornapi.types.Reloadable;
import net.dmulloy2.swornapi.util.FormatUtil;
import net.dmulloy2.swornapi.util.Util;
import net.dmulloy2.swornapi.util.Validate;
import net.dmulloy2.ultimatearena.Config;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.api.ArenaType;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.integration.VaultHandler;
import net.dmulloy2.ultimatearena.tasks.CommandRunner;

/**
 * @author dmulloy2
 */

@Getter @Setter
public class ArenaZone implements Reloadable, ConfigurationSerializable
{
	protected static transient final int CURRENT_VERSION = 3;

	// ---- Stats
	protected int liked;
	protected int disliked;
	protected int timesPlayed;

	protected boolean disabled;
	protected boolean needsPermission;

	protected String worldName;

	// ---- Type
	protected String typeString;
	protected transient ArenaType type;
	protected transient String stylized;

	// ---- Locations
	protected ArenaLocation lobby1;
	protected ArenaLocation lobby2;
	protected ArenaLocation arena1;
	protected ArenaLocation arena2;
	protected ArenaLocation team1spawn;
	protected ArenaLocation team2spawn;
	protected ArenaLocation lobbyREDspawn;
	protected ArenaLocation lobbyBLUspawn;

	// ---- Fields
	protected transient Field lobby;
	protected transient Field arena;

	protected transient List<String> voted = new ArrayList<>();

	protected List<ArenaLocation> spawns = new ArrayList<>();
	protected List<ArenaLocation> flags = new ArrayList<>();

	// ---- Transient
	protected transient File file;
	protected transient String name;
	protected transient World world;
	protected transient boolean loaded;
	protected transient ArenaConfig config;

	protected transient final UltimateArena plugin;

	// Creation constructor
	public ArenaZone(ArenaType type)
	{
		Validate.notNull(type, "type cannot be null");

		this.type = type;
		this.typeString = type.getName();
		this.plugin = type.getPlugin();
		this.stylized = type.getStylizedName();
	}

	// Regular constructor
	public ArenaZone(ArenaType type, File file)
	{
		this(type);

		this.file = file;
		this.name = IOUtil.trimFileExtension(file, ".dat");
		this.initialize();
	}

	/**
	 * Initializes this arena.
	 *
	 * @return True if initialization was successful, false if not
	 */
	public final boolean initialize()
	{
		// Fields
		this.arena = new Field();
		this.lobby = new Field();

		try
		{
			// Load the arena
			loadFromDisk();
		}
		catch (Throwable ex)
		{
			plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "loading {0} from disk", name));
			this.loaded = false;
			return false;
		}

		// Load configuration settings
		loadConfiguration();

		// Set lobby parameters
		lobby.setParam(lobby1, lobby2);
		arena.setParam(arena1, arena2);

		List<ArenaZone> loaded = plugin.getLoadedArenas();
		loaded.add(this);

		this.loaded = true;
		return true;
	}

	// ---- Getters and Setters

	/**
	 * Gets the {@link World} this arena is in.
	 *
	 * @return The world
	 */
	public final World getWorld()
	{
		if (world == null)
			world = plugin.getServer().getWorld(worldName);

		return world;
	}

	/**
	 * Sets the {@link World} this arena is in.
	 * <p>
	 * Should only be used in arena creation
	 *
	 * @param world World this arena is in
	 */
	public final void setWorld(World world)
	{
		Validate.isTrue(worldName == null, "World already set!");
		this.worldName = world.getName();
		this.world = world;
	}

	/**
	 * Gets this arena's type.
	 *
	 * @return This arena's type
	 */
	public final ArenaType getType()
	{
		return type;
	}

	/**
	 * Gets this arena's statistics.
	 *
	 * @return This arena's statistics
	 */
	public final List<String> getStats()
	{
		List<String> lines = new ArrayList<>();

		StringBuilder line = new StringBuilder();
		line.append(FormatUtil.format(plugin.getMessage("genericHeader"), FormatUtil.capitalize(name)));
		lines.add(line.toString());

		// Calculate percentage
		int total = plugin.getTotalArenasPlayed();
		int plays = timesPlayed;

		double percentage = (double) plays / (double) total * 100;

		line = new StringBuilder();
		line.append(FormatUtil.format(plugin.getMessage("statPlays"), plays, total, percentage));
		lines.add(line.toString());

		// Calculate popularity
		if (voted.isEmpty())
		{
			percentage = 0.0D;
		}
		else
		{
			percentage = (double) liked / (double) voted.size() * 100;
		}

		line = new StringBuilder();
		line.append(FormatUtil.format(plugin.getMessage("statPopularity"), liked, voted.size(), percentage));
		lines.add(line.toString());

		return lines;
	}

	// ---- Utility Methods

	public final boolean isInside(Location loc)
	{
		Validate.notNull(loc, "loc cannot be null!");
		return lobby.isInside(loc) || arena.isInside(loc);
	}

	public final boolean isInsideArena(Location loc)
	{
		Validate.notNull(loc, "loc cannot be null!");
		return arena.isInside(loc);
	}

	/**
	 * Gets whether or not a player has voted
	 *
	 * @param player {@link Player} voting
	 * @return True if they have voted, false if not
	 */
	public final boolean hasVoted(Player player)
	{
		Validate.notNull(player, "player cannot be null!");
		return voted.contains(player.getName());
	}

	/**
	 * Gets whether or not this arena is active.
	 *
	 * @return True if the arena is active, false if not
	 */
	public final boolean isActive()
	{
		return plugin.getArena(name) != null;
	}

	/**
	 * Gives a player defined rewards.
	 *
	 * @param ap {@link ArenaPlayer} to give rewards to
	 */
	public void giveRewards(ArenaPlayer ap)
	{
		Validate.notNull(ap, "ap cannot be null!");
		if (! config.isGiveRewards())
			return;

		plugin.debug("Rewarding player {0}", ap.getName());

		if (! config.getRewardCommands().isEmpty())
		{
			new CommandRunner(ap.getName(), config.getRewardCommands(), plugin).runTask(plugin);	
		}

		if (! config.getScaledRewards().isEmpty())
		{
			int xp = ap.getGameXP();
			for (ScaledReward reward : config.getScaledRewards())
			{
				if (reward != null)
					ap.giveItem(reward.get(xp));
			}
		}
		else
		{
			for (ItemStack stack : config.getRewards())
			{
				if (stack != null)
					ap.giveItem(stack.clone());
			}
		}

		if (Config.moneyRewards)
		{
			if (plugin.isVaultEnabled())
			{
				VaultHandler vault = plugin.getVaultHandler();
				double money = config.getCashReward();
				if (config.isRewardBasedOnXp())
					money = money * (ap.getGameXP() / 250.0D);

				if (money > 0.0D)
				{
					String response = vault.depositPlayer(ap.getPlayer(), money);
					if (response == null)
					{
						String format = vault.format(money);
						ap.sendMessage(plugin.getMessage("cashReward"), format);
					}
					else
					{
						ap.sendMessage(plugin.getMessage("cashFailed"), response);
					}
				}
			}
		}
	}

	// ---- I/O

	protected final void loadFromDisk() throws Throwable
	{
		checkFile();

		YamlConfiguration config = new YamlConfiguration();
		config.load(file);

		Map<String, Object> values = config.getValues(false);

		// Versioning
		int version = 0;
		if (values.containsKey("version"))
			version = (int) values.get("version");

		// Conversion
		if (checkConversion(version))
			return;

		// Load
		for (Entry<String, Object> entry : values.entrySet())
		{
			try
			{
				for (java.lang.reflect.Field field : ArenaZone.class.getDeclaredFields())
				{
					if (field.getName().equalsIgnoreCase(entry.getKey()))
					{
						boolean accessible = field.canAccess(this);
						field.setAccessible(true);
						field.set(this, entry.getValue());
						field.setAccessible(accessible);
					}
				}
			} catch (Throwable ignored) { }
		}

		loadCustomOptions(config);
	}

	/**
	 * Loads custom options.
	 * @param config Configuration
	 */
	protected void loadCustomOptions(YamlConfiguration config) { }

	/**
	 * Saves this arena to disk
	 */
	public final void saveToDisk()
	{
		try
		{
			checkFile();
			FileSerialization.save(this, file);
			saveConfiguration();
		}
		catch (Throwable ex)
		{
			plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "saving {0} to disk", name));
		}
	}

	protected final void checkFile()
	{
		if (file == null)
		{
			File folder = new File(plugin.getDataFolder(), "arenas");
			if (! folder.exists())
				folder.mkdirs();

			file = new File(folder, name + ".dat");
		}
	}

	// ---- Conversion

	private boolean checkConversion(int version)
	{
		if (version != CURRENT_VERSION)
		{
			convert();
			return true;
		}

		return false;
	}

	@Deprecated
	private void convert()
	{
		try
		{
			// Make backup
			File backup = new File(file.getAbsolutePath() + "_old");
			Files.copy(file.toPath(), backup.toPath());

			// Load legacy arena zone
			plugin.getFileHandler().loadLegacy(this);
			loadConfiguration();

			// Delete
			file.delete();

			// Save
			saveToDisk();

			// Load
			loadFromDisk();
		}
		catch (Throwable ex)
		{
			plugin.getLogHandler().log(Level.SEVERE, Util.getUsefulStack(ex, "converting " + name));
		}
	}

	// ---- Configuration

	/**
	 * Loads configuration settings
	 */
	protected final void loadConfiguration()
	{
		newConfig();
		config.load(file, getDefaultConfig());
	}

	/**
	 * Creates a new config
	 */
	protected void newConfig()
	{
		try
		{
			config = type.newConfig(this);
		}
		catch (Throwable ex)
		{
			type.getLogger().log(Level.WARNING, "Failed to obtain new config for " + name + ": ", ex);
		}
	}

	/**
	 * Saves configuration settings
	 */
	protected final void saveConfiguration()
	{
		if (config == null) // Config not initialized
			return;

		Map<String, Object> def = getDefaultConfig().serialize();
		Map<String, Object> data = Util.filterDuplicateEntries(config.serialize(), def);
		if (data.isEmpty())
			return;

		YamlConfiguration fc = YamlConfiguration.loadConfiguration(file);
		for (Entry<String, Object> entry : data.entrySet())
		{
			fc.set(entry.getKey(), entry.getValue());
		}

		try
		{
			fc.save(file);
		}
		catch (Throwable ex)
		{
			plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "saving configuration for " + name));
		}
	}

	/**
	 * @return The default config for this type.
	 */
	protected ArenaConfig getDefaultConfig()
	{
		return getType().getConfig();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Object> serialize()
	{
		Map<String, Object> data = new LinkedHashMap<>();

		for (java.lang.reflect.Field field : ArenaZone.class.getDeclaredFields())
		{
			if (Modifier.isTransient(field.getModifiers()))
				continue;

			try
			{
				boolean accessible = field.canAccess(this);

				field.setAccessible(true);

				if (field.getType().equals(Integer.TYPE))
				{
					if (field.getInt(this) != 0)
						data.put(field.getName(), field.getInt(this));
				}
				else if (field.getType().equals(Long.TYPE))
				{
					if (field.getLong(this) != 0)
						data.put(field.getName(), field.getLong(this));
				}
				else if (field.getType().equals(Boolean.TYPE))
				{
					if (field.getBoolean(this))
						data.put(field.getName(), field.getBoolean(this));
				}
				else if (field.getType().isAssignableFrom(Collection.class))
				{
					if (! ((Collection<?>) field.get(this)).isEmpty())
						data.put(field.getName(), field.get(this));
				}
				else if (field.getType().isAssignableFrom(String.class))
				{
					if (field.get(this) != null)
						data.put(field.getName(), field.get(this));
				}
				else if (field.getType().isAssignableFrom(Map.class))
				{
					if (! ((Map<?, ?>) field.get(this)).isEmpty())
						data.put(field.getName(), field.get(this));
				}
				else
				{
					if (field.get(this) != null)
						data.put(field.getName(), field.get(this));
				}

				field.setAccessible(accessible);
			} catch (Throwable ignored) { }
		}

		data.put("version", CURRENT_VERSION);
		return data;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reload()
	{
		if (! file.exists())
		{
			Arena arena = plugin.getArena(name);
			if (arena != null)
				arena.stop();

			plugin.getLoadedArenas().remove(this);
			return;
		}

		// Re-initialize
		initialize();
	}

	// ---- Generic Methods

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this) return true;

		if (obj instanceof ArenaZone that)
		{
			return Objects.equals(name, that.name);
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(name);
	}

	@Override
	public String toString()
	{
		return name;
	}
}
