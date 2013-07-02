package net.dmulloy2.ultimatearena.arenas;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.PotionEffect;

import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.User;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.objects.*;
import net.dmulloy2.ultimatearena.events.*;
import net.dmulloy2.ultimatearena.util.*;

public abstract class Arena 
{
	public List<ArenaPlayer> arenaplayers = new ArrayList<ArenaPlayer>();
	public List<ArenaFlag> flags = new ArrayList<ArenaFlag>();
	public List<ArenaSpawn> spawns = new ArrayList<ArenaSpawn>();
	
	public ArenaConfig config;
	
	public int amountFlagCap = 0;
	public int maxDeaths = 1;
	public int wave = 0;
	public int startingAmount = 0;
	public int starttimer;
	public int gametimer;
	public int maxgametime;
	public int maxwave = 15;
	public int broadcastTimer = 45;
	public int announced = 0;
	public int winningTeam = 999;
	public int canStep;
	public int timer;
	public int amtPlayersInArena;
	public int amtPlayersStartingInArena;
	public int team1size;
	public int team2size;
	
	public boolean allowTeamKilling = false;
	public boolean pauseStartTimer = false;
	public boolean forceStop = false;
	public boolean stopped = false;
	public boolean start = false;
	
	public boolean endGameonDeath;
	public boolean captureFlag;
	public boolean disabled;
	public boolean updatedTeams;
	
	public World world;
	
	public String type;
	public String name = "";
	
	public ArenaZone az;
	public UltimateArena plugin;
	
	public Arena(ArenaZone az) 
	{
		this.az = az;
		this.plugin = az.plugin;
		this.name = az.arenaName;
		this.world = az.world;
		this.az.timesPlayed++;
		this.plugin.arenasPlayed++;
		
		if (this.maxDeaths < 1) 
		{
			this.maxDeaths = 1;
		}
	}
	
	public void reloadConfig() 
	{
		if (config != null) 
		{
			this.maxgametime = config.gameTime;
			this.gametimer = config.gameTime;
			this.starttimer = config.lobbyTime;
			this.maxDeaths = config.maxDeaths;
			this.allowTeamKilling = config.allowTeamKilling;
			this.maxwave = config.maxWave;
			
			if (this.maxDeaths < 1) 
			{
				this.maxDeaths = 1;
			}
		}
	}
	
	public void addPlayer(Player player)
	{
		ArenaPlayer pl = new ArenaPlayer(player, this);
		arenaplayers.add(pl);
		pl.team = getTeam();
		
		// Save Inventories
		if (plugin.getConfig().getBoolean("saveInventories"))
		{
			pl.saveInventory();
			pl.clearInventory();
		}
		
		spawn(player.getName(), false);
		player.sendMessage(ChatColor.GOLD + "You have joined the arena!");
		
		// Basic things players need to play
		player.setGameMode(GameMode.SURVIVAL);
		player.setHealth(20);
		player.setFoodLevel(20);
		player.setFireTicks(0);
		
		// If essentials is found, remove god mode.
		PluginManager pm = plugin.getServer().getPluginManager();
		if (pm.isPluginEnabled("Essentials"))
		{
			Plugin essPlugin = pm.getPlugin("Essentials");
			IEssentials ess = (IEssentials) essPlugin;
			User user = ess.getUser(player);
			if (user.isGodModeEnabled())
				user.setGodModeEnabled(false);
			if (user.isFlying())
				user.setFlying(false);
		}
		
		pl.clearPotionEffects();
		updatedTeams = true;
		
		// Call ArenaJoinEvent
		UltimateArenaJoinEvent joinEvent = new UltimateArenaJoinEvent(pl, this);
		plugin.getServer().getPluginManager().callEvent(joinEvent);
	}
	
	public int getTeam() 
	{
		return 1;
	}
	
