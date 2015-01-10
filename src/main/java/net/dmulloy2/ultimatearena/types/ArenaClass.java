package net.dmulloy2.ultimatearena.types;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import lombok.Getter;
import net.dmulloy2.io.IOUtil;
import net.dmulloy2.types.EnchantmentType;
import net.dmulloy2.types.ItemParser;
import net.dmulloy2.types.MyMaterial;
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
public final class ArenaClass extends Configuration
{
	private boolean needsPermission;
	private String permissionNode;

	private Map<Integer, ItemStack> tools;
	private Map<String, ItemStack> armor;

	private boolean useHelmet = true;
	private double maxHealth = 20.0D;
	private double cost = -1.0D;

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
	public ArenaClass(UltimateArena plugin, File file)
	{
		Validate.notNull(plugin, "plugin cannot be null!");
		Validate.notNull(file, "file cannot be null!");

		this.plugin = plugin;
		this.file = file;
		this.name = IOUtil.trimFileExtension(file, ".yml");

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
		Validate.isTrue(! loaded, "This class has already been loaded!");

		// Initialize variables
		this.armor = new HashMap<>();
		this.tools = new HashMap<>();

		try
		{
			boolean changes = false;

			YamlConfiguration config = new YamlConfiguration();
			config.load(file);

			Map<String, Object> values = config.getValues(false);

			if (isSet(values, "armor"))
			{
				for (Entry<String, Object> entry : getSection(values, "armor").entrySet())
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
						armor.put(entry.getKey(), item);
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

					armor.put(entry.getKey(), item);

					// Convert
					config.set("armor." + entry.getKey().toLowerCase(), ItemUtil.serialize(item));
					changes = true;
				}
			}

			// Item Parser
			ItemParser parser = new ItemParser(plugin);

			if (isSet(values, "tools"))
			{
				int nextSlot = 0;
				for (Entry<String, Object> entry : getSection(values, "tools").entrySet())
				{
					int slot = NumberUtil.toInt(entry.getKey());
					String value = entry.getValue().toString();

					ItemStack stack = parser.parse(value);
					if (stack != null)
						tools.put(slot == -1 ? nextSlot : slot - 1, stack);

					nextSlot++;
				}
			}

			if (isSet(values, "useEssentials"))
			{
				config.set("useEssentials", null);
				changes = true;
			}

			essKitName = getString(values, "essentialsKit", "");
			useEssentials = ! essKitName.isEmpty() && plugin.isEssentialsEnabled();
			if (useEssentials)
			{
				essentialsKit = plugin.getEssentialsHandler().readEssentialsKit(essKitName);
			}

			if (isSet(values, "hasPotionEffects"))
			{
				config.set("hasPotionEffects", null);
				changes = true;
			}

			if (isSet(values, "potionEffects"))
			{
				String effects = getString(values, "potionEffects", "");
				hasPotionEffects = ! effects.isEmpty();
				if (hasPotionEffects)
					potionEffects = readPotionEffects(effects);
			}

			useHelmet = getBoolean(values, "useHelmet", true);

			if (isSet(values, "permissionNode"))
			{
				if (! getString(values, "permissionNode", "").isEmpty())
					config.set("needsPermission", true);

				config.set("permissionNode", null);
				changes = true;
			}

			needsPermission = getBoolean(values, "needsPermission", false);
			permissionNode = "ultimatearena.class." + name.toLowerCase();

			if (isSet(values, "icon"))
			{
				icon = parser.parse(getString(values, "icon", ""));
			}

			if (icon == null)
			{
				icon = new ItemStack(Material.PAPER, 1);
			}

			title = FormatUtil.format(getString(values, "title", "&e" + WordUtils.capitalize(name)));

			if (isSet(values, "description"))
			{
				for (String line : getStringList(values, "description"))
					description.add(FormatUtil.format("&7" + line));
			}

			cost = getDouble(values, "cost", -1.0D);
			if (cost != -1.0D && plugin.isVaultEnabled())
			{
				description.add(FormatUtil.format("&7Cost: &a{0}", plugin.getVaultHandler().format(cost)));
			}

			ItemMeta meta = icon.getItemMeta();
			meta.setDisplayName(title);
			meta.setLore(description);
			icon.setItemMeta(meta);

			maxHealth = getDouble(values, "maxHealth", 20.0D);

			try
			{
				if (changes)
					config.save(file);
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
		Validate.notNull(player, "player cannot be null!");
		return ! needsPermission || plugin.getPermissionHandler().hasPermission(player, permissionNode);
	}

	public final ItemStack getIcon()
	{
		return icon.clone();
	}

	@Override
	public Map<String, Object> serialize()
	{
		// Currently not needed
		return null;
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