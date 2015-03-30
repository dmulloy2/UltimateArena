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
	private double maxHealth;
	private ItemStack[] armorContents;
	private ItemStack[] contents;
	private int level;
	private float saturation;
	private int totalExperience;

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
		this.maxHealth = player.getMaxHealth();
		this.armorContents = player.getInventory().getArmorContents();
		this.contents = player.getInventory().getContents();
		this.level = player.getLevel();
		this.saturation = player.getSaturation();
		this.totalExperience = player.getTotalExperience();
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
		player.setMaxHealth(maxHealth);
		player.setHealth(health);
		player.getInventory().setArmorContents(armorContents);
		player.getInventory().setContents(contents);
		player.setLevel(level);
		player.setSaturation(saturation);
		player.setTotalExperience(totalExperience);
	}
}
