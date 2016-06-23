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

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import lombok.Getter;
import lombok.Setter;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.ItemUtil;
import net.dmulloy2.util.MaterialUtil;
import net.dmulloy2.util.NumberUtil;
import net.dmulloy2.util.Util;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

/**
 * @author dmulloy2
 */

@Getter @Setter
public class ArenaConfig extends Configuration
{
	// ---- Generic
	protected boolean allowTeamKilling, countMobKills, canModifyWorld, unlimitedAmmo, rewardBasedOnXp, giveRewards, forceBalance,
		joinInProgress, preserveMobs;
	protected int gameTime, lobbyTime, maxDeaths, maxPlayers, minPlayers;
	protected String defaultClass;
	protected double cashReward;

	protected List<String> blacklistedClasses, whitelistedClasses;
	protected List<Material> clearMaterials;

	protected transient List<ItemStack> rewards;
	protected transient WinCondition winCondition;
	protected transient List<ScaledReward> scaledRewards;
	protected transient Map<Team, List<String>> mandatedClasses;
	protected transient Map<Integer, List<KillStreak>> killStreaks;

	// ---- Transient
	protected transient File file;
	protected transient String type;
	protected transient boolean loaded;

	protected transient final UltimateArena plugin;

	public ArenaConfig(UltimateArena plugin, String type, File file)
	{
		Validate.notNull(plugin, "plugin cannot be null!");
		Validate.notNull(type, "type cannot be null!");
		Validate.notNull(file, "file cannot be null!");

		this.type = type;
		this.file = file;
		this.plugin = plugin;

		// Set defaults
		this.setDefaults();

		// Load
		this.loaded = load();
	}

	public ArenaConfig(ArenaZone az)
	{
		Validate.notNull(az, "az cannot be null!");

		this.type = az.getName() + " - Config";
		this.plugin = az.getPlugin();
		this.file = az.getFile();
	}

	private final void setDefaults()
	{
		this.rewards = new ArrayList<>();
		this.scaledRewards = new ArrayList<>();
		this.mandatedClasses = new HashMap<>();
		this.clearMaterials = new ArrayList<>();
		this.blacklistedClasses = new ArrayList<>();
		this.whitelistedClasses = new ArrayList<>();
		this.killStreaks = getDefaultKillStreak();
		this.winCondition = WinCondition.LAST_MAN_STANDING;

		this.maxDeaths = 1;
		this.maxPlayers = 24;
		this.minPlayers = 1;
		this.unlimitedAmmo = true;
		this.rewardBasedOnXp = true;
		this.giveRewards = true;

		if (! plugin.getClasses().isEmpty())
			this.defaultClass = plugin.getClasses().get(0).getName();

		setCustomDefaults();
	}

	/**
	 * Sets any custom defaults. Called after {@link #setDefaults()}.
	 */
	protected void setCustomDefaults() { }

	/**
	 * Loads this config.
	 *
	 * @return True if loading was successful, false if not
	 */
	public final boolean load()
	{
		return load(file, this);
	}

