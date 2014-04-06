package net.dmulloy2.ultimatearena;

import java.util.List;

import lombok.AllArgsConstructor;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaClass;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.FieldType;
import net.dmulloy2.ultimatearena.types.LeaveReason;
import net.dmulloy2.ultimatearena.util.Util;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Basic API for hooking into {@link UltimateArena}
 *
 * @author dmulloy2
 */
@AllArgsConstructor
public class UltimateArenaAPI
{
	protected final UltimateArena ultimateArena;
	protected final Plugin accessingPlugin;

	/**
	 * Whether or not a {@link Player} is playing an {@link Arena}
	 *
	 * @param player
	 *        - {@link Player} in question
	 */
	public final boolean isInArena(Player player)
	{
		return ultimateArena.isInArena(player);
	}

	/**
	 * Gets whether or not a {@link Location} is in an {@link Arena}
	 *
	 * @param location
	 *        - {@link Location} in question
	 */
	public final boolean isInArena(Location loc)
	{
		return ultimateArena.isInArena(loc);
	}

	/**
	 * Returns a {@link Player}'s {@link ArenaPlayer} instance
	 *
	 * @param p
	 *        - {@link Player} instance
	 * @return The player's {@link ArenaPlayer} instance
	 */
	public final ArenaPlayer getArenaPlayer(Player p)
	{
		return ultimateArena.getArenaPlayer(p);
	}

	/**
	 * Gets all active {@link Arena}s. The list returned is not modifiable.
	 *
	 * @return {@link List} of all active {@link Arena}s
	 */
	public final List<Arena> getActiveArenas()
	{
		return ultimateArena.getActiveArenas();
	}

	/**
	 * Gets all loaded {@link ArenaZone}s
	 *
	 * @return {@link List} of all loaded {@link ArenaZone}s
	 */
	public final List<ArenaZone> getLoadedArenas()
	{
		return Util.newList(ultimateArena.getLoadedArenas());
	}

	/**
	 * Gets all loaded {@link ArenaZone}s
	 *
	 * @return {@link List} of all loaded {@link ArenaZone}s
	 */
	public final List<ArenaClass> getClasses()
	{
		return Util.newList(ultimateArena.getClasses());
	}

	/**
	 * Gets an {@link ArenaClass} based upon name
	 *
	 * @param name
	 *        - Name of the {@link ArenaClass}
	 * @return {@link ArenaClass} based upon name
	 */
	public final ArenaClass getArenaClass(String name)
	{
		return ultimateArena.getArenaClass(name);
	}

	/**
	 * Gets an {@link ArenaZone} based upon name
	 *
	 * @param name
	 *        - Name of the {@link ArenaZone}
	 * @return {@link ArenaZone} based upon name
	 */
	public final ArenaZone getArenaZone(String name)
	{
		return ultimateArena.getArenaZone(name);
	}

	/**
	 * Gets an {@link Arena} by its name
	 *
	 * @param name
	 *        - Name of the Arena
	 */
	public final Arena getArena(String name)
	{
		return ultimateArena.getArena(name);
	}

	/**
	 * Stops all running arenas
	 */
	public final void stopArenas()
	{
		logUsage("stopAll");
		ultimateArena.stopAll();
	}

	/**
	 * Dumps current API registrations
	 */
	public final void dumpRegistrations()
	{
		logUsage("dumpRegistrations");
		ultimateArena.dumpRegistrations();
	}

	/**
	 * Kicks a given player from their current {@link Arena} (if any)
	 *
	 * @param player
	 *        - {@link Player} to kick
	 */
	public final void kickPlayer(Player player)
	{
		logUsage("kickPlayer(" + player.getName() + ")");
		if (ultimateArena.isInArena(player))
		{
			getArenaPlayer(player).leaveArena(LeaveReason.KICK);
		}
	}

	private void logUsage(String event)
	{
		ultimateArena.outConsole("[API] \"{0}\" called by {1}", event, accessingPlugin);
	}

