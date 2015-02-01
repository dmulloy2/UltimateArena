/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.ultimatearena.types;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import lombok.Getter;
import lombok.Setter;
import net.dmulloy2.io.FileSerialization;
import net.dmulloy2.io.IOUtil;
import net.dmulloy2.types.Reloadable;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.api.ArenaType;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.integration.VaultHandler;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.Util;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.io.Files;

/**
 * @author dmulloy2
 */

@Getter @Setter
public class ArenaZone implements Reloadable, ConfigurationSerializable
{
	protected static transient final int CURRENT_VERSION = 3;

	protected int maxPlayers = 24;

	// ---- Stats
	protected int liked;
	protected int disliked;
	protected int timesPlayed;

	protected boolean disabled;

	protected String worldName;
	protected String defaultClass;

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

	protected transient List<String> voted = new ArrayList<String>();

	protected List<ArenaLocation> spawns = new ArrayList<ArenaLocation>();
	protected List<ArenaLocation> flags = new ArrayList<ArenaLocation>();

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
		this.type = type;
		this.typeString = type.getName();
		this.plugin = type.getPlugin();
	}

	// Regular constructor
	public ArenaZone(ArenaType type, File file)
	{
		this.plugin = type.getPlugin();
		this.file = file;
		this.type = type;
		this.typeString = type.getName();
		this.stylized = type.getStylizedName();
		this.name = IOUtil.trimFileExtension(file, ".dat");
		this.initialize();
	}

	@Deprecated
	public ArenaZone(UltimateArena plugin, File file)
	{
		this.plugin = plugin;
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

		// Default class
		if (defaultClass == null || defaultClass.isEmpty())
		{
			if (! plugin.getClasses().isEmpty())
			{
				ArenaClass ac = plugin.getClasses().get(0);
				if (ac != null)
					this.defaultClass = ac.getName();
			}
		}

		// Set lobby parameters
		lobby.setParam(lobby1, lobby2);
		arena.setParam(arena1, arena2);

		// Add to the loaded arenas list
		plugin.getLoadedArenas().add(this);
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
		List<String> lines = new ArrayList<String>();

		StringBuilder line = new StringBuilder();
		line.append(FormatUtil.format("&3====[ &e{0} &3]====", WordUtils.capitalize(name)));
		lines.add(line.toString());

		// Calculate percentage
		int total = plugin.getTotalArenasPlayed();
		int plays = timesPlayed;

		double percentage = (double) plays / (double) total * 100;

		line = new StringBuilder();
		line.append(FormatUtil.format("&3Plays: &e{0}&3/&e{1} &3(&e{2}%&3)", plays, total, percentage));
		lines.add(line.toString());

		// Calculate popularity
		if (voted.size() == 0)
		{
			percentage = 0.0D;
		}
		else
		{
			percentage = (double) liked / (double) voted.size() * 100;
		}

		line = new StringBuilder();
		line.append(FormatUtil.format("&3Popularity: &e{0}&3/&e{1} &3(&e{2}%&3)", liked, voted.size(), percentage));
		lines.add(line.toString());

		return lines;
	}

	// ---- Utility Methods

	/**
	 * Checks if a location is inside this arena.
	 *
	 * @param loc {@link Location} to check
	 * @return True if the location is inside the arena, false if not
	 */
	public boolean isInside(Location location)
	{
		try
		{
			return lobby.isInside(location) || arena.isInside(location);
		}
		catch (Throwable ex)
		{
			if (! insideStackPrinted)
			{
				plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "performing location check for " + name));

				World world = getWorld();
				if (world == null)
					plugin.getLogHandler().log(Level.WARNING, "This is caused by a null world. Does world {0} exist?", worldName);

				insideStackPrinted = true;
			}

			return false;
		}
	}

	private transient boolean insideStackPrinted;

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

		if (plugin.getConfig().getBoolean("moneyRewards", true))
		{
			if (plugin.isVaultEnabled())
			{
				VaultHandler vault = plugin.getVaultHandler();
				double money = config.getCashReward();
				if (config.isRewardBasedOnXp())
					money = money * (ap.getGameXP() / 250.0D);

				if (money > 0.0D)
				{
					if (vault.depositPlayer(ap.getPlayer(), money))
					{
						String format = vault.format(money);
						ap.sendMessage(plugin.getPrefix() + FormatUtil.format("&a{0} has been added to your account!", format));
					}
					else
					{
						ap.sendMessage(plugin.getPrefix() + FormatUtil.format("&cCould not give cash reward."));
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
						boolean accessible = field.isAccessible();
						field.setAccessible(true);
						field.set(this, entry.getValue());
						field.setAccessible(accessible);
					}
				}
			} catch (Throwable ex) { }
		}

		loadCustomOptions(config);
		loadConfiguration();
	}

	/**
	 * Loads custom options.
	 */
	protected void loadCustomOptions(FileConfiguration fc) { }

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

	private final boolean checkConversion(int version)
	{
		if (version != CURRENT_VERSION)
		{
			convert();
			return true;
		}

		return false;
	}

	@Deprecated
	private final void convert()
	{
		try
		{
			// Make backup
			File backup = new File(file.getAbsolutePath() + "_old");
			Files.copy(file, backup);

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
		config = getType().newConfig(this);
	}

	/**
	 * Saves configuration settings
	 */
	protected final void saveConfiguration()
	{
		if (config == null) // Config not initialized yet
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
		ArenaType type = getType();
		if (type == null)
			return null;

		return type.getConfig();
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
				boolean accessible = field.isAccessible();

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
					if ((String) field.get(this) != null)
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
			} catch (Throwable ex) { }
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof ArenaZone)
		{
			ArenaZone that = (ArenaZone) obj;
			return that.getName().equals(name);
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode()
	{
		int hash = 36;
		hash *= 1 + name.hashCode();
		return hash;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString()
	{
		return name;
	}
}