package net.dmulloy2.ultimatearena.arenas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.events.UltimateArenaDeathEvent;
import net.dmulloy2.ultimatearena.events.UltimateArenaJoinEvent;
import net.dmulloy2.ultimatearena.events.UltimateArenaLeaveEvent;
import net.dmulloy2.ultimatearena.events.UltimateArenaSpawnEvent;
import net.dmulloy2.ultimatearena.flags.ArenaFlag;
import net.dmulloy2.ultimatearena.types.ArenaClass;
import net.dmulloy2.ultimatearena.types.ArenaConfig;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.ArenaSpawn;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.FieldType;
import net.dmulloy2.ultimatearena.types.Permission;
import net.dmulloy2.ultimatearena.types.PotionType;
import net.dmulloy2.ultimatearena.util.FormatUtil;
import net.dmulloy2.ultimatearena.util.InventoryHelper;
import net.dmulloy2.ultimatearena.util.Util;

import org.apache.commons.lang.WordUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.User;

/**
 * Base Data Container for an arena. This can be extended for specific arena
 * types.
 * 
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
	 * 
	 * @param az
	 *            - {@link ArenaZone} to base the {@link Arena} around
	 */
	public Arena(ArenaZone az)
	{
		this.az = az;
		this.plugin = az.getPlugin();
		this.name = az.getArenaName();
		this.world = az.getWorld();
		this.az.setTimesPlayed(az.getTimesPlayed() + 1);
		this.plugin.setArenasPlayed(plugin.getArenasPlayed() + 1);

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
	public final void reloadConfig()
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
	 * Adds a player to an {@link Arena}. Should not be overriden.
	 * 
	 * @param player
	 *            - {@link Player} to add to an arena
	 */
	public final void addPlayer(Player player)
	{
		player.sendMessage(plugin.getPrefix() + FormatUtil.format("&3Joining arena &e{0}&3... Please wait!", name));

		ArenaPlayer pl = new ArenaPlayer(player, this, plugin);

		// Update Teams
		pl.setTeam(getTeam());
		this.updatedTeams = true;

		// Teleport the player to the lobby spawn
		spawn(player, false);

		// Inventory
		pl.saveInventory();
		pl.clearInventory();

		// XP
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
			IEssentials ess = Util.getEssentials();
			User user = ess.getUser(player);

			// Disable GodMode in the arena
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
	 * Gets the base team. Can be overriden in certain cases.
	 * 
	 * @return Base team
	 */
	public int getTeam()
	{
		return 1;
	}

	/**
	 * Announces the arena's existance and reminds players to join.
	 */
	public final void announce()
	{
		for (Player player : plugin.getServer().getOnlinePlayers())
		{
			if (! plugin.isInArena(player))
			{
				if (plugin.getPermissionHandler().hasPermission(player, Permission.JOIN))
				{
					if (announced == 0)
					{
						player.sendMessage(plugin.getPrefix() + 
								FormatUtil.format("&e{0} &3arena has been created!", type.stylize()));
					}
					else
					{
						player.sendMessage(plugin.getPrefix() + 
								FormatUtil.format("&3Hurry up and join the &e{0} &3arena!", type.stylize()));
					}

					player.sendMessage(plugin.getPrefix() + 
							FormatUtil.format("&3Type &e/ua join {0} &3to join!", az.getArenaName()));
				}
			}
		}

		announced++;
	}

	/**
	 * Returns the team a player should be on.
	 * 
	 * @return The team the player should be on
	 */
	public int getBalancedTeam()
	{
		int amt1 = 0;
		int amt2 = 0;

		for (int i = 0; i < arenaPlayers.size(); i++)
		{
			ArenaPlayer ap = arenaPlayers.get(i);
			if (checkValid(ap))
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
	 * 
	 * @param stopifEmpty
	 *            - Stops the arena if empty
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
	 * 
	 * @param p
	 *            - Player instance
	 * @return The player's ArenaPlayer instance
	 */
	public final ArenaPlayer getArenaPlayer(Player p)
	{
		for (int i = 0; i < arenaPlayers.size(); i++)
		{
			ArenaPlayer ap = arenaPlayers.get(i);
			if (checkValid(ap))
			{
				if (ap.getName().equals(p.getName()))
					return ap;
			}
		}

		return null;
	}

	/**
	 * Spawns all players in an arena.
	 */
	public final void spawnAll()
	{
		plugin.debug("Spawning players for Arena: {0}", getArenaZone().getArenaName());

		for (int i = 0; i < arenaPlayers.size(); i++)
		{
			ArenaPlayer ap = arenaPlayers.get(i);
			if (checkValid(ap))
				spawn(ap.getPlayer(), false);
		}
	}

	/**
	 * Gets the spawn for an {@link ArenaPlayer}. Will return null if used by
	 * another {@link Arena}.
	 * 
	 * @param ap
	 *            - {@link ArenaPlayer} instance
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
	 * Spawns a player in an {@link Arena}. This should not be overriden.
	 * 
	 * @param name
	 *            - Player to spawn
	 * @param alreadyspawned
	 *            - Have they already been spawned?
	 */
	public final void spawn(Player player, boolean alreadySpawned)
	{
		plugin.debug("Attempting to spawn player: {0}", player.getName());

		if (! stopped)
		{
			ArenaPlayer ap = getArenaPlayer(player);
			if (checkValid(ap))
			{
				if (ap.getDeaths() < getMaxDeaths())
				{
					plugin.debug("Spawning player: {0}", player.getName());

					Location loc = getSpawn(ap);
					if (loc != null)
					{
						teleport(player, loc);

						// Call spawn event
						ArenaSpawn spawn = new ArenaSpawn(loc);
						UltimateArenaSpawnEvent spawnEvent = new UltimateArenaSpawnEvent(ap, this, spawn);
						plugin.getServer().getPluginManager().callEvent(spawnEvent);
					}

					ap.spawn();

					if (! alreadySpawned)
					{
						onSpawn(ap);
					}
				}
			}
		}
	}

	/**
	 * Called when a player is spawned.
	 * 
	 * @param apl
	 *            - {@link ArenaPlayer} who was spawned
	 */
	public void onSpawn(ArenaPlayer apl)
	{
	}

	/**
	 * Called when a player dies.
	 * 
	 * @param pl
	 *            - {@link ArenaPlayer} who died
	 */
	public void onPlayerDeath(ArenaPlayer pl)
	{
		pl.setAmtKicked(0);

		// Call ArenaDeathEvent
		UltimateArenaDeathEvent deathEvent = new UltimateArenaDeathEvent(pl, this);
		plugin.getServer().getPluginManager().callEvent(deathEvent);
	}

	/**
	 * Default rewarding system. May be overriden in some cases.
	 * 
	 * @param ap
	 *            - {@link ArenaPlayer} to reward
	 * @param half
	 *            - Whether or not to reward half
	 */
	public void reward(ArenaPlayer ap, boolean half)
	{
		plugin.debug("Rewarding player: {0}. Half: {1}", ap.getName(), half);

		if (config != null)
		{
			config.giveRewards(ap.getPlayer(), half);
		}
		else
		{
			InventoryHelper.addItem(ap.getPlayer(), new ItemStack(Material.GOLD_INGOT));
		}
	}

	/**
	 * Rewards an entire team.
	 * 
	 * @param team
	 *            - Team to reward
	 * @param string
	 *            - Reward message
	 * @param half
	 *            - Whether or not to reward half
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
					reward(ap, half);
				}
			}
		}
	}

	/**
	 * Sets the winning team.
	 * 
	 * @param team
	 *            - Winning team
	 */
	public void setWinningTeam(int team)
	{
		for (int i = 0; i < arenaPlayers.size(); i++)
		{
			ArenaPlayer ap = arenaPlayers.get(i);
			if (checkValid(ap))
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
	 * 
	 * @param max
	 *            - Max points for an arena
	 */
	public void checkPlayerPoints(int max)
	{
		for (int i = 0; i < arenaPlayers.size(); i++)
		{
			ArenaPlayer ap = arenaPlayers.get(i);
			if (checkValid(ap))
			{
				if (ap.getPoints() >= max)
				{
					tellPlayers("&3Player &e{0} &3has won!", ap.getName());

					stop();

					reward(ap, false);
				}
			}
		}
	}

	/**
	 * Stops the arena if empty.
	 * 
	 * @return Arena is empty
	 */
	public final boolean checkEmpty()
	{
		boolean ret = isEmpty();
		if (ret)
			stop();

		return ret;
	}

	/**
	 * Checks if the arena is empty.
	 * 
	 * @return Arena is empty
	 */
	public final boolean isEmpty()
	{
		return (isInGame() && getActivePlayers() <= 1);
	}

	/**
	 * Tells all players in the arena a message.
	 * 
	 * @param string
	 *            - Base message
	 * @param objects
	 *            - Objects to format in
	 */
	public final void tellPlayers(String string, Object... objects)
	{
		for (int i = 0; i < arenaPlayers.size(); i++)
		{
			ArenaPlayer ap = arenaPlayers.get(i);
			if (checkValid(ap))
			{
				ap.sendMessage(string, objects);
			}
		}
	}

	/**
	 * Kills all players within a certain radius of a {@link Location}
	 * 
	 * @param loc
	 *            - Center {@link Location}
	 * @param rad
	 *            - Radius to kill within
	 */
	public final void killAllNear(Location loc, int rad)
	{
		plugin.debug("Killing all players near {0} in a radius of {1}", Util.locationToString(loc), rad);

		for (int i = 0; i < arenaPlayers.size(); i++)
		{
			ArenaPlayer ap = arenaPlayers.get(i);
			if (checkValid(ap))
			{
				Location ploc = ap.getPlayer().getLocation();
				if (Util.pointDistance(loc, ploc) < rad)
					ap.getPlayer().setHealth(0.0D);
			}
		}
	}

	/**
	 * Gets a random spawn for an {@link ArenaPlayer}.
	 * 
	 * @param ap
	 *            - {@link ArenaPlayer} to get spawn for
	 * @return Spawn for the {@link ArenaPlayer}
	 */
	public Location getRandomSpawn(ArenaPlayer ap)
	{
		plugin.debug("Getting a random spawn for {0}", ap.getName());

		if (checkValid(ap))
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
	 * Alias for {@link Arena#giveItem(Player, Material, int, short, String)}
	 */
	public final void giveItem(Player pl, int id, short dat, int amt, String message)
	{
		@SuppressWarnings("deprecation")
		Material mat = Material.getMaterial(id);
		
		if (mat != null)
		{
			giveItem(pl, mat, amt, dat, message);
		}
	}
	
	/**
	 * Gives a {@link Player} an item
	 * 
	 * @param player
	 *            - {@link Player} to give items to
	 * @param mat
	 *            - {@link Material} of the item to give
	 * @param dat
	 *            - Data value of the item
	 * @param amt
	 *            - Amount of the item to give
	 * @param message
	 *            - Message to send the {@link Player}
	 */
	public final void giveItem(Player player, Material mat, int amt, short dat, String message)
	{
		if (! message.isEmpty())
		{
			player.sendMessage(plugin.getPrefix() + FormatUtil.format(message));
		}
		
		ItemStack item = new ItemStack(mat, amt, dat);
		
		InventoryHelper.addItem(player, item);
	}

	/**
	 * Gives a player a {@link Potion}
	 * 
	 * @param pl
	 *            - {@link Player} to give the {@link Potion}
	 * @param s
	 *            - Name of the {@link Potion} to give. Must be a valid
	 *            {@link PotionType}
	 * @param amt
	 *            - Amount of the {@link Potion} to give
	 * @param level
	 *            - Level of the {@link Potion} to give
	 * @param splash
	 *            - Whether or not it is a splash {@link Potion}
	 * @param message
	 *            - Message to send to the {@link Player}
	 */
	public void givePotion(Player pl, String s, int amt, int level, boolean splash, String message)
	{
		if (! message.isEmpty())
		{
			pl.sendMessage(plugin.getPrefix() + FormatUtil.format(message));
		}

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
	 * Basic killstreak system. Can be overriden.
	 * 
	 * @param ap
	 *            - {@link ArenaPlayer} to do killstreak for
	 */
	public void doKillStreak(ArenaPlayer ap)
	{
		/** Hunger Arena check **/
		if (ap.getArena().getType().equals("Hunger"))
			return;

		Player pl = ap.getPlayer();

		if (ap.getKillStreak() == 2)
			givePotion(pl, "strength", 1, 1, false, "&e2 &3kills! Unlocked strength potion!");

		if (ap.getKillStreak() == 4)
		{
			givePotion(pl, "heal", 1, 1, false, "&e4 &3kills! Unlocked health potion!");
			giveItem(pl, Material.GRILLED_PORK, 2, (short) 0, "&e4 &3kills! Unlocked Food!");
		}

		if (ap.getKillStreak() == 5)
		{
			if (! type.getName().equalsIgnoreCase("cq"))
			{
				ap.sendMessage(plugin.getPrefix() + "&e5 &3kills! Unlocked Zombies!");
				for (int i = 0; i < 4; i++)
					ap.getPlayer().getLocation().getWorld().spawnEntity(ap.getPlayer().getLocation(), EntityType.ZOMBIE);
			}
		}

		if (ap.getKillStreak() == 8)
		{
			ap.sendMessage("&e8 &3kills! Unlocked attackdogs!");
			for (int i = 0; i < 2; i++)
			{
				Wolf wolf = (Wolf) ap.getPlayer().getLocation().getWorld().spawnEntity(pl.getLocation(), EntityType.WOLF);
				wolf.setOwner(ap.getPlayer());
			}
		}

		if (ap.getKillStreak() == 12)
		{
			givePotion(pl, "regen", 1, 1, false, "&e12 &3kills! Unlocked regen potion!");
			giveItem(pl, Material.GRILLED_PORK, 2, (short) 0, "&e12 &3kills! Unlocked Food!");
		}
	}

	/**
	 * Disables this arena
	 */
	public final void onDisable()
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
	public final void stop()
	{
		if (stopped)
			return; // No need to stop multiple times

		plugin.outConsole("Stopping arena: {0}!", name);

		this.gameMode = Mode.STOPPING;
		this.stopped = true;

		updateSigns();

		onStop();

		announceWinner();

		for (int i = 0; i < arenaPlayers.size(); i++)
		{
			ArenaPlayer ap = arenaPlayers.get(i);
			if (checkValid(ap))
			{
				if (plugin.isInArena(ap.getPlayer()))
				{
					endPlayer(ap, false);
				}
			}
		}

		this.gameMode = Mode.IDLE;

		updateSigns();

		new FinalizeTask(this).runTaskLater(plugin, 120L);
	}

	/**
	 * Called when an arena is stopped
	 */
	public void onStop()
	{
	}

	/**
	 * Teleports a player to the most ideal location
	 * 
	 * @param p
	 *            - Player to teleport
	 * @param loc
	 *            - Raw location
	 */
	public final void teleport(Player p, Location loc)
	{
		p.teleport(loc.clone().add(0.5D, 1.0D, 0.5D));
	}

	/**
	 * Called when an arena is updated
	 */
	public void check()
	{
	}

	/**
	 * Ends an {@link ArenaPlayer}
	 * 
	 * @param ap
	 *            - {@link ArenaPlayer} to end
	 * @param dead
	 *            - Whether or not a player died
	 */
	public void endPlayer(ArenaPlayer ap, boolean dead)
	{
		plugin.debug("Ending Player: {0} Dead: {1}", ap.getName(), dead);

		ap.setOut(true);

		this.updatedTeams = true;

		returnXP(ap);

		ap.clearInventory();
		ap.returnInventory();

		plugin.removePotions(ap.getPlayer());

		teleport(ap.getPlayer(), ap.getSpawnBack());

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
	public void onOutOfTime()
	{
	}

	/**
	 * Called right before an arena runs out of time
	 */
	public void onPreOutOfTime()
	{
	}

	/**
	 * Checks timers
	 */
	public final void checkTimers()
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
	 * Starts the arena. Should not be overriden.
	 */
	public final void start()
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
	public final void update()
	{
		this.team1size = 0;
		this.team2size = 0;

		checkTimers();

		// Get how many people are in the arena
		for (int i = 0; i < arenaPlayers.size(); i++)
		{
			ArenaPlayer ap = arenaPlayers.get(i);
			if (checkValid(ap))
			{
				Player player = Util.matchPlayer(ap.getName());
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
			if (checkValid(ap))
			{
				// Check players in the Arena
				if (isInLobby())
				{
//					ap.getPlayer().setFireTicks(0);
//					ap.getPlayer().setFoodLevel(20);
					ap.decideHat();
				}

				ap.setHealTimer(ap.getHealTimer() - 1);

				ArenaClass ac = ap.getArenaClass();
				if (ac != null)
				{
					if (ac.getName().equalsIgnoreCase("healer") && ap.getHealTimer() <= 0)
					{
						if (ap.getPlayer().getHealth() + 1 <= 20)
						{
							if (ap.getPlayer().getHealth() < 0)
								ap.getPlayer().setHealth(1);
							ap.getPlayer().setHealth(ap.getPlayer().getHealth() + 1);
							ap.setHealTimer(2);
						}
					}

					// Class based potion effects
					if (ac.isHasPotionEffects())
					{
						if (ac.getPotionEffects().size() > 0)
						{
							for (PotionEffect effect : ac.getPotionEffects())
							{
								if (!ap.getPlayer().hasPotionEffect(effect.getType()))
									ap.getPlayer().addPotionEffect(effect);
							}
						}
					}
				}

/*				// Make sure they are still in the Arena
				if (!plugin.isInArena(ap.getPlayer().getLocation()))
				{
					spawn(ap.getPlayer(), false);
					ap.setAmtKicked(ap.getAmtKicked() + 1);
				}
*/
				// Timer Stuff
				if (!isPauseStartTimer())
				{
					if (startTimer == 120)
					{
						ap.sendMessage("&e120 &3seconds until start!");
					}
					if (startTimer == 60)
					{
						ap.sendMessage("&e60 &3seconds until start!");
					}
					if (startTimer == 45)
					{
						ap.sendMessage("&e45 &3seconds until start!");
					}
					if (startTimer == 30)
					{
						ap.sendMessage("&e30 &3seconds until start!");
					}
					if (startTimer == 15)
					{
						ap.sendMessage("&e15 &3seconds until start!");
					}
					if (startTimer > 0 && startTimer < 11)
					{
						ap.sendMessage("&e{0} &3second(s) until start!", startTimer);
					}
				}

				if (gameTimer > 0 && gameTimer < 21)
				{
					ap.sendMessage("&e{0} &3second(s) until end!", gameTimer);
				}
				if (gameTimer == 60 && maxGameTime > 60)
				{
					ap.sendMessage("&e{0} &3minute(s) until end!", gameTimer / 60);
				}
				if (gameTimer == maxGameTime / 2)
				{
					ap.sendMessage("&e{0} &3second(s) until end!", maxGameTime / 2);
				}

				// XP Bar
				decideXPBar(ap);

				// End dead players
				if (! stopped)
				{
					if (ap.getDeaths() >= getMaxDeaths())
					{
						if (ap.getPlayer().getHealth() > 0)
						{
							endPlayer(ap, true);
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
	 * 
	 * @param ap
	 *            - {@link ArenaPlayer} to decide xp bar for
	 */
	public final void decideXPBar(ArenaPlayer ap)
	{
		if (checkValid(ap))
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
	 * 
	 * @param ap
	 *            - {@link ArenaPlayer} to return xp
	 */
	public final void returnXP(ArenaPlayer ap)
	{
		if (ap != null)
		{
			plugin.debug("Returning XP for player: {0}. Levels: {1}", ap.getName(), ap.getBaseLevel());

			// Clear XP
			ap.getPlayer().setExp(0);
			ap.getPlayer().setLevel(0);

			// Give Base XP
			ap.getPlayer().setLevel(ap.getBaseLevel());
		}
	}

	/**
	 * Forces the start of an arena
	 * 
	 * @param player
	 *            - {@link Player} forcing the start of the arena
	 */
	public final void forceStart(Player player)
	{
		if (isInGame())
		{
			player.sendMessage(plugin.getPrefix() + FormatUtil.format("&cThis arena is already in progress!"));
			return;
		}

		plugin.outConsole("Forcefully starting arena: {0}!", name);

		start();

		gameTimer--;

		player.sendMessage(plugin.getPrefix() + FormatUtil.format("&3You have forcefully started &e{0}&3!", name));
	}

	public final String getName()
	{
		return name;
	}

	public final int getStartingAmount()
	{
		return startingAmount;
	}

	public final void setStartingAmount(int startingAmount)
	{
		this.startingAmount = startingAmount;
	}

	public final boolean isForceStop()
	{
		return forceStop;
	}

	public final void setForceStop(boolean forceStop)
	{
		this.forceStop = forceStop;
	}

	public final List<ArenaPlayer> getArenaPlayers()
	{
		return arenaPlayers;
	}

	public final int getStartTimer()
	{
		return startTimer;
	}

	public final ArenaZone getArenaZone()
	{
		return az;
	}

	public final int getActivePlayers()
	{
		int amt = 0;
		for (ArenaPlayer ap : arenaPlayers)
		{
			if (checkValid(ap))
				amt++;
		}

		return amt;
	}

	public final boolean isDisabled()
	{
		return disabled;
	}

	public final void setDisabled(boolean disabled)
	{
		this.disabled = disabled;
	}

	public final FieldType getType()
	{
		return type;
	}

	public final int getGameTimer()
	{
		return gameTimer;
	}

	public final int getMaxGameTime()
	{
		return maxGameTime;
	}

	public final int getMaxDeaths()
	{
		return maxDeaths;
	}

	public final void setMaxDeaths(int maxDeaths)
	{
		this.maxDeaths = maxDeaths;
	}

	public final boolean isUpdatedTeams()
	{
		return updatedTeams;
	}

	public final void setUpdatedTeams(boolean updatedTeams)
	{
		this.updatedTeams = updatedTeams;
	}

	public final List<ArenaFlag> getFlags()
	{
		return flags;
	}

	public final void setFlags(List<ArenaFlag> flags)
	{
		this.flags = flags;
	}

	public final int getWinningTeam()
	{
		return winningTeam;
	}

	public final boolean isStopped()
	{
		return stopped;
	}

	public final void setStopped(boolean stopped)
	{
		this.stopped = stopped;
	}

	public final boolean isAllowTeamKilling()
	{
		return allowTeamKilling;
	}

	public final void setAllowTeamKilling(boolean allowTeamKilling)
	{
		this.allowTeamKilling = allowTeamKilling;
	}

	public final List<ArenaSpawn> getSpawns()
	{
		return spawns;
	}

	public final void setSpawns(List<ArenaSpawn> spawns)
	{
		this.spawns = spawns;
	}

	public final World getWorld()
	{
		return world;
	}

	public final int getWave()
	{
		return wave;
	}

	public final int getMaxWave()
	{
		return maxWave;
	}

	public final int getTeam1size()
	{
		return team1size;
	}

	public final void setTeam1size(int team1size)
	{
		this.team1size = team1size;
	}

	public final boolean isPauseStartTimer()
	{
		return pauseStartTimer;
	}

	public final void setPauseStartTimer(boolean pauseStartTimer)
	{
		this.pauseStartTimer = pauseStartTimer;
	}

	public final void setType(FieldType type)
	{
		this.type = type;
	}

	public final boolean isInGame()
	{
		return (startTimer < 1 && gameTimer > 0);
	}

	public final boolean isInLobby()
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
			if (checkValid(ap))
			{
				kdrMap.put(ap.getName(), ap.getKDR());
			}
		}

		final List<Map.Entry<String, Double>> sortedEntries = new ArrayList<Map.Entry<String, Double>>(kdrMap.entrySet());
		Collections.sort(sortedEntries, new Comparator<Map.Entry<String, Double>>()
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
				line.append(FormatUtil.format("&3#{0}. ", pos));
				line.append(FormatUtil.format(decideColor(apl)));
				line.append(FormatUtil.format(apl.getName().equals(player.getName()) ? "&l" : ""));
				line.append(FormatUtil.format(apl.getName() + "&r"));
				line.append(FormatUtil.format("  &3Kills: &e{0}", apl.getKills()));
				line.append(FormatUtil.format("  &3Deaths: &e{0}", apl.getDeaths()));
				line.append(FormatUtil.format("  &3KDR: &e{0}", entry.getValue()));
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

	public final Mode getGameMode()
	{
		return gameMode;
	}

	public final void updateSigns()
	{
		plugin.getSignHandler().updateSigns();
	}

	public void announceWinner()
	{
		if (winningTeam == 2)
		{
			tellPlayers("&eBlue &3team won!");
		}
		else if (winningTeam == 1)
		{
			tellPlayers("&eRed &3team won!");
		}
		else if (winningTeam == -1)
		{
			tellPlayers("&3Game ended in a tie!");
		}
	}

	public final boolean checkValid(ArenaPlayer ap)
	{
		return ap != null && ! ap.isOut();
	}

	public final List<ArenaPlayer> getValidPlayers()
	{
		List<ArenaPlayer> validPlayers = new ArrayList<ArenaPlayer>();

		for (int i = 0; i < arenaPlayers.size(); i++)
		{
			ArenaPlayer ap = arenaPlayers.get(i);
			if (checkValid(ap))
				validPlayers.add(ap);
		}

		return validPlayers;
	}

	public final class FinalizeTask extends BukkitRunnable
	{
		private final Arena arena;
		public FinalizeTask(Arena arena)
		{
			this.arena = arena;
		}

		@Override
		public void run()
		{
			plugin.getActiveArenas().remove(arena);

			plugin.broadcast("&e{0} &3arena has concluded!", WordUtils.capitalize(name));

			try
			{
				arena.finalize();
			}
			catch (Throwable e)
			{
			}
		}
	}
}