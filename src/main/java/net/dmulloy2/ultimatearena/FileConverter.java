package net.dmulloy2.ultimatearena;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import net.dmulloy2.ultimatearena.arenas.objects.ArenaReward;
import net.dmulloy2.ultimatearena.arenas.objects.CompositeEnchantment;
import net.dmulloy2.ultimatearena.arenas.objects.EnchantmentType;
import net.dmulloy2.ultimatearena.arenas.objects.OldArenaClass;
import net.dmulloy2.ultimatearena.arenas.objects.OldArenaConfig;
import net.dmulloy2.ultimatearena.arenas.objects.OldArenaZone;

/**
 * @author dmulloy2
 */

@SuppressWarnings("deprecation")
public class FileConverter 
{
	private UltimateArena plugin;
	public FileConverter(UltimateArena plugin)
	{
		this.plugin = plugin;
	}
	
	public void run()
	{
		File[] files = plugin.getDataFolder().listFiles();
		for (File file : files)
		{
			if (file.getName().contains(".txt"))
			{
				if (file.getName().equals("players.txt"))
				{
					file.delete();
				}
				
				if (file.getName().equals("whiteListedCommands.txt"))
				{
					convertWhitelistedCommands(file);
				}
				
				if (file.getName().contains("CONFIG.txt"))
				{
					convertArenaConfig(file);
				}
			}
		}
		
		File arenaFolder = new File(plugin.getDataFolder(), "arenas");
		File[] arenaFiles = arenaFolder.listFiles();
		for (File arenaFile : arenaFiles)
		{
			if (!arenaFile.getName().contains(".dat"))
			{
				convertArenaSave(arenaFile);
			}
		}
		
		File classFolder = new File(plugin.getDataFolder(), "classes");
		File[] classFiles = classFolder.listFiles();
		for (File classFile : classFiles)
		{
			if (!classFile.getName().contains(".yml"))
			{
				convertClass(classFile);
			}
		}
	}
	
