/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.ultimatearena.types;

import lombok.Getter;
import lombok.Setter;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
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

	private Arena arena;
	private String name;
	private String uniqueId;
	private PlayerData playerData;

	private final Player player;
	private final Location spawnBack;
	private final UltimateArena plugin;

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
		teleport(arena.getSpawn(arena.getActivePlayers().get(0)));

		// Save Data
		savePlayerData();

		// Clear inventory
		clearInventory();

		// Make sure the player is in survival
		player.setGameMode(GameMode.SURVIVAL);

		// Heal up the Player
		player.setFoodLevel(20);
		player.setFireTicks(0);
		player.setHealth(20);

		// Allow flight
		player.setAllowFlight(true);
		player.setFlySpeed(0.1F);

		// Give them a compass
		player.getInventory().addItem(new ItemStack(Material.COMPASS));

		// Hide the player
		for (ArenaPlayer ap : arena.getActivePlayers())
		{
			ap.getPlayer().hidePlayer(player);
		}

		this.active = true;
	}

	public final void endPlayer()
	{
		reset();

		for (ArenaPlayer ap : arena.getActivePlayers())
		{
			ap.getPlayer().showPlayer(player);
		}

		for (ArenaPlayer ap : arena.getInactivePlayers())
		{
			if (ap != null && ap.getPlayer().isOnline())
			{
				ap.getPlayer().showPlayer(player);
			}
		}

		teleport(spawnBack);

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

	public final void teleport(ArenaLocation location)
	{
		teleport(location.getLocation());
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

	// ---- Generic Methods

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof ArenaSpectator)
		{
			ArenaSpectator that = (ArenaSpectator) obj;
			return that.uniqueId.equals(uniqueId) && that.arena.equals(arena);
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode()
	{
		int hash = 41;
		hash *= 1 + uniqueId.hashCode();
		hash *= 1 + arena.hashCode();
		return hash;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString()
	{
		return name;
	}
}