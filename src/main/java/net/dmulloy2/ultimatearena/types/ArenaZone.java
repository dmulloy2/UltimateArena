package net.dmulloy2.ultimatearena.types;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

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
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author dmulloy2
 */

@Getter
@Setter
public class ArenaZone
{
	private int amtLobbys = 2;
	private int maxPlayers = 24;
	private int amtSpawnpoints = 2;
	
	private int liked;
	private int disliked;
	private int timesPlayed;

	private Material specialType;

	private boolean loaded;
	private boolean disabled;

	private String step;
	private String player;
	private String defaultClass;
	private String arenaName = "";
	private FieldType type;

	private Location lobby1;
	private Location lobby2;
	private Location arena1;
	private Location arena2;
	private Location team1spawn;
	private Location team2spawn;
	private Location lobbyREDspawn;
	private Location lobbyBLUspawn;

	private File file;

	private Field lobby;
	private Field arena;

	private List<String> voted = new ArrayList<String>();
	private List<Location> spawns = new ArrayList<Location>();
	private List<Location> flags = new ArrayList<Location>();

	private World world;

	private final UltimateArena plugin;

	/**
	 * Represents an ArenaZone to be loaded from configuration
	 * 
	 * @param plugin
	 *            - {@link UltimateArena} plugin instance
	 * @param file
	 *            - {@link File} to load
	 * @return new {@link ArenaZone}
	 */
	public ArenaZone(UltimateArena plugin, File file)
	{
		this.arenaName = getName(file);
		this.plugin = plugin;
		this.file = file;

		initialize();
	}

	/**
	 * Represents an ArenaZone to be created from an {@link ArenaCreator}
	 * 
	 * @param ac
	 *            - {@link ArenaCreator} to create arena from
	 * @return new {@link ArenaZone}
	 */
	public ArenaZone(UltimateArena plugin, ArenaCreator ac)
	{
		this.plugin = plugin;
		this.arenaName = ac.arenaName;
		this.type = ac.arenaType;
		this.lobbyBLUspawn = ac.lobbyBLUspawn;
		this.lobbyREDspawn = ac.lobbyREDspawn;
		this.team1spawn = ac.team1spawn;
		this.team2spawn = ac.team2spawn;
		this.lobby1 = ac.lobby1;
		this.lobby2 = ac.lobby2;
		this.arena1 = ac.arena1;
		this.arena2 = ac.arena2;
		this.spawns = ac.spawns;
		this.flags = ac.flags;
		this.maxPlayers = 24;
		this.specialType = ac.specialType;
		this.defaultClass = plugin.getClasses().get(0).getName();
		this.world = lobby1.getWorld();

		save();

		initialize();

		if (loaded)
		{
			plugin.outConsole("Creation of Arena {0} successful!", arenaName);
		}
		else
		{
			plugin.outConsole(Level.WARNING, "Creation of Arena {0} has failed!", arenaName);
		}
	}

	public void initialize()
	{
		this.lobby = new Field();
		this.arena = new Field();

		ArenaClass ac = plugin.getClasses().get(0);
		if (ac != null)
			this.defaultClass = ac.getName();

		load();

		if (isLoaded())
		{
			lobby.setParam(lobby1.getWorld(), lobby1.getBlockX(), lobby1.getBlockZ(), lobby2.getBlockX(), lobby2.getBlockZ());
			arena.setParam(arena1.getWorld(), arena1.getBlockX(), arena1.getBlockZ(), arena2.getBlockX(), arena2.getBlockZ());

			plugin.getLoadedArenas().add(this);
		}
		else
		{
			plugin.outConsole(Level.WARNING, "Arena {0} has failed to load!", arenaName);
		}
	}

	public boolean checkLocation(Location loc)
	{
		return lobby.isInside(loc) || arena.isInside(loc);
	}

	public void save()
	{
		plugin.getFileHandler().save(this);
	}

	public void load()
	{
		plugin.getFileHandler().load(this);
		
		loadConfiguration();
	}

	public boolean canLike(Player player)
	{
		return ! voted.contains(player.getName());
	}

	public String getName(File file)
	{
		return file.getName().replaceAll(".dat", "");
	}
	
	public List<String> getStats()
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
	
	// ---- Configuration ---- //
	private int gameTime, lobbyTime, maxDeaths, maxWave, cashReward, maxPoints;

	private boolean allowTeamKilling, countMobKills, rewardBasedOnXp;
	
	private List<String> blacklistedClasses, whitelistedClasses;
	
	private List<ItemStack> rewards;

	private HashMap<Integer, List<KillStreak>> killStreaks;
	
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
				rewards.addAll(conf.getRewards());
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
				double money = cashReward * (ap.getGameXP() / 100.0D);
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
}