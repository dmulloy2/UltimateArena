package net.dmulloy2.ultimatearena.listeners;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaClass;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.util.FormatUtil;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author dmulloy2
 */

@AllArgsConstructor
public class EntityListener implements Listener
{
	private final UltimateArena plugin;

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onEntityExplode(EntityExplodeEvent event)
	{
		if (plugin.isInArena(event.getLocation()))
		{
			// Prevent block damage in arenas
			event.blockList().clear();
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onFoodLevelChange(FoodLevelChangeEvent event)
	{
		if (event.getEntity() instanceof Player)
		{
			Player player = (Player) event.getEntity();
			ArenaPlayer ap = plugin.getArenaPlayer(player);
			if (ap != null)
			{
				Arena a = ap.getArena();
				if (a.isInLobby())
				{
					// Prevent food level change
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
		if (entity instanceof Player)
		{
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
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onEntityDamageByEntityHighest(EntityDamageByEntityEvent event)
	{
		Player attacker = getPlayer(event.getDamager());
		if (attacker == null)
			return;

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
					ap.sendMessage("&cYou cannot PvP in the lobby!");
					event.setCancelled(true);
					return;
				}

				// Prevent team killing
				if (! arena.isAllowTeamKilling())
				{
					if (dp.getTeam() == ap.getTeam())
					{
						ap.sendMessage("&cYou cannot hurt your team mate!");
						event.setCancelled(true);
						return;
					}
				}
			}
			else
			{
				ap.sendMessage("&cYou cannot hurt players not in the arena!");
				event.setCancelled(true);
				return;
			}
		}
		else
		{
			if (plugin.isInArena(defender))
			{
				attacker.sendMessage(plugin.getPrefix() + FormatUtil.format("&cYou cannot hurt players while they are in an arena!"));
				event.setCancelled(true);
				return;
			}
		}
	}

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
			ItemStack inHand = player.getItemInHand();
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
								if (dp.getPlayer().getHealth() > 0.0D && dp.getPlayer().getHealth() <= 18.0D)
								{
									dp.getPlayer().setHealth(dp.getPlayer().getHealth() + 2.0D);
									ap.sendMessage("&3You have healed &e{0} &3for &e1 &3heart!", dp.getName());
								}
							}
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent event)
	{
		// Cancels all forms of damage in the lobby
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

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDeath(EntityDeathEvent event)
	{
		// Handles deaths inside arenas
		Entity died = event.getEntity();
		if (died == null)
			return;

		// Clear the drops if in an arena
		if (plugin.isInArena(died.getLocation()))
		{
			event.getDrops().clear();
			event.setDroppedExp(0);
		}

		if (died instanceof Player)
		{
			Player pdied = (Player) died;
			if (plugin.isInArena(pdied))
			{
				ArenaPlayer dp = plugin.getArenaPlayer(pdied);

				// Prevents duplicate deaths
				if (dp.isDead()) return;
				dp.onDeath();

				Arena ar = dp.getArena();

				if (pdied.getKiller() instanceof Player)
				{
					Player killer = pdied.getKiller();
					if (killer.getName().equals(pdied.getName())) // Suicide
					{
						ar.tellPlayers("&e{0} &3commited &esuicide&3!", pdied.getName());

						List<String> lines = new ArrayList<String>();
						lines.add(FormatUtil.format("&3----------------------------"));
						lines.add(FormatUtil.format("&3Kills: &e{0}", dp.getKills()));
						lines.add(FormatUtil.format("&3Deaths: &e{0}", dp.getDeaths()));
						lines.add(FormatUtil.format("&3Streak: &e{0}", dp.getKillStreak()));
						lines.add(FormatUtil.format("&3GameXP: &e{0}", dp.getGameXP()));
						lines.add(FormatUtil.format("&3----------------------------"));

						for (String line : lines)
						{
							pdied.sendMessage(line);
						}
					}
					else
					{
						// PvP
						ar.tellPlayers("&e{0} &3killed &e{1} &3with {2}", killer.getName(), pdied.getName(), getWeapon(killer));

						List<String> deadlines = new ArrayList<String>();
						deadlines.add(FormatUtil.format("&3----------------------------"));
						deadlines.add(FormatUtil.format("&3Kills: &e{0}", dp.getKills()));
						deadlines.add(FormatUtil.format("&3Deaths: &e{0}", dp.getDeaths()));
						deadlines.add(FormatUtil.format("&3Streak: &e{0}", dp.getKillStreak()));
						deadlines.add(FormatUtil.format("&3GameXP: &e{0}", dp.getGameXP()));
						deadlines.add(FormatUtil.format("&3----------------------------"));

						for (String deadline : deadlines)
						{
							pdied.sendMessage(deadline);
						}

						// Handle killer
						ArenaPlayer kp = plugin.getArenaPlayer(killer);
						if (kp != null)
						{
							kp.setKills(kp.getKills() + 1);
							kp.setKillStreak(kp.getKillStreak() + 1);
							kp.getArena().handleKillStreak(kp);
							kp.addXP(100);

							List<String> killerlines = new ArrayList<String>();
							killerlines.add(FormatUtil.format("&3----------------------------"));
							killerlines.add(FormatUtil.format("&3Kills: &e{0}", kp.getKills()));
							killerlines.add(FormatUtil.format("&3Deaths: &e{0}", kp.getDeaths()));
							killerlines.add(FormatUtil.format("&3Streak: &e{0}", kp.getKillStreak()));
							killerlines.add(FormatUtil.format("&3GameXP: &e{0}", kp.getGameXP()));
							killerlines.add(FormatUtil.format("&3----------------------------"));

							for (String killerline : killerlines)
							{
								killer.sendMessage(killerline);
							}
						}
					}
				}
				else
				{
					// From this point on, we will return when there is a valid match
					if (pdied.getKiller() instanceof LivingEntity)
					{
						LivingEntity lentity = pdied.getKiller();
						String name = FormatUtil.getFriendlyName(lentity.getType());

						plugin.debug("Player {0} was killed by {1}", pdied.getName(), name);
						ar.tellPlayers("&e{0} &3was killed by &3{1} &e{2}", pdied.getName(), FormatUtil.getArticle(name), name);

						List<String> deadlines = new ArrayList<String>();
						deadlines.add(FormatUtil.format("&3----------------------------"));
						deadlines.add(FormatUtil.format("&cKills: &e{0}", dp.getKills()));
						deadlines.add(FormatUtil.format("&cDeaths: &e{0}", dp.getDeaths()));
						deadlines.add(FormatUtil.format("&cStreak: &e{0}", dp.getKillStreak()));
						deadlines.add(FormatUtil.format("&cGameXP: &e{0}", dp.getGameXP()));
						deadlines.add(FormatUtil.format("&3----------------------------"));

						for (String deadline : deadlines)
						{
							pdied.sendMessage(deadline);
						}

						return;
					}
					else if (pdied.getKiller() instanceof Projectile)
					{
						Projectile proj = (Projectile) pdied.getKiller();
						if (proj.getShooter() instanceof Player)
						{
							Player killer = (Player) proj.getShooter();
							ar.tellPlayers("&e{0} &3killed &e{1} &3with &e{2}", killer.getName(), pdied.getName(), getWeapon(killer));

							List<String> deadlines = new ArrayList<String>();
							deadlines.add(FormatUtil.format("&3----------------------------"));
							deadlines.add(FormatUtil.format("&3Kills: &e{0}", dp.getKills()));
							deadlines.add(FormatUtil.format("&3Deaths: &e{0}", dp.getDeaths()));
							deadlines.add(FormatUtil.format("&3Streak: &e{0}", dp.getKillStreak()));
							deadlines.add(FormatUtil.format("&3GameXP: &e{0}", dp.getGameXP()));
							deadlines.add(FormatUtil.format("&3----------------------------"));

							for (String deadline : deadlines)
							{
								pdied.sendMessage(deadline);
							}

							// Handle killer
							ArenaPlayer kp = plugin.getArenaPlayer(killer);
							kp.setKills(kp.getKills() + 1);
							kp.setKillStreak(kp.getKillStreak() + 1);
							kp.getArena().handleKillStreak(kp);
							kp.addXP(100);

							List<String> killerlines = new ArrayList<String>();
							killerlines.add(FormatUtil.format("&3----------------------------"));
							killerlines.add(FormatUtil.format("&3Kills: &e{0}", kp.getKills()));
							killerlines.add(FormatUtil.format("&3Deaths: &e{0}", kp.getDeaths()));
							killerlines.add(FormatUtil.format("&3Streak: &e{0}", kp.getKillStreak()));
							killerlines.add(FormatUtil.format("&3GameXP: &e{0}", kp.getGameXP()));
							killerlines.add(FormatUtil.format("&3----------------------------"));

							for (String killerline : killerlines)
							{
								kp.sendMessage(killerline);
							}

							return;
						}
						else if (proj.getShooter() instanceof LivingEntity)
						{
							LivingEntity lentity = pdied.getKiller();
							String name = FormatUtil.getFriendlyName(lentity.getType());

							ar.tellPlayers("&e{0} &3was killed by {1} &e{2}", pdied.getName(), FormatUtil.getArticle(name), name);

							List<String> deadlines = new ArrayList<String>();
							deadlines.add(FormatUtil.format("&3----------------------------"));
							deadlines.add(FormatUtil.format("&3Kills: &e{0}", dp.getKills()));
							deadlines.add(FormatUtil.format("&3Deaths: &e{0}", dp.getDeaths()));
							deadlines.add(FormatUtil.format("&3Streak: &e{0}", dp.getKillStreak()));
							deadlines.add(FormatUtil.format("&3GameXP: &e{0}", dp.getGameXP()));
							deadlines.add(FormatUtil.format("&3----------------------------"));

							for (String deadline : deadlines)
							{
								pdied.sendMessage(deadline);
							}

							return;
						}
					}

					// If we've gotten to this point, our checks have basically failed
					// [WARNING] The code gets pretty messy (and repetitive) from this point on

					// TODO: Rewrite this mess

					EntityDamageEvent ede = pdied.getLastDamageCause();

					DamageCause dc = ede.getCause();

					if (dc == DamageCause.ENTITY_ATTACK)
					{
						if (ede instanceof EntityDamageByEntityEvent)
						{
							EntityDamageByEntityEvent edbe = (EntityDamageByEntityEvent) ede;
							Entity damager = edbe.getDamager();
							if (edbe.getDamager() != null)
							{
								if (damager instanceof Player)
								{
									Player killer = (Player) damager;

									ar.tellPlayers("&e{0} &3killed &e{1} &3with {2}", killer.getName(), pdied.getName(), getWeapon(killer));

									List<String> deadlines = new ArrayList<String>();
									deadlines.add(FormatUtil.format("&3----------------------------"));
									deadlines.add(FormatUtil.format("&3Kills: &e{0}", dp.getKills()));
									deadlines.add(FormatUtil.format("&3Deaths: &e{0}", dp.getDeaths()));
									deadlines.add(FormatUtil.format("&3Streak: &e{0}", dp.getKillStreak()));
									deadlines.add(FormatUtil.format("&3GameXP: &e{0}", dp.getGameXP()));
									deadlines.add(FormatUtil.format("&3----------------------------"));

									for (String deadline : deadlines)
									{
										pdied.sendMessage(deadline);
									}

									// Handle killer
									ArenaPlayer kp = plugin.getArenaPlayer(killer);
									if (kp != null)
									{
										kp.setKills(kp.getKills() + 1);
										kp.setKillStreak(kp.getKillStreak() + 1);
										kp.getArena().handleKillStreak(kp);
										kp.addXP(100);

										List<String> killerlines = new ArrayList<String>();
										killerlines.add(FormatUtil.format("&3----------------------------"));
										killerlines.add(FormatUtil.format("&3Kills: &e{0}", kp.getKills()));
										killerlines.add(FormatUtil.format("&3Deaths: &e{0}", kp.getDeaths()));
										killerlines.add(FormatUtil.format("&3Streak: &e{0}", kp.getKillStreak()));
										killerlines.add(FormatUtil.format("&3GameXP: &e{0}", kp.getGameXP()));
										killerlines.add(FormatUtil.format("&3----------------------------"));

										for (String killerline : killerlines)
										{
											killer.sendMessage(killerline);
										}
									}
								}
								else
								{
									String name = FormatUtil.getFriendlyName(damager.getType());

									plugin.debug("Player {0} was killed by {1}", pdied.getName(), name);
									ar.tellPlayers("&e{0} &3was killed by &3{1} &e{2}", pdied.getName(), FormatUtil.getArticle(name), name);

									List<String> deadlines = new ArrayList<String>();
									deadlines.add(FormatUtil.format("&3----------------------------"));
									deadlines.add(FormatUtil.format("&cKills: &e{0}", dp.getKills()));
									deadlines.add(FormatUtil.format("&cDeaths: &e{0}", dp.getDeaths()));
									deadlines.add(FormatUtil.format("&cStreak: &e{0}", dp.getKillStreak()));
									deadlines.add(FormatUtil.format("&cGameXP: &e{0}", dp.getGameXP()));
									deadlines.add(FormatUtil.format("&3----------------------------"));

									for (String deadline : deadlines)
									{
										pdied.sendMessage(deadline);
									}
								}

								return;
							}
						}
					}

					if (dc == DamageCause.PROJECTILE)
					{
						if (ede instanceof EntityDamageByEntityEvent)
						{
							EntityDamageByEntityEvent edbe = (EntityDamageByEntityEvent) ede;
							Entity damager = edbe.getDamager();
							if (edbe.getDamager() != null)
							{
								if (damager instanceof Projectile)
								{
									Projectile proj = (Projectile) damager;
									if (proj.getShooter() != null)
									{
										if (proj.getShooter() instanceof Player)
										{
											Player killer = (Player) proj.getShooter();

											ar.tellPlayers("&e{0} &3killed &e{1} &3with {2}", killer.getName(), pdied.getName(),
													getWeapon(killer));

											List<String> deadlines = new ArrayList<String>();
											deadlines.add(FormatUtil.format("&3----------------------------"));
											deadlines.add(FormatUtil.format("&3Kills: &e{0}", dp.getKills()));
											deadlines.add(FormatUtil.format("&3Deaths: &e{0}", dp.getDeaths()));
											deadlines.add(FormatUtil.format("&3Streak: &e{0}", dp.getKillStreak()));
											deadlines.add(FormatUtil.format("&3GameXP: &e{0}", dp.getGameXP()));
											deadlines.add(FormatUtil.format("&3----------------------------"));

											for (String deadline : deadlines)
											{
												pdied.sendMessage(deadline);
											}

											// Handle killer
											ArenaPlayer kp = plugin.getArenaPlayer(killer);
											if (kp != null)
											{
												kp.setKills(kp.getKills() + 1);
												kp.setKillStreak(kp.getKillStreak() + 1);
												kp.getArena().handleKillStreak(kp);
												kp.addXP(100);

												List<String> killerlines = new ArrayList<String>();
												killerlines.add(FormatUtil.format("&3----------------------------"));
												killerlines.add(FormatUtil.format("&3Kills: &e{0}", kp.getKills()));
												killerlines.add(FormatUtil.format("&3Deaths: &e{0}", kp.getDeaths()));
												killerlines.add(FormatUtil.format("&3Streak: &e{0}", kp.getKillStreak()));
												killerlines.add(FormatUtil.format("&3GameXP: &e{0}", kp.getGameXP()));
												killerlines.add(FormatUtil.format("&3----------------------------"));

												for (String killerline : killerlines)
												{
													killer.sendMessage(killerline);
												}
											}
										}
										else
										{
											String name = "";
											if (proj.getShooter() instanceof LivingEntity)
												name = FormatUtil.getFriendlyName(((LivingEntity) proj.getShooter()).getType());

											plugin.debug("Player {0} was shot by {1}", pdied.getName(), name);
											ar.tellPlayers("&e{0} &3was shot by &3{1} &e{2}", pdied.getName(), FormatUtil.getArticle(name),
													name);

											List<String> deadlines = new ArrayList<String>();
											deadlines.add(FormatUtil.format("&3----------------------------"));
											deadlines.add(FormatUtil.format("&3Kills: &e{0}", dp.getKills()));
											deadlines.add(FormatUtil.format("&3Deaths: &e{0}", dp.getDeaths()));
											deadlines.add(FormatUtil.format("&3Streak: &e{0}", dp.getKillStreak()));
											deadlines.add(FormatUtil.format("&3GameXP: &e{0}", dp.getGameXP()));
											deadlines.add(FormatUtil.format("&3----------------------------"));

											for (String deadline : deadlines)
											{
												pdied.sendMessage(deadline);
											}
										}

										return;
									}
								}
							}
						}
					}

					// There's probably nothing else we can do here, so just turn it into a string

					String dcs = FormatUtil.getFriendlyName(pdied.getLastDamageCause().getCause().toString());
					plugin.debug("Player {0} was killed by {1}", pdied.getName(), dcs);
					ar.tellPlayers("&e{0} &3was killed by &e{1}", pdied.getName(), dcs);

					List<String> deadlines = new ArrayList<String>();
					deadlines.add(FormatUtil.format("&3----------------------------"));
					deadlines.add(FormatUtil.format("&3Kills: &e{0}", dp.getKills()));
					deadlines.add(FormatUtil.format("&3Deaths: &e{0}", dp.getDeaths()));
					deadlines.add(FormatUtil.format("&3Streak: &e{0}", dp.getKillStreak()));
					deadlines.add(FormatUtil.format("&3GameXP: &e{0}", dp.getGameXP()));
					deadlines.add(FormatUtil.format("&3----------------------------"));

					for (String deadline : deadlines)
					{
						pdied.sendMessage(deadline);
					}
				}
			}
		}
		else
		{
			if (died instanceof LivingEntity)
			{
				LivingEntity lentity = (LivingEntity) died;
				if (lentity.getKiller() instanceof Player)
				{
					Player killer = lentity.getKiller();
					if (plugin.isInArena(killer))
					{
						ArenaPlayer ak = plugin.getArenaPlayer(killer);

						// Selectively count mob kills
						if (ak.getArena().isCountMobKills())
						{
							ak.addXP(25);
							ak.setKills(ak.getKills() + 1);
							ak.setKillStreak(ak.getKillStreak() + 1);
							ak.getArena().handleKillStreak(ak);

							List<String> lines = new ArrayList<String>();

							String name = FormatUtil.getFriendlyName(lentity.getType());
							lines.add(plugin.getPrefix()
									+ FormatUtil.format("&e{0} &3killed {1} &e{2}", killer.getName(), FormatUtil.getArticle(name), name));
							lines.add(FormatUtil.format("&3----------------------------"));
							lines.add(FormatUtil.format("&3Kills: &e{0}", ak.getKills()));
							lines.add(FormatUtil.format("&3Deaths: &e{0}", ak.getDeaths()));
							lines.add(FormatUtil.format("&3Streak: &e{0}", ak.getKillStreak()));
							lines.add(FormatUtil.format("&3GameXP: &e{0}", ak.getGameXP()));
							lines.add(FormatUtil.format("&3----------------------------"));

							for (String line : lines)
							{
								killer.sendMessage(line);
							}
						}
					}
				}
			}
		}
	}

	// Line count for onEntityDeath = 416

	private final String getWeapon(Player player)
	{
		StringBuilder ret = new StringBuilder();

		ItemStack inHand = player.getItemInHand();
		if (inHand == null || inHand.getType() == Material.AIR)
		{
			ret.append("their fists");
		}
		else
		{
			String name = FormatUtil.getFriendlyName(inHand.getType());
			String article = FormatUtil.getArticle(name);
			ret.append("&3" + article + " &e" + name);
		}

		return ret.toString();
	}

	private final Player getPlayer(Entity entity)
	{
		if (entity instanceof Player)
		{
			return (Player) entity;
		}

		if (entity instanceof Projectile)
		{
			Projectile proj = (Projectile) entity;
			if (proj.getShooter() instanceof Player)
				return (Player) proj.getShooter();
		}

		return null;
	}
}