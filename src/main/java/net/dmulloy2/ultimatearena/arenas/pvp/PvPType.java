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
package net.dmulloy2.ultimatearena.arenas.pvp;

import net.dmulloy2.ultimatearena.api.ArenaDescription;
import net.dmulloy2.ultimatearena.api.ArenaType;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaCreator;
import net.dmulloy2.ultimatearena.types.ArenaZone;

import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class PvPType extends ArenaType
{
	@Override
	public ArenaCreator newCreator(Player player, String name)
	{
		return new PvPCreator(player, name, this);
	}

	@Override
	public Arena newArena(ArenaZone az)
	{
		return new PvPArena(az);
	}

	protected ArenaDescription description;

	@Override
	public ArenaDescription getDescription()
	{
		if (description == null)
			description = new PvPDescription();

		return description;
	}

	public class PvPDescription extends ArenaDescription
	{
		public PvPDescription()
		{
			this.name = "pvp";
			this.main = "net.dmulloy2.arenas.pvp.PvPType";
			this.stylized = "PvP";
			this.version = "1.0";
			this.author = "dmulloy2";
		}
	}
}
