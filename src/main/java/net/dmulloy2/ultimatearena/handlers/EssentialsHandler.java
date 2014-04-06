package net.dmulloy2.ultimatearena.handlers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.types.ArenaClass;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.util.Util;

import org.bukkit.entity.Player;

/**
 * Handles integration with Essentials.
 * <p>
 * All Essentials integration should go through this handler.
 * Everything is wrapped in a catch-all, since Essentials integration is
 * somewhat buggy and isn't necessary for functioning
 *
 * @author dmulloy2
 */

public class EssentialsHandler
{
	private Object essentials;
	private boolean useEssentials;

	private final UltimateArena plugin;
	public EssentialsHandler(UltimateArena plugin)
	{
		this.plugin = plugin;
		this.essentials = null;
		this.useEssentials = false;
	}

	/**
	 * Returns Essentials. Stored in an object because integration with it is
	 * nearly impossible
	 */
	public final com.earth2me.essentials.Essentials getEssentials()
	{
		return (com.earth2me.essentials.Essentials) essentials;
	}

	/**
	 * Sets the Essentials instance
	 */
	public final void setEssentials(Object essentials)
	{
		this.essentials = essentials;
	}

	/**
	 * Whether or not to use Essentials integration
	 */
	public final boolean useEssentials()
	{
		try
		{
			return useEssentials && essentials != null;
		}
		catch (Throwable ex)
		{
			plugin.getLogHandler().debug(Util.getUsefulStack(ex, "useEssentials()"));
			return false;
		}
	}

	/**
	 * Sets whether or not to use essentials
	 *
	 * @param useEssentials
	 *        - Whether or not to use Essentials
	 */
	public final void setUseEssentials(boolean useEssentials)
	{
		this.useEssentials = useEssentials;
	}

	/**
	 * Disables Essentials god mode
	 *
	 * @param player
	 *        - {@link Player} to disable god mode for
	 */
	public final void disableGodMode(Player player)
	{
		try
		{
			if (useEssentials())
			{
				com.earth2me.essentials.User user = getEssentialsUser(player);
				if (user != null)
				{
					user.setGodModeEnabled(false);
				}
			}
		}
		catch (Throwable ex)
		{
			plugin.getLogHandler().debug(Util.getUsefulStack(ex, "disableGodMode(" + player.getName() + ")"));
		}
	}

	/**
	 * Attempts to get a player's Essentials user
	 *
	 * @param player
	 *        - {@link Player} to get Essentials user for
	 */
	public final com.earth2me.essentials.User getEssentialsUser(Player player)
	{
		try
		{
			if (useEssentials())
			{
				return getEssentials().getUser(player);
			}
		}
		catch (Throwable ex)
		{
			plugin.getLogHandler().debug(Util.getUsefulStack(ex, "getEssentialsUser(" + player.getName() + ")"));
		}

		return null;
	}

	/**
	 * Attempts to give a player their class's Essentials kit items
	 *
	 * @param player
	 *        - {@link ArenaPlayer} to give kit items to
	 */
	public final void giveKitItems(ArenaPlayer player)
	{
		try
		{
			com.earth2me.essentials.User user = getEssentialsUser(player.getPlayer());
			if (user == null)
			{
				throw new Exception("Null user!");
			}

			ArenaClass ac = player.getArenaClass();
			List<String> items = com.earth2me.essentials.Kit.getItems(getEssentials(), user, ac.getEssKitName(),
					ac.getEssentialsKit());
			com.earth2me.essentials.Kit.expandItems(getEssentials(), user, items);
		}
		catch (Throwable ex)
		{
			player.sendMessage("&cCould not give Essentials kit: {0}", ex instanceof ClassNotFoundException
					|| ex instanceof NoSuchMethodError ? "outdated Essentials!" : ex.getMessage());

			plugin.debug(Util.getUsefulStack(ex, "giveKitItems(" + player.getName() + ")"));
		}
	}

	/**
	 * Attempts to read an Essentials kit from configuration
	 *
	 * @param name
	 *        - Name of the Essentials kit
	 */
	public final Map<String, Object> readEssentialsKit(String name)
	{
		Map<String, Object> kit = new HashMap<String, Object>();

		try
		{
			if (useEssentials())
			{
				kit = getEssentials().getSettings().getKit(name);
			}
		}
		catch (Throwable ex)
		{
			plugin.debug(Util.getUsefulStack(ex, "readEssentialsKit(" + name + ")"));
		}

		return kit;
	}
}