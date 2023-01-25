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

import net.dmulloy2.ultimatearena.api.ArenaType;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaClass;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.LeaveReason;
import net.dmulloy2.swornapi.util.ListUtil;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

/**
 * Basic API for hooking into {@link UltimateArena}
 *
 * @author dmulloy2
 */

public final class UltimateArenaAPI
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
	public boolean isInArena(Player player)
	{
		Validate.notNull(player, "player cannot be null!");
		return ultimateArena.isInArena(player);
	}

	/**
	 * Gets whether or not a {@link Location} is in an {@link Arena}.
	 *
	 * @param loc {@link Location} in question
	 */
	public boolean isInArena(Location loc)
	{
		Validate.notNull(loc, "loc cannot be null!");
		return ultimateArena.isInArena(loc);
	}

	/**
	 * Returns a {@link Player}'s {@link ArenaPlayer} instance.
	 *
	 * @param player {@link Player} instance
	 * @return The player's {@link ArenaPlayer} instance, may be null
	 */
	public ArenaPlayer getArenaPlayer(Player player)
	{
		Validate.notNull(player, "player cannot be null!");
		return ultimateArena.getArenaPlayer(player);
	}

	/**
	 * Gets all active {@link Arena}s. The list returned is not modifiable.
	 *
	 * @return An unmodifiable {@link List} of all active {@link Arena}s
	 */
	public List<Arena> getActiveArenas()
	{
		return ListUtil.toList(ultimateArena.getActiveArenas());
	}

	/**
	 * Gets all loaded {@link ArenaZone ArenaZones}. The list returned is not modifiable.
	 *
	 * @return A list of all loaded ArenaZones
	 */
	public List<ArenaZone> getLoadedArenas()
	{
		return Collections.unmodifiableList(ultimateArena.getLoadedArenas());
	}

	/**
	 * Gets all loaded {@link ArenaClass ArenaClasses}. The list returned is not modifiable.
	 *
	 * @return A list of all loaded ArenaClasses
	 */
	public List<ArenaClass> getClasses()
	{
		return Collections.unmodifiableList(ultimateArena.getClasses());
	}

	/**
	 * Gets an {@link ArenaClass} by name.
	 *
	 * @param name Name of the class
	 * @return The class, or null if not found
	 */
	public ArenaClass getArenaClass(String name)
	{
		Validate.notNull(name, "name cannot be null!");
		return ultimateArena.getArenaClass(name);
	}

	/**
	 * Gets an {@link ArenaZone} by name.
	 *
	 * @param name Name of the zone
	 * @return The zone, or null if not found
	 */
	public ArenaZone getArenaZone(String name)
	{
		Validate.notNull(name, "name cannot be null!");
		return ultimateArena.getArenaZone(name);
	}

	/**
	 * Gets an {@link Arena} by name.
	 *
	 * @param name Name of the Arena
	 * @return The Arena, or null if not found.
	 */
	public Arena getArena(String name)
	{
		Validate.notNull(name, "name cannot be null!");
		return ultimateArena.getArena(name);
	}

	/**
	 * Gets the {@link Arena} a Player is in.
	 * 
	 * @param player Player to get arena for
	 * @return The Arena, or null if not found.
	 */
	public Arena getArena(Player player)
	{
		Validate.notNull(player, "player cannot be null!");
		return ultimateArena.getArena(player);
	}

	/**
	 * Stops all running arenas.
	 */
	public void stopArenas()
	{
		logUsage("stopAll");
		ultimateArena.stopAll();
	}

	/**
	 * Dumps current API registrations.
	 * @return The list of registered plugins
	 */
	public List<String> dumpRegistrations()
	{
		logUsage("dumpRegistrations");
		return ultimateArena.dumpRegistrations();
	}

	/**
	 * Kicks a given {@link Player} from their current {@link Arena}, if
	 * applicable.
	 *
	 * @param player Player to kick
	 */
	public void kickPlayer(Player player)
	{
		kickPlayer(player, LeaveReason.GENERIC);
	}

	/**
	 * Kicks a given {@link Player} from their current {@link Arena}, if
	 * applicable.
	 * 
	 * @param player Player to kick
	 * @param reason Reason for kicking the player
	 */
	public void kickPlayer(Player player, LeaveReason reason)
	{
		Validate.notNull(player, "player cannot be null!");
		Validate.notNull(reason, "reason cannot be null!");

		logUsage("kickPlayer(" + player.getName() + ")");

		ArenaPlayer ap = ultimateArena.getArenaPlayer(player);
		if (ap != null)
		{
			ap.leaveArena(reason);
		}
	}

	/**
	 * Gets the list of currently loaded {@link ArenaType ArenaTypes}. Modifications
	 * to the returned list will not be reflected in internal UA code.
	 * 
	 * @return The list
	 */
	public List<ArenaType> getLoadedTypes()
	{
		return ultimateArena.getArenaTypeHandler().getArenaTypes();
	}

	private void logUsage(String event)
	{
		ultimateArena.log("[API] \"{0}\" called by {1}", event, accessingPlugin);
	}

	/**
	 * Returns a new instance of {@link UltimateArenaAPI}. An error message
	 * will be printed using the Plugin's logger if hooking fails.
	 *
	 * @param plugin Plugin to hook into UltimateArnea with
	 * @return The instance, or null if hooking failed
	 */
	public static UltimateArenaAPI hookIntoUA(Plugin plugin)
	{
		Validate.notNull(plugin, "plugin cannot be null!");

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