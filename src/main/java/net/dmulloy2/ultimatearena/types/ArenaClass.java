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
import net.ess3.api.IEssentials;

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
			YamlConfiguration fc = YamlConfiguration.loadConfiguration(file);

			String armorPath = "armor.";
			String arm1 = fc.getString(armorPath + "chestplate");
			if (arm1 != null)
			{
				if (arm1.contains(","))
				{
					String[] split = arm1.split(",");
					ItemStack stack = buildItemStack(Integer.parseInt(split[0]), 1, (byte) 0, readArmorEnchantments(split[1]));
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
					ItemStack stack = buildItemStack(Integer.parseInt(split[0]), 1, (byte) 0, readArmorEnchantments(split[1]));
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
					ItemStack stack = buildItemStack(Integer.parseInt(split[0]), 1, (byte) 0, readArmorEnchantments(split[1]));
					armor.add(stack);
				}
				else
				{
					ItemStack stack = buildItemStack(Integer.parseInt(arm3), 1, (byte) 0, null);
					armor.add(stack);
				}
			}

			for (int i = 0; i < 9; i++)
			{
				String path = "tools." + i;
				if (fc.isSet(path))
				{
					String entry = fc.getString(path);
					entry = entry.replaceAll(" ", "");
					if (entry.startsWith("potion:"))
					{
						ItemStack stack = ItemUtil.readPotion(entry);
						if (stack != null)
						{
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
					plugin.outConsole(Level.WARNING, "Could not load Essentials kit for class {0}: {1}", 
							name, e instanceof ClassNotFoundException || e instanceof NoSuchMethodError ? "outdated Essentials!" : e);
				}
			}

			hasPotionEffects = fc.getBoolean("hasPotionEffects", false);

			if (hasPotionEffects)
			{
				potionEffects = readPotionEffects(fc.getString("potionEffects"));
			}

			usesHelmet = fc.getBoolean("useHelmet", true);
			
			permissionNode = fc.getString("permissionNode", "");
		}
		catch (Exception e)
		{
			plugin.outConsole(Level.SEVERE, "Error loading class \"{0}\": {1}", name, e.getMessage());
			return false;
		}

		plugin.debug("Successfully loaded class {0}!", name);
		return true;
	}

	@SuppressWarnings("deprecation")
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
					try
					{
						type = PotionEffectType.getByName(split1[0]);
					}
					catch (Exception e)
					{
						type = PotionEffectType.getById(Integer.parseInt(split1[0]));
					}

					try
					{
						strength = Integer.parseInt(split1[1]);
					}
					catch (Exception e)
					{
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
				PotionEffectType type = null;
				int strength = 0;

				String[] split1 = str.split(":");
				try
				{
					type = PotionEffectType.getByName(split1[0]);
				}
				catch (Exception e)
				{
					type = PotionEffectType.getById(Integer.parseInt(split1[0]));
				}

				try
				{
					strength = Integer.parseInt(split1[1]);
				}
				catch (Exception e)
				{
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

	@SuppressWarnings("deprecation")
	private ItemStack buildItemStack(int id, int amt, short dat, Map<Enchantment, Integer> enchants)
	{
		if (id > 0)
		{
			ItemStack itemStack = new ItemStack(id, amt, dat);
			if (enchants != null && enchants.size() > 0)
			{
				for (Entry<Enchantment, Integer> entry : enchants.entrySet())
				{
					Enchantment ench = entry.getKey();
					int level = entry.getValue();

					if (ench != null && level > 0)
					{
						itemStack.addUnsafeEnchantment(ench, level);
					}
				}
			}

			return itemStack;
		}

		return null;
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