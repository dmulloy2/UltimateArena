package com.orange451.UltimateArena.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.Arenas.Arena;

public class PluginEntityListener implements Listener {
	
	UltimateArena plugin;
	
	public PluginEntityListener(UltimateArena plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityExplode(EntityExplodeEvent event) {
		if (plugin.isInArena(event.getLocation())) {
			if (event.blockList().size() > 1) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
		try{
			Entity att = event.getDamager();
			if (att instanceof Player) {
				Player attacker = (Player)att;
				if (this.plugin.isInArena(attacker)) {
					ItemStack itm = attacker.getItemInHand();
					itm.setDurability((short) 0);
				}
			}
		}catch(Exception e) {
			//
		}
		try{
			
			Player defender = (Player) event.getEntity();
			
			if (!(event.getEntity() instanceof Player)) {
				return;
			}
			
			Entity att = (event).getDamager();
			
			if (!(att instanceof Player)) {
				
				if ((event).getDamager() instanceof LivingEntity) {
					LivingEntity attacker = (LivingEntity)att;
					if (plugin.isInArena(attacker.getLocation())) {
						if (plugin.isInArena(defender.getLocation())) {
							event.setCancelled(false);
						}
					}
				}
				if ((event).getDamager() instanceof Arrow) {
					Arrow attacker = (Arrow)att;
					try{
						Player shooter = (Player) attacker.getShooter();
						if ((plugin.isInArena(defender)) && (plugin.isInArena(shooter))) {
							event.setCancelled(false);
							Arena a = plugin.getArena(defender);
							if (shooter.getItemInHand().getType().equals(Material.BOW)) {
								shooter.getItemInHand().setDurability((short) 0);
							}
							if (plugin.getArenaPlayer(defender).team == plugin.getArenaPlayer(shooter).team) {
								if (!a.allowTeamKilling) {
									event.setCancelled(true);
									shooter.sendMessage("You cannot hurt your teammate!");
								}
							}
						}
					}catch(Exception e) {
						//
					}
					return;
				}
				return;
			}
			Player attacker = (Player)att;
			if ((plugin.isInArena(defender)) && (plugin.isInArena(attacker))) {
				event.setCancelled(false);
				Arena a = plugin.getArena(defender);
				if (a.starttimer >= 0) {
					event.setCancelled(true);
					return;
				}
				if (plugin.getArenaPlayer(defender).team == plugin.getArenaPlayer(attacker).team) {
					if (!a.allowTeamKilling) {
						event.setCancelled(true);
						if (attacker.getItemInHand().getType().equals(Material.GOLD_AXE)) {
							defender.setHealth(defender.getHealth()+2);
							attacker.sendMessage(ChatColor.GRAY + "You have healed " + ChatColor.GOLD + defender.getName() + ChatColor.GRAY + " for 1 hearts");
						}else{
							attacker.sendMessage("You cannot hurt your teammate!");
						}
					}
				}
			}
			
		}catch(Exception e) {
			//
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDeath(EntityDeathEvent event) {
		try{
			Entity died = event.getEntity();
			if (died instanceof Player) {
				Player pdied = (Player)died;
				if (plugin.isInArena(pdied)) {
					event.getDrops().clear();
					this.plugin.getArena(pdied).onPlayerDeath(plugin.getArenaPlayer(pdied));
				}
			}
			EntityDamageEvent dev = event.getEntity().getLastDamageCause();
			if (dev != null) {
				if (dev.getEntity() != null) {
					if ((dev.getEntity() instanceof Player)) {
						Player dead = (Player)event.getEntity();
						
						if (dev.getCause() != null) {
							DamageCause dc = dev.getCause();
							
							if (dc.equals(DamageCause.ENTITY_ATTACK)) {
								Entity damager = ((EntityDamageByEntityEvent)dev).getDamager(); 
								if (plugin.isInArena(dead)) {
									String attackerName = damager.getType().getName();
									if (damager instanceof Player) {
										attackerName = ((Player)damager).getName();
									}
									event.getDrops().clear();
									plugin.getArenaPlayer(dead).killstreak = 0;
									
									plugin.getArenaPlayer(dead).deaths++;
									String line1 = ChatColor.GREEN + attackerName + ChatColor.WHITE + " killed " + ChatColor.RED + dead.getName();
									String line2 = ChatColor.RED + dead.getName() + " You have been killed by " + attackerName;
									String line3 = ChatColor.RED + "----------------------------";
									String line4 = ChatColor.RED + "Kills: " + plugin.getArenaPlayer(dead).kills;
									String line5 = ChatColor.RED + "Deaths: " + plugin.getArenaPlayer(dead).deaths;
									String line6 = ChatColor.RED + "----------------------------";
									
									dead.sendMessage(line1);
									dead.sendMessage(line2);
									dead.sendMessage(line3);
									dead.sendMessage(line4);
									dead.sendMessage(line5);
									dead.sendMessage(line6);
									if (damager instanceof Player) {
										Player attacker = (Player)((EntityDamageByEntityEvent)dev).getDamager();
										if (plugin.isInArena(attacker)) {
											plugin.getArenaPlayer(attacker).kills++;
											plugin.getArenaPlayer(attacker).killstreak++;
											plugin.getArenaPlayer(attacker).XP += 100;
											line2  = ChatColor.RED + "killed " + dead.getName() + " +100 XP";
											line4 = ChatColor.RED + "Kills: " + plugin.getArenaPlayer(attacker).kills;
											line5 = ChatColor.RED + "Deaths: " + plugin.getArenaPlayer(attacker).deaths;
											
											attacker.sendMessage(line1);
											attacker.sendMessage(line2);
											attacker.sendMessage(line3);
											attacker.sendMessage(line4);
											attacker.sendMessage(line5);
											attacker.sendMessage(line6);
											Arena ar = plugin.getArena(attacker);
											ar.doKillStreak(plugin.getArenaPlayer(attacker));
										}
									}
								}
							}else{
								if (plugin.isInArena(dead)) {
									event.getDrops().clear();
									plugin.getArenaPlayer(dead).killstreak = 0;
									
									plugin.getArenaPlayer(dead).deaths++;
									String line2 = ChatColor.RED + dead.getName() + " You have been killed by " + dc.toString();
									String line3 = ChatColor.RED + "----------------------------";
									String line4 = ChatColor.RED + "Kills: " + plugin.getArenaPlayer(dead).kills;
									String line5 = ChatColor.RED + "Deaths: " + plugin.getArenaPlayer(dead).deaths;
									String line6 = ChatColor.RED + "----------------------------";
									
									dead.sendMessage(line2);
									dead.sendMessage(line3);
									dead.sendMessage(line4);
									dead.sendMessage(line5);
									dead.sendMessage(line6);
								}
							}
						}
					}else{
						LivingEntity dead = (LivingEntity) event.getEntity();
						if (dev.getCause() != null) {
							DamageCause dc = dev.getCause();
							if (dc.equals(DamageCause.ENTITY_ATTACK)) {
								Entity damager = ((EntityDamageByEntityEvent)dev).getDamager();
								if (damager instanceof Player) {
									Player attacker = (Player)((EntityDamageByEntityEvent)dev).getDamager();
									if (plugin.isInArena(attacker)) {
										event.getDrops().clear();
										if (plugin.isInArena(attacker.getLocation())) {
											plugin.getArenaPlayer(attacker).kills++;
											plugin.getArenaPlayer(attacker).killstreak++;
											plugin.getArenaPlayer(attacker).XP += 25;
											
											
											
											String attstr = dead.getType().getName();
//											attstr = attstr.replaceAll("class.org.bukkit.craftbukkit.entity.", "");
//											attstr = attstr.substring(5, attstr.length());
											
											String line1 = ChatColor.GREEN + attacker.getName() + ChatColor.WHITE + " killed " + ChatColor.RED + attstr;
											String line2 = ChatColor.RED + "killed " + attstr + " +25 XP";
											String line3 = ChatColor.RED + "----------------------------";
											String line4 = ChatColor.RED + "Kills: " + plugin.getArenaPlayer(attacker).kills;
											String line5 = ChatColor.RED + "Deaths: " + plugin.getArenaPlayer(attacker).deaths;
											String line6 = ChatColor.RED + "----------------------------";
											
											attacker.sendMessage(line1);
											attacker.sendMessage(line2);
											attacker.sendMessage(line3);
											attacker.sendMessage(line4);
											attacker.sendMessage(line5);
											attacker.sendMessage(line6);
											
											Arena ar = plugin.getArena(attacker);
											ar.doKillStreak(plugin.getArenaPlayer(attacker));
										}
									}
								}
							}else if (dc.equals(DamageCause.PROJECTILE)) {
								Entity damager = ((EntityDamageByEntityEvent)dev).getDamager();
								if (damager instanceof Arrow) {
									if (((Arrow)((EntityDamageByEntityEvent)dev).getDamager()).getShooter() instanceof Player) {
										Player attacker = (Player) ((Arrow)((EntityDamageByEntityEvent)dev).getDamager()).getShooter();
										if (plugin.isInArena(attacker)) {
											event.getDrops().clear();
											if (plugin.isInArena(attacker.getLocation())) {
												plugin.getArenaPlayer(attacker).kills++;
												plugin.getArenaPlayer(attacker).killstreak++;
												plugin.getArenaPlayer(attacker).XP += 25;
												
												String attstr = dead.getType().getName();

//												String attstr = dead.getClass().toString();
//												attstr = attstr.replaceAll("class.org.bukkit.craftbukkit.entity.", "");
//												attstr = attstr.substring(5, attstr.length());
												
												String line1 = ChatColor.GREEN + attacker.getName() + ChatColor.WHITE + " killed " + ChatColor.RED + attstr;
												String line2 = ChatColor.RED + "killed " + attstr + " +25 XP";
												String line3 = ChatColor.RED + "----------------------------";
												String line4 = ChatColor.RED + "Kills: " + plugin.getArenaPlayer(attacker).kills;
												String line5 = ChatColor.RED + "Deaths: " + plugin.getArenaPlayer(attacker).deaths;
												String line6 = ChatColor.RED + "----------------------------";
												
												attacker.sendMessage(line1);
												attacker.sendMessage(line2);
												attacker.sendMessage(line3);
												attacker.sendMessage(line4);
												attacker.sendMessage(line5);
												attacker.sendMessage(line6);
												
												Arena ar = plugin.getArena(attacker);
												ar.doKillStreak(plugin.getArenaPlayer(attacker));
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