	public void convertClass(File old)
	{
		try
		{
			OldArenaClass ac = new OldArenaClass(plugin, old);
			old.delete();
			
			File folder = new File(plugin.getDataFolder(), "classes");
			File file = new File(folder, ac.name + ".yml");
			file.createNewFile();
			
			YamlConfiguration fc = YamlConfiguration.loadConfiguration(file);
			
			if (ac.armor1 > 0)
			{
				StringBuilder line = new StringBuilder();
				line.append(ac.armor1);
				
				if (ac.armorenchant1.size() > 0)
				{
					line.append(",");
					for (CompositeEnchantment ench : ac.armorenchant1)
					{
						line.append(EnchantmentType.toName(ench.getType()) + ":");
						line.append(ench.getLevel() + ",");
					}
					line.deleteCharAt(line.lastIndexOf(","));
				}
				
				String string = line.toString();
				fc.set("armor.chestplate", string);
			}
			
			if (ac.armor2 > 0)
			{
				StringBuilder line = new StringBuilder();
				line.append(ac.armor2);
				
				if (ac.armorenchant2.size() > 0)
				{
					line.append(",");
					for (CompositeEnchantment ench : ac.armorenchant2)
					{
						line.append(EnchantmentType.toName(ench.getType()) + ":");
						line.append(ench.getLevel() + ",");
					}
					line.deleteCharAt(line.lastIndexOf(","));	
				}
				
				String string = line.toString();
				fc.set("armor.leggings", string);
			}
			
			if (ac.armor3 > 0)
			{
				StringBuilder line = new StringBuilder();
				line.append(ac.armor3);
				
				if (ac.armorenchant3.size() > 0)
				{
					line.append(",");
					for (CompositeEnchantment ench : ac.armorenchant3)
					{
						line.append(EnchantmentType.toName(ench.getType()) + ":");
						line.append(ench.getLevel() + ",");
					}
					line.deleteCharAt(line.lastIndexOf(","));
				}
				
				String string = line.toString();
				fc.set("armor.boots", string);
			}
			
			if (ac.weapon1 > 0)
			{
				StringBuilder line = new StringBuilder();
				line.append(ac.weapon1);
				
				boolean hasData = false;
				ItemStack stack = new ItemStack(ac.weapon1, 1);
				hasData = (stack.getType().getMaxDurability() == 0 && ac.special1 > 0);
				
				if (hasData)
				{
					line.append(":" + ac.special1);
				}
				
				line.append("," + ac.amt1);
				
				if (ac.enchant1.size() > 0)
				{
					line.append(",");
					for (CompositeEnchantment ench : ac.enchant1)
					{
						line.append(EnchantmentType.toName(ench.getType()) + ":");
						line.append(ench.getLevel() + ",");
					}
					line.deleteCharAt(line.lastIndexOf(","));
				}
				
				String string = line.toString();
				fc.set("tools.1", string);
			}
			
			if (ac.weapon2 > 0)
			{
				StringBuilder line = new StringBuilder();
				line.append(ac.weapon2);
				
				boolean hasData = false;
				ItemStack stack = new ItemStack(ac.weapon2, 1);
				hasData = (stack.getType().getMaxDurability() == 0 && ac.special2 > 0);
				
				if (hasData)
				{
					line.append(":" + ac.special2);
				}
				
				line.append("," + ac.amt2);
				
				if (ac.enchant2.size() > 0)
				{
					line.append(",");
					for (CompositeEnchantment ench : ac.enchant2)
					{
						line.append(EnchantmentType.toName(ench.getType()) + ":");
						line.append(ench.getLevel() + ",");
					}
					line.deleteCharAt(line.lastIndexOf(","));
				}
				
				String string = line.toString();
				fc.set("tools.2", string);
			}
			
			if (ac.weapon3 > 0)
			{
				StringBuilder line = new StringBuilder();
				line.append(ac.weapon3);
				
				boolean hasData = false;
				ItemStack stack = new ItemStack(ac.weapon3, 1);
				hasData = (stack.getType().getMaxDurability() == 0 && ac.special3 > 0);
				
				if (hasData)
				{
					line.append(":" + ac.special3);
				}
				
				line.append("," + ac.amt3);
				
				if (ac.enchant3.size() > 0)
				{
					line.append(",");
					for (CompositeEnchantment ench : ac.enchant3)
					{
						line.append(EnchantmentType.toName(ench.getType()) + ":");
						line.append(ench.getLevel() + ",");
					}
					line.deleteCharAt(line.lastIndexOf(","));
				}

				String string = line.toString();
				fc.set("tools.3", string);
			}
			
			if (ac.weapon4 > 0)
			{
				StringBuilder line = new StringBuilder();
				line.append(ac.weapon4);
				
				boolean hasData = false;
				ItemStack stack = new ItemStack(ac.weapon4, 1);
				hasData = (stack.getType().getMaxDurability() == 0 && ac.special4 > 0);
				
				if (hasData)
				{
					line.append(":" + ac.special4);
				}
				
				line.append("," + ac.amt4);
				
				if (ac.enchant4.size() > 0)
				{
					line.append(",");
					for (CompositeEnchantment ench : ac.enchant4)
					{
						line.append(EnchantmentType.toName(ench.getType()) + ":");
						line.append(ench.getLevel() + ",");
					}
					line.deleteCharAt(line.lastIndexOf(","));
				}
				
				String string = line.toString();
				fc.set("tools.4", string);
			}
			
			if (ac.weapon5 > 0)
			{
				StringBuilder line = new StringBuilder();
				line.append(ac.weapon5);
				
				boolean hasData = false;
				ItemStack stack = new ItemStack(ac.weapon5, 1);
				hasData = (stack.getType().getMaxDurability() == 0 && ac.special5 > 0);
				
				if (hasData)
				{
					line.append(":" + ac.special5);
				}
				
				line.append("," + ac.amt5);
				
				if (ac.enchant5.size() > 0)
				{
					line.append(",");
					for (CompositeEnchantment ench : ac.enchant5)
					{
						line.append(EnchantmentType.toName(ench.getType()) + ":");
						line.append(ench.getLevel() + ",");
					}
					line.deleteCharAt(line.lastIndexOf(","));
				}

				String string = line.toString();
				fc.set("tools.5", string);
			}
			
			if (ac.weapon6 > 0)
			{
				StringBuilder line = new StringBuilder();
				line.append(ac.weapon6);
				
				boolean hasData = false;
				ItemStack stack = new ItemStack(ac.weapon6, 1);
				hasData = (stack.getType().getMaxDurability() == 0 && ac.special6 > 0);
				
				if (hasData)
				{
					line.append(":" + ac.special6);
				}
				
				line.append("," + ac.amt6);
				
				if (ac.enchant6.size() > 0)
				{
					line.append(",");
					for (CompositeEnchantment ench : ac.enchant6)
					{
						line.append(EnchantmentType.toName(ench.getType()) + ":");
						line.append(ench.getLevel() + ",");
					}
					line.deleteCharAt(line.lastIndexOf(","));
				}
				
				String string = line.toString();
				fc.set("tools.6", string);
			}
			
			if (ac.weapon7 > 0)
			{
				StringBuilder line = new StringBuilder();
				line.append(ac.weapon7);
				
				boolean hasData = false;
				ItemStack stack = new ItemStack(ac.weapon7, 1);
				hasData = (stack.getType().getMaxDurability() == 0 && ac.special7 > 0);
				
				if (hasData)
				{
					line.append(":" + ac.special7);
				}
				
				line.append("," + ac.amt7);
				
				if (ac.enchant7.size() > 0)
				{
					line.append(",");
					for (CompositeEnchantment ench : ac.enchant7)
					{
						line.append(EnchantmentType.toName(ench.getType()) + ":");
						line.append(ench.getLevel() + ",");
					}
					line.deleteCharAt(line.lastIndexOf(","));
				}
				
				String string = line.toString();
				fc.set("tools.7", string);
			}
			
			if (ac.weapon8 > 0)
			{
				StringBuilder line = new StringBuilder();
				line.append(ac.weapon8);
				
				boolean hasData = false;
				ItemStack stack = new ItemStack(ac.weapon8, 1);
				hasData = (stack.getType().getMaxDurability() == 0 && ac.special8 > 0);
				
				if (hasData)
				{
					line.append(":" + ac.special8);
				}
				
				line.append("," + ac.amt8);
				
				if (ac.enchant8.size() > 0)
				{
					line.append(",");
					for (CompositeEnchantment ench : ac.enchant8)
					{
						line.append(EnchantmentType.toName(ench.getType()) + ":");
						line.append(ench.getLevel() + ",");
					}
					line.deleteCharAt(line.lastIndexOf(","));
				}
				
				String string = line.toString();
				fc.set("tools.8", string);
			}
			
			if (ac.weapon9 > 0)
			{
				StringBuilder line = new StringBuilder();
				line.append(ac.weapon9);
				
				boolean hasData = false;
				ItemStack stack = new ItemStack(ac.weapon9, 1);
				hasData = (stack.getType().getMaxDurability() == 0 && ac.special9 > 0);
				
				if (hasData)
				{
					line.append(":" + ac.special9);
				}
				
				line.append("," + ac.amt9);
				
				if (ac.enchant9.size() > 0)
				{
					line.append(",");
					for (CompositeEnchantment ench : ac.enchant9)
					{
						line.append(EnchantmentType.toName(ench.getType()) + ":");
						line.append(ench.getLevel() + ",");
					}
					line.deleteCharAt(line.lastIndexOf(","));
				}
				
				String string = line.toString();
				fc.set("tools.9", string);
			}
			
			fc.set("useEssentials", ac.useEssentials);
			fc.set("essentialsKit", ac.essKitName);
			
			fc.set("useHelmet", ac.helmet);
			
			fc.set("permissionNode", ac.permissionNode);
			
			fc.save(file);
			
			plugin.getLogger().info("Successfully converted class: " + old.getName() + "!");
		}
		catch (Exception e)
		{
			plugin.getLogger().severe("Error converting class \"" + old.getName() + "\": " + e.getMessage());
		}
	}
	
