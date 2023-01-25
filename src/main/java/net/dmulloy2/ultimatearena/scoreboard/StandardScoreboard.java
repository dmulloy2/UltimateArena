/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.ultimatearena.scoreboard;

import net.dmulloy2.swornapi.types.CustomScoreboard;
import net.dmulloy2.swornapi.types.CustomScoreboard.EntryFormat;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.swornapi.util.FormatUtil;

import org.apache.commons.lang.WordUtils;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

/**
 * @author dmulloy2
 */

public final class StandardScoreboard implements ArenaScoreboard
{
	private static final String OBJECTIVE = "UltimateArena";

	private CustomScoreboard board;
	private final ArenaPlayer player;
	private final UltimateArena plugin;

	public StandardScoreboard(UltimateArena plugin, ArenaPlayer player)
	{
		this.plugin = plugin;
		this.player = player;
		this.setup();
	}

	private void setup()
	{
		Scoreboard board = player.getPlayer().getScoreboard();
		if (board == null || CustomScoreboard.isDefault(board))
			board = plugin.getServer().getScoreboardManager().getNewScoreboard();

		Arena arena = player.getArena();

		this.board = CustomScoreboard.newBuilder(board, OBJECTIVE)
			.displayName(FormatUtil.format(plugin.getMessage("scoreboardHeader"), WordUtils.capitalize(arena.getName())))
			.displaySlot(DisplaySlot.SIDEBAR)
			.entryFormat(EntryFormat.ON_LINE)
			.keyPrefix(FormatUtil.format(plugin.getMessage("keyPrefix")))
			.valuePrefix(FormatUtil.format(plugin.getMessage("valPrefix")))
			.addEntry("Timer", arena.getStartTimer())
			.addEntry("Kills", player.getKills())
			.addEntry("Deaths", player.getDeaths())
			.addEntry("KDR", player.getKDR())
			.addEntry("Streak", player.getKillStreak())
			.addEntry("XP", player.getGameXP())
			.build();

		arena.addScoreboardEntries(this.board, player);
		this.board.applyTo(player.getPlayer());
	}

	@Override
	public void update()
	{
		Arena arena = player.getArena();
		board.addEntry("Timer", arena.isInLobby() ? arena.getStartTimer() : arena.getGameTimer());
		board.addEntry("Kills", player.getKills());
		board.addEntry("Deaths", player.getDeaths());
		board.addEntry("KDR", player.getKDR());
		board.addEntry("Streak", player.getKillStreak());
		board.addEntry("XP", player.getGameXP());
		arena.addScoreboardEntries(board, player);
		board.update();
	}

	@Override
	public void dispose()
	{
		board.dispose();
	}

	@Override
	public boolean isEnabled()
	{
		return true;
	}
}
