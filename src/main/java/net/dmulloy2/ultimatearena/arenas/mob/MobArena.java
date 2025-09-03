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
package net.dmulloy2.ultimatearena.arenas.mob;

import java.util.*;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import net.dmulloy2.swornapi.types.CustomScoreboard;
import net.dmulloy2.swornapi.util.FormatUtil;
import net.dmulloy2.swornapi.util.ListUtil;
import net.dmulloy2.swornapi.util.Util;
import net.dmulloy2.ultimatearena.Config;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.integration.VaultHandler;
import net.dmulloy2.ultimatearena.tasks.CommandRunner;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.ScaledReward;

/**
 * @author dmulloy2
 */

public class MobArena extends Arena implements Listener
{
	private int mobTimer, mobSpawn, mobPerWave;
	private int maxWave, wave;

	private Map<Integer, List<EntityType>> waves;

	private final Set<Entity> mobs;
	private List<EntityType> spawning;
	
	public MobArena(ArenaZone az)
	{
		super(az);
		this.winningTeam = null;

		this.spawning = new ArrayList<>();
		spawning.add(EntityType.ZOMBIE);
		spawning.add(EntityType.ZOMBIE);
		spawning.add(EntityType.ZOMBIE);

		this.mobs = new HashSet<>();
		this.newWave();
	}

	private void newWave()
	{
		if (wave > 0)
		{
			tellPlayers(getMessage("survivedWave"));
			tellPlayers(getMessage("nextWave"), wave);
		}

		this.wave++;
		this.mobPerWave = 4 + ((int) (wave * 1.5)) + (active.size() * 3);
		this.mobTimer = (wave * 4) + 20;

		if (wave <= 1)
		{
			mobTimer = 1;
		}

		for (int wave : waves.keySet())
		{
			if (this.wave >= wave)
				spawning.addAll(waves.get(wave));
		}
	}

	@Override
	public void onPlayerEnd(ArenaPlayer ap)
	{
		reward(ap);
	}

	@Override
	public void reward(ArenaPlayer ap)
	{
		// Enable check
		if (! getConfig().isGiveRewards())
			return;

		List<String> commands = getConfig().getRewardCommands();
		if (! commands.isEmpty())
		{
			new CommandRunner(ap.getName(), commands, plugin).runTask(plugin);
		}

		// Get the rewards
		List<ScaledReward> rewards = getConfig().getScaledRewards();
		if (rewards == null)
			rewards = getDefaultRewards();

		for (ScaledReward reward : rewards)
		{
			ItemStack stack = reward.get(ap.getGameXP());
			if (stack.getAmount() > 0)
				ap.giveItem(stack);
		}

		// Money
		if (Config.moneyRewards)
		{
			double money = ap.getGameXP() / 10.0D;
			if (money > 0.0D && plugin.isVaultEnabled())
			{
				VaultHandler vault = plugin.getVaultHandler();
				String response = vault.depositPlayer(ap.getPlayer(), money);
				if (response == null)
				{
					String format = vault.format(money);
					ap.sendMessage(getMessage("cashReward"), format);
				}
				else
				{
					ap.sendMessage(getMessage("cashFailed"), response);
				}
			}
		}
	}

	private static List<ScaledReward> defaultRewards;

	private static List<ScaledReward> getDefaultRewards()
	{
		// Lazy-initialization
		if (defaultRewards == null)
		{
			defaultRewards = new ArrayList<>();
			defaultRewards.add(new ScaledReward(new ItemStack(Material.GOLD_INGOT), 500.0D));
			defaultRewards.add(new ScaledReward(new ItemStack(Material.SLIME_BALL), 550.0D));
			defaultRewards.add(new ScaledReward(new ItemStack(Material.GLOWSTONE_DUST), 450.0D));
			defaultRewards.add(new ScaledReward(new ItemStack(Material.GUNPOWDER), 425.0D));
		}

		return defaultRewards;
	}

	@Override
	public void onOutOfTime()
	{
		setWinningTeam(null);
		rewardTeam(winningTeam);

		Iterator<Entity> it = mobs.iterator();
		while (it.hasNext())
		{
			Entity mob = it.next();
			if (!mob.isDead())
				mob.remove();
			it.remove();
		}
	}