	/**
	 * Loads this config from a given file.
	 *
	 * @param file File to load from
	 * @param def Parent config
	 * @return True if loading was successful, false if not
	 */
	public final boolean load(File file, ArenaConfig def)
	{
		Validate.isTrue(! loaded, "This config has already been loaded!");
		Validate.notNull(file, "file cannot be null!");
		Validate.notNull(def, "def cannot be null!");

		try
		{
			YamlConfiguration config = new YamlConfiguration();
			config.load(file);

			Map<String, Object> values = config.getValues(false);
			this.allowTeamKilling = getBoolean(values, "allowTeamKilling", def.isAllowTeamKilling());
			this.countMobKills = getBoolean(values, "countMobKills", def.isCountMobKills());
			this.canModifyWorld = getBoolean(values, "canModifyWorld", def.isCanModifyWorld());
			this.unlimitedAmmo = getBoolean(values, "unlimitedAmmo", def.isUnlimitedAmmo());
			this.rewardBasedOnXp = getBoolean(values, "rewardBasedOnXp", def.isRewardBasedOnXp());
			this.giveRewards = getBoolean(values, "giveRewards", def.isGiveRewards());
			this.forceBalance = getBoolean(values, "forceBalance", def.isForceBalance());
			this.joinInProgress = getBoolean(values, "joinInProgress", def.isJoinInProgress());

			if (this.preserveMobs = getBoolean(values, "preserveMobs", def.isPreserveMobs()))
				Global.mobPreservation = true;

			this.gameTime = getInt(values, "gameTime", def.getGameTime());
			this.lobbyTime = getInt(values, "lobbyTime", def.getLobbyTime());
			this.maxDeaths = Math.max(1, getInt(values, "maxDeaths", def.getMaxDeaths()));
			this.maxPlayers = getInt(values, "maxPlayers", def.getMaxPlayers());
			this.minPlayers = Math.max(1, getInt(values, "minPlayers", def.getMinPlayers()));

			this.defaultClass = getString(values, "defaultClass", def.getDefaultClass());

			this.cashReward = getDouble(values, "cashReward", def.getCashReward());

			WinCondition condition = WinCondition.fromConfig(getString(values, "winCondition", "default"));
			if (condition != null)
				this.winCondition = condition;

			if (giveRewards)
			{
				if (isSet(values, "rewards"))
				{
					this.rewards = ItemUtil.readItems(getStringList(values, "rewards"), plugin);
				}
				else
				{
					this.rewards = def.getRewards();
				}

				this.scaledRewards = new ArrayList<>();
				if (isSet(values, "scaledRewards"))
				{
					for (String string : getStringList(values, "scaledRewards"))
					{
						ScaledReward reward = ScaledReward.fromString(string);
						if (reward != null)
							scaledRewards.add(reward);
					}
				}
				else
				{
					if (rewardBasedOnXp)
					{
						for (ItemStack item : rewards)
						{
							ScaledReward reward = new ScaledReward(item, 200.0D);
							scaledRewards.add(reward);
						}
					}
					else
					{
						this.scaledRewards = def.getScaledRewards();
					}
				}
			}

			if (isSet(values, "clearMaterials"))
			{
				this.clearMaterials = MaterialUtil.fromStrings(getStringList(values, "clearMaterials"));
			}
			else
			{
				this.clearMaterials = def.getClearMaterials();
			}

			this.blacklistedClasses = getList(values, "blacklistedClasses", def.getBlacklistedClasses());
			this.whitelistedClasses = getList(values, "whitelistedClasses", def.getWhitelistedClasses());

			this.killStreaks = def.getKillStreaks();
			if (isSet(values, "killStreaks"))
			{
				this.killStreaks = new LinkedHashMap<>();

				for (Entry<String, Object> entry : getSection(values, "killStreaks").entrySet())
				{
					int kills = NumberUtil.toInt(entry.getKey());
					if (kills < 0)
						continue;

					List<KillStreak> streaks = new ArrayList<>();

					@SuppressWarnings("unchecked")
					List<String> list = (List<String>) entry.getValue();
					for (String string : list)
					{
						string = string.replaceAll("  ", " ");
						String[] split = string.split(",");

						// Determine type
						KillStreak.Type type = split[0].equalsIgnoreCase("mob") ? KillStreak.Type.MOB : KillStreak.Type.ITEM;

						// Load settings
						String message = split[1].trim();

						// Load specific settings
						if (type == KillStreak.Type.MOB)
						{
							EntityType entityType = EntityType.valueOf(split[2].toUpperCase());
							int amount = NumberUtil.toInt(split[3]);

							streaks.add(new KillStreak(kills, message, entityType, amount));
						}
						else
						{
							String item = FormatUtil.join(",", Arrays.copyOfRange(split, 2, split.length));
							ItemStack stack = ItemUtil.readItem(item, plugin);
							if (stack != null)
								streaks.add(new KillStreak(kills, message, stack));
						}
					}

					killStreaks.put(kills, streaks);
				}
			}

			this.mandatedClasses = def.getMandatedClasses();
			if (isSet(values, "mandatedClasses"))
			{
				this.mandatedClasses = new HashMap<>();

				for (Entry<String, Object> entry : getSection(values, "mandatedClasses").entrySet())
				{
					Team team = Team.get(entry.getKey());
					if (team == null)
						continue;

					@SuppressWarnings("unchecked")
					List<String> classes = (List<String>) entry.getValue();
					mandatedClasses.put(team, classes);
				}
			}

			// Load custom options
			loadCustomOptions(config, def);
		}
		catch (Throwable ex)
		{
			plugin.getLogHandler().log(Level.SEVERE, Util.getUsefulStack(ex, "loading config for \"" + type + "\""));
			return false;
		}

		plugin.getLogHandler().debug("Successfully loaded config for {0}!", type);
		return true;
	}

