package net.dmulloy2.ultimatearena.listeners;

import java.util.ArrayList;
import java.util.List;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaClass;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.util.FormatUtil;

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
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author dmulloy2
 */

public class EntityListener implements Listener
{
	private final UltimateArena plugin;
	public EntityListener(UltimateArena plugin)
	{
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityExplode(EntityExplodeEvent event)
	{
		// This will disable block damage via explosions
		// if it occurs in an arena
		if (! event.isCancelled())
		{
			if (plugin.isInArena(event.getLocation()))
			{
				if (!event.blockList().isEmpty())
				{
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityCombust(EntityCombustEvent event)
	{
		Entity combusted = event.getEntity();
		if (combusted instanceof Player)
		{
			Player combustedPlayer = (Player) combusted;
			if (plugin.isInArena(combustedPlayer))
			{
				Arena a = plugin.getArena(combustedPlayer);
				if (a.isInLobby())
				{
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamageByEntityHighest(EntityDamageByEntityEvent event)
	{
		// This will disable PvP in certain circumstances, like lobby PvP,
		// team killing, etc.
		if (! event.isCancelled() && event.getDamage() > 0.0D)
		{
			Entity attacker = event.getDamager();
			Entity defender = event.getEntity();

			// Decide players
			Player att = null;
			Player def = null;

			// Attacker
			if (attacker instanceof Player)
			{
				att = (Player) attacker;
			}
			else if (attacker instanceof Projectile)
			{
				Entity shooter = ((Projectile) attacker).getShooter();
				if (shooter instanceof Player)
					att = (Player) shooter;
			}

			// Defender
			if (defender instanceof Player)
			{
				def = (Player) defender;
			}
			else if (defender instanceof Projectile)
			{
				Entity shooter = ((Projectile) defender).getShooter();
				if (shooter instanceof Player)
					def = (Player) shooter;
			}

			if (att == null || def == null)
				return;

			if (plugin.isInArena(att))
			{
				ArenaPlayer ap = plugin.getArenaPlayer(att);
				if (plugin.isInArena(def))
				{
					ArenaPlayer dp = plugin.getArenaPlayer(def);
					Arena arena = ap.getArena();
					if (arena.isInLobby())
					{
						ap.sendMessage("&cYou cannot PVP in the lobby!");
						event.setCancelled(true);
						return;
					}

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
				if (plugin.isInArena(def))
				{
					att.sendMessage(plugin.getPrefix() + 
							FormatUtil.format("&cYou cannot hurt players while they are in an arena!"));
					event.setCancelled(true);
					return;
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDamageByEntityMonitor(EntityDamageByEntityEvent event)
	{
		// Repairs armor and items if in an arena
		// Also handles healer class
		if (event.getDamager() instanceof Player)
		{
			Player player = (Player) event.getDamager();
			if (plugin.isInArena(player))
			{
				ItemStack inHand = player.getItemInHand();
				if (inHand != null && inHand.getType() != Material.AIR)
				{
					if (inHand.getType().getMaxDurability() != 0)
					{
						inHand.setDurability((short) 0);
					}
				}

				for (ItemStack armor : player.getInventory().getArmorContents())
				{
					if (armor != null && armor.getType() != Material.AIR)
					{
						armor.setDurability((short) 0);
					}
				}

				// Healer
				if (event.getEntity() instanceof Player)
				{
					Player damaged = (Player) event.getEntity();
					if (plugin.isInArena(damaged))
					{
						ArenaPlayer dp = plugin.getArenaPlayer(damaged);
						ArenaPlayer ap = plugin.getArenaPlayer(player);
						if (ap.getTeam() == dp.getTeam())
						{
							ArenaClass ac = ap.getArenaClass();
							if (ac != null && ac.getName().equalsIgnoreCase("healer"))
							{
								if (inHand != null && inHand.getType() == Material.GOLD_AXE)
								{
									Player pl = dp.getPlayer();
									if ((pl.getHealth() + 2.0D) <= 20.0D)
									{
										pl.setHealth(player.getHealth() + 2.0D);

										ap.sendMessage("&3You have healed &e{0} &3for &e1 &3heart!", pl.getName());
									}
								}
							}
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamage(EntityDamageEvent event)
	{
		// Cancels all forms of damage in the lobby
		if (! event.isCancelled())
		{
			if (event.getEntity() instanceof Player)
			{
				Player player = (Player) event.getEntity();
				if (plugin.isInArena(player))
				{
					Arena arena = plugin.getArena(player);
					if (arena.isInLobby())
					{
						event.setCancelled(true);
					}
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
		if (plugin.isInArena(died))
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
				if (dp.isDead())
					return;

				dp.onDeath();

				Arena ar = plugin.getArena(pdied);

				if (pdied.getKiller() instanceof Player)
				{
					Player killer = pdied.getKiller();
					if (killer.getName().equals(pdied.getName())) // Suicide
					{
						plugin.debug("Player {0} has committed suicide!", pdied.getName());
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
					else // PvP
					{
						plugin.debug("PvP has occured between two players. Killer: {0}. Killed: {1}", killer.getName(), pdied.getName());

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
						kp.setKills(kp.getKills() + 1);
						kp.setKillStreak(kp.getKillStreak() + 1);
						kp.getArena().doKillStreak(kp);
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
				// From this point on, we will return when there is a valid
				// match
				{
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
							kp.getArena().doKillStreak(kp);
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

							plugin.debug("Player {0} was killed by {1}", pdied.getName(), name);
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

					// No valid PvP/PvM reason was found
					String dc = FormatUtil.getFriendlyName(pdied.getLastDamageCause().getCause().toString());
					plugin.debug("Player {0} was killed by {1}", pdied.getName(), dc);
					ar.tellPlayers("&e{0} &3was killed by &e{1}", pdied.getName(), dc);

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
						plugin.debug("{0} has been killed by {1}", FormatUtil.getFriendlyName(lentity.getType()), killer.getName());

						ArenaPlayer ak = plugin.getArenaPlayer(killer);
						if (ak.getArena().isCountMobKills()) // Selectively count mob kills
						{
							ak.addXP(25);
							ak.setKills(ak.getKills() + 1);
							ak.setKillStreak(ak.getKillStreak() + 1);
							ak.getArena().doKillStreak(ak);

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

	/**
	 * Gets the weapon that a player has
	 * 
	 * @param player
	 *            - {@link Player} to get weapon for
	 * @return The player's weapon
	 */
	private String getWeapon(Player player)
	{
		StringBuilder ret = new StringBuilder();

		ItemStack inHand = player.getItemInHand();
		if (inHand == null || inHand.getType() == Material.AIR)
		{
			ret.append("his fists");
		}
		else
		{
			String name = FormatUtil.getFriendlyName(inHand.getType());
			String article = FormatUtil.getArticle(name);
			ret.append("&3" + article + " &e" + name);
		}

		return ret.toString();
	}
}