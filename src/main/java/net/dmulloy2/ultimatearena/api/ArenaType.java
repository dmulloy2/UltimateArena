/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.ultimatearena.api;

import lombok.Getter;
import lombok.NonNull;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.creation.ArenaCreator;
import net.dmulloy2.ultimatearena.types.ArenaConfig;
import net.dmulloy2.ultimatearena.types.ArenaZone;

/**
 * @author dmulloy2
 */

@Getter
public abstract class ArenaType
{
	private boolean enabled;
	private UltimateArena ultimateArena;
	private ArenaDescriptionFile description;

	// ---- Required Getters

	@NonNull
	public abstract ArenaZone getArenaZone();

	@NonNull
	public abstract ArenaCreator getCreator();

	@NonNull
	public abstract ArenaConfig getConfig();

	@NonNull
	public abstract Arena getArena();

	public final String getName()
	{
		return description.getName();
	}
}