package com.orange451.UltimateArena.Arenas.Objects;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.earth2me.essentials.IEssentials;
import com.orange451.UltimateArena.UltimateArena;

public class ArenaClass 
{
	public String name;
	public String permissionNode = "";
	
	public int armor1 = 0;
	public int armor2 = 0;
	public int armor3 = 0;
	
	public List<CompositeEnchantment> armorenchant1 = new ArrayList<CompositeEnchantment>();
	public List<CompositeEnchantment> armorenchant2 = new ArrayList<CompositeEnchantment>();
	public List<CompositeEnchantment> armorenchant3 = new ArrayList<CompositeEnchantment>();
	
	public int weapon1 = 0;
	public int weapon2 = 0;
	public int weapon3 = 0;
	public int weapon4 = 0;
	public int weapon5 = 0;
	public int weapon6 = 0;
	public int weapon7 = 0;
	public int weapon8 = 0;
	public int weapon9 = 0;
	
	public int amt1 = 1;
	public int amt2 = 1;
	public int amt3 = 1;
	public int amt4 = 1;
	public int amt5 = 1;
	public int amt6 = 1;
	public int amt7 = 1;
	public int amt8 = 1;
	public int amt9 = 1;
	
	public byte special1 = 0;
	public byte special2 = 0;
	public byte special3 = 0;
	public byte special4 = 0;
	public byte special5 = 0;
	public byte special6 = 0;
	public byte special7 = 0;
	public byte special8 = 0;
	public byte special9 = 0;
	
	public List<CompositeEnchantment> enchant1 = new ArrayList<CompositeEnchantment>();
	public List<CompositeEnchantment> enchant2 = new ArrayList<CompositeEnchantment>();
	public List<CompositeEnchantment> enchant3 = new ArrayList<CompositeEnchantment>();
	public List<CompositeEnchantment> enchant4 = new ArrayList<CompositeEnchantment>();
	public List<CompositeEnchantment> enchant5 = new ArrayList<CompositeEnchantment>();
	public List<CompositeEnchantment> enchant6 = new ArrayList<CompositeEnchantment>();
	public List<CompositeEnchantment> enchant7 = new ArrayList<CompositeEnchantment>();
	public List<CompositeEnchantment> enchant8 = new ArrayList<CompositeEnchantment>();
	public List<CompositeEnchantment> enchant9 = new ArrayList<CompositeEnchantment>();
	
	public boolean loaded = true;
	public boolean helmet = true;
	
	public boolean useEssentials = false;
	public String essKitName = "";
	public Map<String, Object> essentialsKit;
	
	public boolean hasPotionEffects = false;
	public List<PotionEffect> potionEffects = new ArrayList<PotionEffect>();
	
	public UltimateArena plugin;
	public File file;
	
	public ArenaClass(UltimateArena plugin, File file)
	{
		this.plugin = plugin;
		this.file = file;
		this.name = getName(file);

		this.loaded = load();
		if (!loaded)
		{
			plugin.getLogger().warning("Failed to load class: " + name + "!");
		}
	}
	