	public void announce() 
	{
		if (announced == 0) 
		{
			plugin.getServer().broadcastMessage(ChatColor.AQUA + az.arenaType + ChatColor.GOLD + " arena has been created!");
		}
		else
		{
			plugin.getServer().broadcastMessage(ChatColor.GOLD + "Hurry up and join the " + ChatColor.AQUA + az.arenaType + ChatColor.GOLD + " arena!");
		}
		plugin.getServer().broadcastMessage(ChatColor.GOLD + "type " + ChatColor.AQUA + "/ua join " + az.arenaName + ChatColor.GOLD + " to join!");
		announced++;
	}
	
	public int getBalancedTeam()
	{
		// Returns the team a new player should be on, if there are two teams
		int amt1 = 0;
		int amt2 = 0;
		
		for (ArenaPlayer ap : arenaplayers)
		{
			if (ap != null && !ap.out)
			{
				if (ap.team == 1)
				{
					amt1++;
				}
				else
				{
					amt2++;
				}
			}
		}
		
		if (amt1 > amt2) 
		{
			return 2;
		}
		
		return 1;
	}
	
	public boolean simpleTeamCheck(boolean stopifEmpty) 
	{
		// Team based team checking
		// Checks if any team is empty, and rewards the winning team
		// Returns false if the arena ended
		if (team1size == 0 || team2size == 0) 
		{
			if (stopifEmpty)
			{
				stop();
			}
			if (this.startingAmount > 1)
			{
				return false;
			}
			return true;
		}
		return true;
	}
	
	public ArenaPlayer getArenaPlayer(Player p) 
	{
		// Returns the arenaplayer when given a regular player
		if (p != null) 
		{
			for (ArenaPlayer ap : arenaplayers)
			{
				if (!ap.out)
				{
					Player player = Util.matchPlayer(ap.player.getName());
					if (player != null && player.isOnline())
					{
						if (player.getName().equals(p.getName()))
							return ap;
					}
				}
			}
		}
		return null;
	}
	
	public void spawnAll() 
	{
		plugin.getLogger().info("Spawning all players for Arena \"" + az.arenaName + "\"!");
		for (ArenaPlayer ap : arenaplayers)
		{
			if (ap != null && !ap.out)
				spawn(ap.player.getName(), false);
		}
	}
	
	public Location getSpawn(ArenaPlayer ap) 
	{
		Location loc = null;
		try 
		{
			if (starttimer > 0)
			{
				loc = az.lobbyREDspawn.clone();
				if (ap.team == 2)
					loc = az.lobbyBLUspawn.clone();
			}
			else
			{
				loc = az.team1spawn.clone();
				if (ap.team == 2) 
					loc = az.team2spawn.clone();
			}
		}
		catch (Exception e)
		{
			loc = spawns.get(Util.random(spawns.size())).getLocation().clone().add(0, 2, 0);
		}
		
		if (loc != null)
		{
			loc = loc.clone().add(0.25, 1, 0.25);
		}	
		
		return loc;
	}
	
	public void spawn(String name, boolean alreadyspawned)
	{
		// Default spawning system
		// Spawns the player to THEIR team spawn, and gives them their class
		if (!stopped)
		{
			Player p = Util.matchPlayer(name);
			if (p != null) 
			{
				for (ArenaPlayer ap : arenaplayers)
				{
					if (ap.player.getName().equals(name))
					{
						if (ap != null && !ap.out)
						{
							if (ap.deaths < maxDeaths) 
							{
								Location loc = getSpawn(ap);
								if (loc != null) 
								{
									Location nloc = new Location(loc.getWorld(), loc.getX() + 0.25, loc.getY() + 1.0, loc.getZ() + 0.25);
									teleport(p, nloc);
									
									// Call spawn event
									ArenaSpawn aSpawn = new ArenaSpawn(nloc.getWorld(), nloc.getBlockX(), nloc.getBlockY(), nloc.getBlockZ());
									UltimateArenaSpawnEvent spawnEvent = new UltimateArenaSpawnEvent(ap, this, aSpawn);
									plugin.getServer().getPluginManager().callEvent(spawnEvent);
								}
								ap.spawn();
								if (!alreadyspawned)
								{
									onSpawn(ap);
								}
							}
						}
					}
				}
			}
		}
	}

