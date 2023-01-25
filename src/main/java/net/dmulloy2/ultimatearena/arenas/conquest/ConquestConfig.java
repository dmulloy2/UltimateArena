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
package net.dmulloy2.ultimatearena.arenas.conquest;

import java.io.File;
import java.util.*;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.types.ArenaConfig;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.KillStreak;
import net.dmulloy2.swornapi.util.ItemUtil;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

/**
 * @author dmulloy2
 */

public class ConquestConfig extends ArenaConfig
{
	public ConquestConfig(UltimateArena plugin, String type, File file)
	{
		super(plugin, type, file);
	}

	public ConquestConfig(ArenaZone az)
	{
		super(az);
	}

	@Override
	public Map<Integer, List<KillStreak>> getDefaultKillStreak() {
		Map<Integer, List<KillStreak>> ret = new LinkedHashMap<>();

		// Omit zombies
		ret.put(2, Collections.singletonList(
				new KillStreak(2, "&e2 &3kills! Unlocked strength potion!", ItemUtil.readPotion("strength, 1, 1, false"))));

		ret.put(4, Arrays.asList(
				new KillStreak(4, "&e4 &3kills! Unlocked health potion!", ItemUtil.readPotion("heal, 1, 1, false")),
				new KillStreak(4, "&e4 &3kills! Unlocked food!", new ItemStack(Material.COOKED_PORKCHOP, 2))));

		ret.put(8, Collections.singletonList(
				new KillStreak(8, "&e8 &3kills! Unlocked attack dogs!", EntityType.WOLF, 2)));

		ret.put(12, Arrays.asList(
				new KillStreak(12, "&e12 &3kills! Unlocked regen potion!", ItemUtil.readPotion("regen, 1, 1, false")),
				new KillStreak(12, "&e12 &3kills! Unlocked food!", new ItemStack(Material.COOKED_PORKCHOP, 2))));

		return ret;
	}
}
