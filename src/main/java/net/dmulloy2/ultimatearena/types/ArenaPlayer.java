/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.ultimatearena.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.InventoryUtil;
import net.dmulloy2.util.NumberUtil;
import net.dmulloy2.util.Util;
import net.milkbowl.vault.economy.EconomyResponse;

import org.apache.commons.lang.Validate;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;

/**
 * Represents a Player inside an {@link Arena}.
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
	private int amtKicked;

	private boolean out;
	private boolean canReward;
	private boolean changeClassOnRespawn;

	private List<Double> transactions = new ArrayList<>();
	private Map<String, Object> data = new HashMap<>();

	// Player Info
	private String name;
	private UUID uniqueId;
	private Location spawnBack;

	private ArenaClass arenaClass;
	private PlayerData playerData;

	private Player player;
	private Arena arena;
	private UltimateArena plugin;

	/**
	 * Creates a new ArenaPlayer instance.
	 *
	 * @param player Base {@link Player} to create the ArenaPlayer around
	 * @param arena {@link Arena} the player is in
	 * @param plugin {@link UltimateArena} plugin instance
	 * @throws NullPointerException if any of the arguments are null
	 */
	public ArenaPlayer(Player player, Arena arena, UltimateArena plugin)
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
	 * Decides the player's hat.
	 */
	public final void decideHat(boolean random)
	{
		Player player = getPlayer();
		if (arenaClass != null && ! arenaClass.isUseHelmet())
		{
			ItemStack helmet = player.getInventory().getHelmet();
			if (helmet != null && helmet.getType() == Material.LEATHER_HELMET)
				player.getInventory().setHelmet(null);
			return;
		}

		if (player.getInventory().getHelmet() == null)
		{
			ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
			LeatherArmorMeta meta = (LeatherArmorMeta) helmet.getItemMeta();

			Color color = team == 2 ? Color.BLUE : Color.RED;
			if (random)
			{
				DyeColor rand = DyeColor.values()[Util.random(DyeColor.values().length)];
				color = rand.getColor();
			}

			meta.setColor(color);
			helmet.setItemMeta(meta);
			player.getInventory().setHelmet(helmet);
		}
	}

	/**
	 * Gives the player an item.
	 *
	 * @param stack {@link ItemStack} to give the player
	 * @throws NullPointerException if stack is null
	 */
	public final void giveItem(ItemStack stack)
	{
		InventoryUtil.giveItem(getPlayer(), stack);
	}

	/**
	 * Gives the player a piece of armor.
	 *
	 * @param slot Armor slot (helmet, chestplate, etc.)
	 * @param stack {@link ItemStack} to give as armor
	 * @throws IllegalArgumentException if slot or stack is null
	 */
	public final void giveArmor(String slot, ItemStack stack)
	{
		Validate.notNull(slot, "slot cannot be null!");
		Validate.notNull(stack, "stack cannot be null!");

		switch (slot.toLowerCase())
		{
			case "helmet":
				getPlayer().getInventory().setHelmet(stack);
				return;
			case "chestplate":
				getPlayer().getInventory().setChestplate(stack);
				return;
			case "leggings":
				getPlayer().getInventory().setLeggings(stack);
				return;
			case "boots":
				getPlayer().getInventory().setBoots(stack);
				return;
		}
	}

	/**
	 * Clears the player's inventory.
	 */
	public final void clearInventory()
	{
		Player player = getPlayer();

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
	 * Readies the player for spawning.
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

		Player player = getPlayer();
		if (! player.hasMetadata("UA"))
			player.setMetadata("UA", new FixedMetadataValue(plugin, true));
	}

	/**
	 * Sets the player's class.
	 *
	 * @param ac {@link ArenaClass} to set the player's class to
	 * @return True if the operation was successful, false if not
	 * @throws IllegalArgumentException if ac is null
	 */
	public final boolean setClass(ArenaClass ac)
	{
		Validate.notNull(ac, "ac cannot be null!");

		if (arena.isValidClass(ac))
		{
			if (ac.getCost() != -1.0D && plugin.getVaultHandler().isEnabled())
			{
				EconomyResponse response = plugin.getVaultHandler().withdrawPlayer(player, ac.getCost());
				if (! response.transactionSuccess())
				{
					sendMessage("&cCould not purchase class {0}: {1}", ac.getName(), response.errorMessage);
					return false;
				}
				else
				{
					String format = plugin.getVaultHandler().getEconomy().format(ac.getCost());
					sendMessage("&3You have purchased class &e{0} &3for &e{1}", ac.getName(), format);
				}

				// Add to refund list if they didn't go negative, prevents exploiting
				if (plugin.getVaultHandler().getEconomy().getBalance(player) > 0)
					transactions.add(ac.getCost());
			}

			this.arenaClass = ac;
			this.changeClassOnRespawn = true;

			clearPotionEffects();
			return true;
		}

		return false;
	}

	/**
	 * Gives the player their class items.
	 */
	public final void giveClassItems()
	{
		if (! arena.isInGame())
		{
			if (arena.isInLobby())
				decideHat(false);
			return;
		}

		decideHat(false);

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

		Player player = getPlayer();
		for (Entry<Integer, ItemStack> tool : arenaClass.getTools().entrySet())
		{
			player.getInventory().setItem(tool.getKey(), tool.getValue());
		}

		this.changeClassOnRespawn = false;
	}

	/**
	 * Clears the player's potion effects.
	 */
	public final void clearPotionEffects()
	{
		Player player = getPlayer();
		for (PotionEffect effect : player.getActivePotionEffects())
		{
			player.removePotionEffect(effect.getType());
		}
	}

	/**
	 * Sends the player a formatted and prefixed message.
	 *
	 * @param string Base message
	 * @param objects Objects to format in
	 */
	public final void sendMessage(String string, Object... objects)
	{
		getPlayer().sendMessage(plugin.getPrefix() + FormatUtil.format(string, objects));
	}

	/**
	 * Displays the player's ingame statistics.
	 */
	public final void displayStats()
	{
		getPlayer().sendMessage(FormatUtil.format("&3----------------------------"));
		getPlayer().sendMessage(FormatUtil.format("&3Kills: &e{0}", kills));
		getPlayer().sendMessage(FormatUtil.format("&3Deaths: &e{0}", deaths));
		getPlayer().sendMessage(FormatUtil.format("&3Streak: &e{0}", killStreak));
		getPlayer().sendMessage(FormatUtil.format("&3GameXP: &e{0}", gameXP));
		getPlayer().sendMessage(FormatUtil.format("&3----------------------------"));
	}

	/**
	 * Gives the player xp.
	 *
	 * @param xp XP to give the player
	 */
	public final void addXP(int xp)
	{
		this.gameXP += xp;
	}

	/**
	 * Subtracts xp from the player.
	 *
	 * @param xp XP to subtract
	 */
	public final void subtractXP(int xp)
	{
		this.gameXP -= xp;
	}

	/**
	 * Gets the player's Kill-Death ratio.
	 *
	 * @return Their KDR
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
	 * Whether or not the player is dead
	 *
	 * @return True if they are dead, false if not
	 */
	public final boolean isDead()
	{
		return (System.currentTimeMillis() - deathTime) <= 60L;
	}

	/**
	 * Handles the player's death.
	 */
	public final void onDeath()
	{
		this.deathTime = System.currentTimeMillis();
		this.killStreak = 0;
		this.amtKicked = 0;
		this.deaths++;

		arena.onPlayerDeath(this);
	}

	/**
	 * Makes the player leave their {@link Arena}.
	 *
	 * @param reason Reason the player is leaving
	 */
	public final void leaveArena(LeaveReason reason)
	{
		// Refund transactions if the arena didn't start
		if (! arena.isStarted() && ! transactions.isEmpty())
		{
			double refund = 0;
			for (double transaction : transactions)
				refund += transaction;

			if (refund > 0)
			{
				plugin.getVaultHandler().depositPlayer(player, refund);
				String format = plugin.getVaultHandler().getEconomy().format(refund);
				sendMessage("&3You have been refunded &e{0} &3for your class purchases.", format);
			}
		}

		switch (reason)
		{
			case COMMAND:
				arena.endPlayer(this);
				sendMessage("&3You have left the arena!");
				arena.tellPlayers("&e{0} &3has left the arena!", name);
				break;
			case DEATHS:
				arena.endPlayer(this);
				sendMessage("&3You have exceeded the death limit!");
				arena.tellPlayers("&e{0} &3has been eliminated!", name);
				break;
			case KICK:
				arena.endPlayer(this);
				sendMessage("&cYou have been kicked from the arena!");
				arena.tellPlayers("&e{0} &3has been kicked from the arena!", name);
				break;
			case QUIT:
				arena.endPlayer(this, true);
				arena.tellPlayers("&e{0} &3has left the arena!", name);
				break;
			default:
				arena.endPlayer(this);
				break;
		}
	}

	/**
	 * Teleports the player to a given {@link Location}. Will attempt to
	 * teleport the player to the center of the block.
	 *
	 * @param location {@link Location} to teleport the player to
	 * @throws IllegalArgumentException if location is null
	 */
	public final void teleport(Location location)
	{
		Validate.notNull(location, "location cannot be null!");
		getPlayer().teleport(location.clone().add(0.5D, 1.0D, 0.5D));
	}

	/**
	 * Teleports the player to a given {@link ArenaLocation}. Will attempt to
	 * teleport the player to the center of the block.
	 *
	 * @param location {@link ArenaLocation} to teleport the player to
	 * @throws IllegalArgumentException if location is null
	 */
	public final void teleport(ArenaLocation location)
	{
		Validate.notNull(location, "location cannot be null!");
		teleport(location.getLocation());
	}

	/**
	 * Saves the player's data.
	 */
	public final void savePlayerData()
	{
		Validate.isTrue(playerData == null, "PlayerData already saved!");
		this.playerData = new PlayerData(getPlayer());
	}

	/**
	 * Returns the player to their pre-join state.
	 */
	public final void reset()
	{
		clearInventory();
		clearPotionEffects();
		playerData.apply();

		if (getPlayer().hasMetadata("UA"))
			getPlayer().removeMetadata("UA", plugin);
	}

	/**
	 * Gets this ArenaPlayer's {@link Player} instance.
	 *
	 * @return Player instance
	 */
	public final Player getPlayer()
	{
		return player;
	}

	public final void putData(String key, int value)
	{
		data.put(key, value);
	}

	public final int getDataInt(String key)
	{
		if (data.containsKey(key))
			return (int) data.get(key);

		return -1;
	}

	/**
	 * Clears this player's memory.
	 */
	public final void clear()
	{
		data.clear();
		data = null;
		uniqueId = null;
		spawnBack = null;
		player = null;
		arena = null;
		plugin = null;
	}

	// ---- Generic Methods

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj)
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