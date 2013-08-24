package net.dmulloy2.ultimatearena.arenas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaClass;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaConfig;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaFlag;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaPlayer;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaSpawn;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaZone;
import net.dmulloy2.ultimatearena.arenas.objects.FieldType;
import net.dmulloy2.ultimatearena.arenas.objects.PotionType;
import net.dmulloy2.ultimatearena.events.UltimateArenaDeathEvent;
import net.dmulloy2.ultimatearena.events.UltimateArenaJoinEvent;
import net.dmulloy2.ultimatearena.events.UltimateArenaLeaveEvent;
import net.dmulloy2.ultimatearena.events.UltimateArenaSpawnEvent;
import net.dmulloy2.ultimatearena.permissions.Permission;
import net.dmulloy2.ultimatearena.util.FormatUtil;
import net.dmulloy2.ultimatearena.util.InventoryHelper;
import net.dmulloy2.ultimatearena.util.Util;
import net.ess3.api.IEssentials;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;

import com.earth2me.essentials.User;

/**
 * Base Data Container for an arena.
 * This can be extended for specific arena types.
 * @author dmulloy2
 */

public abstract class Arena 
{
	public static enum Mode
	{
		LOBBY, INGAME, STOPPING, IDLE, DISABLED;
	}
	
	protected List<ArenaPlayer> arenaPlayers = new ArrayList<ArenaPlayer>();
	protected List<ArenaSpawn> spawns = new ArrayList<ArenaSpawn>();
	protected List<ArenaFlag> flags = new ArrayList<ArenaFlag>();

	protected int startingAmount = 0;
	protected int broadcastTimer = 45;
	protected int winningTeam = 999;
	protected int announced = 0;
	protected int maxDeaths = 1;
	protected int maxWave = 15;
	protected int wave = 0;

	protected int maxGameTime;
	protected int startTimer;
	protected int gameTimer;
	protected int team1size;
	protected int team2size;
	
	protected boolean allowTeamKilling = false;
	protected boolean pauseStartTimer = false;
	protected boolean forceStop = false;
	protected boolean stopped = false;
	protected boolean start = false;

	protected boolean updatedTeams;
	protected boolean disabled;
	
	protected Mode gameMode = Mode.DISABLED;

	protected World world;

	protected FieldType type;
	protected String name;

	protected final UltimateArena plugin;
	protected ArenaConfig config;
	protected ArenaZone az;
	
	/**
	 * Creates a new Arena based around an {@link ArenaZone}
	 * @param az - {@link ArenaZone} to base the {@link Arena} around
	 */
	public Arena(ArenaZone az) 
	{
		this.az = az;
		this.plugin = az.getPlugin();
		this.name = az.getArenaName();
		this.world = az.getWorld();
		this.az.setTimesPlayed(az.getTimesPlayed() + 1);
		this.plugin.arenasPlayed++;
		
		if (maxDeaths < 1)
		{
			this.maxDeaths = 1;
		}
		
		this.gameMode = Mode.LOBBY;
		
		updateSigns();
	}
	
	/**
	 * Reloads the arena's configuration
	 */
	public void reloadConfig() 
	{
		if (config != null) 
		{
			this.maxGameTime = config.getGameTime();
			this.gameTimer = config.getGameTime();
			this.startTimer = config.getLobbyTime();
			this.maxDeaths = config.getMaxDeaths();
			this.allowTeamKilling = config.isAllowTeamKilling();
			this.maxWave = config.getMaxWave();
			
			if (maxDeaths < 1)
			{
				this.maxDeaths = 1;
			}
		}
	}
	
