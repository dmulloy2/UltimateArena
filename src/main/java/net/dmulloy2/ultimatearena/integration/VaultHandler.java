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

	public final boolean depositPlayer(Player player, double amount)
	{
		if (! isEnabled())
			return false;

		try
		{
			return economy.depositPlayer(player, amount).transactionSuccess();
		}
		catch (Throwable ex)
		{
			return economy.depositPlayer(player.getName(), amount).transactionSuccess();
		}
	}

	public final boolean withdrawPlayer(Player player, double amount)
	{
		if (! isEnabled())
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

	public final String format(double amount)
	{
		if (! isEnabled())
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

	public final double getBalance(Player player)
	{
		if (! isEnabled())
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

	@Override
	public boolean isEnabled()
	{
		return super.isEnabled() && economy != null;
	}
}