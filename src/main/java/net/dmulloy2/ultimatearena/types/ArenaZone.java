package net.dmulloy2.ultimatearena.types;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import lombok.Getter;
import lombok.Setter;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.io.FileSerialization;
import net.dmulloy2.ultimatearena.util.FormatUtil;
import net.dmulloy2.ultimatearena.util.InventoryUtil;
import net.dmulloy2.ultimatearena.util.ItemUtil;
import net.dmulloy2.ultimatearena.util.NumberUtil;
import net.dmulloy2.ultimatearena.util.Util;
import net.milkbowl.vault.economy.EconomyResponse;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.io.Files;

/**
 * @author dmulloy2
 */

@Getter @Setter
public class ArenaZone implements Reloadable, ConfigurationSerializable
{
	private static transient final int CURRENT_VERSION = 3;
	
	protected int maxPlayers = 24;

	protected int liked;
	protected int disliked;
	protected int timesPlayed;

	protected transient Material specialType; // Spleef
	protected String specialTypeString;

	protected boolean disabled;
	protected transient boolean loaded;

	protected String worldName;
	protected String arenaName = "";
	protected String defaultClass;

	protected transient FieldType type;
	protected String typeString;

	protected ArenaLocation lobby1;
	protected ArenaLocation lobby2;
	protected ArenaLocation arena1;
	protected ArenaLocation arena2;
	protected ArenaLocation team1spawn;
	protected ArenaLocation team2spawn;
	protected ArenaLocation lobbyREDspawn;
	protected ArenaLocation lobbyBLUspawn;

	protected transient File file;

	protected transient Field lobby;
	protected transient Field arena;

	protected transient List<String> voted = new ArrayList<String>();

	protected List<ArenaLocation> spawns = new ArrayList<ArenaLocation>();
	protected List<ArenaLocation> flags = new ArrayList<ArenaLocation>();

	protected transient World world;

	protected transient final UltimateArena plugin;

	public ArenaZone(UltimateArena plugin)
	{
		this.plugin = plugin;
	}

	public ArenaZone(UltimateArena plugin, File file)
	{
		this.plugin = plugin;
		this.file = file;
		this.arenaName = Util.trimFileExtension(file, ".dat");
		this.initialize();
	}

	public final boolean initialize()
	{
		// Fields
		this.arena = new Field();
		this.lobby = new Field();

		// Load the arena
		loadFromDisk();

		if (loaded)
		{
			// Load configuration settings
			loadConfiguration();

			// Default class
			if (defaultClass == null || defaultClass.isEmpty())
			{
				if (! plugin.getClasses().isEmpty())
				{
					ArenaClass ac = plugin.getClasses().get(0);
					if (ac != null)
					{
						this.defaultClass = ac.getName();
					}
				}
			}

			// Set lobby parameters
			lobby.setParam(lobby1, lobby2);
			arena.setParam(arena1, arena2);

			// Add to the loaded arenas list
			plugin.getLoadedArenas().add(this);
		}

		return loaded;
	}

	public final List<String> getStats()
	{
		List<String> lines = new ArrayList<String>();

		StringBuilder line = new StringBuilder();
		line.append(FormatUtil.format("&3====[ &e{0} &3]====", WordUtils.capitalize(arenaName)));
		lines.add(line.toString());

		// Calculate percentage
		int total = plugin.getTotalArenasPlayed();
		int plays = timesPlayed;

		double percentage = ((double) plays / (double) total) * 100;

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
			percentage = ((double) liked / (double) voted.size()) * 100;
		}

		line = new StringBuilder();
		line.append(FormatUtil.format("&3Popularity: &e{0}&3/&e{1} &3(&e{2}%&3)", liked, voted.size(), percentage));
		lines.add(line.toString());

