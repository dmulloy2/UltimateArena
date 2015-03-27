package net.dmulloy2.ultimatearena.arenas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import lombok.Getter;
import lombok.Setter;
import net.dmulloy2.chat.BaseComponent;
import net.dmulloy2.chat.ChatUtil;
import net.dmulloy2.chat.ClickEvent;
import net.dmulloy2.chat.ComponentBuilder;
import net.dmulloy2.chat.HoverEvent;
import net.dmulloy2.types.Reloadable;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.api.ArenaType;
import net.dmulloy2.ultimatearena.api.event.ArenaJoinEvent;
import net.dmulloy2.ultimatearena.api.event.ArenaLeaveEvent;
import net.dmulloy2.ultimatearena.arenas.pvp.PvPArena;
import net.dmulloy2.ultimatearena.gui.ClassSelectionGUI;
import net.dmulloy2.ultimatearena.types.ArenaClass;
import net.dmulloy2.ultimatearena.types.ArenaConfig;
import net.dmulloy2.ultimatearena.types.ArenaFlag;
import net.dmulloy2.ultimatearena.types.ArenaLocation;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.ArenaSpectator;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.KillStreak;
import net.dmulloy2.ultimatearena.types.LeaveReason;
import net.dmulloy2.ultimatearena.types.Permission;
import net.dmulloy2.ultimatearena.types.Team;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.Util;

