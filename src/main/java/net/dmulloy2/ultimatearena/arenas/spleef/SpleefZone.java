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
package net.dmulloy2.ultimatearena.arenas.spleef;

import java.io.File;
import java.util.Map;

import net.dmulloy2.ultimatearena.api.ArenaType;
import net.dmulloy2.ultimatearena.types.ArenaZone;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import lombok.Getter;
import lombok.Setter;

/**
 * @author dmulloy2
 */

@Getter @Setter
public class SpleefZone extends ArenaZone
{
	private String specialTypeString = "SNOW_BLOCK";
	private Material specialType = Material.SNOW_BLOCK;

	public SpleefZone(ArenaType type)
	{
		super(type);
	}

	public SpleefZone(ArenaType type, File file)
	{
		super(type, file);
	}

	public final void setSpecialType(Material material)
	{
		this.specialType = material;
		this.specialTypeString = material.name();
	}

	@Override
	public void loadCustomOptions(YamlConfiguration config)
	{
		this.specialTypeString = config.getString("specialTypeString");
		this.specialType = Material.matchMaterial(specialTypeString);
	}

	@Override
	public Map<String, Object> serialize()
	{
		Map<String, Object> ret = super.serialize();
		ret.put("specialTypeString", specialTypeString);
		return ret;
	}
}