	/**
	 * Adds a player to an {@link Arena}.
	 * Should not be overriden.
	 * @param player - {@link Player} to add to an arena
	 */
	public void addPlayer(Player player)
	{
		player.sendMessage(plugin.getPrefix() + FormatUtil.format("&6Joining arena &b{0}&6... Please wait!", name));
		
		ArenaPlayer pl = new ArenaPlayer(player, this, plugin);

		// Update Teams
		pl.setTeam(getTeam());
		this.updatedTeams = true;
		
		// Teleport the player to the lobby spawn
		spawn(player.getName(), false);
		
		// Save and clear Inventory
		if (plugin.getConfig().getBoolean("saveInventories", true))
		{
			plugin.debug("Saving Inventory for Player: {0}", player.getName());
			
			pl.saveInventory();
			pl.clearInventory();
		}
		
		pl.setBaseLevel(player.getLevel());

		// Make sure the player is in survival
		player.setGameMode(GameMode.SURVIVAL);
		
		// Heal up the Player
		player.setFoodLevel(20);
		player.setFireTicks(0);
		player.setHealth(20);
		
		// Don't allow flight
		player.setAllowFlight(false);
		player.setFlySpeed(0.1F);
		player.setFlying(false);
		
		// If essentials is found, remove god mode.
		PluginManager pm = plugin.getServer().getPluginManager();
		if (pm.isPluginEnabled("Essentials"))
		{
			Plugin essPlugin = pm.getPlugin("Essentials");
			IEssentials ess = (IEssentials) essPlugin;
			User user = ess.getUser(player);
			
			// Disable GodMode in the arena
			if (user.isGodModeEnabled())
				user.setGodModeEnabled(false);
		}
		
		// Clear potion effects
		pl.clearPotionEffects();
		
		// Finally add the player
		arenaPlayers.add(pl);
		
		// Update Signs
		updateSigns();
		
		// Call ArenaJoinEvent
		UltimateArenaJoinEvent joinEvent = new UltimateArenaJoinEvent(pl, this);
		plugin.getServer().getPluginManager().callEvent(joinEvent);
		
		tellPlayers("&a{0} has joined the arena! ({1}/{2})", pl.getName(), getActivePlayers(), az.getMaxPlayers());
	}
	
	/**
	 * Gets the base team.
	 * Can be overriden in certain cases.
	 * @return Base team
	 */
	public int getTeam() 
	{
		return 1;
	}
	
	/**
	 * Announces the arena's existance and reminds players to join.
	 */
	public void announce() 
	{
		for (Player player : plugin.getServer().getOnlinePlayers())
		{
			if (! plugin.isInArena(player))
			{
				if (plugin.getPermissionHandler().hasPermission(player, Permission.JOIN))
				{
					if (announced == 0) 
					{
						player.sendMessage(plugin.getPrefix() + FormatUtil.format("&b{0} &6arena has been created!", getType().getName()));
					}
					else
					{
						player.sendMessage(plugin.getPrefix() + FormatUtil.format("&6Hurry up and join the &b{0} &6arena!", getType().getName()));
					}
					
					player.sendMessage(plugin.getPrefix() + FormatUtil.format("&6Type &b/ua join {0} &6to join!", getArenaZone().getArenaName()));
				}
			}
		}

		announced++;
	}
	