	public boolean load()
	{
		try
		{
			YamlConfiguration fc = YamlConfiguration.loadConfiguration(file);
			
			String armorPath = "armor.";
			String arm1 = fc.getString(armorPath + "chestplate");
			if (arm1 != null)
			{
				if (arm1.contains(","))
				{
					String[] split = arm1.split(",");
					armor1 = Integer.parseInt(split[0]);
					armorenchant1 = readArmorEnchantments(split[1]);
				}
				else
				{
					armor1 = Integer.parseInt(arm1);
				}
			}
			
			String arm2 = fc.getString(armorPath + "leggings");
			if (arm2 != null)
			{
				if (arm2.contains(","))
				{
					String[] split = arm2.split(",");
					armor2 = Integer.parseInt(split[0]);
					armorenchant2 = readArmorEnchantments(split[1]);
				}
				else
				{
					armor2 = Integer.parseInt(arm2);
				}
			}
			
			String arm3 = fc.getString(armorPath + "boots");
			if (arm3 != null)
			{
				if (arm3.contains(","))
				{
					String[] split = arm3.split(",");
					armor3 = Integer.parseInt(split[0]);
					armorenchant3 = readArmorEnchantments(split[1]);
				}
				else
				{
					armor3 = Integer.parseInt(arm3);
				}
			}
			
			String toolPath = "tools.";
			String tool1 = fc.getString(toolPath + 1);
			if (tool1 != null)
			{
				int value = readWep(tool1);
				int value2 = readSpec(tool1);
				int value3 = readAmt(tool1);
				List<CompositeEnchantment> value4 = readToolEnchantments(tool1);
				weapon1 = value;
				special1 = (byte) value2;
				amt1 = value3;
				enchant1 = value4;
			}
			
			String tool2 = fc.getString(toolPath + 2);
			if (tool2 != null)
			{
				int value = readWep(tool2);
				int value2 = readSpec(tool2);
				int value3 = readAmt(tool2);
				List<CompositeEnchantment> value4 = readToolEnchantments(tool2);
				weapon2 = value;
				special2 = (byte) value2;
				amt2 = value3;
				enchant2 = value4;
			}
			
			String tool3 = fc.getString(toolPath + 3);
			if (tool3 != null)
			{
				int value = readWep(tool3);
				int value2 = readSpec(tool3);
				int value3 = readAmt(tool3);
				List<CompositeEnchantment> value4 = readToolEnchantments(tool3);
				weapon3 = value;
				special3 = (byte) value2;
				amt3 = value3;
				enchant3 = value4;
			}
			
			String tool4 = fc.getString(toolPath + 4);
			if (tool4 != null)
			{
				int value = readWep(tool4);
				int value2 = readSpec(tool4);
				int value3 = readAmt(tool4);
				List<CompositeEnchantment> value4 = readToolEnchantments(tool4);
				weapon4 = value;
				special4 = (byte) value2;
				amt4 = value3;
				enchant4 = value4;
			}
			
			String tool5 = fc.getString(toolPath + 5);
			if (tool5 != null)
			{
				int value = readWep(tool5);
				int value2 = readSpec(tool5);
				int value3 = readAmt(tool5);
				List<CompositeEnchantment> value4 = readToolEnchantments(tool5);
				weapon5 = value;
				special5 = (byte) value2;
				amt5 = value3;
				enchant5 = value4;
			}
			
			String tool6 = fc.getString(toolPath + 6);
			if (tool6 != null)
			{
				int value = readWep(tool6);
				int value2 = readSpec(tool6);
				int value3 = readAmt(tool6);
				List<CompositeEnchantment> value4 = readToolEnchantments(tool6);
				weapon6 = value;
				special6 = (byte) value2;
				amt6 = value3;
				enchant6 = value4;
			}
			
			String tool7 = fc.getString(toolPath + 7);
			if (tool7 != null)
			{
				int value = readWep(tool7);
				int value2 = readSpec(tool7);
				int value3 = readAmt(tool7);
				List<CompositeEnchantment> value4 = readToolEnchantments(tool7);
				weapon7 = value;
				special7 = (byte) value2;
				amt7 = value3;
				enchant7 = value4;
			}
			
			String tool8 = fc.getString(toolPath + 8);
			if (tool8 != null)
			{
				int value = readWep(tool8);
				int value2 = readSpec(tool8);
				int value3 = readAmt(tool8);
				List<CompositeEnchantment> value4 = readToolEnchantments(tool8);
				weapon8 = value;
				special8 = (byte) value2;
				amt8 = value3;
				enchant8 = value4;
			}
			
			String tool9 = fc.getString(toolPath + 9);
			if (tool9 != null)
			{
				int value = readWep(tool9);
				int value2 = readSpec(tool9);
				int value3 = readAmt(tool9);
				List<CompositeEnchantment> value4 = readToolEnchantments(tool9);
				weapon9 = value;
				special9 = (byte) value2;
				amt9 = value3;
				enchant9 = value4;
			}
			
			useEssentials = fc.getBoolean("useEssentials");
			
			if (useEssentials)
			{
				String line = fc.getString("essentialsKit");
				
				// Initialize Essentials Hook
				PluginManager pm = plugin.getServer().getPluginManager();
				if (pm.isPluginEnabled("Essentials"))
				{
					Plugin essPlugin = pm.getPlugin("Essentials");
					IEssentials ess = (IEssentials) essPlugin;
					Map<String, Object> kit = ess.getSettings().getKit(line);
					if (kit != null)
					{
						essentialsKit = kit;
					}
				}
				
				essKitName = line;
			}
			
			if (fc.get("hasPotionEffects") != null)
			{
				hasPotionEffects = fc.getBoolean("hasPotionEffects");
			
				if (hasPotionEffects)
				{
					String effects = fc.getString("potionEffects");
					if (effects != null)
					{
						potionEffects = readPotionEffects(effects);
					}
					else
					{
						fc.set("potionEffects", "");
					}
				}
			}
			else
			{
				fc.set("hasPotionEffects", false);
				fc.set("potionEffects", "");
			}
			
			helmet = fc.getBoolean("useHelmet");
			
			String node = fc.getString("permissionNode");
			if (node != null)
			{
				permissionNode = node;
			}
			
			fc.save(file);
		}
		catch (Exception e)
		{
			plugin.getLogger().severe("Error loading class \"" + name + "\": " + e.getMessage());
			return false;
		}
		
		return true;
	}
	
