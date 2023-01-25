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

import java.util.*;
import java.util.Map.Entry;

import net.dmulloy2.swornapi.types.Sorter;
import net.dmulloy2.ultimatearena.Config;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.integration.VaultHandler;
import net.dmulloy2.ultimatearena.scoreboard.ArenaScoreboard;
import net.dmulloy2.ultimatearena.scoreboard.DisabledScoreboard;
import net.dmulloy2.ultimatearena.scoreboard.StandardScoreboard;
import net.dmulloy2.ultimatearena.tasks.CommandRunner;
import net.dmulloy2.swornapi.util.FormatUtil;
import net.dmulloy2.swornapi.util.InventoryUtil;
import net.dmulloy2.swornapi.util.NumberUtil;
import net.dmulloy2.swornapi.util.Util;

import org.apache.commons.lang.Validate;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a Player inside an {@link Arena}.
 * <p>
 * Every player who has joined this arena will have an ArenaPlayer instance. It
 * is important to note, however, that players who are out will still have arena
 * player instances until the arena concludes. Use {@code player.isOut()} to
 * make sure the player is still in the arena.
 *
 * @author dmulloy2
 */

@Getter @Setter
public final class ArenaPlayer
{
	private static final UUID HEALTH_ATTRIBUTE_UUID = UUID.randomUUID();

	private int kills;
	private int deaths;
	private int killStreak;
	private int gameXP;
	private int grace;

	private boolean out;
	private boolean canReward;
	private boolean changeClassOnRespawn;

	private Team team = Team.RED;
	private List<Double> transactions = new ArrayList<>();
	private Map<String, Object> data = new HashMap<>();

	// Player Info
	private String name;
	private UUID uniqueId;
	private Location spawnBack;

	private PlayerData playerData;
	private ArenaClass arenaClass;
	private ArenaScoreboard board;

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
		this.arenaClass = plugin.getArenaClass(arena.getDefaultClass());

		if (Config.scoreboardEnabled)
			this.board = new StandardScoreboard(plugin, this);
		else
			this.board = DisabledScoreboard.getInstance();

