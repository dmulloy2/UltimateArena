/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.ultimatearena.api;

import org.apache.commons.lang.Validate;

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
	private UltimateArena plugin;
	private ArenaDescriptionFile description;

	public ArenaType()
	{
		//
	}

	// ---- Optional Hooks
	public void onLoad() { }

	public void onEnable() { }

	public void onDisable() { }

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

	public final void setPlugin(UltimateArena plugin)
	{
		Validate.isTrue(plugin == null, "Plugin has already been set!");
		this.plugin = plugin;
	}

	public final void setDescription(ArenaDescriptionFile description)
	{
		Validate.isTrue(description == null, "Description has already been set!");
		this.description = description;
	}
}