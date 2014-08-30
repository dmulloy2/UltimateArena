/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.ultimatearena.types;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.dmulloy2.types.MyMaterial;
import net.dmulloy2.util.NumberUtil;

import org.bukkit.inventory.ItemStack;

/**
 * @author dmulloy2
 */

@Data
@AllArgsConstructor
public class ScaledReward
{
	private final MyMaterial type;
	private final double scale;

	public final ItemStack get(int xp)
	{
		int amount = (int) Math.round(xp / scale);
		return type.newItemStack(amount);
	}

	public static final ScaledReward fromString(String string)
	{
		try
		{
			string = string.replaceAll(" ", "");
			MyMaterial type = MyMaterial.fromString(string.substring(0, string.lastIndexOf(",")));
			double scale = NumberUtil.toDouble(string.substring(string.lastIndexOf("," + 1)));
			return new ScaledReward(type, scale);
		} catch (Throwable ex) { }
		return null;
	}
}