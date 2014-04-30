package net.dmulloy2.ultimatearena.arenas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.flags.ArenaFlag;
import net.dmulloy2.ultimatearena.types.ArenaClass;
import net.dmulloy2.ultimatearena.types.ArenaLocation;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.FieldType;
import net.dmulloy2.ultimatearena.types.KillStreak;
import net.dmulloy2.ultimatearena.types.LeaveReason;
import net.dmulloy2.ultimatearena.types.Permission;
import net.dmulloy2.ultimatearena.types.Reloadable;
import net.dmulloy2.ultimatearena.util.FormatUtil;
import net.dmulloy2.ultimatearena.util.Util;

import org.apache.commons.lang.WordUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Base arena class. Can be extended to create custom arena types.
 * <p>
 * This class must be extended to function, since it does not create a
 * functional arena on its own. It does, however, provide useful methods that
 * can be used to create a functional arena.
 * <p>
 * The simplest example of a functional arena is the {@link PVPArena}
 * 
 * @author dmulloy2
 */

@Getter @Setter
public abstract class Arena implements Reloadable
{
	public static enum Mode
	{
		LOBBY, INGAME, STOPPING, IDLE, DISABLED;
	}

	protected List<ArenaPlayer> active;
	protected List<ArenaPlayer> inactive;
	protected List<ArenaPlayer> toReward;

	protected List<ArenaFlag> flags;
	protected List<ArenaLocation> spawns;

	private List<String> blacklistedClasses;
	private List<String> whitelistedClasses;

	private HashMap<Integer, List<KillStreak>> killStreaks;

	protected int broadcastTimer = 45;
	protected int winningTeam = 999;
	protected int maxPoints = 60;
	protected int maxDeaths = 1;
	protected int maxWave = 15;

	protected int startingAmount;
	protected int maxGameTime;
	protected int startTimer;
	protected int gameTimer;
	protected int team1size;
	protected int team2size;
	protected int announced;
	protected int wave;

	protected boolean allowTeamKilling;
	protected boolean rewardBasedOnXp;
	protected boolean pauseStartTimer;
	protected boolean countMobKills;
	protected boolean giveRewards;
	protected boolean forceStop;
	protected boolean stopped;
	protected boolean start;

	protected boolean updatedTeams;
	protected boolean disabled;
	protected boolean inLobby;
	protected boolean inGame;

	protected Mode gameMode = Mode.DISABLED;

	protected final World world;
	protected FieldType type;
	protected String name;

	protected final UltimateArena plugin;
	protected final ArenaZone az;

	/**
	 * Base {@link Arena} constructor. All constructors should reference this.
	 * 
	 * @param az
	 *        - {@link ArenaZone} to base the {@link Arena} around.
	 */
	public Arena(ArenaZone az)
	{
		this.az = az;
		this.plugin = az.getPlugin();
		this.name = az.getName();
		this.type = az.getType();
		this.world = az.getWorld();
		this.az.setTimesPlayed(az.getTimesPlayed() + 1);

		this.active = new ArrayList<ArenaPlayer>();
		this.inactive = new ArrayList<ArenaPlayer>();

		this.flags = new ArrayList<ArenaFlag>();
		this.spawns = new ArrayList<ArenaLocation>();

		this.gameMode = Mode.LOBBY;

		this.startTimer = az.getConfig().getLobbyTime();
		this.inLobby = true;

		this.reload();

		plugin.getSpectatingHandler().registerArena(this);
	}

	/**
	 * Reloads the Arena's settings
	 */
	@Override
	public final void reload()
	{
		this.maxGameTime = az.getConfig().getGameTime();
		this.gameTimer = az.getConfig().getGameTime();
		this.maxDeaths = az.getConfig().getMaxDeaths();
		this.allowTeamKilling = az.getConfig().isAllowTeamKilling();
		this.maxWave = az.getConfig().getMaxWave();
		this.maxPoints = az.getConfig().getMaxPoints();
		this.countMobKills = az.getConfig().isCountMobKills();
		this.rewardBasedOnXp = az.getConfig().isRewardBasedOnXp();
		this.killStreaks = az.getConfig().getKillStreaks();
		this.giveRewards = az.getConfig().isGiveRewards();

		this.blacklistedClasses = az.getConfig().getBlacklistedClasses();
		this.whitelistedClasses = az.getConfig().getWhitelistedClasses();

		if (maxDeaths < 1)
		{
			this.maxDeaths = 1;
		}

		onReload();
	}

