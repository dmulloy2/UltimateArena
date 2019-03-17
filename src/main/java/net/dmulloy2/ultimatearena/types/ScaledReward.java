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

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dmulloy2.util.ItemUtil;
import net.dmulloy2.util.NumberUtil;

import org.bukkit.inventory.ItemStack;

/**
 * @author dmulloy2
 */

@Getter
@AllArgsConstructor
public class ScaledReward
{
	private final ItemStack item;
	private final double scale;

	public ItemStack get(int xp)
	{
		ItemStack ret = item.clone();
		ret.setAmount((int) (xp / scale));
		return ret;
	}

	public static ScaledReward fromString(String string)
	{
		try
		{
			string = string.replaceAll(" ", "");
			ItemStack item = ItemUtil.readItem(string.substring(0, string.lastIndexOf(",")));
			double scale = NumberUtil.toDouble(string.substring(string.lastIndexOf(",") + 1));
			if (item != null && scale != -1)
				return new ScaledReward(item, scale);
		} catch (Throwable ignored) { }
		return null;
	}
}
