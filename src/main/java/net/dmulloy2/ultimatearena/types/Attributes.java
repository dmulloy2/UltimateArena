/**
 * (c) 2016 dmulloy2
 */
package net.dmulloy2.ultimatearena.types;

import javax.naming.Name;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import net.dmulloy2.swornapi.util.FormatUtil;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */
public class Attributes
{
	private final Map<Attribute, AttributeModifier> map;
	private final String className;

	public Attributes(String className)
	{
		this.className = className;
		this.map = new HashMap<>();
	}

	private void loadAttribute(String line)
	{
		// TODO: this won't properly handle non-Minecraft attributes since we split on colon
		String[] split = line.split(":");
		Attribute attribute = Registry.ATTRIBUTE.get(NamespacedKey.minecraft(split[0].toLowerCase().replace(" ", "_")));

		String operStr = split[1].substring(0, 1);
		String numStr = split[1].substring(1);

		double value = Double.parseDouble(numStr);

		Operation operation;
		switch (operStr.toLowerCase())
		{
			case "*", "x" -> operation = Operation.MULTIPLY_SCALAR_1;
			case "/" -> {
				operation = Operation.MULTIPLY_SCALAR_1;
				value = 1.0D / value;
			}
			case "+" -> operation = Operation.ADD_NUMBER;
			case "-" -> {
				operation = Operation.ADD_NUMBER;
				if (value > 0) value = -value;
			}
			default -> throw new IllegalArgumentException("Unknown operation: " + operStr);
		}

		addAttribute(attribute, value, operation);
	}

	public void loadAttributes(List<String> list)
	{
		for (String line : list)
		{
			loadAttribute(line);
		}
	}

	public void addAttribute(Attribute attribute, double value, AttributeModifier.Operation operation)
	{
		NamespacedKey key = new NamespacedKey("ultimatearena", "%s_%s".formatted(className.toLowerCase(), attribute.getKey().asString()));
		map.put(attribute, new AttributeModifier(key, value, operation));
	}

	public void apply(Player player)
	{
		for (Entry<Attribute, AttributeModifier> entry : map.entrySet())
		{
			Attribute attr = entry.getKey();
			AttributeModifier mod = entry.getValue();

			AttributeInstance instance = player.getAttribute(attr);
			if (instance != null)
				instance.addModifier(mod);
		}
	}

	public void remove(Player player)
	{
		for (Entry<Attribute, AttributeModifier> entry : map.entrySet())
		{
			Attribute attr = entry.getKey();
			AttributeModifier mod = entry.getValue();

			AttributeInstance instance = player.getAttribute(attr);
			if (instance != null)
				instance.removeModifier(mod);
		}
	}

	public void clear()
	{
		map.clear();
	}
}
