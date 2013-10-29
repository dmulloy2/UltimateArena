package net.dmulloy2.ultimatearena.types;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import lombok.Getter;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.util.ItemUtil;
import net.dmulloy2.ultimatearena.util.Util;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

/**
 * @author dmulloy2
 */

@Getter
public class ArenaConfig
{
	private int gameTime, lobbyTime, maxDeaths, maxWave, cashReward, maxPoints;

	private boolean allowTeamKilling, countMobKills, rewardBasedOnXp, loaded;

	private List<String> blacklistedClasses, whitelistedClasses;

	private List<ItemStack> rewards;

	private HashMap<Integer, List<KillStreak>> killStreaks;

	private String arenaName;
	private File file;
	private final UltimateArena plugin;

	public ArenaConfig(UltimateArena plugin, String str, File file)
	{
		this.arenaName = str;
		this.file = file;
		this.plugin = plugin;

		this.loaded = load();
		if (! loaded)
		{
			plugin.outConsole(Level.SEVERE, "Could not load config for " + arenaName + "!");
		}
	}

	public boolean load()
	{
		try
		{
			YamlConfiguration fc = YamlConfiguration.loadConfiguration(file);
			if (arenaName.equalsIgnoreCase("mob"))
			{
				this.maxWave = fc.getInt("maxWave");
			}

			if (arenaName.equalsIgnoreCase("koth"))
			{
				this.maxPoints = fc.getInt("maxPoints", 60);
			}

			this.gameTime = fc.getInt("gameTime");
			this.lobbyTime = fc.getInt("lobbyTime");
			this.maxDeaths = fc.getInt("maxDeaths");
			this.allowTeamKilling = fc.getBoolean("allowTeamKilling");
			this.cashReward = fc.getInt("cashReward");
			this.countMobKills = fc.getBoolean("countMobKills", arenaName.equalsIgnoreCase("mob"));

			this.rewards = new ArrayList<ItemStack>();
			for (String reward : fc.getStringList("rewards"))
			{
				ItemStack stack = ItemUtil.readItem(reward);
				if (stack != null)
					rewards.add(stack);
			}

			List<String> xpBasedTypes = Arrays.asList(new String[] { "KOTH", "FFA", "CQ", "MOB", "CTF", "PVP", "BOMB" });

			this.rewardBasedOnXp = fc.getBoolean("rewardBasedOnXp", xpBasedTypes.contains(arenaName.toUpperCase()));

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
				this.killStreaks = KillStreak.defaultKillStreak(FieldType.valueOf(arenaName.toUpperCase()));
			}
		}
		catch (Exception e)
		{
			plugin.outConsole(Level.SEVERE, Util.getUsefulStack(e, "loading config for \"" + arenaName + "\""));
			return false;
		}

		plugin.debug("Loaded ArenaConfig for type: {0}!", arenaName);
		return true;
	}
}