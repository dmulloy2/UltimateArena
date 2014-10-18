/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.ultimatearena.types;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dmulloy2.util.ItemUtil;
import net.dmulloy2.util.NumberUtil;

import org.bukkit.inventory.ItemStack;

/**
 * @author dmulloy2
 */

@Getter
@AllArgsConstructor
public class ScaledReward
{
	private final ItemStack item;
	private final double scale;

	public ItemStack get(int xp)
	{
		ItemStack ret = item.clone();
		ret.setAmount((int) (xp / scale));
		return ret;
	}

	public static ScaledReward fromString(String string)
	{
		try
		{
			string = string.replaceAll(" ", "");
			ItemStack item = ItemUtil.readItem(string.substring(0, string.lastIndexOf(",")));
			double scale = NumberUtil.toDouble(string.substring(string.lastIndexOf(",") + 1));
			if (item != null && scale != -1)
				return new ScaledReward(item, scale);
		} catch (Throwable ex) { }
		return null;
	}
}