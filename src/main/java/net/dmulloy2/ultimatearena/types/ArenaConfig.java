package net.dmulloy2.ultimatearena.types;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import lombok.Getter;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.events.UltimateArenaRewardEvent;
import net.dmulloy2.ultimatearena.util.FormatUtil;
import net.dmulloy2.ultimatearena.util.InventoryHelper;
import net.dmulloy2.ultimatearena.util.ItemUtil;
import net.dmulloy2.ultimatearena.util.Util;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author dmulloy2
 */

@Getter
public class ArenaConfig
{
	private int gameTime, lobbyTime, maxDeaths, maxWave, cashReward, maxPoints;

	private boolean allowTeamKilling, countMobKills;
	private boolean loaded;

	private List<ItemStack> rewards = new ArrayList<ItemStack>();

	private String arenaName;
	private File file;
	private final UltimateArena plugin;

	public ArenaConfig(UltimateArena plugin, String str, File file)
	{
		this.arenaName = str;
		this.file = file;
		this.plugin = plugin;

		this.loaded = load();
		if (! loaded)
		{
			plugin.outConsole(Level.SEVERE, "Could not load config for " + arenaName + "!");
		}
	}

	public boolean load()
	{
		try
		{
			YamlConfiguration fc = YamlConfiguration.loadConfiguration(file);
			if (arenaName.equalsIgnoreCase("mob"))
			{
				this.maxWave = fc.getInt("maxWave");
			}
			
			if (arenaName.equalsIgnoreCase("koth"))
			{
				this.maxPoints = fc.getInt("maxPoints", 60);
			}

			this.gameTime = fc.getInt("gameTime");
			this.lobbyTime = fc.getInt("lobbyTime");
			this.maxDeaths = fc.getInt("maxDeaths");
			this.allowTeamKilling = fc.getBoolean("allowTeamKilling");
			this.cashReward = fc.getInt("cashReward");
			this.countMobKills = fc.getBoolean("countMobKills", 
					arenaName.equalsIgnoreCase("mob"));

			for (String reward : fc.getStringList("rewards"))
			{
				ItemStack stack = ItemUtil.readItem(reward);
				if (stack != null)
					rewards.add(stack);
			}
		}
		catch (Exception e)
		{
			plugin.outConsole(Level.SEVERE, Util.getUsefulStack(e, "loading config for \"" + arenaName + "\""));
			return false;
		}

		plugin.debug("Loaded ArenaConfig for type: {0}!", arenaName);
		return true;
	}

	public void giveRewards(Player player, boolean half)
	{
		plugin.debug("Rewarding player {0}. Half: {1}", player.getName(), half);
		
		UltimateArenaRewardEvent event = new UltimateArenaRewardEvent(player, rewards);
		if (event.isCancelled())
			return;

		List<ItemStack> rewards = event.getRewards();

		for (ItemStack stack : rewards)
		{
			if (stack == null)
				continue;

			if (half)
				stack.setAmount((int) Math.floor(stack.getAmount() / 2));

			InventoryHelper.addItem(player, stack);
		}

		// dmulloy2 new method
		if (plugin.getConfig().getBoolean("moneyrewards"))
		{
			if (plugin.getEconomy() != null)
			{
				if (cashReward > 0)
				{
					plugin.getEconomy().depositPlayer(player.getName(), cashReward);
					String format = plugin.getEconomy().format(cashReward);
					player.sendMessage(plugin.getPrefix() + 
							FormatUtil.format("&a{0} has been added to your account!", format));
				}
			}
		}
	}
}