	/**
	 * Called when the Arena is reloaded.
	 */
	public void onReload() { }

	/**
	 * Adds a player to the {@link Arena}.
	 *
	 * @param player
	 *        - {@link Player} to add to an arena
	 */
	public final void addPlayer(Player player)
	{
		player.sendMessage(plugin.getPrefix() + FormatUtil.format("&3Joining arena &e{0}&3... Please wait!", name));

		ArenaPlayer pl = new ArenaPlayer(player, this, plugin);

		// Update Teams
		pl.setTeam(getTeam());

		this.updatedTeams = true;

		// Teleport the player to the lobby spawn
		spawnLobby(pl);

		// Save vital data
		pl.savePlayerData();

		// Clear Inventory
		pl.clearInventory();

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

		// Disable god mode
		if (plugin.useEssentials())
			plugin.getEssentialsHandler().disableGodMode(player);

		// Clear potion effects
		pl.clearPotionEffects();

		// Decide Hat
		pl.decideHat();

		// Finally add the player
		active.add(pl);

		// API
		onJoin(pl);

		tellPlayers("&a{0} has joined the arena! ({1}/{2})", pl.getName(), active.size(), az.getMaxPlayers());
	}

	/**
	 * Called when an {@link ArenaPlayer} joins the arena.
	 * 
	 * @param ap
	 *        - {@link ArenaPlayer} who joined
	 */
	public void onJoin(ArenaPlayer ap) { }

	/**
	 * Returns which team a new player should be on.
	 * <p>
	 * Can be overriden in certain cases.
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
								FormatUtil.format("&e{0} &3arena has been created!", type.getStylized()));
					}
					else
					{
						player.sendMessage(plugin.getPrefix() +
								FormatUtil.format("&3Hurry up and join the &e{0} &3arena!", type.getStylized()));
					}

					player.sendMessage(plugin.getPrefix() +
							FormatUtil.format("&3Type &e/ua join {0} &3to join!", az.getName()));
				}
			}
		}

		announced++;
	}

	/**
	 * Returns the team with the least number of players on it.
	 */
	public final int getBalancedTeam()
	{
		// Refresh team size
		updateTeams();

		return team1size > team2size ? 2 : 1;
	}

	/**
	 * A simple team check.
	 */
	public boolean simpleTeamCheck()
	{
		if (team1size == 0 || team2size == 0)
		{
			return startingAmount < 1;
		}

		return true;
	}

	/**
	 * Returns a {@link Player}'s {@link ArenaPlayer} instance.
	 * <p>
	 * Every player who has joined this arena will have an ArenaPlayer instance.
	 * It is important to note, however, that players who are out will still
	 * have arena player instances until the arena concludes.
	 *
	 * @param p
	 *        - Player instance
	 * @param checkInactive
	 *        - Whether or not to check the inactive list as well
	 */
	public final ArenaPlayer getArenaPlayer(Player p, boolean checkInactive)
	{
		for (ArenaPlayer ap : active)
		{
			if (ap.getName().equalsIgnoreCase(p.getName()))
				return ap;
		}

		if (checkInactive)
		{
			for (ArenaPlayer ap : inactive)
			{
				if (ap.getName().equalsIgnoreCase(p.getName()))
					return ap;
			}
		}

		return null;
	}

	/**
	 * Alias for {@link #getArenaPlayer(Player, boolean)}
	 * <p>
	 * Has the same effect as <code>getArenaPlayer(p, true)</code>
	 *
	 * @param p
	 *        - Player instance
	 */
	public final ArenaPlayer getArenaPlayer(Player p)
	{
		return getArenaPlayer(p, false);
	}

	/**
	 * Spawns all players in an arena.
	 */
	public final void spawnAll()
	{
		plugin.debug("Spawning players for Arena {0}", name);

		for (ArenaPlayer ap : active)
		{
			spawn(ap);
		}
	}

	/**
	 * Gets the spawn for an {@link ArenaPlayer}.
	 * <p>
	 * Can be overriden under certain circumstances
	 *
	 * @param ap
	 *        - {@link ArenaPlayer} instance
	 */
	public Location getSpawn(ArenaPlayer ap)
	{
		ArenaLocation loc = null;
		if (isInLobby())
		{
			loc = az.getLobbyREDspawn();
			if (ap.getTeam() == 2)
				loc = az.getLobbyBLUspawn();
		}
		else
		{
			loc = az.getTeam1spawn();
			if (ap.getTeam() == 2)
				loc = az.getTeam2spawn();
		}

		return loc.getLocation();
	}

