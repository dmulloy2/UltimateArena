package net.dmulloy2.ultimatearena.types;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import lombok.Getter;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.util.FormatUtil;
import net.dmulloy2.ultimatearena.util.Util;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

/**
 * Represents an ArenaSign, whether it be join or not
 * 
 * @author dmulloy2
 */

@Getter
public class ArenaSign implements ConfigurationSerializable
{
	private int id;
	private String worldName;
	private String arenaName;
	private SimpleVector vec;

	private transient final Sign sign;
	private transient final World world;
	private transient final Location loc;
	private transient final ArenaZone az;
	private transient final UltimateArena plugin;

	/**
	 * Creates a new ArenaSign
	 * 
	 * @param plugin
	 *            - {@link UltimateArena} plugin instance
	 * @param loc
	 *            - {@link Location} of the spawn
	 * @param zone
	 *            - {@link ArenaZone} that the sign is for
	 * @param id
	 *            - The sign's ID
	 */
	public ArenaSign(UltimateArena plugin, Location loc, ArenaZone az, int id)
	{
		this.plugin = plugin;
		this.loc = loc;
		this.world = loc.getWorld();
		this.worldName = world.getName();
		this.az = az;
		this.sign = getSign();

		this.id = id;
		this.arenaName = az.getArenaName();
		this.vec = new SimpleVector(loc.toVector());
	}

	/**
	 * Constructs an ArenaSign from configuration
	 */
	public ArenaSign(UltimateArena plugin, Map<String, Object> args)
	{
		for (Entry<String, Object> entry : args.entrySet())
		{
			try
			{
				for (Field field : getClass().getDeclaredFields())
				{
					if (field.getName().equals(entry.getKey()))
					{
						boolean accessible = field.isAccessible();

						field.setAccessible(true);

						field.set(this, entry.getValue());

						field.setAccessible(accessible);
					}
				}
			}
			catch (IllegalArgumentException | IllegalAccessException ex)
			{
			}
		}

		this.plugin = plugin;
		this.world = plugin.getServer().getWorld(worldName);
		this.loc = vec.toVector().toLocation(world);
		this.sign = getSign();
		this.az = plugin.getArenaZone(arenaName);
	}

	/**
	 * Gets the {@link Sign} instance
	 * 
	 * @return {@link Sign} instance
	 */
	public Sign getSign()
	{
		Block block = loc.getWorld().getBlockAt(loc);
		if (block.getState() instanceof Sign)
		{
			return (Sign) block.getState();
		}

		return null;
	}

	/**
	 * Updates the Sign
	 */
	public void update()
	{
		if (getSign() == null)
		{
			plugin.getSignHandler().deleteSign(this);
			return;
		}

		// plugin.debug("Updating sign: {0}", id);

		sign.setLine(0, "[UltimateArena]");
		sign.setLine(1, az.getArenaName());

		// Line 2
		StringBuilder line = new StringBuilder();
		if (isActive())
		{
			Arena ar = getArena();
			switch (ar.getGameMode())
			{
				case LOBBY:
					line.append(FormatUtil.format("&aJoin ({0} sec)", ar.getStartTimer()));
					break;
				case INGAME:
					line.append(FormatUtil.format("&cIn-Game"));
				case DISABLED:
					line.append(FormatUtil.format("&cDisabled"));
					break;
				case IDLE:
					line.append(FormatUtil.format("&aJoin"));
					break;
				case STOPPING:
					line.append(FormatUtil.format("&cStopping"));
					break;
				default:
					break;
			}
		}
		else
		{
			line.append(FormatUtil.format("&aJoin"));
		}

		sign.setLine(2, line.toString());

		// Line 3
		line = new StringBuilder();
		if (isActive())
		{
			Arena ar = getArena();

			switch (ar.getGameMode())
			{
				case DISABLED:
					line.append("STOPPING (0/");
					line.append(az.getMaxPlayers());
					line.append(")");
					break;
				case IDLE:
					line.append("IDLE (0/");
					line.append(az.getMaxPlayers());
					line.append(")");
					break;
				case INGAME:
					line.append("INGAME (");
					line.append(ar.getPlayerCount());
					line.append("/");
					line.append(az.getMaxPlayers());
					line.append(")");
					break;
				case LOBBY:
					line.append("LOBBY (");
					line.append(ar.getPlayerCount());
					line.append("/");
					line.append(az.getMaxPlayers());
					line.append(")");
					break;
				case STOPPING:
					line.append("STOPPING (0/");
					line.append(az.getMaxPlayers());
					line.append(")");
					break;
				default:
					break;

			}
		}
		else
		{
			if (az.isDisabled())
			{
				line.append("DISABLED");
			}
			else
			{
				line.append("IDLE");
			}

			line.append(" (0/");
			line.append(az.getMaxPlayers());
			line.append(")");
		}

		sign.setLine(3, line.toString());

		sign.update();
	}

	private final boolean isActive()
	{
		return getArena() != null;
	}

	private final Arena getArena()
	{
		return plugin.getArena(az.getArenaName());
	}

	@Override
	public String toString()
	{
		StringBuilder ret = new StringBuilder();
		ret.append("ArenaSign {");
		ret.append("id=" + id + ", ");
		ret.append("loc=" + Util.locationToString(loc));
		ret.append("}");

		return ret.toString();
	}

	@Override
	public Map<String, Object> serialize()
	{
		Map<String, Object> data = new HashMap<String, Object>();

		for (Field field : getClass().getDeclaredFields())
		{
			if (Modifier.isTransient(field.getModifiers()))
				continue;

			try
			{
				boolean accessible = field.isAccessible();

				field.setAccessible(true);

				if (field.getType().equals(Integer.TYPE))
				{
					data.put(field.getName(), field.getInt(this));
				}
				else if (field.getType().equals(Long.TYPE))
				{
					data.put(field.getName(), field.getLong(this));
				}
				else if (field.getType().equals(Boolean.TYPE))
				{
					data.put(field.getName(), field.getBoolean(this));
				}
				else if (field.getType().isAssignableFrom(Collection.class))
				{
					data.put(field.getName(), field.get(this));
				}
				else if (field.getType().isAssignableFrom(String.class))
				{
					data.put(field.getName(), field.get(this));
				}
				else if (field.getType().isAssignableFrom(Map.class))
				{
					data.put(field.getName(), field.get(this));
				}
				else
				{
					data.put(field.getName(), field.get(this));
				}

				field.setAccessible(accessible);

			}
			catch (IllegalArgumentException | IllegalAccessException ex)
			{
			}
		}

		return data;
	}
}