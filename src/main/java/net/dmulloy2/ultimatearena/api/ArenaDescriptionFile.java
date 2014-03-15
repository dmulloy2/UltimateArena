package net.dmulloy2.ultimatearena.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dmulloy2
 */

@Getter
@AllArgsConstructor
public class ArenaDescriptionFile
{
	private String name;
	private String main;
	private String version;
	private String author;

	public final String getFullName()
	{
		return name + " v" + version;
	}
}