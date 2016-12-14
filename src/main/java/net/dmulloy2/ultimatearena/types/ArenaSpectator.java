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
package net.dmulloy2.ultimatearena.types;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

import net.dmulloy2.ultimatearena.Config;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.util.CompatUtil;
import net.dmulloy2.util.FormatUtil;

import org.apache.commons.lang.Validate;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

/**
 * Represents a player who is spectating.
 *
 * @author dmulloy2
 */

@Getter @Setter
public final class ArenaSpectator
{
	private boolean active;
	private PlayerData playerData;

	private Arena arena;
	private String name;
	private Player player;
	private String uniqueId;
	private Location spawnBack;
	private UltimateArena plugin;

	/**
	 * Creates a new ArenaSpectator instance
	 *
	 * @param player Base {@link Player} to create the ArenaSpectator around
	 * @param arena {@link Arena} the player is in
	 * @param plugin {@link UltimateArena} plugin instance
	 */
	public ArenaSpectator(Player player, Arena arena, UltimateArena plugin)
	{
		Validate.notNull(player, "player cannot be null!");
		Validate.notNull(arena, "arena cannot be null!");
		Validate.notNull(plugin, "plugin cannot be null!");

		this.player = player;
		this.name = player.getName();
		this.uniqueId = player.getUniqueId().toString();
		this.spawnBack = player.getLocation();

		this.arena = arena;
		this.plugin = plugin;
	}

	/**
	 * Spawns the player
	 */
	public final void spawn()
	{
		ArenaPlayer[] active = arena.getActivePlayers();
		if (active.length == 0)
		{
			sendMessage(plugin.getMessage("arenaEmpty"));
			plugin.getSpectatingHandler().removeSpectator(this);
			return;
		}

		teleport(arena.getSpawn(active[0]));

		// Save Data
		savePlayerData();

		// Clear inventory
		clearInventory();

		// Put them into survival
		player.setGameMode(GameMode.SURVIVAL);

		// Hide them if applicable
		if (Config.spectatorInvisible)
		{
			for (ArenaPlayer ap : arena.getActivePlayers())
			{
				ap.getPlayer().hidePlayer(player);
			}
		}

		// Heal them up
		player.setFoodLevel(20);
		player.setFireTicks(0);
		player.setHealth(CompatUtil.getMaxHealth(player));

		// Let them fly
		player.setAllowFlight(Config.spectatorFlight);

		// Give them a selector compass
		player.getInventory().addItem(new ItemStack(Material.COMPASS));

		this.active = true;
	}

	public final void endPlayer()
	{
		reset();

		if (Config.spectatorInvisible)
		{
			for (ArenaPlayer ap : arena.getActivePlayers())
			{
				ap.getPlayer().showPlayer(player);
			}

			for (ArenaPlayer ap : arena.getInactivePlayers())
			{
				if (ap != null && ap.isOnline())
				{
					ap.getPlayer().showPlayer(player);
				}
			}
		}

		player.teleport(spawnBack);

		this.active = false;
	}

	/**
	 * Clears the player's inventory
	 */
	public final void clearInventory()
	{
		PlayerInventory inv = player.getInventory();

		player.closeInventory();

		inv.setHelmet(null);
		inv.setChestplate(null);
		inv.setLeggings(null);
		inv.setBoots(null);
		inv.clear();
	}

	/**
	 * Clears a player's potion effects
	 */
	public final void clearPotionEffects()
	{
		for (PotionEffect effect : player.getActivePotionEffects())
		{
			player.removePotionEffect(effect.getType());
		}
	}

	public void sendMessage(String string, Object... objects)
	{
		player.sendMessage(plugin.getPrefix() + FormatUtil.format(string, objects));
	}

	/**
	 * Teleports the player to a given location. Will attempt to teleport the
	 * player to the center of the block.
	 *
	 * @param location {@link Location} to teleport the player to
	 */
	public final void teleport(Location location)
	{
		player.teleport(location.clone().add(0.5D, 1.0D, 0.5D));
	}

	public final void savePlayerData()
	{
		this.playerData = new PlayerData(player);
	}

	public final void reset()
	{
		clearInventory();
		clearPotionEffects();
		playerData.apply();
	}

	/**
	 * Clears this spectator's memory.
	 */
	public final void clear()
	{
		arena = null;
		name = null;
		player = null;
		uniqueId = null;
		spawnBack = null;
		plugin = null;
	}

	// ---- Generic Methods

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this) return true;
		
		if (obj instanceof ArenaSpectator)
		{
			ArenaSpectator that = (ArenaSpectator) obj;
			return Objects.equals(uniqueId, that.uniqueId) &&
					Objects.equals(arena, that.arena);
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(uniqueId, arena);
	}

	@Override
	public String toString()
	{
		return name;
	}
}
