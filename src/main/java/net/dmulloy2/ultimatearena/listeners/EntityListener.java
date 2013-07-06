package net.dmulloy2.ultimatearena.listeners;

import java.util.ArrayList;
import java.util.List;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaPlayer;
import net.dmulloy2.ultimatearena.util.FormatUtil;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author dmulloy2
 */

public class EntityListener implements Listener
{
	private final UltimateArena plugin;
	public EntityListener(final UltimateArena plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityExplode(EntityExplodeEvent event) 
	{
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
	public void onExplosionPrime(ExplosionPrimeEvent event)
	{
		if (! event.isCancelled())
		{
			if (plugin.isInArena(event.getEntity().getLocation()))
			{
				event.setCancelled(true);
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
			ItemStack inHand = player.getItemInHand();
			if (inHand.getType().getMaxDurability() != 0)
			{
				inHand.setDurability((short) 0);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDeath(EntityDeathEvent event)
	{
		Entity died = event.getEntity();
		if (died == null)
			return;
		
		if (plugin.isInArena(died.getLocation())) // Clear drops in arena
		{
			event.getDrops().clear();
			event.setDroppedExp(0);
		}
		
		if (died instanceof Player) // A player died
		{
			Player pdied = (Player)died;
			plugin.debug("Player {0} has died.", pdied.getName());
			if (plugin.isInArena(pdied))
			{
				ArenaPlayer dp = plugin.getArenaPlayer(pdied);
				dp.setKillstreak(0);
				dp.setDeaths(dp.getDeaths() + 1);
				
				Arena ar = plugin.getArena(pdied);
				ar.onPlayerDeath(dp);
				
				if (pdied.getKiller() instanceof Player) 
				{
					Player killer = (Player)pdied.getKiller();
					plugin.debug("Killer: {0}", killer.getName());
					if (killer.getName() == pdied.getName()) // Suicide
					{
						plugin.debug("Player {0} has committed suicide!", pdied.getName());
						
						List<String> lines = new ArrayList<String>();
						lines.add(FormatUtil.format("&a{0} &fhas commited &csuicide!", pdied.getName()));
						lines.add(FormatUtil.format("&c----------------------------"));
						lines.add(FormatUtil.format("&cKills: &f{0}", dp.getKills()));
						lines.add(FormatUtil.format("&cDeaths: &f{0}", dp.getDeaths()));
						lines.add(FormatUtil.format("&cStreak: &f{0}", dp.getKillstreak()));
						lines.add(FormatUtil.format("&c----------------------------"));
						
						for (String line : lines)
						{
							pdied.sendMessage(line);
						}
					}
					else // PVP
					{
						plugin.debug("PVP has occured between two players. Killer: {0}. Killed: {1}", killer.getName(), pdied.getName());
						
						List<String> deadlines = new ArrayList<String>();
						deadlines.add(FormatUtil.format("&c{0} &fhas killed &a{1}", killer.getName(), pdied.getName()));
						deadlines.add(FormatUtil.format("&c----------------------------"));
						deadlines.add(FormatUtil.format("&cKills: &f{0}", dp.getKills()));
						deadlines.add(FormatUtil.format("&cDeaths: &f{0}", dp.getDeaths()));
						deadlines.add(FormatUtil.format("&cStreak: &f{0}", dp.getKillstreak()));
						deadlines.add(FormatUtil.format("&c----------------------------"));
						
						for (String deadline : deadlines)
						{
							pdied.sendMessage(deadline);
						}
						
						// Handle killer
						ArenaPlayer kp = plugin.getArenaPlayer(killer);
						kp.setKills(kp.getKills() + 1);
						kp.setKillstreak(kp.getKillstreak() + 1);
						
						List<String> killerlines = new ArrayList<String>();
						killerlines.add(FormatUtil.format("&a{0} &fhas killed &c{1}", killer.getName(), pdied.getName()));
						killerlines.add(FormatUtil.format("&c----------------------------"));
						killerlines.add(FormatUtil.format("&cKills: &f{0}", kp.getKills()));
						killerlines.add(FormatUtil.format("&cDeaths: &f{0}", kp.getDeaths()));
						killerlines.add(FormatUtil.format("&cStreak: &f{0}", kp.getKillstreak()));
						killerlines.add(FormatUtil.format("&c----------------------------"));
						
						for (String killerline : killerlines)
						{
							killer.sendMessage(killerline);
						}
					}
				}
				else
				{
					if (pdied.getKiller() instanceof LivingEntity)
					{
						LivingEntity lentity = (LivingEntity)pdied.getKiller();
						plugin.debug("Player {0} was killed by {1}", pdied.getName(), FormatUtil.getFriendlyName(lentity.getType()));
						
						List<String> deadlines = new ArrayList<String>();
						
						String name = FormatUtil.getFriendlyName(lentity.getType());
						deadlines.add(FormatUtil.format("&a{0} &fwas killed by &c{0}", pdied.getName(), FormatUtil.getArticle(name) + name));
						deadlines.add(FormatUtil.format("&c----------------------------"));
						deadlines.add(FormatUtil.format("&cKills: &f{0}", dp.getKills()));
						deadlines.add(FormatUtil.format("&cDeaths: &f{0}", dp.getDeaths()));
						deadlines.add(FormatUtil.format("&cStreak: &f{0}", dp.getKillstreak()));
						deadlines.add(FormatUtil.format("&c----------------------------"));
						
						for (String deadline : deadlines)
						{
							pdied.sendMessage(deadline);
						}
					}
					else
					{
						List<String> deadlines = new ArrayList<String>();
						
						String dc = pdied.getLastDamageCause().getCause().toString();
						plugin.debug("Player {0} was killed by {1}", pdied.getName(), FormatUtil.getFriendlyName(dc));
						
						deadlines.add(FormatUtil.format("&a{0} &fwas killed by &c{0}", pdied.getName(), FormatUtil.getFriendlyName(dc)));
						deadlines.add(FormatUtil.format("&c----------------------------"));
						deadlines.add(FormatUtil.format("&cKills: &f{0}", dp.getKills()));
						deadlines.add(FormatUtil.format("&cDeaths: &f{0}", dp.getDeaths()));
						deadlines.add(FormatUtil.format("&cStreak: &f{0}", dp.getKillstreak()));
						deadlines.add(FormatUtil.format("&c----------------------------"));
						
						for (String deadline : deadlines)
						{
							pdied.sendMessage(deadline);
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
					plugin.debug("{0} has been killed by {1}", FormatUtil.getFriendlyName(lentity.getType()), killer.getName());
					
					ArenaPlayer ak = plugin.getArenaPlayer(killer);
					ak.setKills(ak.getKills() + 1);
					ak.setKillstreak(ak.getKillstreak() + 1);
					ak.getArena().doKillStreak(ak);
					
					List<String> lines = new ArrayList<String>();
					
					String name = FormatUtil.getFriendlyName(lentity.getType());
					lines.add(FormatUtil.format("&a{0} &fhas killed {1} &c{2}", killer.getName(), FormatUtil.getArticle(name), name));
					lines.add(FormatUtil.format("&c----------------------------"));
					lines.add(FormatUtil.format("&cKills: &f{0}", ak.getKills()));
					lines.add(FormatUtil.format("&cDeaths: &f{0}", ak.getDeaths()));
					lines.add(FormatUtil.format("&cStreak: &f{0}", ak.getKillstreak()));
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