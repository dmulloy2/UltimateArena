package net.dmulloy2.ultimatearena.integration;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import net.dmulloy2.integration.IntegrationHandler;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.types.ArenaClass;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.util.Util;

import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.Kit;
import com.earth2me.essentials.User;

/**
 * Handles integration with Essentials.
 * <p>
 * All Essentials integration should go through this handler.
 * Everything is wrapped in a catch-all, since Essentials integration is
 * somewhat buggy and isn't necessary for functioning
 *
 * @author dmulloy2
 */

public class EssentialsHandler extends IntegrationHandler
{
	private @Getter Essentials essentials;
	private @Getter boolean enabled;

	private final UltimateArena plugin;
	public EssentialsHandler(UltimateArena plugin)
	{
		this.plugin = plugin;
		this.setup();
	}

	@Override
	public void setup()
	{
		try
		{
			PluginManager pm = plugin.getServer().getPluginManager();
			if (pm.getPlugin("Essentials") != null)
			{
				essentials = (Essentials) pm.getPlugin("Essentials");
				enabled = true;

				plugin.getLogHandler().log("Integration with Essentials successful!");
			}
		} catch (Throwable ex) { }
	}

	/**
	 * Whether or not to use Essentials integration
	 */
	public final boolean useEssentials()
	{
		try
		{
			return enabled && essentials != null;
		}
		catch (Throwable ex)
		{
			plugin.getLogHandler().debug(Util.getUsefulStack(ex, "useEssentials()"));
			return false;
		}
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
				User user = getEssentialsUser(player);
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
	public final User getEssentialsUser(Player player)
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
			User user = getEssentialsUser(player.getPlayer());
			if (user == null)
			{
				throw new Exception("Null user!");
			}

			ArenaClass ac = player.getArenaClass();
			Kit kit = new Kit(ac.getEssKitName(), essentials);
			kit.expandItems(user);
		}
		catch (Throwable ex)
		{
			player.sendMessage("&4Error: &cCould not give Essentials kit: &4{0}", ex.toString());
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