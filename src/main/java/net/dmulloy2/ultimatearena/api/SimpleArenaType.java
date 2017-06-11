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
package net.dmulloy2.ultimatearena.api;

import java.io.File;
import java.lang.reflect.Constructor;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaConfig;
import net.dmulloy2.ultimatearena.types.ArenaCreator;
import net.dmulloy2.ultimatearena.types.ArenaZone;

import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;

/**
 * Represents a simplified ArenaType that automatically constructs objects based
 * on class input.
 * 
 * @author dmulloy2
 */

public abstract class SimpleArenaType extends ArenaType
{
	/**
	 * Gets the ArenaZone class for this ArenaType.
	 * @return The ArenaZone class.
	 */
	public Class<? extends ArenaZone> getArenaZone()
	{
		return ArenaZone.class;
	}

	/**
	 * Gets the ArenaConfig class for this ArenaType.
	 * @return The ArenaConfig class.
	 */
	public Class<? extends ArenaConfig> getArenaConfig()
	{
		return ArenaConfig.class;
	}

	/**
	 * Gets the ArenaCreator class for this ArenaType.
	 * @return The ArenaCreator class.
	 */
	public abstract Class<? extends ArenaCreator> getArenaCreator();

	/**
	 * Gets the Arena class for this ArenaType.
	 * @return The Arena class.
	 */
	public abstract Class<? extends Arena> getArena();

	// ---- Proxy Methods

	@Override
	public ArenaZone getArenaZone(File file)
	{
		Class<? extends ArenaZone> clazz = getArenaZone();
		Validate.notNull(clazz, "ArenaZone class cannot be null!");

		try
		{
			Constructor<? extends ArenaZone> constructor = clazz.getConstructor(getClass(), File.class);
			return constructor.newInstance(this, file);
		}
		catch (Throwable ex)
		{
			throw new RuntimeException("Failed to create new " + clazz.getName() + " instance.", ex);
		}
	}

	@Override
	public ArenaConfig newConfig()
	{
		Class<? extends ArenaConfig> clazz = getArenaConfig();
		Validate.notNull(clazz, "ArenaConfig class cannot be null!");

		String name = getName().toLowerCase();
		File file = new File(getDataFolder(), "config.yml");

		try
		{
			Constructor<? extends ArenaConfig> constructor = clazz.getConstructor(UltimateArena.class, String.class, File.class);
			return constructor.newInstance(getPlugin(), name, file);
		}
		catch (Throwable ex)
		{
			throw new RuntimeException("Failed to create new " + clazz.getName() + " instance.", ex);
		}
	}

	@Override
	public ArenaConfig newConfig(ArenaZone az)
	{
		Class<? extends ArenaConfig> clazz = getArenaConfig();
		Validate.notNull(clazz, "ArenaConfig class cannot be null!");
		
		try
		{
			Constructor<? extends ArenaConfig> constructor = clazz.getConstructor(ArenaZone.class);
			return constructor.newInstance(az);
		}
		catch (Throwable ex)
		{
			throw new RuntimeException("Failed to create new " + clazz.getName() + " instance.", ex);
		}
	}

	@Override
	public ArenaCreator newCreator(Player player, String name)
	{
		Class<? extends ArenaCreator> clazz = getArenaCreator();
		Validate.notNull(clazz, "ArenaCreator class cannot be null!");

		try
		{
			Constructor<? extends ArenaCreator> constructor = clazz.getConstructor(UltimateArena.class, Player.class, String.class);
			return constructor.newInstance(getPlugin(), player, name);
		}
		catch (Throwable ex)
		{
			throw new RuntimeException("Failed to create new " + clazz.getName() + " instance.", ex);
		}
	}

	@Override
	public Arena newArena(ArenaZone az)
	{
		Class<? extends Arena> clazz = getArena();
		Validate.notNull(clazz, "Arena class cannot be null!");

		try
		{
			Constructor<? extends Arena> constructor = clazz.getConstructor(ArenaZone.class);
			return constructor.newInstance(az);
		}
		catch (Throwable ex)
		{
			throw new RuntimeException("Failed to create new " + clazz.getName() + " instance.", ex);
		}
	}
}