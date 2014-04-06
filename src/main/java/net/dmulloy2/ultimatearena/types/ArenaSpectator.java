package net.dmulloy2.ultimatearena.types;

import lombok.Getter;
import lombok.Setter;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.util.FormatUtil;

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
public class ArenaSpectator extends PlayerExtension
{
	private boolean active;

	private Arena arena;

	private String name;
	// private final Player player;
	private final Location spawnBack;

	private PlayerData playerData;

	private final UltimateArena plugin;

	/**
	 * Creates a new ArenaSpectator instance
	 *
	 * @param player
	 *        - Base {@link Player} to create the ArenaSpectator around
	 * @param arena
	 *        - {@link Arena} the player is in
	 * @param plugin
	 *        - {@link UltimateArena} plugin instance
	 */
	public ArenaSpectator(Player player, Arena arena, UltimateArena plugin)
	{
		super(player);
		// this.player = player;
		this.name = getName();
		this.spawnBack = getLocation();

		this.arena = arena;
		this.plugin = plugin;
	}

	/**
	 * Spawns the player
	 */
	public void spawn()
	{
		teleport(arena.getSpawn(arena.getActivePlayers().get(0)));

		// Save Data
		savePlayerData();

		// Clear inventory
		clearInventory();

		// Make sure the player is in survival
		setGameMode(GameMode.SURVIVAL);

		// Heal up the Player
		setFoodLevel(20);
		setFireTicks(0);
		setHealth(20);

		// Allow flight
		setAllowFlight(true);
		setFlySpeed(0.1F);

		// Give them a compass
		getInventory().addItem(new ItemStack(Material.COMPASS));

		// Hide the player
		for (ArenaPlayer ap : arena.getActivePlayers())
		{
			ap.hidePlayer(base);
		}

		this.active = true;
	}

	public void endPlayer()
	{
		reset();

		for (ArenaPlayer ap : arena.getActivePlayers())
		{
			ap.showPlayer(base);
		}

		for (ArenaPlayer ap : arena.getInactivePlayers())
		{
			if (ap != null && ap.isOnline())
			{
				ap.showPlayer(base);
			}
		}

		teleport(spawnBack);

		this.active = false;
	}

	/**
	 * Clears the player's inventory
	 */
	public void clearInventory()
	{
		PlayerInventory inv = getInventory();

		closeInventory();

		inv.setHelmet(null);
		inv.setChestplate(null);
		inv.setLeggings(null);
		inv.setBoots(null);
		inv.clear();
	}

	/**
	 * Clears a player's potion effects
	 */
	public void clearPotionEffects()
	{
		for (PotionEffect effect : getActivePotionEffects())
		{
			removePotionEffect(effect.getType());
		}
	}

	public void sendMessage(String string, Object... objects)
	{
		base.sendMessage(plugin.getPrefix() + FormatUtil.format(string, objects));
	}

	@Override
	public void sendMessage(String string)
	{
		sendMessage(string, new Object[0]);
	}

	/**
	 * Teleports the player to a given location. Will attempt to teleport the
	 * player to the center of the block.
	 *
	 * @param location
	 *        - {@link Location} to teleport the player to
	 */
	public final boolean teleport(Location location)
	{
		return base.teleport(location.clone().add(0.5D, 1.0D, 0.5D));
	}

	public final void teleport(ArenaLocation location)
	{
		teleport(location.getLocation());
	}

	public final void savePlayerData()
	{
		this.playerData = new PlayerData(base);
	}

	public final void reset()
	{
		clearInventory();
		clearPotionEffects();
		playerData.apply();
	}
}