	private Entity spawnMob()
	{
		Location loc = az.getSpawns().get(Util.random(az.getSpawns().size())).getLocation();
		EntityType mobType = spawning.get(Util.random(spawning.size()));
		Entity newMob = loc.getWorld().spawnEntity(loc, mobType);

		if (newMob instanceof Skeleton)
		{
			boolean giveBow = true;
			int enchantmentLevel = Util.random(5) == 0 ? 2 : 1;

			// Skeletons get amped up starting at level 12
			if (wave >= 12)
			{
				// Wither Skeletons
				if (Util.random(2) == 0)
				{
					// Set skeleton type to Wither
					// We have to replace it now since they're going to be different classes
					newMob.remove();

					newMob = loc.getWorld().spawnEntity(loc, EntityType.WITHER_SKELETON);

					// Wither skeletons dont have bows
					giveBow = false;
				}
				else
				{
					// Give them power bows
					enchantmentLevel = Util.random(5) == 0 ? 2 : 1;
				}
			}

			// Give them a bow, if applicable
			if (giveBow)
			{
				ItemStack item = new ItemStack(Material.BOW);
				item.addEnchantment(Enchantment.PUNCH, enchantmentLevel);
				((Skeleton) newMob).getEquipment().setItemInMainHand(item);
			}
		}
		// Special zombies
		else if (newMob instanceof Zombie)
		{
			// For zombies, it starts at wave 7
			if (wave >= 7)
			{
				int rand = Util.random(10);

				if (rand == 10)
				{
					// Zombies with swords
					ItemStack item = new ItemStack(wave >= 12 ? Material.DIAMOND_SWORD : Material.IRON_SWORD);

					// Possibly enchant it
					if (wave >= 12)
					{
						item.addEnchantment(Enchantment.SHARPNESS, 2);
						item.addEnchantment(Enchantment.FIRE_ASPECT, 1);
					}

					((Zombie) newMob).getEquipment().setItemInMainHand(item);
				}
				else if (rand == 5)
				{
					// Babie zombies
					((Ageable) newMob).setBaby();
				}
				else if (rand == 0)
				{
					// Make them a random villager profession
					newMob.remove();
					newMob = world.spawnEntity(loc, EntityType.ZOMBIE_VILLAGER);

					Registry<Villager.Type> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.VILLAGER_TYPE);
					Villager.Type villagerType = ListUtil.randomFromIterable(registry, registry.size());
					((ZombieVillager) newMob).setVillagerType(villagerType);
				}
			}
		}
		// TODO: More fun entity calculations? >:D

		return newMob;
	}

	private void tick()
	{
		mobTimer--;
		mobSpawn--;
		if (mobSpawn >= 0)
		{
			return;
		}

		if (mobTimer >= 0)
		{
			return;
		}

		newWave();
		synchronized (mobs)
		{
			for (int i = 0; i < mobPerWave; i++)
			{
				Entity mob = spawnMob();
				mobs.add(mob);
			}
		}
	}

	@Override
	public void check()
	{
		if (startTimer > 0)
		{
			return;
		}

		tick();

		if (active.isEmpty())
		{
			stop();
		}

		if (wave > maxWave)
		{
			setWinningTeam(null);

			stop();

			rewardTeam(null);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityDeath(EntityDeathEvent event)
	{
		mobs.remove(event.getEntity());
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageByEntityEvent event)
	{
		if (!(event.getDamager() instanceof Player player))
		{
			return;
		}

		if (!mobs.contains(event.getEntity()))
		{
			return;
		}

		ArenaPlayer ap = getArenaPlayer(player);
		if (ap == null)
		{
			player.sendMessage(FormatUtil.format(plugin.getPrefix() + getMessage("hurtMobInArena")));
			event.setCancelled(true);
		}
	}

	@Override
	public void announceWinner()
	{
		if (winningTeam == null)
		{
			if (wave > maxWave)
			{
				tellPlayers(getMessage("beatMob"));
			}
			else
			{
				tellPlayers(getMessage("survivedMob"));
			}
		}
	}

	@Override
	public void onReload()
	{
		this.countMobKills = true;
		this.maxWave = getConfig().getMaxWave();
		this.waves = getConfig().getWaves();
	}

	@Override
	public MobConfig getConfig()
	{
		return (MobConfig) super.getConfig();
	}

	@Override
	public List<String> getExtraInfo()
	{
		return Collections.singletonList("&3Wave: &e" + wave);
	}

	@Override
	public void addScoreboardEntries(CustomScoreboard board, ArenaPlayer player)
	{
		board.addEntry("Wave", wave);
	}
}