		return lines;
	}

	public final boolean checkLocation(Location loc)
	{
		try
		{
			return lobby.isInside(loc) || arena.isInside(loc);
		}
		catch (Exception e)
		{
			plugin.outConsole(Level.WARNING, "Could not perform location check for arena {0}!", arenaName);
			plugin.outConsole(Level.WARNING, "This is often caused by a null world!");
			plugin.outConsole(Level.WARNING, "worldName = {0}, world = {1}", worldName, getWorld() == null ? "null" : getWorld().getName());
			return false;
		}
	}

	public final boolean canLike(Player player)
	{
		return ! voted.contains(player.getName());
	}

	public final void setWorld(World world)
	{
		this.world = world;
		this.worldName = world.getName();
	}

	public final World getWorld()
	{
		if (world == null)
			world = plugin.getServer().getWorld(worldName);

		return world;
	}

	public void giveRewards(ArenaPlayer ap)
	{
		Player player = ap.getPlayer();

		plugin.debug("Rewarding player {0}", player.getName());

		for (ItemStack stack : rewards)
		{
			if (stack == null)
				continue;

			int amt = stack.getAmount();

			// Gradient based, if applicable
			if (rewardBasedOnXp)
				amt = (int) Math.round(ap.getGameXP() / 200.0D);

			if (amt > 0)
			{
				stack.setAmount(amt);

				// Add the item
				InventoryUtil.giveItem(player, stack);
			}
		}

		if (plugin.getConfig().getBoolean("moneyrewards", true))
		{
			if (plugin.getEconomy() != null)
			{
				double money = (double) cashReward;
				if (rewardBasedOnXp)
					money = money * (ap.getGameXP() / 250.0D);

				if (money > 0.0D)
				{
					EconomyResponse er = plugin.getEconomy().depositPlayer(player.getName(), money);
					if (er.transactionSuccess())
					{
						String format = plugin.getEconomy().format(money);
						player.sendMessage(plugin.getPrefix() + FormatUtil.format("&a{0} has been added to your account!", format));
					}
					else
					{
						player.sendMessage(plugin.getPrefix() + FormatUtil.format("&cCould not give cash reward: {0}", er.errorMessage));
					}
				}
			}
		}
	}

	// ---- I/O

	public final void loadFromDisk()
	{
		checkFile();

		FileConfiguration fc = YamlConfiguration.loadConfiguration(file);
		Map<String, Object> values = fc.getValues(false);

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
			// Configuration check
			try
			{
				// This works because if the field does not exist, a
				// NoSuchFieldException is thrown and we will never get to the
				// continue statement
				ArenaConfig.class.getDeclaredField(entry.getKey());
				continue;
			} catch (Exception e) { }

			try
			{
				for (java.lang.reflect.Field field : getClass().getDeclaredFields())
				{
					if (field.getName().equals(entry.getKey()))
					{
						boolean accessible = field.isAccessible();
						field.setAccessible(true);
						field.set(this, entry.getValue());
						field.setAccessible(accessible);
					}
				}
			} catch (Exception e) { }
		}

		this.specialType = Material.matchMaterial(specialTypeString);
		this.type = FieldType.getByName(typeString);
		this.loaded = true;

		loadConfiguration();
	}

	public final void saveToDisk()
	{
		checkFile();

		try
		{
			FileSerialization.save(this, file);
		}
		catch (IOException e)
		{
			plugin.outConsole(Level.SEVERE, Util.getUsefulStack(e, "saving arena " + arenaName));
		}
	}

	private final void checkFile()
	{
		if (file == null)
		{
			File folder = new File(plugin.getDataFolder(), "arenas");
			if (! folder.exists())
			{
				folder.mkdirs();
			}

			file = new File(folder, arenaName + ".dat");
		}
	}

	// ---- Conversion

	public final boolean checkConversion(int version)
	{
		if (version != CURRENT_VERSION)
		{
			convert();
			return true;
		}

		return false;
	}

	@Deprecated
	public final void convert()
	{
		try
		{
			// Make backup
			File backup = new File(file.getAbsolutePath() + "_old");
			Files.copy(file, backup);

			// Load legacy arena zone
			loadConfiguration();
			plugin.getFileHandler().load(this);

			// Delete
			file.delete();

			// Save
			saveToDisk();
			saveConfiguration();

			// Load
			loadFromDisk();
		}
		catch (Exception e)
		{
			plugin.outConsole(Level.SEVERE, Util.getUsefulStack(e, "converting " + arenaName));
		}
	}

	// ---- Configuration

	// Serializable variables
	private int gameTime, lobbyTime, maxDeaths, maxWave, cashReward, maxPoints;

	private boolean allowTeamKilling, countMobKills, rewardBasedOnXp, giveRewards;

	private List<String> blacklistedClasses, whitelistedClasses;

	// These have to be manually loaded, since they are not serializable
	private transient List<ItemStack> rewards;
	private transient HashMap<Integer, List<KillStreak>> killStreaks;

	public void loadConfiguration()
	{
		try
		{
			ArenaConfig conf = plugin.getConfig(type.getName());

			YamlConfiguration fc = YamlConfiguration.loadConfiguration(file);
			if (type.getName().equalsIgnoreCase("mob"))
			{
				this.maxWave = fc.getInt("maxWave", conf.getMaxWave());
			}

			if (type.getName().equalsIgnoreCase("koth"))
			{
				this.maxPoints = fc.getInt("maxPoints", conf.getMaxPoints());
			}

			this.gameTime = fc.getInt("gameTime", conf.getGameTime());
			this.lobbyTime = fc.getInt("lobbyTime", conf.getLobbyTime());
			this.maxDeaths = fc.getInt("maxDeaths", conf.getMaxDeaths());
			this.allowTeamKilling = fc.getBoolean("allowTeamKilling", conf.isAllowTeamKilling());
			this.cashReward = fc.getInt("cashReward", conf.getCashReward());
			this.countMobKills = fc.getBoolean("countMobKills", conf.isCountMobKills());
			this.rewardBasedOnXp = fc.getBoolean("rewardBasedOnXp", conf.isRewardBasedOnXp());

			this.rewards = new ArrayList<ItemStack>();
			if (fc.isSet("rewards"))
			{
				for (String reward : fc.getStringList("rewards"))
				{
					ItemStack stack = ItemUtil.readItem(reward);
					if (stack != null)
						rewards.add(stack);
				}
			}
			else
			{
				this.rewards = conf.getRewards();
			}

			this.giveRewards = fc.getBoolean("giveRewards", conf.isGiveRewards());

			this.blacklistedClasses = new ArrayList<String>();

			if (fc.isSet("blacklistedClasses"))
			{
				blacklistedClasses.addAll(fc.getStringList("blacklistedClasses"));
			}

			this.whitelistedClasses = new ArrayList<String>();

			if (fc.isSet("whitelistedClasses"))
			{
				whitelistedClasses.addAll(fc.getStringList("whitelistedClasses"));
			}

			if (fc.isSet("killStreaks"))
			{
				this.killStreaks = new HashMap<Integer, List<KillStreak>>();

				Map<String, Object> map = fc.getConfigurationSection("killStreaks").getValues(true);

				for (Entry<String, Object> entry : map.entrySet())
				{
					int kills = NumberUtil.toInt(entry.getKey());
					if (kills < 0)
						continue;

					@SuppressWarnings("unchecked") // No way to check this :I
					List<String> values = (List<String>) entry.getValue();

					List<KillStreak> streaks = new ArrayList<KillStreak>();
					for (String value : values)
					{
						value = value.trim();

						// Determine type
						String s = value.substring(0, value.indexOf(",")).trim();

						KillStreak.Type type = null;
						if (s.equalsIgnoreCase("mob"))
							type = KillStreak.Type.MOB;
						else if (s.equalsIgnoreCase("item"))
							type = KillStreak.Type.ITEM;

						if (type == KillStreak.Type.MOB)
						{
							String[] split = value.split(",");

							String message = split[1].trim();
							EntityType entityType = EntityType.valueOf(split[2].trim());
							int amount = Integer.parseInt(split[3].trim());

							streaks.add(new KillStreak(kills, message, entityType, amount));
							continue;
						}
						else if (type == KillStreak.Type.ITEM)
						{
							// Yay substring and indexof!
							s = value.substring(value.indexOf(",") + 1).trim();

							String message = s.substring(0, s.indexOf(",")).trim();

							s = s.substring(s.indexOf(",") + 1).trim();

							ItemStack stack = ItemUtil.readItem(s);
							if (stack != null)
								streaks.add(new KillStreak(kills, message, stack));
							continue;
						}
					}

					killStreaks.put(kills, streaks);
				}
			}
			else
			{
				this.killStreaks = conf.getKillStreaks();
			}
		}
		catch (Exception e)
		{
			plugin.debug(Util.getUsefulStack(e, "loading config for \"" + arenaName + "\""));
		}
	}

	public final void saveConfiguration()
	{
		Map<String, Object> data = serialize();
		YamlConfiguration fc = YamlConfiguration.loadConfiguration(file);
		for (Entry<String, Object> entry : data.entrySet())
		{
			try
			{
				// Make sure it's also defined in ArenaConfig. If not, an
				// Exception will be thrown
				ArenaConfig.class.getDeclaredField(entry.getKey());
				fc.set(entry.getKey(), entry.getValue());
			} catch (Exception e) { }
		}

		try
		{
			fc.save(file);
		} catch (Exception e) { }
	}

	@Override
	public Map<String, Object> serialize()
	{
		Map<String, Object> data = new HashMap<String, Object>();

		for (java.lang.reflect.Field field : getClass().getDeclaredFields())
		{
			if (Modifier.isTransient(field.getModifiers()))
				continue;

			// Configuration check

			try
			{
				// This works because if the field does not exist, a
				// NoSuchFieldException is thrown and we will never get to the
				// continue statement
				ArenaConfig.class.getDeclaredField(field.getName());
				continue;
			} catch (Exception e) { }

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
					if (((String) field.get(this)) != null)
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
			} catch (Exception e) { }
		}

		data.put("version", CURRENT_VERSION);
		return data;
	}

	@Override
	public void reload()
	{
		if (! file.exists())
		{
			if (plugin.getArena(arenaName) != null)
			{
				plugin.getArena(arenaName).stop();
			}

			plugin.getLoadedArenas().remove(this);
			return;
		}

		// Clear lists and maps
		this.blacklistedClasses.clear();
		this.whitelistedClasses.clear();
		this.killStreaks.clear();
		this.rewards.clear();
		this.spawns.clear();

		// Re-initialize
		initialize();
	}

	// ---- Generic Methods

	@Override
	public String toString()
	{
		return arenaName;
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof ArenaZone)
		{
			ArenaZone that = (ArenaZone) o;
			return this.arenaName.equals(that.arenaName);
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		return 71 * arenaName.hashCode();
	}
}