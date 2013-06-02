package com.orange451.UltimateArena.util;

import java.text.MessageFormat;

//import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;

/**
 * @author dmulloy2
 */

public class FormatUtil 
{
	public static String format(String format, Object... objects)
	{
		String ret = MessageFormat.format(format, objects);
//		ret = WordUtils.capitalize(ret, new char[]{'.'});
		return ChatColor.translateAlternateColorCodes('&', ret);
	}
}
