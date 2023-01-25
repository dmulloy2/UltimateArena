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

public class ConquestType extends ArenaType
{
	@Override
	public ArenaCreator newCreator(Player player, String name)
	{
		return new ConquestCreator(player, name, this);
	}

	@Override
	public Arena newArena(ArenaZone az)
	{
		return new ConquestArena(az);
	}

	@Override
	public ArenaConfig newConfig()
	{
		String name = getName().toLowerCase();
		return new ConquestConfig(getPlugin(), name, new File(getDataFolder(), "config.yml"));
	}

	@Override
	public ArenaConfig newConfig(ArenaZone az)
	{
		return new ConquestConfig(az);
	}

	private ArenaDescription description;

	@Override
	public ArenaDescription getDescription()
	{
		if (description == null)
			description = new ConquestDescription();

		return description;
	}

	public class ConquestDescription extends ArenaDescription
	{
		public ConquestDescription()
		{
			this.name = "cq";
			this.main = "net.dmulloy2.arenas.conquest.Conquest";
			this.stylized = "Conquest";
			this.version = "1.0";
			this.author = "dmulloy2";
		}
	}
}
