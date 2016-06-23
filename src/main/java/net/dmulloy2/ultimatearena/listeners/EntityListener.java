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
package net.dmulloy2.ultimatearena.listeners;

import lombok.RequiredArgsConstructor;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.arenas.spleef.SpleefArena;
import net.dmulloy2.ultimatearena.types.ArenaClass;
import net.dmulloy2.ultimatearena.types.ArenaConfig;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.util.CompatUtil;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.MaterialUtil;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustByBlockEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.BlockProjectileSource;
import org.bukkit.projectiles.ProjectileSource;

/**
 * @author dmulloy2
 */

@RequiredArgsConstructor
public class EntityListener implements Listener
{
	private final UltimateArena plugin;

	// Stop block damage in arenas
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onEntityExplode(EntityExplodeEvent event)
	{
		if (plugin.isInArena(event.getLocation()))
		{
			event.blockList().clear();
		}
	}

	// Prevent food level change in the lobby
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onFoodLevelChange(FoodLevelChangeEvent event)
	{
		if (event.getEntity() instanceof Player)
		{
			Player player = (Player) event.getEntity();
			ArenaPlayer ap = plugin.getArenaPlayer(player);
			if (ap != null)
			{
				Arena arena = ap.getArena();
				if (arena.isInLobby())
				{
					player.setFoodLevel(20);
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onEntityCombust(EntityCombustEvent event)
	{
		Entity entity = event.getEntity();
		EntityType type = entity.getType();
		if (type == EntityType.PLAYER)
		{
			// Stop combustion in the lobby
			Player player = (Player) entity;
			ArenaPlayer ap = plugin.getArenaPlayer(player);
			if (ap != null)
			{
				Arena arena = ap.getArena();
				if (arena.isInLobby())
				{
					player.setFireTicks(0);
					event.setCancelled(true);
				}
			}
		}
		else if (type == EntityType.SKELETON || type == EntityType.ZOMBIE)
		{
			if (ArenaConfig.Global.mobPreservation)
			{
				// Skip the other cases
				if (event instanceof EntityCombustByBlockEvent || event instanceof EntityCombustByEntityEvent)
					return;

				// Stop zombies and skeletons from combusting from the sun if applicable
				World world = entity.getWorld();
				if (world.getTime() <= 13500L)
				{
					ArenaZone az = plugin.getZoneInside(entity.getLocation());
					if (az != null)
					{
						Arena arena = plugin.getArena(az.getName());
						if (arena != null && arena.getConfig().isPreserveMobs())
						{
							event.setCancelled(true);
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onEntityDamageByEntityHighest(EntityDamageByEntityEvent event)
	{
		Player attacker = getPlayer(event.getDamager());
		if (attacker == null)
			return;

		// Prevent attacking your attack dogs
		if (event.getEntity() instanceof Wolf)
		{
			if (event.getEntity().hasMetadata("ua_attack_dog_" + attacker.getName()))
			{
				event.setCancelled(true);
				return;
			}
		}

		Player defender = getPlayer(event.getEntity());
		if (defender == null)
			return;

		ArenaPlayer ap = plugin.getArenaPlayer(attacker);
		if (ap != null)
		{
			ArenaPlayer dp = plugin.getArenaPlayer(defender);
			if (dp != null)
			{
				Arena arena = ap.getArena();
				if (arena.isInLobby())
				{
					// Prevent lobby PvP
					ap.sendMessage(plugin.getMessage("lobbyPvp"));
					event.setCancelled(true);
					return;
				}

				// Prevent team killing
				if (! arena.isAllowTeamKilling())
				{
					if (dp.getTeam() == ap.getTeam())
					{
						ap.sendMessage(plugin.getMessage("friendlyFire"));
						event.setCancelled(true);
						return;
					}
				}
			}
			else
			{
				ap.sendMessage(plugin.getMessage("hurtOutside"));
				event.setCancelled(true);
				return;
			}
		}
		else
		{
			if (plugin.isInArena(defender))
			{
				attacker.sendMessage(plugin.getPrefix() + FormatUtil.format(plugin.getMessage("hurtInside")));
				event.setCancelled(true);
				return;
			}
		}
	}

	// Repair armor and in-hand items
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDamageByEntityMonitor(EntityDamageByEntityEvent event)
	{
		Player player = getPlayer(event.getDamager());
		if (player == null)
			return;

		ArenaPlayer ap = plugin.getArenaPlayer(player);
		if (ap != null)
		{
			// Repair in-hand item
			ItemStack inHand = CompatUtil.getItemInMainHand(player);
			if (inHand != null && inHand.getType() != Material.AIR)
			{
				if (inHand.getType().getMaxDurability() != 0)
				{
					inHand.setDurability((short) 0);
				}
			}

			// Repair armor
			for (ItemStack armor : player.getInventory().getArmorContents())
			{
				if (armor != null && armor.getType() != Material.AIR)
				{
					armor.setDurability((short) 0);
				}
			}

			// Healer class
			if (inHand != null && inHand.getType() == Material.GOLD_AXE)
			{
				Player damaged = getPlayer(event.getEntity());
				if (damaged != null)
				{
					ArenaPlayer dp = plugin.getArenaPlayer(damaged);
					if (dp != null)
					{
						if (ap.getTeam() == dp.getTeam())
						{
							ArenaClass ac = ap.getArenaClass();
							if (ac != null && ac.getName().equalsIgnoreCase("healer"))
							{
								Player heal = dp.getPlayer();
								double health = heal.getHealth();
								double maxHealth = heal.getMaxHealth();
								if (health > 0.0D && health < maxHealth)
								{
									heal.setHealth(Math.min(health + 2.0D, maxHealth));
									ap.sendMessage(plugin.getMessage("healer"), dp.getName());
								}
							}
						}
					}
				}
			}
		}
	}

	// Cancel damage in the lobby
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent event)
	{
		if (event.getEntity() instanceof Player)
		{
			Player player = (Player) event.getEntity();
			ArenaPlayer ap = plugin.getArenaPlayer(player);
			if (ap != null)
			{
				Arena arena = ap.getArena();
				if (arena.isInLobby())
				{
					event.setCancelled(true);
				}
			}
		}
	}

	// Clear drops in arenas
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDeathMonitor(EntityDeathEvent event)
	{
		LivingEntity entity = event.getEntity();
		ArenaZone inside = plugin.getZoneInside(entity.getLocation());
		if (inside != null)
		{
			plugin.getLogHandler().debug("Clearing drops from {0} in arena {1}", entity, inside);

			event.getDrops().clear();
			event.setDroppedExp(0);
		}
	}

	// Handle deaths in arenas
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDeath(EntityDeathEvent event)
	{
		if (event.getEntity() instanceof Player)
		{
			Player died = (Player) event.getEntity();
			ArenaPlayer ap = plugin.getArenaPlayer(died);
			if (ap != null)
			{
				// Prevent duplicate deaths
				if (ap.isDead()) return;
				ap.onDeath();

				Arena arena = ap.getArena();

				Player killer = died.getKiller();
				if (killer != null)
				{
					if (killer.equals(died))
					{
						arena.tellPlayers(plugin.getMessage("suicide"), died.getName());
						ap.displayStats();
					}
					else
					{
						arena.tellPlayers(plugin.getMessage("pvpKill"), killer.getName(), died.getName(), getWeapon(killer));
						ap.displayStats();

						ArenaPlayer kp = plugin.getArenaPlayer(killer);
						if (kp != null)
						{
							kp.setKills(kp.getKills() + 1);
							kp.setKillStreak(kp.getKillStreak() + 1);
							kp.getArena().handleKillStreak(kp);
							kp.addXP(100);

							kp.displayStats();
						}
					}
				}
				else
				{
					// Attempt to grab from their last damage cause
					EntityDamageEvent damageEvent = died.getLastDamageCause();
					if (damageEvent instanceof EntityDamageByEntityEvent)
					{
						EntityDamageByEntityEvent damageByEntity = (EntityDamageByEntityEvent) damageEvent;
						Entity damager = damageByEntity.getDamager();
						if (damager instanceof Player)
						{
							killer = (Player) damager;
							arena.tellPlayers(plugin.getMessage("pvpKill"), killer.getName(), died.getName(), getWeapon(killer));
							ap.displayStats();

							ArenaPlayer kp = plugin.getArenaPlayer(killer);
							if (kp != null)
							{
								kp.setKills(kp.getKills() + 1);
								kp.setKillStreak(kp.getKillStreak() + 1);
								kp.getArena().handleKillStreak(kp);
								kp.addXP(100);

								kp.displayStats();
							}
						}
						else if (damager instanceof Projectile)
						{
							Projectile proj = (Projectile) damager;
							ProjectileSource shooter = proj.getShooter();

							if (shooter instanceof Player)
							{
								killer = (Player) damager;
								arena.tellPlayers(plugin.getMessage("pvpKill"), killer.getName(), died.getName(), getWeapon(killer));
								ap.displayStats();

								ArenaPlayer kp = plugin.getArenaPlayer(killer);
								if (kp != null)
								{
									kp.setKills(kp.getKills() + 1);
									kp.setKillStreak(kp.getKillStreak() + 1);
									kp.getArena().handleKillStreak(kp);
									kp.addXP(100);

									kp.displayStats();
								}
							}
							else if (shooter instanceof Entity)
							{
								Entity entity = (Entity) shooter;
								String name = FormatUtil.getFriendlyName(entity.getType());
								arena.tellPlayers(plugin.getMessage("pveDeath"), died.getName(), FormatUtil.getArticle(name), name);
								ap.displayStats();
							}
							else if (shooter instanceof BlockProjectileSource)
							{
								BlockProjectileSource source = (BlockProjectileSource) shooter;
								Block block = source.getBlock();
								String name = FormatUtil.getFriendlyName(block.getType());
								arena.tellPlayers(plugin.getMessage("pveDeath"), died.getName(), FormatUtil.getArticle(name), name);
								ap.displayStats();
							}
						}
						else
						{
							String name = FormatUtil.getFriendlyName(damager.getType());
							arena.tellPlayers(plugin.getMessage("pveDeath"), died.getName(), FormatUtil.getArticle(name), name);
							ap.displayStats();
						}
					}
					else if (damageEvent != null)
					{
						// Stringify it best we can
						arena.tellPlayers(plugin.getMessage("genericDeath"), died.getName(), FormatUtil.getFriendlyName(damageEvent.getCause()));
						ap.displayStats();
					}
					else if (arena instanceof SpleefArena)
					{
						// If it's spleef, they probably fell through the floor
						arena.tellPlayers(plugin.getMessage("spleefDeath"), died.getName());
						ap.displayStats();
					}
					else
					{
						// No idea
						arena.tellPlayers(plugin.getMessage("unknownDeath"), died.getName());
						ap.displayStats();
					}
				}
			}
		}
		else
		{
			LivingEntity died = event.getEntity();
			Player killer = died.getKiller();
			if (killer != null)
			{
				ArenaPlayer ap = plugin.getArenaPlayer(killer);
				if (ap != null)
				{
					// Selectively count mob kills
					if (ap.getArena().isCountMobKills())
					{
						ap.addXP(25);
						ap.setKills(ap.getKills() + 1);
						ap.setKillStreak(ap.getKillStreak() + 1);
						ap.getArena().handleKillStreak(ap);

						String name = FormatUtil.getFriendlyName(died.getType());
						ap.sendMessage(plugin.getMessage("pveKill"), killer.getName(), FormatUtil.getArticle(name), name);
						ap.displayStats();
					}
				}
			}
		}
	}

	private String getWeapon(Player player)
	{
		ItemStack inHand = CompatUtil.getItemInMainHand(player);
		if (inHand == null || inHand.getType() == Material.AIR)
		{
			return "their fists";
		}
		else
		{
			String name = MaterialUtil.getName(inHand);
			String article = FormatUtil.getArticle(name);
			return "&3" + article + " &e" + name;
		}
	}

	private Player getPlayer(Entity entity)
	{
		if (entity instanceof Player)
		{
			return (Player) entity;
		}

		if (entity instanceof Projectile)
		{
			Projectile proj = (Projectile) entity;
			ProjectileSource shooter = proj.getShooter();
			if (shooter instanceof Player)
				return (Player) shooter;
		}

		return null;
	}
}