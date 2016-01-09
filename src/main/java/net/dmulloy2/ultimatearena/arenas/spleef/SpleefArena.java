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
package net.dmulloy2.ultimatearena.arenas.spleef;

import lombok.Getter;
import net.dmulloy2.ultimatearena.arenas.ffa.FFAArena;
import net.dmulloy2.ultimatearena.types.ArenaLocation;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.Field3D;
import net.dmulloy2.util.Util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

@Getter
public class SpleefArena extends FFAArena
{
	private Field3D outZone;
	private Field3D spleefGround;
	private Material specialType;

	public SpleefArena(ArenaZone az)
	{
		super(az);

		ArenaLocation pos1 = az.getFlags().get(0);
		ArenaLocation pos2 = az.getFlags().get(1);
		this.spleefGround = new Field3D(pos1, pos2);
		this.spleefGround.setType(specialType); // Refresh the ground

		ArenaLocation pos3 = az.getFlags().get(2);
		ArenaLocation pos4 = az.getFlags().get(3);
		this.outZone = new Field3D(pos3, pos4);
	}

	@Override
	public void announceWinner()
	{
		if (winner != null)
			tellAllPlayers(getMessage("spleefWon"), winner.getName(), name);
	}

	@Override
	public void onMove(ArenaPlayer ap)
	{
		// Kill them if they're under the arena
		Player player = ap.getPlayer();
		if (player.getHealth() > 0.0D)
		{
			if (outZone.isUnder(player.getLocation()))
				player.setHealth(0.0D);
		}
	}

	@Override
	public void onPlayerEnd(ArenaPlayer ap)
	{
		if (isInGame())
		{
			if (active.size() == 1)
			{
				setWinningTeam(null);
				stop();
				rewardTeam(null);
			}
		}
	}

	@Override
	public void onStop()
	{
		// Refresh the ground
		spleefGround.setType(specialType);
	}

	@Override
	public void onReload()
	{
		// 2 players required
		this.minPlayers = Math.max(2, minPlayers);
		this.specialType = ((SpleefZone) az).getSpecialType();
	}

	// Completely arbitrary number
	private static final int MAX_TRIES = 10;

	private final Location getBlockInSpleefArena()
	{
		int x = Util.random(spleefGround.getWidth() - 1);
		int z = Util.random(spleefGround.getLength() - 1);

		int tries = 0;
		Block block = spleefGround.getBlockAt(x, 0, z);
		while (block.getType() != specialType)
		{
			block = spleefGround.getBlockAt(++x, 0, ++z);
			if (++tries >= MAX_TRIES)
				break;
		}

		// Ensure they spawn on the ground
		if (block.getType() != specialType)
			block.setType(specialType);

		return block.getLocation().add(0.0D, 1.0D, 0.0D);
	}

	@Override
	public SpleefConfig getConfig()
	{
		return (SpleefConfig) super.getConfig();
	}

	@Override
	public Location getSpawn(ArenaPlayer ap)
	{
		if (isInLobby())
			return super.getSpawn(ap);

		return getBlockInSpleefArena();
	}
}