	public void convertArenaSave(File old)
	{
		try
		{
			OldArenaZone az = new OldArenaZone(plugin, old);
			old.delete();
			
			File folder = new File(plugin.getDataFolder(), "arenas");
			File file = new File(folder, az.arenaName + ".dat");
			file.createNewFile();
			
			YamlConfiguration fc = YamlConfiguration.loadConfiguration(file);
			
			fc.set("type", az.arenaType);
			fc.set("world", az.world.getName());
			
			Location lobby1 = az.lobby1;
			fc.set("lobby1.x", lobby1.getBlockX());
			fc.set("lobby1.z", lobby1.getBlockZ());
			
			Location lobby2 = az.lobby2;
			fc.set("lobby2.x", lobby2.getBlockX());
			fc.set("lobby2.z", lobby2.getBlockZ());
			
			Location arena1 = az.arena1;
			fc.set("arena1.x", arena1.getBlockX());
			fc.set("arena1.z", arena1.getBlockZ());
			
			Location arena2 = az.arena2;
			fc.set("arena2.x", arena2.getBlockX());
			fc.set("arena2.z", arena2.getBlockZ());
			
			String arenaType = az.arenaType;
			if (arenaType.equals("pvp"))
			{
				Location lobbyRed = az.lobbyREDspawn;
				fc.set("lobbyRed.x", lobbyRed.getBlockX());
				fc.set("lobbyRed.y", lobbyRed.getBlockY());
				fc.set("lobbyRed.z", lobbyRed.getBlockZ());
				
				Location lobbyBlue = az.lobbyBLUspawn;
				fc.set("lobbyBlue.x", lobbyBlue.getBlockX());
				fc.set("lobbyBlue.y", lobbyBlue.getBlockY());
				fc.set("lobbyBlue.z", lobbyBlue.getBlockZ());
				
				Location team1 = az.team1spawn;
				fc.set("team1.x", team1.getBlockX());
				fc.set("team1.y", team1.getBlockY());
				fc.set("team1.z", team1.getBlockZ());
				
				Location team2 = az.team2spawn;
				fc.set("team2.x", team2.getBlockX());
				fc.set("team2.y", team2.getBlockY());
				fc.set("team2.z", team2.getBlockZ());
			}
			if (arenaType.equals("mob"))
			{
				Location lobbyRed = az.lobbyREDspawn;
				fc.set("lobbyRed.x", lobbyRed.getBlockX());
				fc.set("lobbyRed.y", lobbyRed.getBlockY());
				fc.set("lobbyRed.z", lobbyRed.getBlockZ());
				
				Location team1 = az.team1spawn;
				fc.set("team1.x", team1.getBlockX());
				fc.set("team1.y", team1.getBlockY());
				fc.set("team1.z", team1.getBlockZ());
				
				fc.set("spawnsAmt", az.spawns.size());
				for (int i = 0; i < az.spawns.size(); i++) 
				{
					Location loc = az.spawns.get(i);
					String path = "spawns." + i + ".";

					fc.set(path + "x", loc.getBlockX());
					fc.set(path + "y", loc.getBlockY());
					fc.set(path + "z", loc.getBlockZ());
				}
			}
			if (arenaType.equals("cq")) 
			{
				Location lobbyRed = az.lobbyREDspawn;
				fc.set("lobbyRed.x", lobbyRed.getBlockX());
				fc.set("lobbyRed.y", lobbyRed.getBlockY());
				fc.set("lobbyRed.z", lobbyRed.getBlockZ());
				
				Location lobbyBlue = az.lobbyBLUspawn;
				fc.set("lobbyBlue.x", lobbyBlue.getBlockX());
				fc.set("lobbyBlue.y", lobbyBlue.getBlockY());
				fc.set("lobbyBlue.z", lobbyBlue.getBlockZ());
				
				Location team1 = az.team1spawn;
				fc.set("team1.x", team1.getBlockX());
				fc.set("team1.y", team1.getBlockY());
				fc.set("team1.z", team1.getBlockZ());
				
				Location team2 = az.team2spawn;
				fc.set("team2.x", team2.getBlockX());
				fc.set("team2.y", team2.getBlockY());
				fc.set("team2.z", team2.getBlockZ());
				
				fc.set("flagsAmt", az.flags.size());
				for (int i = 0; i < az.flags.size(); i++) 
				{
					Location loc = az.flags.get(i);
					String path = "flags." + i + ".";

					fc.set(path + "x", loc.getBlockX());
					fc.set(path + "y", loc.getBlockY());
					fc.set(path + "z", loc.getBlockZ());
				}
			}
			if (arenaType.equals("koth"))
			{
				Location lobbyRed = az.lobbyREDspawn;
				fc.set("lobbyRed.x", lobbyRed.getBlockX());
				fc.set("lobbyRed.y", lobbyRed.getBlockY());
				fc.set("lobbyRed.z", lobbyRed.getBlockZ());
				
				fc.set("spawnsAmt", az.spawns.size());
				for (int i = 0; i < az.spawns.size(); i++) 
				{
					Location loc = az.spawns.get(i);
					String path = "spawns." + i + ".";

					fc.set(path + "x", loc.getBlockX());
					fc.set(path + "y", loc.getBlockY());
					fc.set(path + "z", loc.getBlockZ());
				}
				
				fc.set("flag.x", az.flags.get(0).getBlockX());
				fc.set("flag.y", az.flags.get(0).getBlockY());
				fc.set("flag.z", az.flags.get(0).getBlockZ());
			}
			if (arenaType.equals("ffa") || arenaType.equals("hunger"))
			{
				Location lobbyRed = az.lobbyREDspawn;
				fc.set("lobbyRed.x", lobbyRed.getBlockX());
				fc.set("lobbyRed.y", lobbyRed.getBlockY());
				fc.set("lobbyRed.z", lobbyRed.getBlockZ());
				
				fc.set("spawnsAmt", az.spawns.size());
				for (int i = 0; i < az.spawns.size(); i++) 
				{
					Location loc = az.spawns.get(i);
					String path = "spawns." + i + ".";

					fc.set(path + "x", loc.getBlockX());
					fc.set(path + "y", loc.getBlockY());
					fc.set(path + "z", loc.getBlockZ());
				}
				
			}
			if (arenaType.equals("spleef"))
			{
				Location lobbyRed = az.lobbyREDspawn;
				fc.set("lobbyRed.x", lobbyRed.getBlockX());
				fc.set("lobbyRed.y", lobbyRed.getBlockY());
				fc.set("lobbyRed.z", lobbyRed.getBlockZ());
				
				fc.set("specialType", 80);
				
				for (int i = 0; i < 4; i++) 
				{
					Location loc = az.flags.get(i);
					String path = "flags." + i + ".";

					fc.set(path + "x", loc.getBlockX());
					fc.set(path + "y", loc.getBlockY());
					fc.set(path + "z", loc.getBlockZ());
				}
			}
			if (arenaType.equals("bomb"))
			{
				Location lobbyRed = az.lobbyREDspawn;
				fc.set("lobbyRed.x", lobbyRed.getBlockX());
				fc.set("lobbyRed.y", lobbyRed.getBlockY());
				fc.set("lobbyRed.z", lobbyRed.getBlockZ());
				
				Location lobbyBlue = az.lobbyBLUspawn;
				fc.set("lobbyBlue.x", lobbyBlue.getBlockX());
				fc.set("lobbyBlue.y", lobbyBlue.getBlockY());
				fc.set("lobbyBlue.z", lobbyBlue.getBlockZ());
				
				Location team1 = az.team1spawn;
				fc.set("team1.x", team1.getBlockX());
				fc.set("team1.y", team1.getBlockY());
				fc.set("team1.z", team1.getBlockZ());
				
				Location team2 = az.team2spawn;
				fc.set("team2.x", team2.getBlockX());
				fc.set("team2.y", team2.getBlockY());
				fc.set("team2.z", team2.getBlockZ());
				
				fc.set("flag0.x", az.flags.get(0).getBlockX());
				fc.set("flag0.y", az.flags.get(0).getBlockY());
				fc.set("flag0.z", az.flags.get(0).getBlockZ());
				
				fc.set("flag1.x", az.flags.get(1).getBlockX());
				fc.set("flag1.y", az.flags.get(1).getBlockY());
				fc.set("flag1.z", az.flags.get(1).getBlockZ());
			}
			if (arenaType.equals("ctf")) 
			{
				Location lobbyRed = az.lobbyREDspawn;
				fc.set("lobbyRed.x", lobbyRed.getBlockX());
				fc.set("lobbyRed.y", lobbyRed.getBlockY());
				fc.set("lobbyRed.z", lobbyRed.getBlockZ());
				
				Location lobbyBlue = az.lobbyBLUspawn;
				fc.set("lobbyBlue.x", lobbyBlue.getBlockX());
				fc.set("lobbyBlue.y", lobbyBlue.getBlockY());
				fc.set("lobbyBlue.z", lobbyBlue.getBlockZ());
				
				Location team1 = az.team1spawn;
				fc.set("team1.x", team1.getBlockX());
				fc.set("team1.y", team1.getBlockY());
				fc.set("team1.z", team1.getBlockZ());
				
				Location team2 = az.team2spawn;
				fc.set("team2.x", team2.getBlockX());
				fc.set("team2.y", team2.getBlockY());
				fc.set("team2.z", team2.getBlockZ());
				
				fc.set("flag0.x", az.flags.get(0).getBlockX());
				fc.set("flag0.y", az.flags.get(0).getBlockY());
				fc.set("flag0.z", az.flags.get(0).getBlockZ());
				
				fc.set("flag1.x", az.flags.get(1).getBlockX());
				fc.set("flag1.y", az.flags.get(1).getBlockY());
				fc.set("flag1.z", az.flags.get(1).getBlockZ());
			}
			if (arenaType.equals("infect"))
			{
				Location lobbyRed = az.lobbyREDspawn;
				fc.set("lobbyRed.x", lobbyRed.getBlockX());
				fc.set("lobbyRed.y", lobbyRed.getBlockY());
				fc.set("lobbyRed.z", lobbyRed.getBlockZ());
				
				Location lobbyBlue = az.lobbyBLUspawn;
				fc.set("lobbyBlue.x", lobbyBlue.getBlockX());
				fc.set("lobbyBlue.y", lobbyBlue.getBlockY());
				fc.set("lobbyBlue.z", lobbyBlue.getBlockZ());
				
				Location team1 = az.team1spawn;
				fc.set("team1.x", team1.getBlockX());
				fc.set("team1.y", team1.getBlockY());
				fc.set("team1.z", team1.getBlockZ());
				
				Location team2 = az.team2spawn;
				fc.set("team2.x", team2.getBlockX());
				fc.set("team2.y", team2.getBlockY());
				fc.set("team2.z", team2.getBlockZ());
			}
			
			fc.set("maxPlayers", 24);
			fc.set("defaultClass", "");
			
			fc.save(file);
			
			plugin.getLogger().info("Successfully converted arena: " + old.getName() + "!");
		}
		catch (Exception e)
		{
			plugin.getLogger().severe("Error converting arena \"" + old.getName() + "\": " + e.getMessage());
		}
	}
	
