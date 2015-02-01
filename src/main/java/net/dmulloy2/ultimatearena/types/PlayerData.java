/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.ultimatearena.types;

import lombok.Getter;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Stores data on players.
 *
 * @author dmulloy2
 */

@Getter
public final class PlayerData
{
	private boolean allowFlight;
	private float exhaustion;
	private float exp;
	private int fireTicks;
	private float flySpeed;
	private boolean flying;
	private int foodLevel;
	private GameMode gameMode;
	private double health;
	private ItemStack[] armorContents;
	private ItemStack[] contents;
	private int level;
	private float saturation;
	private int totalExperience;
	private double maxHealth;

	private final Player player;

	public PlayerData(Player player)
	{
		this.allowFlight = player.getAllowFlight();
		this.exhaustion = player.getExhaustion();
		this.exp = player.getExp();
		this.fireTicks = player.getFireTicks();
		this.flySpeed = player.getFlySpeed();
		this.flying = player.isFlying();
		this.foodLevel = player.getFoodLevel();
		this.gameMode = player.getGameMode();
		this.health = player.getHealth();
		this.armorContents = player.getInventory().getArmorContents();
		this.contents = player.getInventory().getContents();
		this.level = player.getLevel();
		this.saturation = player.getSaturation();
		this.totalExperience = player.getTotalExperience();
		this.maxHealth = player.getMaxHealth();
		this.player = player;
	}

	public final void apply()
	{
		player.setAllowFlight(allowFlight);
		player.setExhaustion(exhaustion);
		player.setExp(exp);
		player.setFireTicks(fireTicks);
		player.setFlySpeed(flySpeed);
		player.setFlying(flying);
		player.setFoodLevel(foodLevel);
		player.setGameMode(gameMode);
		player.setHealth(health);
		player.getInventory().setArmorContents(armorContents);
		player.getInventory().setContents(contents);
		player.setLevel(level);
		player.setSaturation(saturation);
		player.setTotalExperience(totalExperience);
		player.setMaxHealth(maxHealth);
	}
}