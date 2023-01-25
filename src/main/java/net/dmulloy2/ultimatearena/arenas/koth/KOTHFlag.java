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
package net.dmulloy2.ultimatearena.arenas.koth;

import java.util.List;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.types.ArenaFlag;
import net.dmulloy2.ultimatearena.types.ArenaLocation;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;

import lombok.Getter;
import lombok.Setter;

/**
 * @author dmulloy2
 */

@Getter @Setter
public class KOTHFlag extends ArenaFlag
{
	private static final double RADIUS = 3.0D;

	protected ArenaPlayer leader;
	protected KOTHArena arena;

	public KOTHFlag(KOTHArena arena, ArenaLocation location, UltimateArena plugin)
	{
		super(arena, location, plugin);
		this.arena = arena;
	}

	@Override
	public void checkNear(ArenaPlayer[] arenaPlayers)
	{
		if (! arena.isInGame())
			return;

		ArenaPlayer capturer = findClosest(arenaPlayers, RADIUS);
		if (capturer != null)
			handleCapture(capturer);
	}

	private void handleCapture(ArenaPlayer capturer)
	{
		capturer.putData("kothPoints", capturer.getDataInt("kothPoints", 0) + 1);
		capturer.sendMessage(getMessage("kothCap"), capturer.getDataInt("kothPoints"), arena.getMaxPoints());

		if (capturer.equals(leader))
			return;

		arena.clearCache();
		List<ArenaPlayer> lb = arena.getLeaderboard();

		ArenaPlayer ap = lb.get(0);
		if (ap != null)
		{
			arena.tellPlayers(getMessage("kothLead"), ap.getName());
			leader = ap;
		}
	}
}
