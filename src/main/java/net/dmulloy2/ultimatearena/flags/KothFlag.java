package net.dmulloy2.ultimatearena.flags;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import lombok.Getter;
import lombok.Setter;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.KOTHArena;
import net.dmulloy2.ultimatearena.types.ArenaLocation;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.util.Util;

import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

@Getter @Setter
public class KothFlag extends ArenaFlag
{
	protected ArenaPlayer leader;
	protected KOTHArena arena;

	public KothFlag(KOTHArena arena, ArenaLocation location, UltimateArena plugin)
	{
		super(arena, location, plugin);
		this.arena = arena;
	}

	@Override
	public void checkNear(List<ArenaPlayer> arenaPlayers)
	{
		if (! arena.isInGame())
			return;

		int amt = 0;
		ArenaPlayer capturer = null;

		for (ArenaPlayer ap : arenaPlayers)
		{
			Player player = ap.getPlayer();
			if (player.getHealth() > 0.0D && player.getWorld().getUID().equals(location.getWorld())
					&& player.getLocation().distance(location) < 3.0D)
			{
				amt++;
				capturer = ap;
			}
		}

		if (amt == 1)
		{
			if (capturer != null)
			{
				capturer.setPoints(capturer.getPoints() + 1);
				capturer.sendMessage("&3You have capped for &e1 &3point! (&e{0}&3/&e{1}&3)", capturer.getPoints(), arena.getMaxPoints());
				leadChange();
			}
		}
	}

	private void leadChange()
	{
		HashMap<String, Integer> pointsMap = new HashMap<String, Integer>();
		for (ArenaPlayer ap : arena.getActivePlayers())
		{
			pointsMap.put(ap.getName(), ap.getPoints());
		}

		List<Entry<String, Integer>> sortedEntries = new ArrayList<Entry<String, Integer>>(pointsMap.entrySet());
		Collections.sort(sortedEntries, new Comparator<Entry<String, Integer>>()
		{
			@Override
			public int compare(Entry<String, Integer> entry1, Entry<String, Integer> entry2)
			{
				return -entry1.getValue().compareTo(entry2.getValue());
			}
		});

		int pos = 1;
		for (Entry<String, Integer> entry : sortedEntries)
		{
			if (pos > 1)
				return;

			String string = entry.getKey();
			ArenaPlayer apl = plugin.getArenaPlayer(Util.matchPlayer(string));
			if (apl != null)
			{
				if (leader == null || ! apl.getName().equals(leader.getName()))
				{
					arena.tellPlayers("&e{0} &3has taken the lead!", apl.getName());
					leader = apl;
				}

				pos++;
			}
		}
	}
}