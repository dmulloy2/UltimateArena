/**
 * (c) 2014 dmulloy2
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
				this.economy = provider.getProvider();
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

	@SuppressWarnings("deprecation") // Backwards Compatibility
	public final EconomyResponse depositPlayer(Player player, double amount)
	{
		if (! isEnabled())
			return null;

		try
		{
			return economy.depositPlayer(player, amount);
		}
		catch (Throwable ex)
		{
			return economy.depositPlayer(player.getName(), amount);
		}
	}

	@SuppressWarnings("deprecation") // Backwards Compatibility
	public final EconomyResponse withdrawPlayer(Player player, double amount)
	{
		if (! isEnabled())
			return null;

		try
		{
			return economy.withdrawPlayer(player, amount);
		}
		catch (Throwable ex)
		{
			return economy.withdrawPlayer(player.getName(), amount);
		}
	}

	@Override
	public boolean isEnabled()
	{
		return super.isEnabled() && economy != null;
	}

	public final Economy getEconomy()
	{
		return economy;
	}
}