/**
 * UltimateArena - fully customizable PvP arenas
 * Copyright (C) 2012 - 2015 MineSworn
 * Copyright (C) 2013 - 2015 dmulloy2
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
import net.dmulloy2.types.PotionType;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.ItemUtil;
import net.dmulloy2.util.NumberUtil;
import net.dmulloy2.util.Util;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.file.YamlConfiguration;
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
	private List<String> commands;

	private boolean useHelmet = true;
	private double cost = -1.0D;

	private Attributes attributes;
	private boolean hasPotionEffects;
	private List<PotionEffect> potionEffects;

	// GUI
	private String title;
	private ItemStack icon;
	private List<String> description;

	// ---- Transient
	private final transient File file;
	private final transient String name;
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
	public boolean load()
	{
		Validate.isTrue(! loaded, "This class has already been loaded!");

		// Initialize collections
		this.armor = new HashMap<>();
		this.tools = new HashMap<>();
		this.commands = new ArrayList<>();
		this.description = new ArrayList<>();
		this.attributes = new Attributes(name);

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
					ItemStack item = ItemUtil.readItem(value, plugin);

					if (item != null)
					{
						armor.put(entry.getKey(), item);
					}
				}
			}

			if (isSet(values, "tools"))
			{
				int nextSlot = 0;
				for (Entry<String, Object> entry : getSection(values, "tools").entrySet())
				{
					int slot = NumberUtil.toInt(entry.getKey());
					String value = entry.getValue().toString();

					ItemStack stack = ItemUtil.readItem(value, plugin);
					if (stack != null)
						tools.put(slot == -1 ? nextSlot : slot - 1, stack);

					nextSlot++;
				}
			}

			if (isSet(values, "commands"))
			{
				for (String command : getStringList(values, "commands"))
				{
					if (command.contains("/"))
						command = command.substring(1);

					commands.add(command);
				}
			}

			if (isSet(values, "useEssentials"))
			{
				config.set("useEssentials", null);
				changes = true;
			}

			// Convert essentials kit integration into command framework
			String essKitName = getString(values, "essentialsKit", "");
			if (! essKitName.isEmpty())
			{
				commands.add("kit " + essKitName + " @p");
				config.set("essentialsKit", null);
				config.set("commands", commands);
				changes = true;
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
			permissionNode = "ultimatearena.class." + name.toLowerCase().replaceAll("\\s", "_");

			if (isSet(values, "icon"))
			{
				icon = ItemUtil.readItem(getString(values, "icon", ""));
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

			ItemMeta meta = icon.getItemMeta();
			meta.setDisplayName(title);
			meta.setLore(description);
			icon.setItemMeta(meta);

			if (isSet(values, "attributes"))
			{
				try
				{
					List<String> attributeData = getStringList(values, "attributes");
					attributes.loadAttributes(attributeData);
				}
				catch (Throwable ex)
				{
					plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "reading attributes for {0}", name));
				}
			}

			double maxHealth = getDouble(values, "maxHealth", 20.0D);
			if (maxHealth != 20.0D)
			{
				plugin.getLogHandler().log(Level.WARNING, "use of maxHealth is deprecated. Migrating to attribute in {0}", name);

				double difference = maxHealth - 20.0D;
				attributes.addAttribute(Attribute.GENERIC_MAX_HEALTH, difference, AttributeModifier.Operation.ADD_NUMBER);

				List<String> attributeData = getStringList(values, "attributes");
				String sign = difference > 0 ? "+" : "-";
				attributeData.add(FormatUtil.format("GENERIC_MAX_HEALTH:{0}{1}", sign, difference));
				config.set("attributes", attributeData);
				changes = true;
			}

			try
			{
				if (changes)
					config.save(file);
			}
			catch (Throwable ex)
			{
				plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "saving changes to class \"" + name + "\""));
			}
		}
		catch (Throwable ex)
		{
			plugin.getLogHandler().log(Level.SEVERE, Util.getUsefulStack(ex, "loading class \"{0}\"", name));
			return false;
		}

		plugin.getLogHandler().debug("Successfully loaded class {0}!", name);
		return true;
	}

	private List<PotionEffect> readPotionEffects(String str) {
		List<PotionEffect> ret = new ArrayList<>();

		str = str.replaceAll(" ", "");
		String[] split = str.split(",");
		for (String s : split)
		{
			if (s.contains(":"))
			{
				String[] split1 = s.split(":");
				PotionEffectType type = PotionType.findEffect(split1[0]);
				int strength = NumberUtil.toInt(split1[1]);

				if (type != null && strength >= 0)
				{
					ret.add(new PotionEffect(type, Integer.MAX_VALUE, strength));
				}
			}
		}

		return ret;
	}

	public boolean hasPermission(Player player)
	{
		Validate.notNull(player, "player cannot be null!");
		return ! needsPermission || plugin.getPermissionHandler().hasPermission(player, permissionNode);
	}

	public ItemStack getIcon()
	{
		return icon.clone();
	}

	public boolean checkAvailability(ArenaPlayer ap)
	{
		return checkAvailability(ap, true);
	}

	public boolean checkAvailability(ArenaPlayer ap, boolean message)
	{
		if (! hasPermission(ap.getPlayer()))
		{
			if (message)
				ap.sendMessage(plugin.getMessage("noClassPermission"));
			return false;
		}

		Arena arena = ap.getArena();
		if (! arena.isValidClass(this))
		{
			if (message)
				ap.sendMessage(plugin.getMessage("invalidClass"));
			return false;
		}

		if (! arena.getAvailableClasses(ap.getTeam()).contains(this))
		{
			if (message)
				ap.sendMessage(plugin.getMessage("unavailableClass"));
			return false;
		}

		return true;
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
		this.useHelmet = true;

		// Clear collections
		this.armor.clear();
		this.tools.clear();
		this.commands.clear();
		this.description.clear();

		if (potionEffects != null)
			this.potionEffects.clear();

		this.attributes.clear();

		// Empty strings
		this.permissionNode = "";

		// Load the class again
		this.loaded = false;
		this.loaded = load();
	}
}
