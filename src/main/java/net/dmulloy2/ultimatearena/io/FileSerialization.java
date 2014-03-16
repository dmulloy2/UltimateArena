package net.dmulloy2.ultimatearena.io;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

/**
 * @author dmulloy2
 */

public class FileSerialization
{
	@SuppressWarnings("unchecked")
	public static <T extends ConfigurationSerializable> T load(File file, Class<T> clazz)
	{
		if (! file.exists())
			return null;

		FileConfiguration fc = YamlConfiguration.loadConfiguration(file);
		Map<String, Object> map = fc.getValues(true);

		return (T) ConfigurationSerialization.deserializeObject(map, clazz);
	}

	public static <T extends ConfigurationSerializable> void save(T instance, File file) throws IOException
	{
		if (file.exists())
			file.delete();

		file.createNewFile();

		FileConfiguration fc = YamlConfiguration.loadConfiguration(file);
		for (Entry<String, Object> entry : instance.serialize().entrySet())
		{
			fc.set(entry.getKey(), entry.getValue());
		}

		fc.save(file);
	}
}