	/**
	 * Spawns a player in the {@link Arena}.
	 *
	 * @param ap
	 *        - Player to spawn
	 * @param alreadySpawned
	 *        - Whether or not they've already spawned
	 */
	public final void spawn(ArenaPlayer ap, boolean alreadySpawned)
	{
		plugin.debug("Attempting to spawn player {0}", ap.getName());

		if (! stopped)
		{
			if (ap.getDeaths() < maxDeaths)
			{
				Location loc = getSpawn(ap);
				if (loc != null)
				{
					plugin.debug("Spawning player: {0}", ap.getName());

					ap.teleport(loc);
					ap.spawn();

					if (! alreadySpawned)
					{
						onSpawn(ap);
					}
				}
				else
				{
					// We couldn't find a spawnpoint for some reason :(
					ap.sendMessage("&cError spawning: Null spawnpoint!");
					ap.leaveArena(LeaveReason.ERROR);
				}
			}
			else
			{
				// If they've reached the death cap, remove them
				ap.leaveArena(LeaveReason.DEATHS);
			}
		}
	}

	/**
	 * Alias for {@link #spawn(Player, Boolean)}
	 * <p>
	 * Has the same effect of <code>spawn(player, false)</code>
	 *
	 * @param ap
	 *        - Player to spawn
	 */
	public final void spawn(ArenaPlayer ap)
	{
		spawn(ap, false);
	}

	/**
	 * Called when a player is spawned.
	 *
	 * @param ap
	 *        - {@link ArenaPlayer} who was spawned
	 */
	public void onSpawn(ArenaPlayer ap) { }

	/**
	 * Spawns an {@link ArenaPlayer} into the lobby
	 *
	 * @param ap
	 *        - {@link ArenaPlayer} to spawn
	 */
	public final void spawnLobby(ArenaPlayer ap)
	{
		Location loc = getSpawn(ap);
		if (loc != null)
		{
			plugin.debug("Spawning player {0} in the lobby", ap.getName());

			ap.teleport(loc);
		}
		else
		{
			ap.sendMessage("&cError spawning: Null spawnpoint!");
			ap.leaveArena(LeaveReason.ERROR);
		}
	}

	/**
	 * Called when a player dies.
	 *
	 * @param pl
	 *        - {@link ArenaPlayer} who died
	 */
	public void onPlayerDeath(ArenaPlayer pl)
	{
		pl.setAmtKicked(0);
	}

	/**
	 * Default rewarding system. May be overriden in some cases.
	 *
	 * @param ap
	 *        - {@link ArenaPlayer} to reward
	 */
	public void reward(ArenaPlayer ap)
	{
		if (az.getConfig().isGiveRewards() && ap != null)
			az.giveRewards(ap);
	}

	/**
	 * Rewards an entire team.
	 *
	 * @param team
	 *        - Team to reward
	 */
	public final void rewardTeam(int team)
	{
		if (az.getConfig().isGiveRewards())
		{
			for (ArenaPlayer ap : toReward)
			{
				if (ap.isCanReward())
				{
					if (ap.getTeam() == team || team == -1)
					{
						reward(ap);
					}
				}
			}
		}

		toReward.clear();
	}

	/**
	 * Sets the winning team.
	 *
	 * @param team
	 *        - Winning team
	 */
	public final void setWinningTeam(int team)
	{
		this.toReward = new ArrayList<ArenaPlayer>();

		for (ArenaPlayer ap : active)
		{
			ap.setCanReward(false);
			if (ap.getTeam() == team || team == -1)
			{
				ap.setCanReward(true);

				toReward.add(ap);
			}
		}

		this.winningTeam = team;
	}

	/**
	 * Checks if a player has enough points to win.
	 *
	 * @param max
	 *        - Max points for an arena
	 */
	public final void checkPlayerPoints(int max)
	{
		for (ArenaPlayer ap : getActivePlayers())
		{
			if (ap.getPoints() >= max)
			{
				tellAllPlayers("&3Player &e{0} &3has won!", ap.getName());

				stop();

				reward(ap);
			}
		}
	}