	public void onSpawn(ArenaPlayer apl) {}
	
	public void onPlayerDeath(ArenaPlayer pl) 
	{
		pl.amtkicked = 0;
		
		// Call ArenaDeathEvent
		UltimateArenaDeathEvent deathEvent = new UltimateArenaDeathEvent(pl, this);
		plugin.getServer().getPluginManager().callEvent(deathEvent);
	}
	
	public void reward(ArenaPlayer ap, Player player, boolean half)
	{
		// Default rewarding sytem
		if (config != null) 
		{
			config.giveRewards(player, half);
		}
		else
		{
			PlayerInventory inv = player.getInventory();
			inv.addItem(new ItemStack(Material.GOLD_INGOT, 1));
		}
	}
	
	public void rewardTeam(int team, String string, boolean half)
	{
		// Rewards the winning team (use setWinningTeam)
		for (ArenaPlayer ap : arenaplayers)
		{
			if (ap != null && ap.canReward)
			{
				if (ap.team == team || team == -1)
				{
					Player player = Util.matchPlayer(ap.player.getName());
					if (player != null)
					{
						reward(ap, ap.player, half);
						player.sendMessage(string);
					}
				}
			}
		}
	}
	
	public void setWinningTeam(int team)
	{
		// Sets the winning team, -1 for everyone wins
		for (ArenaPlayer ap : arenaplayers)
		{
			if (ap != null && !ap.out)
			{
				ap.canReward = false;
				if (ap.team == team || team == -1)
				{
					ap.canReward = true;
				}

			}
		}
		this.winningTeam = team;
	}
	
	public void checkPlayerPoints(int max)
	{
		// Checks to see if any player has the max amount of points needed to win
		for (ArenaPlayer ap : arenaplayers)
		{
			if (ap != null && !ap.out)
			{
				if (ap.points >= max)
				{
					reward(ap, Util.matchPlayer(ap.username), false);
					this.tellPlayers(ChatColor.GRAY + "Player " + ChatColor.GOLD + ap.username + ChatColor.GRAY + " has won!");
					stop();
				}
			}
		}
	}
	
	public boolean checkEmpty() 
	{
		boolean ret = isEmpty();
		if (ret) stop();
		
		return ret;
	}
	
	public boolean isEmpty()
	{
		return (starttimer <= 0 && amtPlayersInArena <= 1);
	}
	
	public void tellPlayers(String string, Object...objects) 
	{
		// Tells ALL players in the arena a message
		for (ArenaPlayer ap : arenaplayers)
		{
			if (ap != null && !ap.out) 
			{
				Player player = Util.matchPlayer(ap.player.getName());
				if (player != null && player.isOnline())
				{
					string = FormatUtil.format(string, objects);
					player.sendMessage(string);
				}
			}
		}
	}
	
	public void killAllNear(Location loc, int rad)
	{
		// Kills ALL players in the arena near a point
		for (ArenaPlayer ap : arenaplayers)
		{
			if (ap != null && !ap.out) 
			{
				Player player = Util.matchPlayer(ap.player.getName());
				if (player != null && player.isOnline())
				{
					Location ploc = player.getLocation();
					if (Util.pointDistance(loc, ploc) < rad)
						player.setHealth(0);
				}
			}
		}
	}
	
	public void spawnRandom(String name)
	{
		//Spawns a player to a random spawnpoint
		if (starttimer <= 0) 
		{
			Player p = Util.matchPlayer(name);
			if (p != null) 
			{
				ArenaPlayer ap = plugin.getArenaPlayer(p);
				if (ap != null && !ap.out)
				{
					if (spawns.size() > 0) 
						teleport(p, (spawns.get(Util.random(spawns.size())).getLocation().clone()).add(0, 2, 0));
				}
			}
		}
	}
	
