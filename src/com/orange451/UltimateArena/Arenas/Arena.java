package com.orange451.UltimateArena.Arenas;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.User;
import com.orange451.UltimateArena.InventoryHelper;
import com.orange451.UltimateArena.Arenas.Objects.ArenaConfig;
import com.orange451.UltimateArena.Arenas.Objects.ArenaFlag;
import com.orange451.UltimateArena.Arenas.Objects.ArenaPlayer;
import com.orange451.UltimateArena.Arenas.Objects.ArenaSpawn;
import com.orange451.UltimateArena.Arenas.Objects.ArenaZone;
import com.orange451.UltimateArena.util.Util;

/**
 * @author orange451
 * @editor dmulloy2
 */

public abstract class Arena 
{
	public ArrayList<ArenaPlayer> arenaplayers = new ArrayList<ArenaPlayer>();
	public ArrayList<ArenaFlag> flags = new ArrayList<ArenaFlag>();
	public ArrayList<ArenaSpawn> spawns = new ArrayList<ArenaSpawn>();
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
	public ArenaZone az = null;
	public boolean pauseStartTimer = false;
	
	public Arena(ArenaZone az) 
	{
		this.az = az;
		this.name = az.arenaName;
		this.world = az.world;
		this.az.timesPlayed++;
		this.az.plugin.arenasPlayed++;
		
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
			this.maxwave = config.maxwave;
			
			if (this.maxDeaths < 1) 
			{
				this.maxDeaths = 1;
			}
		}
	}
	
	public void addPlayer(Player player)
	{
		ArenaPlayer pl = new ArenaPlayer(player, this);
		pl.team = getTeam();
		arenaplayers.add(pl);
		spawn(player.getName(), false);
		player.sendMessage(ChatColor.GOLD + "You have joined the arena");
		
		// Basic things players need to play
		player.setGameMode(GameMode.SURVIVAL);
		player.setHealth(20);
		player.setFoodLevel(20);
		player.setFireTicks(0);
		
		// If essentials is found, remove god mode.
		PluginManager pm = az.plugin.getServer().getPluginManager();
		if (pm.isPluginEnabled("Essentials"))
		{
			IEssentials ess = null;
			Plugin essPlugin = pm.getPlugin("Essentials");
			ess = (IEssentials) essPlugin;
			User user = ess.getUser(player);
			if (user.isGodModeEnabled())
				user.setGodModeEnabled(false);
			if (user.isFlying())
				user.setFlying(false);
		}
		az.plugin.removePotions(player);
		updatedTeams = true;
	}
	
	public int getTeam() 
	{
		return 1;
	}
	
	public void announce() 
	{
		if (announced == 0) 
		{
			az.plugin.getServer().broadcastMessage(ChatColor.AQUA + az.arenaType + ChatColor.GOLD + " arena has been created!");
		}
		else
		{
			az.plugin.getServer().broadcastMessage(ChatColor.GOLD + "Hurry up and join the " + ChatColor.AQUA + az.arenaType + ChatColor.GOLD + " arena!");
		}
		az.plugin.getServer().broadcastMessage(ChatColor.GOLD + "type " + ChatColor.AQUA + "/ua join " + az.arenaName + ChatColor.GOLD + " to join!");
		announced++;
	}
	
	public int getBalancedTeam()
	{
		//returns the team a new player should be on, if there are two teams
		int amt1 = 0;
		int amt2 = 0;
		for (int i = 0; i < arenaplayers.size(); i++) 
		{
			if (!arenaplayers.get(i).out) 
			{
				if (arenaplayers.get(i).team == 1)
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
		//team based team checking
		//checks if any team is empty, and rewards the winning team
		//returns false if the arena ended
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
		//returns the arenaplayer when given a regular player
		if (p != null) 
		{
			for (int i = 0; i < arenaplayers.size(); i++) 
			{
				try
				{
					if (arenaplayers.get(i).player != null)
					{
						if (arenaplayers.get(i).player.getName().equals(p.getName())) 
						{
							if (arenaplayers.get(i).out != true) 
							{
								return arenaplayers.get(i);
							}
						}
					}
				}
				catch(Exception e) 
				{
					az.plugin.getLogger().severe("Error: " + e.getMessage());
				}
			}
		}
		return null;
	}
	
	public void spawnAll() 
	{
		az.plugin.getLogger().info("Spawning all players!");
		class SpawnTask extends BukkitRunnable
		{
			@Override
			public void run()
			{
				//spawns every player
				for (int i = 0; i < arenaplayers.size(); i++)
				{
					try
					{
						ArenaPlayer ap = arenaplayers.get(i);
						if (ap != null)
						{
							if (ap.out != true) 
							{
								spawn(ap.player.getName(), false);
							}
						}
					}
					catch(Exception e) 
					{
						az.plugin.getLogger().severe("Error spawning players: " + e.getMessage());
					}
				}
			}
		}
		new SpawnTask().runTask(az.plugin);
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
				{
					loc = az.lobbyBLUspawn.clone();
				}
			}
			else
			{
				loc = az.team1spawn.clone();
				if (ap.team == 2) 
				{
					loc = az.team2spawn.clone();
				}
			}
		}
		catch(Exception e) 
		{
			az.plugin.getLogger().severe("Error getting player spawn: " + e.getMessage());
		}
		if (loc != null)
		{
			loc = loc.clone().add(0.25, 1, 0.25);
		}
		return loc;
	}
	
	public void spawn(String name, boolean alreadyspawned)
	{
		//default spawning system
		//spawns the player to THEIR team spawn, and gives them their class
		if (!stopped)
		{
			final Player p = Util.matchPlayer(name);
			if (p != null) 
			{
				for (int i = 0; i < arenaplayers.size(); i++) 
				{
					ArenaPlayer ap = arenaplayers.get(i);
					try
					{
						if (ap != null)
						{
							if (ap.player != null) 
							{
								if (!ap.out) 
								{
									if (ap.player.getName().equals(name)) 
									{
										if (ap.deaths < this.maxDeaths) 
										{
											Location loc = getSpawn(ap);
											if (loc != null) 
											{
												final Location nloc = new Location(loc.getWorld(), loc.getX() + 0.25, loc.getY() + 1.0, loc.getZ() + 0.25);
												class TeleportTask extends BukkitRunnable
												{
													@Override
													public void run() 
													{
														teleport(p, nloc);
													}
												}
												new TeleportTask().runTask(az.plugin);
											}
											ap.spawn();
											if (!alreadyspawned)
											{
												onSpawn(ap);
											}
										}
									}
								}
								else
								{
									p.setLevel(ap.baselevel);
									p.setExp(ap.startxp);
									p.giveExp((int) (ap.XP / 9.0));
								}
							}
						}
					}
					catch(Exception e)
					{
						az.plugin.getLogger().severe("Error: " + e.getMessage());
					}
				}
			}
		}
	}
	
	public void onSpawn(ArenaPlayer apl) 
	{	
	}
	
	public void onPlayerDeath(ArenaPlayer pl) 
	{
		pl.amtkicked = 0;
	}
	
	public void reward(ArenaPlayer p, Player pl, boolean half)
	{
		//default rewarding sytem
		if (config != null) 
		{
			config.giveRewards(pl, half);
		}
		else
		{
			Inventory inv = pl.getInventory();
			inv.addItem(new ItemStack(Material.GOLD_INGOT, 1));
		}
		
		pl.setLevel(p.baselevel);
		pl.setExp(p.startxp);
		pl.giveExp((int) Math.ceil(p.XP / 9));
		pl.sendMessage(ChatColor.BLUE + "Thanks for playing!");
	}
	
	public void rewardTeam(int team, String string, boolean half)
	{
		//rewards the winning team (use setWinningTeam)
		for (int i = 0; i < arenaplayers.size(); i++)
		{
			ArenaPlayer apl = arenaplayers.get(i);
			if (apl != null) 
			{
				if (apl.canReward)
				{
					if (apl.team == team || team == -1)
					{
						try
						{
							reward(apl, apl.player, half);
							apl.player.sendMessage(string);
						}
						catch(Exception e) 
						{
							az.plugin.getLogger().severe("Error: " + e.getMessage());
						}
					}
				}
			}
		}
	}
	
	public void setWinningTeam(int team)
	{
		//sets the winning team, -1 for everyone wins
		for (int i = 0; i < arenaplayers.size(); i++)
		{
			ArenaPlayer apl = arenaplayers.get(i);
			if (apl != null)
			{
				if (apl.out == false)
				{
					apl.canReward = false;
					if (apl.team == team || team == -1)
					{
						apl.canReward = true;
					}
				}
			}
		}
		this.winningTeam = team;
	}
	
	public void checkPlayerPoints(int max)
	{
		//checks to see if any player has the max amount of points needed to win
		for (int i = 0; i < arenaplayers.size(); i++)
		{
			ArenaPlayer apl = arenaplayers.get(i);
			if (apl != null)
			{
				if (!apl.out)
				{
					if (apl.points >= max)
					{
						reward(apl, Util.matchPlayer(apl.username), false);
						this.tellPlayers(ChatColor.GRAY + "Player " + ChatColor.GOLD + apl.username + ChatColor.GRAY + " has won!");
						stop();
					}
				}
			}
		}
	}
	
	public boolean checkEmpty() 
	{
		boolean ret = isEmpty();
		if (ret)
			stop();
		
		return ret;
	}
	
	public boolean isEmpty()
	{
		if (starttimer <= 0) 
		{
			//check if the arena is empty
			if (amtPlayersInArena <= 1)
			{
				return true;
			}
		}
		return false;
	}
	
	public void tellPlayers(String string) 
	{
		//tells ALL players in the arena a message
		for (int i = 0; i < arenaplayers.size(); i++)
		{
			ArenaPlayer apl = arenaplayers.get(i);
			if (apl != null) 
			{
				if (apl.player != null) 
				{
					if (apl.player.isOnline())
					{
						if (!apl.out) 
						{
							apl.player.sendMessage(string);
						}
					}
				}
			}
		}
	}

	public void killAllNear(Location loc, int rad)
	{
		//kills ALL players in the arena near a point
		for (int i = 0; i < arenaplayers.size(); i++) 
		{
			ArenaPlayer apl = arenaplayers.get(i);
			if (apl != null) 
			{
				if (apl.player != null)
				{
					if (apl.player.isOnline())
					{
						if (!apl.out)
						{
							Location ploc = apl.player.getLocation();
							if (Util.point_distance(loc, ploc) < rad)
							{
								apl.player.setHealth(0);
							}
						}
					}
				}
			}
		}
	}
	
	public void spawnRandom(String name)
	{
		//Spawns a player to a random spawnpoint
		try
		{
			if (starttimer <= 0) 
			{
				Player p = Util.matchPlayer(name);
				if (p != null) 
				{
					ArenaPlayer ap = this.az.plugin.getArenaPlayer(p);
					if (ap != null)
					{
						if (!ap.out) 
						{
							if (spawns.size() > 0) 
							{
								teleport(p, (spawns.get(Util.random(spawns.size())).getLocation().clone()).add(0,2,0));
							}
						}
					}
				}
			}
		}
		catch(Exception e) 
		{
			az.plugin.getLogger().severe("Error: " + e.getMessage());
		}
	}
	
	public void giveItem(Player pl, int id, byte dat, int amt, String type)
	{
		//gives a player an item
		Inventory inv = pl.getInventory();
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
		try
		{
			Player pl = Util.matchPlayer(ap.player.getName());
			if (pl != null)
			{
				/**Hunger Arena check**/
				if (az.plugin.getArena(pl).type.equals("Hunger"))
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
						{
							pl.getLocation().getWorld().spawnEntity(pl.getLocation(), EntityType.ZOMBIE);
						}
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
		catch(Exception e)
		{
			az.plugin.getLogger().severe("Error: " + e.getMessage());
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
		//obvious?
		ap.out = true;
		updatedTeams = true;
	}
	
	public void stop()
	{
		//ends the arena
		stopped = true;
		onStop();
		az.plugin.getLogger().info("Preparing to stop arena: " + name + "!");
		try
		{
			for (int i = 0; i < arenaplayers.size(); i++)
			{
				try
				{
					Player player = arenaplayers.get(i).player;
					if (player != null)
					{
						if (this.az.plugin.isInArena(player)) 
						{
							if (gametimer <= maxgametime)
							{
								Util.matchPlayer(arenaplayers.get(i).player.getName()).sendMessage(ChatColor.BLUE + "Game inturrupted/ended!");
							}
							else
							{
								Util.matchPlayer(arenaplayers.get(i).player.getName()).sendMessage(ChatColor.BLUE + "Game Over!");
							}
							endPlayer(arenaplayers.get(i), false);
						}
					}
					arenaplayers.get(i).out = true;
				}
				catch(Exception e) 
				{
					az.plugin.getLogger().severe("Error: " + e.getMessage());
				}
			}
			az.plugin.activeArena.remove(this);
		}
		catch(Exception e)
		{
			az.plugin.getLogger().severe("Error: " + e.getMessage());
		}
	}
	
	public void onStop()
	{
	}
	
	public void checkPlayers() 
	{
		//check all players, first to see if they need food, or are on fire
		//check if you're a healer, and heals you
		//checks if a player is out of the arena
		if (!stopped) 
		{
			for (int i = 0; i < arenaplayers.size(); i++)
			{
				ArenaPlayer ap = arenaplayers.get(i);
				Player pl = ap.player;
				try
				{
					if (!ap.out)
					{
						if (starttimer > 0) 
						{
							pl.setFireTicks(0);
							pl.setFoodLevel(20);
						}
						ap.decideHat(pl);
						if (ap.mclass.name.equals("healer"))
						{
							ap.player.setHealth(ap.player.getHealth()+1);
						}
						
						if (!(az.plugin.isInArena(pl.getLocation()))) 
						{
							az.plugin.getLogger().info(ap.player.getName() + " got out of the Arena! Putting him back in");
							ap.spawn();
							spawn(ap.player.getName(), false);
						}
					}
				}
				catch(Exception e) 
				{
					az.plugin.getLogger().severe("Error: " + e.getMessage());
				}
			}
		}
	}
	
	public void normalize(Player p)
	{
		//removes all armor and inventory
		az.plugin.normalize(p);
	}
	
	public void teleport(final Player p, final Location add) 
	{
		//safely teleports a player regardless of multi-threading
		class TeleportThread extends BukkitRunnable
		{
			@Override
			public void run()
			{
				p.teleport(add.clone().add(0.5, 0, 0.5));
			}
		}
		new TeleportThread().runTask(az.plugin);
	}
	
	public void check() 
	{
	}
	
	public void endPlayer(final ArenaPlayer p, boolean dead) 
	{
		//when the player is kicked from the arena after too many deaths
		try
		{
			final Player pl = Util.matchPlayer(p.player.getName());
			if (pl != null) 
			{
				class EndPlayerThread extends BukkitRunnable
				{
					@Override
					public void run() 
					{
						teleport(pl, p.spawnBack.clone().add(0,2.0,0));
						normalize(pl);
						pl.setLevel(p.baselevel);
						pl.setExp(p.startxp);
						pl.giveExp((int) (p.XP / 9.0));
						pl.sendMessage(ChatColor.BLUE + "Thanks for playing!");
						
						az.plugin.removePotions(pl);
					}
				}
				new EndPlayerThread().runTask(az.plugin);

				p.out = true;
				updatedTeams = true;
				if (dead) 
				{
					pl.sendMessage(ChatColor.BLUE + "You have exceeded the death limit!");
				}
			}
		}
		catch(Exception e)
		{
			az.plugin.getLogger().severe("Error: " + e.getMessage());
		}
	}
	
	public void onStart() 
	{
		amtPlayersStartingInArena = arenaplayers.size();
	}
	
	public void onOutOfTime() 
	{
	}
	
	public void onPreOutOfTime() 
	{
	}
	
	public void checkTimers() 
	{
		if (stopped)
		{
			arenaplayers.clear();
			return;
		}
		
		if (config == null)
		{
			config = az.plugin.getConfig(type);
			reloadConfig();
		}
		
		if (!pauseStartTimer)
			starttimer--;
		broadcastTimer--;
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
		/////end the game
		if (gametimer <= 0) 
		{
			onPreOutOfTime();
			az.plugin.forceStop(az.arenaName);
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
		//get how many people are in the arena
		try
		{
			for (int i = 0; i < this.arenaplayers.size(); i++) 
			{
				if (arenaplayers.get(i).player.getName() != null)
				{
					if (Util.matchPlayer(arenaplayers.get(i).player.getName()) != null)
					{
						Player online = Util.matchPlayer(arenaplayers.get(i).player.getName());
						if (online != null)
						{
							if (arenaplayers.get(i).out != true)
							{
								if (online.isOnline()) 
								{
									if (arenaplayers.get(i).team == 1) 
									{
										team1size++;
									}
									else
									{
										team2size++;
									}
								}
							}
						}
					} 
				}
			}
		}
		catch(Exception e) 
		{
			az.plugin.getLogger().severe("Error: " + e.getMessage());
		}
		check();

		amtPlayersInArena = 0;

		for (int i = 0; i < arenaplayers.size(); i++)
		{
			try
			{
				ArenaPlayer ap = arenaplayers.get(i);
				Player pl = ap.player;
				if (ap.player.getName() != null)
				{
					if (Util.matchPlayer(ap.player.getName()) != null) 
					{
						ap.player = Util.matchPlayer(ap.player.getName());
						if (!ap.out)
						{
							
							amtPlayersInArena++;
							
							///////////////////////
							//CHECKING THE PLAYER//
							///////////////////////
							if (starttimer > 0) 
							{
								pl.setFireTicks(0);
								pl.setFoodLevel(20);
							}
							ap.decideHat(pl);
							ap.healtimer--;
							if (ap.mclass.name.equals("healer") && ap.healtimer <= 0) 
							{
								if (ap.player.getHealth()+1<=20)
								{
									if (ap.player.getHealth() < 0) 
									{
										ap.player.setHealth(1);
									}
									ap.player.setHealth(ap.player.getHealth()+1);
									ap.healtimer = 2;
								}
							}
							
							if (!(az.plugin.isInArena(pl.getLocation()))) 
							{
								az.plugin.getLogger().info(ap.player.getName() + " Got out of the arena! Putting him back in");
								ap.amtkicked++;
								spawn(ap.player.getName(), false);
							}
							////////////////////
							//DOING TIMER SHIT//
							////////////////////
							if (!pauseStartTimer) 
							{
								if (starttimer > 0 && starttimer < 11) 
								{
									Util.matchPlayer(ap.player.getName()).sendMessage(ChatColor.GOLD + Integer.toString(starttimer) + ChatColor.GRAY + " second(s) until start!");
								}
								if (starttimer == 30) 
								{
									Util.matchPlayer(ap.player.getName()).sendMessage(ChatColor.GOLD + "30 " + ChatColor.GRAY + " second(s) until start!");
								}
								if (starttimer == 60)
								{
									Util.matchPlayer(ap.player.getName()).sendMessage(ChatColor.GOLD + "60 " + ChatColor.GRAY + " second(s) until start!");
								}
								if (starttimer == 45)
								{
									Util.matchPlayer(ap.player.getName()).sendMessage(ChatColor.GOLD + "45 " + ChatColor.GRAY + " second(s) until start!");
								}
								if (starttimer == 15)
								{
									Util.matchPlayer(ap.player.getName()).sendMessage(ChatColor.GOLD + "15 " + ChatColor.GRAY + " second(s) until start!");
								}
								if (starttimer == 120) 
								{
									Util.matchPlayer(ap.player.getName()).sendMessage(ChatColor.GOLD + "120 " + ChatColor.GRAY + " second(s) until start!");
								}
							}
							
							if (gametimer > 0 && gametimer < 21)
							{
								Util.matchPlayer(ap.player.getName()).sendMessage(ChatColor.GOLD + Integer.toString(gametimer) + ChatColor.GRAY + " second(s) until end!");
							}
							if (gametimer == 60 && maxgametime > 60)
							{
								Util.matchPlayer(ap.player.getName()).sendMessage(ChatColor.GOLD + Integer.toString((gametimer-60)/60) + ChatColor.GRAY + " minute(s) until end!");
							}
							if (gametimer == maxgametime/2) 
							{
								Util.matchPlayer(ap.player.getName()).sendMessage(ChatColor.GOLD + Integer.toString(maxgametime/2) + ChatColor.GRAY + " second(s) until end!");
							}
							
							//////TP players back when dead
							if (!stopped) 
							{
								if (ap.deaths >= maxDeaths) 
								{
									try
									{
										Player p = Util.matchPlayer(ap.player.getName());
										if (p != null) 
										{
											if (p.getHealth() > 0) 
											{
												endPlayer(ap, true);
												removePlayer(ap);
											}
										}
									}
									catch(Exception e) 
									{
										az.plugin.getLogger().severe("Error: " + e.getMessage());
									}
								}
							}
						}
					}
				}
			}
			catch(Exception e) 
			{
				az.plugin.getLogger().severe("Error: " + e.getMessage());
			}
		}
		
		if (this.amtPlayersInArena == 0)
			az.plugin.forceStop(az.arenaName);
	}
}