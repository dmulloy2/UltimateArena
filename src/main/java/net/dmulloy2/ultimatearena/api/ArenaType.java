/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.ultimatearena.api;

import lombok.Getter;
import net.dmulloy2.ultimatearena.UltimateArena;

/**
 * @author dmulloy2
 */

@Getter
public abstract class ArenaType
{
	private boolean initialized;
	private UltimateArena ultimateArena;
	private ArenaDescriptionFile description;

	public final String getName()
	{
		return description.getName();
	}
}