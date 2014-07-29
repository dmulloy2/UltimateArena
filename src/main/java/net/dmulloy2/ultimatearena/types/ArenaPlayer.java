package net.dmulloy2.ultimatearena.types;

import java.util.Map.Entry;
import java.util.UUID;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.InventoryUtil;
import net.dmulloy2.util.NumberUtil;

import org.apache.commons.lang.Validate;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;

/**
 * Represents a player inside an {@link Arena}.
 * <p>
 * Every player who has joined this arena will have an ArenaPlayer instance. It
 * is important to note, however, that players who are out will still have arena
 * player instances until the arena concludes. Use {@link ArenaPlayer#isOut()}
 * to make sure the player is actually in the arena.
 *
 * @author dmulloy2
 */

@Getter @Setter
public final class ArenaPlayer
{
	private int kills;
	private int deaths;
	private int killStreak;
	private int gameXP;
	private int team = 1;
	private int points;
	private int amtKicked;
	private int healTimer;

	private boolean out;
	private boolean canReward;
	private boolean changeClassOnRespawn;

	private PlayerData playerData;
	private final Location spawnBack;

	private final String name;
	private final UUID uniqueId;
	private final Player player;

	private ArenaClass arenaClass;

	private final Arena arena;
	private final UltimateArena plugin;

	/**
	 * Creates a new ArenaPlayer instance
	 *
	 * @param player Base {@link Player} to create the arena player around
	 * @param arena {@link Arena} the player is in
	 * @param plugin {@link UltimateArena} plugin instance
	 */
	public ArenaPlayer(@NonNull Player player, @NonNull Arena arena, @NonNull UltimateArena plugin)
	{
		this.player = player;
		this.name = player.getName();
		this.uniqueId = player.getUniqueId();
		this.spawnBack = player.getLocation();

		this.arena = arena;
		this.plugin = plugin;
		this.arenaClass = plugin.getArenaClass(arena.getAz().getDefaultClass());
	}

	/**
	 * Decides the player's hat
	 */
	public final void decideHat()
	{
		if (arenaClass != null && ! arenaClass.isUseHelmet())
		{
			player.getInventory().setHelmet(null);
			return;
		}

		if (player.getInventory().getHelmet() == null)
		{
			ItemStack itemStack = new ItemStack(Material.LEATHER_HELMET);
			LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();
			Color teamColor = Color.RED;
			if (team == 2)
				teamColor = Color.BLUE;
			meta.setColor(teamColor);
			itemStack.setItemMeta(meta);
			player.getInventory().setHelmet(itemStack);
		}
	}

	/**
	 * Gives the player an item
	 *
	 * @param stack {@link ItemStack} to give the player
	 */
	public final void giveItem(@NonNull ItemStack stack)
	{
		InventoryUtil.giveItem(player, stack);
	}

	/**
	 * Gives the player armor
	 *
	 * @param slot Armor slot (helmet, chestplate, etc.)
	 * @param stack {@link ItemStack} to give as armor
	 */
	public final void giveArmor(String slot, @NonNull ItemStack stack)
	{
		switch (slot.toLowerCase())
		{
			case "helmet":
				player.getInventory().setHelmet(stack);
				return;
			case "chestplate":
				player.getInventory().setChestplate(stack);
				return;
			case "leggings":
				player.getInventory().setLeggings(stack);
				return;
			case "boots":
				player.getInventory().setBoots(stack);
				return;
		}
	}

	/**
	 * Clears the player's inventory
	 */
	public final void clearInventory()
	{
		// Close any open inventories
		player.closeInventory();

		// Clear their inventory
		PlayerInventory inv = player.getInventory();

		inv.setHelmet(null);
		inv.setChestplate(null);
		inv.setLeggings(null);
		inv.setBoots(null);
		inv.clear();
	}

	/**
	 * Readies the player for spawning
	 */
	public final void spawn()
	{
		if (amtKicked > 10)
		{
			leaveArena(LeaveReason.KICK);
			return;
		}

		clearInventory();
		clearPotionEffects();

		giveClassItems();

		if (! player.hasMetadata("UA"))
			player.setMetadata("UA", new FixedMetadataValue(plugin, true));
	}

	/**
	 * Sets a player's class
	 *
	 * @param ac {@link ArenaClass} to set the player's class to
	 * @return Whether or not the operation was successful
	 */
	public final boolean setClass(@NonNull ArenaClass ac)
	{
		if (arena.isValidClass(ac))
		{
			this.arenaClass = ac;
			this.changeClassOnRespawn = true;

			clearPotionEffects();
			return true;
		}

		return false;
	}

