package net.dmulloy2.ultimatearena.types;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import lombok.Getter;
import lombok.NonNull;
import net.dmulloy2.types.EnchantmentType;
import net.dmulloy2.types.Reloadable;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.ItemUtil;
import net.dmulloy2.util.MaterialUtil;
import net.dmulloy2.util.NumberUtil;
import net.dmulloy2.util.Util;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * @author dmulloy2
 */

@Getter
public final class ArenaClass implements Reloadable
{
	private boolean needsPermission;
	private String permissionNode;

	private List<ItemStack> armor = new ArrayList<ItemStack>();
	private List<ItemStack> weapons = new ArrayList<ItemStack>();

	private boolean usesHelmet = true;

	// Essentials Integration
	private String essKitName;
	private boolean usesEssentials;
	private Map<String, Object> essentialsKit = new HashMap<String, Object>();

	// Potion Effects
	private boolean hasPotionEffects;
	private List<PotionEffect> potionEffects = new ArrayList<PotionEffect>();

	// ---- Transient
	private transient File file;
	private transient String name;
	private transient boolean loaded;

	private transient final UltimateArena plugin;

	public ArenaClass(@NonNull UltimateArena plugin, @NonNull File file)
	{
		this.plugin = plugin;
		this.file = file;
		this.name = FormatUtil.trimFileExtension(file, ".yml");

		// Load
		this.loaded = load();
	}

	/**
	 * Attempts to load this class
	 *
	 * @return Whether or not loading was successful
	 */
	public final boolean load()
	{
		Validate.isTrue(! loaded, "Class has already been loaded!");

		try
		{
			boolean changes = false;
			YamlConfiguration fc = YamlConfiguration.loadConfiguration(file);

			// TODO: Rewrite this.
			for (String armorPath : armorTypes)
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
						ItemStack stack = ItemUtil.readItem(entry);
						if (stack != null)
							weapons.add(stack);
					}
					catch (Throwable ex)
					{
						plugin.outConsole(Level.SEVERE, Util.getUsefulStack(ex, "parsing item \"" + fc.getString(path) + "\""));
					}
				}
			}

			usesEssentials = fc.getBoolean("useEssentials", false);

			if (usesEssentials && plugin.getEssentialsHandler().useEssentials())
			{
				essKitName = fc.getString("essentialsKit", "");
				if (! essKitName.isEmpty())
				{
					essentialsKit = plugin.getEssentialsHandler().readEssentialsKit(essKitName);
				}
			}

			if (usesEssentials && (essentialsKit.isEmpty() || essKitName.isEmpty()))
			{
				usesEssentials = false;
				fc.set("useEssentials", false);
				fc.set("essentialsKit", "");
				changes = true;
			}

			hasPotionEffects = fc.getBoolean("hasPotionEffects", false);
			if (hasPotionEffects)
			{
				potionEffects = readPotionEffects(fc.getString("potionEffects"));
			}

			usesHelmet = fc.getBoolean("useHelmet", true);

			if (fc.isSet("permissionNode"))
			{
				if (! fc.getString("permissionNode").isEmpty())
					fc.set("needsPermission", true);

				fc.set("permissionNode", null);
				changes = true;
			}

			needsPermission = fc.getBoolean("needsPermission", false);
			permissionNode = "ultimatearena.class." + name.toLowerCase();

			// Attempt to save the file
			try
			{
				if (changes)
					fc.save(file);
			}
			catch (Throwable ex)
			{
				plugin.outConsole(Level.WARNING, Util.getUsefulStack(ex, "saving changes for class \"" + name + "\""));
			}
		}
		catch (Throwable ex)
		{
			plugin.outConsole(Level.SEVERE, Util.getUsefulStack(ex, "loading class \"" + name + "\""));
			return false;
		}

		plugin.debug("Successfully loaded class {0}!", name);
		return true;
	}

	private final List<String> armorTypes = Arrays.asList(new String[]
	{
			"chestplate", "leggings", "boots"
	});

	private final List<PotionEffect> readPotionEffects(String str)
	{
		List<PotionEffect> ret = new ArrayList<PotionEffect>();

		try
		{
			str = str.replaceAll(" ", "");
			if (str.contains(","))
			{
				String[] split = str.split(",");
				for (String s : split)
				{
					if (s.contains(":"))
					{
						String[] split1 = s.split(":");
						PotionEffectType type = PotionEffectType.getByName(split1[0].toUpperCase());
						int strength = NumberUtil.toInt(split1[1]);

						if (type != null && strength >= 0)
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
					PotionEffectType type = PotionEffectType.getByName(split1[0].toUpperCase());
					int strength = NumberUtil.toInt(split1[1]);

					if (type != null && strength >= 0)
					{
						ret.add(new PotionEffect(type, Integer.MAX_VALUE, strength));
					}
				}
			}
		} catch (Throwable ex) { }
		return ret;
	}

	private final Map<Enchantment, Integer> readArmorEnchantments(String string)
	{
		Map<Enchantment, Integer> enchants = new HashMap<Enchantment, Integer>();

		try
		{
			if (string.contains(":"))
			{
				String[] split2 = string.split(":");
				Enchantment enchantment = EnchantmentType.toEnchantment(split2[0]);
				int level = NumberUtil.toInt(split2[1]);

				if (enchantment != null && level > 0)
				{
					enchants.put(enchantment, level);
				}
			}
		} catch (Throwable ex) { }
		return enchants;
	}

	public final boolean checkPermission(Player player)
	{
		if (! needsPermission)
			return true;

		return plugin.getPermissionHandler().hasPermission(player, permissionNode);
	}

	public final ItemStack getArmor(int index)
	{
		if (armor.size() >= index)
		{
			return armor.get(index);
		}

		return null;
	}

	@Override
	public void reload()
	{
		// Boolean defaults
		this.hasPotionEffects = false;
		this.needsPermission = false;
		this.usesEssentials = false;
		this.usesHelmet = true;

		// Clear lists and maps
		this.essentialsKit.clear();
		this.potionEffects.clear();
		this.weapons.clear();
		this.armor.clear();

		// Empty strings
		this.permissionNode = "";
		this.essKitName = "";

		// Load the class again
		this.loaded = false;
		this.loaded = load();
	}
}