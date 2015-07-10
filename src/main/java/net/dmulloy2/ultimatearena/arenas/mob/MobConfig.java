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
package net.dmulloy2.ultimatearena.arenas.mob;

import static net.dmulloy2.util.ListUtil.toList;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import lombok.Getter;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.types.ArenaConfig;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.KillStreak;
import net.dmulloy2.util.ItemUtil;
import net.dmulloy2.util.NumberUtil;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

/**
 * @author dmulloy2
 */

@Getter
public class MobConfig extends ArenaConfig
{
	private int maxWave;
	private Map<Integer, List<String>> waves;

	public MobConfig(UltimateArena plugin, String type, File file)
	{
		super(plugin, type, file);
	}

	public MobConfig(ArenaZone az)
	{
		super(az);
	}

	@Override
	protected void setCustomDefaults()
	{
		this.countMobKills = true;
		this.maxWave = 15;
		this.waves = defaultWaves();
	}

	private Map<Integer, List<String>> defaultWaves()
	{
		Map<Integer, List<String>> ret = new LinkedHashMap<>();
		ret.put(1, toList("ZOMBIE", "ZOMBIE", "ZOMBIE"));
		ret.put(2, toList("ZOMBIE", "ZOMBIE", "SKELETON"));
		ret.put(4, toList("SPIDER"));
		ret.put(7, toList("BLAZE", "BLAZE"));
		ret.put(10, toList("PIG_ZOMBIE", "ENDERMAN"));
		ret.put(13, toList("GHAST"));
		return ret;
	}

	@Override
	public void loadCustomOptions(YamlConfiguration fc, ArenaConfig def)
	{
		this.maxWave = fc.getInt("maxWave", ((MobConfig) def).getMaxWave());

		if (fc.isSet("waves"))
		{
			this.waves = new LinkedHashMap<>();

			ConfigurationSection section = fc.getConfigurationSection("waves");
			Set<String> keys = section.getKeys(false);
			for (String wave : keys)
			{
				int waveNumber = NumberUtil.toInt(wave);
				if (waveNumber == -1)
				{
					plugin.getLogHandler().log(Level.WARNING, "Invalid wave number: {0}", wave);
					continue;
				}

				// Validate the mobs list
				List<String> mobs = new ArrayList<>();
				for (String mob : section.getStringList(wave))
				{
					mob = mob.toUpperCase().replace(" ", "_");

					try
					{
						EntityType.valueOf(mob);
						mobs.add(mob);
					}
					catch (IllegalArgumentException ex)
					{
						plugin.getLogHandler().log(Level.WARNING, "Invalid entity type: {0}", mob);
					}
				}

				waves.put(waveNumber, mobs);
			}
		}
		else
		{
			this.waves = ((MobConfig) def).getWaves();
		}
	}

	@Override
	public Map<Integer, List<KillStreak>> getDefaultKillStreak()
	{
		Map<Integer, List<KillStreak>> ret = new LinkedHashMap<>();

		ret.put(8, Arrays.asList(new KillStreak[] {
				new KillStreak(8, "&e8 &3kills! Unlocked strength potion!", ItemUtil.readPotion("strength, 1, 1, false"))
		}));

		ret.put(12, Arrays.asList(new KillStreak[] {
				new KillStreak(12, "&e12 &3kills! Unlocked swiftness potion!", ItemUtil.readPotion("strength, 1, 1, false"))
		}));

		ret.put(16, Arrays.asList(new KillStreak[] {
				new KillStreak(16, "&e16 &3kills! Unlocked Anti-Fire!", ItemUtil.readPotion("fireres, 1, 1, false"))
		}));

		ret.put(24, Arrays.asList(new KillStreak[] {
				new KillStreak(24, "&e24 &3kills! Unlocked health potion!", ItemUtil.readPotion("heal, 1, 1, false")),
				new KillStreak(24, "&e24 &3kills! Unlocked food!", new ItemStack(Material.GRILLED_PORK, 2))
		}));

		ret.put(32, Arrays.asList(new KillStreak[] {
				new KillStreak(24, "&e32 &3kills! Unlocked attack dogs!", EntityType.WOLF, 3)
		}));

		ret.put(40, Arrays.asList(new KillStreak[] {
				new KillStreak(40, "&e40 &3kills! Unlocked regen potion!", ItemUtil.readPotion("regen, 1, 1, false")),
				new KillStreak(40, "&e40 &3kills! Unlocked food!", new ItemStack(Material.GRILLED_PORK, 2))
		}));

		ret.put(72, Arrays.asList(new KillStreak[] {
				new KillStreak(72, "&e72 &3kills! Unlocked Golden Apples!", new ItemStack(Material.GOLDEN_APPLE, 2))
		}));

		ret.put(112, Arrays.asList(new KillStreak[] {
				new KillStreak(112, "&e112 &3kills! Unlocked Golden Apples!", new ItemStack(Material.GOLDEN_APPLE, 2))
		}));

		return ret;
	}

	@Override
	public void serializeCustomOptions(Map<String, Object> data)
	{
		data.put("maxWave", maxWave);
	}
}