	/**
	 * Gives the player their class items
	 */
	public final void giveClassItems()
	{
		if (! arena.isInGame())
		{
			if (arena.isInLobby())
				decideHat();
			return;
		}

		decideHat();

		if (arenaClass == null)
		{
			giveArmor("chestplate", new ItemStack(Material.IRON_CHESTPLATE));
			giveArmor("leggings", new ItemStack(Material.IRON_LEGGINGS));
			giveArmor("boots", new ItemStack(Material.IRON_BOOTS));
			giveItem(new ItemStack(Material.DIAMOND_SWORD));
			return;
		}

		if (arenaClass.isUseEssentials() && plugin.getEssentialsHandler().useEssentials())
		{
			plugin.getEssentialsHandler().giveKitItems(this);
		}

		for (Entry<String, ItemStack> armor : arenaClass.getArmor().entrySet())
		{
			ItemStack item = armor.getValue();
			if (item != null)
				giveArmor(armor.getKey(), item);
		}

		for (ItemStack tool : arenaClass.getTools())
		{
			if (tool != null)
				giveItem(tool);
		}

		this.changeClassOnRespawn = false;
	}

	/**
	 * Clears the player's potion effects
	 */
	public final void clearPotionEffects()
	{
		for (PotionEffect effect : player.getActivePotionEffects())
		{
			player.removePotionEffect(effect.getType());
		}
	}

	/**
	 * Sends the player a message
	 *
	 * @param string Base message
	 * @param objects Objects to format in
	 */
	public final void sendMessage(String string, Object... objects)
	{
		player.sendMessage(plugin.getPrefix() + FormatUtil.format(string, objects));
	}

	/**
	 * Gives the player xp
	 *
	 * @param xp XP to give the player
	 */
	public final void addXP(int xp)
	{
		this.gameXP += xp;
	}

	/**
	 * Subtracts xp from the player
	 *
	 * @param xp XP to subtract
	 */
	public final void subtractXP(int xp)
	{
		this.gameXP -= xp;
	}

	/**
	 * Gets a player's KDR (Kill-Death Ratio)
	 *
	 * @return KDR
	 */
	public final double getKDR()
	{
		double k = NumberUtil.toDouble(kills);
		if (deaths == 0)
			return k;

		double d = NumberUtil.toDouble(deaths);
		return k / d;
	}

	private long deathTime;

	/**
	 * Returns whether or not the player is dead
	 *
	 * @return Whether or not the player is dead
	 */
	public final boolean isDead()
	{
		return (System.currentTimeMillis() - deathTime) <= 60L;
	}

	/**
	 * Handles the player's death
	 */
	public final void onDeath()
	{
		this.deathTime = System.currentTimeMillis();
		this.killStreak = 0;
		this.deaths++;

		arena.onPlayerDeath(this);
	}

	/**
	 * Makes the player leave their {@link Arena}
	 *
	 * @param reason Reason the player is leaving
	 */
	public final void leaveArena(LeaveReason reason)
	{
		switch (reason)
		{
			case COMMAND:
				arena.endPlayer(this, false);

				sendMessage("&3You have left the arena!");

				arena.tellPlayers("&e{0} &3has left the arena!", name);
				break;
			case DEATHS:
				arena.endPlayer(this, true);
				break;
			case KICK:
				arena.endPlayer(this, false);

				sendMessage("&cYou have been kicked from the arena!");

				arena.tellPlayers("&e{0} &3has been kicked from the arena!", name);
				break;
			case QUIT:
				arena.endPlayer(this, false);

				arena.tellPlayers("&e{0} &3has left the arena!", name);
				break;
			default:
				arena.endPlayer(this, false);
				break;
		}
	}

	/**
	 * Teleports the player to a given {@link Location}. Will attempt to
	 * teleport the player to the center of the block.
	 *
	 * @param location {@link Location} to teleport the player to
	 */
	public final void teleport(@NonNull Location location)
	{
		player.teleport(location.clone().add(0.5D, 1.0D, 0.5D));
	}

	/**
	 * Teleports the player to a given {@link ArenaLocation}. Will attempt to
	 * teleport the player to the center of the block.
	 *
	 * @param location {@link ArenaLocation} to teleport the player to
	 */
	public final void teleport(@NonNull ArenaLocation location)
	{
		teleport(location.getLocation());
	}

	/**
	 * Saves the player's data
	 */
	public final void savePlayerData()
	{
		Validate.isTrue(playerData == null, "PlayerData already saved!");
		this.playerData = new PlayerData(player);
	}

	/**
	 * Returns the player to their pre-join state
	 */
	public final void reset()
	{
		clearInventory();
		clearPotionEffects();
		playerData.apply();

		if (player.hasMetadata("UA"))
			player.removeMetadata("UA", plugin);
	}

	// ---- Generic Methods

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(@NonNull Object obj)
	{
		if (obj instanceof ArenaPlayer)
		{
			ArenaPlayer that = (ArenaPlayer) obj;
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
		int hash = 34;
		hash *= uniqueId.hashCode();
		hash *= arena.hashCode();
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