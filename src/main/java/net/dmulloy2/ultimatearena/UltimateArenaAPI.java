package net.dmulloy2.ultimatearena;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import net.dmulloy2.ultimatearena.arenas.objects.ArenaPlayer;
import net.dmulloy2.ultimatearena.arenas.objects.FieldType;

public class UltimateArenaAPI 
{
	private final UltimateArena plugin;
	private UltimateArenaAPI(Plugin p)
	{
		this.plugin = (UltimateArena)p;
	}

	/**
	 * @param p - Player in question
	 * @return whether or not they're playing in an arena (boolean)
	 */
	public final boolean isPlayerPlayingArena(Player p)
	{
		return plugin.isInArena(p);
	}
	
	/**
	 * @param p - Player in question
	 * @return whether or not they are standing in an arena (boolean)
	 */
	public final boolean isPlayerInArenaLocation(Player p)
	{
		return plugin.isInArena(p.getLocation());
	}
	
	/**
	 * @param p - Player instance
	 * @return The player's ArenaPlayer instance
	 */
	public final ArenaPlayer getArenaPlayer(Player p)
	{
		return plugin.getArenaPlayer(p);
	}
	
	/**
	 * @param loc - Location in question
	 * @return Whether or not an arena is at that location
	 */
	public final boolean isLocationInArena(Location loc)
	{
		return plugin.isInArena(loc);
	}

	/**
	 * @param a - ArenaPlayer instance
	 * @return The ArenaPlayer's kills
	 */
	public final int getKills(ArenaPlayer a)
	{
		return a.getKills();
	}
	
	/**
	 * @param a - ArenaPlayer instance
	 * @return The ArenaPlayer's deaths
	 */
	public final int getDeaths(ArenaPlayer a)
	{
		return a.getDeaths();
	}
	
	/**
	 * @param a - ArenaPlayer instance
	 * @return The ArenaPlayer's team
	 */
	public final int getTeam(ArenaPlayer a)
	{
		return a.getTeam();
	}
	
	/**
	 * @param a - ArenaPlayer instance
	 * @return The ArenaPlayer's killstreak
	 */
	public final int getKillStreak(ArenaPlayer a)
	{
		return a.getKillstreak();
	}
	
	/**
	 * @param a - ArenaPlayer instance
	 * @return The name of the arena the player is in
	 */
	public String getArenaName(ArenaPlayer a)
	{
		return a.getArena().getName();
	}
	
	/**
	 * @param a - ArenaPlayer instance
	 * @return The type of the arena the player is in
	 */
	public FieldType getArenaType(ArenaPlayer a)
	{
		return a.getArena().getType();
	}
	
	/**
	 * @deprecated - We can be more creative than this
	 * @return UltimateArena plugin
	 */
	@Deprecated
	public final UltimateArena getPlugin()
	{
		return plugin;
	}
	
	/**Find the UltimateArena plugin and hook into its API**/
	public static UltimateArenaAPI hookIntoUA()
	{
		Plugin p = Bukkit.getPluginManager().getPlugin("UltimateArena");
		if (p == null)
		{
			Bukkit.getLogger().severe("Could not hook into UltimateArena! Is it installed?");
			return null;
		}
		
		if (!(p instanceof UltimateArena))
		{
			Bukkit.getLogger().severe("Could not hook into UltimateArena! Is there a plugin by the same name?");
			return null;
		}
		
		Bukkit.getLogger().info("Successfully hooked into UltimateArena!");
		
		return new UltimateArenaAPI(p);
	}
}