	/**
	 * Stops the arena if empty.
	 *
	 * @return Whether or not the arena is empty
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
	 * @return Whether or not the arena is empty
	 */
	public final boolean isEmpty()
	{
		return isInGame() && active.size() <= 1;
	}

	/**
	 * Tells all active players in the arena a message.
	 *
	 * @param string
	 *        - Base message
	 * @param objects
	 *        - Objects to format in
	 */
	public final void tellPlayers(String string, Object... objects)
	{
		for (ArenaPlayer ap : active)
		{
			ap.sendMessage(string, objects);
		}
	}

	/**
	 * Tells all players a message.
	 * <p>
	 * Includes inactive players
	 *
	 * @param string
	 *        - Base message
	 * @param objects
	 *        - Objects to format in
	 */
	public final void tellAllPlayers(String string, Object... objects)
	{
		tellPlayers(string, objects);

		for (ArenaPlayer ap : inactive)
		{
			if (ap != null && ap.getPlayer().isOnline())
			{
				ap.sendMessage(string, objects);
			}
		}
	}

	/**
	 * Kills all players within a certain radius of a {@link Location}
	 *
	 * @param loc
	 *        - Center {@link Location}
	 * @param rad
	 *        - Radius to kill within
	 */
	public final void killAllNear(Location loc, double rad)
	{
		plugin.debug("Killing all players near {0} in a radius of {1}", Util.locationToString(loc), rad);

		for (ArenaPlayer ap : active)
		{
			if (ap.getPlayer().getHealth() > 0.0D && ap.getPlayer().getLocation().distance(loc) < rad)
				ap.getPlayer().setHealth(0.0D);
		}
	}

	/**
	 * Returns a random spawn for an {@link ArenaPlayer}.
	 *
	 * @param ap
	 *        - {@link ArenaPlayer} to get spawn for
	 */
	public Location getRandomSpawn(ArenaPlayer ap)
	{
		plugin.debug("Getting a random spawn for {0}", ap.getName());

		if (! spawns.isEmpty())
		{
			return spawns.get(Util.random(spawns.size())).getLocation();
		}

		return null;
	}

	/**
	 * Handles an {@link ArenaPlayer}'s kill streak (if applicable)
	 *
	 * @param ap
	 *        - {@link ArenaPlayer} to handle kill streak for
	 */
	public final void handleKillStreak(ArenaPlayer ap)
	{
		if (killStreaks.isEmpty())
			return;

		if (killStreaks.containsKey(ap.getKillStreak()))
		{
			List<KillStreak> streaks = killStreaks.get(ap.getKillStreak());
			for (KillStreak streak : streaks)
			{
				if (streak != null)
					streak.perform(ap);
			}
		}
	}

	/**
	 * Disables this arena
	 */
	public final void disable()
	{
		tellPlayers("&cThis arena has been disabled!");

		this.gameTimer = -1;

		stop();

		this.disabled = true;
		this.gameMode = Mode.DISABLED;

		updateSigns();
		onDisable();
	}

	/**
	 * Called when the arena is disabled.
	 */
	public void onDisable() { }

	/**
	 * Ends the arena
	 */
	public final void stop()
	{
		if (stopped) return; // No need to stop multiple times

		plugin.outConsole("Stopping arena {0}!", name);

		this.inGame = false;
		this.stopped = true;

		this.gameMode = Mode.STOPPING;

		clearSigns();
		onStop();

		announceWinner();

		for (ArenaPlayer ap : getActivePlayers())
		{
			endPlayer(ap, false);
		}

		plugin.getSpectatingHandler().unregisterArena(this);

		clearEntities();
		conclude(120L);
	}

	/**
	 * Called when an arena is stopped
	 */
	public void onStop() { }

