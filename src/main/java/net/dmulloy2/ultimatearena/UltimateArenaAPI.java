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
package net.dmulloy2.ultimatearena;

import java.util.Collections;
import java.util.List;

import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaClass;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.LeaveReason;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.utility.Util;

/**
 * Basic API for hooking into {@link UltimateArena}
 *
 * @author dmulloy2
 */

public class UltimateArenaAPI
{
	protected final UltimateArena ultimateArena;
	protected final Plugin accessingPlugin;

	private UltimateArenaAPI(UltimateArena ua, Plugin plugin)
	{
		this.ultimateArena = ua;
		this.accessingPlugin = plugin;
	}

	/**
	 * Whether or not a {@link Player} is playing an {@link Arena}.
	 *
	 * @param player {@link Player} in question
	 */
	public final boolean isInArena(Player player)
	{
		return ultimateArena.isInArena(player);
	}

	/**
	 * Gets whether or not a {@link Location} is in an {@link Arena}.
	 *
	 * @param loc {@link Location} in question
	 */
	public final boolean isInArena(Location loc)
	{
		return ultimateArena.isInArena(loc);
	}

	/**
	 * Returns a {@link Player}'s {@link ArenaPlayer} instance.
	 *
	 * @param player {@link Player} instance
	 * @return The player's {@link ArenaPlayer} instance, may be null
	 */
	public final ArenaPlayer getArenaPlayer(Player player)
	{
		return ultimateArena.getArenaPlayer(player);
	}

	/**
	 * Gets all active {@link Arena}s. The list returned is not modifiable.
	 *
	 * @return An unmodifiable {@link List} of all active {@link Arena}s
	 */
	public final List<Arena> getActiveArenas()
	{
		return Util.asList(ultimateArena.getActiveArenas());
	}

	/**
	 * Gets all loaded {@link ArenaZone}s. The list returned is not modifiable.
	 *
	 * @return An unmodifiable {@link List} of all loaded {@link ArenaZone}s
	 */
	public final List<ArenaZone> getLoadedArenas()
	{
		return Collections.unmodifiableList(ultimateArena.getLoadedArenas());
	}

	/**
	 * Gets all loaded {@link ArenaClass}es. The list returned is not modifiable.
	 *
	 * @return An unmodifiable {@link List} of all loaded {@link ArenaClass}es
	 */
	public final List<ArenaClass> getClasses()
	{
		return Collections.unmodifiableList(ultimateArena.getClasses());
	}

	/**
	 * Gets an {@link ArenaClass} by name.
	 *
	 * @param name Name of the {@link ArenaClass}
	 * @return {@link ArenaClass} based upon name
	 */
	public final ArenaClass getArenaClass(String name)
	{
		return ultimateArena.getArenaClass(name);
	}

	/**
	 * Gets an {@link ArenaZone} by name.
	 *
	 * @param name Name of the {@link ArenaZone}
	 * @return {@link ArenaZone} based upon name
	 */
	public final ArenaZone getArenaZone(String name)
	{
		return ultimateArena.getArenaZone(name);
	}

	/**
	 * Gets an {@link Arena} by name.
	 *
	 * @param name Name of the Arena
	 */
	public final Arena getArena(String name)
	{
		return ultimateArena.getArena(name);
	}

	/**
	 * Stops all running arenas.
	 */
	public final void stopArenas()
	{
		logUsage("stopAll");
		ultimateArena.stopAll();
	}

	/**
	 * Dumps current API registrations.
	 */
	public final void dumpRegistrations()
	{
		logUsage("dumpRegistrations");
		ultimateArena.dumpRegistrations();
	}

	/**
	 * Kicks a given player from their current {@link Arena} (if any).
	 *
	 * @param player {@link Player} to kick
	 */
	public final void kickPlayer(Player player)
	{
		logUsage("kickPlayer(" + player.getName() + ")");

		ArenaPlayer ap = ultimateArena.getArenaPlayer(player);
		if (ap != null)
		{
			ap.leaveArena(LeaveReason.KICK);
		}
	}

	private void logUsage(String event)
	{
		ultimateArena.log("[API] \"{0}\" called by {1}", event, accessingPlugin);
	}

	/**
	 * Returns a new instance of {@link UltimateArenaAPI}.
	 *
	 * @param plugin {@link JavaPlugin} to hook into {@link UltimateArena} with
	 * @return New instance of {@link UltimateArenaAPI}
	 */
	public static UltimateArenaAPI hookIntoUA(Plugin plugin)
	{
		PluginManager pm = plugin.getServer().getPluginManager();
		if (! pm.isPluginEnabled("UltimateArena"))
		{
			plugin.getLogger().severe("Could not hook into UltimateArena: Plugin not enabled!");
			return null;
		}

		Plugin p = pm.getPlugin("UltimateArena");
		if (p instanceof UltimateArena)
		{
			plugin.getLogger().info("Successfully hooked into UltimateArena");

			UltimateArena ua = (UltimateArena) p;
			ua.acceptRegistration(plugin);

			return new UltimateArenaAPI(ua, plugin);
		}

		plugin.getLogger().severe("Could not hook into UltimateArena: Is there a plugin by the same name?");
		return null;
	}
}