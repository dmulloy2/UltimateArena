/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.ultimatearena.signs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaZone;

import org.bukkit.Location;
import org.bukkit.configuration.MemorySection;

/**
 * @author dmulloy2
 */

public class LastGameSign extends ArenaSign
{
	public static final SignType TYPE = SignType.LAST_GAME;

	private List<String> leaderboard;
	private boolean initialUpdate;

	public LastGameSign(UltimateArena plugin, Location loc, ArenaZone az, int id)
	{
		super(plugin, TYPE, loc, az, id);
		this.leaderboard = new ArrayList<>();
	}

	public LastGameSign(UltimateArena plugin, MemorySection section)
	{
		super(plugin, TYPE, section);
		this.leaderboard = section.getStringList("leaderboard");
	}

	@Override
	public void update()
	{
		if (! ensureSign())
			return;

		if (! initialUpdate)
		{
			sign.setLine(1, "");

			populate(leaderboard);
			initialUpdate = true;
		}
	}

	private void populate(List<String> leaderboard)
	{
		StringBuilder line1 = new StringBuilder();
		line1.append("[Last ").append(arenaName);
		if (line1.length() > 14)
		{
			line1.delete(13, line1.length());
			line1.append("\u2026").append("]");
		}
		else
		{
			line1.append("]");
		}

		sign.setLine(0, line1.toString());

		for (int i = 0; i < leaderboard.size(); i++)
		{
			StringBuilder line = new StringBuilder();
			line.append(i + 1).append(". ");
			line.append(leaderboard.get(i));

			if (line.length() > 15)
			{
				line.delete(14, line.length());
				line.append("\u2026");
			}

			sign.setLine(i + 1, line.toString());
		}

		sign.update();
	}

	@Override
	public void onArenaCompletion(Arena arena)
	{
		List<String> finalLeaderboard = arena.getFinalLeaderboard();
		if (finalLeaderboard.size() > 1)
		{
			int size = Math.min(3, finalLeaderboard.size());
			this.leaderboard = finalLeaderboard.subList(0, size);
			populate(leaderboard);
		}
	}

	@Override
	public void clear()
	{
		if (! ensureSign())
			return;

		StringBuilder line1 = new StringBuilder();
		line1.append("[Last ").append(arenaName);
		if (line1.length() > 14)
		{
			line1.delete(13, line1.length());
			line1.append("\u2026").append("]");
		}
		else
		{
			line1.append("]");
		}

		sign.setLine(0, line1.toString());
		sign.setLine(1, "");
		sign.setLine(2, "");
		sign.setLine(3, "");
	}

	@Override
	public void serializeCustomData(Map<String, Object> data)
	{
		data.put("leaderboard", leaderboard);
	}
}