	/**
	 * Loads any custom options. Called after {@link #load(File, ArenaConfig)}
	 *
	 * @param fc FileConfiguration to load options from
	 * @param def Parent config
	 */
	protected void loadCustomOptions(YamlConfiguration fc, ArenaConfig def) { }

	/**
	 * Gets this config's default kill streak.
	 *
	 * @return Default kill streak
	 */
	public Map<Integer, List<KillStreak>> getDefaultKillStreak()
	{
		Map<Integer, List<KillStreak>> ret = new LinkedHashMap<>();

		ret.put(2, Arrays.asList(new KillStreak[] {
				new KillStreak(2, "&e2 &3kills! Unlocked strength potion!", ItemUtil.readPotion("strength, 1, 1, false"))
		}));

		ret.put(4, Arrays.asList(new KillStreak[] {
				new KillStreak(4, "&e4 &3kills! Unlocked health potion!", ItemUtil.readPotion("heal, 1, 1, false")),
				new KillStreak(4, "&e4 &3kills! Unlocked food!", new ItemStack(Material.GRILLED_PORK, 2))
		}));

		ret.put(5, Arrays.asList(new KillStreak[] {
				new KillStreak(5, "&e5 &3kills! Unlocked Zombies!", EntityType.ZOMBIE, 4)
		}));

		ret.put(8, Arrays.asList(new KillStreak[] {
				new KillStreak(8, "&e8 &3kills! Unlocked attack dogs!", EntityType.WOLF, 2)
		}));

		ret.put(12, Arrays.asList(new KillStreak[] {
				new KillStreak(12, "&e12 &3kills! Unlocked regen potion!", ItemUtil.readPotion("regen, 1, 1, false")),
				new KillStreak(12, "&e12 &3kills! Unlocked food!", new ItemStack(Material.GRILLED_PORK, 2))
		}));

		return ret;
	}

	/**
	 * Saves this config.
	 */
	public final void save()
	{
		save(file);
	}

	/**
	 * Saves this config to a given file.
	 *
	 * @param file File to save to
	 */
	public final void save(File file)
	{
		try
		{
			if (! file.exists())
				file.createNewFile();

			YamlConfiguration config = new YamlConfiguration();
			config.load(file);

			for (Entry<String, Object> entry : serialize().entrySet())
			{
				config.set(entry.getKey(), entry.getValue());
			}

			config.save(file);
		}
		catch (Throwable ex)
		{
			plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "saving config " + type));
		}
	}

	@Override
	public Map<String, Object> serialize()
	{
		Map<String, Object> data = new LinkedHashMap<>();

		for (Field field : ArenaConfig.class.getDeclaredFields())
		{
			try
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
			} catch (Throwable ex) { }
		}

		serializeCustomOptions(data);
		return data;
	}

	/**
	 * Serializes custom options. This should be overriden by subclasses.
	 *
	 * @param data Serialized data map
	 */
	protected void serializeCustomOptions(Map<String, Object> data) { }

	@Override
	public void reload()
	{
		// Just... don't delete and reload.
		if (! file.exists())
			return;

		// Reset defaults
		this.setDefaults();

		// Reload
		this.loaded = false;
		this.loaded = load();
	}

	/**
	 * Global registry of enabled config values. If a config value is never
	 * enabled, it isn't checked elsewhere and there's no performance hit.
	 * <p>
	 * <i>To be expanded</i>
	 * 
	 * @author dmulloy2
	 */
	public static class Global
	{
		public static boolean mobPreservation = false;
	}
}