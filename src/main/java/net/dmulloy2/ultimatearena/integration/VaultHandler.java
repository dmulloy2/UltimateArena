/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.ultimatearena.integration;

import lombok.Getter;
import net.dmulloy2.integration.IntegrationHandler;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;

/**
 * @author dmulloy2
 */

public class VaultHandler extends IntegrationHandler
{
	private @Getter boolean enabled;
	private @Getter Economy economy;

	private final UltimateArena plugin;
	public VaultHandler(UltimateArena plugin)
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
			if (pm.getPlugin("Vault") != null)
			{
				ServicesManager sm = plugin.getServer().getServicesManager();
				RegisteredServiceProvider<Economy> economyProvider = sm.getRegistration(Economy.class);
				if (economyProvider != null)
				{
					economy = economyProvider.getProvider();
					if (economy != null)
					{
						plugin.getLogHandler().log("Economy integration through {0}", economy.getName());
						enabled = true;
					}
				}
			}
		}
		catch (Throwable ex)
		{
			enabled = false;
		}
	}

	@SuppressWarnings("deprecation") // Backwards Compatibility
	public final EconomyResponse depositPlayer(Player player, double amount)
	{
		if (economy != null)
		{
			try
			{
				return economy.depositPlayer(player, amount);
			}
			catch (Throwable ex)
			{
				return economy.depositPlayer(player.getName(), amount);
			}
		}

		return null;
	}
}