/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.ultimatearena.integration;

import lombok.Getter;
import net.dmulloy2.integration.IntegrationHandler;
import net.dmulloy2.ultimatearena.UltimateArena;

import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;

/**
 * @author dmulloy2
 */

public class VaultHandler extends IntegrationHandler
{
	private @Getter boolean enabled;
	private Object economy;

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
			ServicesManager sm = plugin.getServer().getServicesManager();
			RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> economyProvider =
					sm.getRegistration(net.milkbowl.vault.economy.Economy.class);
			if (economyProvider != null)
			{
				net.milkbowl.vault.economy.Economy economy = economyProvider.getProvider();
				if (economy != null)
				{
					plugin.getLogHandler().log("Economy integration through {0}", economy.getName());
					this.economy = economy;
					enabled = true;
				}
			}
		}
		catch (Throwable ex)
		{
			enabled = false;
		}
	}

	@SuppressWarnings("deprecation") // Backwards Compatibility
	public final net.milkbowl.vault.economy.EconomyResponse depositPlayer(Player player, double amount)
	{
		if (economy != null)
		{
			try
			{
				return ((net.milkbowl.vault.economy.Economy) economy).depositPlayer(player, amount);
			}
			catch (Throwable ex)
			{
				return ((net.milkbowl.vault.economy.Economy) economy).depositPlayer(player.getName(), amount);
			}
		}

		return null;
	}

	@SuppressWarnings("deprecation") // Backwards Compatibility
	public final net.milkbowl.vault.economy.EconomyResponse withdrawPlayer(Player player, double amount)
	{
		if (economy != null)
		{
			try
			{
				return ((net.milkbowl.vault.economy.Economy) economy).withdrawPlayer(player, amount);
			}
			catch (Throwable ex)
			{
				return ((net.milkbowl.vault.economy.Economy) economy).withdrawPlayer(player.getName(), amount);
			}
		}

		return null;
	}

	public final net.milkbowl.vault.economy.Economy getEconomy()
	{
		return (net.milkbowl.vault.economy.Economy) economy;
	}
}