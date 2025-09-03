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

import lombok.Getter;

import java.io.File;
import java.util.*;
import java.util.logging.Level;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;

import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.potion.PotionType;

import net.dmulloy2.swornapi.util.NumberUtil;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.types.ArenaConfig;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.KillStreak;
import net.kyori.adventure.key.Key;

/**
 * @author dmulloy2
 */

@Getter
public class MobConfig extends ArenaConfig
{
	private int maxWave;
	private Map<Integer, List<EntityType>> waves;

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

	private Map<Integer, List<EntityType>> defaultWaves()
	{
		Map<Integer, List<EntityType>> ret = new LinkedHashMap<>();
		ret.put(1, List.of(EntityType.ZOMBIE, EntityType.ZOMBIE, EntityType.ZOMBIE));
		ret.put(2, List.of(EntityType.ZOMBIE, EntityType.ZOMBIE, EntityType.SKELETON));
		ret.put(4, List.of(EntityType.SPIDER));
		ret.put(7, List.of(EntityType.BLAZE, EntityType.BLAZE));
		ret.put(10, List.of(EntityType.ZOMBIFIED_PIGLIN, EntityType.ENDERMAN));
		ret.put(13, List.of(EntityType.GHAST));
		return ret;
	}

	@Override
	public void loadCustomOptions(YamlConfiguration fc, ArenaConfig def)
	{
		this.maxWave = fc.getInt("maxWave", ((MobConfig) def).getMaxWave());

		if (!fc.isSet("waves"))
		{
			this.waves = ((MobConfig) def).getWaves();
			return;
		}

		this.waves = new LinkedHashMap<>();

		Registry<EntityType> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENTITY_TYPE);

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
			List<EntityType> mobs = new ArrayList<>();
			for (String mob : section.getStringList(wave))
			{
				mob = mob.toLowerCase().replace(" ", "_");

				EntityType entityType = null;

				try
				{
					entityType = registry.get(Key.key(mob));
				} catch (Exception ignored) {}

				if (entityType != null)
				{
					mobs.add(entityType);
				}
				else
				{
					plugin.getLogHandler().log(Level.WARNING, "Invalid entity type: {0}", mob);
				}
			}

			waves.put(waveNumber, mobs);
		}
	}

	@Override
	public Map<Integer, List<KillStreak>> getDefaultKillStreak()
	{
		Map<Integer, List<KillStreak>> ret = new LinkedHashMap<>();

		ret.put(8, List.of(new KillStreak(8, "&e8 &3kills! Unlocked strength potion!",
			ItemType.POTION.createItemStack(potionMeta -> potionMeta.setBasePotionType(PotionType.STRENGTH)))));

		ret.put(12, List.of(new KillStreak(12, "&e12 &3kills! Unlocked swiftness potion!",
			ItemType.POTION.createItemStack(potionMeta -> potionMeta.setBasePotionType(PotionType.SWIFTNESS)))));

		ret.put(16, List.of(new KillStreak(16, "&e16 &3kills! Unlocked Anti-Fire!",
			ItemType.POTION.createItemStack(potionMeta -> potionMeta.setBasePotionType(PotionType.FIRE_RESISTANCE)))));

		ret.put(24, List.of(
			new KillStreak(24, "&e24 &3kills! Unlocked health potion!",
				ItemType.POTION.createItemStack(potionMeta -> potionMeta.setBasePotionType(PotionType.HEALING))),
			new KillStreak(24, "&e24 &3kills! Unlocked food!",
				new ItemStack(Material.COOKED_PORKCHOP, 2))
		));

		ret.put(32, List.of(new KillStreak(24, "&e32 &3kills! Unlocked attack dogs!", EntityType.WOLF, 3)));

		ret.put(40, List.of(
			new KillStreak(40, "&e40 &3kills! Unlocked regen potion!",
				ItemType.POTION.createItemStack(potionMeta -> potionMeta.setBasePotionType(PotionType.REGENERATION))),
			new KillStreak(40, "&e40 &3kills! Unlocked food!",
				new ItemStack(Material.COOKED_PORKCHOP, 2))
		));

		ret.put(72, List.of(new KillStreak(72, "&e72 &3kills! Unlocked Golden Apples!", new ItemStack(Material.GOLDEN_APPLE, 2))));

		ret.put(112, List.of(new KillStreak(112, "&e112 &3kills! Unlocked Golden Apples!", new ItemStack(Material.GOLDEN_APPLE, 2))));

		return ret;
	}

	@Override
	public void serializeCustomOptions(Map<String, Object> data)
	{
		data.put("maxWave", maxWave);
	}
}