	private final void conclude(long delay)
	{
		if (delay <= 0 || plugin.isStopping())
		{
			conclude();
			return;
		}

		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				conclude();
			}
		}.runTaskLater(plugin, 120L);
	}

	private final void conclude()
	{
		if (! disabled) this.gameMode = Mode.IDLE;
		plugin.removeActiveArena(this);
		plugin.broadcast("&e{0} &3arena has concluded!", WordUtils.capitalize(name));
	}

	/**
	 * Ends an {@link ArenaPlayer}
	 *
	 * @param ap
	 *        - {@link ArenaPlayer} to end
	 * @param dead
	 *        - Whether or not a player died
	 */
	public void endPlayer(ArenaPlayer ap, boolean dead)
	{
		plugin.debug("Ending Player: {0} Dead: {1}", ap.getName(), dead);

		ap.reset();
		ap.teleport(ap.getSpawnBack());

		ap.setOut(true);
		updatedTeams = true;

		active.remove(ap);
		inactive.add(ap);

		if (dead)
		{
			ap.sendMessage("&3You have exceeded the death limit!");
			tellPlayers("&e{0} &3has been eliminated!", ap.getName());
		}

		if (active.size() > 1)
		{
			tellPlayers("&3There are &e{0} &3players remaining!", active.size());
		}
	}

	/**
	 * Basic timer checker.
	 * <p>
	 * Should not be overriden.
	 */
	public final void checkTimers()
	{
		if (stopped)
			return;

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
	 * Called right before an arena runs out of time.
	 */
	public void onPreOutOfTime() { }

	/**
	 * Called when an arena runs out of time.
	 */
	public void onOutOfTime() { }

	/**
	 * Starts the arena.
	 * <p>
	 * Should not be overriden.
	 */
	public final void start()
	{
		if (! start)
		{
			plugin.outConsole("Starting arena {0} with {1} players", name, active.size());

			this.start = true;
			this.inGame = true;
			this.inLobby = false;

			this.gameMode = Mode.INGAME;

			this.startingAmount = active.size();

			this.gameTimer = maxGameTime;
			this.startTimer = -1;

			onStart();
			spawnAll();
		}
	}

	/**
	 * Called when an arena starts
	 */
	public void onStart() { }

	/**
	 * Arena Updater
	 */
	public final void update()
	{
		if (stopped) // Don't tick if stopped
			return;

		checkTimers();
		updateTeams();
		check();

		for (ArenaPlayer ap : getActivePlayers())
		{
			// Null check
			if (ap == null)
			{
				active.remove(ap);
				inactive.remove(ap);
				continue;
			}

			// Make sure they're still online
			if (! ap.getPlayer().isOnline())
			{
				// Attempt to end them
				ap.leaveArena(LeaveReason.QUIT);
				continue;
			}

			// End if they've reached the death limit and they're alive
			if (ap.getDeaths() >= maxDeaths && ap.getPlayer().getHealth() > 0.0D)
			{
				ap.leaveArena(LeaveReason.DEATHS);
				continue;
			}

			// Hats
			if (isInLobby())
			{
				ap.decideHat();
			}

			// Class based stuff
			ap.setHealTimer(ap.getHealTimer() - 1);

			ArenaClass ac = ap.getArenaClass();
			if (ac != null)
			{
				// Healing. TODO: Does anyone even use this anymore?
				if (ac.getName().equalsIgnoreCase("healer") && ap.getHealTimer() <= 0)
				{
					if (ap.getPlayer().getHealth() > 0 && ap.getPlayer().getHealth() + 1 <= 20)
					{
						ap.getPlayer().setHealth(ap.getPlayer().getHealth() + 1);
						ap.setHealTimer(2);
					}
				}

				// Potion effects
				if (ac.isHasPotionEffects())
				{
					if (ac.getPotionEffects().size() > 0)
					{
						for (PotionEffect effect : ac.getPotionEffects())
						{
							if (! ap.getPlayer().hasPotionEffect(effect.getType()))
								ap.getPlayer().addPotionEffect(effect);
						}
					}
				}
			}

			// Timer Stuff
			if (! pauseStartTimer)
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
		}

		if (active.size() <= 0)
			stop();

		// Update signs
		updateSigns();
	}

	/**
	 * Called when the arena is updated. Generally every second or so.
	 */
	public void check() { }

	/**
	 * Decides the timer xp bar for an {@link ArenaPlayer}
	 *
	 * @param ap
	 *        - {@link ArenaPlayer} to decide xp bar for
	 */
	public final void decideXPBar(ArenaPlayer ap)
	{
		if (plugin.getConfig().getBoolean("timerXPBar", true))
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

	/**
	 * Forces the start of an arena
	 *
	 * @param player
	 *        - {@link Player} forcing the start of the arena
	 */
	public final void forceStart(Player player)
	{
		if (isInGame())
		{
			player.sendMessage(plugin.getPrefix() +
					FormatUtil.format("&cThis arena is already in progress!"));
			return;
		}

		plugin.outConsole("Forcefully starting arena {0}", name);

		start();

		gameTimer--;

		player.sendMessage(plugin.getPrefix() +
				FormatUtil.format("&3You have forcefully started &e{0}&3!", name));
	}

	/**
	 * Clears the entities inside this arena
	 */
	private final void clearEntities()
	{
		plugin.debug("Clearing entities in arena {0}", name);

		List<EntityType> persistentEntities = Arrays.asList(new EntityType[]
		{
				EntityType.PLAYER, EntityType.PAINTING, EntityType.ITEM_FRAME, EntityType.VILLAGER
		});

		for (Entity entity : world.getEntities())
		{
			if (entity != null && entity.isValid())
			{
				if (az.checkLocation(entity.getLocation()))
				{
					if (! persistentEntities.contains(entity.getType()))
					{
						if (entity instanceof LivingEntity)
							((LivingEntity) entity).setHealth(0.0D);

						entity.remove();
					}
				}
			}
		}
	}


	/**
	 * Returns a customized in-game leaderboard
	 *
	 * @param player
	 *        - Player to get leaderboard for
	 */
	// TODO: Use the side scoreboard
	public List<String> getLeaderboard(Player player)
	{
		List<String> leaderboard = new ArrayList<String>();

		// Build kills map
		HashMap<String, Double> kdrMap = new HashMap<String, Double>();

		for (ArenaPlayer ap : getActivePlayers())
		{
			kdrMap.put(ap.getName(), ap.getKDR());
		}

		List<Entry<String, Double>> sortedEntries = new ArrayList<Entry<String, Double>>(kdrMap.entrySet());
		Collections.sort(sortedEntries, new Comparator<Entry<String, Double>>()
		{
			@Override
			public int compare(Entry<String, Double> entry1, Entry<String, Double> entry2)
			{
				return -entry1.getValue().compareTo(entry2.getValue());
			}
		});

		int pos = 1;
		for (Entry<String, Double> entry : sortedEntries)
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

	/**
	 * Decides a player's team color
	 *
	 * @param pl
	 *        - Player to decide team color for
	 */
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

	/**
	 * Updates the signs for the arena
	 */
	protected final void updateSigns()
	{
		plugin.getSignHandler().updateSigns(az);
	}

	/**
	 * Clears the signs for the arena
	 */
	protected final void clearSigns()
	{
		plugin.getSignHandler().clearSigns(az);
	}

	/**
	 * Announces the winner of the arena
	 */
	protected void announceWinner()
	{
		if (winningTeam == 2)
		{
			tellAllPlayers("&eBlue &3team won!");
		}
		else if (winningTeam == 1)
		{
			tellAllPlayers("&eRed &3team won!");
		}
		else if (winningTeam == -1)
		{
			tellAllPlayers("&3Game ended in a tie!");
		}
	}

	/**
	 * Returns whether or not a class can be used in this arena
	 *
	 * @param ac
	 *        - Class to check
	 */
	public final boolean isValidClass(ArenaClass ac)
	{
		if (! whitelistedClasses.isEmpty())
		{
			return whitelistedClasses.contains(ac.getName());
		}

		if (! blacklistedClasses.isEmpty())
		{
			return ! blacklistedClasses.contains(ac.getName());
		}

		return true;
	}

	/**
	 * Workaround for concurrency issues.
	 * <p>
	 * Should not be used for adding or removing players.
	 */
	public final List<ArenaPlayer> getActivePlayers()
	{
		return Util.newList(active);
	}

	/**
	 * Workaround for concurrency issues.
	 * <p>
	 * Should not be used for adding or removing players.
	 */
	public final List<ArenaPlayer> getInactivePlayers()
	{
		return Util.newList(inactive);
	}

	/**
	 * Returns the amount of players currently in the arena
	 */
	public final int getPlayerCount()
	{
		return active.size();
	}

	/**
	 * Updates teams
	 */
	private final void updateTeams()
	{
		this.team1size = 0;
		this.team2size = 0;

		for (ArenaPlayer ap : getActivePlayers())
		{
			if (ap.getTeam() == 1)
				team1size++;
			else
				team2size++;
		}
	}

	// ---- Generic Methods

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(@NonNull Object obj)
	{
		if (obj instanceof Arena)
		{
			Arena that = (Arena) obj;
			return that.getName().equals(name);
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode()
	{
		int hash = 35;
		hash *= name.hashCode();
		return hash;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString()
	{
		return name;
	}
}