package net.dmulloy2.ultimatearena.types;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import lombok.Getter;
import lombok.NonNull;
import net.dmulloy2.types.EnchantmentType;
import net.dmulloy2.types.MyMaterial;
import net.dmulloy2.types.Reloadable;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.ItemUtil;
import net.dmulloy2.util.NumberUtil;
import net.dmulloy2.util.Util;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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

	private List<ItemStack> armor, tools;

	private boolean useHelmet = true;

	// Essentials Integration
	private String essKitName;
	private boolean useEssentials;
	private Map<String, Object> essentialsKit;

	// Potion Effects
	private boolean hasPotionEffects;
	private List<PotionEffect> potionEffects;

	// GUI
	private String title;
	private ItemStack icon;
	private List<String> description;

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

		// Initialize variables
		this.armor = new ArrayList<>();
		this.tools = new ArrayList<>();

		try
		{
			boolean changes = false;
			YamlConfiguration fc = YamlConfiguration.loadConfiguration(file);

			if (fc.isSet("armor"))
			{
				Map<String, Object> values = fc.getConfigurationSection("armor").getValues(false);
				for (Entry<String, Object> entry : values.entrySet())
				{
					String value = entry.getValue().toString();
					ItemStack item = null;

					try
					{
						// Attempt to parse regularly
						item = ItemUtil.readItem(value);
					} catch (Throwable ex) { }

					if (item != null)
					{
						armor.add(item);
						continue;
					}

					// Read legacy
					Material material = null;
					short data = 0;

					Map<Enchantment, Integer> enchants = new HashMap<>();

					value = value.replaceAll(" ", "");
					if (value.contains(","))
					{
						String str = value.substring(0, value.indexOf(","));
						MyMaterial myMat = MyMaterial.fromString(str);
						material = myMat.getMaterial();
						data = myMat.isIgnoreData() ? 0 : myMat.getData();
						enchants = readArmorEnchantments(value.substring(value.indexOf(",") + 1));
					}
					else
					{
						MyMaterial myMat = MyMaterial.fromString(value);
						material = myMat.getMaterial();
						data = myMat.isIgnoreData() ? 0 : myMat.getData();
					}

					item = new ItemStack(material, 1, data);

					if (! enchants.isEmpty())
						item.addUnsafeEnchantments(enchants);

					armor.add(item);

					// Convert
					fc.set("armor." + entry.getKey().toLowerCase(), ItemUtil.serialize(item));
					changes = true;
				}
			}

			if (fc.isSet("tools"))
			{
				Map<String, Object> values = fc.getConfigurationSection("tools").getValues(false);
				for (Entry<String, Object> entry : values.entrySet())
				{
					String value = entry.getValue().toString();

					try
					{
						ItemStack stack = ItemUtil.readItem(value);
						if (stack != null)
							tools.add(stack);
					}
					catch (Throwable ex)
					{
						plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "parsing item \"" + value + "\""));
					}
				}
			}

			if (fc.isSet("useEssentials"))
			{
				fc.set("useEssentials", null);
				changes = true;
			}

			essKitName = fc.getString("essentialsKit", "");
			useEssentials = ! essKitName.isEmpty() && plugin.getEssentialsHandler().useEssentials();
			if (useEssentials)
			{
				essentialsKit = plugin.getEssentialsHandler().readEssentialsKit(essKitName);
			}

			if (fc.isSet("hasPotionEffects"))
			{
				fc.set("hasPotionEffects", null);
				changes = true;
			}

			hasPotionEffects = ! fc.getString("potionEffects").isEmpty();
			if (hasPotionEffects)
			{
				potionEffects = readPotionEffects(fc.getString("potionEffects"));
			}

			useHelmet = fc.getBoolean("useHelmet", true);

			if (fc.isSet("permissionNode"))
			{
				if (! fc.getString("permissionNode").isEmpty())
					fc.set("needsPermission", true);

				fc.set("permissionNode", null);
				changes = true;
			}

			needsPermission = fc.getBoolean("needsPermission", false);
			permissionNode = "ultimatearena.class." + name.toLowerCase();

			if (fc.isSet("icon"))
			{
				try
				{
					icon = ItemUtil.readItem(fc.getString("icon"));
				}
				catch (Throwable ex)
				{
					plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "parsing item " + fc.getString("icon")));
				}
			}

			if (icon == null)
			{
				icon = new ItemStack(Material.PAPER, 1);
			}

			title = FormatUtil.format(fc.getString("title", "&e" + WordUtils.capitalize(name)));

			if (fc.isSet("description"))
			{
				for (String line : fc.getStringList("description"))
					description.add(FormatUtil.format("&7" + line));
			}

			ItemMeta meta = icon.getItemMeta();
			meta.setDisplayName(title);
			meta.setLore(description);
			icon.setItemMeta(meta);

			try
			{
				if (changes)
					fc.save(file);
			}
			catch (Throwable ex)
			{
				plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "saving changes for class \"" + name + "\""));
			}
		}
		catch (Throwable ex)
		{
			plugin.getLogHandler().log(Level.SEVERE, Util.getUsefulStack(ex, "loading class \"" + name + "\""));
			return false;
		}

		plugin.getLogHandler().debug("Successfully loaded class {0}!", name);
		return true;
	}

	private final List<PotionEffect> readPotionEffects(String str) throws Throwable
	{
		List<PotionEffect> ret = new ArrayList<>();

		str = str.replaceAll(" ", "");
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

		return ret;
	}

	private final Map<Enchantment, Integer> readArmorEnchantments(String string) throws Throwable
	{
		Map<Enchantment, Integer> enchants = new HashMap<Enchantment, Integer>();

		string = string.replaceAll(" ", "");
		String[] split = string.split(",");
		for (String s : split)
		{
			if (s.contains(":"))
			{
				String[] split2 = s.split(":");
				Enchantment enchantment = EnchantmentType.toEnchantment(split2[0]);
				int level = NumberUtil.toInt(split2[1]);

				if (enchantment != null && level > 0)
				{
					enchants.put(enchantment, level);
				}
			}
		}

		return enchants;
	}

	public final boolean checkPermission(Player player)
	{
		return ! needsPermission || plugin.getPermissionHandler().hasPermission(player, permissionNode);
	}

	public final ItemStack getIcon()
	{
		return icon.clone();
	}

	@Override
	public void reload()
	{
		// Boolean defaults
		this.hasPotionEffects = false;
		this.needsPermission = false;
		this.useEssentials = false;
		this.useHelmet = true;

		// Clear lists and maps
		this.essentialsKit = null;
		this.potionEffects = null;
		this.armor = null;
		this.tools = null;

		// Empty strings
		this.permissionNode = "";
		this.essKitName = "";

		// Load the class again
		this.loaded = false;
		this.loaded = load();
	}
}