	public int readWep(String str) 
	{
		int ret = 0;
		if (str.contains(","))
		{
			str = str.substring(0, str.indexOf(","));
		}
		try{ ret = Integer.parseInt(str); }catch(Exception e) { }
		if (str.contains(":"))
		{
			str = str.substring(0, str.indexOf(":"));
			ret = Integer.parseInt(str);
		}
		return ret;
	}
	
	public int readSpec(String str)
	{
		int ret = 0;
		if (str.contains(",")) 
		{
			str = str.substring(0, str.indexOf(","));
		}
		try{ ret = Integer.parseInt(str); }catch(Exception e) { }
		if (str.contains(":")) 
		{
			str = str.substring(str.indexOf(":") + 1);
			ret = Integer.parseInt(str);
		}
		return ret;
	}
	
	public int readAmt(String str) 
	{
		int ret = 1;
		if (str.contains(",")) 
		{
			str = str.substring(str.indexOf(",") + 1);
			try{ ret = Integer.parseInt(str); }catch(Exception e) { }
		}
		return ret;
	}
	
	public List<PotionEffect> readPotionEffects(String str)
	{
		List<PotionEffect> ret = new ArrayList<PotionEffect>();
		if (str.contains(","))
		{
			String[] split = str.split(",");
			for (String s : split)
			{
				if (s.contains(":"))
				{
					PotionEffectType type = null;
					int strength = 0;
										
					String[] split1 = s.split(":");
					try { type = PotionEffectType.getByName(split1[0]); }
					catch (Exception e) { type = PotionEffectType.getById(Integer.parseInt(split1[0])); }

					try { strength = Integer.parseInt(split1[1]); }
					catch (Exception e) {}
					
					if (type != null)
					{
						ret.add(new PotionEffect(type, Integer.MAX_VALUE, strength));
					}
				}
			}
		}
		else
		{
			if (str.contains(":"))
			{
				PotionEffectType type = null;
				int strength = 0;
									
				String[] split1 = str.split(":");
				try { type = PotionEffectType.getByName(split1[0]); }
				catch (Exception e) { type = PotionEffectType.getById(Integer.parseInt(split1[0])); }

				try { strength = Integer.parseInt(split1[1]); }
				catch (Exception e) {}
				
				if (type != null)
				{
					ret.add(new PotionEffect(type, Integer.MAX_VALUE, strength));
				}
			}
		}
		
		return ret;
	}
	
	public List<CompositeEnchantment> readArmorEnchantments(String string)
	{
		List<CompositeEnchantment> enchants = new ArrayList<CompositeEnchantment>();
		if (string.contains(":"))
		{
			String[] split2 = string.split(":");
					
			Enchantment enchantment = null;
			try { enchantment = EnchantmentType.toEnchantment(split2[0]); }
			catch (Exception e) { enchantment = Enchantment.getByName(split2[0].toUpperCase()); }
					
			int level = 0;
			try { level = Integer.parseInt(split2[1]); }
			catch (Exception e) {}
					
			if (enchantment != null && level > 0)
			{
				enchants.add(new CompositeEnchantment(enchantment, level));
			}
		}
		return enchants;
	}
	
	public List<CompositeEnchantment> readToolEnchantments(String str)
	{
		List<CompositeEnchantment> enchants = new ArrayList<CompositeEnchantment>();
		if (str.contains(","))
		{
			String[] split = str.split(",");
			if (split.length > 2)
			{
				for (int i=2; i<split.length; i++)
				{
					String s = split[i];
					if (s.contains(":"))
					{
						String[] split2 = s.split(":");
						Enchantment enchantment = null;
						try { enchantment = EnchantmentType.toEnchantment(split2[0]); }
						catch (Exception e) { enchantment = Enchantment.getByName(split2[0].toUpperCase()); }
						
						int level = 0;
						try { level = Integer.parseInt(split2[1]); }
						catch (Exception e) {}
						
						if (enchantment != null && level > 0)
						{
							enchants.add(new CompositeEnchantment(enchantment, level));
						}
					}
				}
			}
		}
		return enchants;
	}

	public boolean checkPermission(Player player)
	{
		if (permissionNode.equals(""))
			return true;
			
		return plugin.getPermissionHandler().hasPermission(player, permissionNode);
	}	
	
	public String getName(File file)
	{
		return file.getName().replaceAll(".yml", "");
	}
}