	public void giveItem(Player pl, int id, byte dat, int amt, String type)
	{
		//gives a player an item
		PlayerInventory inv = pl.getInventory();
		int slot = InventoryHelper.getFirstFreeSlot(inv);
		if (slot != -1) 
		{
			pl.sendMessage(ChatColor.GOLD + type);
			if (dat == 0)
				inv.addItem(new ItemStack(id, amt));
			else
			{
				MaterialData data = new MaterialData(id);
				data.setData(dat);
				ItemStack itm = data.toItemStack(amt);
				pl.getInventory().setItem(slot, itm);
			}
		}
	}
	
	/**Basic Killstreak System**/
	public void doKillStreak(ArenaPlayer ap) 
	{
		Player pl = Util.matchPlayer(ap.player.getName());
		if (pl != null)
		{
			/**Hunger Arena check**/
			if (plugin.getArena(pl).type.equals("Hunger"))
				return;
				
			if (ap.killstreak == 2)
				giveItem(pl, Material.POTION.getId(), (byte)9, 1, "2 kills! Unlocked strength potion!");
				
			if (ap.killstreak == 4)
			{
				giveItem(pl, Material.POTION.getId(), (byte)1, 1, "4 kills! Unlocked Health potion!");
				giveItem(pl, Material.GRILLED_PORK.getId(), (byte)0, 2, "4 kills! Unlocked Food!");
			}
			if (ap.killstreak == 5) 
			{
				if (!(this.az.arenaType.equalsIgnoreCase("cq"))) 
				{
					pl.sendMessage(ChatColor.GOLD + "5 kills! Unlocked Zombies!");
					for (int i = 0; i < 4; i++)
						pl.getLocation().getWorld().spawnEntity(pl.getLocation(), EntityType.ZOMBIE);
				}
			}
			if (ap.killstreak == 8) 
			{
				pl.sendMessage(ChatColor.GOLD + "8 kills! Unlocked attackdogs!");
				for (int i = 0; i < 2; i++)
				{
					Wolf wolf = (Wolf) pl.getLocation().getWorld().spawnEntity(pl.getLocation(), EntityType.WOLF);
					wolf.setOwner(pl);
				}
			}
			if (ap.killstreak == 12)
			{
				giveItem(pl, Material.POTION.getId(), (byte)1, 1, "12 kills! Unlocked Health potion!");
				giveItem(pl, Material.GRILLED_PORK.getId(), (byte)0, 2, "12 kills! Unlocked Food!");
			}
		}
	}
	
	public void onDisable() 
	{
		tellPlayers(ChatColor.RED + "This arena has been disabled!");
		this.gametimer = -1;
		disabled = true;
		stop();
	}
	
	public void removePlayer(ArenaPlayer ap) 
	{
		// Obvious?
		ap.out = true;
		updatedTeams = true;
	}
	
	public void stop()
	{
		// Ends the arena
		stopped = true;
		onStop();
		
		plugin.getLogger().info("Stopping arena \"" + name + "\"!");

		for (ArenaPlayer ap : arenaplayers)
		{
			if (ap != null)
			{
				Player player = Util.matchPlayer(ap.player.getName());
				if (player != null)
				{
					if (plugin.isInArena(player)) 
					{
						if (gametimer <= maxgametime)
						{
							player.sendMessage(ChatColor.BLUE + "Game inturrupted/ended!");
						}
						else
						{
							player.sendMessage(ChatColor.BLUE + "Game Over!");
						}
						endPlayer(ap, false);
					}
				}
				ap.out = true;
			}
		}
		
		plugin.activeArena.remove(this);
		
		plugin.getLogger().info("Arena \"" + name + "\" has been stopped");
	}
	
	public void onStop() {}
	
