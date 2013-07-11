package net.dmulloy2.ultimatearena.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.dmulloy2.ultimatearena.arenas.objects.EnchantmentType;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

/**
 * Util that deals with Items
 * @author dmulloy2
 */

public class ItemUtil
{
	/**
	 * Reads an ItemStack from configuration
	 * @param string - String to read
	 * @return ItemStack from given string
	 */
	public static ItemStack readItem(String string)
	{
		int id = 0;
		int amt = 0;
		byte dat = 0;
		
		Map<Enchantment, Integer> enchantments = new HashMap<Enchantment, Integer>();

		string = string.replaceAll(" ", "");
		if (string.contains(","))
		{
			String s = string.substring(0, string.indexOf(","));
			if (s.contains(":"))
			{
				id = Integer.parseInt(s.substring(0, s.indexOf(":")));
				
				dat = Byte.parseByte(s.substring(s.indexOf(":") + 1, s.indexOf(",")));
			}
			else
			{
				id = Integer.parseInt(s);
			}
			
			s = string.substring(string.indexOf(",") + 1);
			if (s.contains(","))
			{
				amt = Integer.parseInt(s.substring(0, s.indexOf(",")));
				
				s = s.substring(s.indexOf(",") + 1);
			
				if (!s.isEmpty())
				{
					if (s.contains(","))
					{
						String[] split = s.split(",");
						for (String ench : split)
						{
							if (ench.contains(":"))
							{
								Enchantment enchant = EnchantmentType.toEnchantment(ench.substring(0, ench.indexOf(":")));
								int level = Integer.parseInt(ench.substring(ench.indexOf(":") + 1));
								
								if (enchant != null && level > 0)
								{
									enchantments.put(enchant, level);
								}
							}
						}
					}
					else
					{
						if (s.contains(":"))
						{
							Enchantment enchant = EnchantmentType.toEnchantment(s.substring(0, s.indexOf(":")));
							int level = Integer.parseInt(s.substring(s.indexOf(":") + 1));
							
							if (enchant != null && level > 0)
							{
								enchantments.put(enchant, level);
							}
						}
					}
				}
			}
			else
			{
				amt = Integer.parseInt(s);
			}
		}
		
		ItemStack ret = new ItemStack(id, amt);
		if (dat > 0)
		{
			MaterialData data = ret.getData();
			data.setData(dat);
			ret.setData(data);
		}
		
		if (! enchantments.isEmpty())
		{
			for (Entry<Enchantment, Integer> entry : enchantments.entrySet())
			{
				ret.addUnsafeEnchantment(entry.getKey(), entry.getValue());
			}
		}

		return ret;
	}
	
	/**
	 * Reads a potion from configuration
	 * @param string - String to read
	 * @return ItemStack from string (will be a potion)
	 */
	public static ItemStack readPotion(String string)
	{
		string = string.replaceAll(" ", "");
		string = string.substring(string.indexOf(":") + 1);
		
		String[] split = string.split(",");
		if (split.length == 3)
		{
			// Get the type
			PotionType type = net.dmulloy2.ultimatearena.arenas.objects.PotionType.toType(split[0]);
			if (type != null)
			{
				// Get the amount
				int amount = Integer.parseInt(split[1]);
				if (amount != -1)
				{
					// Get the level
					int level = Integer.parseInt(split[2]);
					if (level != -1)
					{
						// Build potion / stack
						Potion potion = new Potion(1);
						potion.setType(type);
						potion.setLevel(level);
						potion.setSplash(false);
						ItemStack ret = potion.toItemStack(amount);
						return ret;
					}
				}
			}
		}
		else if (split.length == 4)
		{
			// Get the type
			PotionType type = net.dmulloy2.ultimatearena.arenas.objects.PotionType.toType(split[0]);
			if (type != null)
			{
				// Get the amount
				int amount = Integer.parseInt(split[1]);
				if (amount != -1)
				{
					// Get the level
					int level = Integer.parseInt(split[2]);
					if (level != -1)
					{
						// Is splash
						boolean splash = Boolean.parseBoolean(split[3]);

						// Build potion / stack
						Potion potion = new Potion(1);
						potion.setType(type);
						potion.setLevel(level);
						potion.setSplash(splash);
						ItemStack ret = potion.toItemStack(amount);
						return ret;
					}
				}
			}
		}
		
		return null;
	}

	/**
	 * Returns the basic data of an ItemStack in string form
	 * @param stack - ItemStack to "convert" to a string
	 * @return ItemStack's data in string form
	 */
	public static String itemToString(ItemStack stack)
	{
		StringBuilder ret = new StringBuilder();
		ret.append("Type: " + FormatUtil.getFriendlyName(stack.getType()));
		ret.append(" Data: " + stack.getData().getData());
		ret.append(" Amount: " + stack.getAmount());
		ret.append(" Enchants:");
		for (Entry<Enchantment, Integer> enchantment : stack.getEnchantments().entrySet())
		{
			ret.append(" " + EnchantmentType.toName(enchantment.getKey()) + ": " + enchantment.getValue());
		}
		
		return ret.toString();
	}
	
	/**
	 * Returns the data of a potion in string form
	 * @param potion - Potion to "convert" to a string
	 * @return Potion's data in string form
	 */
	public static String potionToString(Potion potion)
	{
		StringBuilder ret = new StringBuilder();
		ret.append("Potion: ");
		ret.append("Type: " + potion.getType().toString());
		ret.append(" Level: " + potion.getLevel());
		ret.append(" Splash: " + potion.isSplash());
		
		return ret.toString();
	}
	
	/**
	 * Returns an ItemStack's enchantments in string form
	 * @param stack - ItemStack to get enchantments
	 * @return ItemStack's enchantments in string form
	 */
	public static String getEnchantments(ItemStack stack)
	{
		StringBuilder ret = new StringBuilder();
		if (! stack.getEnchantments().isEmpty())
		{
			ret.append("(");
			for (Entry<Enchantment, Integer> enchantment : stack.getEnchantments().entrySet())
			{
				ret.append(EnchantmentType.toName(enchantment.getKey()) + ": " + enchantment.getValue() + ", ");
			}
			ret.delete(ret.lastIndexOf(","), ret.lastIndexOf(" "));
			ret.append(")");
		}
		
		return ret.toString();
	}
}