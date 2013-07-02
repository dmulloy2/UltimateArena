package net.dmulloy2.ultimatearena.arenas.objects;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.util.InventoryHelper;

@Deprecated
public class OldArenaConfig
{
	public int gameTime, lobbyTime, maxDeaths, maxwave, cashReward;
	public boolean allowTeamKilling;
	public List<ArenaReward> rewards = new ArrayList<ArenaReward>();
	public String arenaName;
	public File file;
	public UltimateArena plugin;
	
	public OldArenaConfig(UltimateArena plugin, String str, File f)
	{
		this.arenaName = str;
		this.file = f;
		this.plugin = plugin;
		
		load();
	}
	
	public void computeData(String str)
	{
		if (str.indexOf("=") > 0)
		{
			String str2 = str.substring(0, str.indexOf("="));
			if (str2.equalsIgnoreCase("maxwave"))
			{
				int value = Integer.parseInt(str.substring(str.indexOf("=")+1));
				maxwave = value;
			}
			if (str2.equalsIgnoreCase("gametime")) 
			{
				int value = Integer.parseInt(str.substring(str.indexOf("=")+1));
				gameTime = value;
			}
			if (str2.equalsIgnoreCase("lobbytime")) 
			{
				int value = Integer.parseInt(str.substring(str.indexOf("=")+1));
				lobbyTime = value;
			}
			if (str2.equalsIgnoreCase("maxdeaths")) 
			{
				int value = Integer.parseInt(str.substring(str.indexOf("=")+1));
				maxDeaths = value;
			}
			if (str2.equalsIgnoreCase("allowteamkilling"))
			{
				boolean value = Boolean.parseBoolean(str.substring(str.indexOf("=")+1));
				allowTeamKilling = value;
			}
			if (str2.equalsIgnoreCase("cashreward")) 
			{
				int value = Integer.parseInt(str.substring(str.indexOf("=")+1));
				cashReward = value;
			}
		}
		else if (str.indexOf(",") > 0 ) 
		{
			getReward(str);
		}
	}
	
	public void giveRewards(final Player player, final boolean half) 
	{
		class RewardTask extends BukkitRunnable
		{
			public void run() 
			{
				for (int i = 0; i < rewards.size(); i++) 
				{
					ArenaReward a = rewards.get(i);
					Material mat = Material.getMaterial(a.type);
					int amt = a.amt;
					byte dat = a.data;
					PlayerInventory inv = player.getInventory();
					MaterialData data = new MaterialData(mat.getId());
					
					if (amt < 1)
						amt = 1;
					
					if (dat > 0)
					{
						data.setData(dat);
					}
					if (half)
						amt = (int)(Math.floor(amt / 2.0));
					
					ItemStack itemStack = new ItemStack(mat, amt);
					if (dat > 0) itemStack.setData(data);
					
					int slot = InventoryHelper.getFirstFreeSlot(inv);
					if (slot > -1)
					{
						inv.setItem(slot, itemStack);
					}
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
		}
		
		new RewardTask().runTask(plugin);
	}
	
	public void getReward(String str) 
	{
		try
		{
			String[] ret = str.split(",");
			if (ret.length == 2)
			{
				int type = 0;
				byte dat = 0;
				int amt = 0;
				if (ret[0].contains(":"))
				{
					String[] split = ret[0].split(":");
					type = Integer.parseInt(split[0]);
					dat = Byte.parseByte(split[1]);
					amt = Integer.parseInt(ret[1]);
				}
				else
				{
					type = Integer.parseInt(ret[0]);
					amt = Integer.parseInt(ret[1]);
				}
				
				ArenaReward ar = new ArenaReward(type, dat, amt);
				rewards.add(ar);
			}
		}
		catch (Exception e)
		{
			plugin.getLogger().severe("Error loading reward from string \"" + str + "\": " + e.getMessage());
		}
	}
	
	public int getRewardType(String str)
	{
		int ret = -1;
		try { ret = Integer.parseInt(str); }
		catch(Exception e) { }
		
		if (str.contains(":")) 
		{
			str = str.substring(0, str.indexOf(":"));
			ret = Integer.parseInt(str);
		}
		return ret;
	}
	
	public int getRewardData(String str)
	{
		int ret = -1;
		try { ret = Integer.parseInt(str); }
		catch(Exception e) { }
		
		if (str.contains(":")) 
		{
			str = str.substring(str.indexOf(":") + 1);
			ret = Integer.parseInt(str);
		}
		return ret;
	}
	
	public void load() 
	{
		List<String> file = new ArrayList<String>();
	    try
	    {
			FileInputStream fstream = new FileInputStream(this.file.getAbsolutePath());
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			while ((strLine = br.readLine()) != null)
			{
				file.add(strLine);
			}
			br.close();
			in.close();
			fstream.close();
        }
	    catch (Exception e)
	    {
        	plugin.getLogger().severe("Error loading files: " + e);
        }
	    
	    for (int i= 0; i < file.size(); i++) 
	    {
	    	computeData(file.get(i));
	    }
	}
}