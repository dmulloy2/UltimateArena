/**
 * UltimateArena - fully customizable PvP arenas
 * Copyright (C) 2012 - 2015 MineSworn
 * Copyright (C) 2013 - 2015 dmulloy2
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
import net.dmulloy2.chat.ComponentSerializer;
import net.dmulloy2.types.CustomScoreboard;
import net.dmulloy2.types.Reloadable;
import net.dmulloy2.ultimatearena.Config;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.api.ArenaType;
import net.dmulloy2.ultimatearena.api.event.ArenaConcludeEvent;
import net.dmulloy2.ultimatearena.api.event.ArenaJoinEvent;
import net.dmulloy2.ultimatearena.api.event.ArenaLeaveEvent;
import net.dmulloy2.ultimatearena.api.event.ArenaSpawnEvent;
import net.dmulloy2.ultimatearena.api.event.ArenaStartEvent;
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
import net.dmulloy2.ultimatearena.types.WinCondition;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.Util;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
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
	protected String lastJoin;

	protected List<String> finalLeaderboard;
	protected int leaderboardIndex;

	protected List<ArenaFlag> flags;
	protected List<ArenaLocation> spawns;

	private String defaultClass;
	private List<String> blacklistedClasses;
	private List<String> whitelistedClasses;

	private Map<Team, List<String>> mandatedClasses;
	private Map<Integer, List<KillStreak>> killStreaks;

	protected WinCondition winCondition;
	protected Team winningTeam;

	protected int broadcastTimer = 45;
	protected int maxPlayers = 24;
	protected int minPlayers = 1;
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
	protected boolean joinInProgress;
	protected boolean countMobKills;
	protected boolean forceBalance;
	protected boolean giveRewards;
	protected boolean limitSpam;

	protected boolean disabled;
	protected boolean stopped;
	protected boolean started;
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
		this.allowTeamKilling = az.getConfig().isAllowTeamKilling();
		this.countMobKills = az.getConfig().isCountMobKills();
		this.rewardBasedOnXp = az.getConfig().isRewardBasedOnXp();
		this.giveRewards = az.getConfig().isGiveRewards();
		this.forceBalance = az.getConfig().isForceBalance();
		this.joinInProgress = az.getConfig().isJoinInProgress();

		this.maxGameTime = az.getConfig().getGameTime();
		this.gameTimer = az.getConfig().getGameTime();
		this.maxDeaths = az.getConfig().getMaxDeaths();
		this.maxPlayers = az.getConfig().getMaxPlayers();
		this.minPlayers = az.getConfig().getMinPlayers();

		this.defaultClass = az.getConfig().getDefaultClass();

		this.killStreaks = az.getConfig().getKillStreaks();
		this.winCondition = az.getConfig().getWinCondition();
		this.blacklistedClasses = az.getConfig().getBlacklistedClasses();
		this.whitelistedClasses = az.getConfig().getWhitelistedClasses();
		this.mandatedClasses = az.getConfig().getMandatedClasses();

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
	 * @param teamId Team name/id
	 */
	public final void addPlayer(Player player, String teamId)
	{
		player.sendMessage(plugin.getPrefix() + FormatUtil.format(getMessage("joiningArena"), name));

		ArenaPlayer ap = new ArenaPlayer(player, this, plugin);

		// API - cancellable join event
		ArenaJoinEvent event = new ArenaJoinEvent(this, ap);
		plugin.getServer().getPluginManager().callEvent(event);
		if (event.isCancelled())
		{
			ap.sendMessage(event.getCancelMessage() != null ? event.getCancelMessage() : "&3Unexpected exit!");
			ap.clear();
			return;
		}

		// API - onJoin
		onJoin(ap);

		// Determine their team
		Team team = teamId == null ? getTeam() : Team.get(teamId);
		ap.setTeam(team);

		// Teleport the player to the lobby spawn
		spawnLobby(ap);

		// Save vital data
		ap.savePlayerData();

		player.setMetadata("UA", plugin.getUAIdentifier());

		ap.clearInventory();

		player.setGameMode(GameMode.SURVIVAL);

		// Heal up the Player
		player.setFoodLevel(20);
		player.setFireTicks(0);
		player.setHealth(20);

		// Don't allow flight
		player.setAllowFlight(false);
		player.setFlySpeed(0.1F);
		player.setFlying(false);

		// Disable Essentials god mode
		if (plugin.isEssentialsEnabled())
			plugin.getEssentialsHandler().disableGodMode(player);

		ap.clearPotionEffects();

		decideHat(ap);

		active.add(ap);
		updateTeams();

		// Alert the other players
		tellPlayers(getMessage("joinedArena"), ap.getName(), active.size(), maxPlayers);
		this.lastJoin = ap.getName();

		// Open Class Selector
		if (Config.classSelectorAutomatic)
		{
			ClassSelectionGUI csGUI = new ClassSelectionGUI(plugin, player);
			plugin.getGuiHandler().open(player, csGUI);
		}

		if (started)
			spawn(ap);
	}

	/**
	 * Decide a player's hat
	 * @param ap Player to give hat to
	 */
	public void decideHat(ArenaPlayer ap)
	{
		ap.decideHat(false);
	}

	/**
	 * Gets a list of available classes for a given Team.
	 * 
	 * @param team Player team
	 * @return Available classes
	 */
	public List<ArenaClass> getAvailableClasses(Team team)
	{
		List<String> classNames = mandatedClasses.get(team);
		if (classNames != null && classNames.size() > 0)
		{
			List<ArenaClass> classes = new ArrayList<>();
			for (String name : classNames)
			{
				ArenaClass ac = plugin.getArenaClass(name);
				if (ac != null)
					classes.add(ac);
			}

			return classes;
		}

		return plugin.getClasses();
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
		if (! Config.globalMessages)
			return;

		// Allow players to click and insert into chat
		String clickToJoin = getMessage("clickToJoin");
		BaseComponent[] components = clickToJoin.isEmpty() ? null : ComponentSerializer.parse(FormatUtil.format(clickToJoin.replace("%s", name)));

		for (Player player : Util.getOnlinePlayers())
		{
			if (! plugin.isInArena(player))
			{
				if (plugin.getPermissionHandler().hasPermission(player, Permission.JOIN))
				{
					String message = "";
					
					if (announced == 0)
						message = FormatUtil.format(getMessage("arenaCreated"), type.getStylizedName());
					else
						message = FormatUtil.format(getMessage("hurryAndJoin"), type.getStylizedName());

					if (! message.isEmpty())
						player.sendMessage(plugin.getPrefix() + message);

					if (components != null)
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
		return redTeamSize > blueTeamSize ? Team.BLUE : Team.RED;
	}

	/**
	 * A simple team check.
	 *
	 * @return True if there are people on both teams, false if not
	 */
	public boolean simpleTeamCheck()
	{
		return (redTeamSize == 0 || blueTeamSize == 0) ? startingAmount < 1 : true;
	}

	/**
	 * Gets a {@link Player}'s {@link ArenaPlayer} instance.
	 * <p>
	 * Every player who has joined this arena will have an ArenaPlayer instance.
	 * It is important to note, however, that players who are out will still
	 * have arena player instances until the arena concludes. Use
	 * {@code player.isOut()} to make sure they're still in the arena.
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

					ArenaSpawnEvent event = new ArenaSpawnEvent(this, ap, loc);
					loc = event.getLocation();

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
					ap.sendMessage(getMessage("nullSpawnpoint"));
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
	 * Alias for {@link #spawn(ArenaPlayer, boolean)}.
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
			ap.sendMessage(getMessage("nullSpawnpoint"));
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
		if (string.isEmpty()) return;

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
		if (string.isEmpty()) return;

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
		tellPlayers(getMessage("hasBeenDisabled"));

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
			endPlayer(ap, LeaveReason.END_GAME);
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
		// API - conclude event
		ArenaConcludeEvent event = new ArenaConcludeEvent(this);
		plugin.getServer().getPluginManager().callEvent(event);

		if (! disabled)
			this.gameMode = Mode.IDLE;

		plugin.removeActiveArena(this);
		plugin.broadcast(getMessage("concluded"), WordUtils.capitalize(name));

		plugin.getSignHandler().onArenaCompletion(this);
		plugin.getSignHandler().updateSigns(az);
	}

	/**
	 * Ends an {@link ArenaPlayer}.
	 *
	 * @param ap Player to end
	 * @param reason Reason they're being ended
	 */
	public final void endPlayer(ArenaPlayer ap, LeaveReason reason)
	{
		plugin.debug("Ending player {0}, reason: {1}", ap.getName(), reason);

		// Dispose of the scoreboard
		ap.getBoard().dispose();

		// API - leave event
		ArenaLeaveEvent event = new ArenaLeaveEvent(this, ap);
		plugin.getServer().getPluginManager().callEvent(event);

		// Reset them
		ap.reset();
		ap.teleport(ap.getSpawnBack());

		ap.setOut(true);

		// Remove from lists
		active.remove(ap);
		if (reason != LeaveReason.QUIT)
			inactive.add(ap);
		updateTeams();

		// Add them to the final leaderboard if applicable
		if (finalLeaderboard != null)
		{
			try
			{
				finalLeaderboard.set(leaderboardIndex, ap.getName());
				leaderboardIndex--;
			} catch (IndexOutOfBoundsException ex) { }
		}

		// Let everyone know why
		switch (reason)
		{
			case COMMAND:
			case QUIT:
				tellPlayers(getMessage("leaveBroadcast"), ap.getName());
				break;
			case DEATHS:
				tellPlayers(getMessage("elimination"), ap.getName());
				break;
			case ERROR:
				tellPlayers(getMessage("errorBroadcast"), ap.getName());
				break;
			case KICK:
				tellPlayers(getMessage("kickBroadcast"), ap.getName());
				break;
			case END_GAME:
			case POWER:
				break;
		}

		if (reason != LeaveReason.END_GAME && active.size() > 1)
			tellPlayers(getMessage("playersRemaining"), active.size());

		// API - onPlayerEnd
		onPlayerEnd(ap);
	}

	/**
	 * Called after an {@link ArenaPlayer} is ended.
	 * 
	 * @param ap Ended ArenaPlayer
	 */
	public void onPlayerEnd(ArenaPlayer ap) { }

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
			tellPlayers(getMessage("outOfTime"));

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
			// Make sure there are enough players
			if (active.size() < minPlayers)
			{
				tellPlayers(getMessage("notEnoughPeople"), minPlayers);
				stop();
				return;
			}

			// API - start event
			ArenaStartEvent event = new ArenaStartEvent(this);
			plugin.getServer().getPluginManager().callEvent(event);

			// API - onStart
			onStart();

			// Balance teams if applicable
			if (forceBalance && lastJoin != null)
			{
				if (redTeamSize != blueTeamSize)
				{
					Player extra = Util.matchPlayer(lastJoin);
					if (extra != null)
					{
						ArenaPlayer ap = getArenaPlayer(extra);
						if (ap != null)
						{
							ap.leaveArena(LeaveReason.KICK);
							ap.sendMessage(getMessage("fairnessKick"));
						}
					}
				}
			}

			plugin.log("Starting arena {0} with {1} players", name, active.size());

			this.started = true;
			this.inGame = true;
			this.inLobby = false;

			this.gameMode = Mode.INGAME;

			this.startingAmount = active.size();

			// Setup the leaderboard for leaderboard signs
			this.finalLeaderboard = new ArrayList<>(startingAmount);
			Collections.fill(finalLeaderboard, "");

			this.leaderboardIndex = startingAmount - 1;

			this.gameTimer = maxGameTime;
			this.startTimer = -1;

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
		check();

		for (ArenaPlayer ap : getActivePlayers())
		{
			// Make sure they're still online
			if (! ap.isOnline())
			{
				ap.leaveArena(LeaveReason.QUIT);
				continue;
			}

			// Kick them if they've reached the death limit and they're alive
			if (ap.getDeaths() >= maxDeaths && ap.getPlayer().getHealth() > 0.0D)
			{
				ap.leaveArena(LeaveReason.DEATHS);
				continue;
			}

			// Lobby hats
			if (isInLobby())
				decideHat(ap);

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
				if (startTimer == 120 || startTimer == 60 || startTimer == 45 || startTimer == 30 || startTimer == 15)
				{
					ap.sendMessage(getMessage("secondsUntilStart"), startTimer);
				}
				if (startTimer > 0 && startTimer < 11)
				{
					ap.sendMessage(getMessage("secondsUntilStart"), startTimer);
				}
			}

			if (gameTimer > 0 && gameTimer < 21)
			{
				ap.sendMessage(getMessage("secondsUntilEnd"), gameTimer);
			}
			if (gameTimer == 60 && maxGameTime > 60)
			{
				ap.sendMessage(getMessage("minutesUntilEnd"), gameTimer / 60);
			}
			if (gameTimer == maxGameTime / 2)
			{
				ap.sendMessage(getMessage("secondsUntilEnd"), maxGameTime / 2);
			}

			// XP Bar
			decideXPBar(ap);

			// Scoreboard
			ap.updateScoreboard();
		}

		// Stop if its empty
		if (active.size() == 0)
			stop();

		// Update signs
		updateSigns();
	}

	/**
	 * Called when the arena is updated. Generally every second or so.
	 */
	public void check() { }

	/**
	 * Called when a player in this Arena moves.
	 * @param ap Moving player
	 */
	public void onMove(ArenaPlayer ap) { }

	/**
	 * Decides the timer xp bar for an {@link ArenaPlayer}.
	 *
	 * @param ap {@link ArenaPlayer} to decide xp bar for
	 */
	public final void decideXPBar(ArenaPlayer ap)
	{
		if (Config.timerXPBar)
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
			player.sendMessage(plugin.getPrefix() + FormatUtil.format(getMessage("arenaInProgress")));
			return;
		}

		plugin.log("Forcefully starting arena {0}", name);

		start();

		gameTimer--;

		player.sendMessage(plugin.getPrefix() + FormatUtil.format(getMessage("forceStart"), name));
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
						if (isInside(entity.getLocation()))
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
	 * @see #getLeaderboard()
	 */
	public List<String> getLeaderboard(Player player)
	{
		List<String> leaderboard = new ArrayList<>();

		int pos = 1;
		for (ArenaPlayer ap : getLeaderboard())
		{
			if (ap != null)
			{
				leaderboard.add(FormatUtil.format(getMessage("leaderboard"),
						pos, decideColor(ap), ap.getName().equals(player.getName()) ? "&l" : "", ap.getName(), ap.getKills(), ap.getDeaths(), ap.getKDR()
				));
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
			tellAllPlayers(getMessage("blueWon"));
		}
		else if (winningTeam == Team.RED)
		{
			tellAllPlayers(getMessage("redWon"));
		}
		else if (winningTeam == null)
		{
			tellAllPlayers(getMessage("tie"));
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

	/**
	 * Workaround for concurrency issues.
	 *
	 * @return An array of active players
	 */
	public final ArenaPlayer[] getActivePlayers()
	{
		return active.toArray(new ArenaPlayer[active.size()]);
	}

	/**
	 * Workaround for concurrency issues.
	 * 
	 * @return An array of inactive players
	 */
	public final ArenaPlayer[] getInactivePlayers()
	{
		return inactive.toArray(new ArenaPlayer[inactive.size()]);
	}

	/**
	 * Updates the size of the red and blue teams.
	 * @see #getRedTeamSize()
	 * @see #getBlueTeamSize()
	 */
	protected final void updateTeams()
	{
		this.redTeamSize = 0;
		this.blueTeamSize = 0;

		for (ArenaPlayer ap : getActivePlayers())
		{
			if (ap.getTeam() == Team.RED)
				redTeamSize++;
			else if (ap.getTeam() == Team.BLUE)
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

	public final ArenaFlag[] getFlags()
	{
		return flags.toArray(new ArenaFlag[flags.size()]);
	}

	// ---- Public API

	/**
	 * Gets the number of Players currently in this Arena.
	 * 
	 * @return Number of Players.
	 */
	public final int getPlayerCount()
	{
		return active.size();
	}

	/**
	 * Gets the maximum number of Players permitted by this Arena.
	 * 
	 * @return Max number of Players.
	 */
	public final int getMaxPlayers()
	{
		return maxPlayers;
	}

	/**
	 * Gets this Arena's {@link ArenaType}.
	 * 
	 * @return This Arena's type.
	 */
	public final ArenaType getType()
	{
		return type;
	}

	/**
	 * Gets this Arena's type name. Equivalent to
	 * <code>getType().getStylizedName()</code>
	 * 
	 * @return This Arena's type name.
	 */
	public final String getTypeName()
	{
		return type.getStylizedName();
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

	/**
	 * Gets this Arena's status. Timer is included if this Arena is ingame or in
	 * the lobby.
	 * 
	 * @return This Arena's status.
	 */
	public final String getStatus()
	{
		switch (gameMode)
		{
			case DISABLED:
				return ChatColor.RED + "Disabled";
			case IDLE:
				return ChatColor.YELLOW + "Idle";
			case INGAME:
				return ChatColor.GREEN + "In Game - " + gameTimer;
			case LOBBY:
				return ChatColor.GREEN + "Lobby - " + startTimer;
			case STOPPING:
				return ChatColor.YELLOW + "Stopping";
			default:
				// This really shouldn't happen, but just in case
				return ChatColor.YELLOW + FormatUtil.getFriendlyName(gameMode);
		}
	}

	/**
	 * Adds scoreboard entries specific to this arena.
	 * @param board Scoreboard to add entries to
	 * @param player Player to add entries for
	 */
	public void addScoreboardEntries(CustomScoreboard board, ArenaPlayer player) { }

	public final String getMessage(String key)
	{
		return plugin.getMessage(key);
	}

	// ---- Generic Methods

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

	@Override
	public int hashCode()
	{
		int hash = 35;
		hash *= 1 + name.hashCode();
		return hash;
	}

	@Override
	public String toString()
	{
		return name;
	}
}
