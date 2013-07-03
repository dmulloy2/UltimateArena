package net.dmulloy2.ultimatearena.arenas.objects;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.earth2me.essentials.IEssentials;
import net.dmulloy2.ultimatearena.UltimateArena;

public class ArenaClass 
{
	private String name;
	private String permissionNode;
	
	private List<ItemStack> armor = new ArrayList<ItemStack>();
	private List<ItemStack> weapons = new ArrayList<ItemStack>();
	
	private boolean loaded = true;
	private boolean helmet = true;
	
	private boolean useEssentials = false;
	private String essKitName;
	private Map<String, Object> essentialsKit;
	
	private boolean hasPotionEffects = false;
	private List<PotionEffect> potionEffects = new ArrayList<PotionEffect>();
	
	private final UltimateArena plugin;
	private File file;
	
	public ArenaClass(final UltimateArena plugin, File file)
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
					ItemStack stack = buildItemStack(Integer.parseInt(split[0]), 1, (byte) 0,
							readArmorEnchantments(split[1]));
					armor.add(stack);
				}
				else
				{
					ItemStack stack = buildItemStack(Integer.parseInt(arm1), 1, (byte) 0, null);
					armor.add(stack);
				}
			}
			
			String arm2 = fc.getString(armorPath + "leggings");
			if (arm2 != null)
			{
				if (arm2.contains(","))
				{
					String[] split = arm2.split(",");
					ItemStack stack = buildItemStack(Integer.parseInt(split[0]), 1, (byte) 0,
							readArmorEnchantments(split[1]));
					armor.add(stack);
				}
				else
				{
					ItemStack stack = buildItemStack(Integer.parseInt(arm2), 1, (byte) 0, null);
					armor.add(stack);
				}
			}
			
			String arm3 = fc.getString(armorPath + "boots");
			if (arm3 != null)
			{
				if (arm3.contains(","))
				{
					String[] split = arm3.split(",");
					ItemStack stack = buildItemStack(Integer.parseInt(split[0]), 1, (byte) 0,
							readArmorEnchantments(split[1]));
					armor.add(stack);
				}
				else
				{
					ItemStack stack = buildItemStack(Integer.parseInt(arm3), 1, (byte) 0, null);
					armor.add(stack);
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
				
				weapons.add(buildItemStack(value, value2, (byte) value3, value4));
			}
			
			String tool2 = fc.getString(toolPath + 2);
			if (tool2 != null)
			{
				int value = readWep(tool2);
				int value2 = readSpec(tool2);
				int value3 = readAmt(tool2);
				List<CompositeEnchantment> value4 = readToolEnchantments(tool2);
				
				weapons.add(buildItemStack(value, value2, (byte) value3, value4));
			}
			
			String tool3 = fc.getString(toolPath + 3);
			if (tool3 != null)
			{
				int value = readWep(tool3);
				int value2 = readSpec(tool3);
				int value3 = readAmt(tool3);
				List<CompositeEnchantment> value4 = readToolEnchantments(tool3);
				
				weapons.add(buildItemStack(value, value2, (byte) value3, value4));
			}
			
			String tool4 = fc.getString(toolPath + 4);
			if (tool4 != null)
			{
				int value = readWep(tool4);
				int value2 = readSpec(tool4);
				int value3 = readAmt(tool4);
				List<CompositeEnchantment> value4 = readToolEnchantments(tool4);
				
				weapons.add(buildItemStack(value, value2, (byte) value3, value4));
			}
			
			String tool5 = fc.getString(toolPath + 5);
			if (tool5 != null)
			{
				int value = readWep(tool5);
				int value2 = readSpec(tool5);
				int value3 = readAmt(tool5);
				List<CompositeEnchantment> value4 = readToolEnchantments(tool5);
				
				weapons.add(buildItemStack(value, value2, (byte) value3, value4));
			}
			
			String tool6 = fc.getString(toolPath + 6);
			if (tool6 != null)
			{
				int value = readWep(tool6);
				int value2 = readSpec(tool6);
				int value3 = readAmt(tool6);
				List<CompositeEnchantment> value4 = readToolEnchantments(tool6);
				
				weapons.add(buildItemStack(value, value2, (byte) value3, value4));
			}
			
			String tool7 = fc.getString(toolPath + 7);
			if (tool7 != null)
			{
				int value = readWep(tool7);
				int value2 = readSpec(tool7);
				int value3 = readAmt(tool7);
				List<CompositeEnchantment> value4 = readToolEnchantments(tool7);
				
				weapons.add(buildItemStack(value, value2, (byte) value3, value4));
			}
			
			String tool8 = fc.getString(toolPath + 8);
			if (tool8 != null)
			{
				int value = readWep(tool8);
				int value2 = readSpec(tool8);
				int value3 = readAmt(tool8);
				List<CompositeEnchantment> value4 = readToolEnchantments(tool8);
				
				weapons.add(buildItemStack(value, value2, (byte) value3, value4));
			}
			
			String tool9 = fc.getString(toolPath + 9);
			if (tool9 != null)
			{
				int value = readWep(tool9);
				int value2 = readSpec(tool9);
				int value3 = readAmt(tool9);
				List<CompositeEnchantment> value4 = readToolEnchantments(tool9);
				
				weapons.add(buildItemStack(value, value2, (byte) value3, value4));
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
						this.essentialsKit = kit;
					}
				}
				
				this.essKitName = line;
			}
			
			if (fc.get("hasPotionEffects") != null)
			{
				hasPotionEffects = fc.getBoolean("hasPotionEffects");
			
				if (hasPotionEffects)
				{
					String effects = fc.getString("potionEffects");
					if (effects != null)
					{
						this.potionEffects = readPotionEffects(effects);
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
		
		str = str.replaceAll(" ", "");
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
	
	private ItemStack buildItemStack(int id, int amt, byte dat, List<CompositeEnchantment> enchants)
	{
		if (id > 0)
		{
			ItemStack itemStack = new ItemStack(id, amt);
			if (enchants != null && enchants.size() > 0)
			{
				for (CompositeEnchantment enchantment : enchants)
				{
					Enchantment ench = enchantment.getType();
					int level = enchantment.getLevel();
					
					if (ench != null && level > 0)
					{
						itemStack.addUnsafeEnchantment(ench, level);
					}
				}
			}
			
			if (dat > 0)
			{
				MaterialData data = itemStack.getData();
				data.setData(dat);
				itemStack.setData(data);
			}
			
			return itemStack;
		}
		
		return null;
	}
	
	public List<ItemStack> getArmor()
	{
		return armor;
	}
	
	public List<ItemStack> getWeapons()
	{
		return weapons;
	}
	
	public ItemStack getArmor(int index)
	{
		if (armor.size() >= index)
		{
			return armor.get(index);
		}
		
		return null;
	}
	
	public ItemStack getWeapon(int index)
	{
		if (weapons.size() >= index)
		{
			return weapons.get(index);
		}
		
		return null;
	}
	
	public boolean usesHelmet()
	{
		return helmet;
	}
	
	public boolean usesEssentials()
	{
		return useEssentials;
	}

	public String getEssKitName() 
	{
		return essKitName;
	}

	public Map<String, Object> getEssentialsKit()
	{
		return essentialsKit;
	}

	public List<PotionEffect> getPotionEffects() 
	{
		return potionEffects;
	}
	
	public String getName()
	{
		return name;
	}
	
	public boolean hasPotionEffects()
	{
		return hasPotionEffects;
	}
}