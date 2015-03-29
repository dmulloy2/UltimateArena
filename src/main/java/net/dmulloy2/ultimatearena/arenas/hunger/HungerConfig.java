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
package net.dmulloy2.ultimatearena.arenas.hunger;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.types.ArenaConfig;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.KillStreak;

/**
 * @author dmulloy2
 */

public class HungerConfig extends ArenaConfig
{
	public HungerConfig(UltimateArena plugin, String type, File file)
	{
		super(plugin, type, file);
	}

	public HungerConfig(ArenaZone az)
	{
		super(az);
	}

	@Override
	protected void setCustomDefaults()
	{
		this.canModifyWorld = true;
		this.unlimitedAmmo = false;
		this.rewardBasedOnXp = false;
	}

	@Override
	public Map<Integer, List<KillStreak>> getDefaultKillStreak()
	{
		// No kill streaks
		return new LinkedHashMap<>();
	}
}
