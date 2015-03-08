/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.ultimatearena.integration;

import java.util.logging.Level;

import net.dmulloy2.integration.DependencyProvider;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.util.Util;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;

/**
 * @author dmulloy2
 */

@SuppressWarnings("deprecation") // Backwards compat
public class VaultHandler extends DependencyProvider<Vault>
{
	private Economy economy;

	public VaultHandler(UltimateArena plugin)
	{
		super(plugin, "Vault");
	}

	@Override
	public void onEnable()
	{
		if (! isEnabled())
			return;

		try
		{
			ServicesManager sm = handler.getServer().getServicesManager();
			RegisteredServiceProvider<Economy> provider = sm.getRegistration(Economy.class);
			if (provider != null)
			{
				economy = provider.getProvider();
				handler.getLogHandler().log("Using {0} for economy.", economy.getName());
			}
		}
		catch (Throwable ex)
		{
			handler.getLogHandler().debug(Level.WARNING, Util.getUsefulStack(ex, "setup()"));
		}
	}

	@Override
	public void onDisable()
	{
		economy = null;
	}

	/**
	 * Attempts to deposit a given amount into a given Player's balance.
	 * 
	 * @param player Player to give money to
	 * @param amount Amount to give
	 * @return Error message, if applicable
	 */
	public String depositPlayer(Player player, double amount)
	{
		if (economy == null)
			return "Economy is disabled.";

		try
		{
			// In the future, we should probably use their Player instance,
			// but for now it doesn't really matter, as most economy plugins
			// will just use their name anyways.

			EconomyResponse response = economy.depositPlayer(player.getName(), amount);
			if (response.transactionSuccess())
				return "Success";

			return response.errorMessage;
		}
		catch (Throwable ex)
		{
			handler.getLogHandler().debug(Level.WARNING, Util.getUsefulStack(ex, "depositPlayer({0}, {1})", player.getName(), amount));
			return ex.toString();
		}
	}

	public boolean withdrawPlayer(Player player, double amount)
	{
		if (economy == null)
			return false;

		try
		{
			return economy.withdrawPlayer(player, amount).transactionSuccess();
		}
		catch (Throwable ex)
		{
			return economy.withdrawPlayer(player.getName(), amount).transactionSuccess();
		}
	}

	public String format(double amount)
	{
		if (economy == null)
			return Double.toString(amount);

		try
		{
			return economy.format(amount);
		}
		catch (Throwable ex)
		{
			handler.getLogHandler().debug(Level.WARNING, Util.getUsefulStack(ex, "format(" + amount + ")"));
		}

		return Double.toString(amount);
	}

	public double getBalance(Player player)
	{
		if (economy == null)
			return 0.0D;

		try
		{
			return economy.getBalance(player);
		}
		catch (Throwable ex)
		{
			return economy.getBalance(player.getName());
		}
	}
}