	public void checkPlayers() 
	{
		// Check all players, first to see if they need food, or are on fire
		// Check if you're a healer, and heals you
		// Checks if a player is out of the arena
		if (!stopped) 
		{
			for (ArenaPlayer ap : arenaplayers)
			{
				if (ap != null && !ap.out)
				{
					if (!ap.out)
					{
						Player player = Util.matchPlayer(ap.player.getName());
						if (player != null)
						{
							if (starttimer > 0) 
							{
								player.setFireTicks(0);
								player.setFoodLevel(20);
								ap.decideHat(player);
							}

							if (ap.mclass.name.equals("healer"))
							{
								ap.player.setHealth(ap.player.getHealth()+1);
							}
								
							if (plugin.isInArena(player.getLocation()))
							{
								plugin.getLogger().info(ap.player.getName() + " got out of the Arena! Putting him back in!");
								ap.spawn();
								spawn(ap.player.getName(), false);
							}
						}
					}
				}
			}
		}
	}
	
	public void normalize(Player p)
	{
		// Removes all armor and inventory
		plugin.normalize(p);
	}
	
	public void teleport(Player p, Location add) 
	{
		// Safely teleports a player regardless of multi-threading
		p.teleport(add.clone().add(0.5, 0, 0.5));
	}
	
	public void check() {}

	public void endPlayer(final ArenaPlayer ap, boolean dead) 
	{
		// When the player is kicked from the arena after too many deaths
		Player player = Util.matchPlayer(ap.player.getName());
		if (player != null) 
		{
			teleport(player, ap.spawnBack.clone().add(0, 2.0, 0));
			normalize(player);
			returnXP(player);
			ap.returnInventory();
			player.sendMessage(ChatColor.BLUE + "Thanks for playing!");
						
			plugin.removePotions(player);
		}
		
		// Call Arena leave event
		UltimateArenaLeaveEvent leaveEvent = new UltimateArenaLeaveEvent(ap, this);
		plugin.getServer().getPluginManager().callEvent(leaveEvent);

		ap.out = true;
		updatedTeams = true;
		if (dead) 
			player.sendMessage(ChatColor.BLUE + "You have exceeded the death limit!");
	}

	public void onStart() 
	{
		amtPlayersStartingInArena = arenaplayers.size();
	}
	
	public void onOutOfTime() {}
	
	public void onPreOutOfTime() {}
	
	public void checkTimers() 
	{
		if (stopped)
		{
			arenaplayers.clear();
			return;
		}
		
		if (config == null)
		{
			config = plugin.getConfig(type);
			reloadConfig();
		}
		
		if (!pauseStartTimer)
		{
			starttimer--;
			broadcastTimer--;
		}
		
		if (starttimer <= 0)
		{
			start();
			gametimer--;
		}
		else
		{
			if (broadcastTimer < 0)
			{
				broadcastTimer = 45;
				announce();
			}
		}
		
		// End the game
		if (gametimer <= 0) 
		{
			onPreOutOfTime();
			plugin.forceStop(az.arenaName);
			onOutOfTime();
		}
	}
	
	public void start()
	{
		if (start == false) 
		{
			this.start = true;
			this.startingAmount = this.amtPlayersInArena;
			this.amtPlayersStartingInArena = this.startingAmount;
			this.onStart();
			
			spawnAll();
			gametimer = maxgametime;
			starttimer = -1;
		}
	}
	
