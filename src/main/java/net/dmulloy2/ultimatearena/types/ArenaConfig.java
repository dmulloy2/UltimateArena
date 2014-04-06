package net.dmulloy2.ultimatearena.types;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import lombok.Getter;
import lombok.Setter;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.util.ItemUtil;
import net.dmulloy2.ultimatearena.util.NumberUtil;
import net.dmulloy2.ultimatearena.util.Util;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

/**
 * @author dmulloy2
 */

@Getter @Setter
public class ArenaConfig implements Reloadable
{
	protected int gameTime, lobbyTime, maxDeaths, cashReward;
	protected int maxWave, maxPoints; // Arena-Specific

	protected boolean allowTeamKilling, countMobKills, rewardBasedOnXp, giveRewards;

	protected List<String> blacklistedClasses, whitelistedClasses;

	protected transient List<ItemStack> rewards;
	protected transient HashMap<Integer, List<KillStreak>> killStreaks;

	// ---- Transient
	protected transient String arenaName;
	protected transient boolean loaded;
	protected transient File file;

	protected transient final UltimateArena plugin;

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

			this.giveRewards = fc.getBoolean("giveRewards", true);

			List<String> xpBasedTypes = Arrays.asList(new String[]
					{
					"KOTH", "FFA", "CQ", "MOB", "CTF", "PVP", "BOMB"
					});

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
				this.killStreaks = KillStreak.defaultKillStreak(FieldType.getByName(arenaName.toUpperCase()));
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

	public final void save()
	{
		try
		{
			Map<String, Object> data = new HashMap<String, Object>();

			for (Field field : getClass().getDeclaredFields())
			{
				if (Modifier.isTransient(field.getModifiers()))
					continue;

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
			}

			YamlConfiguration fc = YamlConfiguration.loadConfiguration(file);
			for (Entry<String, Object> entry : data.entrySet())
			{
				fc.set(entry.getKey(), entry.getValue());
			}

			fc.save(file);
		} catch (Throwable ex) { }
	}

	@Override
	public void reload()
	{
		// Make sure this class still exists
		if (! file.exists())
		{
			plugin.getClasses().remove(this);
			return;
		}

		// Clear lists and maps
		this.blacklistedClasses.clear();
		this.whitelistedClasses.clear();
		this.killStreaks.clear();
		this.rewards.clear();

		// Load again
		load();
	}
}