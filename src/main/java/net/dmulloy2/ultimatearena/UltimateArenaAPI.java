package net.dmulloy2.ultimatearena;

import java.util.Collections;
import java.util.List;

import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaClass;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.FieldType;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Basic API for hooking into {@link UltimateArena}
 * <p>
 * TODO: Implement this like a true API
 * 
 * @author dmulloy2
 */
public class UltimateArenaAPI
{
	private final UltimateArena plugin;

	private UltimateArenaAPI(Plugin p)
	{
		this.plugin = (UltimateArena) p;
	}

	/**
	 * Gets whether or not a {@link Player} is playing an {@link Arena}
	 * 
	 * @param p
	 *            - {@link Player} in question
	 * @return whether or not they're playing in an arena
	 */
	public final boolean isPlayerPlayingArena(Player p)
	{
		return plugin.isInArena(p);
	}

	/**
	 * Gets whether or not a {@link Player} is standing in an {@link Arena}
	 * 
	 * @param p
	 *            - {@link Player} in question
	 * @return whether or not they are standing in an {@link Arena}
	 */
	public final boolean isPlayerInArenaLocation(Player p)
	{
		return plugin.isInArena(p.getLocation());
	}

	/**
	 * Returns a {@link Player}'s {@link ArenaPlayer} instance
	 * 
	 * @param p
	 *            - {@link Player} instance
	 * @return The player's {@link ArenaPlayer} instance
	 */
	public final ArenaPlayer getArenaPlayer(Player p)
	{
		return plugin.getArenaPlayer(p);
	}

	/**
	 * Returns whether or not a {@link Location} is inside an {@link Arena}
	 * 
	 * @param loc
	 *            - {@link Location} in question
	 * @return Whether or not a {@link Location} is inside an {@link Arena}
	 */
	public final boolean isLocationInArena(Location loc)
	{
		return plugin.isInArena(loc);
	}

	/**
	 * Returns the amount of kills a {@link ArenaPlayer} has
	 * 
	 * @param a
	 *            - {@link ArenaPlayer} to get kills for
	 * @return The amount of kills a {@link ArenaPlayer} has
	 */
	public final int getKills(ArenaPlayer a)
	{
		return a.getKills();
	}

	/**
	 * Returns the amount of deaths a {@link ArenaPlayer} has
	 * 
	 * @param a
	 *            - {@link ArenaPlayer} to get deaths for
	 * @return The amount of deaths a {@link ArenaPlayer} has
	 */
	public final int getDeaths(ArenaPlayer a)
	{
		return a.getDeaths();
	}

	/**
	 * Returns the team an {@link ArenaPlayer} is on
	 * 
	 * @param a
	 *            - {@link ArenaPlayer} to get team
	 * @return The team an {@link ArenaPlayer} is on
	 */
	public final int getTeam(ArenaPlayer a)
	{
		return a.getTeam();
	}

	/**
	 * Returns an {@link ArenaPlayer}'s current killstreak
	 * 
	 * @param a
	 *            - {@link ArenaPlayer} to get killstreak
	 * @return An {@link ArenaPlayer}'s current killstreak
	 */
	public final int getKillStreak(ArenaPlayer a)
	{
		return a.getKillStreak();
	}

	/**
	 * Gets the name of the {@link Arena} an {@link ArenaPlayer} is in
	 * 
	 * @param a
	 *            - {@link ArenaPlayer} instance
	 * @return The name of the {@link Arena} the player is in
	 */
	public String getArenaName(ArenaPlayer a)
	{
		return a.getArena().getName();
	}

	/**
	 * Gets the {@link FieldType} of the {@link Arena} an {@link ArenaPlayer} is
	 * in
	 * 
	 * @param a
	 *            - {@link ArenaPlayer} instance
	 * @return The {@link FieldType} of the {@link Arena} the player is in
	 */
	public FieldType getArenaType(ArenaPlayer a)
	{
		return a.getArena().getType();
	}

	/**
	 * Gets all active {@link Arena}s
	 * 
	 * @return {@link List} of all active {@link Arena}s
	 */
	public final List<Arena> getActiveArenas()
	{
		return Collections.unmodifiableList(plugin.getActiveArenas());
	}

	/**
	 * Gets all loaded {@link ArenaZone}s
	 * 
	 * @return {@link List} of all loaded {@link ArenaZone}s
	 */
	public final List<ArenaZone> getLoadedArenas()
	{
		return Collections.unmodifiableList(plugin.getLoadedArenas());
	}

	/**
	 * Gets all loaded {@link ArenaZone}s
	 * 
	 * @return {@link List} of all loaded {@link ArenaZone}s
	 */
	public final List<ArenaClass> getClasses()
	{
		return Collections.unmodifiableList(plugin.getClasses());
	}

	/**
	 * Gets an {@link ArenaClass} based upon name
	 * 
	 * @param name
	 *            - Name of the {@link ArenaClass}
	 * @return {@link ArenaClass} based upon name
	 */
	public final ArenaClass getArenaClass(String name)
	{
		return plugin.getArenaClass(name);
	}

	/**
	 * Gets an {@link ArenaZone} based upon name
	 * 
	 * @param name
	 *            - Name of the {@link ArenaZone}
	 * @return {@link ArenaZone} based upon name
	 */
	public final ArenaZone getArenaZone(String name)
	{
		return plugin.getArenaZone(name);
	}

	/**
	 * Returns a new instance of {@link UltimateArenaAPI}
	 * 
	 * @param plugin
	 *            - {@link JavaPlugin} to hook into {@link UltimateArena}
	 * @return New instance of {@link UltimateArenaAPI}
	 */
	public static UltimateArenaAPI hookIntoUA(JavaPlugin plugin)
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
			
			return new UltimateArenaAPI(p);
		}

		plugin.getLogger().severe("Could not hook into UltimateArena: Is there a plugin by the same name?");
		return null;
	}
}