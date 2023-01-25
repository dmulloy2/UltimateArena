/**
 * UltimateArena - fully customizable PvP arenas
 * Copyright (C) 2012 - 2015 MineSworn
 * Copyright (C) 2013 - 2015 dmulloy2
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.dmulloy2.ultimatearena.signs;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Getter;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaLocation;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.swornapi.util.FormatUtil;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

/**
 * @author dmulloy2
 */

@Getter
public abstract class ArenaSign implements ConfigurationSerializable
{
	public enum SignType
	{
		JOIN, LAST_GAME, STATUS
	}
	
	protected final int id;
	protected final String arenaName;
	protected final ArenaLocation loc;
	protected final SignType type;

	protected transient Sign sign;
	protected transient ArenaZone az;
	protected transient final UltimateArena plugin;

	/**
	 * Creates a new {@link ArenaSign}
	 *
	 * @param plugin {@link UltimateArena} plugin instance
	 * @param loc {@link Location} of the spawn
	 * @param az {@link ArenaZone} that the sign is for
	 * @param id The sign's ID
	 */
	public ArenaSign(UltimateArena plugin, SignType type, Location loc, ArenaZone az, int id)
	{
		Validate.notNull(plugin, "plugin cannot be null!");
		Validate.notNull(type, "type cannot be null!");
		Validate.notNull(loc, "loc cannot be null!");
		Validate.notNull(az, "az cannot be null!");

		this.id = id;
		this.arenaName = az.getName();
		this.loc = new ArenaLocation(loc);
		this.type = type;

		this.getSign();
		this.az = az;
		this.plugin = plugin;
	}

	/**
	 * Constructs an ArenaSign from configuration
	 */
	public ArenaSign(UltimateArena plugin, SignType type, MemorySection section)
	{
		Validate.notNull(plugin, "plugin cannot be null!");
		Validate.notNull(type, "type cannot be null");
		Validate.notNull(section, "section cannot be null!");

		this.id = section.getInt("id");
		this.arenaName = section.getString("arenaName");
		this.loc = (ArenaLocation) section.get("loc");
		this.type = type;

		this.getSign();
		this.az = plugin.getArenaZone(arenaName);
		this.plugin = plugin;
	}

	/**
	 * Updates the Sign
	 */
	public abstract void update();

	protected final boolean ensureSign()
	{
		// Update the sign
		this.getSign();

		// Abort if the sign is null
		if (sign == null)
		{
			plugin.getSignHandler().deleteSign(this);
			return false;
		}

		// Abort if the ArenaZone is null
		if (az == null)
		{
			sign.setLine(0, "[UltimateArena]");
			sign.setLine(1, FormatUtil.format("&4Null Arena"));
			sign.update();

			plugin.getSignHandler().deleteSign(this);
			return false;
		}

		return true;
	}

	/**
	 * Attempts to obtain the Sign
	 */
	protected final void getSign()
	{
		Block block = loc.getWorld().getBlockAt(loc.getLocation());
		if (block.getState() instanceof Sign)
		{
			this.sign = (Sign) block.getState();
			return;
		}

		this.sign = null;
	}

	/**
	 * Clears the Sign
	 */
	public abstract void clear();

	/**
	 * Called when the Arena completes.
	 * @param arena The arena
	 */
	public void onArenaCompletion(Arena arena) { }

	/**
	 * Obtains this Sign's arena
	 * @return This Sign's arena
	 */
	protected final Arena getArena()
	{
		return plugin.getArena(az.getName());
	}

	@Override
	public String toString()
	{
		return String.format("ArenaSign[id=%s, arena=%s, loc=%s]", id, arenaName, loc);
	}

	@Override
	public Map<String, Object> serialize()
	{
		Map<String, Object> data = new LinkedHashMap<>();

		data.put("id", id);
		data.put("type", type.name());
		data.put("arenaName", arenaName);
		data.put("loc", loc);

		serializeCustomData(data);

		return data;
	}

	/**
	 * Serializes custom data.
	 * @param data Data map
	 */
	protected void serializeCustomData(Map<String, Object> data) { }
}