	/**
	 * Returns a new instance of {@link UltimateArenaAPI}
	 *
	 * @param plugin
	 *        - {@link JavaPlugin} to hook into {@link UltimateArena} with
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

	// ---- Deprecated Legacy Methods ---- //
	// Due to be removed ~1.8

	/**
	 * Gets whether or not a {@link Player} is playing an {@link Arena}
	 *
	 * @param p
	 *        - {@link Player} in question
	 * @return whether or not they're playing in an arena
	 * @deprecated In favor of {@link UltimateArenaAPI#isInArena(Player)}
	 */
	@Deprecated
	public final boolean isPlayerPlayingArena(Player p)
	{
		return ultimateArena.isInArena(p);
	}

	/**
	 * Gets whether or not a {@link Player} is standing in an {@link Arena}
	 *
	 * @param p
	 *        - {@link Player} in question
	 * @return whether or not they are standing in an {@link Arena}
	 * @deprecated In favor of {@link UltimateArenaAPI#isInArena(Location)}
	 */
	@Deprecated
	public final boolean isPlayerInArenaLocation(Player p)
	{
		return ultimateArena.isInArena(p.getLocation());
	}

	/**
	 * Returns whether or not a {@link Location} is inside an {@link Arena}
	 *
	 * @param loc
	 *        - {@link Location} in question
	 * @return Whether or not a {@link Location} is inside an {@link Arena}
	 * @deprecated In favor of {@link UltimateArenaAPI#isInArena(Location)}
	 */
	@Deprecated
	public final boolean isLocationInArena(Location loc)
	{
		return ultimateArena.isInArena(loc);
	}

	/**
	 * Returns the amount of kills a {@link ArenaPlayer} has
	 *
	 * @param a
	 *        - {@link ArenaPlayer} to get kills for
	 * @return The amount of kills a {@link ArenaPlayer} has
	 * @deprecated In favor of {@link ArenaPlayer#getKills()}
	 */
	@Deprecated
	public final int getKills(ArenaPlayer a)
	{
		return a.getKills();
	}

	/**
	 * Returns the amount of deaths a {@link ArenaPlayer} has
	 *
	 * @param a
	 *        - {@link ArenaPlayer} to get deaths for
	 * @return The amount of deaths a {@link ArenaPlayer} has
	 * @deprecated In favor of {@Link ArenaPlayer#getDeaths()}
	 */
	@Deprecated
	public final int getDeaths(ArenaPlayer a)
	{
		return a.getDeaths();
	}

	/**
	 * Returns the team an {@link ArenaPlayer} is on
	 *
	 * @param a
	 *        - {@link ArenaPlayer} to get team
	 * @return The team an {@link ArenaPlayer} is on
	 * @deprecated In favor of {@Link ArenaPlayer#getTeam()}
	 */
	@Deprecated
	public final int getTeam(ArenaPlayer a)
	{
		return a.getTeam();
	}

	/**
	 * Returns an {@link ArenaPlayer}'s current killstreak
	 *
	 * @param a
	 *        - {@link ArenaPlayer} to get killstreak
	 * @return An {@link ArenaPlayer}'s current killstreak
	 * @deprecated In favor of {@Link ArenaPlayer#getKillStreak()}
	 */
	@Deprecated
	public final int getKillStreak(ArenaPlayer a)
	{
		return a.getKillStreak();
	}

	/**
	 * Gets the name of the {@link Arena} an {@link ArenaPlayer} is in
	 *
	 * @param a
	 *        - {@link ArenaPlayer} instance
	 * @return The name of the {@link Arena} the player is in
	 * @deprecated In favor of {@Link Arena#getName()}
	 */
	@Deprecated
	public String getArenaName(ArenaPlayer a)
	{
		return a.getArena().getName();
	}

	/**
	 * Gets the {@link FieldType} of the {@link Arena} an {@link ArenaPlayer} is
	 * in
	 *
	 * @param a
	 *        - {@link ArenaPlayer} instance
	 * @return The {@link FieldType} of the {@link Arena} the player is in
	 * @deprecated In favor of {@Link Arena#getType()}
	 */
	@Deprecated
	public FieldType getArenaType(ArenaPlayer a)
	{
		return a.getArena().getType();
	}
}