		// TODO ViewIt integration
	}

	/**
	 * Decides the player's hat.
	 */
	public void decideHat(boolean random)
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

			Color color = team == Team.BLUE ? Color.BLUE : Color.RED;
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
	public void giveItem(ItemStack stack)
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
	public void giveArmor(String slot, ItemStack stack)
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
			default:
				throw new IllegalArgumentException("Unsupported slot: " + slot);
		}
	}

	/**
	 * Clears the player's inventory.
	 */
	public void clearInventory()
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
	public void spawn()
	{
		clearInventory();
		clearPotionEffects();

		applyClass();

		Player player = getPlayer();
		if (! player.hasMetadata("UA"))
			player.setMetadata("UA", plugin.getIdentifier());

		gracePeriod();
	}

	private void gracePeriod()
	{
		if (arena.getGracePeriod() > 0)
		{
			this.grace = arena.getGracePeriod();

			new BukkitRunnable()
			{
				@Override
				public void run()
				{
					sendMessage(plugin.getMessage("graceExpired"));
					grace = 0;
				}
			}.runTaskLater(plugin, grace);

			try
			{
				player.getWorld().spawnParticle(Config.graceParticle, player.getLocation(), grace);
			}
			catch (Throwable ex)
			{
				// Don't worry about it
			}
		}
	}

	/**
	 * Sets the player's class and charges them if applicable.
	 *
	 * @param ac {@link ArenaClass} to set the player's class to
	 * @return True if the operation was successful, false if not
	 * @throws IllegalArgumentException if ac is null
	 */
	public boolean setClass(ArenaClass ac)
	{
		Validate.notNull(ac, "ac cannot be null!");

		// Charge for the class if applicable
		if (ac.getCost() > 0.0D && plugin.isVaultEnabled())
		{
			VaultHandler handler = plugin.getVaultHandler();
			if (handler.has(player, ac.getCost()))
			{
				String response = handler.withdrawPlayer(player, ac.getCost());
				if (response == null)
				{
					String format = handler.format(ac.getCost());
					sendMessage(plugin.getMessage("purchasedClass"), ac.getName(), format);
				}
				else
				{
					sendMessage(plugin.getMessage("purchaseFailed"), ac.getName(), response);
					return false;
				}
			}
			else
			{
				sendMessage(plugin.getMessage("purchaseFailed"), ac.getName(), "Inadequate funds!");
				return false;
			}

			// Add to refund list if they didn't go negative, prevents exploiting
			if (handler.getBalance(player) >= 0.0D)
				transactions.add(ac.getCost());
		}

		this.arenaClass = ac;
		this.changeClassOnRespawn = true;

		clearPotionEffects();
		return true;
	}

	/**
	 * Applies this player's arena class to the player, including items,
	 * potion effects, and attributes.
	 */
	public void applyClass()
	{
		if (! arena.isInGame())
		{
			if (arena.isInLobby())
				arena.decideHat(this);
			return;
		}

		arena.decideHat(this);

		if (arenaClass == null)
		{
			giveArmor("chestplate", new ItemStack(Material.IRON_CHESTPLATE));
			giveArmor("leggings", new ItemStack(Material.IRON_LEGGINGS));
			giveArmor("boots", new ItemStack(Material.IRON_BOOTS));
			giveItem(new ItemStack(Material.DIAMOND_SWORD));
			return;
		}

		List<String> commands = arenaClass.getCommands();
		if (! commands.isEmpty())
			new CommandRunner(player.getName(), commands, plugin).runTask(plugin);

		for (Entry<String, ItemStack> armor : arenaClass.getArmor().entrySet())
		{
			ItemStack item = armor.getValue();
			if (item != null)
				giveArmor(armor.getKey(), item);
		}

		for (Entry<Integer, ItemStack> tool : arenaClass.getTools().entrySet())
		{
			player.getInventory().setItem(tool.getKey(), tool.getValue());
		}

		Attributes attr = arenaClass.getAttributes();

		if (attr != null)
			attr.apply(player);

		this.changeClassOnRespawn = false;
	}

	/**
	 * Clears the player's potion effects.
	 */
	public void clearPotionEffects()
	{
		Player player = getPlayer();
		for (PotionEffect effect : player.getActivePotionEffects())
		{
			player.removePotionEffect(effect.getType());
		}
	}

	private String lastMessage = "";

	/**
	 * Sends the player a formatted and prefixed message.
	 *
	 * @param string Base message
	 * @param objects Objects to format in
	 */
	public void sendMessage(String string, Object... objects)
	{
		if (string.isEmpty())
			return; // Don't send empty messages

		string = plugin.getPrefix() + FormatUtil.format(string, objects);
		if (arena.isLimitSpam())
		{
			if (lastMessage.equals(string))
				return;

			lastMessage = string;
		}

		getPlayer().sendMessage(string);
	}

	/**
	 * Sends the player a formatted message.
	 * 
	 * @param string Base message
	 * @param objects Objects to format in
	 */
	public void sendMessageRaw(String string, Object... objects)
	{
		if (string.isEmpty())
			return;

		string = FormatUtil.format(string, objects);
		getPlayer().sendMessage(string);
	}

	/**
	 * Displays the player's ingame statistics.
	 */
	public void displayStats()
	{
		if (! board.isEnabled())
		{
			sendMessageRaw(plugin.getMessage("statHeader"));
			sendMessageRaw(plugin.getMessage("statKills"), kills);
			sendMessageRaw(plugin.getMessage("statDeaths"), deaths);
			sendMessageRaw(plugin.getMessage("statStreak"), killStreak);
			sendMessageRaw(plugin.getMessage("statXP"), gameXP);
			sendMessageRaw(plugin.getMessage("statHeader"));
		}
	}

	/**
	 * Gives the player xp.
	 *
	 * @param xp XP to give the player
	 */
	public void addXP(int xp)
	{
		this.gameXP += xp;
	}

	/**
	 * Gets the player's Kill-Death ratio.
	 *
	 * @return Their KDR
	 */
	public double getKDR()
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
	public boolean isDead()
	{
		return (System.currentTimeMillis() - deathTime) <= 60L;
	}

	/**
	 * Handles the player's death.
	 */
	public void onDeath()
	{
		this.deathTime = System.currentTimeMillis();
		this.killStreak = 0;
		this.deaths++;

		arena.onPlayerDeath(this);

		if (Config.forceRespawn && plugin.isProtocolEnabled())
			plugin.getProtocolHandler().forceRespawn(player);
	}

	/**
	 * Makes the player leave their {@link Arena}.
	 *
	 * @param reason Reason the player is leaving
	 */
	public void leaveArena(LeaveReason reason)
	{
		// Refund transactions if the arena didn't start
		if (! arena.isStarted() && ! transactions.isEmpty())
		{
			double refund = 0;
			for (double transaction : transactions)
				refund += transaction;

			if (refund > 0.0D && plugin.isVaultEnabled())
			{
				String response = plugin.getVaultHandler().depositPlayer(player, refund);
				if (response == null)
				{
					String format = plugin.getVaultHandler().format(refund);
					sendMessage(plugin.getMessage("classRefund"), format);
				}
			}
		}
		
		arena.endPlayer(this, reason);

		// Message them, if applicable
		switch (reason)
		{
			case COMMAND:
				sendMessage(plugin.getMessage("youLeft"));
				break;
			case DEATHS:
				sendMessage(plugin.getMessage("youDied"));
				break;
			case ERROR:
				// Callers should send a specific error message
				break;
			case GENERIC:
			case KICK:
				sendMessage(plugin.getMessage("youWereKicked"));
				break;
			case POWER:
				sendMessage(plugin.getMessage("powerKick"));
				break;
			default:
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
	public void teleport(Location location)
	{
		Validate.notNull(location, "location cannot be null!");
		getPlayer().teleport(location.clone().add(0.5D, 0.5D, 0.5D));
	}

	/**
	 * Saves the player's data.
	 */
	public void savePlayerData()
	{
		Validate.isTrue(playerData == null, "PlayerData already saved!");
		this.playerData = new PlayerData(getPlayer());
	}

	/**
	 * Returns the player to their pre-join state.
	 */
	public void reset()
	{
		clearInventory();
		clearPotionEffects();

		if (arenaClass != null)
		{
			Attributes attr = arenaClass.getAttributes();

			if (attr != null)
				attr.remove(player);
		}

		board.dispose();
		playerData.apply();

		if (getPlayer().hasMetadata("UA"))
			getPlayer().removeMetadata("UA", plugin);
	}

	/**
	 * Gets this ArenaPlayer's {@link Player} instance.
	 *
	 * @return Player instance
	 */
	public Player getPlayer()
	{
		return player;
	}

	public void putData(String key, int value)
	{
		data.put(key, value);
	}

	public int getDataInt(String key)
	{
		return getDataInt(key, -1);
	}

	public int getDataInt(String key, int def)
	{
		return getData(key, def);
	}

	@SuppressWarnings("unchecked")
	public <T> T getData(String key, T def)
	{
		if (data.containsKey(key))
			return (T) data.get(key);
		return def;
	}

	/**
	 * Clears this player's memory.
	 */
	public void clear()
	{
		data.clear();
		data = null;
		uniqueId = null;
		spawnBack = null;
		player = null;
		arena = null;
		plugin = null;
	}

	public boolean isOnline()
	{
		return player != null && player.isOnline();
	}

	public void updateScoreboard()
	{
		board.update();
	}

	// ---- Generic Methods

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this) return true;
		
		if (obj instanceof ArenaPlayer that)
		{
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

	public static Sorter<ArenaPlayer, Double> kdrSorter()
	{
		return new Sorter<>(ArenaPlayer::getKDR);
	}

	public static <T extends Comparable<T>> Sorter<ArenaPlayer, T> dataSorter(String data, T def)
	{
		return new Sorter<>(key -> key.getData(data, def));
	}
}
