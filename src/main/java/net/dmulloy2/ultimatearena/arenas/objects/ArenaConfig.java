package net.dmulloy2.ultimatearena.arenas.objects;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.util.InventoryHelper;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class ArenaConfig
{
	private int gameTime, lobbyTime, maxDeaths, maxWave, cashReward;
	
	private boolean allowTeamKilling;
	
	private List<ArenaReward> rewards = new ArrayList<ArenaReward>();
	
	private boolean loaded = false;
	
	private String arenaName;
	private File file;
	private final UltimateArena plugin;
	
	public ArenaConfig(final UltimateArena plugin, String str, File file)
	{
		this.arenaName = str;
		this.file = file;
		this.plugin = plugin;
		
		this.loaded = load();
		if (!loaded)
		{
			plugin.getLogger().warning("Could not load config for " + arenaName + "!");
		}
	}
	
	public boolean load()
	{
		try
		{
			YamlConfiguration fc = YamlConfiguration.loadConfiguration(file);
			if (arenaName.equals("mob"))
			{
				if (fc.get("maxWave") == null)
				{
					fc.set("maxWave", 15);
					fc.save(file);
				}
				
				this.maxWave = fc.getInt("maxWave");
			}
			
			this.gameTime = fc.getInt("gameTime");
			this.lobbyTime = fc.getInt("lobbyTime");
			this.maxDeaths = fc.getInt("maxDeaths");
			this.allowTeamKilling = fc.getBoolean("allowTeamKilling");
			this.cashReward = fc.getInt("cashReward");
					
			List<String> words = fc.getStringList("rewards");
			for (String word : words)
			{
				int id = 0;
				byte dat = 0;
				int amt = 0;
						
				String[] split = word.split(",");
				if (split[0].contains(":"))
				{
					String[] split2 = split[0].split(":");
					id = Integer.parseInt(split2[0]);
					dat = Byte.parseByte(split2[1]);
					amt = Integer.parseInt(split[1]);
				}
				else
				{
					id = Integer.parseInt(split[0]);
					amt = Integer.parseInt(split[1]);
				}
						
				ArenaReward reward = new ArenaReward(id, dat, amt);
				rewards.add(reward);
			}
		}
		catch (Exception e)
		{
			plugin.getLogger().severe("Error loading config: " + e.getMessage());
			return false;
		}
		
		return true;
	}
	
	public void giveRewards(Player player, boolean half) 
	{
		for (ArenaReward a : rewards)
		{
			ItemStack stack = new ItemStack(a.getType());
			
			// Calculate Amount
			int amount = 1;
			if (a.getAmount() != 0)
			{
				amount = a.getAmount();
			}
			
			if (half)
			{
				amount = (int) Math.floor(amount / 2.0);
			}
			
			stack.setAmount(amount);
			
			if (a.getData() != 0)
			{
				MaterialData data = new MaterialData(a.getType());
				data.setData(a.getData());
				stack.setData(data);
			}
			
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
					player.sendMessage(ChatColor.GREEN + format + " has been added to your balance!");
				}
			}
		}
	}

	public int getGameTime() 
	{
		return gameTime;
	}

	public int getLobbyTime() 
	{
		return lobbyTime;
	}

	public int getMaxDeaths() 
	{
		return maxDeaths;
	}

	public int getMaxWave()
	{
		return maxWave;
	}

	public int getCashReward()
	{
		return cashReward;
	}

	public boolean isAllowTeamKilling() 
	{
		return allowTeamKilling;
	}
	
	public String getArenaName()
	{
		return arenaName;
	}
}