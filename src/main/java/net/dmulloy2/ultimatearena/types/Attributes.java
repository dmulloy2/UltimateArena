/**
 * (c) 2016 dmulloy2
 */
package net.dmulloy2.ultimatearena.types;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */
public class Attributes
{
	private Map<Attribute, AttributeModifier> map;

	public Attributes(String clazz, List<String> list)
	{
		this.map = new HashMap<>();

		for (String line : list)
		{
			String[] split = line.split(":");
			Attribute attribute = Attribute.valueOf(split[0].toUpperCase().replace(" ", "_"));

			String operStr = split[1].substring(0, 1);
			String numStr = split[1].substring(1);

			double value = Double.parseDouble(numStr);

			Operation operation = null;
			switch (operStr.toLowerCase())
			{
				case "*":
				case "x":
					operation = Operation.MULTIPLY_SCALAR_1;
					break;
				case "/":
					operation = Operation.MULTIPLY_SCALAR_1;
					value = 1.0D / value;
					break;
				case "+":
					operation = Operation.ADD_NUMBER;
					break;
				case "-":
					operation = Operation.ADD_NUMBER;
					if (value > 0) value = -value;
					break;
				default:
					throw new IllegalArgumentException("Unknown operation: " + operStr);
			}

			

			String name = "ua_" + clazz.toLowerCase() + "_" + attribute.name().toLowerCase();
			AttributeModifier mod = new AttributeModifier(name, value, operation);
			map.put(attribute, mod);
		}
	}

	public void apply(Player player)
	{
		for (Entry<Attribute, AttributeModifier> entry : map.entrySet())
		{
			Attribute attr = entry.getKey();
			AttributeModifier mod = entry.getValue();

			player.getAttribute(attr).addModifier(mod);
		}
	}

	public void remove(Player player)
	{
		for (Entry<Attribute, AttributeModifier> entry : map.entrySet())
		{
			Attribute attr = entry.getKey();
			AttributeModifier mod = entry.getValue();

			player.getAttribute(attr).removeModifier(mod);
		}
	}
}