import org.apache.commons.lang.WordUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Base arena class. Can be extended to create custom arena types.
 * <p>
 * This class must be extended to function, since it does not create a
 * functional arena on its own. It does, however, provide useful methods that
 * can be used to create a functional arena.
 * <p>
 * The simplest example of a functional arena is the {@link PvPArena}
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
	protected List<ArenaSpectator> spectators;

	protected List<ArenaFlag> flags;
	protected List<ArenaLocation> spawns;

	private List<String> blacklistedClasses;
	private List<String> whitelistedClasses;

	private Map<Integer, List<KillStreak>> killStreaks;

	protected Team winningTeam;

	protected int broadcastTimer = 45;
	protected int maxDeaths = 1;

	protected int startingAmount;
	protected int maxGameTime;
	protected int startTimer;
	protected int gameTimer;
	protected int redTeamSize;
	protected int blueTeamSize;
	protected int announced;

	protected boolean allowTeamKilling;
	protected boolean rewardBasedOnXp;
	protected boolean pauseStartTimer;
	protected boolean countMobKills;
	protected boolean giveRewards;
	protected boolean stopped;
	protected boolean started;

	protected boolean disabled;
	protected boolean inLobby;
	protected boolean inGame;

	protected String name;

	protected Mode gameMode = Mode.IDLE;

	protected final World world;
	protected final ArenaZone az;
	protected final ArenaType type;
	protected final UltimateArena plugin;

	/**
	 * Base {@link Arena} constructor. All constructors must reference this.
	 *
	 * @param az {@link ArenaZone} to base the {@link Arena} around.
	 */
	public Arena(ArenaZone az)
	{
		this.az = az;
		this.plugin = az.getPlugin();
		this.name = az.getName();
		this.type = az.getType();
		this.world = az.getWorld();
		this.az.setTimesPlayed(az.getTimesPlayed() + 1);

		this.active = new ArrayList<>();
		this.inactive = new ArrayList<>();
		this.spectators = new ArrayList<>();

		this.flags = new ArrayList<>();
		this.spawns = new ArrayList<>();

		this.gameMode = Mode.LOBBY;

		this.startTimer = az.getConfig().getLobbyTime();
		this.inLobby = true;

		this.reload();
	}

	/**
	 * Reloads the Arena's settings.
	 */
	@Override
	public final void reload()
	{
		this.maxGameTime = az.getConfig().getGameTime();
		this.gameTimer = az.getConfig().getGameTime();
		this.maxDeaths = az.getConfig().getMaxDeaths();
		this.allowTeamKilling = az.getConfig().isAllowTeamKilling();
		this.countMobKills = az.getConfig().isCountMobKills();
		this.rewardBasedOnXp = az.getConfig().isRewardBasedOnXp();
		this.killStreaks = az.getConfig().getKillStreaks();
		this.giveRewards = az.getConfig().isGiveRewards();

		this.blacklistedClasses = az.getConfig().getBlacklistedClasses();
		this.whitelistedClasses = az.getConfig().getWhitelistedClasses();

		onReload();
	}

	/**
	 * Called when the Arena is reloaded.
	 */
	public void onReload() { }

	/**
	 * Adds a player to the arena.
	 *
	 * @param player {@link Player} to add to an arena
	 */
	public final void addPlayer(Player player, int team)
	{
		player.sendMessage(plugin.getPrefix() + FormatUtil.format("&3Joining arena &e{0}&3... Please wait!", name));

		ArenaPlayer pl = new ArenaPlayer(player, this, plugin);

		ArenaJoinEvent event = new ArenaJoinEvent(player, pl, this);
		plugin.getServer().getPluginManager().callEvent(event);
		if (event.isCancelled())
		{
			pl.sendMessage(event.getCancelMessage() != null ? event.getCancelMessage() : "&3Unexpected exit!");
			pl.clear();
			return;
		}

		// Set their team
		pl.setTeam(team == -1 ? getTeam() : Team.getById(team));

		// Teleport the player to the lobby spawn
		spawnLobby(pl);

		// Save vital data
		pl.savePlayerData();

		// Add metadata
		player.setMetadata("UA", new FixedMetadataValue(plugin, true));

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
		if (plugin.isEssentialsEnabled())
			plugin.getEssentialsHandler().disableGodMode(player);

		// Clear potion effects
		pl.clearPotionEffects();

		// Decide Hat
		pl.decideHat(false);

		// API
		onJoin(pl);

		// Add the player
		active.add(pl);

		// Open Class Selector
		if (plugin.getConfig().getBoolean("classSelector.automatic", true))
		{
			ClassSelectionGUI csGUI = new ClassSelectionGUI(plugin, player);
			plugin.getGuiHandler().open(player, csGUI);
		}

		tellPlayers("&a{0} has joined the arena! ({1}/{2})", pl.getName(), active.size(), az.getMaxPlayers());
	}

	/**
	 * Called when an {@link ArenaPlayer} joins the arena.
	 *
	 * @param ap {@link ArenaPlayer} who joined
	 */
	public void onJoin(ArenaPlayer ap) { }

	/**
	 * Gets which team a new player should be on.
	 * <p>
	 * Can be overriden in certain cases.
	 *
	 * @return The team, defaults to RED.
	 */
	public Team getTeam()
	{
		return Team.RED;
	}

	/**
	 * Announces the arena's existance and reminds players to join.
	 */
	public final void announce()
	{
		if (! plugin.getConfig().getBoolean("globalMessages", true))
			return;

		// Allow players to click and insert into chat
		BaseComponent[] components = new ComponentBuilder(FormatUtil.format(plugin.getPrefix() + "&3Type "))
			.append(FormatUtil.format("&e/ua join {0}", name))
			.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ua join " + name))
			.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, FormatUtil.format("&eClick to join {0}!", name)))
			.append(FormatUtil.format(" &3to join!"))
			.create();

		for (Player player : Util.getOnlinePlayers())
		{
			if (! plugin.isInArena(player))
			{
				if (plugin.getPermissionHandler().hasPermission(player, Permission.JOIN))
				{
					if (announced == 0)
					{
						player.sendMessage(plugin.getPrefix() + FormatUtil.format("&e{0} &3arena has been created!",
								type.getStylizedName()));
					}
					else
					{
						player.sendMessage(plugin.getPrefix() + FormatUtil.format("&3Hurry up and join the &e{0} &3arena!",
								type.getStylizedName()));
					}

					ChatUtil.sendMessage(player, components);
				}
			}
		}

		announced++;
	}

	/**
	 * Gets the team with the least amount of players on it.
	 *
	 * @return The team with the least amount of players
	 */
	public final Team getBalancedTeam()
	{
		updateTeams();
		return redTeamSize > blueTeamSize ? Team.BLUE : Team.RED;
	}

	/**
	 * A simple team check.
	 *
	 * @return True if there are people on both teams, false if not
	 */
	public boolean simpleTeamCheck()
	{
		updateTeams();
		return redTeamSize == 0 || blueTeamSize == 0 ? startingAmount < 1 : true;
	}

	/**
	 * Gets a {@link Player}'s {@link ArenaPlayer} instance.
	 * <p>
	 * Every player who has joined this arena will have an ArenaPlayer instance.
	 * It is important to note, however, that players who are out will still
	 * have arena player instances until the arena concludes. Use
	 * {@link ArenaPlayer#isOut()} to check if they're still in the arena.
	 *
	 * @param player Player
	 * @param checkInactive Whether or not to check the inactive list as well
	 * @return The player's ArenaPlayer instance, or null if not found
	 */
	public final ArenaPlayer getArenaPlayer(Player player, boolean checkInactive)
	{
		for (ArenaPlayer ap : getActivePlayers())
		{
			if (player.getUniqueId().equals(ap.getUniqueId()))
				return ap;
		}

		if (checkInactive)
		{
			for (ArenaPlayer ap : getInactivePlayers())
			{
				if (player.getUniqueId().equals(ap.getUniqueId()))
					return ap;
			}
		}

		return null;
	}

	/**
	 * Alias for {@link #getArenaPlayer(Player, boolean)}.
	 * <p>
	 * Has the same effect as <code>getArenaPlayer(player, false)</code>
	 *
	 * @param player Player
	 */
	public final ArenaPlayer getArenaPlayer(Player player)
	{
		return getArenaPlayer(player, false);
	}

	/**
	 * Spawns all players in the arena.
	 */
	public final void spawnAll()
	{
		plugin.debug("Spawning players for arena {0}", name);

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
	 * @param ap {@link ArenaPlayer} instance
	 * @return Their spawn
	 */
	public Location getSpawn(ArenaPlayer ap)
	{
		ArenaLocation loc = null;
		if (isInLobby())
		{
			loc = az.getLobbyREDspawn();
			if (ap.getTeam() == Team.BLUE)
				loc = az.getLobbyBLUspawn();
		}
		else
		{
			loc = az.getTeam1spawn();
			if (ap.getTeam() == Team.BLUE)
				loc = az.getTeam2spawn();
		}

		return loc.getLocation();
	}

	/**
	 * Spawns a player in the {@link Arena}.
	 *
	 * @param ap Player to spawn
	 * @param alreadySpawned Whether or not they've already spawned
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
					plugin.debug("Spawning player {0}", ap.getName());

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
	 * Alias for {@link #spawn(Player, Boolean)}.
	 * <p>
	 * Has the same effect of <code>spawn(player, false)</code>
	 *
	 * @param ap Player to spawn
	 */
	public final void spawn(ArenaPlayer ap)
	{
		spawn(ap, false);
	}

	/**
	 * Called when a player is spawned.
	 *
	 * @param ap {@link ArenaPlayer} who was spawned
	 */
	public void onSpawn(ArenaPlayer ap) { }

	/**
	 * Spawns an {@link ArenaPlayer} into the lobby.
	 *
	 * @param ap {@link ArenaPlayer} to spawn
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
	 * @param pl {@link ArenaPlayer} who died
	 */
	public void onPlayerDeath(ArenaPlayer pl) { }

	/**
	 * Default rewarding system. May be overriden in some cases.
	 *
	 * @param ap {@link ArenaPlayer} to reward
	 */
	public void reward(ArenaPlayer ap)
	{
		if (ap != null && az.getConfig().isGiveRewards())
			az.giveRewards(ap);
	}

	/**
	 * Rewards an entire team.
	 *
	 * @param team Team to reward
	 */
	public final void rewardTeam(Team team)
	{
		if (az.getConfig().isGiveRewards())
		{
			for (ArenaPlayer ap : toReward)
			{
				if (ap.isCanReward())
				{
					if (ap.getTeam() == team || team == null)
						reward(ap);
				}
			}
		}

		toReward.clear();
	}

	/**
	 * Sets the winning team.
	 *
	 * @param team Winning team
	 */
	public final void setWinningTeam(Team team)
	{
		this.toReward = new ArrayList<>();

		for (ArenaPlayer ap : active)
		{
			ap.setCanReward(false);
			if (ap.getTeam() == team || team == null)
			{
				ap.setCanReward(true);
				toReward.add(ap);
			}
		}

		this.winningTeam = team;
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
	 * @param string Base message
	 * @param objects Objects to format in
	 */
	public final void tellPlayers(String string, Object... objects)
	{
		for (ArenaPlayer ap : active)
		{
			ap.sendMessage(string, objects);
		}

		for (ArenaSpectator as : spectators)
		{
			as.sendMessage(string, objects);
		}
	}

	/**
	 * Tells all players a message.
	 * <p>
	 * Includes inactive players
	 *
	 * @param string Base message
	 * @param objects Objects to format in
	 */
	public final void tellAllPlayers(String string, Object... objects)
	{
		tellPlayers(string, objects);

		for (ArenaPlayer ap : inactive)
		{
			if (ap != null && ap.isOnline())
			{
				ap.sendMessage(string, objects);
			}
		}
	}

	/**
	 * Kills all players within a certain radius of a {@link Location}
	 *
	 * @param loc Center {@link Location}
	 * @param rad Radius to kill within
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
	 * @param ap {@link ArenaPlayer} to get spawn for
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
	 * @param ap {@link ArenaPlayer} to handle kill streak for
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
	 * Disables this arena.
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
	 * Ends the arena.
	 */
	public final void stop()
	{
		if (stopped)
			return; // No need to stop multiple times

		plugin.log("Stopping arena {0}!", name);

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

		for (ArenaSpectator as : spectators)
		{
			as.endPlayer();
		}

		clearEntities();
		clearMaterials();

		conclude(120L);
	}

	/**
	 * Called when the arena is stopped.
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
		if (! disabled)
			this.gameMode = Mode.IDLE;

		plugin.removeActiveArena(this);
		plugin.broadcast("&e{0} &3arena has concluded!", WordUtils.capitalize(name));
		updateSigns();
	}

	/**
	 * Alias for {@link #endPlayer(ArenaPlayer, boolean)}.
	 * <p>
	 * Same as calling {@code endPlayer(ap, false)}.
	 */
	public final void endPlayer(ArenaPlayer ap)
	{
		endPlayer(ap, false);
	}

	/**
	 * Ends an {@link ArenaPlayer}.
	 *
	 * @param ap Player to end
	 * @param disconnected Whether or not they disconnected
	 */
	public final void endPlayer(ArenaPlayer ap, boolean disconnected)
	{
		plugin.debug("Ending player {0}, disconnected: {1}", ap.getName(), disconnected);

		// API stuff
		onPlayerQuit(ap);

		ArenaLeaveEvent event = new ArenaLeaveEvent(ap.getPlayer(), ap, this);
		plugin.getServer().getPluginManager().callEvent(event);

		// Reset them
		ap.reset();
		ap.teleport(ap.getSpawnBack());

		ap.setOut(true);

		// Remove from lists
		active.remove(ap);
		if (! disconnected)
			inactive.add(ap);

		if (active.size() > 1)
			tellPlayers("&3There are &e{0} &3players remaining!", active.size());
	}

	/**
	 * Called when an {@link ArenaPlayer} quits.
	 *
	 * @param ap ArenaPlayer who quit
	 */
	public void onPlayerQuit(ArenaPlayer ap) { }

	/**
	 * Basic timer checker.
	 * <p>
	 * Can not be overriden.
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
	 * Called right before the arena runs out of time.
	 */
	public void onPreOutOfTime() { }

	/**
	 * Called after the arena runs out of time.
	 */
	public void onOutOfTime() { }

	/**
	 * Starts the arena.
	 * <p>
	 * Can not be overriden.
	 */
	public final void start()
	{
		if (! started)
		{
			plugin.log("Starting arena {0} with {1} players", name, active.size());

			this.started = true;
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
	 * Called when the arena starts.
	 */
	public void onStart() { }

	/**
	 * Arena Updater.
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
			if (! ap.isOnline())
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
				ap.decideHat(false);

			// Class stuff
			ArenaClass ac = ap.getArenaClass();
			if (ac != null)
			{
				if (ac.getName().equalsIgnoreCase("healer"))
				{
					ap.putData("healTimer", ap.getDataInt("healTimer") - 1);
					if (ap.getDataInt("healTimer") <= 0)
					{
						if (ap.getPlayer().getHealth() > 0 && ap.getPlayer().getHealth() + 1 <= 20)
						{
							ap.getPlayer().setHealth(ap.getPlayer().getHealth() + 1);
							ap.getData().put("healTimer", 2);
						}
					}
				}

				// Potion effects
				if (! ap.isChangeClassOnRespawn() && ac.isHasPotionEffects())
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
	 * Decides the timer xp bar for an {@link ArenaPlayer}.
	 *
	 * @param ap {@link ArenaPlayer} to decide xp bar for
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
	 * Forces the start of the arena.
	 *
	 * @param player {@link Player} forcing the start of the arena
	 */
	public final void forceStart(Player player)
	{
		if (isInGame())
		{
			player.sendMessage(plugin.getPrefix() + FormatUtil.format("&cThis arena is already in progress!"));
			return;
		}

		plugin.log("Forcefully starting arena {0}", name);

		start();

		gameTimer--;

		player.sendMessage(plugin.getPrefix() + FormatUtil.format("&3You have forcefully started &e{0}&3!", name));
	}

	private static final List<EntityType> persistentEntities = Arrays.asList(
			EntityType.PLAYER, EntityType.PAINTING, EntityType.ITEM_FRAME, EntityType.VILLAGER
	);

	private final void clearEntities()
	{
		try
		{
			int count = 0;
			for (Entity entity : world.getEntities())
			{
				if (entity != null && entity.isValid())
				{
					if (! persistentEntities.contains(entity.getType()))
					{
						if (isInside(Util.getLocationSafely(entity)))
						{
							if (entity instanceof LivingEntity)
								((LivingEntity) entity).setHealth(0.0D);
	
							entity.remove();
							count++;
						}
					}
				}
			}
	
			plugin.debug("Removed {0} entities from {1}", count, name);
		}
		catch (Throwable ex)
		{
			plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "clearing entities in " + this));
		}
	}

	private final void clearMaterials()
	{
		List<Material> clear = getConfig().getClearMaterials();
		if (! clear.isEmpty())
			az.getArena().removeMaterials(clear);
	}

	/**
	 * Gets a list of active players, sorted by KDR.
	 *
	 * @return The list
	 */
	public List<ArenaPlayer> getLeaderboard()
	{
		Map<ArenaPlayer, Double> kdrMap = new HashMap<>();
		for (ArenaPlayer ap : getActivePlayers())
		{
			kdrMap.put(ap, ap.getKDR());
		}

		List<Entry<ArenaPlayer, Double>> sortedEntries = new ArrayList<>(kdrMap.entrySet());
		Collections.sort(sortedEntries, new Comparator<Entry<ArenaPlayer, Double>>()
		{
			@Override
			public int compare(Entry<ArenaPlayer, Double> entry1, Entry<ArenaPlayer, Double> entry2)
			{
				return -entry1.getValue().compareTo(entry2.getValue());
			}
		});

		List<ArenaPlayer> leaderboard = new ArrayList<>();
		for (Entry<ArenaPlayer, Double> entry : sortedEntries)
		{
			leaderboard.add(entry.getKey());
		}

		return leaderboard;
	}

	/**
	 * Gets a customized in-game leaderboard for a given player
	 *
	 * @param player Player to get the leaderboard for
	 * @return Leaderboard
	 * @see {@link #getLeaderboard()}
	 */
	public List<String> getLeaderboard(Player player)
	{
		List<String> leaderboard = new ArrayList<>();

		int pos = 1;
		for (ArenaPlayer ap : getLeaderboard())
		{
			if (ap != null)
			{
				StringBuilder line = new StringBuilder();
				line.append(FormatUtil.format("&3#{0}. ", pos));
				line.append(FormatUtil.format(decideColor(ap)));
				line.append(FormatUtil.format(ap.getName().equals(player.getName()) ? "&l" : ""));
				line.append(FormatUtil.format(ap.getName() + "&r"));
				line.append(FormatUtil.format("  &3Kills: &e{0}", ap.getKills()));
				line.append(FormatUtil.format("  &3Deaths: &e{0}", ap.getDeaths()));
				line.append(FormatUtil.format("  &3KDR: &e{0}", ap.getKDR()));
				leaderboard.add(line.toString());
				pos++;
			}
		}

		return leaderboard;
	}

	/**
	 * Decides a player's team color.
	 *
	 * @param ap Player to decide team color for
	 */
	protected String decideColor(ArenaPlayer ap)
	{
		return ap.getTeam().getColor().toString();
	}

	/**
	 * Updates the signs for the arena.
	 */
	protected final void updateSigns()
	{
		plugin.getSignHandler().updateSigns(az);
	}

	/**
	 * Clears the signs for the arena.
	 */
	protected final void clearSigns()
	{
		plugin.getSignHandler().clearSigns(az);
	}

	/**
	 * Announces the winner of the arena.
	 */
	protected void announceWinner()
	{
		if (winningTeam == Team.BLUE)
		{
			tellAllPlayers("&eBlue &3team won!");
		}
		else if (winningTeam == Team.RED)
		{
			tellAllPlayers("&eRed &3team won!");
		}
		else if (winningTeam == null)
		{
			tellAllPlayers("&3Game ended in a tie!");
		}
	}

	/**
	 * Whether or not a class can be used in this arena. This method compares
	 * the class against whitelisted and blacklisted classes.
	 *
	 * @param ac Class to check
	 * @return True if the class can be used in this arena, false if not.
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

	private static final ArenaPlayer[] EMPTY_ARRAY = new ArenaPlayer[0];

	/**
	 * Workaround for concurrency issues.
	 *
	 * @return An array of active players
	 */
	public final ArenaPlayer[] getActivePlayers()
	{
		return active.toArray(EMPTY_ARRAY);
	}

	/**
	 * Workaround for concurrency issues.
	 * 
	 * @return An array of inactive players
	 */
	public final ArenaPlayer[] getInactivePlayers()
	{
		return inactive.toArray(EMPTY_ARRAY);
	}

	/**
	 * @return the amount of players currently in the arena
	 */
	public final int getPlayerCount()
	{
		return active.size();
	}

	private final void updateTeams()
	{
		this.redTeamSize = 0;
		this.blueTeamSize = 0;

		for (ArenaPlayer ap : getActivePlayers())
		{
			if (ap.getTeam() == Team.RED)
				redTeamSize++;
			else
				blueTeamSize++;
		}
	}

	/**
	 * Checks whether or not a {@link Location} is inside this arena.
	 *
	 * @param location Location to check
	 * @return True if inside, false if not
	 */
	public final boolean isInside(Location location)
	{
		return az.isInside(location);
	}

	/**
	 * Gets this Arena's configuration.
	 *
	 * @return This arena's configuration
	 */
	public ArenaConfig getConfig()
	{
		return az.getConfig();
	}

	/**
	 * Gets extra info for /ua info
	 *
	 * @return Extra info, or null if none
	 */
	public List<String> getExtraInfo()
	{
		return null;
	}

	/**
	 * Whether or not this arena is active.
	 *
	 * @return True if active, false if not
	 */
	public final boolean isActive()
	{
		return ! stopped;
	}

	public final ArenaFlag[] getFlags()
	{
		return flags.toArray(new ArenaFlag[0]);
	}

	// ---- Generic Methods

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof Arena)
		{
			Arena that = (Arena) obj;
			return this.name.equals(that.name);
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
		hash *= 1 + name.hashCode();
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