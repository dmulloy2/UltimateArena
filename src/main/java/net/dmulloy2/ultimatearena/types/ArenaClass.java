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

import lombok.Getter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.dmulloy2.swornapi.exception.InvalidItemException;
import net.dmulloy2.swornapi.io.IOUtil;
import net.dmulloy2.swornapi.types.PotionType;
import net.dmulloy2.swornapi.util.*;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

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
	private Map<Enchantment, Integer> helmetEnchantments;

	private boolean useHelmet = true;
	private double cost = -1.0D;
	private boolean unlimitedAmmo = true;

	private Attributes attributes;
	private List<PotionEffect> potionEffects;

	// GUI
	private ItemStack icon;

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
		this.attributes = new Attributes(name);
		this.helmetEnchantments = new HashMap<>();

		try
		{
			boolean changes = false;

			YamlConfiguration config = new YamlConfiguration();
			config.load(file);

			Map<String, Object> values = config.getValues(false);

			ConfigurationSection armorSection = config.getConfigurationSection("armor");
			if (armorSection != null)
			{
				for (String key : armorSection.getKeys(false))
				{
					ItemStack item = null;

					ConfigurationSection section = armorSection.getConfigurationSection(key);
					if (section != null && section.getString("type") != null)
					{
						try
						{
							item = ModernItemParser.parseItem(section);
						}
						catch (InvalidItemException ex)
						{
							plugin.getLogHandler().log(Level.WARNING, "[{0}] Could not read armor item in slot \"{1}\": {2}", name, key, ex.getMessage());
						}
					}

					if (item == null)
					{
						String value = armorSection.getString(key);
						plugin.getLogHandler().log("[{0}] Attempting to convert legacy armor item \"{1}\"", name, value);
						item = ItemUtil.readItem(value, plugin);

						if (item == null)
						{
							plugin.getLogHandler().log(Level.WARNING, "[{0}] Could not read armor item \"{1}\"", name, value);
							continue;
						}

						armorSection.set(key, ModernItemParser.serializeItem(item));
						changes = true;
					}

					armor.put(key, item);
				}
			}

			ConfigurationSection toolsSection = config.getConfigurationSection("tools");
			if (toolsSection != null)
			{
				int nextSlot = 0;

				for (String key : toolsSection.getKeys(false))
				{
					ItemStack item = null;

					ConfigurationSection section = toolsSection.getConfigurationSection(key);
					if (section != null && section.getString("type") != null)
					{
						try
						{
							item = ModernItemParser.parseItem(section);
						}
						catch (InvalidItemException ex)
						{
							plugin.getLogHandler().log(Level.WARNING, "[{0}] Could not read tool in slot {1}: {2}", name, key, ex.getMessage());
						}
					}

					if (item == null)
					{
						String value = toolsSection.getString(key);
						plugin.getLogHandler().log("[{0}] Attempting to convert legacy tool \"{1}\"", name, value);
						item = ItemUtil.readItem(value, plugin);

						if (item == null)
						{
							plugin.getLogHandler().log(Level.WARNING, "[{0}] Could not read legacy tool \"{1}\"", name, value);
							continue;
						}

						toolsSection.set(key, ModernItemParser.serializeItem(item));
						changes = true;
					}

					int slot = NumberUtil.toInt(key);
					tools.put(slot == -1 ? nextSlot : slot - 1, item);
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
				Object effectData = get(values, "potionEffects", null);
				if (effectData instanceof Map<?, ?> rawEffectMap)
				{
					potionEffects = new ArrayList<>();

					for (Map.Entry<?, ?> entry : rawEffectMap.entrySet())
					{
						try
						{
							String effectKeyStr = ((String) entry.getKey()).toLowerCase().replace(' ', '_');
							int amplifier = (int) entry.getValue();

							Key effectKey = Key.key(effectKeyStr);
							Registry<PotionEffectType> mobEffectRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.MOB_EFFECT);

							PotionEffectType effectType = mobEffectRegistry.get(effectKey);
							if (effectType != null)
							{
								potionEffects.add(new PotionEffect(effectType, Integer.MAX_VALUE, amplifier));
							}
							else
							{
								plugin.getLogHandler().log(Level.WARNING, "[Class {0}] \"{1}\" is not a valid Potion Effect", name, effectKey);
							}
						} catch (Exception ex)
						{
							plugin.getLogHandler().log(Level.WARNING, "[Class {0}] \"{1}\" is not a valid Potion Effect", name, entry.getKey());
						}
					}
				}
				else
				{
					String effects = getString(values, "potionEffects", "");
					if (!effects.isEmpty())
					{
						potionEffects = readPotionEffects(effects);

						Map<String, Object> newEffectMap = new HashMap<>();
						for (PotionEffect effect : potionEffects)
						{
							newEffectMap.put(effect.getType().getKey().asString(), effect.getAmplifier());
						}
						config.set("potionEffects", newEffectMap);
						changes = true;
					}
				}
			}

			useHelmet = getBoolean(values, "useHelmet", true);

			if (isSet(values, "permissionNode"))
			{
				if (! getString(values, "permissionNode", "").isEmpty())
					config.set("needsPermission", true);

				config.set("permissionNode", null);
				changes = true;
			}

			unlimitedAmmo = getBoolean(values, "unlimitedAmmo", true);
			needsPermission = getBoolean(values, "needsPermission", false);
			permissionNode = "ultimatearena.class." + name.toLowerCase().replaceAll("\\s", "_");

			if (isSet(values, "iconType"))
			{
				Registry<ItemType> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ITEM);
				icon = registry.get(NamespacedKey.fromString(getString(values, "iconType", "minecraft:paper"))).createItemStack();
			}
			else if (isSet(values, "icon"))
			{
				icon = ItemUtil.readItem(getString(values, "icon", ""));
				config.set("iconType", icon.getType().asItemType().getKey().asString());
				config.set("icon", null);
				changes = true;
			}

			if (icon == null)
			{
				icon = new ItemStack(Material.PAPER, 1);
			}

			GsonComponentSerializer componentSerializer = GsonComponentSerializer.gson();

			Component title = null;
			if (isSet(values, "title"))
			{
				String titleStr = getString(values, "title", "");
				if (titleStr.contains("{"))
				{
					try
					{
						title = componentSerializer.deserialize(getString(values, "title", ""));
					} catch (Exception ex)
					{
						plugin.getLogHandler().warn(ex, "Could not parse title component in class {0}", name);
					}
				}
				else
				{
					title = LegacyComponentSerializer.legacyAmpersand().deserialize(titleStr);
					config.set("title", componentSerializer.serialize(title));
					changes = true;
				}
			}

			if (title == null)
			{
				title = Component.text(FormatUtil.capitalize(name), NamedTextColor.YELLOW);
			}

			boolean legacyDescription = false;
			List<Component> description = new ArrayList<>();
			if (isSet(values, "description"))
			{
				try
				{
					for (String line : getStringList(values, "description"))
					{
						boolean legacyLine = !line.contains("{");
						legacyDescription |= legacyLine;

						Component comp = legacyLine
							? LegacyComponentSerializer.legacyAmpersand().deserialize(line)
							: componentSerializer.deserialize(line);
						description.add(comp);
					}
				} catch (Exception ex)
				{
					plugin.getLogHandler().warn(ex, "Could not parse lore in class {0}", name);
				}
			}

			if (legacyDescription)
			{
				List<String> newDescription = new ArrayList<>();
				for (Component line : description)
				{
					newDescription.add(componentSerializer.serialize(line));
				}
				config.set("description", newDescription);
				changes = true;
			}

			cost = getDouble(values, "cost", -1.0D);

			ItemMeta meta = icon.getItemMeta();
			meta.customName(title);
			meta.lore(description);
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
				attributes.addAttribute(Attribute.MAX_HEALTH, difference, AttributeModifier.Operation.ADD_NUMBER);

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

	private List<PotionEffect> readPotionEffects(String str)
	{
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

	public boolean hasPotionEffects()
	{
		return potionEffects != null && !potionEffects.isEmpty();
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
		this.needsPermission = false;
		this.useHelmet = true;

		// Clear collections
		this.armor.clear();
		this.tools.clear();
		this.commands.clear();

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
