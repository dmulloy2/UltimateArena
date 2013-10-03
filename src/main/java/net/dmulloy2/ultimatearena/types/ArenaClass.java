package net.dmulloy2.ultimatearena.types;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import lombok.Getter;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.util.ItemUtil;
import net.dmulloy2.ultimatearena.util.MaterialUtil;
import net.dmulloy2.ultimatearena.util.Util;
import net.ess3.api.IEssentials;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * @author dmulloy2
 */

@Getter
public class ArenaClass
{
	private String name;
	private String permissionNode;

	private List<ItemStack> armor = new ArrayList<ItemStack>();
	private List<ItemStack> weapons = new ArrayList<ItemStack>();

	private boolean loaded = true;
	private boolean usesHelmet = true;

	// Essentials Integration
	private String essKitName;
	private boolean usesEssentials = false;
	private Map<String, Object> essentialsKit;

	// Potion Effects
	private boolean hasPotionEffects = false;
	private List<PotionEffect> potionEffects = new ArrayList<PotionEffect>();

	private File file;
	private final UltimateArena plugin;

	public ArenaClass(UltimateArena plugin, File file)
	{
		this.plugin = plugin;
		this.file = file;
		this.name = getName(file);

		this.loaded = load();
		if (! loaded)
		{
			plugin.outConsole(Level.WARNING, "Failed to load class {0}!", name);
		}
	}

	public boolean load()
	{	
		try
		{
			boolean save = false;
			
			YamlConfiguration fc = YamlConfiguration.loadConfiguration(file);

			String[] armor = new String[] { "chestplate", "leggings", "boots" };
			
			for (String armorPath : armor)
			{
				if (fc.isSet("armor." + armorPath))
				{	
					Material mat = null;
					
					Map<Enchantment, Integer> enchants = new HashMap<Enchantment, Integer>();
					
					String arm = fc.getString("armor." + armorPath);
					if (arm.contains(","))
					{
						String[] split = arm.split(",");
						
						mat = MaterialUtil.getMaterial(split[0]);

						StringBuilder line = new StringBuilder();
						for (int i = 1; i < split.length; i++)
						{
							line.append(split[i] + " ");
						}
						
						line.delete(line.length() - 1, line.length());
						
						enchants = readArmorEnchantments(line.toString());
					}
					else
					{
						mat = MaterialUtil.getMaterial(arm);
					}
					
					ItemStack stack = new ItemStack(mat, 1);
					
					if (! enchants.isEmpty())
					{
						for (Entry<Enchantment, Integer> entry : enchants.entrySet())
						{
							stack.addUnsafeEnchantment(entry.getKey(), entry.getValue());
						}
					}
					
					this.armor.add(stack);
				}
			}

			for (int i = 0; i < 9; i++)
			{
				String path = "tools." + i;
				if (fc.isSet(path))
				{
					try
					{
						String entry = fc.getString(path);
						entry = entry.replaceAll(" ", "");
						if (entry.startsWith("potion:"))
						{
							ItemStack stack = ItemUtil.readPotion(entry);
							if (stack != null)
							{
								plugin.debug("Detected deprecated potion entry. Converting!");
	
								fc.set(path, stack.getType().toString() + ":" + stack.getDurability() + "," + stack.getAmount());
								save = true;
	
								weapons.add(stack);
							}
						}
						else
						{
							ItemStack stack = ItemUtil.readItem(entry);
							if (stack != null)
							{
								weapons.add(stack);
							}
						}
					}
					catch (Exception e)
					{
						plugin.outConsole(Level.SEVERE, "Exception occured while loading class {0}", name);
						plugin.outConsole(Level.SEVERE, "Could not parse item \"{0}\": {1}", fc.getString(path), e);
						
						plugin.debug(Util.getUsefulStack(e));
					}
				}
			}

			usesEssentials = fc.getBoolean("useEssentials", false);

			if (usesEssentials)
			{
				try
				{
					String line = fc.getString("essentialsKit", "");

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
				catch (Throwable e)
				{
					plugin.outConsole(Level.WARNING, "Could not load Essentials kit for class {0}: {1}", name, 
							e instanceof ClassNotFoundException || e instanceof NoSuchMethodError ? "outdated Essentials!" : e.getMessage());
					
					plugin.debug(Util.getUsefulStack(e));
				}
			}

			hasPotionEffects = fc.getBoolean("hasPotionEffects", false);

			if (hasPotionEffects)
			{
				potionEffects = readPotionEffects(fc.getString("potionEffects"));
			}

			usesHelmet = fc.getBoolean("useHelmet", true);
			
			permissionNode = fc.getString("permissionNode", "");
			
			// Save the file if changes were made
			if (save) fc.save(file);
		}
		catch (Exception e)
		{
			plugin.outConsole(Level.SEVERE, "Error loading class \"{0}\": {1}", name, e.getMessage());
			
			plugin.debug(Util.getUsefulStack(e));
			return false;
		}

		plugin.debug("Successfully loaded class {0}!", name);
		return true;
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
					String[] split1 = s.split(":");

					PotionEffectType type = PotionEffectType.getByName(split1[0]);

					int strength = 0;
					
					try
					{
						strength = Integer.parseInt(split1[1]);
					}
					catch (Exception e)
					{
						//
					}

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
				String[] split1 = str.split(":");
				
				PotionEffectType type = PotionEffectType.getByName(split1[0]);

				int strength = 0;
				
				try
				{
					strength = Integer.parseInt(split1[1]);
				}
				catch (Exception e)
				{
					//
				}

				if (type != null)
				{
					ret.add(new PotionEffect(type, Integer.MAX_VALUE, strength));
				}
			}
		}

		return ret;
	}

	public Map<Enchantment, Integer> readArmorEnchantments(String string)
	{
		Map<Enchantment, Integer> enchants = new HashMap<Enchantment, Integer>();
		if (string.contains(":"))
		{
			String[] split2 = string.split(":");

			Enchantment enchantment = null;
			try
			{
				enchantment = EnchantmentType.toEnchantment(split2[0]);
			}
			catch (Exception e)
			{
				enchantment = Enchantment.getByName(split2[0].toUpperCase());
			}

			int level = 0;
			try
			{
				level = Integer.parseInt(split2[1]);
			}
			catch (Exception e)
			{
			}

			if (enchantment != null && level > 0)
			{
				enchants.put(enchantment, level);
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
}