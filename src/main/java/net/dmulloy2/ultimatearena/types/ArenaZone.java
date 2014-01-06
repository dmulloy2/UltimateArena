package net.dmulloy2.ultimatearena.types;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lombok.Getter;
import lombok.Setter;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.util.FormatUtil;
import net.dmulloy2.ultimatearena.util.InventoryHelper;
import net.dmulloy2.ultimatearena.util.ItemUtil;
import net.dmulloy2.ultimatearena.util.Util;
import net.milkbowl.vault.economy.EconomyResponse;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author dmulloy2
 */

@Getter
@Setter
public class ArenaZone implements Reloadable, ConfigurationSerializable
{
	private int maxPlayers = 24;

	private int liked;
	private int disliked;
	private int timesPlayed;

	private Material specialType;

	private boolean disabled;
	private transient boolean loaded;

	private String worldName;
	private String arenaName = "";
	private String defaultClass;

	private FieldType type;

	private ArenaLocation lobby1;
	private ArenaLocation lobby2;
	private ArenaLocation arena1;
	private ArenaLocation arena2;
	private ArenaLocation team1spawn;
	private ArenaLocation team2spawn;
	private ArenaLocation lobbyREDspawn;
	private ArenaLocation lobbyBLUspawn;

	private transient File file;

	private transient Field lobby;
	private transient Field arena;

	private transient List<String> voted = new ArrayList<String>();

	private List<ArenaLocation> spawns = new ArrayList<ArenaLocation>();
	private List<ArenaLocation> flags = new ArrayList<ArenaLocation>();

	private transient World world;

	private transient final UltimateArena plugin;

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
		load();

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
			lobby.setParam(lobby1.getWorld(), lobby1.getX(), lobby1.getZ(), lobby2.getX(), lobby2.getZ());
			arena.setParam(arena1.getWorld(), arena1.getX(), arena1.getZ(), arena2.getX(), arena2.getZ());

			// Add to the loaded arenas list
			plugin.getLoadedArenas().add(this);
		}

		return loaded;
	}

	public final void save()
	{
		// Make sure we have a valid file
		checkFile();

		// Actually save
		plugin.getFileHandler().save(this);
	}

	public final void load()
	{
		// Make sure we have a valid file
		checkFile();

		// Load the core settings
		plugin.getFileHandler().load(this);
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
		return lobby.isInside(loc) || arena.isInside(loc);
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

	// ---- Configuration ---- //

	// Serializable variables
	private int gameTime, lobbyTime, maxDeaths, maxWave, cashReward, maxPoints;

	private boolean allowTeamKilling, countMobKills, rewardBasedOnXp;

	private List<String> blacklistedClasses, whitelistedClasses;

	// These have to be manually loaded, since they are not serializable
	private transient List<ItemStack> rewards;
	private transient HashMap<Integer, List<KillStreak>> killStreaks;

	// TODO: Rewrite this to account for missing variables when we switch to ConfigurationSerialization
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
					int kills = Util.parseInt(entry.getKey());
					if (kills < 0)
						continue;

					@SuppressWarnings("unchecked") // No way to check this :I
					List<String> values = (List<String>) entry.getValue();

					List<KillStreak> streaks = new ArrayList<KillStreak>();
					for (String value : values)
					{
						// Determine type
						String s = value.substring(0, value.indexOf(","));

						KillStreak.Type type = null;
						if (s.equalsIgnoreCase("mob"))
							type = KillStreak.Type.MOB;
						else if (s.equalsIgnoreCase("item"))
							type = KillStreak.Type.ITEM;

						if (type == KillStreak.Type.MOB)
						{
							String[] split = value.split(",");

							String message = split[1];
							EntityType entityType = EntityType.valueOf(split[2]);
							int amount = Integer.parseInt(split[3]);

							streaks.add(new KillStreak(kills, message, entityType, amount));
							continue;
						}
						else if (type == KillStreak.Type.ITEM)
						{
							// Yay substring and indexof!
							s = value.substring(value.indexOf(",") + 1);

							String message = s.substring(0, s.indexOf(","));

							s = s.substring(s.indexOf(",") + 1);

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
				InventoryHelper.addItem(player, stack);
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
						player.sendMessage(plugin.getPrefix() + 
								FormatUtil.format("&a{0} has been added to your account!", format));
					}
					else
					{
						player.sendMessage(plugin.getPrefix() + 
								FormatUtil.format("&cCould not give cash reward: {0}", er.errorMessage));
					}
				}
			}
		}
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Map<String, Object> serialize()
	{
		Map<String, Object> data = new HashMap<String, Object>();

		for (java.lang.reflect.Field field : getClass().getDeclaredFields())
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
					if (! ((Collection) field.get(this)).isEmpty())
						data.put(field.getName(), field.get(this));
				}
				else if (field.getType().isAssignableFrom(String.class))
				{
					if (((String) field.get(this)) != null)
						data.put(field.getName(), field.get(this));
				}
				else if (field.getType().isAssignableFrom(Map.class))
				{
					if (! ((Map) field.get(this)).isEmpty())
						data.put(field.getName(), field.get(this));
				}
				else
				{
					if (field.get(this) != null)
						data.put(field.getName(), field.get(this));
				}

				field.setAccessible(accessible);

			}
			catch (Exception e)
			{
				//
			}
		}

		return data;
	}

	@Override
	public void reload()
	{
		// Clear lists and maps
		this.blacklistedClasses.clear();
		this.whitelistedClasses.clear();
		this.killStreaks.clear();
		this.rewards.clear();
		this.spawns.clear();

		// Re-initialize
		initialize();
	}
}