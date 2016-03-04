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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.dmulloy2.integration.VaultHandler;
import net.dmulloy2.types.CustomScoreboard;
import net.dmulloy2.ultimatearena.Config;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.ScaledReward;
import net.dmulloy2.util.Util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;

/**
 * @author dmulloy2
 */

public class MobArena extends Arena
{
	private int mobTimer, mobSpawn, mobPerWave;
	private int maxWave, wave;

	private Map<Integer, List<String>> waves;

	private List<LivingEntity> mobs;
	private List<String> spawning;
	
	public MobArena(ArenaZone az)
	{
		super(az);
		this.winningTeam = null;

		this.spawning = new ArrayList<>();
		spawning.add("ZOMBIE");
		spawning.add("ZOMBIE");
		spawning.add("ZOMBIE");

		this.mobs = new ArrayList<>();
		this.newWave();
	}

	private final void newWave()
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
	private static final List<ScaledReward> getDefaultRewards()
	{
		// Lazy-initialization
		if (defaultRewards == null)
		{
			defaultRewards = new ArrayList<>();
			defaultRewards.add(new ScaledReward(new ItemStack(Material.GOLD_INGOT), 500.0D));
			defaultRewards.add(new ScaledReward(new ItemStack(Material.SLIME_BALL), 550.0D));
			defaultRewards.add(new ScaledReward(new ItemStack(Material.GLOWSTONE_DUST), 450.0D));
			defaultRewards.add(new ScaledReward(new ItemStack(Material.SULPHUR), 425.0D));
		}

		return defaultRewards;
	}

	@Override
	public void onOutOfTime()
	{
		setWinningTeam(null);
		rewardTeam(winningTeam);
	}

	@Override
	public void check()
	{
		if (startTimer <= 0)
		{
			mobTimer--;
			mobSpawn--;
			if (mobSpawn < 0)
			{
				if (mobTimer < 0)
				{
					newWave();
					synchronized (mobs)
					{
						for (int i = 0; i < mobPerWave; i++)
						{
							Location loc = az.getSpawns().get(Util.random(az.getSpawns().size())).getLocation();
							String mob = spawning.get(Util.random(spawning.size()));
							LivingEntity newMob = (LivingEntity) loc.getWorld().spawnEntity(loc, EntityType.valueOf(mob));

							// Special skeletons
							if (newMob instanceof Skeleton)
							{
								boolean giveBow = true;
								int enchantmentLevel = 0;

								// Skeletons get amped up starting at level 12
								if (wave >= 12)
								{
									// Wither Skeletons
									if (Util.random(2) == 0)
									{
										// Set skeleton type to Wither
										((Skeleton) newMob).setSkeletonType(SkeletonType.WITHER);

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

									// Enchant
									if (enchantmentLevel > 0)
									{
										item.addEnchantment(Enchantment.ARROW_DAMAGE, enchantmentLevel);
									}

									newMob.getEquipment().setItemInMainHand(item);
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
											item.addEnchantment(Enchantment.DAMAGE_ALL, 2);
											item.addEnchantment(Enchantment.FIRE_ASPECT, 1);
										}

										newMob.getEquipment().setItemInMainHand(item);
									}
									else if (rand == 5)
									{
										// Babie zombies
										((Zombie) newMob).setBaby(true);
									}
									else if (rand == 0)
									{
										// Make them a random villager profession
										Profession profession = Profession.values()[Util.random(Profession.values().length)];
										((Zombie) newMob).setVillagerProfession(profession);
									}
								}
							}
							// TODO: More fun entity calculations? >:D

							mobs.add(newMob);
						}
					}
				}
			}

			if (active.size() <= 0)
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
		return Arrays.asList("&3Wave: &e" + wave);
	}

	@Override
	public void addScoreboardEntries(CustomScoreboard board, ArenaPlayer player)
	{
		board.addEntry("Wave", wave);
	}
}