	public void convertArenaConfig(File old)
	{
		try
		{
			String str = old.getName().replaceAll("CONFIG.txt", "");
			OldArenaConfig ac = new OldArenaConfig(plugin, str, old);
			old.delete();
			
			File folder = new File(plugin.getDataFolder(), "configs");
			File file = new File(folder, str + "Config.yml");
			file.createNewFile();
			
			YamlConfiguration fc = YamlConfiguration.loadConfiguration(file);
			
			String field = ac.arenaName;
			
			if (field.equals("mob"))
			{
				fc.set("gameTime", ac.gameTime);
				fc.set("lobbyTime", ac.lobbyTime);
				fc.set("maxDeaths", ac.maxDeaths);
				fc.set("allowTeamKilling", ac.allowTeamKilling);
				fc.set("maxWave", ac.maxwave);
				fc.set("cashReward", ac.cashReward);

				List<String> words = new ArrayList<String>();
				
				List<ArenaReward> rewards = ac.rewards;
				for (int i=0; i<rewards.size(); i++)
				{
					ArenaReward reward = rewards.get(i);
					
					StringBuilder line = new StringBuilder();
					line.append(reward.getType());
					
					if (reward.getData() > 0)
					{
						line.append(":" + reward.getData());
					}
					
					line.append("," + reward.getAmount());
					
					words.add(line.toString());
				}
				
				fc.set("rewards", words);

				fc.save(file);
			}
			else
			{
				fc.set("gameTime", ac.gameTime);
				fc.set("lobbyTime", ac.lobbyTime);
				fc.set("maxDeaths", ac.maxDeaths);
				fc.set("allowTeamKilling", ac.allowTeamKilling);
				fc.set("cashReward", ac.cashReward);

				List<String> words = new ArrayList<String>();
				
				List<ArenaReward> rewards = ac.rewards;
				for (int i=0; i<rewards.size(); i++)
				{
					ArenaReward reward = rewards.get(i);
					
					StringBuilder line = new StringBuilder();
					line.append(reward.getType());
					
					if (reward.getData() > 0)
					{
						line.append(":" + reward.getData());
					}
					
					line.append("," + reward.getAmount());
					
					words.add(line.toString());
				}
				
				fc.set("rewards", words);

				fc.save(file);
			}
			
			plugin.getLogger().info("Successfully converted arena config: " + old.getName() + "!");
		}
		catch (Exception e)
		{
			plugin.getLogger().severe("Error converting arena config \"" + old.getName() + "\": " + e.getMessage());
		}
	}
	
	public void convertWhitelistedCommands(File file)
	{
		file.delete();
			
		plugin.getFileHelper().generateWhitelistedCmds();
	}
}