	/**
	 * Returns the team a player should be on.
	 * @return The team the player should be on
	 */
	public int getBalancedTeam()
	{
		int amt1 = 0;
		int amt2 = 0;
		
		for (int i = 0; i < arenaPlayers.size(); i++)
		{
			ArenaPlayer ap = arenaPlayers.get(i);
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
	
	/**
	 * A simple team check.
	 * @param stopifEmpty - Stops the arena if empty
	 */
	public boolean simpleTeamCheck(boolean stopifEmpty) 
	{
		if (getTeam1size() == 0 || team2size == 0) 
		{
			if (stopifEmpty)
			{
				stop();
			}
			
			if (startingAmount > 1)
			{
				return false;
			}
			
			return true;
		}
		
		return true;
	}
	
	/**
	 * Gets the player's arena player instance.
	 * @param p - Player instance
	 * @return The player's ArenaPlayer instance
	 */
	public ArenaPlayer getArenaPlayer(Player p) 
	{
		if (p != null) 
		{
			for (ArenaPlayer ap : arenaPlayers)
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
	
	/**
	 * Spawns all players in an arena.
	 */
	public void spawnAll() 
	{
		plugin.outConsole("Spawning players for Arena: {0}", getArenaZone().getArenaName());
		
		for (int i = 0; i < arenaPlayers.size(); i++)
		{
			ArenaPlayer ap = arenaPlayers.get(i);
			if (ap != null && !ap.isOut())
				spawn(ap.getPlayer().getName(), false);
		}
	}
	
	/**
	 * Gets the spawn for an {@link ArenaPlayer}.
	 * Will return null if used by another {@link Arena}.
	 * @param ap - {@link ArenaPlayer} instance
	 * @return the {@link ArenaPlayer}'s spawn
	 */
	public Location getSpawn(ArenaPlayer ap) 
	{
		Location loc = null;
		if (isInLobby())
		{
			loc = az.getLobbyREDspawn();
			if (ap.getTeam() == 2)
				loc = az.getLobbyBLUspawn();
		}
		else
		{
			loc = getArenaZone().getTeam1spawn();
			if (ap.getTeam() == 2) 
				loc = az.getTeam2spawn();
		}

		return loc;
	}
	
	/**
	 * Spawns a player in an {@link Arena}.
	 * This should not be overriden.
	 * @param name - Player to spawn
	 * @param alreadyspawned - Have they already been spawned?
	 */
	public void spawn(String name, boolean alreadyspawned)
	{
		plugin.debug("Attempting to spawn player: {0}. Already Spawned: {1}", name, alreadyspawned);
		
		if (! stopped)
		{
			Player p = Util.matchPlayer(name);
			if (p != null) 
			{
				for (int i = 0; i < arenaPlayers.size(); i++)
				{
					ArenaPlayer ap = arenaPlayers.get(i);
					if (ap.getName().equals(name))
					{
						if (ap != null && ! ap.isOut())
						{
							if (ap.getDeaths() < getMaxDeaths()) 
							{
								plugin.debug("Spawning player: {0}", name);
								
								Location loc = getSpawn(ap);
								if (loc != null) 
								{
									teleport(p, loc);
									
									// Call spawn event
									ArenaSpawn spawn = new ArenaSpawn(loc);
									UltimateArenaSpawnEvent spawnEvent = new UltimateArenaSpawnEvent(ap, this, spawn);
									plugin.getServer().getPluginManager().callEvent(spawnEvent);
								}
								
								ap.spawn();
								
								if (! alreadyspawned)
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

	/**
	 * Called when a player is spawned.
	 * @param apl {@link ArenaPlayer} who was spawned
	 */
	public void onSpawn(ArenaPlayer apl) {}
	
	/**
	 * Called when a player dies.
	 * @param pl - {@link ArenaPlayer} who died
	 */
	public void onPlayerDeath(ArenaPlayer pl) 
	{
		pl.setAmtkicked(0);
		
		// Call ArenaDeathEvent
		UltimateArenaDeathEvent deathEvent = new UltimateArenaDeathEvent(pl, this);
		plugin.getServer().getPluginManager().callEvent(deathEvent);
	}
	
	/**
	 * Default rewarding system.
	 * May be overriden in some cases.
	 * @param ap - {@link ArenaPlayer} to reward
	 * @param player - {@link Player} to reward
	 * @param half - Whether or not to reward half
	 */
	public void reward(ArenaPlayer ap, Player player, boolean half)
	{
		plugin.debug("Rewarding player: {0}. Half: {1}", player.getName(), half);
		
		if (config != null) 
		{
			config.giveRewards(player, half);
		}
		else
		{
			InventoryHelper.addItem(player, new ItemStack(Material.GOLD_INGOT));
		}
	}
	
	/**
	 * Rewards an entire team.
	 * @param team - Team to reward
	 * @param string - Reward message
	 * @param half - Whether or not to reward half
	 */
	public void rewardTeam(int team, boolean half)
	{
		plugin.debug("Rewarding team {0}. Half: {1}", team, half);
		
		for (int i = 0; i < arenaPlayers.size(); i++)
		{
			ArenaPlayer ap = arenaPlayers.get(i);
			if (ap != null && ap.canReward())
			{
				if (ap.getTeam() == team || team == -1)
				{
					Player player = ap.getPlayer();
					if (player != null)
					{
						reward(ap, ap.getPlayer(), half);
					}
				}
			}
		}
	}
	
	/**
	 * Sets the winning team.
	 * @param team - Winning team
	 */
	public void setWinningTeam(int team)
	{
		for (int i = 0; i < arenaPlayers.size(); i++)
		{
			ArenaPlayer ap = arenaPlayers.get(i);
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
	
	/**
	 * Checks if a player has enough points to win.
	 * @param max - Max points for an arena
	 */
	public void checkPlayerPoints(int max)
	{
		for (int i = 0; i < arenaPlayers.size(); i++)
		{
			ArenaPlayer ap = arenaPlayers.get(i);
			if (ap != null && !ap.isOut())
			{
				if (ap.getPoints() >= max)
				{
					tellPlayers("&7Player &6{0} &7has won!", ap.getName());
					
					stop();
					
					reward(ap, ap.getPlayer(), false);
				}
			}
		}
	}
	
	/**
	 * Stops the arena if empty.
	 * @return Arena is empty
	 */
	public boolean checkEmpty() 
	{
		boolean ret = isEmpty();
		if (ret) stop();
		
		return ret;
	}
	
	/**
	 * Checks if the arena is empty.
	 * @return Arena is empty
	 */
	public boolean isEmpty()
	{
		return (isInGame() && getActivePlayers() <= 1);
	}
	
	/**
	 * Tells all players in the arena a message.
	 * @param string - Base message
	 * @param objects - Objects to format in
	 */
	public void tellPlayers(String string, Object...objects) 
	{
		for (int i = 0; i < arenaPlayers.size(); i++)
		{
			ArenaPlayer ap = arenaPlayers.get(i);
			if (ap != null && !ap.isOut()) 
			{
				Player player = Util.matchPlayer(ap.getPlayer().getName());
				if (player != null && player.isOnline())
				{
					player.sendMessage(plugin.getPrefix() + FormatUtil.format(string, objects));
				}
			}
		}
	}
	
	/**
	 * Kills all players within a certain radius of a {@link Location}
	 * @param loc - Center {@link Location}
	 * @param rad - Radius to kill within
	 */
	public void killAllNear(Location loc, int rad)
	{
		plugin.debug("Killing all players near {0} in a radius of {1}", Util.locationToString(loc), rad);
		
		for (int i = 0; i < arenaPlayers.size(); i++)
		{
			ArenaPlayer ap = arenaPlayers.get(i);
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

	/**
	 * Gets a random spawn for an {@link ArenaPlayer}.
	 * @param ap - {@link ArenaPlayer} to get spawn for
	 * @return Spawn for the {@link ArenaPlayer}
	 */
	public Location getRandomSpawn(ArenaPlayer ap)
	{
		plugin.debug("Getting a random spawn for {0}", ap.getName());
		
		if (ap != null && !ap.isOut())
		{
			if (! spawns.isEmpty())
			{
				int rand = Util.random(spawns.size());
				ArenaSpawn spawn = spawns.get(rand);
				if (spawn != null)
				{
					return spawn.getLocation();
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Gives a {@link Player} an item
	 * @param pl - {@link Player} to give items to
	 * @param id - ItemId of the item to give
	 * @param dat - Data value of the item
	 * @param amt - Amount of the item to give
	 * @param message - Message to send the {@link Player}
	 */
	public void giveItem(Player pl, int id, byte dat, int amt, String message)
	{
		if (message != "")
			pl.sendMessage(plugin.getPrefix() + message);
		
		ItemStack item = new ItemStack(id, amt);
		if (dat != 0)
		{
			MaterialData data = new MaterialData(id);
			data.setData(dat);
			item.setData(data);
		}

		InventoryHelper.addItem(pl, item);
	}
	
	/**
	 * Gives a player a {@link Potion}
	 * @param pl - {@link Player} to give the {@link Potion}
	 * @param s - Name of the {@link Potion} to give. Must be a valid {@link PotionType}
	 * @param amt - Amount of the {@link Potion} to give
	 * @param level - Level of the {@link Potion} to give
	 * @param splash - Whether or not it is a splash {@link Potion}
	 * @param message - Message to send to the {@link Player}
	 */
	public void givePotion(Player pl, String s, int amt, int level, boolean splash, String message)
	{
		if (message != "")
			pl.sendMessage(plugin.getPrefix() + message);
		
		org.bukkit.potion.PotionType type = PotionType.toType(s);
		if (type != null)
		{
			Potion potion = new Potion(1);
			potion.setType(type);
			potion.setLevel(level);
			potion.setSplash(splash);
			
			InventoryHelper.addItem(pl, potion.toItemStack(amt));
		}
	}
	
	/**
	 * Basic killstreak system.
	 * Can be overriden.
	 * @param ap - {@link ArenaPlayer} to do killstreak for
	 */
	public void doKillStreak(ArenaPlayer ap) 
	{
		plugin.debug("Doing KillStreak for player: {0}", ap.getName());
		
		Player pl = Util.matchPlayer(ap.getPlayer().getName());
		if (pl != null)
		{
			/**Hunger Arena check**/
			if (plugin.getArena(pl).getType().equals("Hunger"))
				return;
				
			if (ap.getKillstreak() == 2)
				givePotion(pl, "strength", 1, 1, false, "2 kills! Unlocked strength potion!");
				
			if (ap.getKillstreak() == 4)
			{
				givePotion(pl, "heal", 1, 1, false, "4 kills! Unlocked health potion!");
				giveItem(pl, Material.GRILLED_PORK.getId(), (byte)0, 2, "4 kills! Unlocked Food!");
			}
			if (ap.getKillstreak() == 5) 
			{
				if (!(getType().getName().equalsIgnoreCase("cq"))) 
				{
					pl.sendMessage(plugin.getPrefix() + "5 kills! Unlocked Zombies!");
					for (int i = 0; i < 4; i++)
						pl.getLocation().getWorld().spawnEntity(pl.getLocation(), EntityType.ZOMBIE);
				}
			}
			if (ap.getKillstreak() == 8) 
			{
				pl.sendMessage(plugin.getPrefix() + "8 kills! Unlocked attackdogs!");
				for (int i = 0; i < 2; i++)
				{
					Wolf wolf = (Wolf) pl.getLocation().getWorld().spawnEntity(pl.getLocation(), EntityType.WOLF);
					wolf.setOwner(pl);
				}
			}
			if (ap.getKillstreak() == 12)
			{
				givePotion(pl, "regen", 1, 1, false, "12 kills! Unlocked regen potion!");
				giveItem(pl, Material.GRILLED_PORK.getId(), (byte)0, 2, "12 kills! Unlocked Food!");
			}
		}
	}
	
	/**
	 * Disables this arena
	 */
	public void onDisable() 
	{
		tellPlayers("&cThis arena has been disabled!");
		
		this.gameTimer = -1;
		
		stop();
		
		this.disabled = true;
		this.gameMode = Mode.DISABLED;
		
		updateSigns();
	}

	/**
	 * Ends the arena
	 */
	public void stop()
	{
		if (stopped) return; // No need to stop multiple times
		
		plugin.outConsole("Stopping arena: {0}!", name);
		
		this.gameMode = Mode.STOPPING;
		this.stopped = true;
		
		updateSigns();
		
		onStop();
		
		announceWinner();

		for (int i = 0; i < arenaPlayers.size(); i++)
		{
			ArenaPlayer ap = arenaPlayers.get(i);
			if (ap != null)
			{
				Player player = ap.getPlayer();
				if (player != null)
				{
					if (plugin.isInArena(player)) 
					{
						if (gameTimer <= maxGameTime)
						{
							ap.sendMessage("&9Game inturrupted/ended!");
						}
						else
						{
							ap.sendMessage("&9Game Over!");
						}
						
						endPlayer(ap, player, false);
					}
				}
			}
		}
		
		this.gameMode = Mode.IDLE;
		
		updateSigns();
		
		plugin.activeArena.remove(this);

		plugin.broadcast("&6Arena &b{0} &6has concluded!", name);
	}
	
	/**
	 * Called when an arena is stopped
	 */
	public void onStop() {}

	/**
	 * Removes all inventory items from a playr
	 * @param p - {@link Player} to remove inventory items from
	 */
	public void normalize(Player p)
	{
		plugin.normalize(p);
	}
	
	/**
	 * Teleports a player to the most ideal location
	 * @param p - Player to teleport
	 * @param loc - Raw location
	 */
	public void teleport(Player p, Location loc) 
	{
		p.teleport(loc.clone().add(0.5D, 1.0D, 0.5D));
	}
	
	/**
	 * Called when an arena is updated
	 */
	public void check() {}

	/**
	 * Ends an {@link ArenaPlayer}
	 * @param ap - {@link ArenaPlayer} to end
	 * @param dead - Whether or not a player died
	 */
	public void endPlayer(ArenaPlayer ap, Player player, boolean dead) 
	{
		plugin.debug("Ending Player: {0} Dead: {1}", ap.getName(), dead);
		
		ap.setOut(true);
		
		this.updatedTeams = true;
		
		returnXP(ap);
		
		ap.clearInventory();
		ap.returnInventory();

		plugin.removePotions(player);
			
		teleport(player, ap.getSpawnBack());

		// Call Arena leave event
		UltimateArenaLeaveEvent leaveEvent = new UltimateArenaLeaveEvent(ap, this);
		plugin.getServer().getPluginManager().callEvent(leaveEvent);

		updateSigns();
		
		if (dead) 
		{
			ap.sendMessage("&3You have exceeded the death limit!");
			tellPlayers("&e{0} &3has been eliminated!", ap.getName());
			
			if (getActivePlayers() > 1)
			{
				tellPlayers("&3There are &e{0} &3players remaining!", getActivePlayers());
			}
		}
	}

	/** 
	 * Called when an arena starts
	 */
	public void onStart()
	{
		this.startingAmount = getActivePlayers();
	}
	
	/**
	 * Called when an arena runs out of time
	 */
	public void onOutOfTime() {}
	
	/**
	 * Called right before an arena runs out of time
	 */
	public void onPreOutOfTime() {}
	
	/**
	 * Checks timers
	 */
	public void checkTimers() 
	{
		if (stopped)
		{
			arenaPlayers.clear();
			return;
		}
		
		if (config == null)
		{
			config = plugin.getConfig(type.getName());
			reloadConfig();
		}
		
		if (! pauseStartTimer)
		{
			startTimer--;
			broadcastTimer--;
		}
		
		if (startTimer <= 0)
		{
			start();
			gameTimer--;
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
		if (gameTimer <= 0) 
		{
			onPreOutOfTime();
			stop();
			onOutOfTime();
		}
	}
	
	/**
	 * Starts the arena.
	 * Should not be overriden.
	 */
	public void start()
	{
		if (! start) 
		{
			plugin.outConsole("Starting arena: {0} Players: {1}", getName(), getActivePlayers());
			
			this.start = true;
			this.gameMode = Mode.INGAME;
			
			this.startingAmount = getActivePlayers();
			
			this.gameTimer = maxGameTime;
			this.startTimer = -1;
			
			updateSigns();
			
			onStart();
			spawnAll();
		}
	}
	
	/**
	 * Arena Updater
	 */
	public void update()
	{
		this.team1size = 0;
		this.team2size = 0;
		
		checkTimers();
		
		// Get how many people are in the arena
		for (int i = 0; i < arenaPlayers.size(); i++)
		{
			ArenaPlayer ap = arenaPlayers.get(i);
			if (ap != null && !ap.isOut())
			{
				Player player = Util.matchPlayer(ap.getPlayer().getName());
				if (player != null)
				{
					if (ap.getTeam() == 1)
						team1size++;
					else
						team2size++;
				}
			}
		}
		
		check();

		for (int i = 0; i < arenaPlayers.size(); i++)
		{
			ArenaPlayer ap = arenaPlayers.get(i);
			if (ap != null && !ap.isOut())
			{
				Player player = ap.getPlayer();
				if (player != null)
				{
					// Check players in the Arena
					if (isInLobby()) 
					{
						player.setFireTicks(0);
						player.setFoodLevel(20);
						ap.decideHat();
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
								
						// Class based potion effects
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
							
					// Make sure they are still in the Arena
					if (!plugin.isInArena(player.getLocation()))
					{
						plugin.debug("Player {0} got out of the arena! Putting him back in!", ap.getName());

						spawn(ap.getPlayer().getName(), false);
						ap.setAmtkicked(ap.getAmtkicked() + 1);
					}
					
					// Timer Stuff
					if (!isPauseStartTimer()) 
					{
						if (startTimer == 120) 
						{
							ap.sendMessage("&6120 &7seconds until start!");
						}
						if (startTimer == 60)
						{
							ap.sendMessage("&660 &7seconds until start!");
						}
						if (startTimer == 45)
						{
							ap.sendMessage("&645 &7seconds until start!");
						}
						if (startTimer == 30) 
						{
							ap.sendMessage("&630 &7seconds until start!");
						}
						if (startTimer == 15)
						{
							ap.sendMessage("&615 &7seconds until start!");
						}
						if (startTimer > 0 && startTimer < 11) 
						{
							ap.sendMessage("&6{0} &7second(s) until start!", startTimer);
						}
					}
							
					if (gameTimer > 0 && gameTimer < 21)
					{
						ap.sendMessage("&6{0} &7second(s) until end!", gameTimer);
					}
					if (gameTimer == 60 && maxGameTime > 60)
					{
						ap.sendMessage("&6{0} &7minute(s) until end!", gameTimer / 60);
					}
					if (gameTimer == maxGameTime/2) 
					{
						ap.sendMessage("&6{0} &7second(s) until end!", maxGameTime / 2);
					}
					
					// XP Bar
					decideXPBar(ap);
							
					// End dead players
					if (! stopped) 
					{
						if (ap.getDeaths() >= getMaxDeaths()) 
						{
							if (player != null) 
							{
								if (player.getHealth() > 0) 
								{
									endPlayer(ap, player, true);
								}
							}
						}
					}
				}
			}
		}
		
		// Stop the arena if there are no players
		if (getActivePlayers() == 0)
			stop();
	}
	
	/**
	 * Decides the timer xp bar for an {@link ArenaPlayer}
	 * @param ap - {@link ArenaPlayer} to decide xp bar for
	 */
	public void decideXPBar(ArenaPlayer ap)
	{
		if (ap != null && ! ap.isOut())
		{
			if (plugin.getConfig().getBoolean("timerXPBar", false))
			{
				if (isInGame())
				{
					ap.getPlayer().setLevel(gameTimer);
				}
					
				if (isInLobby())
				{
					ap.getPlayer().setLevel(startTimer);
				}
			}
		}
	}
	
	/**
	 * Returns a player's xp when they leave the game
	 * @param ap - {@link ArenaPlayer} to return xp
	 */
	public void returnXP(ArenaPlayer ap)
	{
		if (ap != null)
		{
			Player player = ap.getPlayer();
			
			plugin.debug("Returning XP for player: {0}. Levels: {1}", player.getName(), ap.getBaselevel());

			// Clear XP
			player.setExp(0);
			player.setLevel(0);
		
			// Give Base XP
			player.setLevel(ap.getBaselevel());
		}
	}

	/**
	 * Forces the start of an arena
	 * @param player - {@link Player} forcing the start of the arena
	 */
	public void forceStart(Player player)
	{
		if (isInGame())
		{
			player.sendMessage(plugin.getPrefix() + FormatUtil.format("&cThis arena is already in progress!"));
			return;
		}
		
		plugin.outConsole("Forcefully starting arena: {0}!", name);

		start();
		
		gameTimer--;
		
		player.sendMessage(plugin.getPrefix() + FormatUtil.format("&6You have forcefully started &b{0}&6!", name));
	}

	// TODO: Explanations for the rest of the methods
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

	public List<ArenaPlayer> getArenaPlayers() 
	{
		return arenaPlayers;
	}
	
	public int getStartTimer()
	{
		return startTimer;
	}

	public ArenaZone getArenaZone() 
	{
		return az;
	}

	public int getActivePlayers()
	{
		int amt = 0;
		for (ArenaPlayer ap : arenaPlayers)
		{
			if (ap != null && ! ap.isOut())
				amt++;
		}
		
		return amt;
	}
	
	public boolean isDisabled()
	{
		return disabled;
	}

	public void setDisabled(boolean disabled)
	{
		this.disabled = disabled;
	}

	public FieldType getType() 
	{
		return type;
	}
	
	public int getGameTimer()
	{
		return gameTimer;
	}
	
	public int getMaxGameTime()
	{
		return maxGameTime;
	}

	public int getMaxDeaths()
	{
		return maxDeaths;
	}

	public void setMaxDeaths(int maxDeaths) 
	{
		this.maxDeaths = maxDeaths;
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

	public int getMaxWave()
	{
		return maxWave;
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
	
	public void setType(FieldType type)
	{
		this.type = type;
	}
	
	public boolean isInGame()
	{
		return (startTimer < 1 && gameTimer > 0);
	}
	
	public boolean isInLobby()
	{
		return (startTimer > 1);
	}
	
	public List<String> buildLeaderboard(Player player)
	{
		List<String> leaderboard = new ArrayList<String>();
		
		// Build kills map
		HashMap<String, Double> kdrMap = new HashMap<String, Double>();
		for (int i = 0; i < arenaPlayers.size(); i++)
		{
			ArenaPlayer ap = arenaPlayers.get(i);
			if (ap != null && ! ap.isOut())
			{
				kdrMap.put(ap.getName(), ap.getKDR());
			}
		}
		
		final List<Map.Entry<String, Double>> sortedEntries = new ArrayList<Map.Entry<String, Double>>(kdrMap.entrySet());
		Collections.sort(
		sortedEntries, new Comparator<Map.Entry<String, Double>>()
		{
			@Override
			public int compare(final Entry<String, Double> entry1, final Entry<String, Double> entry2)
			{
				return -entry1.getValue().compareTo(entry2.getValue());
			}
		});
		
		int pos = 1;
		for (Map.Entry<String, Double> entry : sortedEntries)
		{
			String string = entry.getKey();
			ArenaPlayer apl = plugin.getArenaPlayer(Util.matchPlayer(string));
			if (apl != null)
			{
				StringBuilder line = new StringBuilder();
				line.append(FormatUtil.format("&6#{0}. ", pos));
				line.append(FormatUtil.format(decideColor(apl)));
				line.append(FormatUtil.format(apl.getName().equals(player.getName()) ? "&l" : ""));
				line.append(FormatUtil.format(apl.getName() + "&r"));
				line.append(FormatUtil.format("  &7Kills: &6{0}", apl.getKills()));
				line.append(FormatUtil.format("  &7Deaths: &6{0}", apl.getDeaths()));
				line.append(FormatUtil.format("  &7KDR: &6{0}", entry.getValue()));
				leaderboard.add(line.toString());
				pos++;
			}
		}
		
		return leaderboard;
	}
	
	protected String decideColor(ArenaPlayer pl)
	{
		if (pl.getTeam() == 1)
		{
			return "&c";
		}
		else if (pl.getTeam() == 2)
		{
			return "&9";
		}
		else
		{
			return "&d";
		}
	}
	
	public Mode getGameMode()
	{
		return gameMode;
	}
	
	public void updateSigns()
	{
		plugin.getSignManager().updateSigns();
	}
	
	public void announceWinner()
	{
		if (winningTeam == 2)
		{
			tellPlayers("&9Blue team won!");
		}
		else if (winningTeam == 1)
		{
			tellPlayers("&9Red team won!");
		}
		else if (winningTeam == -1)
		{
			tellPlayers("&9Game ended in a tie!");
		}
		// else nobody won
	}
}