	public void step() 
	{
		team1size = 0;
		team2size = 0;
		checkTimers();
		
		// Get how many people are in the arena
		for (ArenaPlayer ap : arenaplayers)
		{
			if (ap != null && !ap.out)
			{
				Player player = Util.matchPlayer(ap.player.getName());
				if (player != null)
				{
					if (ap.team == 1)
						team1size++;
					else
						team2size++;
				}
			}
		}
		
		check();

		amtPlayersInArena = 0;

		for (ArenaPlayer ap : arenaplayers)
		{
			if (ap != null && !ap.out)
			{
				Player player = Util.matchPlayer(ap.player.getName());
				if (player != null && player.isOnline())
				{
					amtPlayersInArena++;
							
					// Check players in arena
					if (starttimer > 0) 
					{
						player.setFireTicks(0);
						player.setFoodLevel(20);
						ap.decideHat(player);
					}
					
					ap.healtimer--;
							
					if (ap.mclass != null)
					{
						if (ap.mclass.name.equals("healer") && ap.healtimer <= 0) 
						{
							if (ap.player.getHealth() + 1 <= 20)
							{
								if (ap.player.getHealth() < 0) 
									ap.player.setHealth(1);
								ap.player.setHealth(ap.player.getHealth()+1);
								ap.healtimer = 2;
							}
						}
								
						if (ap.mclass.hasPotionEffects)
						{
							if (ap.mclass.potionEffects.size() > 0)
							{
								for (PotionEffect effect : ap.mclass.potionEffects)
								{
									if (!ap.player.hasPotionEffect(effect.getType()))
										player.addPotionEffect(effect);
								}
							}
						}
					}
							
					if (!plugin.isInArena(player.getLocation()))
					{
						plugin.getLogger().info(ap.player.getName() + " got out of the arena! Putting him back in");
						ap.amtkicked++;
						spawn(ap.player.getName(), false);
					}
					
					// Timer Stuff
					if (!pauseStartTimer) 
					{
						if (starttimer > 0 && starttimer < 11) 
						{
							player.sendMessage(ChatColor.GOLD + Integer.toString(starttimer) + ChatColor.GRAY + " second(s) until start!");
						}
						if (starttimer == 30) 
						{
							player.sendMessage(ChatColor.GOLD + "30" + ChatColor.GRAY + " second(s) until start!");
						}
						if (starttimer == 60)
						{
							player.sendMessage(ChatColor.GOLD + "60" + ChatColor.GRAY + " second(s) until start!");
						}
						if (starttimer == 45)
						{
							player.sendMessage(ChatColor.GOLD + "45" + ChatColor.GRAY + " second(s) until start!");
						}
						if (starttimer == 15)
						{
							player.sendMessage(ChatColor.GOLD + "15" + ChatColor.GRAY + " second(s) until start!");
						}
						if (starttimer == 120) 
						{
							player.sendMessage(ChatColor.GOLD + "120" + ChatColor.GRAY + " second(s) until start!");
						}
					}
							
					if (gametimer > 0 && gametimer < 21)
					{
						player.sendMessage(ChatColor.GOLD + Integer.toString(gametimer) + ChatColor.GRAY + " second(s) until end!");
					}
					if (gametimer == 60 && maxgametime > 60)
					{
						player.sendMessage(ChatColor.GOLD + Integer.toString((gametimer-60)/60) + ChatColor.GRAY + " minute(s) until end!");
					}
					if (gametimer == maxgametime/2) 
					{
						player.sendMessage(ChatColor.GOLD + Integer.toString(maxgametime/2) + ChatColor.GRAY + " second(s) until end!");
					}
							
					// TP players back when dead
					if (!stopped) 
					{
						if (ap.deaths >= maxDeaths) 
						{
							if (player != null) 
							{
								if (player.getHealth() > 0) 
								{
									endPlayer(ap, true);
									removePlayer(ap);
								}
							}
						}
					}
				}
			}
		}
		
		if (this.amtPlayersInArena == 0)
			plugin.forceStop(az.arenaName);
	}
	
	// Return a player's xp after leaving an arena
	public void returnXP(Player player)
	{
		ArenaPlayer ap = plugin.getArenaPlayer(player);
		if (ap != null)
			player.setLevel(ap.baselevel);
	}
	
	// Save players on shutdown
	public void onShutdown()
	{
		for (ArenaPlayer pl : arenaplayers)
		{
			SavedArenaPlayer playerToSave = new SavedArenaPlayer(pl.player.getName(), pl.baselevel, pl.spawnBack, pl.savedInventory, pl.savedArmor);
			plugin.getFileHelper().savePlayer(playerToSave);
		}
		
		stop();
	}
}