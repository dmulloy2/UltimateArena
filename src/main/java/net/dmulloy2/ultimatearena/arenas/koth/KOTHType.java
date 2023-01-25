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
package net.dmulloy2.ultimatearena.arenas.koth;

import java.io.File;

import net.dmulloy2.ultimatearena.api.ArenaDescription;
import net.dmulloy2.ultimatearena.api.ArenaType;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaConfig;
import net.dmulloy2.ultimatearena.types.ArenaCreator;
import net.dmulloy2.ultimatearena.types.ArenaZone;

import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class KOTHType extends ArenaType
{
	@Override
	public ArenaCreator newCreator(Player player, String name)
	{
		return new KOTHCreator(player, name, this);
	}

	@Override
	public Arena newArena(ArenaZone az)
	{
		return new KOTHArena(az);
	}

	@Override
	public ArenaConfig newConfig()
	{
		String name = getName().toLowerCase();
		return new KOTHConfig(getPlugin(), name, new File(getDataFolder(), "config.yml"));
	}

	@Override
	public ArenaConfig newConfig(ArenaZone az)
	{
		return new KOTHConfig(az);
	}

	protected ArenaDescription description;

	@Override
	public ArenaDescription getDescription()
	{
		if (description == null)
			description = new KOTHDescription();

		return description;
	}

	public class KOTHDescription extends ArenaDescription
	{
		public KOTHDescription()
		{
			this.name = "koth";
			this.main = "net.dmulloy2.arenas.koth.KOTHType";
			this.stylized = "KOTH";
			this.version = "1.0";
			this.author = "dmulloy2";
		}
	}
}
