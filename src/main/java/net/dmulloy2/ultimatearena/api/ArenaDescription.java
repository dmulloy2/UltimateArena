package net.dmulloy2.ultimatearena.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dmulloy2
 */

@Getter
@AllArgsConstructor
public class ArenaDescription
{
	protected String name;
	protected String main;
	protected String stylized;
	protected String version;
	protected String author;

	public ArenaDescription() { }

	public final String getFullName()
	{
		return name + " v" + version;
	}
}