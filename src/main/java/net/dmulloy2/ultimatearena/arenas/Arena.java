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
	private List<ArenaPlayer> arenaplayers = new ArrayList<ArenaPlayer>();
	private List<ArenaFlag> flags = new ArrayList<ArenaFlag>();
	private List<ArenaSpawn> spawns = new ArrayList<ArenaSpawn>();
	
	private ArenaConfig config;
	
//	private int amountFlagCap = 0;
	private int maxDeaths = 1;
	private int wave = 0;
	private int startingAmount = 0;
	private int starttimer;
	private int gametimer;
	private int maxgametime;
	private int maxwave = 15;
	private int broadcastTimer = 45;
	private int announced = 0;
	private int winningTeam = 999;
//	private int canStep;
//	private int timer;
	private int amtPlayersInArena;
	private int amtPlayersStartingInArena;
	private int team1size;
	private int team2size;
	
	private boolean allowTeamKilling = false;
	private boolean pauseStartTimer = false;
	private boolean forceStop = false;
	private boolean stopped = false;
	private boolean start = false;
	
//	private boolean endGameonDeath;
//	private boolean captureFlag;
	private boolean disabled;
	private boolean updatedTeams;
	
	private World world;
	
	private String type;
	private String name = "";
	
	private ArenaZone az;
	private UltimateArena plugin;
	
	public Arena(ArenaZone az) 
	{
		this.az = az;
		this.plugin = az.getPlugin();
		this.name = az.getArenaName();
		this.world = az.getWorld();
		this.az.setTimesPlayed(this.az.getTimesPlayed() + 1);
		this.plugin.arenasPlayed++;
		
		if (this.getMaxDeaths() < 1) 
		{
			this.setMaxDeaths(1);
		}
	}
	
	public void reloadConfig() 
	{
		if (config != null) 
		{
			this.setMaxgametime(config.getGameTime());
			this.setGametimer(config.getGameTime());
			this.setStarttimer(config.getLobbyTime());
			this.setMaxDeaths(config.getMaxDeaths());
			this.setAllowTeamKilling(config.allowTeamKilling);
			this.setMaxwave(config.getMaxWave());
			
			if (this.getMaxDeaths() < 1) 
			{
				this.setMaxDeaths(1);
			}
		}
	}
	
	public void addPlayer(Player player)
	{
		ArenaPlayer pl = new ArenaPlayer(player, this);
		getArenaplayers().add(pl);
		pl.setTeam(getTeam());
		
		// Save Inventories
		if (getPlugin().getConfig().getBoolean("saveInventories"))
		{
			getPlugin().debug("Saving Inventory for Player: {0}", player.getName());
			
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
		PluginManager pm = getPlugin().getServer().getPluginManager();
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
		setUpdatedTeams(true);
		
		// Call ArenaJoinEvent
		UltimateArenaJoinEvent joinEvent = new UltimateArenaJoinEvent(pl, this);
		getPlugin().getServer().getPluginManager().callEvent(joinEvent);
	}
	
	public int getTeam() 
	{
		return 1;
	}
	
	public void announce() 
	{
		if (announced == 0) 
		{
			getPlugin().broadcast("&b{0} &6arena has been created!", getArenaZone().getArenaType());
		}
		else
		{
			getPlugin().broadcast("&6Hurry up and join the &b{0} &6arena!", getArenaZone().getArenaType());
		}
		
		getPlugin().broadcast("&6Type &b/ua join {0} &6to join!", getArenaZone().getArenaName());
		announced++;
	}
	
	public int getBalancedTeam()
	{
		// Returns the team a new player should be on, if there are two teams
		int amt1 = 0;
		int amt2 = 0;
		
		for (ArenaPlayer ap : getArenaplayers())
		{
			if (ap != null && !ap.isOut())
			{
				if (ap.getTeam() == 1)
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
		if (getTeam1size() == 0 || team2size == 0) 
		{
			if (stopifEmpty)
			{
				stop();
			}
			if (this.getStartingAmount() > 1)
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
			for (ArenaPlayer ap : getArenaplayers())
			{
				if (!ap.isOut())
				{
					Player player = Util.matchPlayer(ap.getPlayer().getName());
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
		getPlugin().outConsole("Spawning players for Arena: {0}", getArenaZone().getArenaName());
		for (ArenaPlayer ap : getArenaplayers())
		{
			if (ap != null && !ap.isOut())
				spawn(ap.getPlayer().getName(), false);
		}
	}
	
	public Location getSpawn(ArenaPlayer ap) 
	{
		Location loc = null;
		try 
		{
			if (getStarttimer() > 0)
			{
				loc = getArenaZone().getLobbyREDspawn().clone();
				if (ap.getTeam() == 2)
					loc = getArenaZone().getLobbyBLUspawn().clone();
			}
			else
			{
				loc = getArenaZone().getTeam1spawn().clone();
				if (ap.getTeam() == 2) 
					loc = getArenaZone().getTeam2spawn().clone();
			}
		}
		catch (Exception e)
		{
			loc = getSpawns().get(Util.random(getSpawns().size())).getLocation().clone().add(0, 2, 0);
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
		getPlugin().debug("Attempting to spawn player: {0}. Already Spawned: {1}", name, alreadyspawned);
		
		if (!isStopped())
		{
			Player p = Util.matchPlayer(name);
			if (p != null) 
			{
				for (ArenaPlayer ap : getArenaplayers())
				{
					if (ap.getPlayer().getName().equals(name))
					{
						if (ap != null && !ap.isOut())
						{
							if (ap.getDeaths() < getMaxDeaths()) 
							{
								Location loc = getSpawn(ap);
								if (loc != null) 
								{
									Location nloc = new Location(loc.getWorld(), loc.getX() + 0.25, loc.getY() + 1.0, loc.getZ() + 0.25);
									teleport(p, nloc);
									
									getPlugin().debug("Spawning player: {0}", name);
									
									// Call spawn event
									ArenaSpawn aSpawn = new ArenaSpawn(nloc.getWorld(), nloc.getBlockX(), nloc.getBlockY(), nloc.getBlockZ());
									UltimateArenaSpawnEvent spawnEvent = new UltimateArenaSpawnEvent(ap, this, aSpawn);
									getPlugin().getServer().getPluginManager().callEvent(spawnEvent);
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
		pl.setAmtkicked(0);
		
		// Call ArenaDeathEvent
		UltimateArenaDeathEvent deathEvent = new UltimateArenaDeathEvent(pl, this);
		getPlugin().getServer().getPluginManager().callEvent(deathEvent);
	}
	
	public void reward(ArenaPlayer ap, Player player, boolean half)
	{
		// Default rewarding sytem
		getPlugin().debug("Rewarding player: {0}. Half: {1}", player.getName(), half);
		
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
		for (ArenaPlayer ap : getArenaplayers())
		{
			if (ap != null && ap.canReward())
			{
				if (ap.getTeam() == team || team == -1)
				{
					Player player = ap.getPlayer();
					if (player != null)
					{
						reward(ap, ap.getPlayer(), half);
						player.sendMessage(string);
					}
				}
			}
		}
	}
	
	public void setWinningTeam(int team)
	{
		// Sets the winning team, -1 for everyone wins
		for (ArenaPlayer ap : getArenaplayers())
		{
			if (ap != null && !ap.isOut())
			{
				ap.setCanReward(false);
				if (ap.getTeam() == team || team == -1)
				{
					ap.setCanReward(true);
				}

			}
		}
		this.winningTeam = team;
	}
	
	public void checkPlayerPoints(int max)
	{
		// Checks to see if any player has the max amount of points needed to win
		for (ArenaPlayer ap : getArenaplayers())
		{
			if (ap != null && !ap.isOut())
			{
				if (ap.getPoints() >= max)
				{
					reward(ap, Util.matchPlayer(ap.getUsername()), false);
					tellPlayers("&7Player &6{0} &7has won!", ap.getUsername());
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
		return (getStarttimer() <= 0 && getAmtPlayersInArena() <= 1);
	}
	
	public void tellPlayers(String string, Object...objects) 
	{
		// Tells ALL players in the arena a message
		for (ArenaPlayer ap : getArenaplayers())
		{
			if (ap != null && !ap.isOut()) 
			{
				Player player = Util.matchPlayer(ap.getPlayer().getName());
				if (player != null && player.isOnline())
				{
					player.sendMessage(FormatUtil.format(string, objects));
				}
			}
		}
	}
	
	public void killAllNear(Location loc, int rad)
	{
		// Kills ALL players in the arena near a point
		for (ArenaPlayer ap : getArenaplayers())
		{
			if (ap != null && !ap.isOut()) 
			{
				Player player = Util.matchPlayer(ap.getPlayer().getName());
				if (player != null && player.isOnline())
				{
					Location ploc = player.getLocation();
					if (Util.pointDistance(loc, ploc) < rad)
						player.setHealth(0.0D);
				}
			}
		}
	}
	
	public void spawnRandom(String name)
	{
		//Spawns a player to a random spawnpoint
		if (getStarttimer() <= 0) 
		{
			Player p = Util.matchPlayer(name);
			if (p != null) 
			{
				ArenaPlayer ap = getPlugin().getArenaPlayer(p);
				if (ap != null && !ap.isOut())
				{
					if (getSpawns().size() > 0) 
						teleport(p, (getSpawns().get(Util.random(getSpawns().size())).getLocation().clone()).add(0, 2, 0));
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
		getPlugin().debug("Doing KillStreak for player: {0}", ap.getUsername());
		
		Player pl = Util.matchPlayer(ap.getPlayer().getName());
		if (pl != null)
		{
			/**Hunger Arena check**/
			if (getPlugin().getArena(pl).getType().equals("Hunger"))
				return;
				
			if (ap.getKillstreak() == 2)
				giveItem(pl, Material.POTION.getId(), (byte)9, 1, "2 kills! Unlocked strength potion!");
				
			if (ap.getKillstreak() == 4)
			{
				giveItem(pl, Material.POTION.getId(), (byte)1, 1, "4 kills! Unlocked Health potion!");
				giveItem(pl, Material.GRILLED_PORK.getId(), (byte)0, 2, "4 kills! Unlocked Food!");
			}
			if (ap.getKillstreak() == 5) 
			{
				if (!(this.getArenaZone().getArenaType().equalsIgnoreCase("cq"))) 
				{
					pl.sendMessage(ChatColor.GOLD + "5 kills! Unlocked Zombies!");
					for (int i = 0; i < 4; i++)
						pl.getLocation().getWorld().spawnEntity(pl.getLocation(), EntityType.ZOMBIE);
				}
			}
			if (ap.getKillstreak() == 8) 
			{
				pl.sendMessage(ChatColor.GOLD + "8 kills! Unlocked attackdogs!");
				for (int i = 0; i < 2; i++)
				{
					Wolf wolf = (Wolf) pl.getLocation().getWorld().spawnEntity(pl.getLocation(), EntityType.WOLF);
					wolf.setOwner(pl);
				}
			}
			if (ap.getKillstreak() == 12)
			{
				giveItem(pl, Material.POTION.getId(), (byte)1, 1, "12 kills! Unlocked Health potion!");
				giveItem(pl, Material.GRILLED_PORK.getId(), (byte)0, 2, "12 kills! Unlocked Food!");
			}
		}
	}
	
	public void onDisable() 
	{
		tellPlayers("&cThis arena has been disabled!");
		this.setGametimer(-1);
		setDisabled(true);
		stop();
	}
	
	public void removePlayer(ArenaPlayer ap) 
	{
		// Obvious?
		ap.setOut(true);
		setUpdatedTeams(true);
	}
	
	public void stop()
	{
		// Ends the arena
		setStopped(true);
		onStop();
		
		getPlugin().outConsole("Stopping arena: {0}!", name);

		for (ArenaPlayer ap : getArenaplayers())
		{
			if (ap != null)
			{
				Player player = Util.matchPlayer(ap.getPlayer().getName());
				if (player != null)
				{
					if (getPlugin().isInArena(player)) 
					{
						if (getGametimer() <= getMaxgametime())
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
				ap.setOut(true);
			}
		}
		
		getPlugin().activeArena.remove(this);
		
		getPlugin().broadcast("&6Arena &b{0} &6has concluded!", name);
	}
	
	public void onStop() {}

	public void normalize(Player p)
	{
		// Removes all armor and inventory
		getPlugin().normalize(p);
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
		getPlugin().debug("Ending Player: {0} Dead: {1}", ap.getUsername(), dead);
		
		Player player = ap.getPlayer();
		if (player != null) 
		{
			teleport(player, ap.getSpawnBack().clone().add(0, 2.0, 0));
			normalize(player);
			returnXP(player);
			ap.returnInventory();
			
			ap.sendMessage("&9Thanks for playing!");
						
			getPlugin().removePotions(player);
		}
		
		// Call Arena leave event
		UltimateArenaLeaveEvent leaveEvent = new UltimateArenaLeaveEvent(ap, this);
		getPlugin().getServer().getPluginManager().callEvent(leaveEvent);

		ap.setOut(true);
		setUpdatedTeams(true);
		
		if (dead) 
		{
			ap.sendMessage("&9You have exceeded the death limit!");
		}
	}

	public void onStart() 
	{
		setAmtPlayersStartingInArena(getArenaplayers().size());
	}
	
	public void onOutOfTime() {}
	
	public void onPreOutOfTime() {}
	
	public void checkTimers() 
	{
		if (isStopped())
		{
			getArenaplayers().clear();
			return;
		}
		
		if (config == null)
		{
			config = getPlugin().getConfig(getType());
			reloadConfig();
		}
		
		if (!isPauseStartTimer())
		{
			setStarttimer(getStarttimer() - 1);
			broadcastTimer--;
		}
		
		if (getStarttimer() <= 0)
		{
			start();
			setGametimer(getGametimer() - 1);
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
		if (getGametimer() <= 0) 
		{
			onPreOutOfTime();
			getPlugin().forceStop(getArenaZone().getArenaName());
			onOutOfTime();
		}
	}
	
	public void start()
	{
		if (start == false) 
		{
			getPlugin().outConsole("Starting arena: {0} Players: {1}", getName(), getAmtPlayersInArena());
			
			this.start = true;
			this.setStartingAmount(this.getAmtPlayersInArena());
			this.setAmtPlayersStartingInArena(this.getStartingAmount());
			this.onStart();
			
			spawnAll();
			setGametimer(getMaxgametime());
			setStarttimer(-1);
		}
	}
	
	public void step() 
	{
		setTeam1size(0);
		team2size = 0;
		checkTimers();
		
		// Get how many people are in the arena
		for (ArenaPlayer ap : getArenaplayers())
		{
			if (ap != null && !ap.isOut())
			{
				Player player = Util.matchPlayer(ap.getPlayer().getName());
				if (player != null)
				{
					if (ap.getTeam() == 1)
						setTeam1size(getTeam1size() + 1);
					else
						team2size++;
				}
			}
		}
		
		check();

		setAmtPlayersInArena(0);

		for (ArenaPlayer ap : getArenaplayers())
		{
			if (ap != null && !ap.isOut())
			{
				Player player = Util.matchPlayer(ap.getPlayer().getName());
				if (player != null && player.isOnline())
				{
					setAmtPlayersInArena(getAmtPlayersInArena() + 1);
							
					// Check players in arena
					if (getStarttimer() > 0) 
					{
						player.setFireTicks(0);
						player.setFoodLevel(20);
						ap.decideHat(player);
					}
					
					ap.setHealtimer(ap.getHealtimer() - 1);
						
					ArenaClass ac = ap.getArenaClass();
					if (ac != null)
					{
						if (ac.getName().equalsIgnoreCase("healer") && ap.getHealtimer() <= 0) 
						{
							if (ap.getPlayer().getHealth() + 1 <= 20)
							{
								if (ap.getPlayer().getHealth() < 0) 
									ap.getPlayer().setHealth(1);
								ap.getPlayer().setHealth(ap.getPlayer().getHealth()+1);
								ap.setHealtimer(2);
							}
						}
								
						
						if (ac.hasPotionEffects())
						{
							if (ac.getPotionEffects().size() > 0)
							{
								for (PotionEffect effect : ac.getPotionEffects())
								{
									if (!ap.getPlayer().hasPotionEffect(effect.getType()))
										player.addPotionEffect(effect);
								}
							}
						}
					}
							
					if (!getPlugin().isInArena(player.getLocation()))
					{
						getPlugin().outConsole("Player {0} got out of the arena! Putting him back in!", ap.getUsername());

						spawn(ap.getPlayer().getName(), false);
						ap.setAmtkicked(ap.getAmtkicked() + 1);
					}
					
					// Timer Stuff
					if (!isPauseStartTimer()) 
					{
						if (getStarttimer() == 120) 
						{
							ap.sendMessage("&6120 &7seconds until start!");
						}
						if (getStarttimer() == 60)
						{
							ap.sendMessage("&660 &7seconds until start!");
						}
						if (getStarttimer() == 45)
						{
							ap.sendMessage("&645 &7seconds until start!");
						}
						if (getStarttimer() == 30) 
						{
							ap.sendMessage("&630 &7seconds until start!");
						}
						if (getStarttimer() == 15)
						{
							ap.sendMessage("&615 &7seconds until start!");
						}
						if (getStarttimer() > 0 && getStarttimer() < 11) 
						{
							ap.sendMessage("&6{0} &7second(s) until start!", getStarttimer());
						}
					}
							
					if (getGametimer() > 0 && getGametimer() < 21)
					{
						ap.sendMessage("&6{0} &7second(s) until end!", getGametimer());
					}
					if (getGametimer() == 60 && getMaxgametime() > 60)
					{
						ap.sendMessage("&6{0} &7minute(s) until end!", (getGametimer() - 60) / 60);
					}
					if (getGametimer() == getMaxgametime()/2) 
					{
						ap.sendMessage("&6{0} &7second(s) until end!", getMaxgametime() / 2);
					}
							
					// TP players back when dead
					if (!isStopped()) 
					{
						if (ap.getDeaths() >= getMaxDeaths()) 
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
		
		if (this.getAmtPlayersInArena() == 0)
			getPlugin().forceStop(getArenaZone().getArenaName());
	}
	
	// Return a player's xp after leaving an arena
	public void returnXP(Player player)
	{
		getPlugin().debug("Returning XP for player: {0}", player.getName());
		
		ArenaPlayer ap = getPlugin().getArenaPlayer(player);
		if (ap != null)
			player.setLevel(ap.getBaselevel());
	}
	
	// Save players on shutdown
	public void onShutdown()
	{	
		for (ArenaPlayer pl : getArenaplayers())
		{
			SavedArenaPlayer playerToSave = new SavedArenaPlayer(pl.getPlayer().getName(), pl.getBaselevel(), pl.getSpawnBack(), pl.getSavedInventory(), pl.getSavedArmor());
			getPlugin().getFileHelper().savePlayer(playerToSave);
		}
		
		stop();
	}

	public String getName()
	{
		return name;
	}

	public int getStartingAmount() 
	{
		return startingAmount;
	}

	public void setStartingAmount(int startingAmount) 
	{
		this.startingAmount = startingAmount;
	}

	public boolean isForceStop()
	{
		return forceStop;
	}

	public void setForceStop(boolean forceStop) 
	{
		this.forceStop = forceStop;
	}

	public List<ArenaPlayer> getArenaplayers() 
	{
		return arenaplayers;
	}
	
	public int getStarttimer() 
	{
		return starttimer;
	}

	public void setStarttimer(int starttimer) 
	{
		this.starttimer = starttimer;
	}

	public ArenaZone getArenaZone() 
	{
		return az;
	}

	public int getAmtPlayersInArena()
	{
		return amtPlayersInArena;
	}

	public void setAmtPlayersInArena(int amtPlayersInArena) 
	{
		this.amtPlayersInArena = amtPlayersInArena;
	}

	public boolean isDisabled()
	{
		return disabled;
	}

	public void setDisabled(boolean disabled)
	{
		this.disabled = disabled;
	}

	public String getType() 
	{
		return type;
	}
	
	public int getGametimer()
	{
		return gametimer;
	}

	public void setGametimer(int gametimer) 
	{
		this.gametimer = gametimer;
	}

	public int getMaxgametime() 
	{
		return maxgametime;
	}

	public void setMaxgametime(int maxgametime) 
	{
		this.maxgametime = maxgametime;
	}

	public int getMaxDeaths()
	{
		return maxDeaths;
	}

	public void setMaxDeaths(int maxDeaths) 
	{
		this.maxDeaths = maxDeaths;
	}

	public UltimateArena getPlugin()
	{
		return plugin;
	}

	public boolean isUpdatedTeams() 
	{
		return updatedTeams;
	}

	public void setUpdatedTeams(boolean updatedTeams)
	{
		this.updatedTeams = updatedTeams;
	}

	public List<ArenaFlag> getFlags() 
	{
		return flags;
	}

	public void setFlags(List<ArenaFlag> flags)
	{
		this.flags = flags;
	}

	public int getAmtPlayersStartingInArena()
	{
		return amtPlayersStartingInArena;
	}

	public void setAmtPlayersStartingInArena(int amtPlayersStartingInArena) 
	{
		this.amtPlayersStartingInArena = amtPlayersStartingInArena;
	}

	public int getWinningTeam() 
	{
		return winningTeam;
	}

	public boolean isStopped()
	{
		return stopped;
	}

	public void setStopped(boolean stopped)
	{
		this.stopped = stopped;
	}

	public boolean isAllowTeamKilling()
	{
		return allowTeamKilling;
	}

	public void setAllowTeamKilling(boolean allowTeamKilling) 
	{
		this.allowTeamKilling = allowTeamKilling;
	}

	public List<ArenaSpawn> getSpawns() 
	{
		return spawns;
	}

	public void setSpawns(List<ArenaSpawn> spawns)
	{
		this.spawns = spawns;
	}

	public World getWorld() 
	{
		return world;
	}
	
	public int getWave() 
	{
		return wave;
	}

	public void setWave(int wave)
	{
		this.wave = wave;
	}

	public int getMaxwave() 
	{
		return maxwave;
	}

	public void setMaxwave(int maxwave)
	{
		this.maxwave = maxwave;
	}

	public int getTeam1size() 
	{
		return team1size;
	}

	public void setTeam1size(int team1size) 
	{
		this.team1size = team1size;
	}

	public boolean isPauseStartTimer()
	{
		return pauseStartTimer;
	}

	public void setPauseStartTimer(boolean pauseStartTimer) 
	{
		this.pauseStartTimer = pauseStartTimer;
	}
	
	public void setType(String type)
	{
		this.type = type;
	}
}