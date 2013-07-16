package net.dmulloy2.ultimatearena.listeners;

import java.util.ArrayList;
import java.util.List;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaClass;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaPlayer;
import net.dmulloy2.ultimatearena.events.UltimateArenaKillEvent;
import net.dmulloy2.ultimatearena.util.FormatUtil;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author dmulloy2
 */

public class EntityListener implements Listener
{
	private List<String> deadPlayers;
	private final UltimateArena plugin;
	public EntityListener(final UltimateArena plugin)
	{
		this.plugin = plugin;
		this.deadPlayers = new ArrayList<String>();
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityExplode(EntityExplodeEvent event) 
	{
		if (! event.isCancelled())
		{
			if (plugin.isInArena(event.getLocation()))
			{
				if (! event.blockList().isEmpty())
				{
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamageByEntityHighest(EntityDamageByEntityEvent event)
	{
		if (! event.isCancelled() && event.getDamage() > 0.0D)
		{
			Entity attacker = event.getDamager();
			Entity damaged = event.getEntity();
			if ((attacker instanceof Player) && (damaged instanceof Player))
			{
				Player att = (Player)attacker;
				Player dmgd = (Player)damaged;
				if (plugin.isInArena(att))
				{
					ArenaPlayer ap = plugin.getArenaPlayer(att);
					if (ap != null && !ap.isOut())
					{
						if (plugin.isInArena(dmgd))
						{
							ArenaPlayer dp = plugin.getArenaPlayer(dmgd);
							if (dp != null && !dp.isOut())
							{
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
						}
						else
						{
							ap.sendMessage("&cYou cannot hurt players not in the arena!");
							event.setCancelled(true);
							return;
						}
					}
				}
				else
				{
					if (plugin.isInArena(dmgd))
					{
						att.sendMessage(ChatColor.RED + "You cannot hurt players while they are in an arena!");
						event.setCancelled(true);
						return;
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDamageByEntityMonitor(EntityDamageByEntityEvent event)
	{
		if (event.getDamager() instanceof Player)
		{
			Player player = (Player)event.getDamager();
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
				Entity damaged = event.getEntity();
				if (damaged instanceof Player)
				{
					ArenaPlayer dp = plugin.getArenaPlayer((Player)damaged);
					if (dp != null && ! dp.isOut())
					{
						ArenaPlayer ap = plugin.getArenaPlayer(player);
						if (ap != null && ! ap.isOut())
						{
							if (ap.getTeam() == dp.getTeam())
							{
								ArenaClass ac = ap.getArenaClass();
								if (ac != null)
								{
									if (ac.getName().equalsIgnoreCase("healer"))
									{
										if (inHand != null)
										{
											if (inHand.getType() == Material.GOLD_AXE)
											{
												Player pl = dp.getPlayer();
												if ((pl.getHealth() + 2.0D) <= 20.0D)
												{
													pl.setHealth(player.getHealth() + 2.0D);
													
													ap.sendMessage("&7You have healed &6{0} &7for &61&7 heart!", pl.getName());
												}
											}
										}
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
	public void onEntityDeath(EntityDeathEvent event)
	{
		Entity died = event.getEntity();
		if (died == null)
			return;
		
		// Clear the drops if in an arena
		if (plugin.isInArena(died))
		{
			// TODO: Make sure this works
			event.getDrops().clear();
			event.setDroppedExp(0);
		}
		
		if (died instanceof Player)
		{
			// Stops multikilling
			// TODO: A more permanent solution for this
			Player pdied = (Player)died;
			if (deadPlayers.contains(pdied.getName()))
				return;
			
			new DeadPlayerTask(pdied.getName()).runTaskLater(plugin, 60L);
			
			if (plugin.isInArena(pdied))
			{
				ArenaPlayer dp = plugin.getArenaPlayer(pdied);
				if (dp != null && !dp.isOut())
				{
					dp.setKillstreak(0);
					dp.setDeaths(dp.getDeaths() + 1);
					
					Arena ar = plugin.getArena(pdied);
					ar.onPlayerDeath(dp);
					
					if (pdied.getKiller() instanceof Player) 
					{
						Player killer = (Player)pdied.getKiller();
						if (killer.getName() == pdied.getName()) // Suicide
						{
							plugin.debug("Player {0} has committed suicide!", pdied.getName());
							ar.tellPlayers("&c{0} &fcommited &csuicide!", pdied.getName());
							
							List<String> lines = new ArrayList<String>();
							lines.add(FormatUtil.format("&c----------------------------"));
							lines.add(FormatUtil.format("&cKills: &f{0}", dp.getKills()));
							lines.add(FormatUtil.format("&cDeaths: &f{0}", dp.getDeaths()));
							lines.add(FormatUtil.format("&cStreak: &f{0}", dp.getKillstreak()));
							lines.add(FormatUtil.format("&cGameXP: &f{0}", dp.getGameXP()));
							lines.add(FormatUtil.format("&c----------------------------"));
							
							for (String line : lines)
							{
								pdied.sendMessage(line);
							}
						}
						else // PVP
						{
							plugin.debug("PVP has occured between two players. Killer: {0}. Killed: {1}", killer.getName(), pdied.getName());
							
							ar.tellPlayers("&a{0} &fkilled &c{1} &fwith {2}", killer.getName(), pdied.getName(), getWeapon(killer));
							
							List<String> deadlines = new ArrayList<String>();
							deadlines.add(FormatUtil.format("&c----------------------------"));
							deadlines.add(FormatUtil.format("&cKills: &f{0}", dp.getKills()));
							deadlines.add(FormatUtil.format("&cDeaths: &f{0}", dp.getDeaths()));
							deadlines.add(FormatUtil.format("&cStreak: &f{0}", dp.getKillstreak()));
							deadlines.add(FormatUtil.format("&cGameXP: &f{0}", dp.getGameXP()));
							deadlines.add(FormatUtil.format("&c----------------------------"));
							
							for (String deadline : deadlines)
							{
								pdied.sendMessage(deadline);
							}
							
							// Handle killer
							ArenaPlayer kp = plugin.getArenaPlayer(killer);
							if (kp != null && !kp.isOut())
							{
								kp.setKills(kp.getKills() + 1);
								kp.setKillstreak(kp.getKillstreak() + 1);
								kp.getArena().doKillStreak(kp);
								kp.addXP(100);
								
								List<String> killerlines = new ArrayList<String>();
								killerlines.add(FormatUtil.format("&c----------------------------"));
								killerlines.add(FormatUtil.format("&cKills: &f{0}", kp.getKills()));
								killerlines.add(FormatUtil.format("&cDeaths: &f{0}", kp.getDeaths()));
								killerlines.add(FormatUtil.format("&cStreak: &f{0}", kp.getKillstreak()));
								killerlines.add(FormatUtil.format("&cGameXP: &f{0}", kp.getGameXP()));
								killerlines.add(FormatUtil.format("&c----------------------------"));
								
								for (String killerline : killerlines)
								{
									killer.sendMessage(killerline);
								}
								
								UltimateArenaKillEvent killEvent = new UltimateArenaKillEvent(dp, kp, ar);
								plugin.getServer().getPluginManager().callEvent(killEvent);
							}
						}
					}
					else
					{
						if (pdied.getKiller() instanceof LivingEntity)
						{
							LivingEntity lentity = (LivingEntity)pdied.getKiller();
							String name = FormatUtil.getFriendlyName(lentity.getType());
							
							plugin.debug("Player {0} was killed by {1}", pdied.getName(), FormatUtil.getFriendlyName(lentity.getType()));
							ar.tellPlayers("&a{0} &fwas killed by {1} &c{2}", pdied.getName(), FormatUtil.getArticle(name), name);
							
							List<String> deadlines = new ArrayList<String>();
							deadlines.add(FormatUtil.format("&c----------------------------"));
							deadlines.add(FormatUtil.format("&cKills: &f{0}", dp.getKills()));
							deadlines.add(FormatUtil.format("&cDeaths: &f{0}", dp.getDeaths()));
							deadlines.add(FormatUtil.format("&cStreak: &f{0}", dp.getKillstreak()));
							deadlines.add(FormatUtil.format("&cGameXP: &f{0}", dp.getGameXP()));
							deadlines.add(FormatUtil.format("&c----------------------------"));
							
							for (String deadline : deadlines)
							{
								pdied.sendMessage(deadline);
							}
						}
						else
						{
							List<String> deadlines = new ArrayList<String>();
							
							String dc = FormatUtil.getFriendlyName(pdied.getLastDamageCause().getCause().toString());
							plugin.debug("Player {0} was killed by {1}", pdied.getName(), dc);
							ar.tellPlayers("&a{0} &fwas killed by &c{1}", pdied.getName(), dc);
							
							deadlines.add(FormatUtil.format("&c----------------------------"));
							deadlines.add(FormatUtil.format("&cKills: &f{0}", dp.getKills()));
							deadlines.add(FormatUtil.format("&cDeaths: &f{0}", dp.getDeaths()));
							deadlines.add(FormatUtil.format("&cStreak: &f{0}", dp.getKillstreak()));
							deadlines.add(FormatUtil.format("&cGameXP: &f{0}", dp.getGameXP()));
							deadlines.add(FormatUtil.format("&c----------------------------"));
							
							for (String deadline : deadlines)
							{
								pdied.sendMessage(deadline);
							}
						}
					}
				}
			}
		}
		else
		{
			if (died instanceof LivingEntity)
			{
				LivingEntity lentity = (LivingEntity)died;
				if (lentity.getKiller() instanceof Player)
				{
					Player killer = (Player)lentity.getKiller();
					if (plugin.isInArena(killer))
					{
						plugin.debug("{0} has been killed by {1}", FormatUtil.getFriendlyName(lentity.getType()), killer.getName());
						
						ArenaPlayer ak = plugin.getArenaPlayer(killer);
						if (ak != null && !ak.isOut())
						{
							ak.addXP(25);
							ak.setKills(ak.getKills() + 1);
							ak.setKillstreak(ak.getKillstreak() + 1);
							ak.getArena().doKillStreak(ak);
							
							List<String> lines = new ArrayList<String>();
							
							String name = FormatUtil.getFriendlyName(lentity.getType());
							lines.add(FormatUtil.format("&a{0} &fkilled {1} &c{2}", killer.getName(), FormatUtil.getArticle(name), name));
							lines.add(FormatUtil.format("&c----------------------------"));
							lines.add(FormatUtil.format("&cKills: &f{0}", ak.getKills()));
							lines.add(FormatUtil.format("&cDeaths: &f{0}", ak.getDeaths()));
							lines.add(FormatUtil.format("&cStreak: &f{0}", ak.getKillstreak()));
							lines.add(FormatUtil.format("&cGameXP: &f{0}", ak.getGameXP()));
							lines.add(FormatUtil.format("&c----------------------------"));
							
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
	
	private String getWeapon(Player player)
	{
		StringBuilder ret = new StringBuilder();
		
		ItemStack inHand = player.getItemInHand();
		if (inHand == null || inHand.getType() == Material.AIR)
		{
			ret.append("&chis fists");
		}
		else
		{
			String name = FormatUtil.getFriendlyName(inHand.getType());
			String article = FormatUtil.getArticle(name);
			ret.append("&f" + article + " &c" + name);
		}
		
		return ret.toString();
	}
	
	public class DeadPlayerTask extends BukkitRunnable
	{
		private final String player;
		public DeadPlayerTask(final String player)
		{
			this.player = player;
			deadPlayers.add(player);
		}
		
		@Override
		public void run()
		{
			deadPlayers.remove(player);
		}
	}
}